package net.runelite.client.plugins.mafhamtoa.Monkey;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class PathOfApmekenOverlay extends Overlay {

    private final Client client;

    private PathOfApmeken plugin;
    private final ModelOutlineRenderer outlineRenderer;

    @Inject
    private PathOfApmekenOverlay(Client client, PathOfApmeken plugin, ModelOutlineRenderer outlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.outlineRenderer = outlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.getCorruptedPlayers().isEmpty()) {
            for (Player player : plugin.getCorruptedPlayers()) {
                outlineRenderer.drawOutline(player, 2, Color.red, 2);
                renderPlayerHull(graphics, player, Color.red);
            }
        }
        if (plugin.getDoctor() != null)
        {
            outlineRenderer.drawOutline(plugin.getDoctor(), 2, Color.green, 2);
            renderPlayerHull(graphics, plugin.getDoctor(), Color.green);
        }
        if (!plugin.getHighlightedPillars().isEmpty()) {
            for (GameObject pillar : plugin.getHighlightedPillars()) {
                if (pillar != null) {
                    renderGameObject(graphics, pillar, Color.green);
                }
            }
        }
        if (!plugin.getHighlightedVents().isEmpty()) {
            for (GroundObject vent : plugin.getHighlightedVents()) {
                if (vent != null) {
                   renderGroundObject(graphics, vent, Color.green);
                }
            }
        }
        return null;
    }

    private void renderGameObject(Graphics2D graphics, GameObject gameObject, Color color) {
        Shape shape = gameObject.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.draw(shape);
            graphics.setColor(new Color(color.getRed(), color.getGreen(),color.getBlue(), 25));
            graphics.fill(shape);
        }
    }

    private void renderGroundObject(Graphics2D graphics, GroundObject groundObject, Color color) {
        Shape shape = groundObject.getConvexHull();
        if (shape != null) {
            graphics.setColor(color);
            graphics.draw(shape);
            graphics.setColor(new Color(color.getRed(), color.getGreen(),color.getBlue(), 25));
            graphics.fill(shape);
        }
    }

    private void renderPlayerHull(Graphics2D graphics, Player player, Color color) {
        Shape shape = player.getConvexHull();
        if (shape != null) {
            graphics.setColor((new Color (color.getRed(), color.getGreen(), color.getBlue(), 50)));
            graphics.fill(shape);
        }
    }
}