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

import static com.QSRAutoSplitter.QSRID.SPEEDRUN_ACTIVE_SIGNIFIER;

@Slf4j
@PluginDescriptor(
	name = "QSR Auto Splitter",
	description = "Sends split signals through LiveSplit server to automatically track splits for quest speedruns"
)
public class QSRAutoSplitterPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(QSRAutoSplitterPlugin.class);

	// the script that returns the game timer




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
		itemLists.put(QSRID.CA, new Integer[]{ItemID.EGG, ItemID.POT_OF_FLOUR, ItemID.BUCKET_OF_MILK});
		itemLists.put(QSRID.DS, new Integer[]{ItemID.SILVERLIGHT_KEY, ItemID.SILVERLIGHT_KEY_2400,
				ItemID.SILVERLIGHT_KEY_2401});
		itemLists.put(QSRID.ETC, new Integer[]{ItemID.RUBBER_TUBE, ItemID.KEY, ItemID.OIL_CAN, ItemID.PRESSURE_GAUGE,
				ItemID.FISH_FOOD, ItemID.POISON, ItemID.SPADE});
		itemLists.put(QSRID.VS, new Integer[]{ItemID.HAMMER, ItemID.STAKE, ItemID.GARLIC});
		itemLists.put(QSRID.DSI, new Integer[]{ItemID.MAP_PART, ItemID.MAP_PART_1536, ItemID.MAP_PART_1537,
				ItemID.WIZARDS_MIND_BOMB, ItemID.LOBSTER_POT, ItemID.UNFIRED_BOWL, ItemID.CLAY, ItemID.SILK,
				ItemID.ANTIDRAGON_SHIELD, ItemID.HAMMER, ItemID.STEEL_NAILS, ItemID.PLANK,
				ItemID.KEY_1543, ItemID.KEY_1544, ItemID.KEY_1545, ItemID.KEY_1546, ItemID.KEY_1547,
				ItemID.KEY_1548});
		itemLists.put(QSRID.PAR, new Integer[]{ItemID.CLAY, ItemID.KEY_PRINT, ItemID.YELLOW_DYE, ItemID.WIG_2421,
				ItemID.PASTE, ItemID.ROPE, ItemID.BEER, ItemID.PINK_SKIRT, ItemID.BRONZE_KEY, ItemID.ASHES,
				ItemID.REDBERRIES, ItemID.BRONZE_BAR, ItemID.BUCKET_OF_WATER, ItemID.BALL_OF_WOOL,
				ItemID.POT_OF_FLOUR});
		itemLists.put(QSRID.BCS, new Integer[]{ItemID.SPADE, ItemID.TINDERBOX, ItemID.IRON_BAR, ItemID.COAL,
				ItemID.CHEST_26955, ItemID.SCARAB_EMBLEM, ItemID.RUSTY_KEY, ItemID.COOKED_MEAT,
				ItemID.LILY_OF_THE_ELID, ItemID.CURE_CRATE});

	}

	private void initializeVarbLists() {
		varbLists = new HashMap<>();
		varbLists.put(QSRID.DS, new Integer[]{QSRID.PRYSIN1});
		varbLists.put(QSRID.PAR, new Integer[]{QSRID.OSMAN1, QSRID.ALI});
		varbLists.put(QSRID.DSI, new Integer[]{QSRID.OZIACH, QSRID.BOUGHT_BOAT, QSRID.REPAIRED_BOAT,
				QSRID.RECRUITED_NED, QSRID.CRANDOR, QSRID.ELVARG_SLAIN});
		varbLists.put(QSRID.BCS, new Integer[]{QSRID.FIRST_CUTSCENE, QSRID.START_FIGHT1, QSRID.END_FIGHT1,
				QSRID.MAISA2, QSRID.FURNACE_LIT, QSRID.SCARAB_ROTATE, QSRID.MAGES_START, QSRID.MAGES_DEAD,
				QSRID.LEVERS, QSRID.URNS, QSRID.GHOST, QSRID.START_FIGHT3, QSRID.END_FIGHT3,
				QSRID.SCARAB_DIALOGUE, QSRID.NARDAH1, QSRID.NARDAH2, QSRID.NARDAH3,
				QSRID.HIGH_PRIEST, QSRID.START_FIGHT4, QSRID.END_FIGHT4});

	}

	@Override
	protected void shutDown()
	{
		sendMessage("pause");
		clientToolbar.removeNavigation(navButton);
		panel.disconnect();  // terminates active socket
	}

	@Subscribe
	private void onClientShutdown(ClientShutdown e) {
		sendMessage("pause");
	}

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
				return "ERROR";
			}
		}
		return "ERROR";
	}
			//sendMessage("split");

	@Subscribe
	public void onGameTick(GameTick event) {

		if (!started && isInSpeedrun()) {
			started = true;
			sendMessage("reset");
			sendMessage("initgametime"); //FIXME find better spot to init
			sendMessage("starttimer");
			currItemList = new Integer[]{};
			currVarbList = new Integer[]{};

			questsComplete = client.getVarbitValue(QSRID.QUESTS_COMPLETE_COUNTER);
			switch (client.getVarbitValue(QSRID.SPEEDRUN_QUEST_SIGNIFIER)) {
				case QSRID.CA:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started CA", null);
					currItemList = itemLists.get(QSRID.CA).clone();
					break;
				case QSRID.DS:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DS", null);
					currItemList = itemLists.get(QSRID.DS).clone();
					currVarbList = varbLists.get(QSRID.DS).clone();
					break;
				case QSRID.ETC:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started ETC", null);
					currItemList = itemLists.get(QSRID.ETC).clone();
					break;
				case QSRID.PAR:
					currItemList = itemLists.get(QSRID.PAR).clone();
					currVarbList = varbLists.get(QSRID.PAR).clone();
					break;
				case QSRID.VS:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started VS", null);
					currItemList = itemLists.get(QSRID.VS).clone();
					break;
				case QSRID.DSI:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DSI", null);
					currItemList = itemLists.get(QSRID.DSI).clone();
					currVarbList = varbLists.get(QSRID.DSI).clone();
					break;
				case QSRID.BCS:
					currVarbList = varbLists.get(QSRID.BCS).clone();
					break;
				default:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: run has not been implemented yet", null);
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
		if (scriptId == QSRID.SPEEDRUNNING_HELPER_UPDATE)
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
			if (client.getVarbitValue(QSRID.DS_PROGRESS) == currVarbList[i]) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currVarbList[i] = -2; // dedup
			}
			else if (client.getVarpValue(QSRID.PAR_PROGRESS) == currVarbList[i]) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currVarbList[i] = -2; // dedup
			}
			else if (client.getVarpValue(QSRID.DSI_PROGRESS) == currVarbList[i]) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currVarbList[i] = -2; // dedup
			}
			else if (client.getVarbitValue(QSRID.BCS_PROGRESS) == currVarbList[i]) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currVarbList[i] = -2; // dedup
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
		return client.getVarbitValue(QSRID.SPEEDRUN_ACTIVE_SIGNIFIER) == QSRID.IN_RUN;
	}

	public void split() {
		sendMessage("setgametime " + (currTicks + 1) * 0.6);
		sendMessage("split");
	}
}
