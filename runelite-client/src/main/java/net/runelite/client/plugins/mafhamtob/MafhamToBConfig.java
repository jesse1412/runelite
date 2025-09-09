package net.runelite.client.plugins.mafhamtob;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("mafhamtob")
public interface MafhamToBConfig extends Config {

    enum AGGRESSIVENYLORENDERSTYLE
    {
        TILE,
        HULL
    }

    enum XARPUS_TIMER_STYLE
    {
        PIE,
        NUMBER,
        OFF
    }

    enum VERZIK_TIMER_STYLE
    {
        PIE,
        NUMBER,
        OFF
    }

    enum VERZIK_NYLO_HIGHLIGHT_STYLE
    {
        OUTER_BOX,
        INNER_BOX,
        BOTH
    }

    enum VERZIK_GREEN_BALL_STYLE
    {
        TRUE,
        TILE,
        OFF
    }

    @ConfigSection(
            name = "Maiden",
            description = "Configuration for Maiden",
            closedByDefault = true,
            position = 1
    )
    String maiden = "maiden";

    @ConfigSection(
            name = "Bloat",
            description = "Configuration for Bloat",
            closedByDefault = true,
            position = 2
    )
    String bloat = "bloat";

    @ConfigSection(
            name = "Nylocas",
            description = "Configuration for Nylocas",
            closedByDefault = true,
            position = 3
    )
    String nylocas = "nylocas";

    @ConfigSection(
            name = "Sotetseg",
            description = "Configuration for Sotetseg",
            closedByDefault = true,
            position = 4
    )
    String sotetseg = "sotetseg";

    @ConfigSection(
            name = "Xarpus",
            description = "Configuration for Xarpus",
            closedByDefault = true,
            position = 5
    )
    String xarpus = "xarpus";

    @ConfigSection(
            name = "Verzik",
            description = "Configuration for Verzik",
            closedByDefault = true,
            position = 6
    )
    String verzik = "verzik";

