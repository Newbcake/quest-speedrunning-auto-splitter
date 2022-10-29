package com.QSRAutoSplitter;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
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
import java.util.HashMap;
import java.util.List;

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
	public static final int SPEEDRUN_QUEST_SIGNIFIER = 13627;
	public static final int SPEEDRUN_ACTIVE_SIGNIFIER = 12395;

	public static final int QUESTS_COMPLETE_COUNTER = 6347;

	// The number of quests completed. If this increases during a run, we've completed the quest.
	private int questsComplete;
	private int currTicks;

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

	// events to split on TODO add more events and customization
	HashMap<Integer, Integer[]> itemLists;
	HashMap<Integer, Integer[]> varbLists;
	Integer[] currItemList;
	Integer[] currVarbList;

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
		initializeItemLists();
		initializeVarbLists();

		panel.startPanel();
	}

	private void initializeItemLists() {
		itemLists = new HashMap<>();
		itemLists.put(QuestID.CA, new Integer[]{ItemID.EGG, ItemID.POT_OF_FLOUR, ItemID.BUCKET_OF_MILK});
		itemLists.put(QuestID.DS, new Integer[]{ItemID.SILVERLIGHT_KEY, ItemID.SILVERLIGHT_KEY_2400,
				ItemID.SILVERLIGHT_KEY_2401});
		itemLists.put(QuestID.ETC, new Integer[]{ItemID.RUBBER_TUBE, ItemID.KEY, ItemID.OIL_CAN, ItemID.PRESSURE_GAUGE,
				ItemID.FISH_FOOD, ItemID.POISON, ItemID.SPADE});
		itemLists.put(QuestID.VS, new Integer[]{ItemID.HAMMER, ItemID.STAKE, ItemID.GARLIC});
		itemLists.put(QuestID.DSI, new Integer[]{ItemID.MAP_PART, ItemID.MAP_PART_1536, ItemID.MAP_PART_1537,
				ItemID.WIZARDS_MIND_BOMB, ItemID.LOBSTER_POT, ItemID.UNFIRED_POT, ItemID.CLAY, ItemID.SILK,
				ItemID.ANTIDRAGON_SHIELD, ItemID.HAMMER, ItemID.STEEL_NAILS, ItemID.PLANK,
				ItemID.MAZE_KEY, ItemID.KEY_1543, ItemID.KEY_1544, ItemID.KEY_1545, ItemID.KEY_1546, ItemID.KEY_1547,
				ItemID.KEY_1548});
		itemLists.put(QuestID.PAR, new Integer[]{ItemID.CLAY, ItemID.KEY_PRINT, ItemID.YELLOW_DYE, ItemID.WIG,
				ItemID.PASTE, ItemID.ROPE, ItemID.BEER, ItemID.PINK_SKIRT, ItemID.BRONZE_KEY});

	}

	private void initializeVarbLists() {
		varbLists = new HashMap<>();
		varbLists.put(QuestID.BCS, new Integer[]{12,18,20,32,38,40,42,46,48,54,60,62,68,72,80,88,92,98,100});

	}

	/*
    void shutDown
    Called when the user disables the plugin. We pause the timer and disconnect from the LiveSplit Server
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
	void onClientShutdown
	When the client is forcefully closed, the timer should pause
	Parameters:
		None
	Returns:
		None
	 */
	@Subscribe
	private void onClientShutdown(ClientShutdown e) {
		sendMessage("pause");
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

	/*
	void receiveMessage
	Receives a message from the LiveSplit server
	Parameters:
		None
	Returns:
		message (String): The message we are receiving
	 */
	private String receiveMessage() {

		if (reader != null) {
			try {
				return reader.readLine();
			} catch (IOException e) {
				return "ERROR";
			}
		}
		return "ERROR";
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
			currItemList = new Integer[]{};
			currVarbList = new Integer[]{};

			questsComplete = client.getVarbitValue(QUESTS_COMPLETE_COUNTER);
			switch (client.getVarbitValue(SPEEDRUN_QUEST_SIGNIFIER)) {
				case QuestID.CA:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started CA", null);
					currItemList = itemLists.get(QuestID.CA).clone();
					break;
				case QuestID.DS:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DS", null);
					currItemList = itemLists.get(QuestID.DS).clone();
					break;
				case QuestID.ETC:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started ETC", null);
					currItemList = itemLists.get(QuestID.ETC).clone();
					break;
				case QuestID.VS:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started VS", null);
					currItemList = itemLists.get(QuestID.VS).clone();
					break;
				case QuestID.DSI:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DSI", null);
					currItemList = itemLists.get(QuestID.DSI).clone();
					break;
				case QuestID.BCS:
					currItemList = new Integer[]{};
					currVarbList = varbLists.get(QuestID.BCS).clone();
				default:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: run has not been implemented yet", null);
					currItemList = new Integer[]{};
					break;
			}
		} else if (started && client.getWidget(WidgetInfo.QUEST_COMPLETED_NAME_TEXT) != null) {
			completeRun();
			started = false;
		} else if (started && !isInSpeedrun()) {
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
		if ( client.getWidget(WidgetInfo.QUEST_COMPLETED_NAME_TEXT) != null) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: quest complete", null);

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
			currTicks = ticks;
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

	/*
   void onChatMessage
   Called every time the client receives a message in the chat box. Checks for end of run because there's no obvious script.
   Parameters:
       event (ChatMessage): The object that contains the chat message text
   Returns:
       None
    */
	@Subscribe
	public void onChatMessage(ChatMessage event) {
	}
	public void completeRun() {
		questsComplete = 0;
		started = false;
		sendMessage("getcurrenttimerphase");
		String msg = receiveMessage();
		loop:
		while (!msg.equals("ERROR")) {
			switch (msg) {
				case "Running":
					sendMessage("getsplitindex");
					String i = receiveMessage();
					sendMessage("skipsplit");
					sendMessage("getsplitindex");
					String j = receiveMessage();
					if (i.equals(j)) {
						split();
						break loop;
					}
					break;
				case "Paused":
					sendMessage("resume");
					break;
				case "Ended":
					sendMessage("unsplit");
					break;
				case "NotRunning":
					break loop;
			}
			sendMessage("getcurrenttimerphase");
			msg = receiveMessage();
		}

	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		if (started && client.getVarbitValue(6347) > questsComplete) {
			completeRun();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: quest complete!", null);

		}

		for (int i = 0; i < currVarbList.length; i++) {
			if (client.getVarbitValue(QuestID.BCS_PROGRESS) == currVarbList[i]) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currVarbList[i] = -2; // dedup (-1 is empty slot) FIXME
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		final ItemContainer itemContainer = event.getItemContainer();
		if (itemContainer != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		for (int i = 0; i < currItemList.length; i++) {
			if (itemContainer.contains(currItemList[i])) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currItemList[i] = -2; // dedup (-1 is empty slot) FIXME
			}
		}

	}

	public boolean isInSpeedrun() {
		return client.getVarbitValue(SPEEDRUN_ACTIVE_SIGNIFIER) == 5;
		// 12395 = 0 not in a run
		// 		   5 in a run
	}

	public void split() {
		sendMessage("setgametime " + (currTicks + 1) * 0.6);
		sendMessage("split");
	}
}
