package net.runelite.client.plugins.dukesucellus;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;


import javax.inject.Inject;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DukeSucellusSceneOverlay extends Overlay {
    private final Client client;
    @Inject
    private DukeSucellusPlugin plugin;
    private final OverlayManager overlayManager;

    @Inject
    public DukeSucellusSceneOverlay(OverlayManager overlayManager, Client client, DukeSucellusPlugin plugin, ItemManager itemManager) {
        this.overlayManager = overlayManager;
        this.client = client;
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    public void startUp() {
        overlayManager.add(this);
    }

    public void shutDown() {
        overlayManager.removeIf(o -> o instanceof DukeSucellusSceneOverlay);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!plugin.isDukeRoom()) {
            return null;
        }

        if (plugin.getRespawnTimer() != null && plugin.getRespawnTimer() > -1)
        {
            String timer = String.valueOf(plugin.getRespawnTimer());
            Player player = client.getLocalPlayer();
            Point point = player.getCanvasTextLocation(graphics2D, String.valueOf(timer), player.getLogicalHeight() + 60);
            Color color = Color.GREEN;
            if (point != null)
            {
                renderTextLocation(graphics2D, timer, 14, 4, color, point);
            }
        }

        List<TileHighlight> builtObjects = plugin.getHighlights();
        List<LocalPoint> safeTiles = plugin.getSafeLocations();

        if (!safeTiles.isEmpty() && plugin.isDukePrep()) {
            for (LocalPoint localPoint : safeTiles) {
                drawSafety(localPoint, graphics2D);
            }
        }

        if (!builtObjects.isEmpty()) {
            int currentTick = client.getTickCount();

            for (Iterator<TileHighlight> iterator = builtObjects.iterator(); iterator.hasNext(); ) {
                TileHighlight tileHighlight = iterator.next();

                String identifier = tileHighlight.getIdentifier();
                int despawnTick = tileHighlight.getDespawnTick();
                LocalPoint location = tileHighlight.getLocation();

                if (currentTick >= despawnTick) {
                    iterator.remove();
                    continue;
                }

                if (plugin.isDukeFight()) {
                    if (Objects.equals(identifier, "DUKE_POISON_ATTACK") || Objects.equals(identifier, "DUKE_STOMP")) {
                        drawTile(tileHighlight, graphics2D);
                    }
                } else {
                    int spawnTick = tileHighlight.getSpawnTick();
                    int hurtTick = tileHighlight.getHurtTick();
                    int duration = hurtTick - spawnTick;

                    int zOffset = Objects.equals(identifier, "EYE_LASER") ? 250 : 0;
                    Point canvasPoint = Perspective.localToCanvas(client, location, client.getPlane(), zOffset);

                    double progress = plugin.getPieProgress(currentTick, hurtTick, duration);
                    double divisorStep = (double) 1 / duration;

                    Color finalColour = Objects.equals(identifier, "POISON_VENT") ? Color.red : Color.green;
                    Color intermediaryColour = Objects.equals(identifier, "POISON_VENT") ? Color.cyan : Color.red;
                    Color pieColour = progress == 0 ? finalColour : progress <= divisorStep ? intermediaryColour : Color.cyan;

                    if (Objects.equals(identifier, "POISON_VENT")) {
                        drawTile(tileHighlight, graphics2D);
                        if (currentTick <= hurtTick) {
                            if (canvasPoint != null) {
                                drawPie(canvasPoint, progress, pieColour, graphics2D);
                            }

                        }
                    }
                    if (Objects.equals(identifier, "EYE_LASER") || Objects.equals(identifier, "DUKE_STUN_SHADOW")) {
                        if (currentTick <= hurtTick) {
                            if (canvasPoint != null) {
                                drawPie(canvasPoint, progress, pieColour, graphics2D);
                            }

                        }
                        if (Objects.equals(identifier, "DUKE_STUN_SHADOW")) {
                            drawTile(tileHighlight, graphics2D);
                        }
                    }
                }
            }
        }

        // Duke Eye Attack Pie Chart
        if (plugin.getNextEyeAttackTick() >= client.getTickCount()) {
            int currentTick = client.getTickCount();

            LocalPoint localPoint = plugin.getDukeLocation();
            if (localPoint == null) {
                return null;
            }

            Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(),16);
            if (canvasPoint == null) {
                return null;
            }

            double eyeProgress = plugin.getPieProgress(currentTick, plugin.getNextEyeAttackTick(), plugin.getEyeAttackTicks());
            Color pieColour = eyeProgress == 0 ? Color.green : eyeProgress < 0.4 ? Color.red : Color.cyan;

            drawPie(canvasPoint, eyeProgress, pieColour, graphics2D);
        }

        return null;
  }

    private void drawSafety(LocalPoint localPoint, Graphics2D graphics2D) {
      Polygon tilePolygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 3);
      if (tilePolygon == null) {
          return;
      }

      int currentStage = plugin.getVentStage();
      LocalPoint leftDuke = plugin.getLeftDuke();
      LocalPoint rightDuke = plugin.getRightDuke();
      LocalPoint rightMining = plugin.getRightMining();
      Color outlineColour;

      if (localPoint == leftDuke) {
          outlineColour = currentStage == 4 ? Color.yellow : Color.green;
      } else if (localPoint == rightDuke || localPoint == rightMining) {
          outlineColour = Color.green;
      } else {
          outlineColour = currentStage == 1 ? Color.green : Color.yellow;
      }

      Color fillColour = new Color(outlineColour.getRed(), outlineColour.getGreen(), outlineColour.getBlue(), 50);
      graphics2D.setColor(outlineColour);
      graphics2D.drawPolygon(tilePolygon);
      graphics2D.setColor(fillColour);
      graphics2D.fillPolygon(tilePolygon);
  }
    private void drawTile(TileHighlight tileHighlight, Graphics2D graphics2D) {
        LocalPoint location = tileHighlight.getLocation();
        int size = tileHighlight.getSize();

        Polygon tilePolygon = Perspective.getCanvasTileAreaPoly(client, location, size);
        if (tilePolygon == null) {
            return;
        }

        Color outlineColour = Color.BLUE;
        Color fillColour = new Color(outlineColour.getRed(), outlineColour.getGreen(), outlineColour.getBlue(), 50);

        graphics2D.setColor(outlineColour);
        graphics2D.drawPolygon(tilePolygon);
        graphics2D.setColor(fillColour);
        graphics2D.fillPolygon(tilePolygon);
  }
    private void drawPie(Point canvasPoint , double progress, Color pieColour, Graphics2D graphics2D) {
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(canvasPoint);
        pie.setProgress(1 - progress);
        pie.setBorderColor(pieColour);
        pie.setFill(pieColour);

        pie.render(graphics2D);
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
