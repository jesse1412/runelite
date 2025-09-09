package net.runelite.client.plugins.cerberuscycle;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class CerberusCycleOverlay extends Overlay {

    private final Client client;
    private final CerberusCyclePlugin plugin;
    private final CerberusCycleConfig config;

    @Inject
    private CerberusCycleOverlay(Client client, CerberusCyclePlugin plugin, CerberusCycleConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getLocalPlayer() == null) {
            return null;
        }
        if (!plugin.isRunCerberus()) {
            return null;
        }
        int cycle = plugin.attackCycle;
        int tickCounter = plugin.getTickCounter();
        final String cycleStr = String.valueOf(cycle);
        final String tickStr = String.valueOf(tickCounter);
        Color color = Color.white;
        String attackStr = "??";
        switch (cycle) {
            case 1:
            case 11:
            case 21:
                attackStr = "Triple";
                color = config.tripleColor();
                break;
            case 2:
            case 3:
            case 4:
            case 6:
            case 8:
            case 9:
            case 12:
            case 13:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 24:
            case 26:
            case 27:
                attackStr = "Auto";
                color = config.defaultColor();
                break;
            case 5:
            case 10:
            case 15:
            case 20:
            case 25:
                attackStr = "Lava";
                color = config.lavaColor();
                break;
            case 7:
            case 14:
            case 28:
                attackStr = "Ghosts";
                color = config.ghostColor();
                break;


        }
        Point canvasPoint = plugin.getCerberusNPC().getCanvasTextLocation(graphics, cycleStr + " " + attackStr, config.textHeight());
        Point canvasPoint2 = plugin.getCerberusNPC().getCanvasTextLocation(graphics, tickStr, (config.textHeight() - 100));
        renderTextLocation(graphics, cycleStr + " " + attackStr, config.fontSize(), Font.BOLD, color, canvasPoint);
        if (config.showTickCounter())
        {
            renderTextLocation(graphics, tickStr, config.fontSize(), Font.BOLD, color, canvasPoint2);
        }
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                    canvasPoint.getX(),
                    canvasPoint.getY());
            final Point canvasCenterPoint_shadow = new Point(
                    canvasPoint.getX() + 1,
                    canvasPoint.getY() + 1) ;
            if (config.showShadow())
            {
                OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            }
            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }
}