package net.runelite.client.plugins.mafhamtoa.Mirror;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class MirrorRoomOverlay extends Overlay {
    private final Client client;
    private final MirrorRoom plugin;
    @Inject
    private MafhamToAConfig config;

    @Inject
    public MirrorRoomOverlay(MirrorRoom plugin, Client client)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (LocalPoint localPoint : plugin.mirrorTileHighlights) {
            Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
            Color configcolor = config.getMirrorColor();
            if (canvasPoint != null) {
                Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                graphics.setColor(configcolor);
                graphics.drawPolygon(poly);
                graphics.setColor(configcolor);
                graphics.fillPolygon(poly);
            }
        }
        for (LocalPoint localPoint : plugin.yellowHighlights) {
            Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
            if (canvasPoint != null) {
                Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                Color configcolor = config.getMirrorYellowColor();
                graphics.setColor(configcolor);
                graphics.drawPolygon(poly);
                graphics.fillPolygon(poly);
            }
        }
        for (GameObject gameObject : plugin.wallHighlights) {
            if (gameObject != null) {
                Shape shape = gameObject.getConvexHull();
                Color configcolor = config.getWallColor();
                graphics.setColor(configcolor);
                graphics.fill(shape);
                graphics.setColor(new Color(configcolor.getRed(),configcolor.getGreen(),configcolor.getBlue(), 255));
                graphics.draw(shape);
            }
        }
        for (GameObject gameObject : plugin.mirrorHighlights) {
                if (gameObject != null) {
                Shape shape = gameObject.getConvexHull();
                Color configcolor = config.getMirrorColor();
                graphics.setColor(new Color(configcolor.getRed(),configcolor.getGreen(),configcolor.getBlue(),85));
                graphics.fill(shape);
            }
        }
        for (GameObject gameObject : plugin.dirtyMirrors) {
            if (gameObject != null) {
                Shape shape = gameObject.getConvexHull();
                Color configcolor = new Color(111, 78, 55,215);
                graphics.setColor(new Color(configcolor.getRed(),configcolor.getGreen(),configcolor.getBlue(),configcolor.getAlpha()));
                graphics.fill(shape);
            }
        }
        return null;
    }

}