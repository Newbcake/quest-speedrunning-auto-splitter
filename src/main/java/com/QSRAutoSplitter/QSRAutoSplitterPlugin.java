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
import java.util.*;

import static com.QSRAutoSplitter.QSRID.QUESTS_COMPLETE_COUNTER;
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
	private int currQuest = -1;

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

	private List<Pair<Integer, Integer>> itemList;
	private List<Pair<Integer, Integer>> varbList;
	private List<Pair<Integer, Integer>> varpList;

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
		/*
Sir Prysin's key,0,2399
Captain Rovin's key,0,2400
Wizard Traiborn's key,0,2401
		 */
		itemLists.put(QSRID.ETC, new Integer[]{ItemID.RUBBER_TUBE, ItemID.KEY, ItemID.OIL_CAN, ItemID.PRESSURE_GAUGE,
				ItemID.FISH_FOOD, ItemID.POISON, ItemID.SPADE});
/*
Pressure gauge,0,271
Fish food,0,272
Poison,0,273
Key,0,275
Rubber tube,0,276
Oil can,0,277
Spade,0,952
 */
		itemLists.put(QSRID.VS, new Integer[]{ItemID.HAMMER, ItemID.STAKE, ItemID.GARLIC});
/*
Hammer,0,2347
Stake,0,1549
Garlic,0,1550
 */
		itemLists.put(QSRID.DSI, new Integer[]{ItemID.MAP_PART, ItemID.MAP_PART_1536, ItemID.MAP_PART_1537,
				ItemID.WIZARDS_MIND_BOMB, ItemID.LOBSTER_POT, ItemID.UNFIRED_BOWL, ItemID.CLAY, ItemID.SILK,
				ItemID.ANTIDRAGON_SHIELD, ItemID.HAMMER, ItemID.STEEL_NAILS, ItemID.PLANK,
				ItemID.KEY_1543, ItemID.KEY_1544, ItemID.KEY_1545, ItemID.KEY_1546, ItemID.KEY_1547,
				ItemID.KEY_1548});
/*
Melzar's map part,0,1535
Thalzar's map part,0,1536
Lozar's map part,0,1537
Unfired bowl,0,1791
Wizard's mind bomb,0,1907
Lobster pot,0,301
Silk,0,905
Anti-dragon shield,0,1540
Hammer,0,2347
Steel nails,0,1539,90
Plank,0,960,3
Red key,0,1543
Orange key,0,1544
Yellow key,0,1545
Blue key,0,1546
Magenta key,0,1547
Green key,0,1548
 */
		itemLists.put(QSRID.PAR, new Integer[]{ItemID.CLAY, ItemID.KEY_PRINT, ItemID.YELLOW_DYE, ItemID.WIG_2421,
				ItemID.PASTE, ItemID.ROPE, ItemID.BEER, ItemID.PINK_SKIRT, ItemID.BRONZE_KEY, ItemID.ASHES,
				ItemID.REDBERRIES, ItemID.BRONZE_BAR, ItemID.BUCKET_OF_WATER, ItemID.BALL_OF_WOOL,
				ItemID.POT_OF_FLOUR});
/*
Clay,0,434
Key print,0,2423
Yellow dye,0,1765
Wig,0,2421
Paste,0,2424
Rope,0,954
Beer,0,1917,3
Pink skirt,0,1013
Bronze key,0,2418
Ashes,0,592
Redberries,0,1951
Bronze bar,0,2349
Bucket of water,0,1929
Ball of wool,0,1759,3
Pot of flour,0,1933
 */
		itemLists.put(QSRID.BCS, new Integer[]{ItemID.SPADE, ItemID.TINDERBOX, ItemID.IRON_BAR, ItemID.COAL,
				ItemID.CHEST_26955, ItemID.SCARAB_EMBLEM, ItemID.RUSTY_KEY, ItemID.COOKED_MEAT,
				ItemID.LILY_OF_THE_ELID, ItemID.CURE_CRATE});
	}
/*
Spade,0,952
Tinderbox,0,590
Iron bar,0,2351
Coal,0,453
Chest,0,26955
Scarab emblem,0,26953
Rusty key,0,26960
Cooked meat,0,2142
Lily of the Elid,0,26961
Cure crate,0,26962
 */
