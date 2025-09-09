package net.runelite.client.plugins.mafhamtoa.Wardens;

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

public class WardensTileFlipOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private WardensTileFlip wardensTileFlip;

    @Inject
    private WardensTileFlipOverlay(Client client, MafhamToAConfig config, WardensTileFlip wardensTileFlip)
    {
        this.client = client;
        this.config = config;
        this.wardensTileFlip = wardensTileFlip;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showWardenTile())
        {
            return null;
        }
        if (wardensTileFlip.getHighlightTile() != null)
        {
            renderTile(wardensTileFlip.getHighlightTile(), graphics, new Color(0,255,0,85));
        }
        return null;
    }

    private void renderTile(WorldPoint worldPoint, Graphics2D graphics, Color color)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        assert localPoint != null;
        Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
        if (canvasPoint != null) {
            Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
            graphics.setColor(color);
            graphics.drawPolygon(poly);
            graphics.fillPolygon(poly);
        }
    }
}