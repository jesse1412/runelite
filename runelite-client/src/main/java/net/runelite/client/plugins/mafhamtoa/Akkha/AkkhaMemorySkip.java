package net.runelite.client.plugins.mafhamtoa.Akkha;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.plugins.mafhamtoa.MafhamToAPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class AkkhaMemorySkip extends MafhamToAPlugin {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaMemorySkipOverlay akkhaMemorySkipOverlay;
    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MafhamToAConfig.class);
    }

    private List<GameObject> gameObjectsList = new ArrayList<>();
    @Getter
    private List<WorldPoint> greenHighlights = new ArrayList<>();
    @Getter
    private List<WorldPoint> yellowHighlights = new ArrayList<>();
    @Getter
    private List<WorldPoint> orangeHighlights = new ArrayList<>();
    @Getter
    private Set<Player> playerHighlights = new HashSet<>();
    private static final List<Integer> MEMORY_IDS = Arrays.asList(45871, 45869, 45870, 45868);
    private static final List<Integer> AKKHA_IDS = Arrays.asList(
            11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796
    );
    private static final List<Integer> GFX_IDS = Arrays.asList(
            2256, 2257, 2258, 2259
    );
    private List<GraphicsObject> graphicsObjects = new ArrayList<>();
    private boolean startCycle = false;
    private int cycleCounter = -1;
    private int yellowCycleCounter = 0;
    private boolean memoryDetected = false;
    private Integer selectedQuadrant;

    @Override
    public void startUp() throws Exception {
        overlayManager.add(akkhaMemorySkipOverlay);
    }

    @Override
    public void shutDown() throws Exception {
        overlayManager.remove(akkhaMemorySkipOverlay);
        gameObjectsList.clear();
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        GraphicsObject graphicsObject = event.getGraphicsObject();
        if (GFX_IDS.contains(graphicsObject.getId())) {
            graphicsObjects.add(graphicsObject);
            //For manual we use when the quadrants alight to start the timing
            if (graphicsObjects.size() > 80 && memoryDetected && config.memorySetting() == MafhamToAConfig.MemorySetting.Manual)
            {
                startCycle = true;
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (MEMORY_IDS.contains(event.getGameObject().getId()))
        {
            memoryDetected = true;
            gameObjectsList.add(event.getGameObject());
            if (gameObjectsList.size() == (3 + Math.floor(findAkkhalevel() / 2)))
            {
                if (config.memorySetting() == MafhamToAConfig.MemorySetting.Follow)
                {
                    startCycle = true;
                    for (TombsTiles tile : TombsTiles.values())
                    {
                        if (tile.getPosition() == 1 && selectedQuadrant == null && tile.getGroup() < 10)
                        {
                            WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                            Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                            for (WorldPoint worldPoint1 : worldPointCollection)
                            {
                                if (client.getLocalPlayer().getWorldLocation().distanceTo(worldPoint1) < 4)
                                {
                                    selectedQuadrant = tile.getGroup();
                                }
                            }
                        }
                    }
                }
                if (config.memorySetting() == MafhamToAConfig.MemorySetting.Manual)
                {
                    for (TombsTiles tile : TombsTiles.values())
                    {
                        if (tile.getPosition() == 1 && selectedQuadrant == null && tile.getGroup() >= 10)
                        {
                            WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                            Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                            for (WorldPoint worldPoint1 : worldPointCollection)
                            {
                                if (client.getLocalPlayer().getWorldLocation().distanceTo(worldPoint1) < 3)
                                {
                                    selectedQuadrant = tile.getGroup();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npcSpawned = event.getNpc();
        if (AKKHA_IDS.contains(npcSpawned.getId())) {
            gameObjectsList.clear();
            graphicsObjects.clear();
            startCycle = false;
            memoryDetected = false;
            cycleCounter = -1;
            yellowCycleCounter = 0;
            selectedQuadrant = null;
            //System.out.println("found akkha");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        greenHighlights.clear();
        yellowHighlights.clear();
        orangeHighlights.clear();
        playerHighlights.clear();
        graphicsObjects.clear();
        if (memoryDetected && !startCycle)
        {
            if (config.memorySetting() == MafhamToAConfig.MemorySetting.Follow)
            {
                for (TombsTiles tile : TombsTiles.values())
                {
                    if (tile.getPosition() == 15 && tile.getGroup() < 10) //start tile given position 15 arbitrarily
                    {
                        WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                        Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                        greenHighlights.addAll(worldPointCollection);
                    }
                    if (tile.getPosition() == 16) //orange tile given position 16 arbitrarily
                    {
                        WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                        Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                        orangeHighlights.addAll(worldPointCollection);
                    }
                    if (tile.getPosition() == 0 && tile.getGroup() < 10)
                    {
                        WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                        Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                        yellowHighlights.addAll(worldPointCollection);
                    }
                }
                for (Player player : client.getPlayers())
                {
                    for (WorldPoint worldPoint : greenHighlights)
                    {
                        if (!Objects.equals(player.getName(), client.getLocalPlayer().getName()))
                        {
                            if (player.getWorldLocation().equals(worldPoint))
                            {
                                playerHighlights.add(player);
                            }
                        }
                    }
                }
            }
            if (config.memorySetting() == MafhamToAConfig.MemorySetting.Manual)
            {
                for (TombsTiles tile : TombsTiles.values())
                {
                    if (tile.getPosition() == 15 && tile.getGroup() >= 10) //start tile given position 15 arbitrarily
                    {
                        WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                        Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                        greenHighlights.addAll(worldPointCollection);
                    }
                    if (tile.getPosition() == 0 && tile.getGroup() >= 10)
                    {
                        WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                        Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                        yellowHighlights.addAll(worldPointCollection);
                    }
                }
            }
        }
        if (startCycle)
        {
            cycleCounter++;
            yellowCycleCounter++;
            for (TombsTiles tile : TombsTiles.values())
            {
                if (selectedQuadrant == null)
                {
                    return;
                }
                if (tile.getPosition() == cycleCounter && tile.getGroup() == selectedQuadrant)
                {
                    WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                    Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                    greenHighlights.addAll(worldPointCollection);
                }
                if (tile.getPosition() == yellowCycleCounter && tile.getGroup() == selectedQuadrant)
                {
                    WorldPoint worldPoint = WorldPoint.fromRegion(tile.getRegion(), tile.getX(), tile.getY(), tile.getZ());
                    Collection<WorldPoint> worldPointCollection = WorldPoint.toLocalInstance(client, worldPoint);
                    yellowHighlights.addAll(worldPointCollection);
                }
            }
        }
    }

    private int findAkkhalevel(){

        if (client.getWidget(481,45) != null)
        {
            if (Integer.parseInt(Objects.requireNonNull(client.getWidget(481, 45)).getText()) == 6)
            {
                return 5;
            }
            return Integer.parseInt(Objects.requireNonNull(client.getWidget(481, 45)).getText());
        }

        return 0;
    }


}