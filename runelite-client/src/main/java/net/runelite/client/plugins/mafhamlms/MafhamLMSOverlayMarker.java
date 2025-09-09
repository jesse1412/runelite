package net.runelite.client.plugins.mafhamlms;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class MafhamLMSOverlayMarker extends Overlay {

    @Inject
    private MafhamLMSPlugin plugin;
    @Inject
    private MafhamLMSConfig config;
    private final Client client;
    private final int ancientsParentID = 218;
    private final int iceBarrageChildID = 82;
    private final int bloodBarrageChildID = 86;

    @Inject
    public MafhamLMSOverlayMarker(Client client, MafhamLMSPlugin plugin, MafhamLMSConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGH);
        setResettable(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!inLMSGame())
        {
            return null;
        }

        if (config.dontClickThis())
        {
            if (client.getWidget(ancientsParentID, 3) != null)
            {
                Widget widget2 = client.getWidget(ancientsParentID, 3);
                int x = widget2.getWidth();
                widget2.setSize(x, 256);
                widget2.revalidate();
                for (int i = 70; i <= 104; i++)
                {
                    Widget widget = client.getWidget(ancientsParentID, i);
                    if (widget != null)
                    {
                        if (!widget.getName().contains("Blood Barrage") && !widget.getName().contains("Ice Barrage"))
                        {
                            widget.setHidden(true);
                            widget.revalidate();
                        }
                    }
                }
            }
        }


        if (client.getWidget(ancientsParentID, bloodBarrageChildID) != null)
        {
            Widget widget = client.getWidget(ancientsParentID, bloodBarrageChildID);
            if (config.dontClickThis())
            {
                widget.setOriginalX(100);
                widget.setOriginalY(122);
                widget.setSize(60,60);
                widget.revalidate();
            }
            if (config.highlightSpells())
            {
                renderWidgetOverlay(widget, graphics, Color.RED);
            }
        }
        if (client.getWidget(ancientsParentID, iceBarrageChildID) != null)
        {
            Widget widget = client.getWidget(ancientsParentID, iceBarrageChildID);
            if (config.dontClickThis())
            {
                widget.setOriginalX(0);
                widget.setOriginalY(150);
                widget.setSize(60,60);
                widget.revalidate();
            }
            if (config.highlightSpells())
            {
                renderWidgetOverlay(widget, graphics, Color.CYAN);
            }
        }
        return null;
    }

    private boolean inLMSGame()
    {
        return client.getWidget(328, 5) != null;
    }

    private void renderWidgetOverlay(Widget widget, Graphics2D graphics, Color color)
    {
        int widgetX = widget.getCanvasLocation().getX();
        int widgetY = widget.getCanvasLocation().getY();
        int width = widget.getWidth();
        int height = widget.getHeight();
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(1));
        graphics.drawRect(widgetX, widgetY, width, height);
    }
}