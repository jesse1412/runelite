package net.runelite.client.plugins.mafhamtob.Nylocas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.*;
import javax.inject.Inject;
import com.google.common.collect.ImmutableList;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class NylocasOverlay extends Overlay
{
    @Inject
    private Nylocas nylocas;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private NpcUtil npcUtil;
    @Inject
    private Client client;
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    protected NylocasOverlay(Client client, MafhamToBConfig config, NpcUtil npcUtil, ModelOutlineRenderer modelOutlineRenderer)
    {
        this.config = config;
        this.client = client;
        this.npcUtil = npcUtil;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (nylocas.isNyloActive())
        {
            List<LocalPoint> localPointList = new ArrayList<>();
            final Map<NPC, Integer> npcMap = nylocas.getNylocasNpcs();
            Map<WorldPoint, NPC> wps = new HashMap<>();
            for (NPC npc : npcMap.keySet())
            {
                if (config.nyloAggressiveOverlay() && nylocas.getAggressiveNylocas().contains(npc) && !npcUtil.isDying(npc) && !npc.isDead())
                {
                    if (config.nyloAggressiveOverlayStyle() == MafhamToBConfig.AGGRESSIVENYLORENDERSTYLE.TILE)
                    {
                        LocalPoint lp = npc.getLocalLocation();
                        if (lp != null)
                        {
                            Point canvasPoint = Perspective.localToCanvas(client, lp, client.getPlane());
                            Color configcolor = new Color(255, 0, 0, 255);
                            if (canvasPoint != null) {
                                Polygon poly = Perspective.getCanvasTilePoly(client, lp);
                                graphics.setColor(configcolor);
                                graphics.drawPolygon(poly);
                                graphics.setColor(configcolor);
                                //graphics.fillPolygon(poly);
                            }
                        }
                    }
                    else if (config.nyloAggressiveOverlayStyle() == MafhamToBConfig.AGGRESSIVENYLORENDERSTYLE.HULL)
                    {
                        modelOutlineRenderer.drawOutline(npc, 1, Color.RED, 2);
                    }
                }

                int ticksLeft = npcMap.get(npc);
                if (ticksLeft > -1)
                {
                    if (config.nyloExplosions() && ticksLeft <= 6)
                    {
                        LocalPoint lp = npc.getLocalLocation();
                        if (lp != null && !npcUtil.isDying(npc) && !npc.isDead())
                        {
                            modelOutlineRenderer.drawOutline(npc, 1, Color.RED, 2);
                            int size = 1;
                            switch (npc.getId())
                            {
                                case 8345:
                                case 8346:
                                case 8347:
                                case 10794:
                                case 10795:
                                case 10796:
                                    size = 2;
                                    break;
                            }
                            if (size == 1)
                            {
                                WorldPoint wp = npc.getWorldLocation();
                                for (int x = -2; x <= 2; x++)
                                {
                                    for (int y = -2; y <= 2; y++)
                                    {
                                        wps.put(wp.dx(x).dy(y), npc);
                                    }
                                }
                            }
                            if (size == 2)
                            {
                                WorldPoint wp = npc.getWorldLocation();
                                for (int x = -2; x <= 3; x++)
                                {
                                    for (int y = -2; y <= 3; y++)
                                    {
                                        wps.put(wp.dx(x).dy(y), npc);
                                    }
                                }
                            }
                            for (Map.Entry<WorldPoint, NPC> entry : wps.entrySet())
                            {
                                WorldPoint worldPoint = entry.getKey();
                                NPC nylo = entry.getValue();
                                if (nylo.isDead() || npcUtil.isDying(npc))
                                {
                                    continue;
                                }
                                if (nylocas.getPillarPoints().contains(worldPoint))
                                {
                                    continue;
                                }
                                if (!nylocas.getNyloArea().contains(worldPoint))
                                {
                                    continue;
                                }
                                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                                if (localPoint == null)
                                {
                                    continue;
                                }
                                if (localPointList.contains(localPoint))
                                {
                                    continue;
                                }
                                localPointList.add(localPoint);
                                Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
                                Color configcolor = new Color(255, 0, 0, 85);
                                if (canvasPoint != null) {
                                    Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                                    graphics.setColor(configcolor);
                                    graphics.fillPolygon(poly);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
