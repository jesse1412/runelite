package net.runelite.client.plugins.mafhamtoa.Baba;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class BabaCounterOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private BabaCounter babaCounter;

    @Inject
    private BabaCounterOverlay(Client client, MafhamToAConfig config, BabaCounter babaCounter)
    {
        this.client = client;
        this.config = config;
        this.babaCounter = babaCounter;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showBabaTimer())
        {
            return null;
        }
        if (babaCounter.getBabaBoss() == null || babaCounter.getBabaTimer() == null)
        {
            return null;
        }

        String babaTimer = babaCounter.getBabaTimer().toString();
        Point canvasPoint = babaCounter.getBabaBoss().getCanvasTextLocation(graphics, babaTimer, config.babaTextHeight());
        Point piePoint = Perspective.localToCanvas(client, babaCounter.getBabaBoss().getLocalLocation(), client.getPlane(), config.babaTextHeight());
        Color color = babaCounter.getBabaTimer() == 1 ? config.babaTimingTextColor() : config.babaTextColor();
        if (config.babaSetting() == MafhamToAConfig.BabaSetting.TickCount)
        {
            renderTextLocation(graphics, babaTimer, config.babaFontSize(), 4, color, canvasPoint);
        }
        if (config.babaSetting() == MafhamToAConfig.BabaSetting.Pie)
        {
            if (babaCounter.getBabaTimer() <= 4) {
                drawPie(piePoint, graphics);
            }
        }
        return null;
    }

    private void drawPie(Point canvasPoint, Graphics2D graphics2D) {
        double progress = babaCounter.getPieProgress();
        if (progress < 0)
        {
            return;
        }
        ProgressPieComponent pie = new ProgressPieComponent();
        Color color = progress == 0 ? config.babaTimingTextColor() : config.babaTextColor();
        pie.setPosition(canvasPoint);
        pie.setBorderColor(color);
        pie.setFill(color);
        pie.setProgress(1 - progress);

        pie.render(graphics2D);
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