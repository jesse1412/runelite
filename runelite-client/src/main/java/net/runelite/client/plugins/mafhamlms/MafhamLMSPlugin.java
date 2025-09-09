package net.runelite.client.plugins.mafhamlms;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.prayer.PrayerConfig;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static net.runelite.api.widgets.WidgetConfig.DRAG;
import static net.runelite.api.widgets.WidgetConfig.DRAG_ON;

@PluginDescriptor(
        name = "Mafham LMS",
        description = "Mafham LMS",
        tags = {"Mafham", "LMS"}
)
public class MafhamLMSPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamLMSOverlay mafhamLMSOverlay;
    @Inject
    private MafhamLMSOverlayMarker mafhamLMSOverlayMarker;
    @Inject
    private MafhamLMSConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private Hooks hooks;
    @Provides
    MafhamLMSConfig getConfig(ConfigManager configManager){return configManager.getConfig(MafhamLMSConfig.class);}

    private static final WorldArea lmsCompetitiveLobby = new WorldArea(3139, 3632, 6, 7, 0);
    private boolean lmsPlayed = false;
    private static final int PRAYER_X_OFFSET = 37;
    private static final int PRAYER_Y_OFFSET = 37;
    private static final int QUICK_PRAYER_SPRITE_X_OFFSET = 2;
    private static final int QUICK_PRAYER_SPRITE_Y_OFFSET = 2;
    private static final int PRAYER_COLUMN_COUNT = 5;
    private int[] storedPrayerOrder;
    private final int[] maxMedPrayerOrder = {22, 10, 9, 15, 21, 3, 8, 28, 7, 6, 4, 11, 23, 17, 1, 20, 12, 13, 14, 0, 19, 26, 24, 27, 5, 25, 18, 16, 2};
    private final int[] oneDefPurePrayerOrder = {24, 26, 9, 15, 21, 3, 8, 28, 7, 6, 4, 11, 27, 17, 1, 20, 12, 13, 14, 0, 19, 10, 22, 23, 5, 25, 18, 16, 2};
    private final int[] oldmaxMedPrayerOrder = {0, 1, 2, 18, 19, 3, 4, 5, 6, 7, 8, 20, 21, 9, 10, 11, 12, 13, 14, 22, 23, 15, 16, 17, 28, 25, 26, 24, 27};
    private final int[] oldoneDefPurePrayerOrder = {0, 1, 2, 18, 19, 3, 4, 5, 6, 7, 8, 20, 21, 9, 26, 11, 12, 13, 14, 24, 27, 15, 16, 17, 28, 25, 10, 22, 23};
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;


    @Override
    protected void startUp() throws Exception {
        hooks.registerRenderableDrawListener(drawListener);
        overlayManager.add(mafhamLMSOverlay);
        overlayManager.add(mafhamLMSOverlayMarker);
    }

    @Override
    protected void shutDown() throws Exception {
        hooks.unregisterRenderableDrawListener(drawListener);
        overlayManager.remove(mafhamLMSOverlay);
        overlayManager.remove(mafhamLMSOverlayMarker);
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof Player) {
            Player player = (Player) renderable;
            Player local = client.getLocalPlayer();
            if (player == local && inLMSGame() && config.hideAndOutline())
            {
                return drawingUI;
            }
        }
        return true;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (inLMSGame() && config.enableMetronome())
        {
            //weird preferences stuff for changing the global volume, playing tick sound then changing it back I think
            //also I think this makes it work while game is muted which is cool
            Preferences preferences = client.getPreferences();
            int previousVolume = preferences.getSoundEffectVolume();
            preferences.setSoundEffectVolume(config.tickVolume());
            client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.tickVolume());
            preferences.setSoundEffectVolume(previousVolume);
        }
        if (client.getLocalPlayer().getWorldLocation().distanceTo(lmsCompetitiveLobby) != 0)
        {
            return;
        }
        lmsPlayed = true;
        int prayerbook = client.getVarbitValue(Varbits.PRAYERBOOK);
        if (storedPrayerOrder != null && !Arrays.toString(getPrayerOrder(prayerbook)).equals(Arrays.toString(storedPrayerOrder)))
        {
            setPrayerOrder(prayerbook, storedPrayerOrder);
            rebuildPrayers(false);
            //System.out.println("prayers restored");
        }
        //System.out.println("in lobby");
        storedPrayerOrder = getPrayerOrder(prayerbook);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        final MenuEntry menuEntry = event.getMenuEntry();
        if ((menuEntry.getOption().contains("Attack") || menuEntry.getOption().contains("Fight")) && shiftModifier() && inLMSGame())
        {
            menuEntry.setDeprioritized(true);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (!lmsPlayed)
        {
            return;
        }
        int prayerbook = client.getVarbitValue(Varbits.PRAYERBOOK);
        String input = chatMessage.getMessage();
        String message = input.replaceAll("<[^>]+>", "");
        String zerkerMsg = "Last Man Standing mode: Zerker";
        String maxMedMsg = "Last Man Standing mode: Max/Med";
        String oneDefPure = "Last Man Standing mode: 1 Def Pure";
        if (message.contains(zerkerMsg))
        {
            setPrayerOrder(prayerbook, oneDefPurePrayerOrder);
            rebuildPrayers(false);
            //System.out.println("zerker");
        }
        if (message.contains(maxMedMsg))
        {
            setPrayerOrder(prayerbook, maxMedPrayerOrder);
            rebuildPrayers(false);
            //System.out.println("max med");
        }
        if (message.contains(oneDefPure))
        {
            setPrayerOrder(prayerbook, oneDefPurePrayerOrder);
            rebuildPrayers(false);
            //System.out.println("one def");
        }
    }

    private void setPrayerOrder(int prayerbook, int[] prayers)
    {
        var s = Arrays.stream(prayers)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(","));
        configManager.setConfiguration(PrayerConfig.GROUP, "prayer_order_book_" + prayerbook, s);
    }

    private int[] getPrayerOrder(int prayerbook)
    {
        var s = configManager.getConfiguration(PrayerConfig.GROUP, "prayer_order_book_" + prayerbook);
        if (s == null)
        {
            return null;
        }
        return Arrays.stream(s.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int[] defaultPrayerOrder(EnumComposition prayerEnum)
    {
        return Arrays.stream(prayerEnum.getKeys())
                .boxed() // IntStream does not accept a custom comparator
                .sorted(Comparator.comparing(id ->
                {
                    var prayerObjId = prayerEnum.getIntValue(id);
                    var prayerObj = client.getItemDefinition(prayerObjId);
                    return prayerObj.getIntValue(ParamID.OC_PRAYER_LEVEL);
                }))
                .mapToInt(i -> i)
                .toArray();
    }

    private EnumComposition getPrayerBookEnum(int prayerbook)
    {
        var enumId = prayerbook == 1 ? EnumID.PRAYERS_RUINOUS : EnumID.PRAYERS_NORMAL;
        return client.getEnum(enumId);
    }
    private boolean isInterfaceOpen(int interfaceId)
    {
        return client.getWidget(interfaceId, 0) != null;
    }
    private boolean isHidden(int prayerbook, int prayer)
    {
        Boolean b = configManager.getConfiguration(PrayerConfig.GROUP, "prayer_hidden_book_" + prayerbook + "_" + prayer, boolean.class);
        return b == Boolean.TRUE;
    }

    private void rebuildPrayers(boolean unlocked)
    {
        var prayerbook = client.getVarbitValue(Varbits.PRAYERBOOK);
        var prayerBookEnum = getPrayerBookEnum(prayerbook);
        var prayerIds = MoreObjects.firstNonNull(getPrayerOrder(prayerbook), defaultPrayerOrder(prayerBookEnum));

        if (isInterfaceOpen(WidgetID.PRAYER_GROUP_ID))
        {
            int index = 0;
            for (int prayerId : prayerIds)
            {
                var prayerObjId = prayerBookEnum.getIntValue(prayerId);
                var prayerObj = client.getItemDefinition(prayerObjId);
                var prayerWidget = client.getWidget(prayerObj.getIntValue(ParamID.OC_PRAYER_COMPONENT));

                assert prayerWidget != null;

                boolean hidden = isHidden(prayerbook, prayerId);
                // in unlocked mode we show the prayers, but they have opacity set
                if (hidden && !unlocked)
                {
                    prayerWidget.setHidden(true);
                    ++index;
                    continue;
                }

                int widgetConfig = prayerWidget.getClickMask();
                if (unlocked)
                {
                    // allow dragging of this widget
                    widgetConfig |= DRAG;
                    // allow this widget to be dragged on
                    widgetConfig |= DRAG_ON;
                }
                else
                {
                    // remove drag flag
                    widgetConfig &= ~DRAG;
                    // remove drag on flag
                    widgetConfig &= ~DRAG_ON;
                }
                prayerWidget.setClickMask(widgetConfig);

                if (unlocked)
                {
                    prayerWidget.setHidden(false);

                    if (hidden)
                    {
                        prayerWidget.setAction(3, "Unhide");
                        prayerWidget.getChild(1).setOpacity(200);
                    }
                    else
                    {
                        prayerWidget.setAction(3, "Hide");
                    }
                }
                else
                {
                    prayerWidget.setAction(3, null);
                }

                int x = index % PRAYER_COLUMN_COUNT;
                int y = index / PRAYER_COLUMN_COUNT;
                int widgetX = x * PRAYER_X_OFFSET;
                int widgetY = y * PRAYER_Y_OFFSET;

                prayerWidget.setPos(widgetX, widgetY);
                prayerWidget.revalidate();

                ++index;
            }
        }

        if (isInterfaceOpen(WidgetID.QUICK_PRAYERS_GROUP_ID))
        {
            Widget prayersContainer = client.getWidget(WidgetInfo.QUICK_PRAYER_PRAYERS);
            if (prayersContainer == null)
            {
                return;
            }

            Widget[] prayerWidgets = prayersContainer.getDynamicChildren();
            // (op targetable component)* (prayer icon, checkbox)*
            if (prayerWidgets == null || prayerWidgets.length != prayerBookEnum.size() * 3)
            {
                return;
            }

            var sortedPrayers = defaultPrayerOrder(prayerBookEnum);
            int index = 0;
            for (int prayerId : prayerIds)
            {
                int x = index % PRAYER_COLUMN_COUNT;
                int y = index / PRAYER_COLUMN_COUNT;

                Widget prayerWidget = prayerWidgets[prayerId];
                prayerWidget.setPos(x * PRAYER_X_OFFSET, y * PRAYER_Y_OFFSET);
                prayerWidget.revalidate();

                int sortedIdx = ArrayUtils.indexOf(sortedPrayers, prayerId);
                int childId = prayerBookEnum.size() + 2 * sortedIdx;

                Widget prayerSpriteWidget = prayerWidgets[childId];
                prayerSpriteWidget.setPos(
                        QUICK_PRAYER_SPRITE_X_OFFSET + x * PRAYER_X_OFFSET,
                        QUICK_PRAYER_SPRITE_Y_OFFSET + y * PRAYER_Y_OFFSET);
                prayerSpriteWidget.revalidate();

                Widget prayerToggleWidget = prayerWidgets[childId + 1];
                prayerToggleWidget.setPos(
                        x * PRAYER_X_OFFSET,
                        y * PRAYER_Y_OFFSET);
                prayerToggleWidget.revalidate();

                ++index;
            }
        }
    }

    private boolean inLMSGame()
    {
        return client.getWidget(328, 5) != null;
    }

    private boolean shiftModifier()
    {
        return client.isKeyPressed(KeyCode.KC_SHIFT);
    }
}