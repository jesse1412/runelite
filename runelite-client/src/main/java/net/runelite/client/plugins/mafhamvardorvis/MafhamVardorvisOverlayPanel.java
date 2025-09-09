package net.runelite.client.plugins.mafhamvardorvis;

import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MafhamVardorvisOverlayPanel extends Overlay {

    @Inject
    private Client client;
    @Inject
    private MafhamVardorvisPlugin plugin;
    @Inject
    private MafhamVardorvisConfig config;
    @Inject
    private SpriteManager spriteManager;
    private final PanelComponent prayAgainstPanel = new PanelComponent();
    private int scale = 40;

    @Inject
    private MafhamVardorvisOverlayPanel(Client client, MafhamVardorvisPlugin plugin, SpriteManager spriteManager, MafhamVardorvisConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;
        this.setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        this.prayAgainstPanel.getChildren().clear();
        if (!config.showRangeOverlay())
        {
            return null;
        }
        if (plugin.getHeadTimer() == null)
        {
            return null;
        }
        if (plugin.getHeadTimer() > -1)
        {
            InfoBoxComponent prayComponent = new InfoBoxComponent();
            BufferedImage prayImg = ImageUtil.resizeImage(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0), scale, scale);
            prayComponent.setImage(prayImg);
            prayComponent.setColor(Color.WHITE);
            prayComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
            prayComponent.setPreferredSize(new Dimension(scale + 4, scale + 4));
            this.prayAgainstPanel.getChildren().add(prayComponent);

            this.prayAgainstPanel.setPreferredSize(new Dimension(scale + 4, scale + 4));
            this.prayAgainstPanel.setBorder(new Rectangle(0, 0, 0, 0));
            return this.prayAgainstPanel.render(graphics);
        }
        return null;
    }
}