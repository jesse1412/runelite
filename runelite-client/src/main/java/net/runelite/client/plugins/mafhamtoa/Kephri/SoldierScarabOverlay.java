package net.runelite.client.plugins.mafhamtoa.Kephri;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class SoldierScarabOverlay extends Overlay {

    private final Client client;
    private final SoldierScarab soldierScarab;

    @Inject
    private MafhamToAConfig config;

    @Inject
    public SoldierScarabOverlay(Client client, SoldierScarab soldierScarab, MafhamToAConfig config)
    {
        this.client = client;
        this.config = config;
        this.soldierScarab = soldierScarab;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!config.showSoldierScarabTimer())
        {
            return null;
        }
        if (soldierScarab.getNpc() == null)
        {
            return null;
        }
        if (soldierScarab.getAttackCounter() == null)
        {
            return null;
        }
        String string = soldierScarab.getAttackCounter().toString();
        Point canvasPoint = soldierScarab.getNpc().getCanvasTextLocation(graphics, string, 200);
        if (canvasPoint != null)
        {
            renderTextLocation(graphics, string, 22, 4, Color.CYAN, canvasPoint);
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