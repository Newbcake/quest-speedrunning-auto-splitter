package com.QSRAutoSplitter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

@Slf4j
@PluginDescriptor(
	name = "QSR Auto Splitter",
	description = "Sends split signals through LiveSplit server to automatically track splits for quest speedruns"
)
public class QSRAutoSplitterPlugin extends Plugin
{
	// The variables to interact with livesplit
	PrintWriter writer;

	@Inject
	private Client client;

	@Inject
	private QSRAutoSplitterConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	// side panel
	private NavigationButton navButton;
	private QSRAutoSplitterPanel panel;

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
		panel = new QSRAutoSplitterPanel(client, writer, config, this);
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
}
