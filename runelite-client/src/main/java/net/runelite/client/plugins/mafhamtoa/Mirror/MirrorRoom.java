package net.runelite.client.plugins.mafhamtoa.Mirror;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.Monkey.GameMessage;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MirrorRoom {
    @Inject
    private Client client;
    @Inject
    private MirrorRoomOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Getter(AccessLevel.PACKAGE)
    public WorldPoint referencePoint;
    @Getter(AccessLevel.PACKAGE)
    public final Set<LocalPoint> mirrorTileHighlights = new HashSet<>();
    public Map<GameObject, WorldPoint> wallsMap = new HashMap<>();
    public Set<WorldPoint> wallsPoints = new HashSet<>();
    public Map<WorldPoint, GameObject> nonStaticMirrorMap = new HashMap<>();
    public Map<WorldPoint, Integer> mirrorTilesOrientationMap = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    public final Set<GameObject> mirrorHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    public final Set<LocalPoint> yellowHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    public final Set<GameObject> wallHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    public final Set<GameObject> dirtyMirrors = new HashSet<>();
    public final int DIRTY_MIRROR_ID = 45457;

    public boolean LRoom1 = false;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset() {
        System.out.println("resetting...");
        wallsMap.clear();
        nonStaticMirrorMap.clear();
        wallsPoints.clear();
        mirrorTileHighlights.clear();
        mirrorHighlights.clear();
        wallHighlights.clear();
        yellowHighlights.clear();
        referencePoint = null;
        dirtyMirrors.clear();
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Path of Het.";
        if (message.contains(challengeComplete))
        {
            System.out.println("Challenge complete");
            reset();
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (event.getNpc().getId() == 11707) { //het's seal
            {
                wallsMap.clear();
                nonStaticMirrorMap.clear();
                wallsPoints.clear();
                mirrorTileHighlights.clear();
                mirrorHighlights.clear();
                wallHighlights.clear();
                yellowHighlights.clear();
                dirtyMirrors.clear();
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (event.getNpc().getId() == 11707) { //het's seal
            {
                reset();
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event.getGameObject().getId() == 45485) { //shielded statue
            referencePoint = event.getGameObject().getWorldLocation();
        }
        if (event.getGameObject().getId() == 45462 || event.getGameObject().getId() == 45464) { //breakable wall middle or edge
            wallsMap.put(event.getGameObject(), event.getGameObject().getWorldLocation());
        }
        if (event.getGameObject().getId() == DIRTY_MIRROR_ID) {
            dirtyMirrors.add(event.getGameObject());
        }
        if (event.getGameObject().getId() == 45456 || event.getGameObject().getId() == DIRTY_MIRROR_ID) //static mirror
        {
            dirtyMirrors.removeIf(dirtyMirror -> event.getGameObject().getId() == 45456 && Objects.equals(event.getGameObject().getWorldLocation().toString(), dirtyMirror.getWorldLocation().toString()));
            int mirrorPositionY = event.getGameObject().getWorldLocation().getRegionY();
            int mirrorPositionX = event.getGameObject().getWorldLocation().getRegionX();
            String mirrorInt = (mirrorPositionX + " " + mirrorPositionY);
            switch (mirrorInt)
            {
                case "40 38" :
                    System.out.println("S room 1 found!");
                    SRoom1();
                    break;
                case "26 32" :
                    System.out.println("S room 2 found!");
                    SRoom2();
                    break;
                case "35 24" :
                    System.out.println("S room 3 found!");
                    SRoom3();
                    break;
                case "39 37" :
                    System.out.println("S room 4 found!");
                    SRoom4();
                    break;
                case "26 25" :
                    System.out.println("Line room 1 found!");
                    LineRoom1();
                    break;
                case "26 28" :
                    System.out.println("Line room 2 found!");
                    LineRoom2();
                    break;
                case "36 39" :
                    System.out.println("Line room 3 found!");
                    LineRoom3();
                    break;
                case "36 29" :
                    System.out.println("Line room 4 found!");
                    LineRoom4();
                    break;
                case "27 40" :
                    System.out.println("L room 1 found!");
                    LRoom1();
                    break;
                case "24 32" :
                    System.out.println("L room 2 found!");
                    LRoom2();
                    break;
                case "41 36" :
                    System.out.println("L room 3 found!");
                    LRoom3();
                    break;
                case "34 41" :
                    System.out.println("L room 4 found!");
                    LRoom4();
                    break;
                }
            }
        if (event.getGameObject().getId() == 45455) { //non-static mirror
            nonStaticMirrorMap.put(event.getGameObject().getWorldLocation(), event.getGameObject());
        }
        if (event.getGameObject().getId() == 29733) { //weird left behind object after picking up a mirror
            nonStaticMirrorMap.remove(event.getGameObject().getWorldLocation());
            }
        if (event.getGameObject().getId() == 45466) { //broken wall mined out
            wallsPoints.remove(event.getGameObject().getWorldLocation());
        }
    }


    public void LRoom1()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-8).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-1).dy(8);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(-1).dy(2);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror2, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(0).dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror2);
        Collections.addAll(yellowHighlights, yellow1, yellow2);
        LRoom1 = true;
    }

    public void LRoom2()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-8).dy(0);
        WorldPoint Wmirror3 = referencePoint.dx(-1).dy(3);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(-1).dy(2);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror3, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(0).dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror3);
        Collections.addAll(yellowHighlights, yellow1, yellow3);
    }

    public void LRoom3()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-8).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-8).dy(8);
        WorldPoint Wmirror3 = referencePoint.dx(1).dy(8);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(1).dy(2);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror2, 768);
        mirrorTilesOrientationMap.put(Wmirror3, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(-1).dy(0));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow1, yellow2, yellow3);
    }

    public void LRoom4()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-8).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-8).dy(8);
        WorldPoint Wmirror3 = referencePoint.dx(1).dy(8);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(1).dy(2);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror2, 768);
        mirrorTilesOrientationMap.put(Wmirror3, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(-1).dy(0));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(0).dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow1, yellow2, yellow3);
    }

    public void SRoom1()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-8).dy(0);
        WorldPoint Wmirror3 = referencePoint.dx(0).dy(-7);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(0).dy(-6);
        WorldPoint Wall2 = referencePoint.dx(-8).dy(-4);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        wallsPoints.add(Wall2);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 768);
        mirrorTilesOrientationMap.put(Wmirror3, 1792);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(-1).dy(0));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror3);
        Collections.addAll(yellowHighlights, yellow1, yellow3);
    }

    public void SRoom2()
    {
        //Get mirror world locations
        WorldPoint Wmirror2 = referencePoint.dx(-9).dy(-7);
        WorldPoint Wmirror3 = referencePoint.dx(0).dy(-7);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(0).dy(-6);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror2, 256);
        mirrorTilesOrientationMap.put(Wmirror3, 1792);
        //Get mirror local locations
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(0).dy(-1));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow2, yellow3);
    }

    public void SRoom3()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-9).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-9).dy(-8);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(0).dy(-6);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 768);
        mirrorTilesOrientationMap.put(Wmirror2, 256);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(-1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror2);
        Collections.addAll(yellowHighlights, yellow1);
    }

    public void SRoom4()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-9).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-9).dy(-7);
        WorldPoint Wmirror3 = referencePoint.dx(0).dy(-7);
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(0).dy(-6);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 768);
        mirrorTilesOrientationMap.put(Wmirror2, 256);
        mirrorTilesOrientationMap.put(Wmirror3, 1792);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(-1).dy(0));
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(0).dy(-1));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1, mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow1, yellow2, yellow3);
    }

    public void LineRoom1()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-11).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-11).dy(7); //no yellow
        WorldPoint Wmirror3 = referencePoint.dx(0).dy(7);
        //Get wall location(s)
        //WorldPoint Wall1 = referencePoint.dx(X).dy(Y);
        //Add wall(s) to matcher map
        //wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror2, 768);
        mirrorTilesOrientationMap.put(Wmirror3, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(0).dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1,mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow1,yellow3);
    }

    public void LineRoom2()
    {
        //Get mirror world locations
        WorldPoint Wmirror1 = referencePoint.dx(-10).dy(0);
        WorldPoint Wmirror2 = referencePoint.dx(-10).dy(7);
        WorldPoint Wmirror3 = referencePoint.dx(0).dy(7);
        //Get wall location(s)
        //WorldPoint Wall1 = referencePoint.dx(X).dy(Y);
        //Add wall(s) to matcher map
        //wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror1, 256);
        mirrorTilesOrientationMap.put(Wmirror2, 768);
        mirrorTilesOrientationMap.put(Wmirror3, 1280);
        //Get mirror local locations
        LocalPoint mirror1 = LocalPoint.fromWorld(client, Wmirror1);
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow1 = LocalPoint.fromWorld(client, Wmirror1.dx(0).dy(-1));
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(-1).dy(0));
        LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(0).dy(1));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror1,mirror2, mirror3);
        Collections.addAll(yellowHighlights, yellow1,yellow2, yellow3);
    }

    public void LineRoom3()
    {
        //Get mirror world locations
        WorldPoint Wmirror2 = referencePoint.dx(-10).dy(7);
        //Get wall location(s)
        //WorldPoint Wall1 = referencePoint.dx(X).dy(Y);
        //Add wall(s) to matcher map
        //wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror2, 768);
        //Get mirror local locations
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        LocalPoint yellow2 = LocalPoint.fromWorld(client, Wmirror2.dx(-1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror2);
        Collections.addAll(yellowHighlights, yellow2);
    }

    public void LineRoom4()
    {
        //Get mirror world locations
        WorldPoint Wmirror2 = referencePoint.dx(-10).dy(-8); // no yellow
        WorldPoint Wmirror3 = referencePoint.dx(4).dy(-8); //no yellow
        //Get wall location(s)
        WorldPoint Wall1 = referencePoint.dx(-10).dy(-3);
        //Add wall(s) to matcher map
        wallsPoints.add(Wall1);
        //Add mirror locations matcher map with the orientation they need to be
        mirrorTilesOrientationMap.put(Wmirror2, 256);
        mirrorTilesOrientationMap.put(Wmirror3, 1792);
        //Get mirror local locations
        LocalPoint mirror2 = LocalPoint.fromWorld(client, Wmirror2);
        LocalPoint mirror3 = LocalPoint.fromWorld(client, Wmirror3);
        //Get yellow local locations (where to walk from to get the mirror in the right orientation)
        //LocalPoint yellow3 = LocalPoint.fromWorld(client, Wmirror3.dx(1).dy(0));
        //Add mirror and yellow local locations to highlights
        Collections.addAll(mirrorTileHighlights, mirror2, mirror3);
        //Collections.addAll(yellowHighlights, yellow3);
    }

    public List<GameObject> MirrorMatcher(Map<WorldPoint, GameObject> nonStaticMirrorMap, Map<WorldPoint, Integer> mirrorTiles) {
        List<GameObject> matchedMirrors = new ArrayList<>();

        for (Map.Entry<WorldPoint, Integer> entry : mirrorTiles.entrySet()) {
            WorldPoint worldPoint = entry.getKey();
            Integer orientation = entry.getValue();

            if (nonStaticMirrorMap.containsKey(worldPoint)) {
                GameObject gameObject = nonStaticMirrorMap.get(worldPoint);
                if (gameObject.getOrientation() == orientation) {
                    matchedMirrors.add(gameObject);
                }
            }
        }

        return matchedMirrors;
    }

    public List<GameObject> WallMatcher(Map<GameObject, WorldPoint> wallsMap, Set<WorldPoint> wallsPoints) {
        List<GameObject> matchedWalls = new ArrayList<>();
        for (Map.Entry<GameObject, WorldPoint> entry : wallsMap.entrySet()) {
            if (wallsPoints.contains(entry.getValue())) {
                matchedWalls.add(entry.getKey());
            }
        }
        return matchedWalls;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        mirrorHighlights.clear();
        wallHighlights.clear();
         for (GameObject gameObject : MirrorMatcher(nonStaticMirrorMap, mirrorTilesOrientationMap))
            {
                mirrorHighlights.add(gameObject);
            }
            wallHighlights.addAll(WallMatcher(wallsMap, wallsPoints));
        }
}