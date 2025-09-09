package net.runelite.client.plugins.mafhamgauntlet;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

public class MafhamGauntletSceneOverlay extends Overlay {

    private final Client client;
    private final MafhamGauntletPlugin plugin;
    private final MafhamGauntletConfig config;
    private final int[] rotations = {90, 180, 270};

    @Inject
    private MafhamGauntletSceneOverlay(Client client, MafhamGauntletPlugin plugin, MafhamGauntletConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showLines())
        {
            return null;
        }
        if (client.getLocalPlayer() == null)
        {
            return null;
        }
        if (plugin.getMainBoss() == null)
        {
            return null;
        }
        if (plugin.getCentrePoint() == null)
        {
            return null;
        }
        graphics.setStroke(new BasicStroke(config.lineWidth()));
        graphics.setColor(config.lineColor());

        WorldPoint worldPoint = plugin.getCentrePoint();
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null)
        {
            return null;
        }
        double centreX = localPoint.getX() + 64;
        double centreY = localPoint.getY() + 64;
        for (Tiles tile : Tiles.values())
        {
            LocalPoint lp0 = new LocalPoint(localPoint.getX() + tile.getX(), localPoint.getY() + tile.getY());
            LocalPoint lp1 = new LocalPoint(localPoint.getX() + tile.getX2(), localPoint.getY() + tile.getY2());
            net.runelite.api.Point p0 = Perspective.localToCanvas(client, lp0, client.getPlane());
            Point p1 = Perspective.localToCanvas(client, lp1, client.getPlane());
            if (p0 != null && p1 != null)
            {
                graphics.drawLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
            }
            for (int rotationAmount : rotations)
            {
                double[] rotatedPoints1 = rotatePoint(lp0.getX(), lp0.getY(), centreX, centreY, rotationAmount);
                double[] rotatedPoints2 = rotatePoint(lp1.getX(), lp1.getY(), centreX, centreY, rotationAmount);
                LocalPoint lp2 = new LocalPoint((int)rotatedPoints1[0], (int)rotatedPoints1[1]);
                LocalPoint lp3 = new LocalPoint((int)rotatedPoints2[0], (int)rotatedPoints2[1]);
                net.runelite.api.Point p2 = Perspective.localToCanvas(client, lp2, client.getPlane());
                Point p3 = Perspective.localToCanvas(client, lp3, client.getPlane());
                if (p2 != null && p3 != null)
                {
                    graphics.drawLine(p2.getX(), p2.getY(), p3.getX(), p3.getY());
                }
            }
        }

        return null;
    }

    public static double[] rotatePoint(double x, double y, double centerX, double centerY, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);

        double translatedX = x - centerX;
        double translatedY = y - centerY;

        double rotatedX = translatedX * Math.cos(angleInRadians) - translatedY * Math.sin(angleInRadians);
        double rotatedY = translatedX * Math.sin(angleInRadians) + translatedY * Math.cos(angleInRadians);

        double[] result = new double[2];
        result[0] = rotatedX + centerX;
        result[1] = rotatedY + centerY;

        return result;
    }
}