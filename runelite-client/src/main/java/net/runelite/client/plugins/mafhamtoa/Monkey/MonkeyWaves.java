package net.runelite.client.plugins.mafhamtoa.Monkey;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class MonkeyWaves {

    @Inject
    private Client client;
    @Inject
    private MonkeyWavesOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    private boolean spawnedThisTick = false;
    @Getter
    private Integer waveCounter;
    @Getter
    private Integer groupSize;
    private final int[] memberVarbits = {Varbits.TOA_MEMBER_0_HEALTH, Varbits.TOA_MEMBER_1_HEALTH, Varbits.TOA_MEMBER_2_HEALTH, Varbits.TOA_MEMBER_3_HEALTH,
            Varbits.TOA_MEMBER_4_HEALTH, Varbits.TOA_MEMBER_5_HEALTH, Varbits.TOA_MEMBER_6_HEALTH, Varbits.TOA_MEMBER_7_HEALTH};

    private final List<Integer> ids = Arrays.asList(11709,11712,11711,11714,11715,11710,11713,11717,11716);

    public void startUp() {
        overlayManager.add(overlay);
    }
    public void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset()
    {
        spawnedThisTick = false;
        waveCounter = null;
        groupSize = null;
    }


    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (mapRegion != 15186) {
                return;
            }
        }
        if (ids.contains(npcSpawned.getNpc().getId()))
        {
            spawnedThisTick = true;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Path of Apmeken.";
        String challengeStarted = "Challenge started: Path of Apmeken.";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid, diedTryAgain))
        {
            reset();
        }
        if (checkStrings(message, challengeStarted))
        {
            int members = 0;
            groupSize = findGroupSize(members);
            waveCounter = 0;
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
    public void onGameTick(GameTick gameTick)
    {
        if (spawnedThisTick && waveCounter != null && waveCounter < 10)
        {
            waveCounter++;
        }
        spawnedThisTick = false;
    }

    private int findGroupSize(int members) {
        for (int varbit : memberVarbits) {
            members += Math.min(client.getVarbitValue(varbit), 1);
        }

        return members;
    }
}