package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class AkkhaDetonateOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private AkkhaDetonate akkhaDetonate;

    @Inject
    private AkkhaDetonateOverlay(Client client, AkkhaDetonate akkhaDetonate, MafhamToAConfig config)
    {
        this.client = client;
        this.akkhaDetonate = akkhaDetonate;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.detonateToggle())
        {
            return null;
        }
        if (!akkhaDetonate.getTileHighlights().isEmpty())
        {
            for (WorldPoint worldPoint : akkhaDetonate.getTileHighlights())
            {
                renderTile(worldPoint, graphics, config.getAkkhaOrbColor());
            }
        }
        return null;
    }

    private void renderTile(WorldPoint worldPoint, Graphics2D graphics, Color color)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
        {
            return;
        }
        Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
        if (canvasPoint != null) {
            Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
            graphics.setColor(color);
            graphics.drawPolygon(poly);
            graphics.fillPolygon(poly);
        }
    }
}