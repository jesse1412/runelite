package net.runelite.client.plugins.dukesucellus;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.plugins.dukesucellus.Enums.NextAttack;
import net.runelite.client.ui.overlay.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class AttackHandlerOverlay extends Overlay {

    @Inject
    private Client client;
    @Inject
    private AttackHandler attackHandler;
    @Inject
    private DukeSucellusConfig config;
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    public AttackHandlerOverlay(Client client, AttackHandler attackHandler, DukeSucellusConfig config, ModelOutlineRenderer modelOutlineRenderer)
    {
        this.client = client;
        this.attackHandler = attackHandler;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (attackHandler.getDukeBoss() == null)
        {
            return null;
        }
        if (attackHandler.getNextAttack() == null)
        {
            return null;
        }
        if (attackHandler.getAttackCounter() == null)
        {
            return null;
        }

        if (attackHandler.getDukeBoss().getId() == 12191) //fighting
        {
            if (!attackHandler.over25HP())
            {
                if (config.enrageHighlight())
                {
                    modelOutlineRenderer.drawOutline(attackHandler.getDukeBoss(), 2, Color.RED, 2);
                }

            }
        }

        NPC dukeBoss = attackHandler.getDukeBoss();
        NextAttack nextAttack = attackHandler.getNextAttack();
        String nextAttackString = "Next: " + nextAttack.toString();
        int attackCounter = attackHandler.getAttackCounter();
        String attackCounterString = String.valueOf(attackCounter);
        Color color;
        switch (attackCounter)
        {
            case 1:
                color = Color.orange;
                break;
            case 0:
                color = Color.RED;
                break;
            default:
                color = config.counterTextColor();
                break;
        }
        int counterSize = config.counterSize();
        int counterHeight = config.counterHeight();
        Point canvasPoint = dukeBoss.getCanvasTextLocation(graphics, attackCounterString, counterHeight);
        Point canvasPoint2 = new Point((canvasPoint.getX() - 28), (canvasPoint.getY() - 14));
        renderTextLocation(graphics, attackCounterString, counterSize, Font.BOLD, color, canvasPoint);
        renderTextLocation(graphics, nextAttackString, counterSize, Font.BOLD, color, canvasPoint2);

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
}