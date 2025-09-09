package net.runelite.client.plugins.mafhamtoa.Wardens;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class WardensCoreTimer {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WardensCoreTimerOverlay overlay;

    private final int WARDEN_DOWNED_NPCID = 11755;
    private final int WARDEN_CORE_NPCID = 11771;
    @Getter
    private NPC core;
    @Getter
    private boolean inWardens;
    private double wardenHP;
    @Getter
    private Integer coreTimer;
    @Getter
    private int blueTimer = -1;
    @Getter
    private int orangeTimer = -1;
    @Getter
    private Set<GameObject> ufos = new HashSet<>();

    public void startUp()
    {
        overlayManager.add(overlay);
    }

    public void shutDown()
    {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset()
    {
        core = null;
        coreTimer = null;
        wardenHP = -1;
        blueTimer = -1;
        orangeTimer = -1;
        ufos.clear();
        inWardens = false;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event.getGameObject().getId() == 45750 || event.getGameObject().getId() == 45751)
        {
            ufos.add(event.getGameObject());
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        ufos.remove(event.getGameObject());
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (npcChanged.getNpc().getId() == WARDEN_DOWNED_NPCID)
        {
            if (client.getVarpValue(VarPlayer.HP_HUD_NPC_ID) == WARDEN_DOWNED_NPCID)
            {
                double currentHP = client.getVarbitValue(6099);
                double maxHP = client.getVarbitValue(6100);
                wardenHP = (currentHP/maxHP) * 100;
                //System.out.println("Warden HP: " + wardenHP);
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == 11750 ||
            npcSpawned.getNpc().getId() == 11751 ||
            npcSpawned.getNpc().getId() == 11752) //obelisk ids
        {
            inWardens = true;
        }
        if (npcSpawned.getNpc().getId() == WARDEN_CORE_NPCID)
        {
            core = npcSpawned.getNpc();
            if (wardenHP > -1)
            {
                if (wardenHP >= 80) {
                    coreTimer = 21 + 1;
                } else if (wardenHP >= 60) {
                    coreTimer = 29 + 1;
                } else if (wardenHP >= 40) {
                    coreTimer = 37 + 1;
                } else if (wardenHP >= 20) {
                    coreTimer = 45+ 1;
                } else {
                    coreTimer = 53 + 1;
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Elidinis' Warden uses the last of its power to restore Tumeken's Warden!";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid, diedTryAgain))
        {
            reset();
        }
        if (message.contains("<col=3366ff>A large ball of energy is shot your way...</col>"))
        {
            blueTimer = 14;
        }
        if (message.contains("<col=ff8e32>A large ball of energy is shot your way...</col>"))
        {
            orangeTimer = 14;
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
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc().getId() == WARDEN_CORE_NPCID)
        {
            core = null;
            coreTimer = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (orangeTimer > -1)
        {
            orangeTimer--;
        }
        if (blueTimer > -1)
        {
            blueTimer--;
        }
        if (core == null || coreTimer == null)
        {
            return;
        }
        if (coreTimer > 0)
        {
            coreTimer--;
        }
    }
}