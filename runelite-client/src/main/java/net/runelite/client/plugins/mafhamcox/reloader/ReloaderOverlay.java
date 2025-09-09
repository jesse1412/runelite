package net.runelite.client.plugins.mafhamcox.reloader;

import java.awt.*;

import lombok.Getter;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.plugins.mafhamcox.MafhamCoxPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;

public class ReloaderOverlay extends Overlay {

    @Inject
    private MafhamCoxPlugin plugin;
    @Inject
    private MafhamCoxConfig config;
    private static final Color BUTTON_COLOR = new Color(0, 0, 0, 100);
    private static final Color BUTTON_HOVER_COLOR = new Color(255, 255, 255, 50);
    private static final Color TEXT_COLOR = new Color(255, 255, 255, 150);
    private static final Font font = new Font("Arial", Font.BOLD, 12);

    public ReloaderOverlay() {
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showReloader())
        {
            return null;
        }
        if (!plugin.isCoxLoaded)
        {
            return null;
        }
        if (!plugin.inRaid())
        {
            return null;
        }
        graphics.setColor(new Color(255,255,255,100));
        graphics.setStroke(new BasicStroke(1));
        graphics.drawRect(0,0,100,33);
        graphics.setColor(plugin.isHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
        graphics.fillRect(0, 0, 100, 33);
        graphics.setFont(font);
        graphics.setColor(TEXT_COLOR);
        graphics.setColor(new Color(255,255,255,200));
        graphics.drawString("Reload Raid", 19,21);
        plugin.clickBox = getBounds();

        return new Dimension(100, 33);
    }
}
