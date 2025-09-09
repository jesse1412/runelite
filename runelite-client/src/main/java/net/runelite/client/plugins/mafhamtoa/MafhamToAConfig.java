/*
 * Written by https://github.com/Mafham
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * however I would prefer if you didn't sell this plugin for profit!
 * I made this to teach myself how to develop plugins, this code sux.
 * I do not condone rule-breaking or use of illegal plugins. Thanks :)
 */

package net.runelite.client.plugins.mafhamtoa;

import java.awt.Color;

import net.runelite.client.config.*;

@ConfigGroup ("mafhamtoa")
public interface MafhamToAConfig extends Config{

    enum AkkhaSetting
    {
        FourTick,
        FiveTick,
        Off
    }
    enum MemorySetting
    {
        Manual,
        Follow,
        Off
    }

    enum BabaSetting
    {
        Pie,
        TickCount
    }

    @ConfigSection(
            name = "Zebak",
            description = "Configuration for Zebak",
            closedByDefault = true,
            position = 1
    )
    String zebak = "zebak";

    @ConfigSection(
            name = "Akkha",
            description = "Configuration for Akkha",
            closedByDefault = true,
            position = 2
    )
    String akkha = "akkha";

    @ConfigSection(
            name = "Wardens",
            description = "Configuration for Wardens",
            closedByDefault = true,
            position = 0
    )
    String wardens = "wardens";

    @ConfigSection(
            name = "Baba",
            description = "Configuration for Baba",
            closedByDefault = true,
            position = 4
    )
    String baba = "baba";

    @ConfigSection(
            name = "Mirror Room",
            description = "Configuration for the Mirror Puzzle Room",
            closedByDefault = true,
            position = 6
    )
    String mirrorroom = "mirrorroom";

    @ConfigSection(
            name = "Monkey Room",
            description = "Configuration for the Monke Room",
            closedByDefault = true,
            position = 5
    )
    String monkeyroom = "monkeyroom";

    @ConfigSection(
            name = "Kephri",
            description = "Configuration for Kephri",
            closedByDefault = true,
            position = 3
    )
    String kephri = "kephri";

    @ConfigSection(
            name = "Palm Room",
            description = "Configuration for Palm of Resourcefulness room",
            closedByDefault = true,
            position = 7
    )
    String palm = "palm";

    @ConfigSection(
            name = "Kephri Puzzle",
            description = "Configuration for Kephri Puzzle",
            closedByDefault = true,
            position = 8
    )
    String kephriPuzzle = "kephriPuzzle";

    //---------------sections end--------------------------
    @ConfigItem(
            position = 0,
            keyName = "boulderToggle",
            name = "Show Jug Highlights",
            description = "Highlights which jugs are aligned with boulders",
            section = zebak
    )
    default boolean boulderToggle(){return true;}
    @Alpha
    @ConfigItem(
            keyName = "greenColor",
            name = "Direct Impact Jug Color",
            description = "Color for jugs that can directly impact a boulder",
            position = 1,
            section = zebak
    )
    default Color getGreenColor() {
        return new Color(0, 255, 0, 255);
    }
    @Alpha
    @ConfigItem(
            keyName = "yellowColor",
            name = "Explode Jug Color",
            description = "Color for jugs that you need to roll then shoot",
            position = 2,
            section = zebak
    )
    default Color getYellowColor() {return new Color(255, 255, 0, 255);}
    @Alpha
    @ConfigItem(
            keyName = "safeColor",
            name = "Safe Tile Color",
            description = "Color for poison-free tiles behind the boulders",
            position = 3,
            section = zebak
    )
    default Color getSafeColor() {
        return new Color(0, 255, 0, 255);
    }
    @Alpha
    @ConfigItem(
            keyName = "safeFillColor",
            name = "Safe Tile Fill Color",
            description = "Color for poison-free tiles behind the boulders",
            position = 4,
            section = zebak
    )
    default Color getSafeFillColor() {
        return new Color(0, 255, 0, 25);
    }
    @ConfigItem(
            keyName = "highlightHullFill",
            name = "Highlight Hull",
            description = "Whether or not to draw a filled in hull over the jug",
            position = 5,
            section = zebak
    )
    default boolean highlightHull() {
        return false;
    }

