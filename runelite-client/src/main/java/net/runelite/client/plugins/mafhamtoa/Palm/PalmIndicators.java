package net.runelite.client.plugins.mafhamtoa.Palm;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PalmIndicators {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PalmIndicatorsOverlay overlay;
    private final int SPIKE_ID = 45415;
    @Getter
    private Set<Spike> spikes = new HashSet<>();
    @Getter
    private Map<GraphicsObject, Instant> acids = new HashMap<>();
    private final int SPIKE_ACTIVE_ANIMID = 9563;
    private final int SPIKE_IDLE_ANIMID = 9562;
    private final int ACID_ID = 2129;
    private final int PALM_ID = 32740;
    @Getter
    private GameObject palm;
    @Getter
    boolean hasMovedY;
    private WorldPoint previousPos;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        spikes.clear();
        acids.clear();
        palm = null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Path of Crondis.";
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
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() == SPIKE_ID)
        {
            int direction = gameObjectSpawned.getGameObject().getOrientation();
            spikes.add(new Spike(gameObjectSpawned.getGameObject(), 0, 0, direction));
        }
        if (gameObjectSpawned.getGameObject().getId() == PALM_ID)
        {
            palm = gameObjectSpawned.getGameObject();
        }
    }
    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        if (gameObjectDespawned.getGameObject().getId() == SPIKE_ID)
        {
            spikes.removeIf(spike -> spike.getGameObject().equals(gameObjectDespawned.getGameObject()));
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (mapRegion == 15700) //safety reset after entering zebak's room
            {
                reset();
            }
        }
        if (client.getLocalPlayer() == null)
        {
            return;
        }
        WorldPoint currentPos = client.getLocalPlayer().getWorldLocation();
        if (previousPos != null)
        {
            hasMovedY = currentPos.getY() != previousPos.getY();
        }
        if (!spikes.isEmpty())
        {
            for (Spike spike : spikes)
            {
                int currentAnimId = ((DynamicObject) spike.getGameObject().getRenderable()).getAnimation().getId();
                if (currentAnimId != spike.getPreviousAnim() && currentAnimId == SPIKE_ACTIVE_ANIMID)
                {
                    spike.setCycle(10);
                }
                spike.setCycle(spike.getCycle() - 1);
                spike.setPreviousAnim(currentAnimId);
            }
        }
        previousPos = client.getLocalPlayer().getWorldLocation();
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
    {
        if (graphicsObjectCreated.getGraphicsObject().getId() == ACID_ID)
        {
            acids.put(graphicsObjectCreated.getGraphicsObject(), Instant.now());
        }
    }
}