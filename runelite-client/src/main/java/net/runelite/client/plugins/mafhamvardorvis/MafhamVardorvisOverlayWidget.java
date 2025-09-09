package net.runelite.client.plugins.mafhamvardorvis;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MafhamVardorvisOverlayWidget extends Overlay {

    private final Client client;
    @Inject
    private MafhamVardorvisPlugin plugin;
    @Inject
    private MafhamVardorvisConfig config;

    @Inject
    public MafhamVardorvisOverlayWidget(Client client, MafhamVardorvisPlugin plugin, MafhamVardorvisConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.highlightOsu()) {
            return null;
        }
        for (int i = 6; i <= 25; i++) {
            Widget widget = client.getWidget(833, i);
            {
                if (widget != null && widget.getAnimationId() != 10375 && !widget.isHidden())
                {
                    Color color = Color.green;
                    int widgetX = widget.getCanvasLocation().getX();
                    int widgetY = widget.getCanvasLocation().getY();
                    int widgetWidth = widget.getWidth();
                    int widgetHeight = widget.getHeight();
                    graphics.setColor(new Color(0, 255, 0, 55));
                    graphics.fillRect(widgetX, widgetY, widgetWidth, widgetHeight);
                    graphics.setColor(color);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.drawRect(widgetX, widgetY, widgetWidth, widgetHeight);
                }
            }
        }
        return null;
    }
}