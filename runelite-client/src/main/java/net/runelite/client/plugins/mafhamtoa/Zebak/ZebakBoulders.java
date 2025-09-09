/*
 * Written by https://github.com/Mafham
 * No rights reserved. Use, redistribute, and modify at your own discretion,
 * however I would prefer if you didn't sell this plugin for profit!
 * I made this to teach myself how to develop plugins, this code sux.
 * I do not condone rule-breaking or use of illegal plugins. Thanks :)
 */
package net.runelite.client.plugins.mafhamtoa.Zebak;

import java.time.Instant;
import java.util.*;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
public class ZebakBoulders {
    @Inject
    private Client client;
    @Inject
    private ZebakBouldersOverlay overlay;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamToAConfig.class);
    }
    private Map<NPC, WorldPoint> BoulderMap = new HashMap<>();
    private Map<NPC, WorldPoint> JugMap = new HashMap<>();
    private Map<NPC, Integer> RollerMap = new HashMap<>();
    private ArrayList<WorldPoint> BoulderTiles = new ArrayList<>();
    @Getter
    private ArrayList<WorldPoint> nearMissBoulderTiles = new ArrayList<>();
    private ArrayList<WorldPoint> despawnedPoisonTiles = new ArrayList<>();
    private ArrayList<WorldPoint> SafeTiles = new ArrayList<>();
    private ArrayList<WorldPoint> poisonOrBoulders = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<NPC> greenJugHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<NPC> yellowJugHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<NPC> blueJugHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Map<NPC, Instant> rollingBoulderTimingHL = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<LocalPoint> safeTileHighlights = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    Map<WorldPoint, WorldPoint> yellowLineTiles = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    Map<WorldPoint, WorldPoint> greenLineTiles = new HashMap<>();
    private int ticksToLand = 2; //assumes you are using a bow, code bp later pls
    private boolean resetThisTick = false;
    boolean safeTileNotFound = true;
    private static final List<Integer> POISON_IDS = Arrays.asList(45570, 45571, 45572, 45573, 45574, 45575, 45576);
    private Set<Projectile> projectileCache = new HashSet<>();

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public enum Axis {
        HORIZONTAL,     // X-axis
        VERTICAL,       // Y-axis
        DIAGONAL_ASC,   // Diagonal ascending (/) axis
        DIAGONAL_DESC,  // Diagonal descending (\) axis
        NONE            // Neither X, Y, nor diagonal axis
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Zebak.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        if (checkStrings(message, challengeComplete, diedTryAgain, diedFinal, leftRaid))
        {
            reset();
            poisonOrBoulders.clear();
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
    public void onNpcSpawned(NpcSpawned event) {
        if (!config.boulderToggle()){
            return;
        }
        NPC npcSpawned = event.getNpc();
        if (npcSpawned.getId() == 11735) // jug
        {
            WorldPoint worldPoint = npcSpawned.getWorldLocation();
            JugMap.put(npcSpawned, worldPoint);
        }
        if (npcSpawned.getId() == 11737) // boulder
        {
            //collect the tiles around the boulder which work for the jug to explode on
            //collect the 3 safe tiles you can stand on to check if they're poisoned later
            WorldPoint worldPoint = npcSpawned.getWorldLocation();
            poisonOrBoulders.add(worldPoint);
            BoulderMap.put(npcSpawned, worldPoint);
            WorldPoint tile1 = worldPoint.dx(1).dy(1);
            WorldPoint tile2 = worldPoint.dx(2).dy(1);
            WorldPoint tile3 = worldPoint.dx(3).dy(1);
            WorldPoint tile4 = worldPoint.dx(4).dy(1);
            WorldPoint tile5 = worldPoint.dx(4).dy(0);
            WorldPoint tile6 = worldPoint.dx(4).dy(-1);
            WorldPoint tile7 = worldPoint.dx(3).dy(-1);
            WorldPoint tile8 = worldPoint.dx(2).dy(-1);
            WorldPoint tile9 = worldPoint.dx(1).dy(-1);
            WorldPoint tile10 = worldPoint.dy(1);
            WorldPoint tile11 = worldPoint.dy(-1);
            WorldPoint safetile1 = worldPoint.dx(1).dy(0);
            WorldPoint safetile2 = worldPoint.dx(2).dy(0);
            WorldPoint safetile3 = worldPoint.dx(3).dy(0);
            WorldPoint nearMissTile1 = worldPoint.dy(1);
            WorldPoint nearMissTile2 = worldPoint.dy(-1);
            Collections.addAll(BoulderTiles, tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10, tile11);
            Collections.addAll(SafeTiles, safetile1, safetile2, safetile3);
            Collections.addAll(nearMissBoulderTiles, nearMissTile1, nearMissTile2);
        }
        if (npcSpawned.getId() == 11738 && !resetThisTick) { //wave
            //reset on wave just in case
            reset();
            resetThisTick = true;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (!config.boulderToggle()){
            return;
        }
        NPC npc = event.getNpc();
        if (npc.getId() == 11737 && !resetThisTick) { //boulder
            reset();
            resetThisTick = true;
        }
        if (npc.getId() == 11737) //boulder
        {
            poisonOrBoulders.remove(npc.getWorldLocation());
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (!config.boulderToggle()){
            return;
        }
        if (POISON_IDS.contains(event.getGameObject().getId()))
        {
            poisonOrBoulders.add(event.getGameObject().getWorldLocation());
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (!config.boulderToggle()){
            return;
        }
        GameObject gameObject = event.getGameObject();
        //put all the despawned poison tiles into a map
        if (POISON_IDS.contains(gameObject.getId()))
        {
            despawnedPoisonTiles.add(gameObject.getWorldLocation());
            poisonOrBoulders.remove(event.getGameObject().getWorldLocation());
        }
    }

    private void reset() {
        BoulderMap.clear();
        BoulderTiles.clear();
        JugMap.clear();
        RollerMap.clear();
        despawnedPoisonTiles.clear();
        SafeTiles.clear();
        safeTileHighlights.clear();
        greenJugHighlights.clear();
        yellowJugHighlights.clear();
        blueJugHighlights.clear();
        rollingBoulderTimingHL.clear();
        yellowLineTiles.clear();
        greenLineTiles.clear();
        nearMissBoulderTiles.clear();
        safeTileNotFound = true;
        //System.out.println("resetting...");
}

    private Map<NPC, WorldPoint> greenJugMatcher() {
        //make a new hashmap for lined up jugs and boulders
        Map<NPC, WorldPoint> matchedGreens = new HashMap<>();
        for (NPC boulder : BoulderMap.keySet()) {
            WorldPoint boulderPoint = BoulderMap.get(boulder);
            for (NPC jug : JugMap.keySet()) {
                WorldPoint jugPoint = JugMap.get(jug);
                //check if they are less than 8 tiles away, more than 4 (this is too short without a bp)
                int deltaX = Math.abs(boulderPoint.getX() - jugPoint.getX());
                int deltaY = Math.abs(boulderPoint.getY() - jugPoint.getY());
                int distance = (Math.max(deltaX,deltaY));
                if ((distance < 8) && !BoulderMap.isEmpty()) {
                    //check if they are cardinally/ordinally aligned
                    if (boulderPoint.getX() == jugPoint.getX() || boulderPoint.getY() == jugPoint.getY() || Math.abs(boulderPoint.getX() - jugPoint.getX()) == Math.abs(boulderPoint.getY() - jugPoint.getY())) {
                        //check if safe tile not found and it's not a blue jug
                        if (!blueJugHighlights.contains(jug) && safeTileNotFound) {
                            matchedGreens.put(jug, jugPoint);
                            greenLineTiles.put(jugPoint, boulderPoint);
                        }
                    }
                }
            }
        }
        //if a jug is ordinally aligned with any cardinal tile next to the boulder, it will still make contact with it
        //since it clips the corner of the boulder, but it will explode one tile PAST the boulder so we can only
        //have the jug be west of the boulder, since the safe tiles are on the east, away from Zebak
        for (WorldPoint nearMissPoint : nearMissBoulderTiles)
        {
            for (NPC jug : JugMap.keySet())
            {
                WorldPoint jugPoint = JugMap.get(jug);
                //check if they are less than 8 tiles away, more than 4 (this is too short without a bp)
                int deltaX = Math.abs(nearMissPoint.getX() - jugPoint.getX());
                int deltaY = Math.abs(nearMissPoint.getY() - jugPoint.getY());
                int distance = (Math.max(deltaX,deltaY));
                if ((distance < 8) && !BoulderMap.isEmpty())
                {
                    //check if they are ordinally aligned and the jug is west of the boulder
                    if (Math.abs(nearMissPoint.getX() - jugPoint.getX()) == Math.abs(nearMissPoint.getY() - jugPoint.getY())
                        && jugPoint.getX() < nearMissPoint.getX())
                    {
                        if (!greenLineTiles.containsKey(jug.getWorldLocation()) && !blueJugHighlights.contains(jug) && safeTileNotFound)
                        {
                            matchedGreens.put(jug, jugPoint);
                            greenLineTiles.put(jugPoint, nearMissPoint);
                        }
                    }
                }
            }
        }
        return matchedGreens;
    }

    private Map<NPC, WorldPoint> blueJugMatcher() {
        Map<NPC, WorldPoint> matchedBlues = new HashMap<>();
        for (WorldPoint boulderPoint : SafeTiles) {
            for (NPC jug : JugMap.keySet()) {
                WorldPoint jugPoint = JugMap.get(jug);
                //check if they are less than 2 tiles away
                int distance = jugPoint.distanceTo(boulderPoint);
                if ((distance < 2) && !BoulderMap.isEmpty()) {
                    matchedBlues.put(jug, boulderPoint);
                }
            }
        }
        return matchedBlues;
    }

    private Map<NPC, WorldPoint> yellowJugMatcher() {
        //make a new hashmap for lined up jugs and boulder tiles
        Map<NPC, WorldPoint> matchedYellows = new HashMap<>();
        for (WorldPoint boulderPoint : BoulderTiles) {
            for (NPC jug : JugMap.keySet()) {
                WorldPoint jugPoint = JugMap.get(jug);
                    //check if they are less than 8 tiles away
                    int deltaX = Math.abs(boulderPoint.getX() - jugPoint.getX());
                    int deltaY = Math.abs(boulderPoint.getY() - jugPoint.getY());
                    int distance = (Math.max(deltaX,deltaY));
                    if ((distance < 8 && distance > 3) && !BoulderMap.isEmpty()) {
                        //check if they are cardinally aligned FIRST
                        if (boulderPoint.getX() == jugPoint.getX() || boulderPoint.getY() == jugPoint.getY()) {
                            //check if it's not already a green line and a safe tile isn't found
                            if (!greenLineTiles.containsKey(jug.getWorldLocation()) && !blueJugHighlights.contains(jug) && safeTileNotFound) {
                                matchedYellows.put(jug, jugPoint);
                                yellowLineTiles.put(jugPoint, boulderPoint);
                            }
                        }
                        //now check if they are ordinally aligned after first checking if cardinally, we want to prioritise cardinal for a bigger attack window
                        if (Math.abs(boulderPoint.getX() - jugPoint.getX()) == Math.abs(boulderPoint.getY() - jugPoint.getY())) {
                            //check if it's not already a green line and a safe tile isn't found AND it's not in yellowlinetiles/matched yellows
                            if (!greenLineTiles.containsKey(jug.getWorldLocation()) && !yellowLineTiles.containsKey(jug.getWorldLocation()) && !matchedYellows.containsKey(jug) && !blueJugHighlights.contains(jug) && safeTileNotFound) {
                                matchedYellows.put(jug, jugPoint);
                                yellowLineTiles.put(jugPoint, boulderPoint);
                            }
                        }
                    }
                }
            }
        return matchedYellows;
    }



    @Subscribe
    public void onGameTick(GameTick event) {
        if (!config.boulderToggle()){
            return;
        }
        resetThisTick = false;
        blueJugHighlights.clear();
        yellowJugHighlights.clear();
        yellowLineTiles.clear();
        greenJugHighlights.clear();
        greenLineTiles.clear();

        //do a full reset when zebak starts the attack, just in case
        for (Projectile projectile : client.getProjectiles())
        {
            if (projectileCache.contains(projectile))
            {
                continue;
            }
            if (projectile.getId() == 2173) //thrown jug
            {
                //System.out.println("jug found, resetting");
                reset();
                projectileCache.add(projectile);
            }
        }
        if (!projectileCache.isEmpty())
        {
            projectileCache.removeIf(projectile -> projectile.getRemainingCycles() <= 0);
        }

        //check if any safe tiles have any "despawned" poison on them
        for (WorldPoint despawnedPoisonPoint : despawnedPoisonTiles)
        {
            if (SafeTiles.contains(despawnedPoisonPoint))
            {
                //highlight them
                safeTileHighlights.add(LocalPoint.fromWorld(client, despawnedPoisonPoint));
            }
        }
        //once a safe tile is found, clear the jug and line HLs
        if (!safeTileHighlights.isEmpty())
        {
            safeTileNotFound = false;
        }

        Map<NPC, WorldPoint> bluePoints = blueJugMatcher();
        //check if safe tile is found
        if (!bluePoints.isEmpty() && safeTileNotFound) {
            for (NPC npc : bluePoints.keySet()) {
                blueJugHighlights.add(npc);
            }
        }

        Map<NPC, WorldPoint> greenPoints = greenJugMatcher();
        //check if safe tile is found
        if (!greenPoints.isEmpty() && safeTileNotFound) {
            for (NPC npc : greenPoints.keySet()) {
                greenJugHighlights.add(npc);
            }
        }
        Map<NPC, WorldPoint> yellowPoints = yellowJugMatcher();
        if (!yellowPoints.isEmpty()) {
            for (NPC npc : yellowPoints.keySet()) {
                //check if it's not already in the green map or that a safe tile isn't found
                if (!greenPoints.containsKey(npc) && safeTileNotFound) {
                    yellowJugHighlights.add(npc);
                }
            }
        }
        //Check if there are no poison tiles on the tiles we need
        Iterator<Map.Entry<WorldPoint, WorldPoint>> greenLineIterator = greenLineTiles.entrySet().iterator();
        Iterator<Map.Entry<WorldPoint, WorldPoint>> yellowLineIterator = yellowLineTiles.entrySet().iterator();

        while (greenLineIterator.hasNext()) {
            Map.Entry<WorldPoint, WorldPoint> greenEntry = greenLineIterator.next();
            checkPoison(greenEntry, greenLineIterator);
        }

        while (yellowLineIterator.hasNext()) {
            Map.Entry<WorldPoint, WorldPoint> yellowEntry = yellowLineIterator.next();
            checkPoison(yellowEntry, yellowLineIterator);
        }

        //if the rolling jug is spawned
        //if (!RollerMap.isEmpty() && !BoulderTiles.isEmpty())
        {
            for (NPC roller : RollerMap.keySet())
            {
                Player player = client.getLocalPlayer();
                WorldPoint playerpoint = player.getWorldLocation();
                WorldPoint rollerpoint = roller.getWorldLocation();
                //calculate distance between us and the jug
                int deltaX = (Math.abs(playerpoint.getX() - rollerpoint.getX()));
                int deltaY = (Math.abs(playerpoint.getY() - rollerpoint.getY()));
                int distance = (Math.max(deltaX,deltaY));
                if (distance < 3)
                {
                    ticksToLand = 2;
                }
                if (distance >= 3)
                {
                    ticksToLand = 3;
                }
                for (WorldPoint boulderPoint : BoulderTiles)
                {
                    //if the roller is in the timing window
                    if (
                            Math.abs(rollerpoint.getY() - boulderPoint.getY()) < (1 + ticksToLand) &&
                            Math.abs(rollerpoint.getX() - boulderPoint.getX()) < (1 + ticksToLand) &&
                            !rollingBoulderTimingHL.containsKey(roller)
                    ) {
                            rollingBoulderTimingHL.put(roller, Instant.now());
                            RollerMap.remove(roller);
                    }
                }
            }
        }
        for (Map.Entry<NPC, Instant> entry : rollingBoulderTimingHL.entrySet())
        {
            Instant rollerWindow = entry.getValue().plusMillis(1200);
            if (Instant.now().isAfter(rollerWindow))
            {
                rollingBoulderTimingHL.remove(entry.getKey());
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event)
    {
        if (!config.boulderToggle()){
            return;
        }
        if (event.getNpc().getId() == 11736 && !BoulderTiles.isEmpty()) //rolling boulder
        {
        NPC roller = event.getNpc();
        //remove it from the map to stop drawing a line/highlight
        JugMap.remove(roller);
        RollerMap.put(roller, 0);
        }
    }

    private static Axis determineAxis(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            return Axis.VERTICAL;
        } else if (y1 == y2) {
            return Axis.HORIZONTAL;
        } else if (x1 - y1 == x2 - y2) {
            return Axis.DIAGONAL_ASC;
        } else if (x1 + y1 == x2 + y2) {
            return Axis.DIAGONAL_DESC;
        } else {
            return Axis.NONE;
        }
    }

    private void checkPoison(Map.Entry<WorldPoint, WorldPoint> entry, Iterator<Map.Entry<WorldPoint, WorldPoint>> iterator) {
        if (!poisonOrBoulders.isEmpty()) {
            WorldPoint jugPoint = entry.getKey();
            WorldPoint boulderPoint = entry.getValue();
            WorldPoint checkTile1;
            WorldPoint checkTile2;
            Axis axis = determineAxis(jugPoint.getX(), jugPoint.getY(), boulderPoint.getX(), boulderPoint.getY());
            switch (axis) {
                case VERTICAL:
                    checkTile1 = jugPoint.dy(1);
                    checkTile2 = jugPoint.dy(-1);
                    if (poisonOrBoulders.contains(checkTile1) && poisonOrBoulders.contains(checkTile2)) {
                        iterator.remove();
                        greenJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                        yellowJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                    }
                    break;
                case HORIZONTAL:
                    checkTile1 = jugPoint.dx(1);
                    checkTile2 = jugPoint.dx(-1);
                    if (poisonOrBoulders.contains(checkTile1) && poisonOrBoulders.contains(checkTile2)) {
                        iterator.remove();
                        greenJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                        yellowJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                    }
                    break;
                case DIAGONAL_ASC:
                    checkTile1 = jugPoint.dx(1).dy(1);
                    checkTile2 = jugPoint.dx(-1).dy(-1);
                    if (poisonOrBoulders.contains(checkTile1) && poisonOrBoulders.contains(checkTile2)) {
                        iterator.remove();
                        greenJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                        yellowJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                    }
                    break;
                case DIAGONAL_DESC:
                    checkTile1 = jugPoint.dx(1).dy(-1);
                    checkTile2 = jugPoint.dx(-1).dy(1);
                    if (poisonOrBoulders.contains(checkTile1) && poisonOrBoulders.contains(checkTile2)) {
                        iterator.remove();
                        greenJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                        yellowJugHighlights.removeIf(jug -> Objects.equals(jug.getWorldLocation().toString(), jugPoint.toString()));
                    }
                    break;
                case NONE:
                    System.out.println("This should be impossible to print");
                    break;
            }
        }
    }
}