package net.runelite.client.plugins.mafhammuspah;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.mafhamtob.Direction;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Muspah",
        description = "Mafham Muspah",
        tags = {"Mafham", "Muspah", "Phantom"}
)
public class MafhamMuspahPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamMuspahOverlay overlay;
    @Inject
    private MafhamMuspahPieOverlay pieOverlay;
    @Inject
    private MafhamMuspahConfig config;
    @Inject
    private XpDropDamageCalculator xpDropDamageCalculator;
    @Inject
    private ClientThread clientThread;
    private boolean resetXpTrackerLingerTimerFlag = false;
    private int nextAttackTick = -1;
    private static final int ATTACK_TICKS = 4;
    private int lastOpponentId = -1;
    @Getter
    private Integer totalDamage = 0;
    @Getter
    private NPC muspahNPC;
    private Integer muspahPreviousNPCID;
    @Getter
    private Integer spikeCounter;
    private static final int[] previous_exp = new int[Skill.values().length];
    private HashMap<NPC, WorldPoint> clouds = new HashMap<>();
    @Getter
    private Set<WorldPoint> cloudPoints = new HashSet<>();
    @Getter
    private Integer bossHP;
    //We start the fight with this value at 0, and increment it every time Muspah does a spec
    //This is to modify the remaining HP value until spec
    @Getter
    private Integer bossFightState = 0;
    @Getter
    private Integer cloudPhaseStartTime;

    @Provides
    MafhamMuspahConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamMuspahConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        overlayManager.add(pieOverlay);
        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invokeLater(() ->
            {
                int[] xps = client.getSkillExperiences();
                System.arraycopy(xps, 0, previous_exp, 0, previous_exp.length);
            });
        } else {
            Arrays.fill(previous_exp, 0);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        overlayManager.remove(pieOverlay);
    }

    private void reset() {
        totalDamage = 0;
        lastOpponentId = -1;
        muspahNPC = null;
        spikeCounter = null;
        muspahPreviousNPCID = null;
        nextAttackTick = -1;
        cloudPoints.clear();
        bossFightState = 0;
        cloudPhaseStartTime = null;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        if (muspahNPC == null)
        {
            return;
        }
        int rangedID = 12077;
        int meleeID = 12078;
        int teleportingID = 12082;
        int shieldedID = 12079;
        int postShieldID = 12080;
        if (muspahNPC.getId() == teleportingID || muspahNPC.getId() == shieldedID || muspahNPC.getId() == postShieldID)
        {
            return;
        }
        if (client.getVarpValue(VarPlayer.HP_HUD_NPC_ID) == rangedID || client.getVarpValue(VarPlayer.HP_HUD_NPC_ID) == meleeID)
        {
            double currentHP = client.getVarbitValue(6099);
            bossHP = (int) currentHP;
        }
    }

    @Subscribe
    protected void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.HOPPING)
        {
            Arrays.fill(previous_exp, 0);
            resetXpTrackerLingerTimerFlag = true;
        }
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN && resetXpTrackerLingerTimerFlag)
        {
            resetXpTrackerLingerTimerFlag = false;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (muspahNPC == null)
        {
            return;
        }
        Actor npc = event.getActor();
        int animID = event.getActor().getAnimation();
        if (npc != muspahNPC) {
            return;
        }
        if (animID == 9923) { //spike start anim
            spikeCounter = 53;
            bossFightState++;
        }
        if (animID == 9920) {
            nextAttackTick = client.getTickCount() + ATTACK_TICKS;
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (npcSpawned.getNpc().getId() == 12077 || npcSpawned.getNpc().getId() == 12078) {
            muspahNPC = npcSpawned.getNpc();
        }
        if (npcSpawned.getNpc().getId() == 12083) //cloudID
        {
            clouds.put(npcSpawned.getNpc(), npcSpawned.getNpc().getWorldLocation());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (npcDespawned.getNpc() == muspahNPC) {
            reset();
        }
        clouds.remove(npcDespawned.getNpc());
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged) {
        if (npcChanged.getNpc() != muspahNPC) {
            return;
        }
        if (npcChanged.getNpc().getId() == 12079 || npcChanged.getNpc().getId() == 12080) //shielded or post shield
        {
            bossHP = null;
        }
        //if muspah changes to teleport, we don't want to clear damage
        if (npcChanged.getNpc().getId() == 12082) {
            muspahPreviousNPCID = muspahNPC.getId();
            bossFightState++;
            bossHP = null;
            cloudPhaseStartTime = client.getTickCount();
            return;
        }
        //if muspah was just teleporting, we don't want to clear damage
        if (muspahPreviousNPCID != null && muspahPreviousNPCID == 12082) {
            muspahPreviousNPCID = muspahNPC.getId();
            return;
        }
        totalDamage = 0;
        muspahPreviousNPCID = muspahNPC.getId();
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (muspahNPC == null) {
            reset();
        }
        cloudPoints.clear();
        if (!clouds.isEmpty())
        {
            for (Map.Entry<NPC, WorldPoint> entry : clouds.entrySet())
            {
                NPC npc = entry.getKey();
                WorldPoint spawnPoint = entry.getValue();
                WorldPoint currentPoint = npc.getWorldLocation();
                WorldPoint tileOneAhead = null;
                WorldPoint tileTwoAhead = null;
                if (currentPoint.getY() > spawnPoint.getY() && currentPoint.getX() == spawnPoint.getX()) // NORTH
                {
                    tileOneAhead = currentPoint.dy(1);
                    tileTwoAhead = currentPoint.dy(2);
                }
                else if (currentPoint.getY() > spawnPoint.getY() && currentPoint.getX() > spawnPoint.getX()) // NORTHEAST
                {
                    tileOneAhead = currentPoint.dy(1).dx(1);
                    tileTwoAhead = currentPoint.dy(2).dx(2);
                }
                else if (currentPoint.getY() == spawnPoint.getY() && currentPoint.getX() > spawnPoint.getX()) // EAST
                {
                    tileOneAhead = currentPoint.dx(1);
                    tileTwoAhead = currentPoint.dx(2);
                }
                else if (currentPoint.getY() < spawnPoint.getY() && currentPoint.getX() > spawnPoint.getX()) // SOUTHEAST
                {
                    tileOneAhead = currentPoint.dy(-1).dx(1);
                    tileTwoAhead = currentPoint.dy(-2).dx(2);
                }
                else if (currentPoint.getY() < spawnPoint.getY() && currentPoint.getX() == spawnPoint.getX()) // SOUTH
                {
                    tileOneAhead = currentPoint.dy(-1);
                    tileTwoAhead = currentPoint.dy(-2);
                }
                else if (currentPoint.getY() < spawnPoint.getY() && currentPoint.getX() < spawnPoint.getX()) // SOUTHWEST
                {
                    tileOneAhead = currentPoint.dy(-1).dx(-1);
                    tileTwoAhead = currentPoint.dy(-2).dx(-2);
                }
                else if (currentPoint.getY() == spawnPoint.getY() && currentPoint.getX() < spawnPoint.getX()) // WEST
                {
                    tileOneAhead = currentPoint.dx(-1);
                    tileTwoAhead = currentPoint.dx(-2);
                }
                else if (currentPoint.getY() > spawnPoint.getY() && currentPoint.getX() < spawnPoint.getX()) // NORTHWEST
                {
                    tileOneAhead = currentPoint.dy(1).dx(-1);
                    tileTwoAhead = currentPoint.dy(2).dx(-2);
                }
                cloudPoints.add(tileOneAhead);
                cloudPoints.add(tileTwoAhead);
            }
        }

        if (spikeCounter != null && spikeCounter > -1) {
            spikeCounter--;
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() != client.getLocalPlayer()) {
            return;
        }

        if (muspahNPC == null) {
            return;
        }

        Actor opponent = event.getTarget();

        if (opponent instanceof NPC) {
            NPC npc = (NPC) opponent;

            lastOpponentId = npc.getId();
        } else {
            lastOpponentId = -1;
        }
    }

    @Subscribe
    protected void onStatChanged(StatChanged event) {
        int currentXp = event.getXp();
        int previousXp = previous_exp[event.getSkill().ordinal()];
        if (previousXp > 0 && currentXp - previousXp > 0) {
            if (event.getSkill() == net.runelite.api.Skill.HITPOINTS) {
                int hit;
                hit = xpDropDamageCalculator.calculateHitOnNpc(lastOpponentId, currentXp - previousXp, config.xpMultiplier());
                if (muspahNPC != null && muspahNPC.getId() != 12082 && muspahNPC.getAnimation() != 9928) //not teleporting and not doing pre-teleport animation
                {
                    totalDamage = totalDamage + hit;
                }
                //System.out.println("Hit npc with hp xp drop xp: " + (currentXp - previousXp) +  " hit: " + hit + " npc_id: " + lastOpponentId);
            }
        }

        previous_exp[event.getSkill().ordinal()] = event.getXp();
    }

    public double getProgress() {
        return (double) (this.nextAttackTick - client.getTickCount()) / ATTACK_TICKS;
    }
}