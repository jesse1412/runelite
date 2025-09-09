package net.runelite.client.plugins.mafhammuspah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("mafhammuspah")
public interface MafhamMuspahConfig extends Config {

    @ConfigItem(
            keyName = "xpMultiplier",
            name = "Xp multiplier",
            description = "The bonus xp multiplier (from season game mode for example) that should be factored when calculating the hit",
            position = 0
    )
    default double xpMultiplier()
    {
        return 1;
    }

    @ConfigItem(
            keyName = "showSpikeTimer",
            name = "Show Spike Timer",
            description = "Shows timer of how long until spikes end",
            position = 1
    )
    default boolean showSpikeTimer()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showDamageCounter",
            name = "Show Damage Counter",
            description = "Shows a number of how much dmg you've done this phase",
            position = 2
    )
    default boolean showDamageCounter()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showPhaseSwitchOverlay",
            name = "Show Phase Switch Overlay",
            description = "Highlights Muspah when phase switch happening",
            position = 2
    )
    default boolean showPhaseSwitchOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showMeleeTiming",
            name = "Show Melee Step Back",
            description = "Shows when to step back",
            position = 3
    )
    default boolean showMeleeTiming()
    {
        return true;
    }

    @ConfigItem(
            keyName = "pieChartHeight",
            name = "Step Back Pie Height",
            description = "How high the pie chart is",
            position = 4
    )
    @Range(
            min = 1,
            max = 1000
    )
    default int pieChartHeight()
    {
        return 50;
    }

    @ConfigItem(
            keyName = "showCloudTiles",
            name = "Show Cloud Tiles",
            description = "Show Cloud Tiles",
            position = 5
    )
    default boolean showCloudTiles()
    {
        return true;
    }
    @ConfigItem(
            keyName = "showRemainingHP",
            name = "Show Remaining HP Until Spec",
            description = "",
            position = 6
    )
    default boolean showRemainingHP()
    {
        return true;
    }

    @ConfigItem(
            keyName = "cloudPhaseTimer",
            name = "Show Cloud Phase Timer",
            description = "",
            position = 7
    )
    default boolean cloudPhaseTimer()
    {
        return true;
    }
}