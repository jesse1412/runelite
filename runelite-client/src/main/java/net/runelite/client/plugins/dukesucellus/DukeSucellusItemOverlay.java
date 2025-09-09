package net.runelite.client.plugins.dukesucellus;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class DukeSucellusItemOverlay extends WidgetItemOverlay {
    @Inject
    private Client client;
    private final DukeSucellusPlugin plugin;
    @Inject
    public DukeSucellusItemOverlay(Client client, DukeSucellusPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        if (!plugin.isDukeRoom() || itemId != 25975 || plugin.isDukeFight() || client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000) {
            return;
        }
        drawItem(graphics2D, widgetItem.getCanvasBounds());
    }

    public void drawItem(Graphics2D graphics2D, Rectangle bounds) {
        Color colour = plugin.shouldEquipLightbearer() ? Color.GREEN : Color.RED;

        graphics2D.setColor(new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 75));
        graphics2D.fillRect((int) bounds.getX(), (int) bounds.getY(), bounds.width, bounds.height);
    }
}
