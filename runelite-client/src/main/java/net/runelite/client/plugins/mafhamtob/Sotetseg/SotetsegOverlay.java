package net.runelite.client.plugins.mafhamtob.Sotetseg;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;
import java.util.Set;

public class SotetsegOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private Sotetseg sotetseg;
    @Inject
    private MafhamToBConfig config;

    @Inject
    private SotetsegOverlay(Client client, Sotetseg sotetseg, MafhamToBConfig config)
    {
        this.sotetseg = sotetseg;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (sotetseg.getSotetsegNPC() != null && sotetseg.getSotetsegAttackCounter() != null && config.mageAttackCounter())
        {
            String string = String.valueOf(sotetseg.getSotetsegAttackCounter());
            Point canvasPoint = sotetseg.getSotetsegNPC().getCanvasTextLocation(graphics, string, 200);
            if (canvasPoint != null)
            {
                OverlayUtil.renderTextLocation(graphics, canvasPoint, string, Color.green);
            }
        }
        if (config.SotetsegMaze1())
        {
            int i = 1;
            for (GroundObject o : sotetseg.getRedTiles().keySet())
            {
                Polygon poly = o.getCanvasTilePoly();
                if (poly != null)
                {
                    graphics.setColor(Color.WHITE);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.draw(poly);
                }
                Point textLocation = o.getCanvasTextLocation(graphics, String.valueOf(i), 0);
                if (textLocation != null)
                {
                    OverlayUtil.renderTextLocation(graphics, textLocation, String.valueOf(i), Color.WHITE);
                }

                i++;
            }
        }

        if (config.SotetsegHMMaze())
        {
            if (!sotetseg.getHMHiddenTiles().isEmpty())
            {
                for (WorldPoint p : sotetseg.getHMHiddenTiles())
                {
                    drawTile(graphics, p, Color.WHITE, 2, 255, 10);
                }
            }
        }

        if (config.SotetsegMaze2())
        {
            for (WorldPoint p : sotetseg.getRedTilesOverworld())
            {
                drawTile(graphics, p, Color.WHITE, 2, 255, 10);
            }
        }
        if (!sotetseg.getBigBalls().isEmpty() && config.bigBall())
        {
            for (Projectile projectile : sotetseg.getBigBalls())
            {
                int ticksLeft = projectile.getRemainingCycles() / 30;
                String tickString = String.valueOf(ticksLeft);
                LocalPoint lp = null;
                if (projectile.getInteracting() != null)
                {
                    lp = projectile.getInteracting().getLocalLocation();
                }
                if (lp != null)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, lp, client.getPlane());
                    Color configcolor = new Color(255, 0, 0, 255);
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
                        graphics.setColor(configcolor);
                        graphics.drawPolygon(poly);
                        graphics.setColor(configcolor);
                        //graphics.fillPolygon(poly);
                    }
                    Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, tickString, 0);
                    if (config.bigBallTimer())
                    {
                        OverlayUtil.renderTextLocation(graphics, canvasTextLocation, tickString, Color.CYAN);
                    }
                }
            }
        }
        for (Map.Entry<Projectile, LocalPoint> entry : sotetseg.getProjectileHighlights().entrySet())
        {
            int ticksLeft = entry.getKey().getRemainingCycles() / 30;
            String tickString = String.valueOf(ticksLeft);
            LocalPoint lp = entry.getValue();
            if (lp != null)
            {
                Point canvasPoint = Perspective.localToCanvas(client, lp, client.getPlane());
                Color configcolor = new Color(255, 0, 0, 255);
                if (entry.getKey().getId() == 1606)
                {
                    configcolor = Color.CYAN;
                }
                if (entry.getKey().getId() == 1607)
                {
                    configcolor = Color.GREEN;
                }
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, lp);
                    graphics.setColor(configcolor);
                    if (config.highlightOrbs())
                    {
                        graphics.drawPolygon(poly);
                    }
                    graphics.setColor(configcolor);
                    //graphics.fillPolygon(poly);
                }
                if (config.orbTimer())
                {
                    Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, tickString, 0);
                    OverlayUtil.renderTextLocation(graphics, canvasTextLocation, tickString, Color.CYAN);
                }
            }
        }
        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32) {
            return;
        }
        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return;
        }
        //OverlayUtil.renderPolygon(graphics, poly, color);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }
}