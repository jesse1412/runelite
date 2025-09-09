package net.runelite.client.plugins.mafhamaraxxor;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@PluginDescriptor(
        name = "Mafham Araxxor",
        description = "Mafham Araxxor",
        tags = {"Mafham", "Araxxor"}
)
public class MafhamAraxxorPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private MafhamAraxxorOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Getter
    private NPC boss;
    @Getter
    private Integer spawnTick;
    @Getter
    private Tuple enrageTick;
    @Getter
    private Integer attackTimer;
    @Getter
    private Integer attacksUntilSpecCounter;
    private final LocalPoint firstEggSpawnLocation = new LocalPoint(6656, 5888, -1);
    @Getter
    private String nextSpecialString = "Unknown";
    private final int araxxorID = 13668;
    private final int greenEggID = 13674;
    private final int whiteEggID = 13670;
    private final int redEggID = 13672;

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        boss = null;
        spawnTick = null;
        nextSpecialString = "Unknown";
        attackTimer = null;
        attacksUntilSpecCounter = null;
        enrageTick = null;
    }

    @Subscribe
    public void onGameTick (GameTick gameTick)
    {
        if (!inAraxxorRoom())
        {
            reset();
            return;
        }
        if (attackTimer != null && attackTimer > 0)
        {
            attackTimer--;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (!inAraxxorRoom())
        {
            return;
        }
        if (animationChanged.getActor() != boss)
        {
            return;
        }
        int animID = animationChanged.getActor().getAnimation();
        int defaultAttack = 11480;
        int mageAttack = 11479;
        int poisonSpray = 11478;
        int walkPoisonAttack = 11477;
        int poisonBomb = 11476;
        int enrageStart = 11488;
        int enrageStomp = 11487;
        int instantDecrementPrevention = 1; //cuz onGameTick runs immediately after this and decrements
        List<Integer> specialAttacks = List.of(poisonSpray, walkPoisonAttack, poisonBomb);

        if (animID == defaultAttack || animID == mageAttack)
        {
            attackTimer = 5 + instantDecrementPrevention;
            if (attacksUntilSpecCounter != null) //this is unnecessary
            {
                attacksUntilSpecCounter--;
            }
        }
        if (specialAttacks.contains(animID))
        {
            attackTimer = 5 + instantDecrementPrevention;
            attacksUntilSpecCounter = 6;
        }
        if (animID == enrageStart)
        {
            enrageTick = new Tuple(client.getTickCount(), 4);
            attackTimer = null;
        }
        if (animID == enrageStomp)
        {
            enrageTick = new Tuple(client.getTickCount(), 3);
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (!inAraxxorRoom())
        {
            return;
        }
        NPC npc = npcSpawned.getNpc();
        int id = npc.getId();
        LocalPoint spawnLocation = npc.getLocalLocation();

        if (id == araxxorID)
        {
            boss = npcSpawned.getNpc();
            spawnTick = client.getTickCount();
            attacksUntilSpecCounter = 6;
            attackTimer = 6;
        }

        if (Objects.equals(spawnLocation, firstEggSpawnLocation))
        {
            switch (id) {
                case redEggID:
                    nextSpecialString = "Walk";
                    break;
                case whiteEggID:
                    nextSpecialString = "Spray";
                    break;
                case greenEggID:
                    nextSpecialString = "Bomb";
                    break;
                default:
                    //unreachable
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (boss == null)
        {
            return;
        }
        if (npcDespawned.getNpc() == boss)
        {
            reset();
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (!inAraxxorRoom())
        {
            return;
        }
        if (npcChanged.getNpc() != boss)
        {
            return;
        }
        int araxxorDeadID = 13669;
        if (npcChanged.getNpc().getId() == araxxorDeadID)
        {
            reset();
        }
    }

    private boolean inAraxxorRoom()
    {
        if (client.getLocalPlayer() == null)
        {
            return false;
        }
        int araxxorMapRegionID = 14489;
        for (Integer mapID : client.getMapRegions())
        {
            if (mapID == araxxorMapRegionID)
            {
                return true;
            }
        }
        return false;
    }
}