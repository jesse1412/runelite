package net.runelite.client.plugins.mafhamleviathan;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class MafhamLeviathanOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamLeviathanPlugin plugin;

    @Inject
    private MafhamLeviathanOverlay(MafhamLeviathanPlugin plugin, Client client)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isBossSpawned())
        {
            return null;
        }

        Projectile closestProjectile = null;

        for (Projectile projectile : client.getProjectiles())
        {
            if (projectile.getId() == 2489 || projectile.getId() == 2488 || projectile.getId() == 2487)
            {
                if (closestProjectile == null || projectile.getRemainingCycles() < closestProjectile.getRemainingCycles())
                {
                    closestProjectile = projectile;
                }
            }
            if (projectile.getId() == 2489) //Mage
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 21);
                renderRectangle(graphics, prayerWidget, projectile, Color.cyan, 55);
            }
            if (projectile.getId() == 2488) //Melee
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 23);
                renderRectangle(graphics, prayerWidget, projectile, Color.ORANGE, 55);
            }
            if (projectile.getId() == 2487) //Range
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 22);
                renderRectangle(graphics, prayerWidget, projectile, Color.GREEN, 55);
            }
        }

        if (closestProjectile != null)
        {
            if (closestProjectile.getId() == 2489) //Mage
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 21);
                Color color = client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC) ? Color.GREEN : Color.RED;
                renderOutlinePolygon(graphics, prayerWidget.getBounds(), color, 0);
            }
            if (closestProjectile.getId() == 2488) //Melee
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 23);
                Color color = client.isPrayerActive(Prayer.PROTECT_FROM_MELEE) ? Color.GREEN : Color.RED;
                renderOutlinePolygon(graphics, prayerWidget.getBounds(), color, 0);
            }
            if (closestProjectile.getId() == 2487) //Range
            {
                Widget prayerWidget = client.getWidget(InterfaceID.PRAYER, 22);
                Color color = client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES) ? Color.GREEN : Color.RED;
                renderOutlinePolygon(graphics, prayerWidget.getBounds(), color, 0);
            }
        }

        return null;
    }

    private static void renderOutlinePolygon(Graphics2D graphics, Shape poly, Color color, int alpha)
    {
        graphics.setColor(color);
        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        graphics.fill(poly);
        graphics.setStroke(originalStroke);
    }

    private void renderRectangle(Graphics2D graphics, Widget prayerWidget, Projectile projectile, Color color, int alpha)
    {
        int remainingCycles = projectile.getRemainingCycles();
        if (remainingCycles > 90 || remainingCycles < 0)
        {
            return;
        }
        int rectWidth = prayerWidget.getWidth() / 2;
        int rectHeight = prayerWidget.getHeight() / 4;
        int rectX = (int) prayerWidget.getBounds().getX();
        rectX += prayerWidget.getWidth() / 4;
        int rectY = (int) prayerWidget.getBounds().getY();
        rectY -= remainingCycles * 2;
        rectY -= rectHeight; //so it lines up with the top of the prayer box
        Rectangle rectangle = new Rectangle(rectWidth, rectHeight);
        rectangle.translate(rectX, rectY);
        renderOutlinePolygon(graphics, rectangle, color, alpha);
    }
}