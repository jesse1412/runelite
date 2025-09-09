package net.runelite.client.plugins.mafhamcox.olmcrystals;

import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class OlmCrystalsOverlay extends Overlay {

    private final Client client;
    private final OlmCrystals olmCrystals;
    private final MafhamCoxConfig config;

    @Inject
    public OlmCrystalsOverlay(Client client, OlmCrystals olmCrystals, MafhamCoxConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        this.client = client;
        this.olmCrystals = olmCrystals;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightFallingCrystals())
        {
            return null;
        }
        if (olmCrystals.getCrystalsMapForHighlighting().isEmpty())
        {
            return null;
        }
        for (GraphicsObject graphicsObject : olmCrystals.getCrystalsMapForHighlighting().keySet())
        {
            Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, graphicsObject.getLocation(), 3);
            if (tilePoly == null) {
                continue;
            }
            Color tileColor = new Color(255, 0, 0, 25);
            Color outlineColor = Color.RED;
            graphics.setColor(tileColor);
            graphics.fillPolygon(tilePoly);
            graphics.setColor(outlineColor);
            graphics.drawPolygon(tilePoly);

        }
        return null;
    }

}