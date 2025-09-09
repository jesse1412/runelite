package net.runelite.client.plugins.menuentryrecolor;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("menuentryrecolor")
public interface MenuEntryRecolorConfig extends Config {

    @ConfigSection(
            name = "Group 1",
            description = "",
            position = 1
    )
    String group1Section = "group1";

    @ConfigSection(
            name = "Group 2",
            description = "",
            position = 2
    )
    String group2Section = "group2";

    @ConfigSection(
            name = "Group 3",
            description = "",
            position = 3
    )
    String group3Section = "group3";

    @ConfigSection(
            name = "Group 4",
            description = "",
            position = 4
    )
    String group4Section = "group4";

    @ConfigSection(
            name = "Group 5",
            description = "",
            position = 5
    )
    String group5Section = "group5";

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "group1Color",
            name = "Color 1",
            description = "Color for group 1 highlights",
            section = group1Section
    )
    default Color getGroup1Color() {
        return Color.GREEN;
    }

    @ConfigItem(
            position = 2,
            keyName = "group1Npcs",
            name = "NPCs to highlight with Color 1",
            description = "NPCs to highlight with Color 1",
            section = group1Section
    )
    default String getNpcs1() {
        return "";
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "group2Color",
            name = "Color 2",
            description = "Color for group 2 highlights",
            section = group2Section
    )
    default Color getGroup2Color() {
        return Color.RED;
    }

    @ConfigItem(
            position = 2,
            keyName = "group2Npcs",
            name = "NPCs to highlight with Color 2",
            description = "NPCs to highlight with Color 2",
            section = group2Section
    )
    default String getNpcs2() {
        return "";
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "group3Color",
            name = "Color 3",
            description = "Color for group 3 highlights",
            section = group3Section
    )
    default Color getGroup3Color() {return Color.BLUE;}

    @ConfigItem(
            position = 2,
            keyName = "group3Npcs",
            name = "NPCs to highlight with Color 3",
            description = "NPCs to highlight with Color 3",
            section = group3Section
    )
    default String getNpcs3() {
        return "";
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "group4Color",
            name = "Color 4",
            description = "Color for group 4 highlights",
            section = group4Section
    )
    default Color getGroup4Color() {return Color.ORANGE;}

    @ConfigItem(
            position = 2,
            keyName = "group4Npcs",
            name = "NPCs to highlight with Color 4",
            description = "NPCs to highlight with Color 4",
            section = group4Section
    )
    default String getNpcs4() {
        return "";
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "group5Color",
            name = "Color 5",
            description = "Color for group 5 highlights",
            section = group5Section
    )
    default Color getGroup5Color() {return Color.pink;}

    @ConfigItem(
            position = 2,
            keyName = "group5Npcs",
            name = "NPCs to highlight with Color 5",
            description = "NPCs to highlight with Color 5",
            section = group5Section
    )
    default String getNpcs5() {
        return "";
    }
}