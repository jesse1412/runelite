package net.runelite.client.plugins.mafhamtoa.KephriPuzzle;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class KephriPuzzleOverlay extends Overlay {

    private final Client client;
    private final KephriPuzzle kephriPuzzle;

    @Inject
    private MafhamToAConfig config;

    @Inject
    public KephriPuzzleOverlay(Client client, KephriPuzzle kephriPuzzle, MafhamToAConfig config)
    {
        this.client = client;
        this.config = config;
        this.kephriPuzzle = kephriPuzzle;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showPuzzleTiles())
        {
            return null;
        }
        if (kephriPuzzle.getPuzzleTiles().isEmpty())
        {
            return null;
        }
        for (PuzzleTile puzzleTile : kephriPuzzle.getPuzzleTiles())
        {
            LocalPoint localPoint = puzzleTile.getLocalPoint();
            String string = puzzleTile.getName();
            Color color1 = puzzleTile.getColor();
            Color color = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 50);
            if (localPoint != null)
            {
                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                if (canvasPoint != null)
                {
                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    graphics.setColor(color);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.drawPolygon(poly);

                    //Point textLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, string, 0);

                    Rectangle tileB = poly.getBounds();
                    Rectangle txtB = graphics.getFontMetrics().getStringBounds(string, graphics).getBounds();
                    Point p = new Point(tileB.x + tileB.width / 2 - txtB.width / 2, tileB.y + tileB.height / 2 + txtB.height / 2);


                    OverlayUtil.renderTextLocation(graphics, p, string, color);
                }
            }
        }
        return null;
    }
}