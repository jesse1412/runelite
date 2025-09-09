package net.runelite.client.plugins.mafhamcolo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mafhamcolo")
public interface MafhamColoConfig extends Config {

    @ConfigItem(
            position = 0,
            keyName = "showFremennik",
            name = "Show Fremennik Ticks",
            description = "Shows Fremennik ticks"
    )
    default boolean fremennikTicks(){return false;}

    @ConfigItem(
            position = 1,
            keyName = "showJavelin",
            name = "Show Javelin Timer",
            description = "Shows javelin bois timer"
    )
    default boolean javelinTimer(){return true;}

    @ConfigItem(
            position = 2,
            keyName = "highlightJavelin",
            name = "Highlight Javelin when throwing",
            description = "Highlights javelin boi when he throws"
    )
    default boolean javelinHighlight(){return true;}

    @ConfigItem(
            position = 3,
            keyName = "reinforcementsTimer",
            name = "Reinforcements Timer",
            description = "Reinforcements Timer"
    )
    default boolean reinforcementsTimer(){return true;}

    @ConfigItem(
            position = 4,
            keyName = "lineOfSightHighlight",
            name = "Line of Sight Highlight",
            description = "Highlights stuff if LOS"
    )
    default boolean lineOfSightHL(){return false;}

    @ConfigItem(
            position = 5,
            keyName = "startTiming",
            name = "Wave Start Timing",
            description = "Wave start timing"
    )
    default boolean waveStartTiming(){return true;}
}