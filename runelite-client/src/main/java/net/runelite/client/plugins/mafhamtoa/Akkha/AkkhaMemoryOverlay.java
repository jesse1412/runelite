package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

public class AkkhaMemoryOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;

    @Inject
    public AkkhaMemoryOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }
    public ArrayList<LocalPoint> greenTiles = new ArrayList<LocalPoint>();
    public ArrayList<LocalPoint> yellowTiles = new ArrayList<LocalPoint>();
    private final PanelComponent panelComponent = new PanelComponent();


    @Override
    public Dimension render(Graphics2D graphics) {
        if (greenTiles == null)
        {
            return null;
        }
            for (LocalPoint localPoint : greenTiles) {
                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                Color configcolor = new Color(0, 255, 0,85);
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    graphics.setColor(configcolor);
                    graphics.drawPolygon(poly);
                    graphics.fillPolygon(poly);
                }
            }
        for (LocalPoint Testlocalpoint : yellowTiles) {
            Point canvasPoint = Perspective.localToCanvas(client, Testlocalpoint, client.getPlane());
            Color configcolor = new Color(255, 255, 0,85);
            if (canvasPoint != null) {
                Polygon poly = Perspective.getCanvasTilePoly(client, Testlocalpoint);
                graphics.setColor(configcolor);
                graphics.drawPolygon(poly);
                graphics.fillPolygon(poly);
            }
        }
        return panelComponent.render(graphics);
    }
}