package com.QSRAutoSplitter;

import net.runelite.api.ItemID;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("qsrautosplitter")
public interface QSRAutoSplitterConfig extends Config
{
	String desc = "The split config for this quest.";
	String secDesc = "All of the split configs for quests of this length.";
	@ConfigItem(
			position = 0,
			keyName = "port",
			name = "Port",
			description = "Port for the LiveSplit server. (Restart required)"
	)
	default int port() {
		return 16834;
	}

	@ConfigSection(
			name = "Customization Instructions",
			description = "Instructions on how to edit a config",
			position = 1,
			closedByDefault = true
	)
	String instructionsSection = "instructionsSect";

	@ConfigItem(
			keyName = "instructions",
			name = "Instructions",
			description = "Instructions on how to edit a config",
			position = 1,
			section = "instructionsSect"
	)
	default String instructions()
	{
		return "Edit any section below to change what actions will cause a split.\n" +
				"The first element is just for naming, and is completely ignored.\n" +
				"\n" +
				"Format:\n" +
				"<name>,0,<itemID>(,<quantity>)\n" +
				"Splits when an item matching the ID enters your inventory for the first time.\n" +
				"If a quantity is given, it will split when you first have that many of the item in your inventory.\n" +
				"You can find item IDs at https://www.osrsbox.com/tools/item-search/\n" +
				"\n" +
				"<name>,1,<varb>,<value>\n" +
				"Splits when the varbit changes to that value for the first time.\n" +
				"\n" +
				"<name>,2,<varp>,<value>\n" +
				"Splits when the varplayer changes to that value for the first time.\n" +
				"You can find varbs and varps using RuneLite's developer mode's Var Inspector https://github.com/runelite/runelite/wiki/Using-the-client-developer-tools";
	}
/*


 */

	@ConfigSection(
			name = "Very Short",
			description = secDesc,
			position = 2,
			closedByDefault = true
	)
	String veryShortSection = "veryshort";

	@ConfigItem(
			keyName = "bkfList",
			name = "Black Knights' Fortress",
			description = desc,
			position = 0,
			section = "veryshort"
	)
	default String bkfList()
	{
		return "Iron chainbody,0,1101\n" +
				"Bronze med helm,0,1139\n" +
				"Cabbage,0,1965\n" +
				"Grill,2,130,2\n" +
				"Hole,2,130,3";
	}

	@ConfigItem(
			keyName = "calist",
			name = "Cook's Assistant",
			description = desc,
			position = 1,
			section = "veryshort"
	)
	default String caList()
	{
		return "Bucket of milk,0,1927\n" +
				"Pot of flour,0,1933\n" +
				"Egg,0,1944";
	}

	@ConfigItem(
			keyName = "etclist",
			name = "Ernest the Chicken",
			description = desc,
			position = 2,
			section = "veryshort"
	)
	default String etcList()
	{
		return "Pressure gauge,0,271\n" +
				"Fish food,0,272\n" +
				"Poison,0,273\n" +
				"Key,0,275\n" +
				"Rubber tube,0,276\n" +
				"Oil can,0,277\n" +
				"Spade,0,952";
	}

	@ConfigItem(
			keyName = "vslist",
			name = "Vampyre Slayer",
			description = desc,
			position = 3,
			section = "veryshort"
	)
	default String vsList()
	{
		return "Hammer,0,2347\n" +
				"Stake,0,1549\n" +
				"Garlic,0,1550";
	}

	@ConfigItem(
			keyName = "xmslist",
			name = "X Marks the Spot",
			description = desc,
			position = 4,
			section = "veryshort"
	)
	default String xmsList()
	{
		return "Spade,0,952\n" +
				"Veos,1,8063,2\n" +
				"Window,1,8063,3\n" +
				"Castle,1,8063,4\n" +
				"Leela,1,8063,5\n" +
				"Pig pen,1,8063,6";
	}

	@ConfigSection(
			name = "Short",
			description = secDesc,
			position = 3,
			closedByDefault = true
	)
	String shortSection = "short";

