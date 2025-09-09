package net.runelite.client.plugins.mafhammuspah;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class MafhamMuspahPieOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private MafhamMuspahPlugin plugin;
    @Inject
    private MafhamMuspahConfig config;

    @Inject
    public MafhamMuspahPieOverlay(Client client, MafhamMuspahPlugin plugin, MafhamMuspahConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getMuspahNPC() == null)
        {
            return null;
        }

        LocalPoint localPoint = plugin.getMuspahNPC().getLocalLocation();
        if (localPoint == null) {
            return null;
        }

        net.runelite.api.Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(),config.pieChartHeight());
        if (canvasPoint == null)
        {
            return null;
        }

        double progress = plugin.getProgress();
        Color c = progress == 0 ? Color.green : Color.cyan;
        if (progress < 0)
        {
            return null;
        }
        if (!config.showMeleeTiming())
        {
            return null;
        }
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(canvasPoint);
        pie.setProgress(1 - progress);
        pie.setBorderColor(c);
        pie.setFill(c);
        return pie.render(graphics);
    }
}
