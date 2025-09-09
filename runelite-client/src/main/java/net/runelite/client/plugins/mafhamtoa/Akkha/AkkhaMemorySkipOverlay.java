package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class AkkhaMemorySkipOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private AkkhaMemorySkip akkhaMemorySkip;
    @Inject
    private MafhamToAConfig config;
    private final ModelOutlineRenderer outlineRenderer;

    @Inject
    private AkkhaMemorySkipOverlay(Client client, AkkhaMemorySkip akkhaMemorySkip, MafhamToAConfig config, ModelOutlineRenderer outlineRenderer)
    {
        this.client = client;
        this.akkhaMemorySkip = akkhaMemorySkip;
        this.config = config;
        this.outlineRenderer = outlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.memorySetting() == MafhamToAConfig.MemorySetting.Off)
        {
            return null;
        }
        if (!akkhaMemorySkip.getPlayerHighlights().isEmpty())
        {
            for (Player player : akkhaMemorySkip.getPlayerHighlights())
            {
                outlineRenderer.drawOutline(player, 2, Color.green, 2);
            }
        }
        if (!akkhaMemorySkip.getGreenHighlights().isEmpty())
        {
            for (WorldPoint worldPoint : akkhaMemorySkip.getGreenHighlights())
            {
                renderTile(worldPoint, graphics, new Color(0, 255, 0,85));
            }
        }
        if (!akkhaMemorySkip.getYellowHighlights().isEmpty())
        {
            for (WorldPoint worldPoint : akkhaMemorySkip.getYellowHighlights())
            {
                renderTile(worldPoint, graphics, new Color(255, 255, 0,85));
            }
        }
        if (!akkhaMemorySkip.getOrangeHighlights().isEmpty())
        {
            for (WorldPoint worldPoint : akkhaMemorySkip.getOrangeHighlights())
            {
                renderTile(worldPoint, graphics, new Color(255,165,0,85));
            }
        }
        return null;
    }

    private void renderTile(WorldPoint worldPoint, Graphics2D graphics, Color color)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        assert localPoint != null;
        Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
        if (canvasPoint != null) {
            Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
            graphics.setColor(color);
            graphics.drawPolygon(poly);
            graphics.fillPolygon(poly);
        }
    }
}