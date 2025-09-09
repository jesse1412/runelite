package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class AkkhaTickCounterOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private AkkhaTickCounter akkhaTickCounter;

    @Inject
    public AkkhaTickCounterOverlay(Client client, AkkhaTickCounter akkhaTickCounter)
    {
        this.client = client;
        this.akkhaTickCounter = akkhaTickCounter;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.akkhaTimerToggle())
        {
            return null;
        }
        if (akkhaTickCounter.getAkkhaNPC() != null && akkhaTickCounter.getAkkhaTimer() > -1)
        {
            NPC akkhaNPC = akkhaTickCounter.getAkkhaNPC();
            Point canvasPoint = akkhaNPC.getCanvasTextLocation(graphics, String.valueOf(akkhaTickCounter.getAkkhaTimer()), 400);
            int fontsize = config.specialTimerTextSize();
            renderTextLocation(graphics, String.valueOf(akkhaTickCounter.getAkkhaTimer()), fontsize, 4, Color.cyan, canvasPoint);
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