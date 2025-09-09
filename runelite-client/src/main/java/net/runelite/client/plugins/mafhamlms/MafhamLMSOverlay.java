package net.runelite.client.plugins.mafhamlms;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class MafhamLMSOverlay extends Overlay {

    @Inject
    private MafhamLMSPlugin plugin;
    @Inject
    private MafhamLMSConfig config;
    @Inject
    private Client client;
    @Inject
    private ModelOutlineRenderer outlineRenderer;

    @Inject
    private MafhamLMSOverlay(MafhamLMSPlugin plugin, MafhamLMSConfig config, Client client, ModelOutlineRenderer outlineRenderer)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.outlineRenderer = outlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!inLMSGame())
        {
            return null;
        }
        if (config.hideBarrage())
        {
            for (Player player :client.getPlayers())
            {
                if (player != client.getLocalPlayer())
                {
                    if (player.getGraphic() == 369)
                    {
                        player.setGraphic(-1);
                    }
                }

            }
        }
        if (config.hideAndOutline())
        {
            Player player = client.getLocalPlayer();
            outlineRenderer.drawOutline(player, 1, Color.gray, 2);
        }
        return null;
    }

    private boolean inLMSGame()
    {
        return client.getWidget(328, 5) != null;
    }
}