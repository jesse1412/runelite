package net.runelite.client.plugins.mafhammuspah;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class MafhamMuspahOverlay extends Overlay {

    @Inject
    private MafhamMuspahPlugin plugin;
    @Inject
    private Client client;
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;
    @Inject
    private MafhamMuspahConfig config;

    @Inject
    private MafhamMuspahOverlay(MafhamMuspahPlugin plugin, Client client, ModelOutlineRenderer modelOutlineRenderer, MafhamMuspahConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getCloudPhaseStartTime() != null && config.cloudPhaseTimer())
        {
            int cloudStart = plugin.getCloudPhaseStartTime();
            int cloudLength = 20;
            int timer = (cloudStart + cloudLength) - client.getTickCount();
            if (timer > -1)
            {
                String string = String.valueOf(timer);
                graphics.setFont(new Font("arial", Font.BOLD, 14));
                Point point = client.getLocalPlayer().getCanvasTextLocation(graphics, string, client.getLocalPlayer().getLogicalHeight() - 15);
                OverlayUtil.renderTextLocation(graphics, point, string, Color.cyan);
            }
        }
        if (plugin.getBossHP() != null && config.showRemainingHP())
        {
            int bossHP = plugin.getBossHP();
            int remainingHP;
            switch (plugin.getBossFightState())
            {
                case 0:
                    remainingHP = bossHP - 630;
                    break;
                case 1:
                    remainingHP = bossHP - 420;
                    break;
                case 2:
                    remainingHP = bossHP - 127;
                    break;
                default:
                    remainingHP = bossHP;
            }
            String string = "Spec: " + remainingHP;
            net.runelite.api.Point canvasPoint = plugin.getMuspahNPC().getCanvasTextLocation(graphics, string, 75);
            renderTextLocation(graphics, string, 14, 4, Color.GREEN, canvasPoint);
        }
        if (!plugin.getCloudPoints().isEmpty())
        {
            if (config.showCloudTiles())
            {
                for (WorldPoint worldPoint : plugin.getCloudPoints())
                {
                    if (worldPoint != null)
                    {
                        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                        if (localPoint != null)
                        {
                            Color borderColor = new Color(0,0,0,0);
                            Color color = new Color(255, 0, 0, 64);
                            Polygon poly = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                            renderPoly(graphics, borderColor, color, poly);
                        }
                    }
                }
            }
        }
        if (plugin.getMuspahNPC() == null)
        {
            return null;
        }
        NPC npc = plugin.getMuspahNPC();
        if (plugin.getSpikeCounter() != null && plugin.getSpikeCounter() > -1)
        {
            for (GraphicsObject graphicsObject : client.getGraphicsObjects())
            {
                if (graphicsObject.getId() == 2326) //spike
                {
                    String string = plugin.getSpikeCounter().toString();
                    LocalPoint localPoint = graphicsObject.getLocation();
                    Point canvasPoint = Perspective.getCanvasTextLocation(client, graphics, localPoint, string, 0);
                    if (config.showSpikeTimer())
                    {
                        renderTextLocation(graphics, string, 12, 4, Color.CYAN, canvasPoint);
                    }
                }
            }
        }
        if (npc.getId() == 12077 || npc.getId() == 12078 || npc.getId() == 12082) //range/melee/teleporting
        {
            String string = plugin.getTotalDamage().toString();
            net.runelite.api.Point canvasPoint = npc.getCanvasTextLocation(graphics, string, 0);
            if (config.showDamageCounter())
            {
                renderTextLocation(graphics, string, 14, 4, Color.CYAN, canvasPoint);
            }
        }
        if (plugin.getTotalDamage() > 79 && npc.getId() == 12078 && config.showPhaseSwitchOverlay())
        {
            Color color = Color.green;
            modelOutlineRenderer.drawOutline(npc, 1, color, 2);
        }
        if (plugin.getTotalDamage() > 99 && npc.getId() == 12077 && config.showPhaseSwitchOverlay())
        {
            Color color = Color.RED;
            modelOutlineRenderer.drawOutline(npc, 1, color, 2);
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

    private void renderPoly(Graphics2D graphics, Color borderColor, Color fillColor, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke((float) 1.5));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }
}