package net.runelite.client.plugins.mafhamplayerindicators;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;

public class MafhamPlayerIndicatorsOverlay extends Overlay
{

    private MafhamPlayerIndicatorsConfig config;
    @Inject
    private MafhamPlayerIndicatorsPlugin plugin;
    @Inject
    private Client client;

    @Inject
    private MafhamPlayerIndicatorsOverlay(MafhamPlayerIndicatorsPlugin plugin, MafhamPlayerIndicatorsConfig config, Client client)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.hlSetting() == MafhamPlayerIndicatorsConfig.HighlightSetting.TRUE || config.hlSetting() == MafhamPlayerIndicatorsConfig.HighlightSetting.BOTH) {
            for (LocalPoint localPoint : plugin.worldHighlights) {
                Color tileColor = config.tileColor();
                Color outlineColor = config.tileOutlineColor();
                final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                if (poly != null) {
                    graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
                    graphics.setColor(outlineColor);
                    graphics.drawPolygon(poly);
                    graphics.setColor(tileColor);
                    graphics.fillPolygon(poly);
                }
            }
        }
        if (!plugin.playerHighlights.isEmpty()) {
            for (Player player : plugin.playerHighlights) {
                final String name = Text.sanitize(player.getName());
                Point textLocation = player.getCanvasTextLocation(graphics, name, player.getLogicalHeight() + 40);
                if (textLocation != null) {
                    OverlayUtil.renderTextLocation(graphics, textLocation, name, config.textColor());
                }
            }
        }
        if (config.hlSetting() == MafhamPlayerIndicatorsConfig.HighlightSetting.TILE || config.hlSetting() == MafhamPlayerIndicatorsConfig.HighlightSetting.BOTH)
        {
            for (Player player : plugin.getLocalHighlights()) {
                LocalPoint localPoint = player.getLocalLocation();
                Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                Color tileColor = config.tileColor();
                Color outlineColor = config.tileOutlineColor();
                if (poly != null)
                {
                    graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
                    graphics.setColor(outlineColor);
                    graphics.drawPolygon(poly);
                    graphics.setColor(tileColor);
                    graphics.fillPolygon(poly);
                }
            }
        }
        return null;
    }
}