package net.runelite.client.plugins.mafhamplayerindicators;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("mafhamplayerindicators")
public interface MafhamPlayerIndicatorsConfig extends Config
{
    enum HighlightSetting
    {
        TILE,
        TRUE,
        BOTH;
    }

    @ConfigSection(
            name = "Highlight Style",
            description = "How you want the highlight to be configured",
            closedByDefault = true,
            position = 1
    )
    String Style = "style";

    @ConfigSection(
            name = "Tiles",
            description = "Configuration for tile highlights",
            closedByDefault = true,
            position = 2
    )
    String Tile = "tile";

    @ConfigSection(
            name = "Names",
            description = "Configuration for name highlights",
            closedByDefault = true,
            position = 3
    )
    String Names = "names";

    //---------------sections end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "highlightStyle",
            name = "Tile Highlight Style",
            description = "Configures whether you want true tile, normal tile or both (why?)",
            section = Style
    )
    default HighlightSetting hlSetting()
    {
        return HighlightSetting.TRUE;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "tileColor",
            name = "Player Tile Color",
            description = "Changes color of the player tile highlight",
            section = Style
    )
    default Color tileColor()
    {
        return new Color(0, 255, 0,25);
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "outlineColor",
            name = "Player Tile Outline Color",
            description = "Changes color of the player tile outline highlight",
            section = Style
    )
    default Color tileOutlineColor()
    {
        return new Color(0, 255, 0,255);
    }

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "textColor",
            name = "Player Text Color",
            description = "Changes color of the text above the player",
            section = Style
    )
    default Color textColor()
    {
        return new Color(0, 255, 0,255);
    }

    @ConfigItem(
            keyName = "outlineStrokeWidth",
            name = "Outline stroke width",
            description = "Width (px) of the tile outline",
            position = 4,
            section = Style
    )
    default int getOutlineStrokeWidth() {
        return 2;
    }

    @ConfigItem(
            position = 5,
            keyName = "highlightEveryoneTile",
            name = "Highlight All Player Tiles",
            description = "Highlights every player's tile",
            section = Tile
    )
    default boolean highlightAll(){return false;}

    @ConfigItem(
            position = 6,
            keyName = "highlightFriendTile",
            name = "Highlight Friends' Tiles",
            description = "Highlights friends' tiles",
            section = Tile
    )
    default boolean highlightFriends(){return false;}

    @ConfigItem(
            position = 7,
            keyName = "highlightRaidsTile",
            name = "Highlight Raids Members' Tiles",
            description = "Highlight all players' tiles when in a raid (ToA, ToB, CoX)",
            section = Tile
    )
    default boolean highlightRaids(){return false;}

    @ConfigItem(
            position = 8,
            keyName = "highlightListTile",
            name = "Highlight List Tiles",
            description = "Highlights players' tiles from the list",
            section = Tile
    )
    default boolean highlightList(){return true;}

    @ConfigItem(
            position = 9,
            keyName = "highlightAllNames",
            name = "Highlight All Player Names",
            description = "Highlights all players' names",
            section = Names
    )
    default boolean highlightAllNames(){return false;}

    @ConfigItem(
            position = 10,
            keyName = "highlightFriendNames",
            name = "Highlight Friends' Names",
            description = "Highlights all players' names",
            section = Names
    )
    default boolean highlightFriendsNames(){return true;}

    @ConfigItem(
            position = 12,
            keyName = "highlightListNames",
            name = "Highlight List Names",
            description = "Highlights listed players' names",
            section = Names
    )
    default boolean highlightListNames(){return true;}

    @ConfigItem(
            position = 11,
            keyName = "highlightRaidsNames",
            name = "Highlight Raids Members' Names",
            description = "Highlight all players' names when in a raid (ToA, ToB, CoX)",
            section = Names
    )
    default boolean highlightRaidsNames(){return true;}

    @ConfigItem(
            keyName = "playerNames",
            name = "Highlight List Names",
            description = "Player names to highlight"
    )
    default String getPlayerNames(){return "";}
}