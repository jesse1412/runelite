package net.runelite.client.plugins.mafhamcolo;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.api.Point;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

public class MafhamColoPieOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamColoPlugin plugin;

    @Inject
    public MafhamColoPieOverlay(Client client, MafhamColoPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!playerIsInColo())
        {
            return null;
        }

        if (plugin.getMeleeAttack1Tick() == null && plugin.getMeleeAttack2Tick() == null)
        {
            return null;
        }

        Point canvasPoint = null;
        for (NPC npc : client.getNpcs())
        {
            if (Objects.equals(npc.getName(), "Sol Heredit"))
            {
                LocalPoint localPoint = npc.getLocalLocation();
                canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane(),0);
            }
        }
        if (canvasPoint == null)
        {
            return null;
        }
        Double progress = null;
        if (plugin.getMeleeAttack1Tick() != null)
        {
            int currentTick = client.getTickCount();
            int startTick = plugin.getMeleeAttack1Tick();
            int attack1Tick = startTick + 3;
            int attack2Tick = startTick + 6;
            int attack3Tick = startTick + 9;
            if (currentTick <= attack1Tick)
            {
                progress = getProgress(attack1Tick, 3);
            }
            if (currentTick > attack1Tick && currentTick <= attack2Tick)
            {
                progress = getProgress(attack2Tick, 3);
            }
            if (currentTick > attack2Tick && currentTick <= attack3Tick)
            {
                progress = getProgress(attack3Tick, 3);
            }
        }
        if (plugin.getMeleeAttack2Tick() != null)
        {
            int currentTick = client.getTickCount();
            int startTick = plugin.getMeleeAttack2Tick();
            int attack1Tick = startTick + 3;
            int attack2Tick = startTick + 6;
            int attack3Tick = startTick + 10;
            if (currentTick <= attack1Tick)
            {
                progress = getProgress(attack1Tick, 3);
            }
            if (currentTick > attack1Tick && currentTick <= attack2Tick)
            {
                progress = getProgress(attack2Tick, 3);
            }
            if (currentTick > attack2Tick && currentTick <= attack3Tick)
            {
                progress = getProgress(attack3Tick, 4);
            }
        }
        if (progress == null)
        {
            return null;
        }
        Color c = progress == 0 ? Color.green : Color.cyan;
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(canvasPoint);
        pie.setDiameter(75);
        pie.setProgress(1 - progress);
        pie.setBorderColor(c);
        pie.setFill(c);
        return pie.render(graphics);
    }

    private boolean playerIsInColo()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (7216 == mapRegion)
            {
                return true;
            }
        }
        return false;
    }

    public double getProgress(int nextAttackTick, int attackDelayDivisor)
    {
        return (double) (nextAttackTick - client.getTickCount()) / attackDelayDivisor;
    }
}