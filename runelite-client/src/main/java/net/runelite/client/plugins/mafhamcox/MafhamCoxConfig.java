package net.runelite.client.plugins.mafhamcox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("mafhamcox")
public interface MafhamCoxConfig extends Config {

    enum VespulaSetting
    {
        EASY,
        HARD
    }

    @ConfigSection(
            name = "Screen Marker",
            description = "Configuration for the screen marker",
            closedByDefault = true,
            position = 0
    )
    String marker = "marker";

    @ConfigSection(
            name = "Vespula",
            description = "Configuration for Vespula",
            closedByDefault = true,
            position = 1
    )
    String vespula = "vespula";

    @ConfigSection(
            name = "Muttadiles",
            description = "Configuration for Muttadiles",
            closedByDefault = true,
            position = 2
    )
    String muttadiles = "muttadiles";

    @ConfigSection(
            name = "Object Highlights",
            description = "Configuration for object highlights",
            closedByDefault = true,
            position = 6
    )
    String objHighlights = "objHighlights";

    @ConfigSection(
            name = "Crabs",
            description = "Configuration for crabs room",
            closedByDefault = true,
            position = 3
    )
    String crabs = "crabs";

    @ConfigSection(
            name = "Olm",
            description = "Configuration for olm",
            closedByDefault = true,
            position = 6
    )
    String olm = "olm";

    @ConfigSection(
            name = "Tekton",
            description = "Configuration for tekton",
            closedByDefault = true,
            position = 4
    )
    String tekton = "tekton";

    @ConfigSection(
            name = "Vasa",
            description = "Configuration for vasa",
            closedByDefault = true,
            position = 5
    )
    String vasa = "vasa";

    //------------------------------------------------sections end--------------------------------------
    @ConfigItem(
            keyName = "showMakeParty",
            name = "Make Party Marker",
            description = "Configures whether or not the make party screen marker is displayed",
            position = 0,
            section = marker
    )
    default boolean showMakeParty()
    {
        return true;
    }
    @ConfigItem(
            keyName = "showReloader",
            name = "Show Reloader",
            description = "Configures whether or not to show the reload raid button",
            position = 1,
            section = marker
    )
    default boolean showReloader(){return true;}

    @ConfigItem(
            keyName = "outlineStrokeWidth",
            name = "Marker stroke width",
            description = "Width (px) of the tile outline",
            position = 3,
            section = marker
    )
    default int getOutlineStrokeWidth() {
        return 1;
    }

    @Alpha
    @ConfigItem(
            keyName = "markerColor",
            name = "Marker Color",
            description = "Color for the screen marker",
            position = 4,
            section = marker
    )
    default Color markerColor() {
        return new Color(255, 0, 0, 25);
    }

    //------------------------------------------------marker end--------------------------------------

    @ConfigItem(
            name = "—————— Vespula ——————",
            keyName = "vespula divider",
            description = "",
            position = 0,
            section = vespula
    )
    void vespulaDivider();

    @ConfigItem(
            name = "—————— Colors ——————",
            keyName = "colors divider",
            description = "",
            position = 6,
            section = vespula
    )
    void colorsDivider();

