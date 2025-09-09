package net.runelite.client.plugins.cerberuscycle;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("cerberuscycle")
public interface CerberusCycleConfig extends Config {

    @ConfigItem(
            keyName = "showTickCounter",
            name = "Show Tick Counter",
            description = "Show the tick counter",
            position = 0
    )
    default boolean showTickCounter() {return false;}

    @ConfigItem(
            keyName = "textHeight",
            name = "Text Height",
            description = "Height of text over Cerberus",
            position = 1
    )
    default int textHeight()
    {
        return 130;
    }

    @ConfigItem(
            keyName = "fontSize",
            name = "Font Size",
            description = "Size of the font",
            position = 2
    )
    default int fontSize()
    {
        return 12;
    }

    @ConfigItem(
            keyName = "showShadow",
            name = "Show Text Shadow",
            description = "Show a shadow under the text",
            position = 3
    )
    default boolean showShadow() {return true;}

    @Alpha
    @ConfigItem(
            keyName = "defaultColor",
            name = "Default Text Color",
            description = "Color of the font",
            position = 4
    )
    default Color defaultColor()
    {
        return Color.white;
    }

    @Alpha
    @ConfigItem(
            keyName = "tripleColor",
            name = "Triple Attack Text Color",
            description = "Color of the triple attack",
            position = 5
    )
    default Color tripleColor()
    {
        return Color.green;
    }

    @Alpha
    @ConfigItem(
            keyName = "lavaColor",
            name = "Lava Attack Text Color",
            description = "Color of the lava attack",
            position = 6
    )
    default Color lavaColor()
    {
        return Color.orange;
    }

    @Alpha
    @ConfigItem(
            keyName = "ghostColor",
            name = "Ghost Attack Text Color",
            description = "Color of the ghost attack",
            position = 7
    )
    default Color ghostColor()
    {
        return Color.cyan;
    }
}