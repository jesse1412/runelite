package net.runelite.client.plugins.mafhamtoa.Akkha;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Renderable;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.NPC;

import javax.inject.Inject;

import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.plugins.mafhamtoa.MafhamToAPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;
import java.util.Map;
public class AkkhaOrb extends MafhamToAPlugin {
    private final static int ORB_ID = 11804;
    private final static String[] CARDINAL_DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    @Inject
    private Client client;
    @Inject
    private MafhamToAPlugin plugin;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
    private Map<NPC, Integer> npcMap = new HashMap<>();

    @Inject
    private AkkhaOrbOverlay highlightOverlay;
    @Inject
    private TombsTilesOverlay tombsTilesOverlay;

    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MafhamToAConfig.class);
    }

    @Override
    public void startUp() {
        overlayManager.add(highlightOverlay);
        hooks.registerRenderableDrawListener(drawListener);
    }


    @Override
    public void shutDown() {
        overlayManager.remove(highlightOverlay);
        highlightOverlay.finalLocals.clear();
        hooks.unregisterRenderableDrawListener(drawListener);
        npcMap.clear();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc.getId() == ORB_ID) {
            npcMap.put(npc, ORB_ID);
        }
    }

    @Subscribe
    public void onNpcDespawned (NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc.getId() == ORB_ID) {
            npcMap.remove(npc, ORB_ID);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        plugin.cumStarted = !npcMap.isEmpty();
        highlightOverlay.finalLocals.clear();
        highlightOverlay.dangerTiles.clear();
        highlightOverlay.lineTiles.clear();
        for (Map.Entry<NPC, Integer> npc : npcMap.entrySet() ) {
            int orientation = npc.getKey().getOrientation();
            WorldPoint wp = npc.getKey().getWorldLocation();

            highlightOverlay.getDirection(orientation, wp);
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI) {
        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if (npc.getId() == ORB_ID && config.hideOrbs())
            {
                return false;
            }
        }
        return true;
    }
}