    @ConfigItem(
            keyName = "showVespulaHelper",
            name = "Show Vespula Indicator",
            description = "Shows when to click on the portal based on your prayer enhance regen timer",
            position = 1,
            section = vespula
    )
    default boolean showVespulaHelper()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 8,
            keyName = "portalColor",
            name = "Portal Color",
            description = "Changes color of the portal when you need to click on it",
            section = vespula
    )
    default Color portalColor()
    {
        return new Color(0, 255, 0,255);
    }

    @ConfigItem(
            position = 4,
            keyName = "vespulaHull",
            name = "Highlight Portal Hull",
            description = "Highlights the portal's hull",
            section = vespula
    )
    default boolean vespulaHull() {return false;}

    @ConfigItem(
            position = 7,
            keyName = "portalHullAlpha",
            name = "Portal Hull Alpha",
            description = "Alpha of the portal hull highlight",
            section = vespula
    )
    @Range(
            max = 255
    )
    default int portalHullAlpha() {return 80;}

    @ConfigItem(
            position = 3,
            keyName = "vespulaOutline",
            name = "Highlight Portal Outline",
            description = "Highlights the portal's outline",
            section = vespula
    )
    default boolean vespulaOutline() {return true;}

    @ConfigItem(
            keyName = "showRegenTicks",
            name = "Show Regen Ticks",
            description = "Shows how many ticks until prayer regen over the portal",
            position = 5,
            section = vespula
    )
    default boolean showRegenTicks()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 9,
            keyName = "regenTextColor",
            name = "Regen Text Color",
            description = "Changes color of the regen ticks text over the portal",
            section = vespula
    )
    default Color regenTicksTextColor()
    {
        return new Color(255, 255, 0,255);
    }

    @ConfigItem(
            position = 2,
            keyName = "indicatorMode",
            name = "Indicator Mode",
            description = "Easy: Highlight portal when 1 tick until prayer regens. Hard = Highlight portal when your HP and ticks until regen are optimal, sometimes forces you to click floor and prayer on same tick. WARNING: DIFFICULT",
            section = vespula
    )
    default MafhamCoxConfig.VespulaSetting vespSetting()
    {
        return VespulaSetting.EASY;
    }
    @Alpha
    @ConfigItem(
            position = 10,
            keyName = "tileColor",
            name = "Vespula Tile Color",
            description = "Color of the tile showing where to run to at Vespula",
            section = vespula
    )
    default Color vespTileColor(){return new Color(0,255,0,255);}

    //------------------------------------------------vespula end--------------------------------------

    @ConfigItem(
            name = "—————— Passages ——————",
            keyName = "passages divider",
            description = "",
            position = 0,
            section = objHighlights
    )
    void passagesDivider();

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "passagesColor",
            name = "Passages Color",
            description = "Changes color of the passages highlight",
            section = objHighlights
    )
    default Color passagesColor()
    {
        return new Color(0, 255, 0,255);
    }

    @ConfigItem(
            position = 2,
            keyName = "passagesHull",
            name = "Highlight Passages Hull",
            description = "Configures whether to highlight hull for passages",
            section = objHighlights
    )
    default boolean passagesHull() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "passagesOutline",
            name = "Highlight Passages Outline",
            description = "Configures whether to highlight outline for passages",
            section = objHighlights
    )
    default boolean passagesOutline() {return false;}

    @ConfigItem(
            name = "—————— Chests ——————",
            keyName = "chests divider",
            description = "",
            position = 6,
            section = objHighlights
    )
    void chestsDivider();

    @Alpha
    @ConfigItem(
            position = 11,
            keyName = "chestsColor",
            name = "Chests Color",
            description = "Changes color of the chests highlight",
            section = objHighlights
    )
    default Color chestsColor()
    {
        return new Color(0, 255, 0,255);
    }

    @ConfigItem(
            position = 8,
            keyName = "chestsHull",
            name = "Highlight Chests Hull",
            description = "Configures whether to highlight hull for chests",
            section = objHighlights
    )
    default boolean chestsHull() {return true;}

    @ConfigItem(
            position = 7,
            keyName = "chestsOutline",
            name = "Highlight Chests Outline",
            description = "Configures whether to highlight outline for chests",
            section = objHighlights
    )
    default boolean chestsOutline() {return false;}

    @ConfigItem(
            name = "—————— Shortcuts ——————",
            keyName = "chests divider",
            description = "",
            position = 12,
            section = objHighlights
    )
    void shortcutsDivider();

    @ConfigItem(
            position = 13,
            keyName = "shortcutsHull",
            name = "Highlight Shortcuts Hull",
            description = "Configures whether to highlight hull for shortcuts",
            section = objHighlights
    )
    default boolean shortcutsHull() {return true;}

    @ConfigItem(
            position = 14,
            keyName = "shortcutsOutline",
            name = "Highlight Shortcuts Outline",
            description = "Configures whether to highlight outline for shortcuts",
            section = objHighlights
    )
    default boolean shortcutsOutline() {return false;}

    @Alpha
    @ConfigItem(
            position = 16,
            keyName = "boulderColor",
            name = "Boulder Color",
            description = "Changes color of the boulder highlight",
            section = objHighlights
    )
    default Color boulderColor()
    {
        return new Color(255, 0, 0,255);
    }

    @Alpha
    @ConfigItem(
            position = 17,
            keyName = "treeColor",
            name = "Woodcutting Color",
            description = "Changes color of the woodcutting highlight",
            section = objHighlights
    )
    default Color woodcuttingColor()
    {
        return new Color(0, 255, 0,255);
    }

    @Alpha
    @ConfigItem(
            position = 18,
            keyName = "miningColor",
            name = "Mining Color",
            description = "Changes color of the mining highlight",
            section = objHighlights
    )
    default Color miningColor()
    {
        return new Color(0, 255, 255,255);
    }

    @ConfigItem(
            name = "—————— Energy Wells ——————",
            keyName = "wells divider",
            description = "",
            position = 19,
            section = objHighlights
    )
    void wellsDivider();

    @ConfigItem(
            position = 20,
            keyName = "energyWellHull",
            name = "Highlight Energy Well Hull",
            description = "Configures whether to highlight hull for energy wells",
            section = objHighlights
    )
    default boolean energyWellHull() {return true;}

    @ConfigItem(
            position = 21,
            keyName = "energyWellOutline",
            name = "Highlight Energy Well Outline",
            description = "Configures whether to highlight outline for energy wells",
            section = objHighlights
    )
    default boolean energyWellOutline() {return false;}

    @ConfigItem(
            position = 21,
            keyName = "energyWellTile",
            name = "Highlight Energy Well Tile",
            description = "Configures whether to highlight tile for energy wells",
            section = objHighlights
    )
    default boolean energyWellTile() {return true;}

    @Alpha
    @ConfigItem(
            position = 23,
            keyName = "wellColor",
            name = "Energy Well Color",
            description = "Changes color of the energy well highlight",
            section = objHighlights
    )
    default Color energyWellColor()
    {
        return new Color(0, 255, 0,255);
    }

    //------------------------------------------------highlights end--------------------------------------

    @ConfigItem(
            position = 0,
            keyName = "showMeatTree",
            name = "Show Tree Chop Timer",
            description = "Shows a timer for when your next chop will occur",
            section = muttadiles
    )
    default boolean showMeatTree(){return true;}

    @ConfigItem(
            position = 1,
            keyName = "treeTextSize",
            name = "Meat Tree Text Size",
            description = "Size of the tree counter text",
            section = muttadiles
    )
    default int treeTextSize(){return 14;}

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "muttaTreeColor",
            name = "Meat Tree Text Color",
            description = "Changes color of the tree counter text",
            section = muttadiles
    )
    default Color treeColor()
    {
        return new Color(255, 255, 0,255);
    }

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "tick1Color",
            name = "Meat Tree Reaggro Text Color",
            description = "Color of the tick (1) which is the tick you need to click back on the tree in order to get a chop",
            section = muttadiles
    )
    default Color tick1Color()
    {
        return new Color(0, 255, 0,255);
    }

    //------------------------------------------------muttadiles end--------------------------------------

    @ConfigItem(
            position = 0,
            keyName = "isCrabsEnabled",
            name = "Show Crabs Tiles",
            description = "Configures whether or not to show tile highlights at crabs",
            section = crabs
    )
    default boolean isCrabsEnabled(){return true;}

    @ConfigItem(
            position = 1,
            keyName = "isCrabsHullEnabled",
            name = "Highlight Crabs Hull",
            description = "Highlights crabs hull when they are on the highlighted tiles",
            section = crabs
    )
    default boolean crabsHull(){return true;}

    @ConfigItem(
            position = 2,
            keyName = "isCrabsOutlineEnabled",
            name = "Highlight Crabs Outline",
            description = "Highlights crabs outline when they are on the highlighted tiles",
            section = crabs
    )
    default boolean crabsOutline(){return false;}

    @ConfigItem(
            position = 5,
            keyName = "crabsHullAlpha",
            name = "Crabs Hull Alpha",
            description = "Alpha of the crabs hull highlight",
            section = crabs
    )
    @Range(
            max = 255
    )
    default int crabsHullAlpha() {return 80;}

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "crabsTileColor",
            name = "Crabs Tile Color",
            description = "Color of the tiles showing where to place the crabs",
            section = crabs
    )
    default Color crabsTileColor()
    {
        return new Color(0, 255, 0,255);
    }

    //------------------------------------------------crabs end--------------------------------------

    @ConfigItem(
            name = "—————— Olm ——————",
            keyName = "olm",
            description = "",
            position = 0,
            section = olm
    )
    void olmDivider();

    @ConfigItem(
            position = 1,
            keyName = "showCurrentAttack",
            name = "Show Current Attack",
            description = "Shows the current attack during olm.",
            section = olm
    )
    default boolean showCurrentAttack()
    {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = "showNextAttack",
            name = "Show Next Attack",
            description = "Shows the next attack during olm.",
            section = olm
    )
    default boolean showNextAttack()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "showTickCycle",
            name = "Show Tick Cycle",
            description = "Shows the 4 tick cycle during olm.",
            section = olm
    )
    default boolean showTickCycle()
    {
        return false;
    }

    @ConfigItem(
            position = 5,
            keyName = "showHeadPhase",
            name = "Show During Head Phase",
            description = "Shows overlays during head phase.",
            section = olm
    )
    default boolean showHeadPhase()
    {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "showNextSpecial",
            name = "Show Next Special",
            description = "Shows the next special attack during olm.",
            section = olm
    )
    default boolean showNextSpecial()
    {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "olmDebug",
            name = "Debug Info",
            description = "Dev tool to show info about olm",
            section = olm
    )
    default boolean olmDebug()
    {
        return false;
    }

    @ConfigItem(
            name = "—————— Labels ——————",
            keyName = "labels divider",
            description = "",
            position = 9,
            section = olm
    )
    void labelsDivider();

    @ConfigItem(
            position = 10,
            keyName = "showCurrentLabel",
            name = "Show Current Attack Label",
            description = "Labels the current attack at olm with 'Curr:'",
            section = olm
    )
    default boolean showCurrentLabel()
    {
        return false;
    }

    @ConfigItem(
            position = 11,
            keyName = "showNextLabel",
            name = "Show Next Attack Label",
            description = "Labels the next attack at olm with 'Next:'",
            section = olm
    )
    default boolean showNextLabel()
    {
        return true;
    }

    @ConfigItem(
            position = 12,
            keyName = "showTickLabel",
            name = "Show Tick Counter Label",
            description = "Labels 4 tick counter at olm with 'Tick:'",
            section = olm
    )
    default boolean showTickLabel()
    {
        return true;
    }

    @ConfigItem(
            name = "—————— Colors ——————",
            keyName = "colors divider",
            description = "",
            position = 13,
            section = olm
    )
    void olmColorsDivider();

    @ConfigItem(
            position = 14,
            keyName = "olmRegularColor",
            name = "Text Color",
            description = "Changes color of Olm's tick counter",
            section = olm
    )
    default Color olmRegularColor()
    {
        return new Color(255, 255, 255);
    }

    @ConfigItem(
            position = 15,
            keyName = "olmSpecialColor",
            name = "Special Color",
            description = "Changes color of a special on Olm's tick counter",
            section = olm
    )
    default Color olmSpecialColor()
    {
        return new Color(89, 255, 0);
    }

    @ConfigItem(
            name = "—————— Text ——————",
            keyName = "text divider",
            description = "",
            position = 16,
            section = olm
    )
    void textDivider();

    @ConfigItem(
            position = 17,
            keyName = "fontStyle",
            name = "Font Style",
            description = "Bold/Italics/Plain",
            section = olm
    )
    default MafhamCoxConfig.FontStyle fontStyle()
    {
        return MafhamCoxConfig.FontStyle.BOLD;
    }

    @Range(
            min = 9,
            max = 20
    )
    @ConfigItem(
            position = 18,
            keyName = "textSize",
            name = "Text Size",
            description = "Text Size for Timers.",
            section = olm
    )
    @Units(Units.PIXELS)
    default int textSize()
    {
        return 14;
    }

    @Getter
    @AllArgsConstructor
    enum FontStyle
    {
        BOLD("Bold", Font.BOLD),
        ITALIC("Italic", Font.ITALIC),
        PLAIN("Plain", Font.PLAIN);

        private final String name;
        private final int font;

        @Override
        public String toString()
        {
            return this.getName();
        }
    }

    @ConfigItem(
            name = "—————— Crystal Bombs ——————",
            keyName = "crystal divider",
            description = "",
            position = 19,
            section = olm
    )
    void crystalDivider();

    @ConfigItem(
            name = "Highlight Crystal Bombs",
            keyName = "crystalbombs",
            description = "Highlight the crystal bombs' explosion radius",
            position = 20,
            section = olm
    )
    default boolean showBombs() {return true;}

    @ConfigItem(
            position = 21,
            keyName = "crystalBombColor",
            name = "Crystal Bombs Color",
            description = "Changes color of the crystal bombs highlight",
            section = olm
    )
    default Color crystalBombsColor()
    {
        return new Color(255, 0, 0);
    }

    @ConfigItem(
            position = 22,
            keyName = "crystalBombsAlpha",
            name = "Crystal Bombs Alpha",
            description = "Alpha of the crystal bombs highlight",
            section = olm
    )
    @Range(
            max = 255
    )
    default int crystalBombsAlpha() {return 25;}

    @ConfigItem(
            name = "—————— Interphase Timer ——————",
            keyName = "interphase divider",
            description = "",
            position = 23,
            section = olm
    )
    void interPhaseDivider();

    @ConfigItem(
            name = "Show Interphase Timer",
            keyName = "interPhaseTimer",
            description = "Show a timer between Olm's phases",
            position = 24,
            section = olm
    )
    default boolean showInterPhaseTimer() {return true;}

    @ConfigItem(
            position = 25,
            keyName = "interPhaseColor",
            name = "Interphase Timer Color",
            description = "Changes color of the interphase timer",
            section = olm
    )
    default Color interPhaseTimerColor()
    {
        return new Color(0, 255, 255);
    }

    @ConfigItem(
            position = 26,
            keyName = "interPhaseSize",
            name = "Interphase Timer Size",
            description = "Size of the interphase timer text",
            section = olm
    )
    default int interPhaseTimerSize(){return 14;}

    @ConfigItem(
            name = "Highlight Falling Crystals",
            keyName = "highlightFallingCrystals",
            description = "Highlights falling crystals",
            position = 27,
            section = olm
    )
    default boolean highlightFallingCrystals() {return true;}

    @ConfigItem(
            name = "—————— Experimental ——————",
            keyName = "experimental divider",
            description = "",
            position = 50,
            section = olm
    )
    void experimentalDivider();

    @ConfigItem(
            name = "Make camera face Olm",
            keyName = "cameraOlm",
            description = "Makes the camera face Olm when he spawns",
            position = 51,
            section = olm
    )
    default boolean cameraOlm() {return false;}

    //------------------------------------------------olm end--------------------------------------

    @ConfigItem(
            name = "Show Safespot Timer",
            keyName = "tektonSafespotTimer",
            description = "Shows timer until Tekton gives up trying to reach his target",
            position = 0,
            section = tekton
    )
    default boolean tektonSafespotTimer() {return true;}

    @ConfigItem(
            name = "Show Interacting Arrow",
            keyName = "tektonInteractingArrow",
            description = "Draws an arrow to who Tekton is targetting",
            position = 1,
            section = tekton
    )
    default boolean tektonInteractingArrow() {return true;}

    @ConfigItem(
            name = "Show Interacting Name",
            keyName = "tektonInteractingName",
            description = "Shows the name Tekton is targetting over him",
            position = 2,
            section = tekton
    )
    default boolean tektonInteractingName() {return true;}

    //------------------------------------------------tekton end--------------------------------------

    @ConfigItem(
            name = "Show Heal Timer",
            keyName = "showVasaTimer",
            description = "Shows timer until Vasa stops healing",
            position = 0,
            section = vasa
    )
    default boolean showVasaTimer() {return true;}

    @Range(
            min = 1,
            max = 100
    )
    @ConfigItem(
            name = "Timer Text Size",
            keyName = "vasaFontSize",
            description = "Size of the timer text",
            position = 1,
            section = vasa
    )
    default int vasaFontSize() {return 14;}

    @Range(
            min = 0,
            max = 600
    )
    @ConfigItem(
            name = "Timer Text Height",
            keyName = "vasaTextHeight",
            description = "Height of the timer text",
            position = 2,
            section = vasa
    )
    default int vasaTextHeight() {return 0;}

    @Alpha
    @ConfigItem(
            position = 25,
            keyName = "vasaColor",
            name = "Timer Text Color",
            description = "Changes color of the heal timer",
            section = vasa
    )
    default Color vasaColor()
    {
        return new Color(0, 255, 255);
    }
}