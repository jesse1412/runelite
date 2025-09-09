package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class AkkhaEnrageLineOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private AkkhaEnrageLine akkhaEnrageLine;

    private static final Polygon ARROW_HEAD = new Polygon(
            new int[]{0, -6, 6},
            new int[]{0, -10, -10},
            3
    );

    @Inject
    private AkkhaEnrageLineOverlay(Client client, AkkhaEnrageLine akkhaEnrageLine, MafhamToAConfig config)
    {
        this.client = client;
        this.config = config;
        this.akkhaEnrageLine = akkhaEnrageLine;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.drawLineToNextAkkha())
        {
            return null;
        }
        if (akkhaEnrageLine.getAkkhaPoint() != null)
        {
            int currentTick = client.getTickCount();
            int spawnTick = akkhaEnrageLine.getSpawnTick();
            if (currentTick < spawnTick + 3)
            {
                LocalPoint fl = client.getLocalPlayer().getLocalLocation();
                LocalPoint tl = akkhaEnrageLine.getAkkhaPoint();
                if (fl == null)
                {
                    return null;
                }
                net.runelite.api.Point fs = Perspective.localToCanvas(client, fl, client.getPlane(),client.getPlane());
                if (fs == null)
                {
                    return null;
                }
                int fsx = fs.getX();
                int fsy = fs.getY();

                if (tl == null)
                {
                    return null;
                }
                Point ts = Perspective.localToCanvas(client, tl, client.getPlane(), client.getPlane());
                if (ts == null)
                {
                    return null;
                }
                int tsx = ts.getX();
                int tsy = ts.getY();
                graphics.setColor(Color.GREEN);
                graphics.setStroke(new BasicStroke(1));
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
        return null;
    }
}