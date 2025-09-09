package net.runelite.client.plugins.alchemicalhydra;

import java.awt.Color;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Config;
import net.runelite.client.config.Range;

@ConfigGroup("Hydra")

public interface AlchemicalHydraConfig extends Config
{
    @ConfigItem(
            keyName = "HighlightPoison",
            name = "Highlight poison: ",
            description = "Configures whether the poison the Hydra throws should be highlighted."
    )
    default boolean HighlightPoison()
    {
        return true;
    }

    @ConfigItem(
            keyName = "HighlightPoisonDanger",
            name = "Highlight poison colour: ",
            description = ""
    )
    default Color HighlightColourPoison()
    {
        return new Color(40,255,160);
    }

    @ConfigItem(
            keyName = "ShowPrayers",
            name = "Show Prayers: ",
            description = "Configures whether prayers should be shown."
    )
    default boolean ShowPrayers()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showVents",
            name = "Show Vent Timer",
            description = "Shows a timer for when the vents will proc"
    )
    default boolean showVents(){return true;}

    @ConfigItem(
            keyName = "showFlamePoison",
            name = "Show Flame/Poison counter",
            description = "Shows a counter for how many attacks until flame/poison"
    )
    default boolean showFlamePoison(){return true;}
    @Range(max=100)
    @ConfigItem(
            keyName = "masterVolume",
            name = "Master Volume",
            description = "Sets the master volume of all ground item sounds",
            position = 0
    )
    default int masterVolume()
    {
        return 50;
    }
}
