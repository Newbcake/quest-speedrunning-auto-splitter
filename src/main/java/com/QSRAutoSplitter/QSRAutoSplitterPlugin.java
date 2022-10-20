package com.QSRAutoSplitter;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@PluginDescriptor(
	name = "QSR Auto Splitter",
	description = "Sends split signals through LiveSplit server to automatically track splits for quest speedruns"
)
public class QSRAutoSplitterPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(QSRAutoSplitterPlugin.class);

	// the script that returns the game timer
	public static final int SPEEDRUNNING_HELPER_UPDATE = 5879;

	// The variables to interact with livesplit
	PrintWriter writer;
	BufferedReader reader;

	@Inject
	private Client client;

	@Inject
	private QSRAutoSplitterConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Getter
	private boolean interpolate;

	// side panel
	private NavigationButton navButton;
	private QSRAutoSplitterPanel panel;

	// is the timer running?
	private boolean started = false;
	private boolean paused = false;

	@Provides
	QSRAutoSplitterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QSRAutoSplitterConfig.class);
	}

	/*
	void startUp
	The function is called when Runelite loads the plugin or is enabled by the user. We create the panel and give it
	access to what it needs
	Parameters:
		None
	Returns:
		None
 */
	@Override
	protected void startUp()
	{
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/qsr_auto_splitter_icon.png");
		panel = new QSRAutoSplitterPanel(client, writer, reader, config, this);
		navButton = NavigationButton.builder().tooltip("Quest Speedrunning Auto Splitter")
				.icon(icon).priority(6).panel(panel).build();
		clientToolbar.addNavigation(navButton);

		panel.startPanel();
	}

	/*
    void shutDown
    Called when the user disables the plugin. We disconnect from the LiveSplit Server
    Parameters:
        None
    Returns:
        None
     */
	@Override
	protected void shutDown()
	{
		sendMessage("pause");
		clientToolbar.removeNavigation(navButton);
		panel.disconnect();  // terminates active socket
	}

	/*
	void sendMessage
	Sends a message to the LiveSplit server
	Parameters:
		message (String): The message we are sending
	Returns:
		None
	 */
	private void sendMessage(String message) {

		if (writer != null) {
			writer.write(message + "\r\n");
			writer.flush();
		}
	}

	private String receiveMessage() {

		if (reader != null) {
			try {
				return reader.readLine();
			} catch (IOException e) {
				return "Error: no message found";
			}
		}
		return "Error: reader not found";
	}
			//sendMessage("split");

	/*
    void onGameTick
    Called each game tick. We check to see if we've started a run, and we tell LiveSplit to start the timer
    Parameters:
        None
    Returns:
        None
     */
	@Subscribe
	public void onGameTick(GameTick event) {

		if (!started && isInSpeedrun()) {
			started = true;
			sendMessage("reset");
			sendMessage("initgametime"); //FIXME find better spot to init
			sendMessage("starttimer");
			switch (client.getVarbitValue(13627)) {
				case 1:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started CA", null);
					break;
				case 2:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DS", null);
					break;
				case 7:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started ETC", null);
					break;
				case 8:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started VS", null);
					break;
				case 17:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DSI", null);
					break;
				default:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: run has not been implemented yet", null);
					break;
			}
		}
		else if (started && !isInSpeedrun()) {
			started = false;
			sendMessage("getcurrenttimerphase");
			switch (receiveMessage()) {
				case "Running":
					sendMessage("pause");
					break;
				case "NotRunning:":
				case "Paused":
				case "Ended":
				default:
					break;
			}
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event) {
		ScriptEvent scriptEvent = event.getScriptEvent();
		// Filter out the non-server created scripts. Do note that other plugins may call CS2s, such as the quest helper plugin.
		if (scriptEvent == null || scriptEvent.getSource() != null) {
			return;
		}
		final Object[] arguments = scriptEvent.getArguments();
		final int scriptId = (int) arguments[0];
		if (scriptId == SPEEDRUNNING_HELPER_UPDATE)
		{
			final int ticks = (int) arguments[1];
			sendMessage("setgametime " + ticks*0.6);
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event) {
		logger.debug( "QSR: state changed to " + event.getGameState());
		if (started) {
			if (event.getGameState() == GameState.LOADING ||
					event.getGameState() == GameState.LOGGED_IN ||
					event.getGameState() == GameState.CONNECTION_LOST) {
				if (paused) {
					sendMessage("resume");
					logger.debug( "QSR: unpaused on " + event.getGameState());
					paused = false;
				}
			} else if (!paused) {
				logger.debug( "QSR: paused on " + event.getGameState());
				sendMessage("pause");
				paused = true;
			}
		}
	}

	public boolean isInSpeedrun() {
		return client.getVarbitValue(12395) == 5;
		// VARBIT MEANINGS
		// 12395 = 0 not in a run
		// 		   5 in a run
		// 13627 = 1 Cook's Assistant
		//		   2 Demon Slayer
		// 		   7 Ernest the Chicken
		// 		   8 Vampyre Slayer
		// 		   17 Dragon Slayer I
	}
}
