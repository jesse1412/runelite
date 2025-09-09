package net.runelite.client.plugins.mafhammoons;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

public class MafhamMoonsOverlay extends Overlay {

    @Inject
    private MafhamMoonsPlugin plugin;
    @Inject
    private Client client;

    private static final Polygon ARROW_HEAD = new Polygon(
            new int[]{0, -6, 6},
            new int[]{0, -10, -10},
            3
    );

    @Inject
    private MafhamMoonsOverlay(MafhamMoonsPlugin plugin, Client client)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInMoons())
        {
            return null;
        }
        final int blueMoonId = 13013;
        final int bloodMoonId = 13011;
        final int eclipseMoonId = 13012;
        final int floorSymbolId = 13015;
        final int frozenWeaponsNPCID = 13025;
        final int frozenWeaponsAnimID = 11031;
        final int frozenWeaponsAnimID2 = 11030;
        final List<Integer> Ids = List.of(bloodMoonId, blueMoonId, eclipseMoonId);
        boolean floorSymbolSpawned = false;

        List<WorldPoint> tornadoTilesFinal = new ArrayList<>();
        if (!plugin.getTornadoTiles().isEmpty())
        {
            for (Map.Entry<WorldPoint, WorldPoint> entry : plugin.getTornadoTiles().entrySet())
            {
                WorldPoint worldPoint1 = entry.getKey();
                WorldPoint worldPoint2 = entry.getValue();
                Collections.addAll(tornadoTilesFinal, worldPoint1, worldPoint2);

                LocalPoint fl = LocalPoint.fromWorld(client, worldPoint1);
                LocalPoint tl = LocalPoint.fromWorld(client, worldPoint2);
                if (fl == null)
                {
                    return null;
                }
                net.runelite.api.Point fs = Perspective.localToCanvas(client, fl, client.getPlane(),client.getPlane());
                if (fs == null)
                {
                    return null;
                }
                int fsx = fs.getX();
                int fsy = fs.getY();

                if (tl == null)
                {
                    return null;
                }
                Point ts = Perspective.localToCanvas(client, tl, client.getPlane(), client.getPlane());
                if (ts == null)
                {
                    return null;
                }
                int tsx = ts.getX();
                int tsy = ts.getY();
                graphics.setColor(Color.GREEN);
                graphics.setStroke(new BasicStroke(1));
                graphics.drawLine(fsx, fsy, tsx, tsy);

                AffineTransform t = new AffineTransform();
                t.translate(tsx, tsy);
                t.rotate(tsx - fsx, tsy - fsy);
                t.rotate(Math.PI / -2);
                AffineTransform ot = graphics.getTransform();
                graphics.setTransform(t);
                graphics.fill(ARROW_HEAD);
                graphics.setTransform(ot);
            }
        }
        if (!tornadoTilesFinal.isEmpty())
        {
            for (WorldPoint worldPoint : tornadoTilesFinal)
            {
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (localPoint != null)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                    if (canvasPoint != null)
                    {
                        Color color = new Color(255, 0, 0, 85);
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        graphics.setColor(color);
                        graphics.drawPolygon(poly);
                        graphics.fillPolygon(poly);
                    }
                }
            }
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc.getId() == floorSymbolId)
            {
                floorSymbolSpawned = true;
            }
            if (npc.getId() == frozenWeaponsNPCID)
            {
                if (npc.getAnimation() == frozenWeaponsAnimID || npc.getAnimation() == frozenWeaponsAnimID2)
                {
                    LocalPoint localPoint = npc.getLocalLocation();
                    if (localPoint != null)
                    {
                        Color color = Color.GREEN;
                        Shape clickbox = Perspective.getClickbox(client, npc.getModel(), npc.getCurrentOrientation(), localPoint.getX(), localPoint.getY(),
                                Perspective.getTileHeight(client, localPoint, npc.getWorldLocation().getPlane()));
                        renderClickbox(graphics, clickbox, client.getMouseCanvasPosition(), color, color, 255, 20, color, true);
                    }
                }
            }
        }

        for (NPC npc : client.getNpcs())
        {
            if (Ids.contains(npc.getId()))
            {
                if (floorSymbolSpawned)
                {
                    LocalPoint localPoint = npc.getLocalLocation();
                    if (localPoint != null)
                    {
                        Color color = Color.CYAN;
                        Shape clickbox = Perspective.getClickbox(client, npc.getModel(), npc.getCurrentOrientation(), localPoint.getX(), localPoint.getY(),
                                Perspective.getTileHeight(client, localPoint, npc.getWorldLocation().getPlane()));
                        renderClickbox(graphics, clickbox, client.getMouseCanvasPosition(), color, color, 255, 20, color, true);
                    }
                }

            }
        }

        if (!plugin.getBloods().isEmpty())
        {
            for (GameObject blood : plugin.getBloods().keySet())
            {
                Color color = new Color(0, 255, 0, 75);
                WorldPoint worldPoint = blood.getWorldLocation();
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (localPoint != null)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        if (poly != null) {
                            graphics.setColor(color);
                            //graphics.drawPolygon(poly);
                            graphics.fillPolygon(poly);
                        }

                    }
                }
            }
        }
        if (!plugin.getClones().isEmpty())
        {
            for (NPC clone : plugin.getClones().keySet())
            {
                Color color = new Color(0, 255, 0, 75);
                LocalPoint localPoint = clone.getLocalLocation();
                if (localPoint != null)
                {
                    //int size = clone.getComposition().getSize();
                    int size = 3;
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, size);
                    renderPoly(graphics, color, color, 0, 75, tilePoly, 2, true);
                }
            }
        }
        if (plugin.getSpecTimer() != null && plugin.getSpecTimer() > -1)
        {
            Player player = client.getLocalPlayer();
            if (player != null)
            {
                Point point = player.getCanvasTextLocation(graphics, String.valueOf(plugin.getSpecTimer()), player.getLogicalHeight() + 60);
                Color color = Color.cyan;
                if (point != null)
                {
                    renderTextLocation(graphics, String.valueOf(plugin.getSpecTimer()), 14, 4, color, point);
                }
            }
        }

        if (plugin.getChosenJaguar() != null)
        {
            NPC npc = plugin.getChosenJaguar();
            LocalPoint localPoint = npc.getLocalLocation();
            if (localPoint != null) {
                Color color = Color.GREEN;
                Shape clickbox = Perspective.getClickbox(client, npc.getModel(), npc.getCurrentOrientation(), localPoint.getX(), localPoint.getY(),
                        Perspective.getTileHeight(client, localPoint, npc.getWorldLocation().getPlane()));
                renderClickbox(graphics, clickbox, client.getMouseCanvasPosition(), color, color, 255, 40, color, true);
            }
        }

        return null;
    }

    public static void renderClickbox(Graphics2D graphics, Shape area, Point mousePosition, Color line, Color fill, int lineAlpha, int fillAlpha, Color hovered, boolean antiAlias)
    {
        if (area != null)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            if (area.contains(mousePosition.getX(), mousePosition.getY()))
            {
                graphics.setColor(new Color(hovered.getRed(), hovered.getGreen(), hovered.getBlue(), lineAlpha));
            }
            else
            {
                graphics.setColor(new Color(line.getRed(), line.getGreen(), line.getBlue(), lineAlpha));
            }
            graphics.draw(area);
            graphics.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), fillAlpha));
            graphics.fill(area);
        }
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
}