/*
 * Written by https://github.com/Mafham
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * however I would prefer if you didn't sell this plugin for profit!
 * I made this to teach myself how to develop plugins, this code sux.
 * I do not condone rule-breaking or use of illegal plugins. Thanks :)
 */

package net.runelite.client.plugins.mafhamtoa.Zebak;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class ZebakBouldersOverlay extends Overlay {
    private final ZebakBoulders plugin;
    private final Client client;
    @Inject
    private MafhamToAConfig config;

    @Inject
    public ZebakBouldersOverlay(ZebakBoulders plugin, Client client)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.getGreenJugHighlights().isEmpty() && plugin.getYellowJugHighlights().isEmpty() && plugin.getRollingBoulderTimingHL().isEmpty() && plugin.getSafeTileHighlights().isEmpty()) {
            return null;
        }
        if (!config.boulderToggle())
        {
            return null;
        }
        for (NPC npc : plugin.getBlueJugHighlights()) {
            renderNpcOverlay(graphics, npc, Color.CYAN);
        }
        for (NPC npc : plugin.getGreenJugHighlights()) {
            renderNpcOverlay(graphics, npc, config.getGreenColor());
        }
        for (NPC npc : plugin.getYellowJugHighlights()) {
            renderNpcOverlay(graphics, npc, config.getYellowColor());
        }
        for (NPC npc : plugin.getRollingBoulderTimingHL().keySet()) {
            renderNpcOverlay(graphics, npc, Color.green);
        }
        for (LocalPoint localPoint : plugin.getSafeTileHighlights())
        {
            Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
            if (canvasPoint != null) {
                Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                graphics.setColor(config.getSafeColor());
                graphics.drawPolygon(poly);
                graphics.setColor(config.getSafeFillColor());
                graphics.fillPolygon(poly);
            }
        }

        for(Map.Entry<WorldPoint, WorldPoint> entry : plugin.yellowLineTiles.entrySet()) {
            WorldPoint jug = entry.getKey();
            WorldPoint boulder = entry.getValue();
            LocalPoint fl = LocalPoint.fromWorld(client, jug);
            LocalPoint tl = LocalPoint.fromWorld(client, boulder);
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
            graphics.setColor(config.getYellowColor());
            graphics.setStroke(new BasicStroke(1));
            graphics.drawLine(fsx, fsy, tsx, tsy);
        }
        for(Map.Entry<WorldPoint, WorldPoint> entry : plugin.greenLineTiles.entrySet()) {
            WorldPoint jug = entry.getKey();
            WorldPoint boulder = entry.getValue();
            LocalPoint fl = LocalPoint.fromWorld(client, jug);
            LocalPoint tl = LocalPoint.fromWorld(client, boulder);
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
            graphics.setColor(config.getGreenColor());
            graphics.setStroke(new BasicStroke(1));
            graphics.drawLine(fsx, fsy, tsx, tsy);
        }
        return null;
    }

    private void renderNpcOverlay(Graphics2D graphics, NPC npc, Color color) {
        if (config.highlightHullOutline()) {
            Shape shape = npc.getConvexHull();
            if (shape != null) {
                graphics.setColor(color);
                graphics.draw(shape);
            }
        }
        if (config.highlightHull()) {
            Shape shape = npc.getConvexHull();
            if (shape != null) {
                graphics.setColor((new Color (color.getRed(), color.getGreen(), color.getBlue(), 50)));
                graphics.fill(shape);
            }
        }

        if (config.highlightTile()) {
            Shape shape = npc.getCanvasTilePoly();
            if (shape != null) {
                graphics.setColor(color);
                graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
                graphics.draw(shape);
            }
        }
    }
}


