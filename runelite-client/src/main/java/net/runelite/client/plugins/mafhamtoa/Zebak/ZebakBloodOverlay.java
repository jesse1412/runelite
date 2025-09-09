package net.runelite.client.plugins.mafhamtoa.Zebak;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class ZebakBloodOverlay extends Overlay {

    private final ZebakBlood plugin;
    private final Client client;
    @Inject
    private MafhamToAConfig config;

    @Inject
    public ZebakBloodOverlay(ZebakBlood plugin, Client client)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.zebakBlood())
        {
            return null;
        }
        if (plugin.getBloodTick() != null)
        {
            String string = plugin.getBloodTick().toString();
            net.runelite.api.Point point = client.getLocalPlayer().getCanvasTextLocation(graphics, string, 420);
            renderTextLocation(graphics, string, 16, 1, Color.ORANGE, point);
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