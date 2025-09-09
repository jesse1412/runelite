package net.runelite.client.plugins.mafhamcolo;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class MafhamColoEquipmentOverlay extends Overlay {

    @Inject
    private MafhamColoPlugin plugin;
    @Inject
    private Client client;
    @Inject
    private MafhamColoConfig config;
    private final int colRegionID = 7216;
    private NpcUtil npcUtil;

    @Inject
    private MafhamColoEquipmentOverlay(MafhamColoPlugin plugin, Client client, NpcUtil npcUtil, MafhamColoConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.npcUtil = npcUtil;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!playerIsInColo())
        {
            return null;
        }
        if (!plugin.getFremenniks().isEmpty())
        {
            for (Fremennik fremennik : plugin.getFremenniks())
            {
                if (npcUtil.isDying(fremennik.npc))
                {
                    continue;
                }
                Color color = Color.white;
                int fontSize = 12;
                if (fremennik.ticksUntilAttack == 1)
                {
                    color = Color.RED;
                    fontSize = 20;
                }
                String attackCount = String.valueOf(fremennik.ticksUntilAttack);
                Point canvasPoint = fremennik.npc.getCanvasTextLocation(graphics, attackCount, 0);
                if (config.fremennikTicks())
                {
                    renderTextLocation(graphics, attackCount, fontSize, 4, color, canvasPoint, true);
                }
            }
        }

        if (plugin.getEquipmentSlot() != null)
        {
            if (client.getWidget(InterfaceID.EQUIPMENT, 0) != null)
            {
                Color color = Color.CYAN;
                if (plugin.getEquipmentSlot() == EquipmentSlot.BODY)
                {
                    Widget widget = client.getWidget(InterfaceID.EQUIPMENT, 19);
                    renderWidgetOverlay(widget, graphics, color);
                }
                if (plugin.getEquipmentSlot() == EquipmentSlot.BACK)
                {
                    Widget widget = client.getWidget(InterfaceID.EQUIPMENT, 16);
                    renderWidgetOverlay(widget, graphics, color);
                }
                if (plugin.getEquipmentSlot() == EquipmentSlot.FEET)
                {
                    Widget widget = client.getWidget(InterfaceID.EQUIPMENT, 23);
                    renderWidgetOverlay(widget, graphics, color);
                }
                if (plugin.getEquipmentSlot() == EquipmentSlot.HANDS)
                {
                    Widget widget = client.getWidget(InterfaceID.EQUIPMENT, 22);
                    renderWidgetOverlay(widget, graphics, color);
                }
                if (plugin.getEquipmentSlot() == EquipmentSlot.LEGS)
                {
                    Widget widget = client.getWidget(InterfaceID.EQUIPMENT, 21);
                    renderWidgetOverlay(widget, graphics, color);
                }
            }
        }
        return null;
    }

    private void renderWidgetOverlay(Widget widget, Graphics2D graphics, Color color)
    {
        int widgetX = widget.getCanvasLocation().getX();
        int widgetY = widget.getCanvasLocation().getY();
        int width = widget.getWidth();
        int height = widget.getHeight();
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(4));
        graphics.drawRect(widgetX, widgetY, width, height);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        graphics.fillRect(widgetX, widgetY, width, height);
    }

    private boolean playerIsInColo()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (colRegionID == mapRegion)
            {
                return true;
            }
        }
        return false;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, net.runelite.api.Point canvasPoint, Boolean shadow)
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
            if (shadow)
            {
                OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            }
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }
}