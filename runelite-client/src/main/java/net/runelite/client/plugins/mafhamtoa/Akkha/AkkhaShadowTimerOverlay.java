package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class AkkhaShadowTimerOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private AkkhaShadowTimer akkhaShadowTimer;

    @Inject
    private AkkhaShadowTimerOverlay(Client client, MafhamToAConfig config, AkkhaShadowTimer akkhaShadowTimer)
    {
        this.client = client;
        this.config = config;
        this.akkhaShadowTimer = akkhaShadowTimer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showShadowTimer())
        {
            return null;
        }
        if (akkhaShadowTimer.getTimer() != null && !akkhaShadowTimer.getShadowNPCs().isEmpty())
        {
            for (NPC npc : akkhaShadowTimer.getShadowNPCs())
            {
                Color color;
                if (akkhaShadowTimer.getTimer() < 85)
                {
                    color = Color.green;
                }
                else color = Color.RED;
                //it goes from 0 to 100 and decrements by 2% every tick pretty much
                Double myDouble = (100 - akkhaShadowTimer.getTimer()) / 2;
                DecimalFormat df = new DecimalFormat("0");
                String result = df.format(myDouble);
                Point canvasPoint = npc.getCanvasTextLocation(graphics, result, 0);
                int fontsize = config.shadowTimerTextSize();
                renderTextLocation(graphics, result, fontsize, 4, color, canvasPoint);
            }
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