	@ConfigItem(
			keyName = "bimlist",
			name = "Below Ice Mountain",
			description = desc,
			position = 0,
			section = "short"
	)
	default String bimList()
	{
		return "Cooked meat,0,2142\n" +
				"Knife,0,946\n" +
				"Bread,0,2309\n" +
				"Start,1,12063,10\n" +
				"Janken,1,12066,40\n" +
				"Checkal,1,12065,5\n" +
				"Atlas,1,12065,10\n" +
				"Montage,1,12065,15\n" +
				"Flex,1,12065,40\n" +
				"Charlie,1,12064,2\n" +
				"Cook,1,12064,10\n" +
				"Marley,1,12064,40\n" +
				"Willow,1,12063,25\n" +
				"Start fight,1,12063,35\n" +
				"Ancient Guardian,1,12063,40";
	}

/*

*/
	@ConfigItem(
			keyName = "dslist",
			name = "Demon Slayer",
			description = desc,
			position = 1,
			section = "short"
	)
	default String dsList()
	{
		return "Sir Prysin's key,0,2399\n" +
				"Captain Rovin's key,0,2400\n" +
				"Wizard Traiborn's key,0,2401\n" +
				"Bones,0,526,25\n" +
				"Sir Prysin,1,2561,2";
	}

	@ConfigItem(
			keyName = "parlist",
			name = "Prince Ali Rescue",
			description = desc,
			position = 2,
			section = "short"
	)
	default String parList()
	{
		return "Clay,0,434\n" +
				"Key print,0,2423\n" +
				"Onion,0,1957,2\n" +
				"Yellow dye,0,1765\n" +
				"Wig,0,2421\n" +
				"Paste,0,2424\n" +
				"Rope,0,954\n" +
				"Beer,0,1917,3\n" +
				"Pink skirt,0,1013\n" +
				"Bronze key,0,2418\n" +
				"Ashes,0,592\n" +
				"Redberries,0,1951\n" +
				"Bronze bar,0,2349\n" +
				"Bucket of water,0,1929\n" +
				"Ball of wool,0,1759,3\n" +
				"Pot of flour,0,1933\n" +
				"Osman,2,273,20\n" +
				"Ali,2,273,100";
	}

	@ConfigItem(
			keyName = "toelist",
			name = "Temple of the Eye",
			description = desc,
			position = 3,
			section = "short"
	)
	default String toeList()
	{
		return "Bucket of water,0,1929\n" +
				"Chisel,0,1755\n" +
				"Mind rune,0,558,5\n" +
				"Water rune,0,555,5\n" +
				"Water rune,0,555,10\n" +
				"Persten 1,1,13738,10\n" +
				"Dark mage 1,1,13738,15\n" +
				"Tea,1,13741,1\n" +
				"Dark mage 2,1,13738,25\n" +
				"Start puzzle,1,13738,35\n" +
				"Puzzle,1,13738,40\n" +
				"Dark Mage 2,1,13738,45\n" +
				"Persten 2,1,13738,60\n" +
				"Sedridor 1,1,13738,70\n" +
				"Traiborn 1,1,13738,75\n" +
				"Tamara 1,1,13742,1\n" +
				"Felix 1,1,13743,1\n" +
				"Cordelia 1,1,13744,1\n" +
				"All viewed,1,13738,80\n" +
				"Puzzle complete,1,13738,85\n" +
				"Persten,1,13738,95\n" +
				"Felix 2,1,13755,1\n" +
				"Tamara 2,1,13754,1\n" +
				"Cordelia 2,1,13756,1\n" +
				"Great Guardian 1,1,13759,15\n" +
				"Felix,1,13759,20\n" +
				"Tamara,1,13759,25\n" +
				"Cell,1,13759,30\n" +
				"Tamara,1,13759,40\n" +
				"Felix,1,13759,45\n" +
				"Power guardians,1,13759,50\n" +
				"Tamara,1,13759,55\n" +
				"Tamara with 5 ess,1,13759,60\n" +
				"Tamara after mind,1,13759,65\n" +
				"Charge,1,13759,70\n" +
				"Great Guardian,1,13759,75\n" +
				"Tamara,1,13759,80\n" +
				"Great guardian,1,13759,85\n" +
				"Tamara,1,13759,90\n" +
				"Water guardian,1,13759,95\n" +
				"Tamara,1,13759,100\n" +
				"Charge water cell,1,13759,105\n" +
				"Tamara,1,13759,110\n" +
				"Great Guardian,1,13738,120\n" +
				"Great guardian cutscene,1,13738,125";
	}

	@ConfigSection(
			name = "Medium",
			description = secDesc,
			position = 4,
			closedByDefault = true
	)
	String mediumSection = "medium";