    //---------------sections end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "maidenTimer",
            name = "Show Maiden Timer",
            description = "Shows a timer for Maiden",
            section = maiden
    )
    default boolean maidenTimer(){return true;}

    //---------------maiden end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "bloatTimer",
            name = "Bloat Down Timer",
            description = "Shows how long bloat will be down for",
            section = bloat
    )
    default boolean bloatTimer(){return true;}

    @ConfigItem(
            position = 1,
            keyName = "hidePillar",
            name = "Hide Pillar",
            description = "pillar",
            section = bloat
    )
    default boolean hidePillar(){return true;}

    @ConfigItem(
            position = 3,
            keyName = "stompArea",
            name = "Highlight Stomp Area",
            description = "Highlights where the stomp affects",
            section = bloat
    )
    default boolean stompArea(){return true;}

    @ConfigItem(
            position = 4,
            keyName = "bloatHighlight",
            name = "Highlight Bloat",
            description = "Highlights bloat true tile",
            section = bloat
    )
    default boolean bloatHighlight(){return true;}

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "bloatUpColor",
            name = "Bloat Up Color",
            description = "Color of up bloat",
            section = bloat
    )
    default Color bloatUpColor(){return Color.GREEN;}

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "bloatDownColor",
            name = "Bloat Down Color",
            description = "Color of down bloat",
            section = bloat
    )
    default Color bloatDownColor(){return Color.RED;}

    //---------------Bloat end--------------------------

    @ConfigItem(
            position = 410,
            keyName = "nyloAggressiveOverlay",
            name = "Highlight Aggressive Nylocas",
            description = "Highlights aggressive Nylocas after they spawn",
            section = nylocas
    )
    default boolean nyloAggressiveOverlay()
    {
        return true;
    }

    @ConfigItem(
            position = 411,
            keyName = "nyloAggressiveOverlayStyle",
            name = "Highlight Aggressive Nylocas Style",
            description = "Highlight style for aggressive Nylocas after they spawn.",
            section = nylocas
    )
    default AGGRESSIVENYLORENDERSTYLE nyloAggressiveOverlayStyle()
    {
        return AGGRESSIVENYLORENDERSTYLE.HULL;
    }

    @ConfigItem(
            position = 402,
            keyName = "nyloExplosions",
            name = "Nylocas Explosion Warning",
            description = "Highlights a Nylocas that is about to explode.",
            section = nylocas
    )
    default boolean nyloExplosions()
    {
        return true;
    }

    @ConfigItem(
            position = 412,
            keyName = "removeNyloEntries",
            name = "Remove Nylo Entries",
            description = "",
            section = nylocas
    )
    default boolean removeNyloEntries()
    {
        return true;
    }

    //---------------Nylocas end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "bigBall",
            name = "Highlight Big Ball",
            description = "Highlight whoever has big ball",
            section = sotetseg
    )
    default boolean bigBall()
    {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "bigBallTimer",
            name = "Show Big Ball Timer",
            description = "Shows a timer for when big ball explodey",
            section = sotetseg
    )
    default boolean bigBallTimer()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "highlightOrbs",
            name = "Highlight Orbs",
            description = "Highlights orbs that are targeting you",
            section = sotetseg
    )
    default boolean highlightOrbs()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "orbTimer",
            name = "Show Orb Timer",
            description = "Shows a timer for the orbs",
            section = sotetseg
    )
    default boolean orbTimer()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "mageAttackCounter",
            name = "Show Mage Attack Counter",
            description = "Shows how many mage attacks left until big ball",
            section = sotetseg
    )
    default boolean mageAttackCounter()
    {
        return true;
    }

    @ConfigItem(
            position = 5,
            keyName = "SotetsegMaze1",
            name = "Sotetseg maze",
            description = "",
            section = sotetseg
    )
    default boolean SotetsegMaze1(){ return true; }

    @ConfigItem(
            position = 6,
            keyName = "SotetsegMaze2",
            name = "Sotetseg maze (solo mode)",
            description = "",
            section = sotetseg
    )
    default boolean SotetsegMaze2(){ return true; }

    @ConfigItem(
            position = 7,
            keyName = "SotetsegHMMaze",
            name = "Sotetseg HM Maze",
            description = "",
            section = sotetseg
    )
    default boolean SotetsegHMMaze(){ return true; }

    //---------------Sotetseg end--------------------------

    @ConfigItem(
            position = 0,
            keyName = "xarpusCounterMode",
            name = "Xarpus Counter Mode",
            description = "Timer for when Xarpus will turn/shoot",
            section = xarpus
    )
    default XARPUS_TIMER_STYLE xarpusCounterMode(){ return XARPUS_TIMER_STYLE.PIE; }

    @ConfigItem(
            position = 1,
            keyName = "stareTiles",
            name = "Show Stare Tiles",
            description = "Shows where not to stand in stare",
            section = xarpus
    )
    default boolean stareTiles(){ return true; }

    @ConfigItem(
            position = 2,
            keyName = "screechIndicator",
            name = "Screech Indicator",
            description = "Makes everyone shout screech when screech happens",
            section = xarpus
    )
    default boolean screechIndicator(){ return true; }

    //---------------Xarpus end--------------------------
    @ConfigItem(
            position = 705,
            keyName = "verzikAutosTick",
            name = "Verzik Attack Tick Counter",
            description = "Displays the ticks until Verzik will attack next.",
            section = verzik
    )
    default VERZIK_TIMER_STYLE verzikAutosTick()
    {
        return VERZIK_TIMER_STYLE.PIE;
    }

    @ConfigItem(
            position = 721,
            keyName = "verzikTornado",
            name = "Verzik Personal Tornado Highlight",
            description = "Displays the tornado that is targeting you.",
            section = verzik
    )
    default boolean verzikTornado()
    {
        return true;
    }

    @ConfigItem(
            position = 722,
            keyName = "verzikPersonalTornadoOnly",
            name = "Verzik ONLY Highlight Personal",
            description = "Displays the tornado that is targeting you ONLY after it solves which one is targeting you.",
            section = verzik
    )
    default boolean verzikPersonalTornadoOnly()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 723,
            keyName = "verzikTornadoColor",
            name = "Verzik Tornado Highlight Color",
            description = "Select a color for the Verzik Tornadoes Overlay to be.",
            section = verzik
    )
    default Color verzikTornadoColor()
    {
        return Color.RED;
    }

    @ConfigItem(
            position = 708,
            keyName = "verzikNyloPersonalWarning",
            name = "Verzik Nylo Direct Aggro Warning",
            description = "Highlights the Nylocas that are targeting YOU and ONLY you.",
            section = verzik
    )
    default boolean verzikNyloPersonalWarning()
    {
        return true;
    }

    @ConfigItem(
            position = 709,
            keyName = "verzikNyloOtherWarning",
            name = "Verzik Nylo Indirect Aggro Warnings",
            description = "Highlights the Nylocas that are targeting OTHER players.",
            section = verzik
    )
    default boolean verzikNyloOtherWarning()
    {
        return true;
    }

    @ConfigItem(
            position = 714,
            keyName = "verzikNyloExplodeAOE",
            name = "Verzik Nylo Explosion Area",
            description = "Highlights the area of explosion for the Nylocas (Personal or Indirect Warnings MUST be enabled).",
            section = verzik
    )
    default boolean verzikNyloExplodeAOE()
    {
        return true;
    }

    @ConfigItem(
            position = 715,
            keyName = "verzikNyloHighlightStyle",
            name = "Verzik Nylo Highlight Style",
            description = "Inner box = true tile, outer box = explosion radius",
            section = verzik
    )
    default VERZIK_NYLO_HIGHLIGHT_STYLE verzikNyloHighlightStyle()
    {
        return VERZIK_NYLO_HIGHLIGHT_STYLE.INNER_BOX;
    }

    @ConfigItem(
            position = 716,
            keyName = "verzikGreenBall",
            name = "Verzik Green Ball Highlight",
            description = "Highlights the green ball's target with a counter",
            section = verzik
    )
    default VERZIK_GREEN_BALL_STYLE verzikGreenBall()
    {
        return VERZIK_GREEN_BALL_STYLE.TILE;
    }

}