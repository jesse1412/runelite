package net.runelite.client.plugins.mafhamvardorvis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup ("mafhamvardorvis")
public interface MafhamVardorvisConfig extends Config {

    @ConfigItem(
            position = 0,
            keyName = "showAxePaths",
            name = "Show Axe Paths",
            description = "Highlights the path the axes will take"
    )
    default boolean showAxePaths(){return false;}

    @ConfigItem(
            position = 1,
            keyName = "showCornerTiming",
            name = "Show Corner Timing",
            description = "Highlights a tile for corner runthrough timing (NE corner)"
    )
    default boolean showCornerTiming(){return true;}

    @ConfigItem(
            position = 2,
            keyName = "showRangeOverlay",
            name = "Show Range Overlay",
            description = "Displays an overlay for when the range head appears"
    )
    default boolean showRangeOverlay(){return true;}

    @ConfigItem(
            position = 3,
            keyName = "playRangeSound",
            name = "Play Sound on Range",
            description = "Plays a sound when the range head appears"
    )
    default boolean playRangeSound(){return true;}

    @ConfigItem(
            position = 4,
            keyName = "entityHideAxes",
            name = "Entity Hide Axes",
            description = "Makes everything cleaner to look at, need to rely on highlighted tiles"
    )
    default boolean entityHideAxes(){return false;}

    @ConfigItem(
            position = 5,
            keyName = "highlightOsu",
            name = "Highlight osu",
            description = "Highlight osu"
    )
    default boolean highlightOsu(){return true;}

    @Range(max=100)
    @ConfigItem(
            keyName = "masterVolume",
            name = "Master Volume",
            description = "Sets the master volume of range sound",
            position = 100
    )
    default int masterVolume()
    {
        return 50;
    }


}