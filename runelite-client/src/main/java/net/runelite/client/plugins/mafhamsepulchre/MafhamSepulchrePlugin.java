package net.runelite.client.plugins.mafhamsepulchre;


import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Sepulchre",
        description = "",
        tags = {"Sepulchre, Mafham"}
)
public class MafhamSepulchrePlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamSepulchreOverlay overlay;

    private static final Set<Integer> sepulchreRegionIDs = Set.of(
            9054, 9053, 8797, 8796, 9052, 9309, 9310,
            9821, 10077, 10078, 10333, 10076,
            9564, 9563, 9562, 9819, 9818,
            10075, 10074, 10331,
            9051, 9050, 9307
    );

    private static final Set<Integer> flameStatueIDs = Set.of(
            38409, 38410, 38411, 38412,
            38416, 38417, 38418, 38419, 38420,
            38421, 38422, 38423, 38424, 38425
    );

    @Getter
    private boolean inSepulchre = false;
    @Getter
    private boolean floor5 = false;
    @Getter
    private Set<FloorTile> teleportTiles = new HashSet<>();
    private Set<GameObject> crossbowers = new HashSet<>();
    @Getter
    private Set<LocalPoint> crossbowTiles = new HashSet<>();
    private Set<WorldPoint> arrowPoints = new HashSet<>();
    @Getter
    private Integer flameTimer;
    private GameObject flameStatue;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        inSepulchre = false;
        teleportTiles.clear();
        crossbowers.clear();
        crossbowTiles.clear();
        arrowPoints.clear();
        flameTimer = null;
        flameStatue = null;
        floor5 = false;
    }

    @Subscribe
    private void onScriptPreFired(final ScriptPreFired event)
    {
        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        WorldPoint worldLocation = WorldPoint.fromLocalInstance(client, localPoint);
        int regionID = worldLocation.getRegionID();
        if (inSepulchre || regionID == 9565)
        {
            // https://github.com/RuneStar/cs2-scripts/blob/master/scripts/%5Bclientscript%2Cfade_overlay%5D.cs2
            if (event.getScriptId() == 948)
            {
                event.getScriptEvent().getArguments()[4] = 255; // transparency (default=0)
                event.getScriptEvent().getArguments()[5] = 0; // duration? (default=50)
            }
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        MessageNode messageNode = chatMessage.getMessageNode();
        if (messageNode.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }
        String message = chatMessage.getMessage();
        if (message.contains("You have completed Floor"))
        {
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (client.getLocalPlayer() == null)
        {
            return;
        }
        if (client.getLocalPlayer().getWorldLocation() == null)
        {
            return;
        }
        if (flameTimer != null)
        {
            int stopTick = (floor5) ? 7 : 8;
            if (flameTimer < stopTick)
            {
                flameTimer++;
            }
            else flameTimer = null;
        }
        if (flameStatue != null)
        {
            Animation animation = ((DynamicObject) flameStatue.getRenderable()).getAnimation();
            if (animation != null)
            {
                if (animation.getId() == 8657)
                {
                    flameTimer = 1;
                }
            }
        }
        if (!teleportTiles.isEmpty())
        {
            teleportTiles.removeIf(floorTile -> client.getTickCount() > floorTile.spawnTick + 3);
        }

        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        WorldPoint worldLocation = WorldPoint.fromLocalInstance(client, localPoint);
        int regionID = worldLocation.getRegionID();

        if (sepulchreRegionIDs.contains(regionID))
        {
            inSepulchre = true;
            floor5 = regionID == 9051 || regionID == 9050 || regionID == 9307;
        }
        else
        {
            reset();
            return;
        }

        crossbowTiles.clear();
        arrowPoints.clear();

        for (NPC npc : client.getNpcs())
        {
            int id = npc.getId();
            if (id == 8687 || id == 9672 || id == 9673 || id == 9674)
            {
                arrowPoints.add(npc.getWorldLocation());
                handleArrows(npc);
            }
        }
        handleCrossbowers();
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
    {
        if (!inSepulchre)
        {
            return;
        }
        GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
        if (graphicsObject.getId() == 1816) //yellow tile
        {
            teleportTiles.add(new FloorTile(graphicsObject.getLocation(), client.getTickCount(), FloorTile.Type.YELLOW));
        }
        if (graphicsObject.getId() == 1815) //blue tile
        {
            teleportTiles.add(new FloorTile(graphicsObject.getLocation(), client.getTickCount(), FloorTile.Type.BLUE));
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        int id = gameObjectSpawned.getGameObject().getId();
        if (id == 38444 || id == 38445 || id == 38446) //crossbow statues
        {
            crossbowers.add(gameObjectSpawned.getGameObject());
        }
        if (flameStatueIDs.contains(gameObjectSpawned.getGameObject().getId()))
        {
            flameStatue = gameObjectSpawned.getGameObject();
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
    {
        crossbowers.remove(gameObjectDespawned.getGameObject());
    }

    private void handleArrows(NPC arrow)
    {
        Angle angle = new Angle(arrow.getOrientation());
        Direction direction = angle.getNearestDirection();
        WorldPoint worldPoint = arrow.getWorldLocation();
        switch (direction)
        {
            case SOUTH:
                collectTiles(worldPoint, Direction.SOUTH);
                break;
            case WEST:
                collectTiles(worldPoint, Direction.WEST);
                break;
            case NORTH:
                collectTiles(worldPoint, Direction.NORTH);
                break;
            case EAST:
                collectTiles(worldPoint, Direction.EAST);
                break;
            default:
                break;
        }
    }

    public void handleCrossbowers()
    {
        if (crossbowers.isEmpty())
        {
            return;
        }
        for (GameObject crossbower : crossbowers)
        {
            WorldPoint worldPoint = crossbower.getWorldLocation();
            Angle angle = new Angle(crossbower.getOrientation());
            Direction direction = angle.getNearestDirection();
            Animation animation = ((DynamicObject) crossbower.getRenderable()).getAnimation();

            if (animation != null)
            {
                if (animation.getId() == 8682 || animation.getId() == 8683) //starting to throw
                {
                    switch (direction)
                    {
                        case SOUTH:
                            collectTiles(worldPoint, Direction.SOUTH);
                            break;
                        case WEST:
                            collectTiles(worldPoint, Direction.WEST);
                            break;
                        case NORTH:
                            collectTiles(worldPoint, Direction.NORTH);
                            break;
                        case EAST:
                            collectTiles(worldPoint, Direction.EAST);
                            break;
                        default:
                            break;
                    }
                }
            }

        }
    }

    private void collectTiles(WorldPoint worldPoint, Direction direction)
    {
        Set<WorldPoint> stoppingTilesTemp = Set.of(
                //Floor 1
                new WorldPoint(2276, 5975, 2),
                new WorldPoint(2276, 5976, 2),
                new WorldPoint(2276, 5977, 2),

                new WorldPoint(2265, 6016, 2),
                new WorldPoint(2265, 6017, 2),
                new WorldPoint(2265, 6018, 2),

                new WorldPoint(2245, 6003, 2),
                new WorldPoint(2245, 6002, 2),
                new WorldPoint(2245, 6001, 2),

                //Floor 2, upper
                new WorldPoint(2554, 5976, 2),
                new WorldPoint(2555, 5976, 2),
                new WorldPoint(2556, 5976, 2),

                new WorldPoint(2515, 6012, 2),
                new WorldPoint(2515, 6013, 2),
                new WorldPoint(2515, 6014, 2),

                new WorldPoint(2531, 5971, 2),
                new WorldPoint(2531, 5972, 2),
                new WorldPoint(2531, 5973, 2),

                //Floor 2, lower
                new WorldPoint(2503, 5973, 1),
                new WorldPoint(2503, 5972, 1),
                new WorldPoint(2503, 5971, 1),

                //Floor 3, upper
                new WorldPoint(2426, 5871, 2),
                new WorldPoint(2425, 5871, 2),
                new WorldPoint(2424, 5871, 2),

                new WorldPoint(2393, 5859, 2),
                new WorldPoint(2393, 5860, 2),
                new WorldPoint(2393, 5861, 2),

                new WorldPoint(2371, 5863, 2),
                new WorldPoint(2370, 5863, 2),
                new WorldPoint(2369, 5863, 2),

                //Floor 3, lower
                new WorldPoint(2399, 5853, 1),
                new WorldPoint(2400, 5853, 1),
                new WorldPoint(2401, 5853, 1),

                //Floor 4, upper
                new WorldPoint(2509, 5871, 2),
                new WorldPoint(2509, 5870, 2),
                new WorldPoint(2509, 5869, 2),

                new WorldPoint(2500, 5885, 2),
                new WorldPoint(2500, 5886, 2),
                new WorldPoint(2500, 5887, 2),

                new WorldPoint(2515, 5824, 2),
                new WorldPoint(2515, 5825, 2),
                new WorldPoint(2515, 5826, 2),

                //Floor 4, lower
                new WorldPoint(2554, 5865, 1),
                new WorldPoint(2553, 5865, 1),
                new WorldPoint(2552, 5865, 1),

                new WorldPoint(2504, 5859, 1),
                new WorldPoint(2505, 5859, 1),
                new WorldPoint(2506, 5859, 1),

                //Floor 5, middle
                new WorldPoint(2245, 5828, 1),
                new WorldPoint(2244, 5828, 1),
                new WorldPoint(2243, 5828, 1),

                //Floor 5, lower
                new WorldPoint(2296, 5825, 0),
                new WorldPoint(2297, 5825, 0),
                new WorldPoint(2298, 5825, 0),

                new WorldPoint(2303, 5875, 0),
                new WorldPoint(2303, 5876, 0),
                new WorldPoint(2303, 5877, 0)

        );
        for (int i = 1; i < 14; i++)
        {
            WorldPoint adjustedWP;
            switch (direction)
            {
                case NORTH:
                    adjustedWP = worldPoint.dy(i);
                    break;
                case EAST:
                    adjustedWP = worldPoint.dx(i);
                    break;
                case WEST:
                    adjustedWP = worldPoint.dx(-i);
                    break;
                case SOUTH:
                    adjustedWP = worldPoint.dy(-i);
                    break;
                default:
                    adjustedWP = worldPoint; //unreachable?
                    break;
            }
            if (arrowPoints.contains(adjustedWP))
            {
                break;
            }
            LocalPoint localPoint = LocalPoint.fromWorld(client, adjustedWP);
            if (localPoint != null)
            {
                WorldPoint instanceWP = WorldPoint.fromLocalInstance(client, localPoint);
                if (stoppingTilesTemp.contains(instanceWP))
                {
                    break;
                }
            }
            LocalPoint adjustedLP = LocalPoint.fromWorld(client, adjustedWP);
            crossbowTiles.add(adjustedLP);
        }
    }
}