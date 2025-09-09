package net.runelite.client.plugins.mafhamtoa.Kephri;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class SoldierScarab {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SoldierScarabOverlay overlay;
    @Getter
    private Integer attackCounter;
    @Getter
    private NPC npc;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        attackCounter = null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Kephri.";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid))
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

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (npc == null)
        {
            return;
        }
        if (animationChanged.getActor() == npc)
        {
            if (npc.getAnimation() == 9587)
            {
                attackCounter = 7;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (attackCounter != null && npc != null)
        {
            if (attackCounter > 0)
            {
                attackCounter--;
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == 11724)
        {
            npc = npcSpawned.getNpc();
            attackCounter = 6;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc().getId() == 11724)
        {
            npc = null;
            attackCounter = null;
        }
    }
}