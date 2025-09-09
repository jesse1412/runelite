package net.runelite.client.plugins.mafhamtoa.Akkha;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.plugins.mafhamtoa.MafhamToAPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class AkkhaMemory extends MafhamToAPlugin {
    @Inject
    private Client client;
    @Inject MafhamToAConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AkkhaMemoryOverlay highlightOverlay;
    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MafhamToAConfig.class);
    }
    private Map<GameObject, Integer> gameObjectsMap = new LinkedHashMap<>();

    private static final List<Integer> AKKHA_IDS = Arrays.asList(
            11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796
    );

    private static final List<Integer> GFX_IDS = Arrays.asList(
            2256, 2257, 2258, 2259
    );

    private WorldPoint adjustedblackworld = null;
    private WorldPoint adjustedyellowworld = null;
    private WorldPoint adjustedwhiteworld = null;
    private WorldPoint adjustedredworld = null;
    private int counter = 0;
    private List<GraphicsObject> graphicsObjects = new ArrayList<>();

    boolean foundgfx = true; //this prevents the multiple flames spawning causing the iterator to iterate the entire list immediately. Only once per tick.

    @Override
    public void startUp() throws Exception {
        overlayManager.add(highlightOverlay);
    }

    @Override
    public void shutDown() throws Exception {
        overlayManager.remove(highlightOverlay);
        gameObjectsMap.clear();
    }


    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (!config.memoryToggle()){
            return;
        }
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() == 45871) { //black
            gameObjectsMap.put(gameObject, event.getGameObject().getId());
            WorldPoint blackworld = event.getGameObject().getWorldLocation();
            adjustedblackworld = blackworld.dx(-1).dy(-1).dz(0);
            //System.out.println("Added Black Game Object with ID: " + event.getGameObject().getId());
        }
        if (gameObject.getId() == 45869) { //yellow
            gameObjectsMap.put(gameObject, event.getGameObject().getId());
            WorldPoint yellowworld = event.getGameObject().getWorldLocation();
            adjustedyellowworld = yellowworld.dx(-1).dy(1).dz(0);
            //System.out.println("Added Yellow Game Object with ID: " + event.getGameObject().getId());
        }
        if (gameObject.getId() == 45870) { //white
            gameObjectsMap.put(gameObject, event.getGameObject().getId());
            WorldPoint whiteworld = event.getGameObject().getWorldLocation();
            adjustedwhiteworld = whiteworld.dx(1).dy(1).dz(0);
            //System.out.println("Added White Game Object with ID: " + event.getGameObject().getId());
        }
        if (gameObject.getId() == 45868) { //red
            gameObjectsMap.put(gameObject, event.getGameObject().getId());
            WorldPoint redworld = event.getGameObject().getWorldLocation();
            adjustedredworld = redworld.dx(1).dy(-1).dz(0);
            //System.out.println("Added Red Game Object with ID: " + event.getGameObject().getId());
        }
    }


    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (!config.memoryToggle()){
            return;
        }
        NPC npcSpawned = event.getNpc();
        if (AKKHA_IDS.contains(npcSpawned.getId())) {
            gameObjectsMap.clear();
            //System.out.println("found akkha");
        }
    }


    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if (!config.memoryToggle()){
            return;
        }
        GraphicsObject graphicsObject = event.getGraphicsObject();
        if (GFX_IDS.contains(graphicsObject.getId()))
        {
            graphicsObjects.add(graphicsObject);
        }
        //check size 80 to make sure a double trouble det can't progress the iterator
        if (!gameObjectsMap.isEmpty() && foundgfx && graphicsObjects.size() > 80)  {
            if (GFX_IDS.contains(graphicsObject.getId())) {
                Iterator<Map.Entry<GameObject, Integer>> iterator = gameObjectsMap.entrySet().iterator();
                //System.out.println("gfx spawned");
                if (iterator.hasNext()) {
                    Map.Entry<GameObject, Integer> firstEntry = iterator.next();
                    iterator.remove();
                    foundgfx = false;
                //System.out.println("Removed first entry with value " + firstEntry.getValue());
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!config.memoryToggle()){
            return;
        }
        graphicsObjects.clear();
        highlightOverlay.greenTiles.clear();
        highlightOverlay.yellowTiles.clear();
        List<Integer> valuesList = new ArrayList<>(gameObjectsMap.values());
        if (counter == 1)
        {
            foundgfx = true;
            counter = 0;
        }
        if (!foundgfx)
        {
            counter++;
        }
        if (!gameObjectsMap.isEmpty()) {
            int firstValue = valuesList.get(0);
            if (firstValue == 45871 && adjustedblackworld != null) { //black
                highlightOverlay.greenTiles.add(LocalPoint.fromWorld(client, adjustedblackworld));
            }
            if (firstValue == 45869 && adjustedyellowworld != null) { //yellow
                highlightOverlay.greenTiles.add(LocalPoint.fromWorld(client, adjustedyellowworld));
            }
            if (firstValue == 45870 && adjustedwhiteworld != null) { //white
                highlightOverlay.greenTiles.add(LocalPoint.fromWorld(client, adjustedwhiteworld));
            }
            if (firstValue == 45868 && adjustedredworld != null) { //red
                highlightOverlay.greenTiles.add(LocalPoint.fromWorld(client, adjustedredworld));
            }
            if (valuesList.size() > 1) {
                int secondValue = valuesList.get(1);
                if (secondValue == 45871 && adjustedblackworld != null) { //black
                    highlightOverlay.yellowTiles.add(LocalPoint.fromWorld(client, adjustedblackworld));
                }
                if (secondValue == 45869 && adjustedyellowworld != null) { //yellow
                    highlightOverlay.yellowTiles.add(LocalPoint.fromWorld(client, adjustedyellowworld));
                }
                if (secondValue == 45870 && adjustedwhiteworld != null) { //white
                    highlightOverlay.yellowTiles.add(LocalPoint.fromWorld(client, adjustedwhiteworld));
                }
                if (secondValue == 45868 && adjustedredworld != null) { //red
                    highlightOverlay.yellowTiles.add(LocalPoint.fromWorld(client, adjustedredworld));
                }
            }
        }
    }
}
