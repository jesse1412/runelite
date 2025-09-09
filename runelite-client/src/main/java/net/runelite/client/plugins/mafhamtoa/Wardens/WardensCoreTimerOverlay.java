package net.runelite.client.plugins.mafhamtoa.Wardens;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class WardensCoreTimerOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private WardensCoreTimer wardensCoreTimer;

    @Inject
    private WardensCoreTimerOverlay(Client client, MafhamToAConfig config, WardensCoreTimer wardensCoreTimer)
    {
        this.client = client;
        this.config = config;
        this.wardensCoreTimer = wardensCoreTimer;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.showWardenIDs() && wardensCoreTimer.isInWardens())
        {
            for (NPC npc : client.getNpcs())
            {
                int id = npc.getId();
                int index = npc.getIndex();
                String string = String.valueOf(index);
                Point canvasPoint = npc.getCanvasTextLocation(graphics, string, 0);
                Point canvasPoint2 = npc.getCanvasTextLocation(graphics, string, 500);
                switch (id)
                {
                    case 11750:
                    case 11751:
                    case 11752: //obelisks
                    case 11747:
                    case 11749: //tumeken's warden
                    case 11746:
                    case 11748: //elidinis' warden
                        renderTextLocationNoOutline(graphics,string, 10, 4, Color.GREEN, canvasPoint2);
                        break;
                    case 11769: //skulls
                        renderTextLocationNoOutline(graphics,string, 10, 4, Color.GREEN, canvasPoint);
                        break;
                    default:
                        break;
                }
            }
        }
        if (config.showUfos())
        {
            if (!wardensCoreTimer.getUfos().isEmpty())
            {
                for (GameObject gameObject : wardensCoreTimer.getUfos())
                {
                    LocalPoint localPoint = gameObject.getLocalLocation();
                    Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, localPoint, 3);
                    if (tilePoly != null)
                    {
                        graphics.setColor(new Color(255,0,0,85));
                        graphics.fill(tilePoly);
                    }
                }
            }
        }
        if (!config.showCoreTimer())
        {
            return null;
        }
        if (wardensCoreTimer.getCoreTimer() != null && wardensCoreTimer.getCore() != null)
        {
            Point canvasPoint = wardensCoreTimer.getCore().getCanvasTextLocation(graphics, String.valueOf(wardensCoreTimer.getCoreTimer()), 200);
            renderTextLocation(graphics, String.valueOf(wardensCoreTimer.getCoreTimer()), 14,4, Color.GREEN, canvasPoint);
        }
        if (wardensCoreTimer.getBlueTimer() > -1)
        {
            Player player = client.getLocalPlayer();
            Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 60);
            String string = String.valueOf(wardensCoreTimer.getBlueTimer());
            Color color = Color.cyan;
            if (point != null)
            {
                renderTextLocation(graphics, string, 14, 1, color, point);
            }
        }
        if (wardensCoreTimer.getOrangeTimer() > -1)
        {
            Player player = client.getLocalPlayer();
            Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 120);
            String string = String.valueOf(wardensCoreTimer.getOrangeTimer());
            Color color = new Color(255, 132,50,255);
            if (point != null)
            {
                renderTextLocation(graphics, string, 14, 1, color, point);
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

    private void renderTextLocationNoOutline(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, net.runelite.api.Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final net.runelite.api.Point canvasCenterPoint = new net.runelite.api.Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            //final net.runelite.api.Point canvasCenterPoint_shadow = new Point(
                    //canvasPoint.getX() + 1,
                    //canvasPoint.getY() + 1);
            //OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }
}