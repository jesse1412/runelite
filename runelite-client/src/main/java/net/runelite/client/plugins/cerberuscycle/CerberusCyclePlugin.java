package net.runelite.client.plugins.cerberuscycle;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Deque;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Cerberus Cycle",
        description = "Show Cerberus' attack cycle",
        tags = {"cerberus", "cycle", "attack"}
)

public class CerberusCyclePlugin extends Plugin {

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    CerberusCycleOverlay overlay;
    @Provides
    CerberusCycleConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CerberusCycleConfig.class);
    }
    @Getter(AccessLevel.PACKAGE)
    private NPC cerberusNPC;
    @Getter(AccessLevel.PACKAGE)
    private boolean runCerberus = false;
    private static final int meleeAttackAnim = 4491;
    private static final int rangeAttackAnim = 4490;
    private static final int ghostsAnim = 4494;
    private static final int grrAnim = 4493;
    private static final int mageAttackProj = 1242;
    //private static final int rangeAttackProj = 1245;
    private int projStartCycle = 0;
    public int attackCycle = 1;
    private int attackTick = 0;
    @Getter(AccessLevel.PACKAGE)
    private int tickCounter = 0;
    private boolean tripleAttack = false;


    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        attackCycle = 1;
        attackTick = 0;
        projStartCycle = 0;
        tickCounter = 0;
        runCerberus = false;
        tripleAttack = false;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        if (event.getNpc().getId() == 5862) //cerb risen
        {
            cerberusNPC = event.getNpc();
            runCerberus = true;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event)
    {
        if (event.getNpc().getId() == 5862) //cerb risen
        {
            cerberusNPC = event.getNpc();
            runCerberus = true;
        }
        if (event.getNpc().getId() == 5863) //cerb back on throne
        {
            reset();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (event.getNpc().getId() == 5863 || event.getNpc().getId() == 5862) //either cerb version
        {
            if (runCerberus)
            {
                runCerberus = false;
                reset();
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (!runCerberus) {
            return;
        }
        handleProjectiles(client.getProjectiles());
        int animation = cerberusNPC.getAnimation();
        int currentTick = client.getTickCount();
        if (animation == meleeAttackAnim)
        {
            tripleAttack = false;
            if (currentTick > attackTick + 5) //wait 5 ticks before checking again
                {
                    incrementCounter();
                    tickCounter = 6;
                    attackTick = client.getTickCount();
                }
        }
        if (animation == rangeAttackAnim)
        {
            if (tripleAttack) //are we in a triple attack?
            {
                if (tickCounter == 0)
                {
                    tickCounter = 3;
                }
                decrementTickCounter();
                return;
            }
            if (currentTick > attackTick + 5) //wait 5 ticks before checking again
            {
                incrementCounter();
                tickCounter = 6;
                attackTick = client.getTickCount();
            }
        }
        if (animation == ghostsAnim || animation == grrAnim)
        {
            if (currentTick > attackTick + 5) //wait 5 ticks before checking again
            {
                incrementCounter();
                tickCounter = 8;
                attackTick = client.getTickCount();
            }
        }
        decrementTickCounter();
    }

    private void incrementCounter()
    {
        if (attackCycle == 28) //reset to 1 when we finish the entire cycle
        {
            attackCycle = 1;
        }
        else
        {
            attackCycle++;
        }
    }

    private void decrementTickCounter()
    {
        tickCounter--;
        if (tickCounter < 0) //don't go below 0
        {
            tickCounter = 0;
        }
    }

    private void handleProjectiles(Deque<Projectile> projectiles)
    {
        for (Projectile projectile : projectiles)
        {
            if (projectile.getId() != mageAttackProj)
            {
                return;
            }
            projStartCycle = projectile.getStartCycle();
            if (client.getGameCycle() < projStartCycle) {
                if (attackCycle == 1 || attackCycle == 11 || attackCycle == 21) //triple attacks
                {
                    tripleAttack = true;
                    tickCounter = 3;
                }
                else {
                    incrementCounter();
                    attackTick = client.getTickCount();
                    tickCounter = 6;
                }
            }
        }
    }
}