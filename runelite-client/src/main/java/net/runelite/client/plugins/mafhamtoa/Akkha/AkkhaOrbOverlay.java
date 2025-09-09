package net.runelite.client.plugins.mafhamtoa.Akkha;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AkkhaOrbOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    public AkkhaOrbOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }
    private final PanelComponent panelComponent = new PanelComponent();
    private static final Polygon ARROW_HEAD = new Polygon(
            new int[]{0, -6, 6},
            new int[]{0, -10, -10},
            3
    );
    private WorldPoint adjustedwp;
    private WorldPoint adjustedwp2;
    private WorldPoint adjustedwp3;
    public Map<WorldPoint, WorldPoint> lineTiles = new HashMap<>();

    public ArrayList<LocalPoint> finalLocals = new ArrayList<>();
    public ArrayList<LocalPoint> dangerTiles = new ArrayList<>();

    int[] directionIndices = {0, 256, 512, 768, 1024, 1280, 1536, 1792, 2048};

    public void getDirection(int orientation, WorldPoint wp) {

        int distance = Math.abs(directionIndices[0] - orientation);
        int id = 0;
        for (int i = 1; i<directionIndices.length; i++) {
            int displacement = Math.abs(directionIndices[i] - orientation);
            if (displacement < distance) {
                id = i;
                distance = displacement;
            }
        }
        orientation = directionIndices[id];
          switch (orientation) {
              case 0: case 2048:
                adjustedwp = wp.dx(0).dy(-1).dz(0);
                adjustedwp2 = wp.dx(0).dy(-2).dz(0);
                adjustedwp3 = wp.dx(0).dy(-3).dz(0);
                break;
            case 256:
                adjustedwp = wp.dx(-1).dy(-1).dz(0);
                adjustedwp2 = wp.dx(-2).dy(-2).dz(0);
                adjustedwp3 = wp.dx(-3).dy(-3).dz(0);
                break;
            case 512:
                adjustedwp = wp.dx(-1).dy(0).dz(0);
                adjustedwp2 = wp.dx(-2).dy(0).dz(0);
                adjustedwp3 = wp.dx(-3).dy(0).dz(0);
                break;
            case 768:
                adjustedwp = wp.dx(-1).dy(1).dz(0);
                adjustedwp2 = wp.dx(-2).dy(2).dz(0);
                adjustedwp3 = wp.dx(-3).dy(3).dz(0);
                break;
            case 1024:
                adjustedwp = wp.dx(0).dy(1).dz(0);
                adjustedwp2 = wp.dx(0).dy(2).dz(0);
                adjustedwp3 = wp.dx(0).dy(3).dz(0);
                break;
            case 1280:
                adjustedwp = wp.dx(1).dy(1).dz(0);
                adjustedwp2 = wp.dx(2).dy(2).dz(0);
                adjustedwp3 = wp.dx(3).dy(3).dz(0);
                break;
            case 1536:
                adjustedwp = wp.dx(1).dy(0).dz(0);
                adjustedwp2 = wp.dx(2).dy(0).dz(0);
                adjustedwp3 = wp.dx(3).dy(0).dz(0);
                break;
            case 1792:
                adjustedwp = wp.dx(1).dy(-1).dz(0);
                adjustedwp2 = wp.dx(2).dy(-2).dz(0);
                adjustedwp3 = wp.dx(3).dy(-3).dz(0);
                break;
        }
        if (config.akkhaOrbTileAmount() == 1) {
            dangerTiles.add(LocalPoint.fromWorld(client, adjustedwp));
            lineTiles.put(wp, adjustedwp);
        }
          if (config.akkhaOrbTileAmount() == 2) {
              dangerTiles.add(LocalPoint.fromWorld(client, adjustedwp));
              finalLocals.add(LocalPoint.fromWorld(client, adjustedwp2));
              lineTiles.put(adjustedwp, adjustedwp2);
          }
        if (config.akkhaOrbTileAmount() == 3) {
            dangerTiles.add(LocalPoint.fromWorld(client, adjustedwp));
            finalLocals.add(LocalPoint.fromWorld(client, adjustedwp2));
            finalLocals.add(LocalPoint.fromWorld(client, adjustedwp3));
            lineTiles.put(adjustedwp, adjustedwp3);
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (client == null || client.getLocalPlayer() == null || client.getLocalPlayer().getLocalLocation() == null || adjustedwp == null || finalLocals == null)
        {
            return null;
        }
        for (LocalPoint finalLocal : finalLocals) {
            if (config.orbToggle())
            {
                Point canvasPoint = Perspective.localToCanvas(client, finalLocal, client.getPlane());
                Color configcolor = config.getAkkhaOrbColor();
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, finalLocal);
                    graphics.setColor(configcolor);
                    graphics.drawPolygon(poly);
                    graphics.fillPolygon(poly);
                }
            }
        }

        for (LocalPoint dangerTile : dangerTiles) {
            if (config.orbToggle())
            {
                Point canvasPoint = Perspective.localToCanvas(client, dangerTile, client.getPlane());
                Color configcolor = config.getAkkhaOrbDangerColor();
                if (canvasPoint != null) {
                    Polygon poly = Perspective.getCanvasTilePoly(client, dangerTile);
                    graphics.setColor(configcolor);
                    graphics.drawPolygon(poly);
                    graphics.fillPolygon(poly);
                }
            }
        }

        for(Map.Entry<WorldPoint, WorldPoint> entry : lineTiles.entrySet()) {
            if (config.lineToggle())
            {
                WorldPoint orb = entry.getKey();
                WorldPoint tile = entry.getValue();
                LocalPoint fl = LocalPoint.fromWorld(client, orb);
                LocalPoint tl = LocalPoint.fromWorld(client, tile);
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
                graphics.setColor(Color.CYAN);
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
            return panelComponent.render(graphics);
        }
    }