	@ConfigItem(
			keyName = "bcslist",
			name = "Beneath Cursed Sands",
			description = desc,
			position = 0,
			section = "medium"
	)
	default String bcsList()
	{
		return "Spade,0,952\n" +
				"Tinderbox,0,590\n" +
				"Iron bar,0,2351\n" +
				"Coal,0,453\n" +
				"Chest,0,26955\n" +
				"Scarab emblem,0,26953\n" +
				"Rusty key,0,26960\n" +
				"Cooked meat,0,2142\n" +
				"Lily of the Elid,0,26961\n" +
				"Cure crate,0,26962\n" +
    			"Maisa 1,1,13841,12\n" +
				"Start fight 1,1,13841,18\n" +
				"End fight 1,1,13841,20\n" +
				"Maisa 2,1,13841,26\n" +
				"Furnace lit,1,13841,32\n" +
				"Rotation puzzle,1,13841,38\n" +
				"Start fight 2,1,13841,40\n" +
				"End fight 2,1,13841,42\n" +
				"Lever puzzle,1,13841,46\n" +
				"Urn puzzle,1,13841,48\n" +
				"Mehhar,1,13841,54\n" +
				"Start fight 3,1,13841,60\n" +
				"End fight 3,1,13841,62\n" +
				"High Priest of Scabaras,1,13841,68\n" +
				"Maisa 3,1,13841,72\n" +
				"Zahur,1,13841,80\n" +
				"Heat puzzle,1,13841,84\n" +
				"High priest 1,1,13841,92\n" +
				"Start fight 4,1,13841,98\n" +
				"End fight 4,1,13841,100";
	}

	@ConfigItem(
			keyName = "dsilist",
			name = "Dragon Slayer I",
			description = desc,
			position = 1,
			section = "medium"
	)
	default String dsiList()
	{
		return "Melzar's map part,0,1535\n" +
				"Thalzar's map part,0,1536\n" +
				"Lozar's map part,0,1537\n" +
				"Clay,0,434\n" +
				"Bucket of water,0,1929\n" +
				"Unfired bowl,0,1791\n" +
				"Wizard's mind bomb,0,1907\n" +
				"Lobster pot,0,301\n" +
				"Silk,0,950\n" +
				"Anti-dragon shield,0,1540\n" +
				"Hammer,0,2347\n" +
				"Steel nails,0,1539,90\n" +
				"Plank,0,960,3\n" +
				"Red key,0,1543\n" +
				"Orange key,0,1544\n" +
				"Yellow key,0,1545\n" +
				"Blue key,0,1546\n" +
				"Magenta key,0,1547\n" +
				"Green key,0,1548\n" +
    			"Oziach,2,176,2\n" +
				"Bought boat,2,176,3\n" +
				"Repaired boat,2,176,6\n" +
				"Recruited Ned,2,176,7\n" +
				"Crandor,2,176,8\n" +
				"Elvarg slain,2,176,9\n" +
				"Oracle,1,1832,1";
	}

	@ConfigItem(
			keyName = "tohlist",
			name = "A Taste of Hope",
			description = desc,
			position = 2,
			section = "medium"
	)
	default String tohList()
	{
		return "Safalaan 1,1,6396,20\n" +
				"Harpert,1,6396,35\n" +
				"Window,1,6396,45\n" +
				"Safalaan 2,1,6396,55\n" +
				"Flaygian,1,6396,65\n" +
				"Safalaan 3,1,6396,75\n" +
				"Give note,1,6396,90\n" +
				"Start Abomination,1,6396,100\n" +
				"End Abomination,1,6396,110\n" +
				"Safalaan pre-flail,1,6396,120\n" +
				"Safalaan post-flail,1,6396,135\n" +
				"Start Ranis,1,6396,140\n" +
				"End Ranis,1,6396,150";
	}

	@ConfigSection(
			name = "Long",
			description = secDesc,
			position = 5,
			closedByDefault = true
	)
	String longSection = "long";

