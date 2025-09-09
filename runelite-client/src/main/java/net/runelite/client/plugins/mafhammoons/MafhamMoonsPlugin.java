package net.runelite.client.plugins.mafhammoons;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Moons",
        description = "Mafham Moons",
        tags = {"Mafham", "Moons"}
)
public class MafhamMoonsPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamMoonsOverlay overlay;
    @Getter
    private Integer specTimer;
    @Getter
    private boolean inMoons = false;
    @Getter
    private NPC chosenJaguar;
    private NPC jaguarFloorSymbol;
    @Getter
    private HashMap<GameObject, Integer> bloods = new HashMap<>();
    @Getter
    private HashMap<NPC, Integer> clones = new HashMap<>();
    private HashMap<NPC, WorldPoint> tornadoes = new HashMap<>();
    @Getter
    private HashMap<WorldPoint, WorldPoint> tornadoTiles = new HashMap<>();
    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
    private GameObject cloneSymbolObject = null;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        hooks.unregisterRenderableDrawListener(drawListener);
        reset();
    }

    public void reset()
    {
        bloods.clear();
        inMoons = false;
        specTimer = null;
        cloneSymbolObject = null;
        clones.clear();
        tornadoTiles.clear();
        tornadoes.clear();
        chosenJaguar = null;
        jaguarFloorSymbol = null;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        for (int mapRegion : client.getMapRegions())
        {
            final int MOONS_MAP_ID = 5783;
            if (mapRegion == MOONS_MAP_ID) {
                inMoons = true;
                break;
            }
        }
        if (!inMoons)
        {
            reset();
            return;
        }
        tornadoTiles.clear();
        int bloodDelay = 2;
        final int jaguarID = 13021;

        if (!tornadoes.isEmpty())
        {
            for (Map.Entry<NPC, WorldPoint> entry : tornadoes.entrySet())
            {
                NPC npc = entry.getKey();
                WorldPoint spawnPoint = entry.getValue();
                WorldPoint currentPoint = npc.getWorldLocation();
                WorldPoint tileOneAhead = null;
                WorldPoint tileTwoAhead= null;
                if (currentPoint.getY() > spawnPoint.getY()) //NORTH
                {
                    tileOneAhead = currentPoint.dy(1);
                    tileTwoAhead = currentPoint.dy(2);
                }
                else if (currentPoint.getY() < spawnPoint.getY()) //SOUTH
                {
                    tileOneAhead = currentPoint.dy(-1);
                    tileTwoAhead = currentPoint.dy(-2);
                }
                if (tileOneAhead != null || tileTwoAhead != null)
                {
                    tornadoTiles.put(tileOneAhead, tileTwoAhead);
                }

            }
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc.getId() == jaguarID)
            {
                bloodDelay = 1;
                if (jaguarFloorSymbol != null)
                {
                    WorldPoint jaguarPoint = npc.getWorldLocation();
                    WorldPoint symbolPoint = jaguarFloorSymbol.getWorldLocation();
                    if (symbolPoint.distanceTo(jaguarPoint) < 3)
                    {
                        chosenJaguar = npc;
                    }
                }

            }
        }
        if (!bloods.isEmpty())
        {
            Iterator<Map.Entry<GameObject, Integer>> iterator = bloods.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<GameObject, Integer> entry = iterator.next();
                int startTick = entry.getValue();

                if (client.getTickCount() > (startTick + bloodDelay))
                {
                    iterator.remove();
                }
            }
        }
        if (!clones.isEmpty())
        {
            Iterator<Map.Entry<NPC, Integer>> iterator = clones.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<NPC, Integer> entry = iterator.next();
                int startTick = entry.getValue();

                if (client.getTickCount() > (startTick + 2))
                {
                    iterator.remove();
                }
            }
        }
        if (specTimer != null && specTimer > -1)
        {
            specTimer--;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (!inMoons)
        {
            return;
        }
        GameObject gameObject = event.getGameObject();
        int gameObjectID = gameObject.getId();
        final int bloodID = 51046;
        final int cloneFloorSymbolID = 51041;
        final int tornadoArenaID = 51053;
        final int bloodRainArenaID = 51054;
        final int cuppaObjectID = 51362;
        if (gameObjectID == bloodID)
        {
            bloods.put(event.getGameObject(), client.getTickCount());
        }
        if (gameObjectID == cloneFloorSymbolID)
        {
            specTimer = 54;
            cloneSymbolObject = gameObject;
        }
        if (gameObjectID == tornadoArenaID)
        {
            specTimer = 55;
        }
        if (gameObjectID == bloodRainArenaID)
        {
            specTimer = 21;
        }
        if (gameObjectID == cuppaObjectID)
        {
            reset();
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (!inMoons)
        {
            return;
        }
        final int bloodID = 51046;
        final int cloneFloorSymbolID = 51041;
        if (event.getGameObject().getId() == bloodID)
        {
            bloods.remove(event.getGameObject());
        }
        if (event.getGameObject() == cloneSymbolObject)
        {
            cloneSymbolObject = null;
        }
        if (event.getGameObject().getId() == cloneFloorSymbolID)
        {
            cloneSymbolObject = null;
        }

    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        if (!inMoons)
        {
            return;
        }
        NPC npc = event.getNpc();
        int npcID = npc.getId();
        final int jaguarID = 13021;
        final int jaguarFloorSymbolID = 13015;
        final int frozenWeaponsNPCID = 13025;
        final int eclipseMoonId = 13012;
        final int tornadoID = 13027;
        if (npcID == jaguarID)
        {
            specTimer = 41;
        }
        if (npcID == frozenWeaponsNPCID)
        {
            specTimer = 50;
        }
        if (npcID == eclipseMoonId)
        {
            if (cloneSymbolObject != null)
            {
                clones.put(npc, client.getTickCount());
            }
        }
        if (npcID == tornadoID)
        {
            tornadoes.put(npc, npc.getWorldLocation());
        }
        if (npcID == jaguarFloorSymbolID)
        {
            jaguarFloorSymbol = npc;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (!inMoons)
        {
            return;
        }
        NPC npc = event.getNpc();
        clones.remove(npc);
        tornadoes.remove(npc);
        if (npc == chosenJaguar)
        {
            chosenJaguar = null;
        }
        if (npc == jaguarFloorSymbol)
        {
            jaguarFloorSymbol = null;
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI) {
        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            int tornadoID = 13027;
            if (npc.getId() == tornadoID)
            {
                return false;
            }
        }
        return true;
    }

}