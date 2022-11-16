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
		return "";
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
		return "";
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
		return "";
	}

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
		return "";
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
		return "";
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
		return "";
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
		return "";
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
		return "";
	}
}
