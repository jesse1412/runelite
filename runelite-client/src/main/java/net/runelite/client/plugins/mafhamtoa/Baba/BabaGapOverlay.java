package net.runelite.client.plugins.mafhamtoa.Baba;

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

public class BabaGapOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private BabaCounter babaCounter;

    @Inject
    private BabaGapOverlay(Client client, MafhamToAConfig config, BabaCounter babaCounter)
    {
        this.client = client;
        this.config = config;
        this.babaCounter = babaCounter;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showBabaGap())
        {
            return null;
        }
        if (!babaCounter.isMindTheGap())
        {
            return null;
        }
        if (babaCounter.getGapTiles().isEmpty())
        {
            return null;
        }
        for (WorldPoint worldPoint : babaCounter.getGapTiles())
        {
            renderTile(worldPoint, graphics, new Color(255,0,0,85));
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
            //graphics.drawPolygon(poly);
            graphics.fillPolygon(poly);
        }
    }

}