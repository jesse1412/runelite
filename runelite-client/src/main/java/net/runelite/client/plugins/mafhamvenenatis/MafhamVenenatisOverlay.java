package net.runelite.client.plugins.mafhamvenenatis;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.RuneLite;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MafhamVenenatisOverlay extends Overlay {

    private final Client client;
    private final MafhamVenenatisPlugin plugin;
    private MafhamVenenatisConfig config;

    private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
    private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
    private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
    private static final int OVERLAY_ICON_DISTANCE = 50;
    private static final int OVERLAY_ICON_MARGIN = 8;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    private MafhamVenenatisOverlay(Client client, MafhamVenenatisPlugin plugin, MafhamVenenatisConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private BufferedImage getIcon(MafhamVenenatisPlugin.attackStyle attackStyle)
    {
        switch (attackStyle)
        {
            case RANGE1:
                return loadImage(config.iconsPath() + "" + "spiderling.png");
            case RANGE2:
                return iconManager.getSkillImage(Skill.RANGED);
            case MAGE1:
                return loadImage(config.iconsPath() + "" + "web.png");
            case MAGE2:
                return iconManager.getSkillImage(Skill.MAGIC);

        }
        return null;
    }

    public static BufferedImage loadImage(String filePath) {
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getTickCount() <= plugin.getDeathTime() + 10 && plugin.getRespawnPoint() != null)
        {
            int timer = (plugin.getDeathTime() + 10) - client.getTickCount();
            LocalPoint localPoint = plugin.getRespawnPoint();

            if (localPoint != null)
            {
                Point respawnTimerPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(), 0);
                Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 4);
                String respawnTimer = String.valueOf(timer);
                renderTextLocation(graphics, respawnTimer, 24, Font.BOLD, Color.green, respawnTimerPoint);
                renderPoly(graphics, Color.white, new Color(0,0,0,0), polygon);
            }
        }

        if (plugin.getMainBoss() == null)
        {
            return null;
        }

        if (!plugin.getWebObjects().isEmpty())
        {
            for (GameObject gameObject : plugin.getWebObjects())
            {
                LocalPoint localPoint = gameObject.getLocalLocation();
                Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                //Color outlineColor = new Color(255, 0, 0, 25);
                Color fillColor = new Color(255, 0, 0, 75);
                //graphics.setColor(outlineColor);
                //graphics.drawPolygon(polygon);
                graphics.setColor(fillColor);
                graphics.fillPolygon(polygon);
            }
        }

        if (plugin.getCurrentAttackStyle() == null)
        {
            return null;
        }

        LocalPoint lp = plugin.getMainBoss().getLocalLocation();
        if (lp != null)
        {
            net.runelite.api.Point point = Perspective.localToCanvas(client, lp, client.getPlane(),
                    16);
            if (point != null)
            {
                point = new Point(point.getX(), point.getY());
                BufferedImage icon = getIcon(plugin.getCurrentAttackStyle());

                int bgPadding = 4;
                int currentPosX = 0;

                graphics.setStroke(new BasicStroke(2));
                graphics.setColor(COLOR_ICON_BACKGROUND);
                graphics.fillOval(
                        point.getX() - currentPosX - bgPadding,
                        point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                        icon.getWidth() + bgPadding * 2,
                        icon.getHeight() + bgPadding * 2);

                graphics.setColor(COLOR_ICON_BORDER);
                graphics.drawOval(
                        point.getX() - currentPosX - bgPadding,
                        point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                        icon.getWidth() + bgPadding * 2,
                        icon.getHeight() + bgPadding * 2);

                graphics.drawImage(
                        icon,
                        point.getX() - currentPosX,
                        point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE,
                        null);
                graphics.setColor(COLOR_ICON_BORDER_FILL);
                Arc2D.Double arc = new Arc2D.Double(
                        point.getX() - currentPosX - bgPadding,
                        point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                        icon.getWidth() + bgPadding * 2,
                        icon.getHeight() + bgPadding * 2,
                        90.0,
                        360.0 * (-plugin.getBossAttackCounter()) / 4,
                        Arc2D.OPEN);
                graphics.draw(arc);

                currentPosX += icon.getWidth() + OVERLAY_ICON_MARGIN;
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

    private void renderPoly(Graphics2D graphics, Color borderColor, Color fillColor, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke((float) 1));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }
}