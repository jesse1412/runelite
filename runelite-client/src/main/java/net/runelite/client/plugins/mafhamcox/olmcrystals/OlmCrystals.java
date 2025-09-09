package net.runelite.client.plugins.mafhamcox.olmcrystals;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OlmCrystals {

    @Inject
    private Client client;
    @Inject
    private OlmCrystalsOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Getter
    private HashMap<GraphicsObject, Integer> crystalsMapForHighlighting = new HashMap<>();
    private HashMap<GraphicsObject, Integer> crystalsMapForLogic = new HashMap<>();
    private static final int crystalID = 1447;
    private static final int explosionID = 1358;
    private Integer crystalAgeWhenExplosionOccurred = null;

    public void startUp() {
        overlayManager.add(overlay);
    }
    public void shutDown() {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        crystalsMapForLogic.clear();
        crystalsMapForHighlighting.clear();
        crystalAgeWhenExplosionOccurred = null;
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        GraphicsObject graphicsObject = event.getGraphicsObject();
        int id = graphicsObject.getId();
        int currentTick = client.getTickCount();
        if (id != crystalID && id != explosionID)
        {
            return;
        }
        if (id == crystalID)
        {
            crystalsMapForLogic.put(graphicsObject, currentTick);
            crystalsMapForHighlighting.put(graphicsObject, currentTick);
        }
        if (id == explosionID)
        {
            for (Map.Entry<GraphicsObject, Integer> entry : crystalsMapForLogic.entrySet()) {
                int crystalAge = currentTick - entry.getValue();
                if (crystalAge < 3) // ignore young crystals to prevent removing overlapping crystals
                {
                    continue;
                }
                if (!entry.getKey().getLocation().equals(event.getGraphicsObject().getLocation())) // if they're not on same tile
                {
                    continue;
                }
                //System.out.println("Age when explosion detected: " + crystalAge);
                crystalAgeWhenExplosionOccurred = crystalAge;
            }
        }

    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (!crystalsMapForHighlighting.isEmpty())
        {
            int timeToRemoveGraphic = 4;
            if (crystalAgeWhenExplosionOccurred != null)
            {
                timeToRemoveGraphic = crystalAgeWhenExplosionOccurred - 1;
            }
            Iterator<Map.Entry<GraphicsObject, Integer>> iterator = crystalsMapForHighlighting.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<GraphicsObject, Integer> entry = iterator.next();
                if (client.getTickCount() >= entry.getValue() + timeToRemoveGraphic) {
                    iterator.remove();
                }
            }
        }
        if (!crystalsMapForLogic.isEmpty())
        {
            crystalsMapForLogic.entrySet().removeIf(entry -> client.getTickCount() >= entry.getValue() + 4);
        }
    }

}