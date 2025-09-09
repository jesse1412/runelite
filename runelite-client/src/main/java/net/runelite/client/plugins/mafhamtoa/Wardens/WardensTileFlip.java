package net.runelite.client.plugins.mafhamtoa.Wardens;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.Akkha.TombsTiles;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Collection;

public class WardensTileFlip {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WardensTileFlipOverlay overlay;
    private final int TUMEKEN_NPCID = 11762;
    private final int TUMEKEN_LEFTATT_ID = 9675;
    private final int TUMEKEN_RIGHTATT_ID = 9677;
    private final int TUMEKEN_MIDATT_ID = 9679;
    private final int SKULL_NPCID = 11772;
    private NPC warden;
    private boolean skullsSpawned = false;
    private Integer lastAttack;
    @Getter
    private WorldPoint highlightTile;

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
        warden = null;
        lastAttack = null;
        skullsSpawned = false;
        highlightTile = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == TUMEKEN_NPCID)
        {
            warden = npcSpawned.getNpc();
        }
        if (npcSpawned.getNpc().getId() == SKULL_NPCID)
        {
            skullsSpawned = true;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        String challengeComplete = "Challenge complete: The Wardens.";
        if (checkStrings(message, diedFinal, leftRaid, diedTryAgain, challengeComplete))
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
    public void onGameTick(GameTick event)
    {
        boolean shouldReset = true;
        for (int mapRegion : client.getMapRegions()) {
            if (mapRegion == 15696) {
                shouldReset = false;
                break;
            }
        }
        if (shouldReset)
        {
            reset();
        }
        if (warden == null)
        {
            return;
        }
        int currentAnim = warden.getAnimation();
        if (currentAnim == TUMEKEN_LEFTATT_ID || currentAnim == TUMEKEN_RIGHTATT_ID || currentAnim == TUMEKEN_MIDATT_ID)
        {
            skullsSpawned = false;
            highlightTile = null;
            lastAttack = currentAnim;
        }
        if (skullsSpawned)
        {
            TombsTiles tile;
            WorldPoint worldPoint;
            Collection<WorldPoint> worldPointCollection;
            switch (lastAttack)
            {
                case TUMEKEN_LEFTATT_ID:
                    tile = TombsTiles.WARDENS_LEFT;
                    worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                    worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                    for (WorldPoint worldPoint1 : worldPointCollection)
                    {
                        highlightTile = worldPoint1;
                    }
                    break;
                case TUMEKEN_RIGHTATT_ID:
                    tile = TombsTiles.WARDENS_MIDDLE;
                    worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                    worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                    for (WorldPoint worldPoint1 : worldPointCollection)
                    {
                        highlightTile = worldPoint1;
                    }
                    break;
                case TUMEKEN_MIDATT_ID:
                    tile = TombsTiles.WARDENS_RIGHT;
                    worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                    worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                    for (WorldPoint worldPoint1 : worldPointCollection)
                    {
                        highlightTile = worldPoint1;
                    }
                    break;
            }
        }
    }
}