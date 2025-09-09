package net.runelite.client.plugins.mafhamtob.Maiden;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class MaidenOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private Maiden maiden;

    @Inject
    private MaidenOverlay(Client client, MafhamToBConfig config, Maiden maiden)
    {
        this.client = client;
        this.config = config;
        this.maiden = maiden;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.maidenTimer())
        {
            return  null;
        }
        if (maiden.getMaiden() == null || maiden.getMaidenCounter() == null)
        {
            return null;
        }
        net.runelite.api.Point piePoint = Perspective.localToCanvas(client, maiden.getMaiden().getLocalLocation(), client.getPlane(), 200);
        if (piePoint != null)
        {
            drawPie(piePoint, graphics);
        }
        return null;
    }

    private void drawPie(Point canvasPoint, Graphics2D graphics2D) {
        int timer = maiden.getMaidenCounter();
        int denominator = maiden.getMaidenAttackSpeed();
        double progress = (double) timer / denominator;
        if (progress < 0)
        {
            return;
        }
        ProgressPieComponent pie = new ProgressPieComponent();
        Color color = progress == 0 ? Color.green : Color.cyan;
        pie.setPosition(canvasPoint);
        pie.setBorderColor(color);
        pie.setFill(color);
        pie.setProgress(1 - progress);

        pie.render(graphics2D);
    }

}