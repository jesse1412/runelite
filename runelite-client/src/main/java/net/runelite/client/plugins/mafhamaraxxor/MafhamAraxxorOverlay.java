package net.runelite.client.plugins.mafhamaraxxor;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;

public class MafhamAraxxorOverlay extends Overlay {

    @Inject
    private MafhamAraxxorPlugin plugin;
    @Inject
    private Client client;

    @Inject
    private MafhamAraxxorOverlay(MafhamAraxxorPlugin plugin, Client client)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getBoss() == null)
        {
            return null;
        }

        if (plugin.getSpawnTick() != null)
        {
            LocalPoint eggLocal1 = new LocalPoint(6656, 5888, -1);
            LocalPoint eggLocal2 = new LocalPoint(5888, 5504, -1);
            LocalPoint eggLocal3 = new LocalPoint(4992, 5632, -1);
            int currentTick = client.getTickCount();
            int bossSpawnTick = plugin.getSpawnTick();

            renderEggTimer(eggLocal1, 21, currentTick, bossSpawnTick, graphics);
            renderEggTimer(eggLocal2, 63, currentTick, bossSpawnTick, graphics);
            renderEggTimer(eggLocal3, 105, currentTick, bossSpawnTick, graphics);
        }

        if (plugin.getAttacksUntilSpecCounter() != null && plugin.getAttackTimer() != null)
        {
            renderBossTimer(graphics);
        }

        Double progress = null;
        Point piePoint = null;
        if (plugin.getEnrageTick() != null)
        {
            piePoint = Perspective.localToCanvas(client, plugin.getBoss().getLocalLocation(), client.getPlane(), 0);
            int currentTick = client.getTickCount();
            int startTick = plugin.getEnrageTick().getAttackTick();
            int attackLength = plugin.getEnrageTick().getAttackLength();
            int endTick = startTick + attackLength;
            if (currentTick <= endTick)
            {
                progress = getProgress(endTick, attackLength);
            }
        }
        if (progress == null)
        {
            return null;
        }
        Color c = progress == 0 ? Color.green : Color.cyan;
        ProgressPieComponent pie = new ProgressPieComponent();
        pie.setPosition(piePoint);
        pie.setProgress(1 - progress);
        pie.setBorderColor(c);
        pie.setFill(c);
        return pie.render(graphics);
    }

    private void renderBossTimer(Graphics2D graphics)
    {
        Color color;
        switch (plugin.getAttacksUntilSpecCounter())
        {
            case 1:
                color = Color.orange;
                break;
            case 0:
                color = Color.RED;
                break;
            default:
                color = Color.green;
                break;
        }
        String attackTimer = String.valueOf(plugin.getAttackTimer());
        String specString = "Spec: " + plugin.getNextSpecialString() + " | " + plugin.getAttacksUntilSpecCounter();
        Point canvasPoint = plugin.getBoss().getCanvasTextLocation(graphics, attackTimer, 0);
        Point canvasPoint2 = new Point((canvasPoint.getX() - 50), (canvasPoint.getY() - 18));
        renderTextLocation(graphics, attackTimer, 16, Font.PLAIN, color, canvasPoint);
        renderTextLocation(graphics, specString, 16, Font.PLAIN, color, canvasPoint2);
    }

    private void renderEggTimer(LocalPoint localPoint, int offset, int currentTick, int bossSpawnTick, Graphics2D graphics)
    {
        int eggHatchTick = bossSpawnTick + offset;
        int eggHatchTimer = eggHatchTick - currentTick;
        if (eggHatchTimer < 0)
        {
            return;
        }
        Color color = (eggHatchTimer < 5) ? Color.RED : Color.GREEN;
        String eggHatchString = String.valueOf(eggHatchTimer);
        Point eggPoint = Perspective.getCanvasTextLocation(client, graphics, localPoint, eggHatchString, 0);
        renderTextLocation(graphics, eggHatchString, 20, Font.PLAIN, color, eggPoint);
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

    public double getProgress(int nextAttackTick, int attackDelayDivisor)
    {
        return (double) (nextAttackTick - client.getTickCount()) / attackDelayDivisor;
    }
}