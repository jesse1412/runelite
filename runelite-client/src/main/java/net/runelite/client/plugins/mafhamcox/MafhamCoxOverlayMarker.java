package net.runelite.client.plugins.mafhamcox;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MafhamCoxOverlayMarker extends Overlay {

    private final Client client;
    private int widgetX = 428;
    private int widgetY = 329;
    private final int coxWidgetParentID = 499;
    private final int coxWidgetChildID = 58;
    private static final WorldArea coxWorldArea = new WorldArea(1240, 3553, 8, 12, 0);
    @Inject
    MafhamCoxPlugin plugin;
    @Inject
    MafhamCoxConfig config;

    @Inject
    public MafhamCoxOverlayMarker(Client client, MafhamCoxPlugin plugin, MafhamCoxConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        setMovable(true);
        setResizable(true);
        setMinimumSize(16);
        setResettable(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client.getWidget(coxWidgetParentID,coxWidgetChildID) != null)
        {
            Widget widget = (client.getWidget(coxWidgetParentID,coxWidgetChildID));
            widgetX = widget.getCanvasLocation().getX();
            widgetY = widget.getCanvasLocation().getY();
        }
        if (plugin.isCoxLoaded() && inCoxArea() && config.showMakeParty()) {
            graphics.setColor(config.markerColor());
            graphics.fillRect(widgetX, widgetY, 100, 25);
            graphics.setColor(new Color(config.markerColor().getRed(), config.markerColor().getGreen(), config.markerColor().getBlue(), 255));
            graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
            graphics.drawRect(widgetX, widgetY, 100, 25);
        }
        return null;
    }

    private boolean inCoxArea()
    {
        return client.getLocalPlayer().getWorldLocation().distanceTo(coxWorldArea) == 0;
    }

}