	@ConfigItem(
			keyName = "akdlist",
			name = "A Kingdom Divided",
			description = desc,
			position = 0,
			section = "long"
	)
	default String akdList()
	{
		return "Martin 1,1,12296,4\n" +
				"Fullore 1,1,12296,6\n" +
				"Fuggy,1,12296,14\n" +
				"Herbert,1,12296,16\n" +
				"Start Judge of Yama,1,12296,18\n" +
				"Judge of Yama,1,12296,20\n" +
				"Yama cutscene,1,12296,24\n" +
				"Fullore 2,1,12296,26\n" +
				"Dark essence block,0,13446\n" +
				"Martin 2,1,12296,30\n" +
				"Steal key,1,12296,32\n" +
				"Display case,1,12296,34\n" +
				"Martin 3,1,12296,42\n" +
				"Martin ruins,1,12296,44\n" +
				"Rose panel,1,12296,50\n" +
				"Start assassin 1,1,12296,56\n" +
				"Assassin 1,1,12296,58\n" +
				"Winter panel,1,12296,64\n" +
				"Martin bar,1,12296,70\n" +
				"Bar panel,1,12296,72\n" +
				"Statue puzzle,1,12296,74\n" +
				"Arrest,1,12296,76\n" +
				"Move boxes,1,12296,80\n" +
				"Rose cutscene,1,12296,84\n" +
				"Volcanic sulphur,0,13571\n" +
				"Kaht B'alam 1,1,12296,88\n" +
				"Lizard egg,1,12296,90\n" +
				"Damp key,0,25810\n" +
				"Start Xamphur,1,12296,100\n" +
				"Xamphur,1,12296,102\n" +
				"Search table,1,12296,106\n" +
				"Funeral,1,12296,110\n" +
				"Invite Piscarilius,1,12313,1\n" +
				"Invite Arceuus,1,12314,1\n" +
				"Molten glass,0,1775\n" +
				"Invite Lovakengj,1,12315,1\n" +
				"Invite Hosidius,1,12316,1\n" +
				"Invite Shayzien,1,12317,1\n" +
				"Meeting,1,12296,122\n" +
				"Fullore,1,12296,124\n" +
				"Shayzien,1,12322,2\n" +
				"Lovakengj,1,12320,2\n" +
				"Piscarilius,1,12318,2\n" +
				"Arceuus,1,12319,2\n" +
				"Fullore,1,12320,4\n" +
				"Hosidius,1,12321,2\n" +
				"Taskaal,1,12319,6\n" +
				"Enchant potion,1,12319,8\n" +
				"Doors of Dinh,1,12319,10\n" +
				"Start barbarian,1,12321,4\n" +
				"Barbarian,1,12321,6\n" +
				"Phileas,1,12321,8\n" +
				"Martin,1,12320,6\n" +
				"Start assassin 2,1,12320,10\n" +
				"Assassin 2,1,12320,12\n" +
				"Jorra,1,12320,14\n" +
				"Mori,1,12318,8\n" +
				"Chasm,1,12318,10\n" +
				"Chest,1,12322,6\n" +
				"Arceuus,1,12319,12\n" +
				"Hosidius,1,12321,10\n" +
				"Shayzien,1,12322,8\n" +
				"Lovakengj,1,12320,16\n" +
				"Piscarilius,1,12318,12\n" +
				"Meeting,1,12296,138\n" +
				"Fullore,1,12296,140\n" +
				"Hosidius,1,12296,142\n" +
				"Fullore,1,12296,144\n" +
				"Coronation,1,12296,148";
	}

	@ConfigItem(
			keyName = "sotflist",
			name = "Sins of the Father",
			description = desc,
			position = 1,
			section = "long"
	)
	default String sotfList()
	{
		return "Logs,0,1511,3\n" +
				"Short vine,0,7778,3\n" +
				"Vyrewatch top,0,9634\n" +
				"Blisterwood logs,0,24691,8\n" +
				"Veliaf,1,7255,4\n" +
				"Hameln,1,7255,6\n" +
				"Carl,1,7255,8\n" +
				"Start stealth,1,7255,10\n" +
				"End stealth,1,7255,14\n" +
				"Start Kroy,1,7255,16\n" +
				"Kill Kroy,1,7255,18\n" +
				"Veliaf,1,7255,22\n" +
				"Paterdomus,1,7255,28\n" +
				"Fenkenstrain,1,7255,34\n" +
				"End trek,1,7255,40\n" +
				"Talk at boat,1,7255,42\n" +
				"Start puzzle,1,7255,50\n" +
				"End puzzle,1,7255,52\n" +
				"Vertida,1,10348,1\n" +
				"Kael,1,10347,1\n" +
				"Radigad,1,10351,1\n" +
				"Polmafi,1,10350,2\n" +
				"Ivan,1,10349,1\n" +
				"Veliaf,1,7255,56\n" +
				"Vanescula,1,7255,58\n" +
				"Start bloodveld,1,7255,62\n" +
				"End bloodveld,1,7255,64\n" +
				"Safalaan lab,1,7255,66\n" +
				"Turn in book,1,7255,70\n" +
				"Vanescula,1,7255,72\n" +
				"Polmafi,1,7255,78\n" +
				"Vanescula,1,7255,80\n" +
				"Start Damien,1,7255,84\n" +
				"End Damien,1,7255,88\n" +
				"Vanescula,1,7255,90\n" +
				"Desmodus,1,7255,94\n" +
				"Mordan,1,7255,96\n" +
				"Maria,1,7255,100\n" +
				"Start valves,1,7255,108\n" +
				"Start tree,1,7255,112\n" +
				"Vanescula,1,7255,114\n" +
				"Vertida,1,7255,118\n" +
				"Vanescula,1,7255,122\n" +
				"Fight cutscene,1,7255,128\n" +
				"Start Vanstrom,1,7255,130\n" +
				"Phase 1,2,1683,9571\n" +
				"End Vanstrom,1,7255,132";
	}

