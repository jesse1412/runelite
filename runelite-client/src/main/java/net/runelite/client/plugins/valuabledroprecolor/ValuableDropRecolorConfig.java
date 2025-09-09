package net.runelite.client.plugins.valuabledroprecolor;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("valuabledroprecolor")
public interface ValuableDropRecolorConfig extends Config {

    @ConfigSection(
            name = "Transparent Settings",
            description = "Settings for transparent chatbox",
            position = 0
    )
    String transparent = "transparent";

    @ConfigSection(
            name = "Opaque Settings",
            description = "Settings for opaque chatbox",
            position = 0
    )
    String opaque = "opaque";

    @ConfigItem(
            keyName = "highlightedItems",
            name = "Highlighted Items",
            description = "Configures specifically highlighted items. Format: (item), (item)",
            position = 0
    )
    default String getHighlightItems()
    {
        return "";
    }

    @ConfigItem(
            keyName = "recolorHighlightedOnly",
            name = "Recolor Highlighted Drops Only",
            description = "Configures whether or not to only recolor highlighted drops",
            position = 1
    )
    default boolean recolorHighlightedOnly()
    {
        return false;
    }

    @ConfigItem(
            keyName = "lowValuePrice",
            name = "Low value price",
            description = "Configures the start price for low value items",
            position = 2
    )
    default int lowValuePrice()
    {
        return 20000;
    }

    @ConfigItem(
            keyName = "mediumValuePrice",
            name = "Medium value price",
            description = "Configures the start price for medium value items",
            position = 3
    )
    default int mediumValuePrice()
    {
        return 100000;
    }

    @ConfigItem(
            keyName = "highValuePrice",
            name = "High value price",
            description = "Configures the start price for high value items",
            position = 4
    )
    default int highValuePrice()
    {
        return 1000000;
    }

    @ConfigItem(
            keyName = "insaneValuePrice",
            name = "Insane value price",
            description = "Configures the start price for insane value items",
            position = 5
    )
    default int insaneValuePrice()
    {
        return 10000000;
    }

    @Alpha
    @ConfigItem(
            keyName = "defaultColorTransparent",
            name = "Default items",
            description = "Configures the color for default, non-highlighted items",
            position = 1,
            section = transparent
    )
    default Color defaultColorTransparent()
    {
        return Color.lightGray;
    }

    @Alpha
    @ConfigItem(
            keyName = "highlightedColorTransparent",
            name = "Highlighted items",
            description = "Configures the color for highlighted items",
            position = 2,
            section = transparent
    )
    default Color highlightedColorTransparent()
    {
        return Color.decode("#AA00FF");
    }

    @Alpha
    @ConfigItem(
            keyName = "lowValueColorTransparent",
            name = "Low value items",
            description = "Configures the color for low value items",
            position = 3,
            section = transparent
    )
    default Color lowValueColorTransparent()
    {
        return Color.decode("#66B2FF");
    }

    @Alpha
    @ConfigItem(
            keyName = "mediumValueColorTransparent",
            name = "Medium value items",
            description = "Configures the color for medium value items",
            position = 4,
            section = transparent
    )
    default Color mediumValueColorTransparent()
    {
        return Color.decode("#99FF99");
    }

    @Alpha
    @ConfigItem(
            keyName = "highValueColorTransparent",
            name = "High value items",
            description = "Configures the color for high value items",
            position = 5,
            section = transparent
    )
    default Color highValueColorTransparent()
    {
        return Color.decode("#FF9600");
    }

    @Alpha
    @ConfigItem(
            keyName = "insaneValueColorTransparent",
            name = "Insane value items",
            description = "Configures the color for insane value items",
            position = 6,
            section = transparent
    )
    default Color insaneValueColorTransparent()
    {
        return Color.decode("#FF66B2");
    }

    @Alpha
    @ConfigItem(
            keyName = "untradeableDropTransparent",
            name = "Untradeable items",
            description = "Configures the color for untradeable items",
            position = 7,
            section = transparent
    )
    default Color untradeableDropTransparent()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            keyName = "defaultColorOpaque",
            name = "Default items",
            description = "Configures the color for default, non-highlighted items",
            position = 1,
            section = opaque
    )
    default Color defaultColorOpaque()
    {
        return Color.BLACK;
    }

    @Alpha
    @ConfigItem(
            keyName = "highlightedColorOpaque",
            name = "Highlighted items",
            description = "Configures the color for highlighted items",
            position = 2,
            section = opaque
    )
    default Color highlightedColorOpaque()
    {
        return Color.decode("#AA00FF");
    }

    @Alpha
    @ConfigItem(
            keyName = "lowValueColorOpaque",
            name = "Low value items",
            description = "Configures the color for low value items",
            position = 3,
            section = opaque
    )
    default Color lowValueColorOpaque()
    {
        return Color.decode("#0000FF");
    }

    @Alpha
    @ConfigItem(
            keyName = "mediumValueColorOpaque",
            name = "Medium value items",
            description = "Configures the color for medium value items",
            position = 4,
            section = opaque
    )
    default Color mediumValueColorOpaque()
    {
        return Color.decode("#008100");
    }

    @Alpha
    @ConfigItem(
            keyName = "highValueColorOpaque",
            name = "High value items",
            description = "Configures the color for high value items",
            position = 5,
            section = opaque
    )
    default Color highValueColorOpaque()
    {
        return Color.decode("#985C00");
    }

    @Alpha
    @ConfigItem(
            keyName = "insaneValueColorOpaque",
            name = "Insane value items",
            description = "Configures the color for insane value items",
            position = 6,
            section = opaque
    )
    default Color insaneValueColorOpaque()
    {
        return Color.decode("#FF007D");
    }

    @Alpha
    @ConfigItem(
            keyName = "untradeableDropOpaque",
            name = "Untradeable items",
            description = "Configures the color for untradeable items",
            position = 7,
            section = opaque
    )
    default Color untradeableDropOpaque()
    {
        return Color.RED;
    }


}