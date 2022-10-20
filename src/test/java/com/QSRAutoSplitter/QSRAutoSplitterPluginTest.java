package com.QSRAutoSplitter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class QSRAutoSplitterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(QSRAutoSplitterPlugin.class);
		RuneLite.main(args);
	}
}