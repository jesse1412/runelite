package net.runelite.client.plugins.mafhamtob.Bloat;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class Bloat {

    @Inject
    private Client client;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private BloatOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    private final int BLOAT_NPC_ID = 8359;
    private final int BLOAT_DOWN_ANIM_ID = 8082;

    public static final Set<Integer> tankObjectIDs = ImmutableSet.of(32957, 32955, 32959, 32960, 32964, 33084, 0);
    public static final Set<Integer> topOfTankObjectIDs = ImmutableSet.of(32958, 32962, 32964, 32965, 33062);
    public static final Set<Integer> ceilingChainsObjectIDs = ImmutableSet.of(32949, 32950, 32951, 32952, 32953, 32954, 32970);
    private final int BLOAT_MAP_REGION = 13125;
    @Getter
    private boolean bloatDown = false;
    @Getter
    private Integer downCounter;
    @Getter
    private NPC bloat;
    private WorldArea pillarArea;
    private WorldArea arenaArea;
    private List<WorldPoint> arenaPoints = new ArrayList<>();
    @Getter
    private Set<WorldPoint> losHighlights = new HashSet<>();

    public void startUp() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invoke(this::hide);
        }
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
        clientThread.invoke(() ->
        {
            if (client.getGameState() == GameState.LOGGED_IN) {
                client.setGameState(GameState.LOADING);
            }
        });
    }

    public void reset() {
        bloatDown = false;
        bloat = null;
        downCounter = null;
        losHighlights.clear();
        arenaPoints.clear();
        arenaArea = null;
        pillarArea = null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        String message = chatMessage.getMessage();
        String challengeComplete = "Wave 'The Pestilent Bloat'";
        //String diedTryAgain = "You have failed. The vampyres take pity";
        if (checkStrings(message, challengeComplete)) {
            reset();
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
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            hide();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() != 32957) //pillar
        {
            return;
        }
        WorldPoint pillarPoint = gameObjectSpawned.getGameObject().getWorldLocation();
        WorldPoint pillarSW = pillarPoint.dx(-2).dy(-2);
        WorldPoint roomSW = pillarSW.dx(-5).dy(-5);
        pillarArea = new WorldArea(pillarSW, 6, 6);
        arenaArea = new WorldArea(roomSW, 16, 16);
        arenaPoints = arenaArea.toWorldPointList();

    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (bloatDown && downCounter != null && downCounter > -1) {
            downCounter--;
        }
        if (bloat == null)
        {
            return;
        }
        WorldArea bloatArea = new WorldArea(bloat.getWorldLocation().getX(), bloat.getWorldLocation().getY(), 5, 5, 0);
        List<WorldPoint> bloatPoints = bloatArea.toWorldPointList();
        if (bloatDown && downCounter != null && downCounter == 4)
        {
            for (WorldPoint worldPoint : arenaPoints)
            {
                if (worldPoint.distanceTo(pillarArea) > 1)
                {
                    continue;
                }
                if (pillarArea.contains(worldPoint))
                {
                    continue;
                }
                for (WorldPoint bloatPoint : bloatPoints)
                {
                    int wx = worldPoint.getX();
                    int wy = worldPoint.getY();
                    int bx = bloatPoint.getX();
                    int by = bloatPoint.getY();
                    //if tile is less than 6 tiles around the corner from bloat
                    if (Math.abs(wx - bx) + Math.abs(wy - by) < 6)
                    {
                        losHighlights.add(worldPoint);
                    }
                    //if tile is in line with bloat, but there's no pillar in a tile we check halfway between them
                    if (wx == bx)
                    {
                        WorldPoint checkPoint;
                        int difference;
                        if (wy > by)
                        {
                            difference = (wy - by) / 2;
                            checkPoint = new WorldPoint(wx, (wy - difference), client.getPlane());
                        }
                        else
                        {
                            difference = (by - wy) / 2;
                            checkPoint = new WorldPoint(wx, (by - difference), client.getPlane());
                        }
                        if (!pillarArea.contains(checkPoint))
                        {
                            losHighlights.add(worldPoint);
                        }
                    }
                    if (wy == by)
                    {
                        WorldPoint checkPoint;
                        int difference;
                        if (wx > bx)
                        {
                            difference = (wx - bx) / 2;
                            checkPoint = new WorldPoint((wx - difference), wy, client.getPlane());
                        }
                        else
                        {
                            difference = (bx - wx) / 2;
                            checkPoint = new WorldPoint((bx - difference), wy, client.getPlane());
                        }
                        if (!pillarArea.contains(checkPoint))
                        {
                            losHighlights.add(worldPoint);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId()) {
            case NpcID.PESTILENT_BLOAT:
            case NpcID.PESTILENT_BLOAT_10812:
            case NpcID.PESTILENT_BLOAT_10813:
            case NpcID.PESTILENT_BLOAT_11184:
                    bloat = npcSpawned.getNpc();
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (npcDespawned.getNpc() == bloat) {
            bloat = null;
            bloatDown = false;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        if (animationChanged.getActor() != bloat) {
            return;
        }
        if (animationChanged.getActor().getAnimation() == BLOAT_DOWN_ANIM_ID) {
            bloatDown = true;
            downCounter = 31;
        }
        else
        {
            losHighlights.clear();
            bloatDown = false;
        }
    }

    private void hide() {
        if (!config.hidePillar()) {
            return;
        }
        boolean isInBloat = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == BLOAT_MAP_REGION);
        if (!isInBloat) {
            return;
        }
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        Player player = client.getLocalPlayer();
        int cnt = 0;
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                for (int z = 0; z < 4; ++z) {

                    Tile tile = tiles[z][x][y];
                    if (tile == null) {
                        continue;
                    }


                    for (GameObject gameObject : tile.getGameObjects()) {
                        if (gameObject == null) {
                            continue;
                        }
                        //	boolean isClickable = gameObject.getClickbox() != null;
                        //	boolean isSmall = gameObject.sizeX() == 1 && gameObject.sizeY() == 1;
                        //	boolean differentPlane = gameObject.getPlane() != player.getWorldLocation().getPlane();
                        //  (!isSmall && differentPlane && !isClickable) ||
                        if (tankObjectIDs.contains(gameObject.getId()) || topOfTankObjectIDs.contains(gameObject.getId()) || ceilingChainsObjectIDs.contains(gameObject.getId())) {
                            scene.removeGameObject(gameObject);
                            ++cnt;
                            break;
                        }
                    }
                }
            }
        }
    }
}