package net.runelite.client.plugins.mafhamtoa.Palm;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

public class PalmIndicatorsOverlay extends Overlay {

    @Inject
    private PalmIndicators palmIndicators;
    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    public PalmIndicatorsOverlay(PalmIndicators palmIndicators, Client client, MafhamToAConfig config, ModelOutlineRenderer modelOutlineRenderer)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.palmIndicators = palmIndicators;
        this.client = client;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        Iterator<Map.Entry<GraphicsObject, Instant>> iterator = palmIndicators.getAcids().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<GraphicsObject, Instant> entry = iterator.next();
            GraphicsObject acid = entry.getKey();
            Instant acidTime = entry.getValue();

            LocalPoint localPoint = LocalPoint.fromWorld(client, palmIndicators.getPalm().getWorldLocation());
            int distance = Math.abs(localPoint.getY() - acid.getLocation().getY());
            Instant now = Instant.now();

            if (distance > 2600 && now.isAfter(acidTime.plusMillis(1800))) {
                iterator.remove();
            }
            if (distance > 2300 && now.isAfter(acidTime.plusMillis(2400))) {
                iterator.remove();
            }
            if (distance > 1900 && now.isAfter(acidTime.plusMillis(3000))) {
                iterator.remove();
            }
            if (distance > 1400 && now.isAfter(acidTime.plusMillis(3600))) {
                iterator.remove();
            }
        }


        if (!palmIndicators.getSpikes().isEmpty())
        {
            for (Spike spike : palmIndicators.getSpikes())
            {
                GameObject spikeGameObject = spike.getGameObject();
                Color greenColor = new Color(0,255,0,config.statueTileAlpha());
                Color redColor = new Color(255,0,0,config.statueTileAlpha());
                Color color;
                switch (spike.getCycle())
                {
                    case 7: case 6:
                        color = redColor;
                        break;
                    case 5:
                        color = palmIndicators.hasMovedY ? greenColor : redColor;
                        break;
                    default:
                        color = greenColor;
                        break;
                }
                WorldPoint worldPoint = spikeGameObject.getWorldLocation();
                WorldPoint worldPoint2 = worldPoint.dx(0);
                WorldPoint worldPoint3 = worldPoint.dx(1);
                if (config.showStatueTiles())
                {
                    renderTile(worldPoint2, graphics, color);
                    renderTile(worldPoint3, graphics, color);
                }
                if (config.showStatueOutline())
                {
                    Color outlineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.statueOutlineAlpha());
                    modelOutlineRenderer.drawOutline(spikeGameObject, config.statueOutlineWidth(), outlineColor, config.statueOutlineFeather());
                }

            }
        }
        if (!palmIndicators.getAcids().isEmpty())
        {
            if (config.showPalmPoison()) {
                for (GraphicsObject graphicsObject : palmIndicators.getAcids().keySet()) {
                    LocalPoint localPoint = graphicsObject.getLocation();
                    Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        if (poly != null) {
                            Color color = new Color(255, 0, 0, 45);
                            graphics.setColor(color);
                            graphics.drawPolygon(poly);
                            graphics.fillPolygon(poly);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void renderTile(WorldPoint worldPoint, Graphics2D graphics, Color color)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        for (GraphicsObject acid : palmIndicators.getAcids().keySet())
        {
            if (acid.getLocation().equals(localPoint))
            {
                return;
            }
        }
        Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
        if (canvasPoint != null) {
            Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
            if (poly == null)
            {
                return;
            }
            graphics.setColor(color);
            graphics.drawPolygon(poly);
            graphics.fillPolygon(poly);
        }
    }
}