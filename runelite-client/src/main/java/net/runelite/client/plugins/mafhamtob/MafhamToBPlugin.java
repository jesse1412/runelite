package net.runelite.client.plugins.mafhamtob;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.mafhamtob.Bloat.Bloat;
import net.runelite.client.plugins.mafhamtob.Maiden.Maiden;
import net.runelite.client.plugins.mafhamtob.Nylocas.Nylocas;
import net.runelite.client.plugins.mafhamtob.Sotetseg.Sotetseg;
import net.runelite.client.plugins.mafhamtob.Verzik.Verzik;
import net.runelite.client.plugins.mafhamtob.Xarpus.Xarpus;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = "Mafham ToB",
        description = "Utilities for the Theatre of Blood.",
        tags = {"tob", "raid", "2", "maiden", "bloat", "nylo", "xarpus", "verzik"}
)
public class MafhamToBPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private Maiden maiden;
    @Inject
    private Bloat bloat;
    @Inject
    private Nylocas nylocas;
    @Inject
    private Sotetseg sotetseg;
    @Inject
    private Xarpus xarpus;
    @Inject
    private Verzik verzik;

    @Provides
    MafhamToBConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamToBConfig.class);
    }

    private static final List<Integer> TOB_REGION_IDS = Arrays.asList(
            12869, //MAIDEN 1
            12613, //MAIDEN 2
            13125, //BLOAT
            13122, //NYLO
            13123, //SOTE OVERWORLD
            13379, //SOTE UNDERWORLD
            12612, //XARPUS
            12611, //VERZIK
            12867 //THRONE ROOM
    );

    @Override
    protected void startUp() throws Exception {
        maiden.startUp();
        bloat.startUp();
        nylocas.startUp();
        sotetseg.startUp();
        xarpus.startUp();
        verzik.startUp();
    }

    @Override
    protected void shutDown() throws Exception {
        maiden.shutDown();
        bloat.shutDown();
        nylocas.shutDown();
        sotetseg.shutDown();
        xarpus.shutDown();
        verzik.shutDown();
    }

    private void reset() {
        maiden.reset();
        bloat.reset();
        nylocas.reset();
        sotetseg.reset();
        xarpus.reset();
        verzik.reset();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        if (!isPlayerInToB()) {
            return;
        }
        maiden.onHitsplatApplied(hitsplatApplied);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        boolean outsideToB = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 14642);
        if (outsideToB) {
            reset();
            return;
        }
        if (!isPlayerInToB()) {
            return;
        }
        maiden.onGameTick(gameTick);
        bloat.onGameTick(gameTick);
        nylocas.onGameTick(gameTick);
        sotetseg.onGameTick(gameTick);
        xarpus.onGameTick(gameTick);
        verzik.onGameTick(gameTick);
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved)
    {
        if (!isPlayerInToB()) {
            return;
        }
        sotetseg.onProjectileMoved(projectileMoved);
        verzik.onProjectileMoved(projectileMoved);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
    {
        if (!isPlayerInToB()) {
            return;
        }
        nylocas.onMenuOptionClicked(menuOptionClicked);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded entry)
    {
        if (!isPlayerInToB()) {
            return;
        }
        nylocas.onMenuEntryAdded(entry);
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        if (!isPlayerInToB()) {
            return;
        }
        sotetseg.onClientTick(clientTick);
        verzik.onClientTick(clientTick);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned groundObjectSpawned)
    {
        if (!isPlayerInToB()) {
            return;
        }
        sotetseg.onGroundObjectSpawned(groundObjectSpawned);
        xarpus.onGroundObjectSpawned(groundObjectSpawned);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (!isPlayerInToB()) {
            return;
        }
        bloat.onGameObjectSpawned(gameObjectSpawned);
        nylocas.onGameObjectSpawned(gameObjectSpawned);
        verzik.onGameObjectSpawned(gameObjectSpawned);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (!isPlayerInToB()) {
            return;
        }
        maiden.onNpcSpawned(npcSpawned);
        bloat.onNpcSpawned(npcSpawned);
        nylocas.onNpcSpawned(npcSpawned);
        sotetseg.onNpcSpawned(npcSpawned);
        xarpus.onNpcSpawned(npcSpawned);
        verzik.onNpcSpawned(npcSpawned);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (!isPlayerInToB()) {
            return;
        }
        maiden.onNpcDespawned(npcDespawned);
        bloat.onNpcDespawned(npcDespawned);
        nylocas.onNpcDespawned(npcDespawned);
        sotetseg.onNpcDespawned(npcDespawned);
        xarpus.onNpcDespawned(npcDespawned);
        verzik.onNpcDespawned(npcDespawned);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (!isPlayerInToB()) {
            return;
        }
        nylocas.onNpcChanged(npcChanged);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (!isPlayerInToB()) {
            return;
        }
        maiden.onChatMessage(chatMessage);
        bloat.onChatMessage(chatMessage);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        maiden.onAnimationChanged(animationChanged);
        bloat.onAnimationChanged(animationChanged);
        nylocas.onAnimationChanged(animationChanged);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        bloat.onGameStateChanged(gameStateChanged);
        nylocas.onGameStateChanged(gameStateChanged);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) throws Exception {
        if (configChanged.getKey().equals("hidePillar")) {
            bloat.shutDown();
            bloat.startUp();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        if (!isPlayerInToB()) {
            return;
        }
        nylocas.onVarbitChanged(varbitChanged);
    }

    private boolean isPlayerInToB()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (TOB_REGION_IDS.contains(mapRegion))
            {
                return true;
            }
        }
        return false;
    }
}