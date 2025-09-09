package net.runelite.client.plugins.mafhamvenenatis;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Venenatis",
        description = "Venenatis",
        tags = {"venenatis, Mafham"},
        enabledByDefault = true
)

public class MafhamVenenatisPlugin extends Plugin {

    private static final int SPINDEL_ID = 11998;
    private static final int SPIDERLING_ID = 12001;
    private static final List<Integer> SPINDEL_ANIM_IDS = Arrays.asList(9991,9989,9990);
    private static final int SPINDEL_WEB = 2360;
    private WorldPoint previousLocation;
    private boolean ignoreNextAttack = false;
    private boolean animationChangedThisTick = false;
    @Getter
    private int bossAttackCounter;
    @Getter
    private NPC mainBoss;
    @Getter
    private LocalPoint respawnPoint;
    @Getter
    private int deathTime = 0;
    @Getter
    private Set<GameObject> webObjects = new HashSet<>();
    private Set<Projectile> storedProjectiles = new HashSet<>();


    enum attackStyle
    {
        RANGE1,
        RANGE2,
        MAGE1,
        MAGE2
    }
    @Getter
    private MafhamVenenatisPlugin.attackStyle currentAttackStyle;

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamVenenatisOverlay overlay;
    @Inject
    private ClientThread clientThread;

    @Provides
    MafhamVenenatisConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamVenenatisConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        currentAttackStyle = attackStyle.RANGE1;
        mainBoss = null;
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset()
    {
        mainBoss = null;
        currentAttackStyle = attackStyle.RANGE1;
        bossAttackCounter = 0;
        ignoreNextAttack = false;
        previousLocation = null;
        webObjects.clear();
        storedProjectiles.clear();
        animationChangedThisTick = false;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }
        String message = chatMessage.getMessage();
        if (message.contains("Your Spindel kill count is"))
        {
            deathTime = client.getTickCount();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (mainBoss == null)
        {
            return;
        }
        int webMiddleID = 47084;
        int webCornerID = 47086;
        GameObject gameObject = gameObjectSpawned.getGameObject();
        int id = gameObject.getId();
        if (id == webCornerID || id == webMiddleID)
        {
            webObjects.add(gameObject);
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        if (webObjects.isEmpty())
        {
            return;
        }
        webObjects.remove(gameObjectDespawned.getGameObject());
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        NPC npc = npcSpawned.getNpc();
        if (npc.getId() == SPINDEL_ID)
        {
            mainBoss = npc;
            mainBoss.setAnimation(-1);
            respawnPoint = mainBoss.getLocalLocation();
        }
        if (npc.getId() == SPIDERLING_ID)
        {
            currentAttackStyle = attackStyle.RANGE1;
            bossAttackCounter = 1;
            ignoreNextAttack = true;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (mainBoss == null)
        {
            return;
        }
        NPC npc = npcDespawned.getNpc();
        if (mainBoss.getId() == npc.getId())
        {
            reset();
        }
    }
    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        if (mainBoss == null)
        {
            return;
        }
        if (!storedProjectiles.isEmpty())
        {
            return;
        }
        if (event.getProjectile().getId() == SPINDEL_WEB)
        {
            currentAttackStyle = attackStyle.MAGE1;
            bossAttackCounter = 3;
            storedProjectiles.add(event.getProjectile());
            ignoreNextAttack = true;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (mainBoss == null)
        {
            return;
        }
        Actor actor = animationChanged.getActor();
        if (actor != mainBoss)
        {
            return;
        }
        if (!SPINDEL_ANIM_IDS.contains(actor.getAnimation()))
        {
            return;
        }
        animationChangedThisTick = true;
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (mainBoss == null)
        {
            reset();
            return;
        }

        clientThread.invokeAtTickEnd(this::handleTickLogic);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (client.getGameState() == GameState.HOPPING)
        {
            reset();
        }
    }

    private boolean bossMoved()
    {
        if (mainBoss == null)
        {
            return false;
        }
        if (previousLocation == null)
        {
            return false;
        }
        WorldPoint currentLocation = mainBoss.getWorldLocation();
        return !Objects.equals(previousLocation, currentLocation);
    }

    private void handleTickLogic()
    {
        if (!storedProjectiles.isEmpty())
        {
            storedProjectiles.removeIf(projectile -> projectile.getRemainingCycles() < 1);
        }
        if (animationChangedThisTick)
        {
            handleAnimations();
        }
        //Sync case
        if (bossMoved() && bossAttackCounter == 3)
        {
            switch (currentAttackStyle) {
                case RANGE1:
                    currentAttackStyle = attackStyle.RANGE2;
                    break;
                case RANGE2:
                    currentAttackStyle = attackStyle.MAGE1;
                    break;
                case MAGE1:
                    currentAttackStyle = attackStyle.MAGE2;
                    break;
                case MAGE2:
                    currentAttackStyle = attackStyle.RANGE1;
                    break;
                default:
            }
            System.out.println("3 Attack phase detected, adjusting...");
            bossAttackCounter = 0;
        }

        previousLocation = mainBoss.getWorldLocation();
        ignoreNextAttack = false;
        animationChangedThisTick = false;
    }

    private void handleAnimations()
    {
        if (ignoreNextAttack)
        {
            return;
        }
        bossAttackCounter++;

        if (bossAttackCounter == 4) {
            bossAttackCounter = 0;
            switch(currentAttackStyle) {
                case RANGE1:
                    currentAttackStyle = attackStyle.RANGE2;
                    break;
                case RANGE2:
                    currentAttackStyle = attackStyle.MAGE1;
                    break;
                case MAGE1:
                    currentAttackStyle = attackStyle.MAGE2;
                    break;
                case MAGE2:
                    currentAttackStyle = attackStyle.RANGE1;
                    break;
                default:
            }
        }
    }
}