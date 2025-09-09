package net.runelite.client.plugins.mafhamtoa.KephriPuzzle;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;

public class KephriPuzzle {

    private final Map<Integer, PuzzleTile> tileMap = Map.of(
            45361, new PuzzleTile("Star", Color.cyan, null),
            45364, new PuzzleTile("Boot", Color.green, null),
            45359, new PuzzleTile("Diamond", Color.blue, null),
            45363, new PuzzleTile("W", Color.yellow, null),
            45357, new PuzzleTile("Knives", Color.red, null)
    );

    private static final Set<Integer> TILE_COMPLETE_IDS = Set.of(45368, 45366, 45372, 45370, 45373);

    @Getter
    private Set<PuzzleTile> puzzleTiles = new HashSet<>();

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KephriPuzzleOverlay overlay;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        puzzleTiles.clear();
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Path of Scabaras.";
        if (message.contains(challengeComplete))
        {
            reset();
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        GroundObject groundObject = event.getGroundObject();
        int id = groundObject.getId();
        LocalPoint localPoint = groundObject.getLocalLocation();
        if (TILE_COMPLETE_IDS.contains(id))
        {
            if (!puzzleTiles.isEmpty())
            {
                puzzleTiles.removeIf(puzzleTile -> Objects.equals(puzzleTile.getLocalPoint(), event.getGroundObject().getLocalLocation()));
                return;
            }
        }

        PuzzleTile tile = tileMap.get(id);
        if (tile != null)
        {
            puzzleTiles.add(new PuzzleTile(tile.getName(), tile.getColor(), localPoint));
        }

    }
}