package net.runelite.client.plugins.mafhamcox;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamcox.olmcounter.Olm;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MafhamCoxOverlay extends Overlay {
    private final Client client;
    private final ModelOutlineRenderer modelOutlineRenderer;
    private static final List<Integer> CHEST_IDS = Arrays.asList(29770, 29779, 29780, 37978, 29769);
    private static final int PASSAGE_ID = 29789;
    private final int ENERGY_WELL_ID = 30066;
    @Inject
    private MafhamCoxPlugin plugin;
    @Inject
    private MafhamCoxConfig config;
    @Inject
    private Olm olm;

    @Inject
    public MafhamCoxOverlay(Client client, MafhamCoxPlugin plugin, MafhamCoxConfig config, ModelOutlineRenderer modelOutlineRenderer, Olm olm)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.olm = olm;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        //VANGUARD SPAWN
        if (plugin.getMeleeVanguardSpawn() != null)
        {
            WorldPoint worldPoint = plugin.getMeleeVanguardSpawn();
            LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, 19);
            graphics.setColor(Color.RED);
            graphics.drawPolygon(tilePoly);
        }
        //VESPULA PORTAL
        if (plugin.VESPULA_OBJECT != null && plugin.isVespulaStarted()) {
            if (plugin.isHighlightVespula() && config.showVespulaHelper()) {
                Color color = config.portalColor();
                int alpha = config.portalHullAlpha();
                if (config.vespulaHull()) {
                    renderObjectHull(graphics, plugin.VESPULA_OBJECT, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                }
                if (config.vespulaOutline()) {
                    renderObjectOutline(graphics, plugin.VESPULA_OBJECT, color);
                }
            }
            // REGEN COUNTER
            if (config.showRegenTicks() && plugin.getEnhRegenTimer() > -1) {
                Point canvasPoint = plugin.VESPULA_OBJECT.getCanvasTextLocation(graphics, String.valueOf(plugin.getEnhRegenTimer()), 230);
                renderTextLocation(graphics, String.valueOf(plugin.getEnhRegenTimer()), 24, 4, config.regenTicksTextColor(), canvasPoint);
            }
        }
        //GAME OBJECTS
        if (plugin.inRaid())
        {
            for (GroundObject groundObject : plugin.getGroundObjectHighlights().keySet())
            {
                if (groundObject.getPlane() == client.getPlane())
                {
                    if (ENERGY_WELL_ID == groundObject.getId())
                    {
                        Color color = config.energyWellColor();
                        int alpha = 80;
                        if (config.energyWellHull())
                        {
                            renderGroundObjectHull(graphics, groundObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.energyWellOutline())
                        {
                            renderGroundObjectOutline(graphics, groundObject, color);
                        }
                        if (config.energyWellTile())
                        {
                            LocalPoint localPoint = groundObject.getLocalLocation();
                            final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                            if (poly != null) {
                                graphics.setStroke(new BasicStroke(1));
                                graphics.setColor(color);
                                graphics.drawPolygon(poly);
                            }
                        }
                    }
                }
            }
            for (GameObject gameObject : plugin.getObjectHighlights().keySet()) {
                if (gameObject.getPlane() == client.getPlane()) {
                    if (CHEST_IDS.contains(gameObject.getId()))
                    {
                        Color color = config.chestsColor();
                        int alpha = 80;
                        if (config.chestsHull())
                        {
                            renderObjectHull(graphics, gameObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.chestsOutline())
                        {
                            renderObjectOutline(graphics, gameObject, color);
                        }
                    }
                    if (PASSAGE_ID == gameObject.getId())
                    {
                        Color color = config.passagesColor();
                        int alpha = 80;
                        if (config.passagesHull())
                        {
                            renderObjectHull(graphics, gameObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.passagesOutline())
                        {
                            renderObjectOutline(graphics, gameObject, color);
                        }
                    }
                    if (plugin.getBOULDER_ID() == gameObject.getId())
                    {
                        Color color = config.boulderColor();
                        int alpha = 80;
                        if (config.shortcutsHull())
                        {
                            renderObjectHull(graphics, gameObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.shortcutsOutline())
                        {
                            renderObjectOutline(graphics, gameObject, color);
                        }
                    }
                    if (plugin.getTREE_SHORTCUT_ID() == gameObject.getId() || plugin.getTREE_SHORTCUT_DONE_ID() == gameObject.getId())
                    {
                        Color color = config.woodcuttingColor();
                        int alpha = 80;
                        if (config.shortcutsHull())
                        {
                            renderObjectHull(graphics, gameObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.shortcutsOutline())
                        {
                            renderObjectOutline(graphics, gameObject, color);
                        }
                    }
                    if (plugin.getMINING_ID() == gameObject.getId() || plugin.getMINING_DONE_ID() == gameObject.getId())
                    {
                        Color color = config.miningColor();
                        int alpha = 80;
                        if (config.shortcutsHull())
                        {
                            renderObjectHull(graphics, gameObject, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                        }
                        if (config.shortcutsOutline())
                        {
                            renderObjectOutline(graphics, gameObject, color);
                        }
                    }
                }
            }
            //MUTTADILE
            if (plugin.getMEAT_TREE_NPC() != null && config.showMeatTree())
            {
                Point canvasPoint = plugin.getMEAT_TREE_NPC().getCanvasTextLocation(graphics, String.valueOf(plugin.getTreeCounter()), 130);
                int size = config.treeTextSize();
                if (plugin.getTreeCounter() == 0 || plugin.getTreeCounter() > 1)
                {
                    renderTextLocation(graphics, String.valueOf(plugin.getTreeCounter()), size, 4, config.treeColor(), canvasPoint);
                }
                if (plugin.getTreeCounter() == 1)
                {
                    renderTextLocation(graphics, String.valueOf(plugin.getTreeCounter()), size, 4, config.tick1Color(), canvasPoint);
                }
            }
            //VESP TILES
            if (plugin.getVespTileHighlights() != null) {
                for (LocalPoint localPoint : plugin.getVespTileHighlights()) {
                    Color outlineColor = config.vespTileColor();
                    final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    if (poly != null) {
                        graphics.setStroke(new BasicStroke(1));
                        graphics.setColor(outlineColor);
                        graphics.drawPolygon(poly);
                    }
                }
            }
            //CRABS
            if (!plugin.getCrabTileHighlights().isEmpty() && config.isCrabsEnabled()) {
                int distanceToOrb = plugin.getORB_SHOOTER_OBJECT().getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation());
                int currentPlane = client.getPlane();
                int orbPlane = plugin.getORB_SHOOTER_OBJECT().getPlane();
                if (distanceToOrb < 32 && currentPlane == orbPlane) {
                    for (LocalPoint localPoint : plugin.getCrabTileHighlights()) {
                        if (localPoint != null) {
                            Color outlineColor = config.crabsTileColor();
                            final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                            if (poly != null) {
                                graphics.setStroke(new BasicStroke(1));
                                graphics.setColor(outlineColor);
                                graphics.drawPolygon(poly);
                            }
                        }
                    }
                }
            }
            if (!plugin.getCrabNpcHighlights().isEmpty()) {
                for (NPC npc : plugin.getCrabNpcHighlights()) {
                    Color color;
                    switch (npc.getId()) {
                        default: //7576:
                            color = Color.WHITE;
                            break;
                        case 7577:
                            color = Color.RED;
                            break;
                        case 7578:
                            color = Color.GREEN;
                            break;
                        case 7579:
                            color = Color.BLUE;
                    }

                    if (config.crabsHull()) {
                        int alpha = config.crabsHullAlpha();
                        renderNPCHull(graphics, npc, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                    }

                    if (config.crabsOutline()) {
                        renderNPCOutline(graphics, npc, color);
                    }
                }
            }
            //OLM
            if (olm.crippled)
            {
                int tick = olm.crippleTicks;
                final String tickStr = String.valueOf(tick);
                Point canvasPoint = olm.hand.getCanvasTextLocation(graphics, tickStr, 50);
                renderTextLocation(graphics, tickStr, config.textSize(), config.fontStyle().getFont(), Color.GRAY, canvasPoint);
            }
            if (olm.active)
            {
                GameObject head = olm.head;
                if (head != null) {
                    final int tick = olm.ticksUntilNextAttack;
                    final int cycle = olm.attackCycle;
                    final int nextspec = olm.specialCycle;
                    String cycleStr = "?";
                    switch (cycle) {
                        case 1:
                            cycleStr = "2";
                            break;
                        case 4:
                            cycleStr = "1";
                            break;
                        case 3:
                            cycleStr = "4";
                            break;
                        case 2:
                            cycleStr = "3";
                            break;
                        case -1:
                            cycleStr = "??";
                            break;
                    }
                    String nextcycleStr = "?";
                    switch (cycle) {
                        case 1:
                            nextcycleStr = "3";
                            break;
                        case 4:
                            nextcycleStr = "2";
                            break;
                        case 3:
                            nextcycleStr = "1";
                            break;
                        case 2:
                            nextcycleStr = "4";
                            break;
                        case -1:
                            nextcycleStr = "??";
                            break;
                    }
                    String nextspecStr = "?";
                    switch (nextspec) {
                        case 1:
                            nextspecStr = "Crystals";
                            break;
                        case 2:
                            nextspecStr = "Lightning";
                            break;
                        case 3:
                            nextspecStr = "Portals";
                            break;
                        case 4:
                            if (olm.finalPhase) {
                                nextspecStr = "Heal";
                            } else nextspecStr = "Crystals";
                            break;

                    }
//						final String combinedStr = this.olm.getTicksUntilNextAttack() >= 1 ? cycleStr + ": " +  tickStr : "??:?"; //old string from xkylee
                    String attackStr;
                    if (olm.ticksUntilNextAttack <1)
                    {
                        attackStr = "?";
                    }
                    else if (config.showCurrentLabel())
                    {
                        attackStr = "Curr: " + cycleStr;
                    }
                    else if (!config.showCurrentLabel())
                    {
                        attackStr = cycleStr;
                    }
                    else
                    {
                        attackStr = "?";
                    }


                    String tickStr = String.valueOf(tick); //old string
                    if (olm.ticksUntilNextAttack <1)
                    {
                        tickStr = "?";
                    }
                    else if (config.showTickLabel())
                    {
                        tickStr ="Tick: " + tick;
                    }
                    else if (!config.showTickLabel())
                    {
                        tickStr = String.valueOf(tick);
                    }

                    String nextattackStr;
                    if (olm.ticksUntilNextAttack <1)
                    {
                        nextattackStr = "?";
                    }
                    else if (config.showNextLabel())
                    {
                        nextattackStr = "Next: " + nextcycleStr;
                    }
                    else if (!config.showNextLabel())
                    {
                        nextattackStr = nextcycleStr;
                    }
                    else
                    {
                        nextattackStr = "?";
                    }

//						final String attackStr = this.olm.getTicksUntilNextAttack() >= 1 ? cycleStr : "?"; //old string for before config
                    final String specialStr = olm.ticksUntilNextAttack >= 1 ? nextspecStr : "?";
//						final String nextattackStr = this.olm.getTicksUntilNextAttack() >= 1 ? nextcycleStr : "?"; //old string for before config
                    Point canvasPoint = head.getCanvasTextLocation(graphics, attackStr, 130);
                    Point canvasPoint2 = head.getCanvasTextLocation(graphics, specialStr, 230);
                    Point canvasPoint3 = head.getCanvasTextLocation(graphics, tickStr, -70);
                    Point canvasPoint4 = head.getCanvasTextLocation(graphics, nextattackStr, 30);
                    Color color;
                    if (cycle == 1) color = config.olmSpecialColor();
                    else color = config.olmRegularColor();
                    if (config.showCurrentAttack()) {
                        renderTextLocation(graphics, attackStr, config.textSize(), config.fontStyle().getFont(), color, canvasPoint);
                    }
                    if (config.showNextSpecial()) {
                        renderTextLocation(graphics, specialStr, config.textSize(), config.fontStyle().getFont(), color, canvasPoint2);
                    }
                    if (config.showTickCycle()) {
                        renderTextLocation(graphics, tickStr, config.textSize(), config.fontStyle().getFont(), color, canvasPoint3);
                    }
                    if (config.showNextAttack()) {
                        renderTextLocation(graphics, nextattackStr, config.textSize(), config.fontStyle().getFont(), color, canvasPoint4);
                    }
                }
                //INTERPHASE TIMER
                Player player = client.getLocalPlayer();
                if (player != null && olm.interPhase && config.showInterPhaseTimer())
                {
                    Point point = player.getCanvasTextLocation(graphics, String.valueOf(olm.interPhaseTimer), player.getLogicalHeight() + 60);
                    Color color = config.interPhaseTimerColor();
                    if (point != null)
                    {
                        renderTextLocation(graphics, String.valueOf(olm.interPhaseTimer), config.interPhaseTimerSize(), 4, color, point);
                        //OverlayUtil.renderTextLocation(graphics, point, String.valueOf(olm.interPhaseTimer), color);
                    }
                }
            }
            //CRYSTAL BOMBS
            if (!plugin.getCrystalBombs().isEmpty() && config.showBombs())
            {
                for (GameObject gameObject : plugin.getCrystalBombs())
                {
                    Color color = config.crystalBombsColor();
                    int alpha = config.crystalBombsAlpha();
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, gameObject.getLocalLocation(),7);
                    graphics.setStroke(new BasicStroke(1));
                    graphics.setColor(color);
                    graphics.drawPolygon(tilePoly);
                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                    graphics.fillPolygon(tilePoly);
                }
            }
        }
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, net.runelite.api.Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final net.runelite.api.Point canvasCenterPoint = new net.runelite.api.Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final net.runelite.api.Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    private void renderObjectOutline(Graphics2D graphics, GameObject gameObject, Color color) {
            Shape shape = gameObject.getConvexHull();
            if (shape != null) {
                graphics.setColor(color);
                graphics.draw(shape);
            }
    }
    private void renderObjectHull(Graphics2D graphics, GameObject gameObject, Color color) {
            Shape shape = gameObject.getConvexHull();
            if (shape != null) {
                graphics.setColor(color);
                graphics.fill(shape);
            }
    }

    private void renderNPCOutline(Graphics2D graphics, NPC npc, Color color) {
        Shape shape = npc.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.draw(shape);
        }
    }

    private void renderNPCHull(Graphics2D graphics, NPC npc, Color color) {
        Shape shape = npc.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.fill(shape);
        }
    }

    private void renderGroundObjectOutline(Graphics2D graphics, GroundObject groundObject, Color color) {
        Shape shape = groundObject.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.draw(shape);
        }
    }
    private void renderGroundObjectHull(Graphics2D graphics, GroundObject groundObject, Color color) {
        Shape shape = groundObject.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.fill(shape);
        }
    }
}