package net.runelite.client.plugins.mafhamcox.vasa;

import net.runelite.api.Client;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.api.Point;

import javax.inject.Inject;
import java.awt.*;

public class VasaOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private Vasa vasa;
    @Inject
    private MafhamCoxConfig config;

    @Inject
    public VasaOverlay(Client client, Vasa vasa, MafhamCoxConfig config)
    {
        this.client = client;
        this.vasa = vasa;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (vasa.getVasaNPC() == null)
        {
            return null;
        }
        if (vasa.getVasaTimer() == null)
        {
            return null;
        }
        Color color = config.vasaColor();
        String timerString = "Heal: " + vasa.getVasaTimer().toString();
        Point canvasPoint = vasa.getVasaNPC().getCanvasTextLocation(graphics, timerString, config.vasaTextHeight());
        if (config.showVasaTimer())
        {
            renderTextLocation(graphics, timerString, config.vasaFontSize(), 4, color, canvasPoint);
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
            final net.runelite.api.Point canvasCenterPoint_shadow = new net.runelite.api.Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

}