/*
Egg,0,1944
Pot of flour,0,1933
Bucket of milk,0,1927
First cutscene,1,13841,12
*/

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

	private void setup(String configStr) {
		itemList = new ArrayList<>();
		varbList = new ArrayList<>();
		varpList = new ArrayList<>();

		String[] configList = configStr.split("\n");
		for (String item : configList) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: " + item, null);
		}

		for (String line : configList) {
			String[] args = line.split(",");
			Pair<Integer, Integer> pair;
			try {
				int type = Integer.parseInt(args[1]);
				if (type == 0) {
					if (args.length < 4) { // default 1 item
						pair = new Pair<>(Integer.parseInt(args[2]), 1);
					} else {
						pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					}
					itemList.add(pair);
				} else if (type == 1) {
					pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					varbList.add(pair);
				} else if (type == 2) {
					pair = new Pair<>(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
					varpList.add(pair);
				} else {
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: could not parse line: " + line, null);
				}
			} catch (Exception e) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: could not parse line: " + line, null);
			}
		}
	}
	@Subscribe
	public void onGameTick(GameTick event) {

		if (!started && isInSpeedrun()) {
			started = true;
			sendMessage("reset");
			sendMessage("initgametime"); //FIXME find better spot to init
			sendMessage("starttimer");
			//currItemList = new Integer[]{};
			//currVarbList = new Integer[]{};

			questsComplete = client.getVarbitValue(QSRID.QUESTS_COMPLETE_COUNTER);
			currQuest = client.getVarbitValue(QSRID.SPEEDRUN_QUEST_SIGNIFIER);
			String configStr = "";

			switch (currQuest) {
				case QSRID.CA:
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started CA", null);
					//currItemList = itemLists.get(QSRID.CA).clone();
					configStr = config.caList();
					break;
				case QSRID.DS:
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DS", null);
					//currItemList = itemLists.get(QSRID.DS).clone();
					//currVarbList = varbLists.get(QSRID.DS).clone();
					configStr = config.dsList();
					break;
				case QSRID.ETC:
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started ETC", null);
					//currItemList = itemLists.get(QSRID.ETC).clone();
					configStr = config.etcList();
					break;
				case QSRID.PAR:
					//currItemList = itemLists.get(QSRID.PAR).clone();
					//currVarbList = varbLists.get(QSRID.PAR).clone();
					configStr = config.parList();
					break;
				case QSRID.VS:
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started VS", null);
					//currItemList = itemLists.get(QSRID.VS).clone();
					configStr = config.vsList();
					break;
				case QSRID.DSI:
					//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: started DSI", null);
					//currItemList = itemLists.get(QSRID.DSI).clone();
					//currVarbList = varbLists.get(QSRID.DSI).clone();
					configStr = config.dsiList();
					break;
				case QSRID.BCS:
					//currVarbList = varbLists.get(QSRID.BCS).clone();
					configStr = config.bcsList();
					break;
				default:
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: run has not been implemented yet", null);
					configStr = "";
					break;
			}
			setup(configStr);
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
		if (started && client.getVarbitValue(QUESTS_COMPLETE_COUNTER) > questsComplete) {
			completeRun();
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: quest complete!", null);
		}

		for (Pair<Integer, Integer> pair : varbList) {
			if (client.getVarbitValue(pair.first) == pair.second) {
				split();
				varbList.remove(pair);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: " + pair.first + "; " + pair.second, null);

			}
		}
		for (Pair<Integer, Integer> pair : varpList) {
			if (client.getVarpValue(pair.first) == pair.second) {
				split();
				varpList.remove(pair);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: " + pair.first + "; " + pair.second, null);

			}
		}
		/*
		for (int i = 0; i < currVarbList.length; i++) {
			switch (currQuest) {
				case QSRID.DS:
					if (client.getVarbitValue(QSRID.DS_PROGRESS) == currVarbList[i]) {
						split();
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
						currVarbList[i] = -2; // dedup
					}
					break;
				case QSRID.PAR:
					if (client.getVarpValue(QSRID.PAR_PROGRESS) == currVarbList[i]) {
						split();
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
						currVarbList[i] = -2; // dedup
					}
					break;
				case QSRID.DSI:
					if (client.getVarpValue(QSRID.DSI_PROGRESS) == currVarbList[i]) {
						split();
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split" + client.getVarpValue(QSRID.DSI_PROGRESS), null);
						currVarbList[i] = -2; // dedup
					}
					break;
				case QSRID.BCS:
					if (client.getVarbitValue(QSRID.BCS_PROGRESS) == currVarbList[i]) {
						split();
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
						currVarbList[i] = -2; // dedup
					}
					break;
			}
		}*/
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		final ItemContainer itemContainer = event.getItemContainer();
		if (itemContainer != client.getItemContainer(InventoryID.INVENTORY))
		{
			return;
		}

		for (Pair<Integer, Integer> pair : itemList) {
			if (itemContainer.count(pair.first) >= pair.second) {
				split();
				itemList.remove(pair);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: " + pair.first + "; " + pair.second, null);

			}
		}
		/*
		for (int i = 0; i < currItemList.length; i++) {
			if (itemContainer.contains(currItemList[i])) {
				split();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "QSR: split", null);
				currItemList[i] = -2; // dedup (-1 is empty slot) FIXME
			}
		}

		*/

	}

	public boolean isInSpeedrun() {
		return client.getVarbitValue(QSRID.SPEEDRUN_ACTIVE_SIGNIFIER) == QSRID.IN_RUN;
	}

	public void split() {
		sendMessage("setgametime " + (currTicks + 1) * 0.6);
		sendMessage("split");
	}
}
