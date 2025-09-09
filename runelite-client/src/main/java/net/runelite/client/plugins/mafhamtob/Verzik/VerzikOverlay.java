package net.runelite.client.plugins.mafhamtob.Verzik;

import java.awt.*;
import java.text.DecimalFormat;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

public class VerzikOverlay extends Overlay
{
    @Inject
    private Verzik verzik;
    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private NpcUtil npcUtil;

    @Inject
    protected VerzikOverlay(Verzik verzik, Client client, MafhamToBConfig config, SpriteManager spriteManager, NpcUtil npcUtil)
    {
        this.verzik = verzik;
        this.client = client;
        this.config = config;
        this.spriteManager = spriteManager;
        this.npcUtil = npcUtil;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (verzik.isVerzikActive())
        {
            if (verzik.getGreenBall() != null && config.verzikGreenBall() != MafhamToBConfig.VERZIK_GREEN_BALL_STYLE.OFF)
            {
                Projectile projectile = verzik.getGreenBall();
                int ticksLeft = projectile.getRemainingCycles() / 30;
                String tickString = String.valueOf(ticksLeft);
                LocalPoint tile = null;
                LocalPoint trueTile = null;
                if (projectile.getInteracting() != null)
                {
                    tile = projectile.getInteracting().getLocalLocation();
                    WorldPoint trueWP = projectile.getInteracting().getWorldLocation();
                    if (trueWP != null)
                    {
                        trueTile = LocalPoint.fromWorld(client, trueWP);
                    }
                }
                if (trueTile != null && config.verzikGreenBall() == MafhamToBConfig.VERZIK_GREEN_BALL_STYLE.TRUE)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, trueTile, client.getPlane());
                    Color configcolor = new Color(0, 255, 0, 255);
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, trueTile);
                        graphics.setColor(configcolor);
                        graphics.drawPolygon(poly);
                        graphics.setColor(configcolor);
                        //graphics.fillPolygon(poly);
                    }
                    Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, trueTile, tickString, 0);
                    OverlayUtil.renderTextLocation(graphics, canvasTextLocation, tickString, Color.GREEN);
                }
                if (tile != null && config.verzikGreenBall() == MafhamToBConfig.VERZIK_GREEN_BALL_STYLE.TILE)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, tile, client.getPlane());
                    Color configcolor = new Color(0, 255, 0, 255);
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, tile);
                        graphics.setColor(configcolor);
                        graphics.drawPolygon(poly);
                        graphics.setColor(configcolor);
                        //graphics.fillPolygon(poly);
                    }
                    Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, tile, tickString, 0);
                    OverlayUtil.renderTextLocation(graphics, canvasTextLocation, tickString, Color.GREEN);
                }
            }
            String tick_text = "";
            int tick_int = 0;
            if (verzik.getVerzikSpecial() != Verzik.SpecialAttack.WEBS)
            {
                tick_text += verzik.getVerzikTicksUntilAttack() - 1;
                tick_int += verzik.getVerzikTicksUntilAttack() - 1;
            }
            if (verzik.getVerzikPhase() == Verzik.Phase.PHASE1 && verzik.isVerzikFirstAttackDone())
            {
                if (tick_int < 12)
                {
                    tick_text = String.valueOf(verzik.getVerzikTicksUntilAttack() + 1);
                }
                if (tick_int == 12 || tick_int == 13)
                {
                    tick_text = String.valueOf(verzik.getVerzikTicksUntilAttack() - 13);
                }
            }
            Point canvasPoint = verzik.getVerzikNPC().getCanvasTextLocation(graphics, tick_text, 60);
            net.runelite.api.Point piePoint = Perspective.localToCanvas(client, verzik.getVerzikNPC().getLocalLocation(), client.getPlane(), 60);

            if (canvasPoint != null)
            {
                Color col = verzik.verzikSpecialWarningColor().getColor();
                if (config.verzikAutosTick() == MafhamToBConfig.VERZIK_TIMER_STYLE.NUMBER || (verzik.getVerzikPhase() == Verzik.Phase.PHASE1 && (config.verzikAutosTick() == MafhamToBConfig.VERZIK_TIMER_STYLE.NUMBER || config.verzikAutosTick() == MafhamToBConfig.VERZIK_TIMER_STYLE.PIE)))
                {
                    String string = verzik.verzikSpecialWarningColor().getText();
                    renderTextLocation(graphics, tick_text, 12, Font.BOLD, col, canvasPoint);
                    if (!string.isEmpty())
                    {
                        net.runelite.api.Point textPoint = new Point(canvasPoint.getX(), canvasPoint.getY() + 14);
                        renderTextLocation(graphics, string, 12, Font.BOLD, col, textPoint);
                    }
                }
                if (config.verzikAutosTick() == MafhamToBConfig.VERZIK_TIMER_STYLE.PIE && verzik.getVerzikPhase() != Verzik.Phase.PHASE1)
                {
                    String string = verzik.verzikSpecialWarningColor().getText();
                    int numerator = tick_int;
                    numerator = numerator - 1;
                    int denominator = 4;
                    if (verzik.getVerzikPhase() == Verzik.Phase.PHASE2)
                    {
                        denominator = 3;
                    }
                    if (numerator > -1 && numerator < 4)
                    {
                        drawPie(piePoint, graphics, numerator, denominator);
                    }
                    if (!string.isEmpty())
                    {
                        net.runelite.api.Point textPoint = new Point(canvasPoint.getX(), canvasPoint.getY() + 14);
                        renderTextLocation(graphics, string, 12, Font.BOLD, col, textPoint);
                    }
                }
            }

            if (verzik.getVerzikPhase() == Verzik.Phase.PHASE3)
            {
                if (config.verzikTornado() && (!config.verzikPersonalTornadoOnly() || (config.verzikPersonalTornadoOnly() && verzik.getVerzikLocalTornado() != null)))
                {
                    verzik.getVerzikTornadoes().forEach(k ->
                    {
                        if (k.getCurrentPosition() != null)
                        {
                            drawTile(graphics, k.getCurrentPosition(), config.verzikTornadoColor(), 1, 120);
                        }

                        if (k.getLastPosition() != null)
                        {
                            //drawTile(graphics, k.getLastPosition(), config.verzikTornadoColor(), 2, 180);
                        }
                    });
                }
            }
            if (verzik.getVerzikPhase() == Verzik.Phase.PHASE2 || verzik.getVerzikPhase() == Verzik.Phase.PHASE3)
            {
                if (config.verzikNyloPersonalWarning() || config.verzikNyloOtherWarning())
                {
                    verzik.getVerzikAggros().forEach(k ->
                    {
                        if (k.getAnimation() != 8000 && !k.isDead()) // blowing up animation
                        {
                            if ((k.getInteracting() != null && config.verzikNyloPersonalWarning() && k.getInteracting() == client.getLocalPlayer())
                                    || (config.verzikNyloOtherWarning() && k.getInteracting() != client.getLocalPlayer()))
                            {
                                Color color = Color.LIGHT_GRAY;
                                if (k.getInteracting() == client.getLocalPlayer())
                                {
                                    color = Color.YELLOW;
                                }
                                if (k.getInteracting() == null)
                                {
                                    color = new Color(255, 114, 118, 255);
                                }
                                if (k.getInteracting() != null)
                                {
                                    Point textLocation = k.getCanvasTextLocation(graphics, k.getInteracting().getName(), 80);
                                    if (textLocation != null)
                                    {
                                        OverlayUtil.renderTextLocation(graphics, textLocation, k.getInteracting().getName(), color);
                                    }
                                }

                                if (config.verzikNyloExplodeAOE())
                                {
                                    int size = 1;
                                    int size2 = 1;
                                    int thick_size = 1;
                                    final NPCComposition composition = k.getComposition();

                                    if (composition != null)
                                    {
                                        size = composition.getSize() + 2 * thick_size;
                                        size2 = composition.getSize();
                                    }

                                    LocalPoint lp = LocalPoint.fromWorld(client, k.getWorldLocation());
                                    if (lp != null)
                                    {
                                        lp = new LocalPoint(lp.getX() + 64, lp.getY() + 64);
                                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
                                        Polygon tilePoly2 = Perspective.getCanvasTileAreaPoly(client, lp, size2);
                                        Color fillColor = new Color(0,0,0,0);
                                        if (tilePoly != null)
                                        {
                                            if (config.verzikNyloHighlightStyle() == MafhamToBConfig.VERZIK_NYLO_HIGHLIGHT_STYLE.OUTER_BOX || config.verzikNyloHighlightStyle() == MafhamToBConfig.VERZIK_NYLO_HIGHLIGHT_STYLE.BOTH)
                                            {
                                                renderPoly(graphics, color, fillColor,255, 0, tilePoly, 1, true);
                                            }
                                            if (config.verzikNyloHighlightStyle() == MafhamToBConfig.VERZIK_NYLO_HIGHLIGHT_STYLE.INNER_BOX || config.verzikNyloHighlightStyle() == MafhamToBConfig.VERZIK_NYLO_HIGHLIGHT_STYLE.BOTH)
                                            {
                                                renderPoly(graphics, color, fillColor,255, 0, tilePoly2, 1, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1) ;
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha)
    {
        WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32)
        {
            return;
        }
        LocalPoint lp = LocalPoint.fromWorld(this.client, point);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
        if (poly == null)
        {
            return;
        }
        //OverlayUtil.renderPolygon(graphics, poly, color);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        graphics.fill(poly);
    }

    private void renderPoly(Graphics2D graphics, Color outlineColor, Color fillColor, int lineAlpha, int fillAlpha, Shape polygon, double width, boolean antiAlias)
    {
        if (polygon != null)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), lineAlpha));
            graphics.setStroke(new BasicStroke((float) width));
            graphics.draw(polygon);
            graphics.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillAlpha));
            graphics.fill(polygon);
        }
    }

    private void drawPie(Point canvasPoint, Graphics2D graphics2D, int timer, int denominator) {
        double progress = (double) timer / denominator;
        if (progress < 0)
        {
            return;
        }
        ProgressPieComponent pie = new ProgressPieComponent();
        Color color = progress == 0 ? Color.green : Color.cyan;
        pie.setPosition(canvasPoint);
        pie.setBorderColor(color);
        pie.setFill(color);
        pie.setProgress(1 - progress);

        pie.render(graphics2D);
    }
}
