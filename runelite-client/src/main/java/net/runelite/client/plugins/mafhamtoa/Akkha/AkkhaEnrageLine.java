package net.runelite.client.plugins.mafhamtoa.Akkha;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class AkkhaEnrageLine {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaEnrageLineOverlay overlay;

    @Getter
    private LocalPoint akkhaPoint;
    private WorldPoint oldWorldPoint;
    @Getter
    private int spawnTick = 0;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        akkhaPoint = null;
        spawnTick = 0;
        oldWorldPoint = null;
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (npcChanged.getNpc().getId() == 11795) //first enrage akkha
        {
            akkhaPoint = npcChanged.getNpc().getLocalLocation();
            oldWorldPoint = npcChanged.getNpc().getWorldLocation();
            spawnTick = client.getTickCount() + 3;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        for (NPC npc : client.getNpcs())
        {
            if (npc.getId() == 11798) //greyed out akkha
            {
                if (npc.getAnimation() == 9784) //teleporting anim
                {
                    akkhaPoint = npc.getLocalLocation();
                    spawnTick = client.getTickCount() + 3;
                }
            }
        }
    }


    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Akkha.";
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
}