    @ConfigItem(
            keyName = "highlightHullOutline",
            name = "Highlight Outline",
            description = "Whether or not to draw an outline on the jug",
            position = 6,
            section = zebak
    )
    default boolean highlightHullOutline() {
        return true;
    }

    @ConfigItem(
            keyName = "highlightTile",
            name = "Highlight Tile",
            description = "Whether or not to draw tile(s) beneath the jug",
            position = 7,
            section = zebak
    )
    default boolean highlightTile() {
        return false;
    }
    @ConfigItem(
            keyName = "outlineStrokeWidth",
            name = "Outline stroke width",
            description = "Width (px) of the hull outline",
            position = 8,
            section = zebak
    )
    default int getOutlineStrokeWidth() {
        return 2;
    }

    @ConfigItem(
            keyName = "zebakBlood",
            name = "Zebak Blood Timer",
            description = "Timer for Zebak's blood barrage",
            position = 9,
            section = zebak
    )
    default boolean zebakBlood() {
        return true;
    }

    //---------------zebak end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "akkhaTimerToggle",
            name = "Show Special Timer",
            description = "Shows a timer for when Akkha will next use a spec",
            section = akkha
    )
    default boolean akkhaTimerToggle(){return true;}
    @ConfigItem(
            position = 0,
            keyName = "memoryToggle",
            name = "Show Memory Highlights",
            description = "Show where to stand for the memory game",
            section = akkha
    )
    default boolean memoryToggle(){return true;}

    @ConfigItem(
            position = 0,
            keyName = "detonateToggle",
            name = "Show Detonate Highlights",
            description = "Show where not to stand for the detonate special",
            section = akkha
    )
    default boolean detonateToggle(){return true;}

    @ConfigItem(
            position = 0,
            keyName = "orbToggle",
            name = "Show Akkha Orb Tiles",
            description = "Draws tiles ahead of Akkha's orbs so you know where not to stand",
            section = akkha
    )
    default boolean orbToggle(){return true;}

    @ConfigItem(
            position = 0,
            keyName = "lineToggle",
            name = "Show Akkha Orb Lines",
            description = "Draws lines ahead of Akkha's orbs",
            section = akkha
    )
    default boolean lineToggle(){return true;}

    @ConfigItem(
            position = 0,
            keyName = "showShadowTimer",
            name = "Show Shadow Timer",
            description = "Shows a timer for when the shadows are about to go kaboom and explode the quadrants",
            section = akkha
    )
    default boolean showShadowTimer(){return true;}

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "akkhaOrb",
            name = "Akkha Orb first colour",
            description = "Changes color of 1 tile ahead of Akkha's orbs",
            section = akkha
    )
    default Color getAkkhaOrbDangerColor() {return new Color(255, 0, 0, 85);}

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "akkhaDangerOrb",
            name = "Akkha Orb other colours",
            description = "Changes color of the 2 or 3 tiles ahead of Akkha's orbs",
            section = akkha
    )
    default Color getAkkhaOrbColor() {return new Color(255, 128, 0, 85);}

    @ConfigItem(
            position = 4,
            keyName = "akkhaOrbTileAmount",
            name = "Akkha Orb Tile Amount",
            description = "Amount of tiles to draw ahead of Akkha's orbs (1-3)",
            section = akkha
    )
    @Range(
            min = 1,
            max = 3
    )
    default int akkhaOrbTileAmount() {return 2;}

    @ConfigItem(
            position = 5,
            keyName = "hideOrbs",
            name = "Hide Orbs",
            description = "Entity hide orbs",
            section = akkha
    )
    default boolean hideOrbs() {return false;}

    @ConfigItem(
            position = 6,
            keyName = "akkhaSetting",
            name = "Akkha Butterfly Setting",
            description = "Which tiles to draw for Akkha butterfly",
            section = akkha
    )
    default  MafhamToAConfig.AkkhaSetting akkhaSetting(){return AkkhaSetting.FourTick;}

    @ConfigItem(
            position = 7,
            keyName = "memorySetting",
            name = "Akkha Memory Skip Setting",
            description = "Which method for skipping double trouble",
            section = akkha
    )
    default  MafhamToAConfig.MemorySetting memorySetting(){return MemorySetting.Manual;}

    @ConfigItem(
            position = 8,
            keyName = "shadowTimerTextSize",
            name = "Shadow Timer Text Size",
            description = "For the shadows in each corner",
            section = akkha
    )
    @Range(
            min = 1,
            max = 100
    )
    default int shadowTimerTextSize(){return 14;}

    @ConfigItem(
            position = 8,
            keyName = "specialTimerTextSize",
            name = "Special Timer Text Size",
            description = "For the 100 tick timer until Akkha does his specs",
            section = akkha
    )
    @Range(
            min = 1,
            max = 100
    )
    default int specialTimerTextSize(){return 14;}

    @ConfigItem(
            position = 9,
            keyName = "drawLineToNextAkkha",
            name = "Draw Line to Next Akkha",
            description = "Draws a line showing where next akkha is in enrage",
            section = akkha
    )
    default boolean drawLineToNextAkkha() {return true;}


    //---------------akkha end--------------------------

    @Alpha
    @ConfigItem(
            position = 0,
            keyName = "mirrorColor",
            name = "Mirror Tile Color",
            description = "Changes color of the tiles the mirrors need to be placed on",
            section = mirrorroom
    )
    default Color getMirrorColor() {return new Color(0, 255, 0, 85);}

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "mirrorColorYellow",
            name = "Mirror Step Direction Tile Color",
            description = "Changes color of the tiles you need to step from for the mirror to be placed in right orientation",
            section = mirrorroom
    )
    default Color getMirrorYellowColor() {return new Color(255, 255, 0, 85);}

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "wallColor",
            name = "Wall Color",
            description = "Changes color of the walls you need to mine",
            section = mirrorroom
    )
    default Color getWallColor() {return new Color(0, 255, 0, 85);}

    //---------------mirror end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "showMonkeyPanel",
            name = "Show Sight Panel",
            description = "Toggles an info panel showing who has sight and what the issue is",
            section = monkeyroom
    )
    default boolean showMonkeyPanel() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "showWavePanel",
            name = "Show Waves Panel",
            description = "Toggles an info panel showing what the next wave will spawn",
            section = monkeyroom
    )
    default boolean showWavePanel() {return true;}

    //---------------monkey room end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "kephriCounterToggle",
            name = "Show Kephri Counter",
            description = "Shows how many attacks until next spec",
            section = kephri
    )
    default boolean kephriCounterToggle() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "hideScarabs",
            name = "Hide Unattackable Scarab Swarms",
            description = "Hides scarab swarms that are next to Kephri",
            section = kephri
    )
    default boolean hideScarabs() {return true;}

    @ConfigItem(
            position = 2,
            keyName = "highlightScarabs",
            name = "Highlight Scarab Outline",
            description = "Highlights the outline of the scarab swarms",
            section = kephri
    )
    default boolean highlightScarabs() {return true;}

    @ConfigItem(
            position = 3,
            keyName = "scarabGradient",
            name = "Scarab Highlight Gradient",
            description = "Highlights swarms that are closer to Kephri brighter",
            section = kephri
    )
    default boolean scarabGradient() {return true;}

    @ConfigItem(
            position = 4,
            keyName = "hideAttackedSwarms",
            name = "Hide Already Attacked Scarabs",
            description = "Hides swarms that you've already attacked",
            section = kephri
    )
    default boolean hideAttackedSwarms() {return true;}

    @Alpha
    @ConfigItem(
            keyName = "scarabColor",
            name = "Scarab Color",
            description = "Color of the scarab highlight",
            position = 5,
            section = kephri
    )
    default Color scarabColor()
    {
        return Color.cyan;
    }

    @Range(min = 0, max = 50)
    @ConfigItem(
            position = 6,
            keyName = "kephriOutlineWidth",
            name = "Highlight Outline Width",
            description = "Sets the width of the scarab highlight",
            section = kephri
    )
    default int scarabOutlineWidth() {return 2;}

    @ConfigItem(
            position = 7,
            keyName = "showSoliderTimer",
            name = "Show Solider Scarab Timer",
            description = "Shows solider scarab attack timer",
            section = kephri
    )
    default boolean showSoldierScarabTimer() {return true;}

    //---------------kephri end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "showStatueTiles",
            name = "Show Statue Tiles",
            description = "Displays tiles under the statues for when they proc",
            section = palm
    )
    default boolean showStatueTiles() {return false;}

    @ConfigItem(
            position = 1,
            keyName = "showStatueOutline",
            name = "Show Statue Outline",
            description = "Displays an outline on the statues for when they proc",
            section = palm
    )
    default boolean showStatueOutline() {return true;}

    @ConfigItem(
            position = 2,
            keyName = "showPalmPoison",
            name = "Show Poison Tiles",
            description = "Displays tiles for where the poison is about to proc",
            section = palm
    )
    default boolean showPalmPoison() {return true;}

    @ConfigItem(
            position = 3,
            keyName = "statueTileAlpha",
            name = "Statue Tile Alpha",
            description = "Alpha for the color of the tile indicators",
            section = palm
    )
    @Range(
            min = 0,
            max = 255
    )
    default int statueTileAlpha() {return 45;}

    @ConfigItem(
            position = 4,
            keyName = "statueOutlineAlpha",
            name = "Statue Outline Alpha",
            description = "Alpha for the color of the outline indicators",
            section = palm
    )
    @Range(
            min = 0,
            max = 255
    )
    default int statueOutlineAlpha() {return 155;}

    @Range(min = 0, max = 50)
    @ConfigItem(
            position = 10,
            keyName = "outlineWidth",
            name = "Outline/Hull Width",
            description = "Sets the width of the outline/hull highlights",
            section = palm
    )
    default int statueOutlineWidth() {return 2;}

    @Range(min = 0, max = 5)
    @ConfigItem(
            position = 11,
            keyName = "outlineFeather",
            name = "Outline Feather",
            description = "Sets the feather of the outline highlight",
            section = palm
    )
    default int statueOutlineFeather() {return 2;}

    //---------------palm room end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "showWardenTile",
            name = "Show Warden Insanity Tile",
            description = "Shows where the stand after skulls for insanity",
            section = wardens
    )
    default boolean showWardenTile() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "showCoreTimer",
            name = "Show Core Timer",
            description = "Shows a timer for how long the core will last",
            section = wardens
    )
    default boolean showCoreTimer() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "showUfoTiles",
            name = "Show Ufo Tiles",
            description = "",
            section = wardens
    )
    default boolean showUfos() {return true;}

    @ConfigItem(
            position = 2,
            keyName = "showWardenIDs",
            name = "Show IDs",
            description = "Shows wardens and skulls ids for rapid heal tick eat",
            section = wardens
    )
    default boolean showWardenIDs() {return true;}

    //---------------wardens end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "showBabaTimer",
            name = "Show Baba Timer",
            description = "Shows a timer for Baba's attacks",
            section = baba
    )
    default boolean showBabaTimer() {return true;}

    @ConfigItem(
            position = 0,
            keyName = "showBabaGap",
            name = "Show Mind the Gap Warning",
            description = "Shows where not to stand when Ba-Ba goes to boulder phase for mind the gap",
            section = baba
    )
    default boolean showBabaGap() {return true;}

    @ConfigItem(
            keyName = "babaTextHeight",
            name = "Text Height",
            description = "Height of text over Baba",
            position = 1,
            section = baba
    )
    default int babaTextHeight()
    {
        return 300;
    }

    @ConfigItem(
            keyName = "babaFontSize",
            name = "Font Size",
            description = "Size of the font",
            position = 2,
            section = baba
    )
    default int babaFontSize()
    {
        return 18;
    }

    @Alpha
    @ConfigItem(
            keyName = "babaTextColor",
            name = "Text Color",
            description = "Color of the text",
            position = 3,
            section = baba
    )
    default Color babaTextColor()
    {
        return Color.cyan;
    }

    @Alpha
    @ConfigItem(
            keyName = "babaTimingTextColor",
            name = "RedX Timing Text Color",
            description = "Color of the text when you need to click under baba for redX",
            position = 4,
            section = baba
    )
    default Color babaTimingTextColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            position = 5,
            keyName = "babaSetting",
            name = "Baba Counter Setting",
            description = "Draw pie chart or tick counter for Baba's attacks",
            section = baba
    )
    default MafhamToAConfig.BabaSetting babaSetting(){return BabaSetting.Pie;}

    //---------------baba end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "showPuzzleTiles",
            name = "Show Unflipped Puzzle Tiles",
            description = "Shows what the tiles are when unflipped (solo only)",
            section = kephriPuzzle
    )
    default boolean showPuzzleTiles() {return true;}


}