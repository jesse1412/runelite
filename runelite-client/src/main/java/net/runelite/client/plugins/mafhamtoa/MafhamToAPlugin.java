package net.runelite.client.plugins.mafhamtoa;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.mafhamtoa.Akkha.*;
import net.runelite.client.plugins.mafhamtoa.Baba.BabaCounter;
import net.runelite.client.plugins.mafhamtoa.Kephri.KephriCounter;
import net.runelite.client.plugins.mafhamtoa.Kephri.SoldierScarab;
import net.runelite.client.plugins.mafhamtoa.KephriPuzzle.KephriPuzzle;
import net.runelite.client.plugins.mafhamtoa.Mirror.MirrorRoom;
import net.runelite.client.plugins.mafhamtoa.Monkey.MonkeyWaves;
import net.runelite.client.plugins.mafhamtoa.Monkey.PathOfApmeken;
import net.runelite.client.plugins.mafhamtoa.Palm.PalmIndicators;
import net.runelite.client.plugins.mafhamtoa.Util.MessageUpdate;
import net.runelite.client.plugins.mafhamtoa.Wardens.WardensCoreTimer;
import net.runelite.client.plugins.mafhamtoa.Wardens.WardensTileFlip;
import net.runelite.client.plugins.mafhamtoa.Zebak.ZebakBlood;
import net.runelite.client.plugins.mafhamtoa.Zebak.ZebakBoulders;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Mafham ToA",
        description = "Utilities for the Tombs of Amascut.",
        tags = {"toa", "raid", "3", "akkha", "zebak", "kephri", ""}
)
public class MafhamToAPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ZebakBoulders zebakBoulders;
    @Inject
    private AkkhaMemory akkhaMemory;
    @Inject
    private AkkhaOrb akkhaOrb;
    @Inject
    private AkkhaTickCounter akkhaTickCounter;
    @Inject
    private MirrorRoom mirrorRoom;
    @Inject
    private PathOfApmeken pathOfApmeken;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TombsTilesOverlay tombsTilesOverlay;
    @Inject
    private AkkhaMemorySkip akkhaMemorySkip;
    @Inject
    private AkkhaDetonate akkhaDetonate;
    @Inject
    private KephriCounter kephriCounter;
    @Inject
    private PalmIndicators palmIndicators;
    @Inject
    private WardensTileFlip wardensTileFlip;
    @Inject
    private WardensCoreTimer wardensCoreTimer;
    @Inject
    private AkkhaShadowTimer akkhaShadowTimer;
    @Inject
    private BabaCounter babaCounter;
    @Inject
    private MonkeyWaves monkeyWaves;
    @Inject
    private ZebakBlood zebakBlood;
    @Inject
    private AkkhaEnrageLine akkhaEnrageLine;
    @Inject
    private KephriPuzzle kephriPuzzle;
    @Inject
    private SoldierScarab soldierScarab;
    @Inject
    private WSClient wsClient;
    @Getter
    @Setter
    public boolean cumStarted = false;

    private static final List<Integer> TOA_REGION_IDS = Arrays.asList(
            14160, //NEUXS
            15698, //CRONDIS
            15700, //ZEBAK
            14162, //SCABARAS
            14164, //KEPHRI
            15186, //APMEKEN
            15188, //BABA
            14674, //HET
            14676, //AKKHA
            15184, //WARDENS 1
            15696, //WARDENS 2
            14672 //TOMB
    );

    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamToAConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        wsClient.registerMessage(MessageUpdate.class);
        zebakBoulders.startUp();
        akkhaMemory.startUp();
        akkhaMemorySkip.startUp();
        akkhaOrb.startUp();
        mirrorRoom.startUp();
        pathOfApmeken.startUp();
        akkhaTickCounter.startUp();
        akkhaDetonate.startUp();
        kephriCounter.startUp();
        palmIndicators.startUp();
        wardensTileFlip.startUp();
        wardensCoreTimer.startUp();
        akkhaShadowTimer.startUp();
        babaCounter.startUp();
        monkeyWaves.startUp();
        zebakBlood.startUp();
        akkhaEnrageLine.startUp();
        kephriPuzzle.startUp();
        soldierScarab.startUp();
        overlayManager.add(tombsTilesOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        wsClient.unregisterMessage(MessageUpdate.class);
        zebakBoulders.shutDown();
        akkhaMemory.shutDown();
        akkhaMemorySkip.shutDown();
        akkhaOrb.shutDown();
        mirrorRoom.shutDown();
        pathOfApmeken.shutDown();
        akkhaTickCounter.shutDown();
        akkhaDetonate.shutDown();
        kephriCounter.shutDown();
        palmIndicators.shutDown();
        wardensTileFlip.shutDown();
        wardensCoreTimer.shutDown();
        akkhaShadowTimer.shutDown();
        babaCounter.shutDown();
        monkeyWaves.shutDown();
        zebakBlood.shutDown();
        akkhaEnrageLine.shutDown();
        kephriPuzzle.shutDown();
        soldierScarab.shutDown();
        overlayManager.remove(tombsTilesOverlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        kephriCounter.onGameStateChanged(gameStateChanged);
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        if (!isPlayerInToa()) {
            return;
        }
        kephriCounter.onStatChanged(statChanged);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        if (!isPlayerInToa()) {
            return;
        }
        babaCounter.onVarbitChanged(varbitChanged);
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        if (!isPlayerInToa()) {
            return;
        }
        babaCounter.onHitsplatApplied(hitsplatApplied);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (!isPlayerInToa()) {
            return;
        }
        akkhaMemorySkip.onNpcSpawned(npcSpawned);
        zebakBoulders.onNpcSpawned(npcSpawned);
        akkhaMemory.onNpcSpawned(npcSpawned);
        akkhaOrb.onNpcSpawned(npcSpawned);
        akkhaTickCounter.onNPCSpawned(npcSpawned);
        kephriCounter.onNpcSpawned(npcSpawned);
        wardensTileFlip.onNpcSpawned(npcSpawned);
        wardensCoreTimer.onNpcSpawned(npcSpawned);
        akkhaShadowTimer.onNpcSpawned(npcSpawned);
        babaCounter.onNpcSpawned(npcSpawned);
        monkeyWaves.onNpcSpawned(npcSpawned);
        soldierScarab.onNpcSpawned(npcSpawned);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (!isPlayerInToa()) {
            return;
        }
        zebakBoulders.onNpcDespawned(npcDespawned);
        akkhaOrb.onNpcDespawned(npcDespawned);
        mirrorRoom.onNpcDespawned(npcDespawned);
        akkhaTickCounter.onNPCDespawned(npcDespawned);
        kephriCounter.onNpcDespawned(npcDespawned);
        wardensCoreTimer.onNpcDespawned(npcDespawned);
        akkhaShadowTimer.onNpcDespawned(npcDespawned);
        babaCounter.onNpcDespawned(npcDespawned);
        soldierScarab.onNpcDespawned(npcDespawned);
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved)
    {
        if (!isPlayerInToa()) {
            return;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (!isPlayerInToa()) {
            return;
        }
        akkhaMemorySkip.onGameObjectSpawned(gameObjectSpawned);
        akkhaMemory.onGameObjectSpawned(gameObjectSpawned);
        mirrorRoom.onGameObjectSpawned(gameObjectSpawned);
        pathOfApmeken.onGameObjectSpawned(gameObjectSpawned);
        zebakBoulders.onGameObjectSpawned(gameObjectSpawned);
        palmIndicators.onGameObjectSpawned(gameObjectSpawned);
        akkhaDetonate.onGameObjectSpawned(gameObjectSpawned);
        babaCounter.onGameObjectSpawned(gameObjectSpawned);
        wardensCoreTimer.onGameObjectSpawned(gameObjectSpawned);
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        if (!isPlayerInToa()) {
            return;
        }
        zebakBoulders.onGameObjectDespawned(gameObjectDespawned);
        pathOfApmeken.onGameObjectDespawned(gameObjectDespawned);
        palmIndicators.onGameObjectDespawned(gameObjectDespawned);
        akkhaDetonate.onGameObjectDespawned(gameObjectDespawned);
        babaCounter.onGameObjectDespawned(gameObjectDespawned);
        wardensCoreTimer.onGameObjectDespawned(gameObjectDespawned);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        if (!isPlayerInToa()) {
            return;
        }
        pathOfApmeken.onGroundObjectSpawned(event);
        kephriPuzzle.onGroundObjectSpawned(event);
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        if (!isPlayerInToa()) {
            return;
        }
        pathOfApmeken.onGroundObjectDespawned(event);
    }

    @Subscribe
    private void onMessageUpdate(MessageUpdate message)
    {
        if (!isPlayerInToa()) {
            return;
        }
        pathOfApmeken.onMessageUpdate(message);
        kephriCounter.onMessageUpdate(message);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        akkhaDetonate.onChatMessage(chatMessage);
        pathOfApmeken.onChatMessage(chatMessage);
        mirrorRoom.onChatMessage(chatMessage);
        zebakBoulders.onChatMessage(chatMessage);
        akkhaTickCounter.onChatMessage(chatMessage);
        kephriCounter.onChatMessage(chatMessage);
        palmIndicators.onChatMessage(chatMessage);
        wardensCoreTimer.onChatMessage(chatMessage);
        wardensTileFlip.onChatMessage(chatMessage);
        akkhaShadowTimer.onChatMessage(chatMessage);
        babaCounter.onChatMessage(chatMessage);
        monkeyWaves.onChatMessage(chatMessage);
        zebakBlood.onChatMessage(chatMessage);
        akkhaEnrageLine.onChatMessage(chatMessage);
        kephriPuzzle.onChatMessage(chatMessage);
        soldierScarab.onChatMessage(chatMessage);
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
    {
        if (!isPlayerInToa()) {
            return;
        }
        akkhaDetonate.onGraphicsObjectCreated(graphicsObjectCreated);
        akkhaMemory.onGraphicsObjectCreated(graphicsObjectCreated);
        akkhaMemorySkip.onGraphicsObjectCreated(graphicsObjectCreated);
        palmIndicators.onGraphicsObjectCreated(graphicsObjectCreated);
        babaCounter.onGraphicsObjectCreated(graphicsObjectCreated);
        zebakBlood.onGraphicsObjectCreated(graphicsObjectCreated);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        //NPE upon login/hop without this
        if (client.getLocalPlayer() == null)
        {
            return;
        }
        if (!isPlayerInToa()) {
            return;
        }
        babaCounter.onAnimationChanged(animationChanged);
        kephriCounter.onAnimationChanged(animationChanged);
        soldierScarab.onAnimationChanged(animationChanged);
    }

    @Subscribe
    public void onGameTick (GameTick gameTick)
    {
        if (!isPlayerInToa()) {
            return;
        }
        akkhaDetonate.onGameTick(gameTick);
        akkhaMemorySkip.onGameTick(gameTick);
        zebakBoulders.onGameTick(gameTick);
        akkhaMemory.onGameTick(gameTick);
        akkhaOrb.onGameTick(gameTick);
        mirrorRoom.onGameTick(gameTick);
        akkhaTickCounter.onGameTick(gameTick);
        kephriCounter.onGameTick(gameTick);
        palmIndicators.onGameTick(gameTick);
        wardensTileFlip.onGameTick(gameTick);
        wardensCoreTimer.onGameTick(gameTick);
        akkhaShadowTimer.onGameTick(gameTick);
        babaCounter.onGameTick(gameTick);
        monkeyWaves.onGameTick(gameTick);
        zebakBlood.onGameTick(gameTick);
        akkhaEnrageLine.onGameTick(gameTick);
        soldierScarab.onGameTick(gameTick);
    }

    @Subscribe
    public void onNpcChanged (NpcChanged npcChanged)
    {
        if (!isPlayerInToa()) {
            return;
        }
        zebakBoulders.onNpcChanged(npcChanged);
        mirrorRoom.onNpcChanged(npcChanged);
        kephriCounter.onNpcChanged(npcChanged);
        wardensCoreTimer.onNpcChanged(npcChanged);
        babaCounter.onNpcChanged(npcChanged);
        akkhaEnrageLine.onNpcChanged(npcChanged);
    }

    private boolean isPlayerInToa()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (TOA_REGION_IDS.contains(mapRegion))
            {
                return true;
            }
        }
        return false;
    }
}
