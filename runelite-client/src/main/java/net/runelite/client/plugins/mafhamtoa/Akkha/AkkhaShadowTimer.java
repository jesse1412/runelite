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
import java.util.HashSet;
import java.util.Set;

public class AkkhaShadowTimer {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaShadowTimerOverlay overlay;
    private final int TIMER_NPCID = 11805;
    private final int SHADOW_NPCID = 11797;
    private Set<NPC> timerNPCs = new HashSet<>();
    @Getter
    private Set<NPC> shadowNPCs = new HashSet<>();
    @Getter
    private Double timer;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset()
    {
        timer = null;
        timerNPCs.clear();
        shadowNPCs.clear();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == SHADOW_NPCID)
        {
            shadowNPCs.add(npcSpawned.getNpc());
        }
        if (npcSpawned.getNpc().getId() == TIMER_NPCID)
        {
            timerNPCs.add(npcSpawned.getNpc());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        shadowNPCs.remove(npcDespawned.getNpc());
        timerNPCs.remove(npcDespawned.getNpc());
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (!timerNPCs.isEmpty())
        {
            for (NPC npc : timerNPCs)
            {
                if (npc.getHealthRatio() != -1 && npc.getHealthScale() != -1)
                {
                    timer = ((double) npc.getHealthRatio() / (double) npc.getHealthScale() * 100);
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String finalStand = "Akkha makes his final stand!";
        String challengeComplete = "Challenge complete: Akkha.";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid, finalStand))
        {
            reset();
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
}