package net.runelite.client.plugins.mafhamtoa.Monkey;

import net.runelite.api.Client;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class PathofApmekenOverlayPanel extends OverlayPanel {

    private PathOfApmeken plugin;
    private final Client client;
    private final MafhamToAConfig config;

    @Inject
    private PathofApmekenOverlayPanel(PathOfApmeken plugin, Client client, MafhamToAConfig config)
    {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showMonkeyPanel()) {
            return null;
        }
        for (int mapRegion : client.getMapRegions()) {
            if (mapRegion != 15186) {
                return null;
            }
        }
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Sight: ")
                    .right(plugin.getCurrentSight())
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Issue: ")
                    .right(plugin.getCurrentIssue())
                    .build());

        return super.render(graphics);
    }
}