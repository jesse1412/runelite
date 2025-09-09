package net.runelite.client.plugins.mafhamcox.vasa;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class Vasa {

    @Inject
    private Client client;
    @Inject
    private VasaOverlay overlay;
    @Inject
    private MafhamCoxConfig mafhamCoxConfig;
    @Inject
    private OverlayManager overlayManager;
    @Getter
    private Integer vasaTimer;
    private boolean runTimer = false;
    private final int GLOWING_CRYSTAL_NPCID = 7568;
    private final int VASA_HEALING_NPCID = 7567;
    private final int VASA_ATTACKING_NPCID = 7566;
    private final int mageProjectileID = 1327;
    @Getter
    private NPC vasaNPC;

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
        vasaNPC = null;
        vasaTimer = null;
        runTimer = false;
    }

    //running this just in case the timer messes up somehow, it should fix it
    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        if (vasaNPC == null)
        {
            return;
        }
        if (event.getProjectile().getId() == mageProjectileID)
        {
            vasaTimer = null;
            runTimer = false;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (npcChanged.getNpc().getId() == VASA_ATTACKING_NPCID)
        {
            vasaNPC = npcChanged.getNpc();
        }
        if (npcChanged.getNpc().getId() == VASA_HEALING_NPCID)
        {
            vasaNPC = npcChanged.getNpc();
            runTimer = true;
            if (vasaTimer == null || vasaTimer == 0)
            {
                vasaTimer = 67;
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc().getId() == GLOWING_CRYSTAL_NPCID)
        {
            runTimer = false;
        }
        if (npcDespawned.getNpc().getId() == VASA_ATTACKING_NPCID || npcDespawned.getNpc().getId() == VASA_HEALING_NPCID)
        {
            vasaNPC = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (vasaNPC == null)
        {
            return;
        }
        if (!runTimer)
        {
            return;
        }
        if (vasaTimer == null)
        {
            return;
        }
        if (vasaTimer < 1)
        {
            return;
        }
        vasaTimer--;
    }

}