package net.runelite.client.plugins.mafhamtoa.Akkha;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class AkkhaTickCounter {

    private final List<Integer> akkhaIDs = Arrays.asList(11792, 11790, 11791);
    private boolean timerStarted = false;
    @Getter
    private NPC akkhaNPC;
    @Getter
    private int akkhaTimer;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaTickCounterOverlay overlay;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        timerStarted = false;
        akkhaNPC = null;
        akkhaTimer = -1;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (akkhaNPC != null && (akkhaNPC.getAnimation() == 9778 || akkhaNPC.getAnimation() == 9776))
        {
            akkhaTimer = 97;
            return;
        }
        if (timerStarted && akkhaTimer > -1)
        {
            //System.out.println(akkhaTimer);
            akkhaTimer--;
        }
    }

    @Subscribe
    public void onNPCSpawned(NpcSpawned npcSpawned)
    {
        if (akkhaIDs.contains(npcSpawned.getNpc().getId()))
        {
            timerStarted = true;
            akkhaNPC = npcSpawned.getNpc();
            akkhaTimer = 96;
        }
        if (npcSpawned.getNpc().getId() == 11789) //akkha starting id
        {
            akkhaNPC = npcSpawned.getNpc();
        }
    }

    @Subscribe
    public void onNPCDespawned(NpcDespawned npcDespawned)
    {
        if (akkhaIDs.contains(npcDespawned.getNpc().getId()))
        {
            reset();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String akkhaStartedString = "Challenge started: Akkha.";
        String finalStand = "Akkha makes his final stand!";
        if (message.contains(akkhaStartedString))
        {
            timerStarted = true;
            akkhaTimer = 100;
        }
        if (message.contains(finalStand))
        {
            reset();
        }
    }

}