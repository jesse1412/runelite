package net.runelite.client.plugins.dukesucellus;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.ScriptID;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.dukesucellus.Enums.NextAttack;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Objects;

public class AttackHandler {

    @Inject
    private Client client;
    @Inject
    private AttackHandlerOverlay attackHandlerOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private DukeSucellusConfig config;

    private final int DUKE_SPAWNED = 12166;
    private final int DUKE_IDLE = 12167;
    private final int DUKE_FIGHTING = 12191;
    private final int DUKE_DEAD = 12192;
    private final int DUKE_EYE_ATTACK = 10180;
    private final int DUKE_SLAM_ATTACK = 10176;
    private final int DUKE_GAS_ATTACK = 10178;

    @Getter
    private NPC dukeBoss;
    private Integer attacksUntilGas; //every 3 attacks
    private Integer attacksUntilEye; //every 5 attacks
    private int lastGasAttack = 0; //duke checks hp on first gas attack when he spits 2 so we have to try and only check that one
    @Getter
    private Integer attackCounter;
    @Getter
    private NextAttack nextAttack;
    private WorldPoint storedTile;

    /*
    Duke's attack pattern:
    Spawns, 5 reg attacks, eye
    3 reg attacks, gas
    ^ This part never changes
    Then after this, if Duke is OVER 25% HP, gas every 4 attacks, if UNDER, every 3
    Eye is every 5 attacks still
    If Duke could do an eye or gas attack, it will always be eye, and then one regular attack, then gas
    (He adds in one extra auto after the eye so he doesn't do 2 specs in a row)
    Duke checks his hp upon shooting the first gas projectile. He doesn't recheck until he does the FIRST gas
    projectile again, never checks hp on 2nd
     */

    public void startUp()
    {
        overlayManager.add(attackHandlerOverlay);
    }

    public void shutDown()
    {
        overlayManager.remove(attackHandlerOverlay);
        fullReset();
    }

    private void fullReset()
    {
        dukeBoss = null;
        storedTile = null;
        resetBoss();
    }
    private void resetBoss()
    {
        attacksUntilGas = null;
        attacksUntilEye = null;
        attackCounter = null;
        nextAttack = null;
        lastGasAttack = 0;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (dukeBoss != null)
        {
            if (storedTile != null)
            {
                if (dukeBoss.getId() == DUKE_IDLE)
                {
                    if (playerHasMoved())
                    {
                        WorldPoint bossSW = dukeBoss.getWorldLocation();
                        WorldPoint bossSouthRow = bossSW.dy(-1);
                        WorldArea spamPoint = new WorldArea(bossSouthRow.getX(), bossSouthRow.getY(), 7, 1, client.getPlane());
                        if (spamPoint.contains(client.getLocalPlayer().getWorldLocation()))
                        {
                            if (config.cameraThing())
                            {
                                client.runScript(ScriptID.CAMERA_DO_ZOOM, 550, 550);
                                client.runScript(1050, 4); //compass click id, cardinal dir
                            }
                        }
                    }
                }
            }
        }
        storedTile = client.getLocalPlayer().getWorldLocation();


        if (attacksUntilGas == null) //first 5 attacks
        {
            attackCounter = attacksUntilEye;
            nextAttack = NextAttack.EYE;
            return;
        }
        if (attacksUntilGas < attacksUntilEye)
        {
            nextAttack = NextAttack.GAS;
        }
        else nextAttack = NextAttack.EYE;
        attackCounter = Math.min(attacksUntilEye, attacksUntilGas);


    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        final int npcId = npcSpawned.getNpc().getId();
        if (npcId == DUKE_SPAWNED)
        {
            dukeBoss = npcSpawned.getNpc();
        }
        if (npcId == DUKE_IDLE)
        {
            dukeBoss = npcSpawned.getNpc();
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (dukeBoss == null)
        {
            return;
        }
        if (npcChanged.getNpc().getId() == DUKE_FIGHTING)
        {
            //System.out.println("Duke now fighting");
            attacksUntilEye = 5;
            nextAttack = NextAttack.EYE;
        }
        if (npcChanged.getNpc().getId() == DUKE_DEAD)
        {
            //System.out.println("Duke died");
            resetBoss();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (dukeBoss == npcDespawned.getNpc())
        {
            resetBoss();
            dukeBoss = null;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        final int animationID = animationChanged.getActor().getAnimation();
        if (dukeBoss == null)
        {
            return;
        }
        if (dukeBoss.getId() == DUKE_DEAD)
        {
            return;
        }
        if (animationID == DUKE_SLAM_ATTACK)
        {
            if (attacksUntilGas != null)
            {
                attacksUntilGas--;
            }
            if (attacksUntilEye != null)
            {
                attacksUntilEye--;
            }
        }
        if (animationID == DUKE_GAS_ATTACK && nowIsAfterGasWait())
        {
            if (over25HP())
            {
                attacksUntilGas = 4;
            }
            else attacksUntilGas = 3;
        }
        if (animationID == DUKE_EYE_ATTACK)
        {
            attacksUntilEye = 5;
            if (attacksUntilGas == null)
            {
                //first eye attack, so we set next gas to 3 (always 3)
                attacksUntilGas = 3;
            }
            if (attacksUntilGas == 0)
            {
                //an extra attack gets added before another gas attack can happen,
                //if next gas is due to happen. So we add another attack in here
                attacksUntilGas = 1;
            }
        }
    }

    public boolean over25HP()
    {
        int v = client.getVarpValue(VarPlayer.HP_HUD_NPC_ID);
        if (v == DUKE_SPAWNED || v == DUKE_IDLE || v == DUKE_FIGHTING || v == DUKE_DEAD)
        {
            double currentHP = client.getVarbitValue(6099);
            if (currentHP > 110)
            {
                return true;
            }
        }
        return false;
    }

    private boolean nowIsAfterGasWait()
    {
        return client.getTickCount() > (lastGasAttack + 6);
    }
    private boolean playerHasMoved()
    {
        if (storedTile == null)
        {
            return false;
        }
        WorldPoint currentTile = client.getLocalPlayer().getWorldLocation();
        return !Objects.equals(currentTile, storedTile);
    }
}