package net.runelite.client.plugins.alchemicalhydra;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AlchemicalHydraOverlay extends Overlay {
    private final Client client;
    private final AlchemicalHydraPlugin plugin;
    private final AlchemicalHydraConfig config;
    private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
    private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
    private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
    private static final int OVERLAY_ICON_DISTANCE = 50;
    private static final int OVERLAY_ICON_MARGIN = 8;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    private AlchemicalHydraOverlay(Client client, AlchemicalHydraPlugin plugin, AlchemicalHydraConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private BufferedImage getIcon(AlchemicalHydraPlugin.attackStyle attackStyle) {
        switch (attackStyle) {
            case RANGE:
                return iconManager.getSkillImage(Skill.RANGED);
            case MAGE:
                return iconManager.getSkillImage(Skill.MAGIC);
        }
        return null;
    }

    @Override
    public Dimension render(Graphics2D graphics) {


        if (plugin.getRedVent() != null && plugin.getVentCounter() > -1 && config.showVents())
        {
            GameObject vent = plugin.getRedVent();
            Point canvasPoint = vent.getCanvasTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 0);
            renderTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 12, 4, Color.green, canvasPoint);
        }

        if (plugin.getGreenVent() != null && plugin.getVentCounter() > -1 && config.showVents())
        {
            GameObject vent = plugin.getGreenVent();
            Point canvasPoint = vent.getCanvasTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 0);
            renderTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 12, 4, Color.green, canvasPoint);
        }

        if (plugin.getBlueVent() != null && plugin.getVentCounter() > -1 && config.showVents())
        {
            GameObject vent = plugin.getBlueVent();
            Point canvasPoint = vent.getCanvasTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 0);
            renderTextLocation(graphics, String.valueOf(plugin.getVentCounter()), 12, 4, Color.green, canvasPoint);
        }

        if (plugin.getHydra() == null)
            return null;

        if(config.HighlightPoison()) {
            for (WorldPoint point : plugin.getPoisonTiles()) {
                drawAOE(graphics, point, config.HighlightColourPoison(), 200, 30, 1);
            }
        }

        if(config.ShowPrayers()) {
            if(plugin.getNextAttackStyle() == null)
            {
                return null;
            }
            if (plugin.isThirdPhase() && plugin.getFlameAttackCount() > 0 && config.showFlamePoison())
            {
                Point canvasPoint = plugin.getHydra().getCanvasTextLocation(graphics, String.valueOf(plugin.getFlameAttackCount()), 300);
                renderTextLocation(graphics, String.valueOf(plugin.getFlameAttackCount()), 24, 4, Color.cyan, canvasPoint);
            }
            if (plugin.isLastPhase() && config.showFlamePoison())
            {
                Point canvasPoint = plugin.getHydra().getCanvasTextLocation(graphics, String.valueOf(plugin.getPoistonAttackCount()), 300);
                renderTextLocation(graphics, String.valueOf(plugin.getPoistonAttackCount()), 24, 4, Color.cyan, canvasPoint);
            }
            LocalPoint lp = plugin.getHydra().getLocalLocation();
            if (lp != null) {
                net.runelite.api.Point point = Perspective.localToCanvas(client, lp, client.getPlane(),
                        16);
                if (point != null) {
                    point = new Point(point.getX(), point.getY());
                    BufferedImage icon = getIcon(plugin.getNextAttackStyle());

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
                    //System.out.println(-360.0 * (plugin.getAttacksPerSwitch() - (plugin.getAttacksToSwitch())) / plugin.getAttacksPerSwitch());
                    //System.out.println((plugin.getAttacksPerSwitch() - plugin.getAttacksToSwitch()) / plugin.getAttacksPerSwitch());
                    //System.out.println(plugin.getAttacksPerSwitch());
                    //System.out.println(plugin.getAttacksToSwitch());
                    //System.out.println(plugin.getAttacksPerSwitch() - (plugin.getAttacksToSwitch()));
                    graphics.setColor(COLOR_ICON_BORDER_FILL);
                    Arc2D.Double arc = new Arc2D.Double(
                            point.getX() - currentPosX - bgPadding,
                            point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                            icon.getWidth() + bgPadding * 2,
                            icon.getHeight() + bgPadding * 2,
                            90.0,
                            -360.0 * ((plugin.getAttacksToSwitch()) + 1) / plugin.getAttacksPerSwitch(),
                            Arc2D.OPEN);
                    graphics.draw(arc);

                    currentPosX += icon.getWidth() + OVERLAY_ICON_MARGIN;
                }

            }
        }

        return null;
    }

    private void drawAOE(Graphics2D graphics, WorldPoint point, Color color, int outlineAlpha, int fillAlpha, int size) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (point.distanceTo(playerLocation) >= 32) {
            return;
        }
        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }
        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return;
        }

        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

        if (tilePoly != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(tilePoly);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
            graphics.fill(tilePoly);
        }
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
