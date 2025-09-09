package net.runelite.client.plugins.mafhamtoa.Kephri;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class KephriCounterOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private KephriCounter kephriCounter;

    private final ModelOutlineRenderer outlineRenderer;

    @Inject
    public KephriCounterOverlay(Client client, MafhamToAConfig config, KephriCounter kephriCounter, ModelOutlineRenderer outlineRenderer)
    {
        this.client = client;
        this.config = config;
        this.kephriCounter = kephriCounter;
        this.outlineRenderer = outlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (kephriCounter.getKephri() == null || kephriCounter.getKephriCounter() == null)
        {
            return null;
        }
        if (config.highlightScarabs())
        {
            Color configColor = config.scarabColor();
            int outlineWidth = config.scarabOutlineWidth();
            for (NPC swarm : kephriCounter.getScarabSwarmHealKephri())
            {
                LocalPoint swarmPoint = swarm.getLocalLocation();
                WorldArea kephriArea = kephriCounter.getKephriArea();
                int finalAlpha = 0;
                for (WorldPoint worldPoint : kephriArea.toWorldPointList())
                {
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                    int distance = swarmPoint.distanceTo(localPoint);
                    int alpha;
                    // Linear interpolation between 256 and 768
                    alpha = 255 - ((distance - 256) * (255 - 25)) / (896 - 256);
                    finalAlpha = Math.max(finalAlpha, alpha);
                }
                if (config.scarabGradient())
                {
                    Color gradientColor = new Color(configColor.getRed(), configColor.getGreen(), configColor.getBlue(), finalAlpha);
                    outlineRenderer.drawOutline(swarm, outlineWidth, gradientColor, 2);
                }
                else
                {
                    outlineRenderer.drawOutline(swarm, outlineWidth, configColor, 2);
                }
            }
        }
        if (!config.kephriCounterToggle())
        {
            return null;
        }
        for (Player player : kephriCounter.getDungPlayers())
        {
            outlineRenderer.drawOutline(player, 2, Color.red, 2);
        }
        if (kephriCounter.getKephriCounter() > -1)
        {
            NPC kephriNPC = kephriCounter.getKephri();
            Color color;
            switch (kephriCounter.getKephriCounter())
            {
                case 1:
                    color = Color.orange;
                    break;
                case 0:
                    color = Color.RED;
                    break;
                default:
                    color = Color.green;
                    break;
            }
            String kephriCounterString = kephriCounter.getKephriCounter().toString();
            String kephriSpecString = "Next: " + kephriCounter.getNextSpec();
            Point canvasPoint = kephriNPC.getCanvasTextLocation(graphics, kephriCounterString, 0);
            Point canvasPoint2 = new Point((canvasPoint.getX() - 28), (canvasPoint.getY() - 14));
            renderTextLocation(graphics, kephriCounterString, 14, 4, color, canvasPoint);
            renderTextLocation(graphics, kephriSpecString, 14, 4, color, canvasPoint2);
            if (kephriCounter.getDownedTimer() != null && kephriCounter.getDownedTimer() > -1)
            {
                String kephriDownedTimerString = "Downed: " + kephriCounter.getDownedTimer().toString();
                Point canvasPoint3 = new Point((canvasPoint2.getX() + 5), (canvasPoint2.getY() - 14));
                renderTextLocation(graphics, kephriDownedTimerString, 14, 4, color, canvasPoint3);
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