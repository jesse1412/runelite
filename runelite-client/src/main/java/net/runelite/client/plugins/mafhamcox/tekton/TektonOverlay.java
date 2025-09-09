package net.runelite.client.plugins.mafhamcox.tekton;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.stream.Stream;

public class TektonOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private Tekton tekton;
    @Inject
    private MafhamCoxConfig config;
    private static final int INTERACTING_SHIFT = -16;
    private static final Polygon ARROW_HEAD = new Polygon(
            new int[]{0, -3, 3},
            new int[]{0, -5, -5},
            3
    );

    @Inject
    public TektonOverlay(Client client, Tekton tekton, MafhamCoxConfig config)
    {
        this.client = client;
        this.tekton = tekton;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (tekton.getTekton() == null)
        {
            return null;
        }
        if (tekton.getSeekingCounter() != null && tekton.getSeekingCounter() > -1 && tekton.getTekton() != null && tekton.getTarget() != null)
        {
            Color color = Color.LIGHT_GRAY;
            if (tekton.getTarget() == client.getLocalPlayer())
            {
                color = Color.CYAN;
            }
            String timerString = tekton.getSeekingCounter().toString();
            String targetString = tekton.getTarget().getName();
            Point canvasPoint = tekton.getTekton().getCanvasTextLocation(graphics, timerString, 0);
            Point canvasPoint2 = new Point((canvasPoint.getX() - 16), (canvasPoint.getY() - 16));
            if (config.tektonSafespotTimer())
            {
                renderTextLocation(graphics, timerString, 14, 4, color, canvasPoint);
            }
            if (config.tektonInteractingName())
            {
                renderTextLocation(graphics, targetString, 14, 4, color, canvasPoint2);
            }
            if (config.tektonInteractingArrow())
            {
                renderInteracting(graphics, tekton.getTekton(), color);
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

    private void renderInteracting(Graphics2D graphics, Actor fa, Color INTERACTING_COLOR)
    {
        Actor ta = fa.getInteracting();
        if (ta == null)
        {
            return;
        }

        LocalPoint fl = fa.getLocalLocation();
        Point fs = Perspective.localToCanvas(client, fl, client.getPlane(), fa.getLogicalHeight() / 2);
        if (fs == null)
        {
            return;
        }
        int fsx = fs.getX();
        int fsy = fs.getY() - INTERACTING_SHIFT;

        LocalPoint tl = ta.getLocalLocation();
        Point ts = Perspective.localToCanvas(client, tl, client.getPlane(), ta.getLogicalHeight() / 2);
        if (ts == null)
        {
            return;
        }
        int tsx = ts.getX();
        int tsy = ts.getY() - INTERACTING_SHIFT;

        graphics.setColor(INTERACTING_COLOR);
        graphics.drawLine(fsx, fsy, tsx, tsy);

        AffineTransform t = new AffineTransform();
        t.translate(tsx, tsy);
        t.rotate(tsx - fsx, tsy - fsy);
        t.rotate(Math.PI / -2);
        AffineTransform ot = graphics.getTransform();
        graphics.setTransform(t);
        graphics.fill(ARROW_HEAD);
        graphics.setTransform(ot);
    }
}