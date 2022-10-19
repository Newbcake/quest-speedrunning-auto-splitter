package com.QSRAutoSplitter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface QSRAutoSplitterConfig extends Config
{
	@ConfigItem(
			position = 2,
			keyName = "port",
			name = "Port",
			description = "Port for the LiveSplit server. (Restart required)"
	)
	default int port() {
		return 16834;
	}

	@ConfigItem(
			keyName = "interpolate",
			name = "Interpolate timer between ticks",
			description = "When unchecked, time will only be updated in 0.6t increments.",
			position = 2
	)
	default boolean interpolate() {
		return false;
	}
}
