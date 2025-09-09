package net.runelite.client.plugins.mafhamcox.tekton;

import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class Tekton {

    @Inject
    private Client client;
    @Inject
    private TektonOverlay overlay;
    @Inject
    private MafhamCoxConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Getter
    private NPC tekton;
    private boolean seekingTarget = false;
    @Getter
    private Integer seekingCounter;
    @Getter
    private Actor target;
    private final List<Integer> tektonIDS = Arrays.asList(7540,7541,7542,7543,7545,7544);

    public void startUp()
    {
        overlayManager.add(overlay);
    }

    public void shutDown()
    {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        tekton = null;
        seekingTarget = false;
        target = null;
        seekingCounter = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (tektonIDS.contains(npcSpawned.getNpc().getId()))
        {
            tekton = npcSpawned.getNpc();
            seekingCounter = 17;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc() == tekton)
        {
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (tekton == null)
        {
            return;
        }
        if (tekton.isInteracting())
        {
            seekingTarget = true;
            target = tekton.getInteracting();
        }
        else
        {
            seekingTarget = false;
            target = null;
            seekingCounter = 17;
        }
        if (seekingTarget && seekingCounter != null && seekingCounter > -1)
        {
            seekingCounter--;
        }
    }


}