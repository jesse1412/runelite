package net.runelite.client.plugins.mafhamtoa.Akkha;

import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Point;
import net.runelite.api.widgets.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.plugins.mafhamtoa.MafhamToAPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;

public class TombsTilesOverlay extends Overlay {

    private final Client client;
    private final MafhamToAConfig config;
    @Inject
    private ConfigManager configManager;
    private MafhamToAPlugin plugin;

    private Widget picker = null;

    @Inject
    private TombsTilesOverlay(Client client, MafhamToAConfig config, MafhamToAPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        removePickerWidget();
        for (int mapRegion : client.getMapRegions()) {
            if (mapRegion != 14676) {
                return null;
            }
        }
        if (client.getLocalPlayer() == null)
        {
            return null;
        }
        if (config.akkhaSetting() == MafhamToAConfig.AkkhaSetting.Off)
        {
            return null;
        }
        addPickerWidget();
        if (plugin.isCumStarted())
        {
            return null;
        }
        if (config.akkhaSetting() == MafhamToAConfig.AkkhaSetting.FourTick)
        {
            for (TombsTiles tile : TombsTiles.values())
            {
                if (tile.getGroup() != 0)
                {
                   continue;
                }
                WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                for (WorldPoint worldPoint1 : worldPointCollection)
                {
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint1);
                    if (localPoint == null)
                    {
                        return null;
                    }
                    final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    if (poly != null) {
                        graphics.setStroke(new BasicStroke(1));
                        graphics.setColor(tile.getColor());
                        graphics.drawPolygon(poly);
                    }
                    if (!Strings.isNullOrEmpty(tile.getLabel()))
                    {
                        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, tile.getLabel(), 0);
                        if (canvasTextLocation != null)
                        {
                            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, tile.getLabel(), tile.getColor());
                        }
                    }
                }
            }
        }
        if (config.akkhaSetting() == MafhamToAConfig.AkkhaSetting.FiveTick)
        {
            for (TombsTiles tile : TombsTiles.values())
            {
                if (tile.getGroup() != 1)
                {
                    continue;
                }
                WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                for (WorldPoint worldPoint1 : worldPointCollection)
                {
                    LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint1);
                    if (localPoint == null)
                    {
                        return null;
                    }
                    final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
                    if (poly != null) {
                        graphics.setStroke(new BasicStroke(1));
                        graphics.setColor(tile.getColor());
                        graphics.drawPolygon(poly);
                    }
                }
            }
        }
        return null;
    }

    private void addPickerWidget()
    {
        removePickerWidget();

        int x = 10, y = 2;
        Widget parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
        if (parent == null)
        {
            Widget[] roots = client.getWidgetRoots();

            parent = Stream.of(roots)
                    .filter(w -> w.getType() == WidgetType.LAYER && w.getContentType() == 0 && !w.isSelfHidden())
                    .sorted(Comparator.comparingInt((Widget w) -> w.getRelativeX() + w.getRelativeY())
                            .reversed()
                            .thenComparingInt(Widget::getId)
                            .reversed())
                    .findFirst().get();
            x = 4;
            y = 4;
        }

        picker = parent.createChild(-1, WidgetType.GRAPHIC);

        picker.setSpriteId(SpriteID.MOBILE_FINGER_ON_INTERFACE);
        picker.setOriginalWidth(15);
        picker.setOriginalHeight(17);
        picker.setOriginalX(x);
        picker.setOriginalY(y);
        picker.revalidate();
        picker.setAction(0, "Swap tile setting");
        picker.setClickMask(WidgetConfig.USE_WIDGET);
        picker.setNoClickThrough(true);
        picker.setHasListener(true);
        picker.setOnOpListener((JavaScriptCallback) ev ->
        {
            if (config.akkhaSetting() == MafhamToAConfig.AkkhaSetting.FiveTick)
            {
                configManager.setConfiguration("mafhamtoa", "akkhaSetting", MafhamToAConfig.AkkhaSetting.FourTick);
            }
            else configManager.setConfiguration("mafhamtoa", "akkhaSetting", MafhamToAConfig.AkkhaSetting.FiveTick);
        });
    }

    private void removePickerWidget()
    {
        if (picker == null)
        {
            return;
        }

        Widget parent = picker.getParent();
        if (parent == null)
        {
            return;
        }

        Widget[] children = parent.getChildren();
        if (children == null || children.length <= picker.getIndex() || children[picker.getIndex()] != picker)
        {
            return;
        }

        children[picker.getIndex()] = null;
    }
}