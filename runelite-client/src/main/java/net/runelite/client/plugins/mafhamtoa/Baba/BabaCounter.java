package net.runelite.client.plugins.mafhamtoa.Baba;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BabaCounter {

    @Inject
    private Client client;
    @Inject
    private BabaCounterOverlay overlay;
    @Inject
    private BabaGapOverlay gapOverlay;
    @Inject
    private OverlayManager overlayManager;
    private final int BABA_NPC_ID = 11778;
    private final int BABA_FINAL_ID = 11779;
    private final int BABA_BOULDER_PHASE_ID = 11780;
    private final int BABA_SWIPE_ID = 9743;
    private final int BABA_BOULDER_THROW_ID = 9744;
    private final int BABA_GFX_ROCKS_ID = 2250;
    private final int BABA_GFX_ROCKS_FAST_ID = 2251;
    private final int BABA_GFX_SHADOW_MIDDLE_ID = 1448;
    private final int BABA_GFX_SHADOW_OUTER_ID = 2111;
    private final int TELEPORT_CRYSTAL_ID = 45754;
    private GameObject teleportCrystal;
    @Getter
    private Set<WorldPoint> gapTiles = new HashSet<>();
    private boolean finalPhase = false;
    private boolean hitsplatOnBaba = false;
    @Getter
    private Integer babaTimer;
    private Double babaHP;
    @Getter
    private boolean mindTheGap;
    private boolean phase1Ended;
    private boolean phase2Ended;
    private boolean swipeBool;
    @Getter
    private final Integer babaCounterDenominator = 4;
    @Getter
    private NPC babaBoss;
    private WorldPoint babaPreviousLocation;
    private WorldPoint playerPreviousLocation;
    private List<GraphicsObject> shadowsList = new ArrayList<>();
    private List<GraphicsObject> shadowsList2 = new ArrayList<>();
    private List<GraphicsObject> rocksList = new ArrayList<>();

    public void startUp()
    {
        overlayManager.add(overlay);
        overlayManager.add(gapOverlay);
    }

    public void shutDown()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(gapOverlay);
        reset();
    }

    private void reset()
    {
        babaBoss = null;
        babaTimer = null;
        babaHP = null;
        shadowsList.clear();
        rocksList.clear();
        babaPreviousLocation = null;
        playerPreviousLocation = null;
        finalPhase = false;
        hitsplatOnBaba = false;
        mindTheGap = false;
        phase1Ended = false;
        phase2Ended = false;
        swipeBool = false;
        shadowsList2.clear();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        if (client.getVarpValue(VarPlayer.HP_HUD_NPC_ID) == BABA_NPC_ID || client.getVarpValue(VarPlayer.HP_HUD_NPC_ID) == BABA_FINAL_ID)
        {
            double currentHP = client.getVarbitValue(6099);
            double maxHP = client.getVarbitValue(6100);
            double percentage = (currentHP/maxHP) * 100;
            babaHP = percentage;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Ba-Ba.";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid, diedTryAgain))
        {
            reset();
        }
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid))
        {
            teleportCrystal = null;
            gapTiles.clear();
        }
    }

    public static boolean checkStrings(String string1, String... stringsToCheck) {
        for (String str : stringsToCheck) {
            if (string1.contains(str)) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        if (babaBoss == null)
        {
            return;
        }
        if (hitsplatApplied.getActor() == babaBoss)
        {
            hitsplatOnBaba = true;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() == TELEPORT_CRYSTAL_ID)
        {
            teleportCrystal = gameObjectSpawned.getGameObject();
            WorldPoint teleportCrystalPoint = teleportCrystal.getWorldLocation();
            for (int x = 4; x <= 23; x++)
            {
                for (int y = -2; y <= 2; y++)
                {
                    gapTiles.add(teleportCrystalPoint.dx(x).dy(y));
                }
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        if (gameObjectDespawned.getGameObject().getId() == TELEPORT_CRYSTAL_ID)
        {
            teleportCrystal = null;
            gapTiles.clear();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == BABA_NPC_ID)
        {
            babaBoss = npcSpawned.getNpc();
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (npcChanged.getNpc().getId() == BABA_FINAL_ID)
        {
            finalPhase = true;
            if (hitsplatOnBaba)
            {
                babaTimer = 5;
            }
            else babaTimer = 8;
        }
        if (npcChanged.getNpc().getId() == BABA_BOULDER_PHASE_ID)
        {
            babaTimer = null;
            mindTheGap = false;
            if (babaHP <= 67)
            {
                phase1Ended = true;
            }
            if (babaHP <= 34)
            {
                phase2Ended = true;
            }
        }
        //this is called after baba finishes boulder phase
        if (npcChanged.getNpc().getId() == BABA_NPC_ID)
        {
            if (hitsplatOnBaba)
            {
                babaTimer = 5;
            }
            else babaTimer = 8;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (babaBoss == null)
        {
            return;
        }
        if (babaBoss == npcDespawned.getNpc())
        {
           reset();
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
    {
        if (babaBoss == null)
        {
            return;
        }
        GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
        if (graphicsObject.getId() == BABA_GFX_SHADOW_MIDDLE_ID || graphicsObject.getId() == BABA_GFX_SHADOW_OUTER_ID)
        {
            shadowsList.add(graphicsObject);
            shadowsList2.add(graphicsObject);
        }
        if (graphicsObject.getId() == BABA_GFX_ROCKS_ID || graphicsObject.getId() == BABA_GFX_ROCKS_FAST_ID)
        {
            rocksList.add(graphicsObject);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (animationChanged.getActor() != babaBoss)
        {
            return;
        }
        //if no rocks/shadows when baba swipes, it's a normal swipe not the fake rock spawning one
        if (animationChanged.getActor().getAnimation() == BABA_SWIPE_ID)
        {
            swipeBool = true;
        }

        //boulder throw seems to be same time as swipe but w/e
        if (animationChanged.getActor().getAnimation() == BABA_BOULDER_THROW_ID && babaTimer < 1)
        {
            if (finalPhase)
            {
                babaTimer = 4;
            }
            else
            {
                babaTimer = 6;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (babaBoss == null)
        {
            return;
        }
        //New list of shadows which is cleared earlir than the other one, this is for better timing of the
        // mind the gap part which checks if baba is not interacting and all the shadows are gone

        if (babaBoss.getHealthRatio() != -1 && babaBoss.getHealthScale() != -1)
        {
            //babaHP = ((double) babaBoss.getHealthRatio() / (double) babaBoss.getHealthScale() * 100);
        }
        //if it's time for a new attack and there are shadows present, this is a shadow attack
        if (!shadowsList.isEmpty() && (babaTimer == null || babaTimer < 1) && !babaBoss.isInteracting())
        {
            //If the tank was under him the prev tick then it's a small shadow attack, otherwise a big one
            if (wasTankUnderBabaLastTick())
            {
                babaTimer = 5;
            }
            //This will only be correct with shaking things up invo active :(
            else
            {
                babaTimer = 12;
            }
        }
        if (swipeBool)
        {
            if (rocksList.isEmpty() && shadowsList.isEmpty())
            {
                if (babaTimer == null || babaTimer < 1)
                {
                    if (finalPhase)
                    {
                        babaTimer = 4;
                    }
                    else
                    {
                        babaTimer = 6;
                    }
                }
            }
        }
        if (babaTimer != null && babaTimer < 2)
        {
            shadowsList2.clear();
            //System.out.println("cleared shadowslist");
        }

        if (babaHP != null)
        {
            if (babaHP > 0 && babaHP <= 66.6 && !phase1Ended && !babaBoss.isInteracting() && shadowsList2.isEmpty())
            {
                mindTheGap = true;
            }
            if (babaHP > 0 && babaHP <= 33.3 & !phase2Ended && !babaBoss.isInteracting() && shadowsList2.isEmpty())
            {
                mindTheGap = true;
            }
        }

        Actor tank = babaBoss.getInteracting();
        if (tank != null)
        {
            playerPreviousLocation = tank.getWorldLocation();
        }
        babaPreviousLocation = babaBoss.getWorldLocation();

        if (babaTimer != null && babaTimer > 0)
        {
            babaTimer--;
        }
        rocksList.clear();
        //don't clear shadows until baba is ready to attack again to stop it firing multiple times
        if (babaTimer != null && babaTimer < 1)
        {
            shadowsList.clear();
        }
        hitsplatOnBaba = false;
        swipeBool = false;
    }

    public double getPieProgress() {
            return (double) (babaTimer - 1) / babaCounterDenominator;
    }

    private boolean wasTankUnderBabaLastTick()
    {
        if (babaBoss == null || playerPreviousLocation == null || babaPreviousLocation == null)
        {
            return false;
        }
        //baba is 5x5
        List<WorldPoint> babaPoints = new ArrayList<>();
        for (int x = 0; x <= 4; x++)
        {
            for (int y = 0; y <= 4; y++)
            {
                WorldPoint worldPoint = babaPreviousLocation.dx(x).dy(y);
                babaPoints.add(worldPoint);
            }
        }
        return babaPoints.contains(playerPreviousLocation);
    }
}