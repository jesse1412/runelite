package net.runelite.client.plugins.mafhamcolo;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

public class MafhamColoOverlay extends Overlay {

    @Inject
    private MafhamColoPlugin plugin;
    @Inject
    private Client client;
    @Inject
    private MafhamColoConfig config;
    private ModelOutlineRenderer modelOutlineRenderer;
    private NpcUtil npcUtil;
    private final int seerID = 12815;
    private final int berserkerID = 12816;
    private final int archerID = 12814;
    private final int colRegionID = 7216;

    @Inject
    private MafhamColoOverlay(MafhamColoPlugin plugin, Client client, ModelOutlineRenderer modelOutlineRenderer, NpcUtil npcUtil, MafhamColoConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.npcUtil = npcUtil;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!playerIsInColo())
        {
            return null;
        }

        if (plugin.getWaveContinueTime() != null)
        {
            int continueTime = plugin.getWaveContinueTime();
            int tickTiming = continueTime + 5;
            int counter = tickTiming - client.getTickCount();
            String counterString = String.valueOf(counter);
            Color color = counter == 0 ? Color.green : Color.cyan;
            if (counter > -1)
            {
                Point canvasPoint = client.getLocalPlayer().getCanvasTextLocation(graphics, counterString, 250);
                if (config.waveStartTiming())
                {
                    renderTextLocation(graphics, counterString, 20,4, color, canvasPoint, true);
                }

            }
        }

        if (!plugin.getJavelins().isEmpty())
        {
            for (Javelin javelin : plugin.getJavelins())
            {
                String attackCount = String.valueOf(javelin.attackCount);
                Color color = Color.GREEN;
                if (javelin.attackCount == 0)
                {
                    color = Color.red;
                }
                Point canvasPoint = javelin.npc.getCanvasTextLocation(graphics, attackCount, 0);
                if (config.javelinTimer())
                {
                    renderTextLocation(graphics, attackCount, 16, 4, color, canvasPoint, true);
                }

            }
        }
        if (!plugin.getPillars().isEmpty())
        {
            if (plugin.getWaveStartTime() != null)
            {
                int currentTime = client.getTickCount();
                int waveStartTime = plugin.getWaveStartTime();
                int reinforcementsSpawnTime = waveStartTime + 66;
                int timeUntilSpawn = reinforcementsSpawnTime - currentTime;
                if (timeUntilSpawn > -1)
                {
                    for (GameObject pillar : plugin.getPillars())
                    {
                        String timerString = String.valueOf(timeUntilSpawn);
                        Point canvasPoint = pillar.getCanvasTextLocation(graphics, timerString, 0);
                        if (config.reinforcementsTimer())
                        {
                            renderTextLocation(graphics, timerString, 12, 4, Color.CYAN, canvasPoint, false);
                        }
                    }
                }
            }
        }

        for (NPC npc : client.getNpcs())
        {
            int id = npc.getId();
            if (Objects.equals(npc.getName(), "Sol Heredit"))
            {
                if (npc.getAnimation() == 10886 || npc.getAnimation() == 10887) //melee pray attacks
                {
                    modelOutlineRenderer.drawOutline(npc, 3, Color.red, 1);
                }
                if (npc.getAnimation() == 10884) //grapple attack
                {
                    modelOutlineRenderer.drawOutline(npc, 3, Color.blue, 1);
                }
            }
            if (id == 12811) //shaman
            {
                if (client.getLocalPlayer().getWorldLocation().distanceTo(npc.getWorldLocation()) < 11)
                {
                    if (hasLineOfSight(client.getLocalPlayer().getWorldArea(), npc.getWorldArea()))
                    {
                        if (!npcUtil.isDying(npc))
                        {
                            if (config.lineOfSightHL())
                            {
                                fillTile(graphics, npc, Color.cyan);
                            }

                        }
                    }
                }
            }
            if (id == 12817) //javelin
            {
                if (npc.getAnimation() == 10893) //throwID
                {
                    if (!npcUtil.isDying(npc))
                    {
                        if (config.javelinHighlight())
                        {
                            modelOutlineRenderer.drawOutline(npc, 2, Color.ORANGE, 1);
                        }
                    }
                }
                if (hasLineOfSight(client.getLocalPlayer().getWorldArea(), npc.getWorldArea()))
                {
                    if (!npcUtil.isDying(npc))
                    {
                        if (config.lineOfSightHL())
                        {
                            fillTile(graphics, npc, Color.GREEN);
                        }
                    }
                }
            }
            if (id == 12819) //shockwave
            {
                if (hasLineOfSight(client.getLocalPlayer().getWorldArea(), npc.getWorldArea()))
                {
                    if (!npcUtil.isDying(npc))
                    {
                        if (config.lineOfSightHL())
                        {
                            fillTile(graphics, npc, Color.CYAN);
                        }
                    }
                }
            }
            if (id == 12818) //manticore
            {
                if (hasLineOfSight(client.getLocalPlayer().getWorldArea(), npc.getWorldArea()))
                {
                    if (!npcUtil.isDying(npc))
                    {
                        if (config.lineOfSightHL())
                        {
                            fillTile(graphics, npc, Color.ORANGE);
                        }
                    }
                }
            }
        }
        if (!plugin.getHighlightTiles().isEmpty())
        {
            Color color = new Color(0, 255, 0, 35);
            for (WorldPoint worldPoint : plugin.getHighlightTiles().keySet())
            {
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (localPoint != null)
                {
                    Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                    if (canvasPoint != null) {
                        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                        graphics.setColor(color);
                        //graphics.drawPolygon(poly);
                        graphics.fillPolygon(poly);
                    }
                }
            }
        }
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, net.runelite.api.Point canvasPoint, Boolean shadow)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final net.runelite.api.Point canvasCenterPoint = new net.runelite.api.Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final net.runelite.api.Point canvasCenterPoint_shadow = new net.runelite.api.Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1);
            if (shadow)
            {
                OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            }
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    private boolean playerIsInColo()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (colRegionID == mapRegion)
            {
                return true;
            }
        }
        return false;
    }

    private boolean hasLineOfSight(WorldArea playerWorldArea, WorldArea npcWorldArea)
    {
        return playerWorldArea.hasLineOfSightTo(client, npcWorldArea);
    }

    private void fillTile(Graphics2D graphics, NPC npc, Color color)
    {
        LocalPoint localPoint = LocalPoint.fromWorld(client, npc.getWorldLocation());
        if (localPoint != null)
        {
            Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint, npc.getComposition().getSize());
            if (polygon != null)
            {
                Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 105);
                graphics.setColor(color2);
                //graphics.drawPolygon(polygon);
                graphics.fillPolygon(polygon);
            }
        }
    }
}