package net.runelite.client.plugins.mafhamtob.Sotetseg;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class Sotetseg {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SotetsegOverlay overlay;
    private List<Integer> soteAttackingIds = Arrays.asList(8388, 10865, 10868);
    static final int SOTETSEG_MAGE_ORB = 1606;
    static final int SOTETSEG_RANGE_ORB = 1607;
    static final int SOTETSEG_BIG_AOE_ORB = 1604;
    private static final int GROUNDOBJECT_ID_BLACKMAZE = 33034;
    private static final int GROUNDOBJECT_ID_REDMAZE = 33035;
    private List<Integer> soteIDs = Arrays.asList(NpcID.SOTETSEG, NpcID.SOTETSEG_8388, 10864, 10864, 10867, 10868);
    @Getter
    private Map<Projectile, LocalPoint> projectileHighlights = new HashMap<>();
    private Set<Projectile> checkedProjectiles = new HashSet<>();
    @Getter
    private Set<Projectile> bigBalls = new HashSet<>();
    @Getter
    private NPC sotetsegNPC;
    @Getter
    private Integer sotetsegAttackCounter;
    @Getter(AccessLevel.PACKAGE)
    private final Map<GroundObject, Tile> RedTiles = new LinkedHashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> RedTilesOverworld = new ArrayList<>();
    @Getter
    private List<WorldPoint> HMHiddenTiles = new ArrayList<>();

    private List<WorldPoint> BlackTilesOverworld = new ArrayList<>();

    private List<WorldPoint> BlackTilesUnderworld= new ArrayList<>();

    private List<WorldPoint> RedTilesUnderworld= new ArrayList<>();

    private List<Point> GridPath = new ArrayList<>();

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        GridPath.clear();
        RedTilesUnderworld.clear();
        BlackTilesUnderworld.clear();
        BlackTilesOverworld.clear();
        RedTilesOverworld.clear();
        RedTiles.clear();
        HMHiddenTiles.clear();
        projectileHighlights.clear();
        checkedProjectiles.clear();
        sotetsegAttackCounter = null;
        bigBalls.clear();
        sotetsegNPC = null;
        //System.out.println("Sotetseg reset");
    }


    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        if (sotetsegNPC == null)
        {
            return;
        }
        Projectile p = event.getProjectile();
        if (checkedProjectiles.contains(p))
        {
            return;
        }
        if (p.getId() == SOTETSEG_MAGE_ORB)
        {
            WorldPoint soteWp = WorldPoint.fromLocal(client, sotetsegNPC.getLocalLocation());
            WorldPoint projWp = WorldPoint.fromLocal(client, p.getX1(), p.getY1(), client.getPlane());
            if (p.getId() == SOTETSEG_MAGE_ORB && sotetsegNPC.getAnimation() == 8139 && projWp.equals(soteWp))
            {
                sotetsegAttackCounter--;
                checkedProjectiles.add(p);
            }
        }
    }


    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        bigBalls.clear();
        projectileHighlights.clear();
        for (Projectile p : client.getProjectiles())
        {
            if (p.getId() == SOTETSEG_BIG_AOE_ORB)
            {
                if (!checkedProjectiles.contains(p))
                {
                    sotetsegAttackCounter = 10;
                    checkedProjectiles.add(p);
                }
                bigBalls.add(p);
            }
            if (p.getId() == SOTETSEG_MAGE_ORB || p.getId() == SOTETSEG_RANGE_ORB)
            {
                int x = (int) p.getX();
                int y = (int) p.getY();
                LocalPoint projectilePoint = new LocalPoint(x, y);
                if (p.getInteracting() == client.getLocalPlayer())
                {
                projectileHighlights.put(p, projectilePoint);
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId())
        {
            case NpcID.SOTETSEG:
            case NpcID.SOTETSEG_8388:
            case 10864:
            case 10865:
            case 10867:
            case 10868:
                if (sotetsegNPC == null)
                {
                    sotetsegNPC = npc;
                    sotetsegAttackCounter = 10;
                    //System.out.println("Sotetseg found");
                }
                RedTiles.clear();
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        NPC npc = npcDespawned.getNpc();
        switch (npc.getId())
        {
            case NpcID.SOTETSEG:
            case NpcID.SOTETSEG_8388:
            case 10864:
            case 10865:
            case 10867:
            case 10868:
                if (client.getPlane() != 3)
                {
                    sotetsegNPC = null;
                    sotetsegAttackCounter = 10;
                    RedTiles.clear();
                    //System.out.println("Sotetseg despawn");
                }
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        for (Projectile projectile : client.getProjectiles())
        {
            if (checkedProjectiles.contains(projectile))
            {
                if (projectile.getEndCycle() < client.getGameCycle())
                {
                    checkedProjectiles.remove(projectile);
                }
            }
        }
        boolean sotetsegFighting = false;
        for (NPC npc : client.getNpcs())
        {
            //sote goes null sometimes when mazed so...
            if (soteIDs.contains(npc.getId()))
            {
                sotetsegNPC = npc;
            }
            if (soteAttackingIds.contains(npc.getId()))
            {
                BlackTilesUnderworld.clear();
                BlackTilesOverworld.clear();
                RedTilesOverworld.clear();
                RedTilesUnderworld.clear();
                HMHiddenTiles.clear();
                GridPath.clear();
                sotetsegFighting = true;
                RedTiles.clear();
                //System.out.println("Sotetseg fighting reset");
                break;
            }
        }
        if (!sotetsegFighting)
        {
            if (!BlackTilesUnderworld.isEmpty() && !RedTilesUnderworld.isEmpty() && GridPath.isEmpty() && client.getLocalPlayer().getWorldLocation().getPlane() == 0)
            {
                int minX = 99999;
                int minY = 99999;
                for (WorldPoint p : BlackTilesUnderworld)
                {
                    int x = p.getX();
                    int y = p.getY();
                    if (x < minX)
                    {
                        minX = x;
                    }
                    if (y < minY)
                    {
                        minY = y;
                    }
                }



                boolean messageSent = false;
                for (WorldPoint p : RedTilesUnderworld)
                {
                    WorldPoint pN = new WorldPoint(p.getX(), p.getY() + 1, p.getPlane());
                    WorldPoint pS = new WorldPoint(p.getX(), p.getY() - 1, p.getPlane());
                    WorldPoint pE = new WorldPoint(p.getX() + 1, p.getY(), p.getPlane());
                    WorldPoint pW = new WorldPoint(p.getX() - 1, p.getY(), p.getPlane());

                    if ( !(     (RedTilesUnderworld.contains(pN) && RedTilesUnderworld.contains(pS)) ||
                            (RedTilesUnderworld.contains(pE) && RedTilesUnderworld.contains(pW))        )   )
                    {
                        GridPath.add(new Point(p.getX() - minX, p.getY() - minY));
                        //System.out.println("GridPath added" + GridPath.size());
                        if (!messageSent)
                        {
                            //client.addChatMessage(ChatMessageType.SERVER, "", "Maze path acquired.", null);
                            messageSent = true;
                        }
                    }

                }
            }

            if (!BlackTilesOverworld.isEmpty() && !GridPath.isEmpty() && RedTilesOverworld.isEmpty())
            {
                int minX = 99999;
                int minY = 99999;
                for (WorldPoint p : BlackTilesOverworld)
                {
                    int x = p.getX();
                    int y = p.getY();
                    if (x < minX)
                    {
                        minX = x;
                    }
                    if (y < minY)
                    {
                        minY = y;
                    }
                }
                for (Point p : GridPath)
                {
                    RedTilesOverworld.add(new WorldPoint(minX + p.getX(), minY + p.getY(), 0));
                    //System.out.println("RedTilesOverworld added " + RedTilesOverworld.size());
                }
            }
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject o = event.getGroundObject();
        if (o.getId() >= 41750 && o.getId() <= 41756) // HM hidden maze tile IDs
        {
            Tile t = event.getTile();
            WorldPoint p = t.getWorldLocation();
            HMHiddenTiles.add(p);
        }

        if (o.getId() == GROUNDOBJECT_ID_BLACKMAZE)
        {
            Tile t = event.getTile();
            WorldPoint p = t.getWorldLocation();
            if (t.getPlane() == 0)
            {
                if (!BlackTilesOverworld.contains(p))
                    BlackTilesOverworld.add(p);
                //System.out.println("BlacktilesOverworld added " + BlackTilesOverworld.size());
            }
            else
            {
                if (!BlackTilesUnderworld.contains(p))
                    BlackTilesUnderworld.add(p);
                //System.out.println("BlacktilesUnderworld added " + BlackTilesUnderworld.size());
            }
        }

        if (o.getId() == GROUNDOBJECT_ID_REDMAZE)
        {
            Tile t = event.getTile();
            WorldPoint p = t.getWorldLocation();
            if (p.getPlane() == 0)
            {
                if (!RedTiles.containsValue(t))
                {
                    RedTiles.put(o,t);
                }
            }
            else
            {
                if (!RedTilesUnderworld.contains(p))
                    RedTilesUnderworld.add(p);
                //System.out.println("RedTilesUnderworld added " + RedTilesUnderworld.size());
            }
        }
    }
}