	@ConfigSection(
			name = "Very Long",
			description = secDesc,
			position = 6,
			closedByDefault = true
	)
	String verylongSection = "verylong";

	@ConfigItem(
			keyName = "dsiilist",
			name = "Dragon Slayer II",
			description = desc,
			position = 0,
			section = "verylong"
	)
	default String dsiiList()
	{
		return "Goutweed,0,3261\n" +
				"Oak planks,0,8778,8\n" +
				"Saw,0,8794\n" +
				"Hammer,0,2347\n" +
				"Swamp paste,0,1941,10\n" +
				"Molten glass,0,1775,2\n" +
				"Glassblowing pipe,0,1785\n" +
				"Spade,0,952\n" +
				"Astral rune,0,9075\n" +
				"Tinderbox,0,590\n" +
				"Pestle and mortar,0,233\n" +
				"Dallas Jones (Karamja),1,6104,10\n" +
				"Dallas Jones (Elvarg),1,6104,15\n" +
				"Enter lab,1,6104,20\n" +
				"Start Spawn,1,6104,22\n" +
				"Spawn,1,6104,25\n" +
				"Mural,1,6104,30\n" +
				"Dallas Jones (lab),1,6104,35\n" +
				"Dallas Jones (hoth),1,6104,40\n" +
				"Puzzle start,1,6104,45\n" +
				"Puzzle,1,6104,50\n" +
				"Dallas Jones (hoth),1,6104,55\n" +
				"Jardic,1,6104,60\n" +
				"Build rowboat,1,6104,65\n" +
				"Lithkren,1,6104,71\n" +
				"Dallas Jones (lithkren),1,6104,80\n" +
				"Diary of Aviras,1,6104,81\n" +
				"Dallas Jones (lithkren),1,6104,90\n" +
				"Bob the cat,1,6104,95\n" +
				"Sphinx,1,6104,100\n" +
				"Oneiromancer,1,6104,105\n" +
				"Enter dream,1,6104,110\n" +
				"Bob,1,6104,111\n" +
				"Start Robert the Strong,1,6100,280\n" +
				"Robert the Strong,1,6104,115\n" +
				"Cutscene,1,6104,120\n" +
				"Bob,1,6104,125\n" +
				"Enter Karamja temple,1,6106,15\n" +
				"Key piece (Karamja),1,6106,20\n" +
				"Reldo,1,6107,10\n" +
				"Census,1,6107,15\n" +
				"Reldo,1,6107,25\n" +
				"Sarah,1,6107,35\n" +
				"Ava,1,6107,40\n" +
				"Locator orb,1,6107,50\n" +
				"Key piece (Morytania),1,6107,55\n" +
				"Start bust,1,6105,25\n" +
				"Key piece (Shayzien),1,6105,35\n" +
				"Brundt,1,6108,10\n" +
				"Torfinn,1,6108,15\n" +
				"Start Vorkath,1,6108,25\n" +
				"Vorkath,1,6108,30\n" +
				"Key piece (Ungael),1,6108,35\n" +
				"Mithril door,1,6108,40\n" +
				"Dragon heads,1,6108,45\n" +
				"Dragon key,1,6104,135\n" +
				"Ancient doors,1,6104,140\n" +
				"Dallas Jones (Lithkren),1,6104,145\n" +
				"Zorgoth,1,6104,150\n" +
				"Jardric,1,6104,155\n" +
				"King Roald,1,6104,160\n" +
				"King Thoros,1,6113,1\n" +
				"Chieftan Brundt,1,6114,1\n" +
				"Sir Amik Varze,1,6115,1\n" +
				"Meeting,1,6104,165\n" +
				"Bob,1,6104,170\n" +
				"Start assault,1,6104,175\n" +
				"Assault,1,6104,180\n" +
				"Start agility,1,6104,181\n" +
				"Red dragon,1,6104,182\n" +
				"Iron dragon,1,6104,183\n" +
				"Brutal green dragon,1,6104,184\n" +
				"Start final ship,1,6104,185\n" +
				"Bob,1,6104,186\n" +
				"Green and blue dragons,1,6104,190\n" +
				"Start wave 2,1,6104,191\n" +
				"Red dragon,1,6104,195\n" +
				"Start wave 3,1,6104,196\n" +
				"Rune dragon,1,6104,200\n" +
				"Start Galvek,1,6104,201\n" +
				"Phase 2,1,6099,600\n" +
				"Galvek,1,6104,205\n" +
				"Bob memorial,1,6104,210";
	}
}