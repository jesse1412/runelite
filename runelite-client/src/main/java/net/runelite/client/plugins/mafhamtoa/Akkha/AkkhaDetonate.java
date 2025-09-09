package net.runelite.client.plugins.mafhamtoa.Akkha;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class AkkhaDetonate {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaDetonateOverlay overlay;
    private static final List<Integer> GFX_IDS = Arrays.asList(
            2256, 2257, 2258, 2259
    );
    private final int TELEPORT_CRYSTAL_OBJ_ID = 45866;
    private GameObject teleportCrystal;
    @Getter
    private Set<WorldPoint> tileHighlights = new HashSet<>();
    private boolean detonationActivated = false;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() {
        overlayManager.remove(overlay);
        detonationActivated = false;
        tileHighlights.clear();
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        String message = chatMessage.getMessage();
        String detonationString = "You have been marked for detonation!";
        String finalStand = "Akkha makes his final stand!";
        if (message.contains(detonationString))
        {
            detonationActivated = true;
        }
        if (message.contains(finalStand))
        {
            detonationActivated = false;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() == TELEPORT_CRYSTAL_OBJ_ID)
        {
            teleportCrystal = gameObjectSpawned.getGameObject();
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        if (teleportCrystal == gameObjectDespawned.getGameObject())
        {
            teleportCrystal = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        tileHighlights.clear();
        if (detonationActivated)
        {
            for (Player player : client.getPlayers())
            {
                if (Objects.equals(player.getName(), client.getLocalPlayer().getName()))
                {
                    continue;
                }
                if (player.getWorldLocation().getX() >= teleportCrystal.getWorldLocation().getX())
                {
                    continue;
                }
                //diagonals
                for (int dx = -3; dx <= 3; dx++)
                {
                    for (int dy = -3; dy <= 3; dy++)
                    {
                        if ((dx != 0 && dy != 0) && (Math.abs(dy) == Math.abs(dx)))
                        {
                            WorldPoint worldPoint = player.getWorldLocation().dx(dx).dy(dy);
                            tileHighlights.add(worldPoint);
                        }
                    }
                }
                //horizontal
                for (int dx = -3; dx <= 3; dx++)
                {
                    if (dx != 0)
                    {
                        WorldPoint worldPoint = player.getWorldLocation().dx(dx);
                        tileHighlights.add(worldPoint);
                    }
                }
                //vertical
                for (int dy = -3; dy <= 3; dy++)
                {
                    if (dy != 0)
                    {
                        WorldPoint worldPoint = player.getWorldLocation().dy(dy);
                        tileHighlights.add(worldPoint);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        if (GFX_IDS.contains(event.getGraphicsObject().getId()))
        {
            detonationActivated = false;
        }
    }

}