package net.runelite.client.plugins.mafhamtob.Bloat;

import net.runelite.api.Client;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class BloatOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private Bloat bloat;

    @Inject
    public BloatOverlay(Client client, MafhamToBConfig config, Bloat bloat)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (bloat.getBloat() == null)
        {
            return null;
        }
        if (config.bloatHighlight())
        {
            Color color = config.bloatUpColor();
            if (bloat.isBloatDown())
            {
                color = config.bloatDownColor();
            }
            LocalPoint localPoint = LocalPoint.fromWorld(client, bloat.getBloat().getWorldLocation());
            NPCComposition npcComposition = bloat.getBloat().getTransformedComposition();
            Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 25);
            if (localPoint != null && npcComposition != null)
            {
                int size = npcComposition.getSize();
                LocalPoint lp = new LocalPoint(localPoint.getX() + size * 128 / 2 - 64, localPoint.getY() + size * 128 / 2 - 64);
                Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, size);
                renderPoly(graphics, color, fillColor, poly);
            }
        }
        if (bloat.getDownCounter() == null)
        {
            return null;
        }
        if (config.stompArea() && !bloat.getLosHighlights().isEmpty())
        {
            for (WorldPoint worldPoint : bloat.getLosHighlights()) {
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    Color configcolor = new Color(255, 0 ,0, 115);
                    graphics.setColor(configcolor);
                    graphics.fillPolygon(poly);
                }
            }
        }
        if (!config.bloatTimer())
        {
            return null;
        }
        if (bloat.getDownCounter() < 0)
        {
            return null;
        }
        Color color = Color.cyan;
        String bloatString = bloat.getDownCounter().toString();
        Point canvasPoint = bloat.getBloat().getCanvasTextLocation(graphics, bloatString, 0);
        renderTextLocation(graphics, bloatString, 14, 4, color, canvasPoint);
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