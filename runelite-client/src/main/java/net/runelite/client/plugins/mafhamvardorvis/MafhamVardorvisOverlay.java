package net.runelite.client.plugins.mafhamvardorvis;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collection;

public class MafhamVardorvisOverlay extends Overlay {

    @Inject
    private Client client;
    private MafhamVardorvisPlugin plugin;
    private MafhamVardorvisConfig config;

    @Inject
    private MafhamVardorvisOverlay(Client client, MafhamVardorvisPlugin plugin, MafhamVardorvisConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getLocalPlayer() == null) {
            return null;
        }
        if (config.showAxePaths())
        {
            for (WorldPoint worldPoint : plugin.getTiles())
            {
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (localPoint == null)
                {
                    return null;
                }
                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                Color configcolor = new Color(255,0,0,75);
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    graphics.setColor(configcolor);
                    graphics.fillPolygon(poly);
                }
            }
        }

        if ((!plugin.getCornerAxes().isEmpty() || plugin.isRunThrough()) && plugin.getPillar() != null && config.showCornerTiming())
        {
            Color greenColor = new Color(0,255,0,85);
            Color yellowColor = new Color(255,255,0,85);
            Color color = plugin.isRunThrough() ? greenColor : yellowColor;
            WorldPoint worldPoint = plugin.getPillar().getWorldLocation().dy(8);
            LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
            if (localPoint != null)
            {
                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    graphics.setColor(color);
                    graphics.fillPolygon(poly);
                }
            }
        }
        if (config.showCornerTiming() && plugin.getPillar() != null)
        {
            //close corner tile timing
            if (plugin.getCloseCornerAxeSpawnTick() != null)
            {
                Color greenColor = new Color(0,255,0,85);
                Color yellowColor = new Color(255,255,0,85);
                int axeLifetime = client.getTickCount() - plugin.getCloseCornerAxeSpawnTick();
                WorldPoint worldPoint = plugin.getPillar().getWorldLocation().dx(2).dy(10);
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (localPoint != null)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                    if (canvasPoint != null)
                    {
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        if (axeLifetime < 3)
                        {
                            graphics.setColor(yellowColor);
                            graphics.fillPolygon(poly);
                        }
                        if (axeLifetime == 3)
                        {
                            graphics.setColor(greenColor);
                            graphics.fillPolygon(poly);
                        }
                    }
                }
            }
        }
        return null;
    }
}