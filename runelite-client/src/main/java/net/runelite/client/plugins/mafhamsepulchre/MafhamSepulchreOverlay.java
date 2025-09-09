package net.runelite.client.plugins.mafhamsepulchre;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class MafhamSepulchreOverlay extends Overlay {

    @Inject
    private Client client;

    @Inject
    private MafhamSepulchrePlugin plugin;

    @Inject
    private MafhamSepulchreOverlay(Client client, MafhamSepulchrePlugin plugin)
    {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInSepulchre())
        {
            return null;
        }
        if (plugin.getFlameTimer() != null)
        {
            String string = plugin.getFlameTimer().toString();
            net.runelite.api.Point point = client.getLocalPlayer().getCanvasTextLocation(graphics, string, 200);
            int goTick = (plugin.isFloor5() ? 3 : 4);
            Color color = (plugin.getFlameTimer() == goTick ? Color.green : Color.orange);
            renderTextLocation(graphics, string, 16, 1, color, point);
        }
        if (!plugin.getCrossbowTiles().isEmpty())
        {
            for (LocalPoint localPoint : plugin.getCrossbowTiles())
            {
                if (localPoint != null)
                {
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                    if (tilePoly != null)
                    {
                        Color color = new Color(255, 0 ,0, 55);
                        graphics.setColor(color);
                        graphics.fillPolygon(tilePoly);
                    }
                }
            }
        }
        if (!plugin.getTeleportTiles().isEmpty())
        {
            for (FloorTile floorTile : plugin.getTeleportTiles())
            {
                if (floorTile.type == FloorTile.Type.BLUE)
                {
                    LocalPoint localPoint = floorTile.localPoint;
                    if (localPoint != null)
                    {
                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                        if (tilePoly != null)
                        {
                            Color color = new Color(0, 255 ,0, 100);
                            graphics.setColor(color);
                            graphics.fillPolygon(tilePoly);
                        }
                    }
                }
                if (floorTile.type == FloorTile.Type.YELLOW)
                {
                    LocalPoint localPoint = floorTile.localPoint;
                    if (localPoint != null)
                    {
                        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                        if (tilePoly != null)
                        {
                            Color color = new Color(255, 0 ,0, 100);
                            graphics.setColor(color);
                            graphics.fillPolygon(tilePoly);
                        }
                    }
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
}