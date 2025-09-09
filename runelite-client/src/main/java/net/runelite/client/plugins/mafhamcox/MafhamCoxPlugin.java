package net.runelite.client.plugins.mafhamcox;


import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.mafhamcox.olmcounter.CoxDebugBox;
import net.runelite.client.plugins.mafhamcox.olmcounter.Olm;
import net.runelite.client.plugins.mafhamcox.olmcrystals.OlmCrystals;
import net.runelite.client.plugins.mafhamcox.reloader.InputListener;
import net.runelite.client.plugins.mafhamcox.reloader.ReloaderOverlay;
import net.runelite.client.plugins.mafhamcox.tekton.Tekton;
import net.runelite.client.plugins.mafhamcox.vasa.Vasa;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;

@PluginDescriptor(
        name = "Mafham CoX",
        description = "Utilities for the Chambers of Xeric",
        tags = {"Mafham", "chambers","raids","cox"}
)

public class MafhamCoxPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private MafhamCoxConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamCoxOverlay overlay;
    @Inject
    private MafhamCoxOverlayMarker overlayMarker;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Olm olm;
    @Inject
    private Tekton tekton;
    @Inject
    private Vasa vasa;
    @Inject
    private OlmCrystals olmCrystals;
    @Inject
    private CoxDebugBox coxDebugBox;
    @Inject
    private ReloaderOverlay reloaderOverlay;
    @Inject
    InputListener inputListener;
    @Inject
    private MouseManager mouseManager;
    private static final int COX_REGION_ID = 4919;
    @Getter
    public NPC VESPULA_NPC;
    @Getter
    private NPC MEAT_TREE_NPC;
    private final int MUTTADILE_TREE_NPCID = 7564;
    private final int CRABS_NPCID = 7576;
    private final int VESPULA_PORTALID = 7533;
    private final int VESPULA_OBJECTID = 30072;
    private final int SKULL_OBJECTID = 30049;
    private final int ORB_SHOOTERID = 29752;
    private final int BLACK_CRYSTALID = 29758;
    private final int CRABS_END_CRYSTALID = 29757;
    @Getter
    private final int MINING_ID = 29738;
    @Getter
    private final int MINING_DONE_ID = 29739;
    @Getter
    private final int BOULDER_ID = 29740;
    @Getter
    private final int TREE_SHORTCUT_ID = 29736;
    @Getter
    private final int TREE_SHORTCUT_DONE_ID = 29737;
    private final int CRYSTAL_BOMB_ID = 29766;
    private final int ENERGY_WELL_ID = 30066;
    private final int OLM_ID = 7551;
    @Getter
    private GameObject ORB_SHOOTER_OBJECT;
    private GameObject BLACK_CRYSTAL_OBJECT;
    @Getter
    public GameObject VESPULA_OBJECT;
    @Getter
    public boolean highlightVespula;
    private int totalEnhCycles = 0;
    private int enhRegenInterval;
    @Getter
    private int enhRegenTimer = -1;
    @Getter
    private int treeCounter = -1;
    private final int enhVarb = 5417;
    private boolean enhTimerStarted = false;
    @Getter
    private boolean vespulaStarted = false;
    private boolean crabsRoomComplete = false;
    public boolean isCoxLoaded = false;
    @Getter
    public Rectangle clickBox;
    @Setter
    @Getter
    public boolean isHovered = false;
    private boolean vanguardsChecked = false;
    @Getter
    private WorldPoint meleeVanguardSpawn;
    @Getter
    private HashMap<GameObject, Integer> objectHighlights = new HashMap<>();
    @Getter
    private HashMap<GroundObject, Integer> groundObjectHighlights = new HashMap<>();
    @Getter
    private Set<LocalPoint> vespTileHighlights = new HashSet<>();
    private static final List<Integer> CHEST_IDS = Arrays.asList(29770, 29779, 29780, 37978, 29769);
    private static final List<Integer> coxRegionIDs = Arrays.asList(13136,13392,13137,13393,13138,13394,13139,13395,13140,13396,
            13141,13397,13142,13398,13143,13399,13144,13400,13145,13401,12889);
    private ArrayList<WorldPoint> treeTiles = new ArrayList<>();
    private ArrayList<GameObject> skullObjects = new ArrayList<>();
    private Set<WorldPoint> crabTileWorlds = new HashSet<>();
    @Getter
    private Set<LocalPoint> crabTileHighlights = new HashSet<>();
    @Getter
    private Set<NPC> crabNpcHighlights = new HashSet<>();
    private Set<NPC> crabNpcs = new HashSet<>();
    @Getter
    private Set<GameObject> crystalBombs = new HashSet<>();
    private static final int PASSAGE_ID = 29789;

    @Provides
    MafhamCoxConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamCoxConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        overlayManager.add(overlayMarker);
        overlayManager.add(coxDebugBox);
        overlayManager.add(reloaderOverlay);
        mouseManager.registerMouseListener(inputListener);
        tekton.startUp();
        vasa.startUp();
        olmCrystals.startUp();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(overlayMarker);
        overlayManager.remove(coxDebugBox);
        overlayManager.remove(reloaderOverlay);
        mouseManager.unregisterMouseListener(inputListener);
        tekton.shutDown();
        vasa.shutDown();
        olmCrystals.shutDown();
        reset();
    }

    private void reset()
    {
        //System.out.println("reset ran");
        treeCounter = -1;
        totalEnhCycles = 0;
        enhTimerStarted = false;
        vespulaStarted = false;
        VESPULA_OBJECT = null;
        VESPULA_NPC = null;
        highlightVespula = false;
        objectHighlights.clear();
        groundObjectHighlights.clear();
        vespTileHighlights.clear();
        skullObjects.clear();
        crabTileHighlights.clear();
        crabTileWorlds.clear();
        crabNpcHighlights.clear();
        crabNpcs.clear();
        crystalBombs.clear();
        crabsRoomComplete = false;
        BLACK_CRYSTAL_OBJECT = null;
        ORB_SHOOTER_OBJECT = null;
        VESPULA_OBJECT = null;
        MEAT_TREE_NPC = null;
        VESPULA_NPC = null;
        vanguardsChecked = false;
        meleeVanguardSpawn = null;
        tekton.reset();
        vasa.reset();
        olmCrystals.reset();
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved projectileMoved)
    {
        if (!inRaid())
        {
            return;
        }
        vasa.onProjectileMoved(projectileMoved);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.GAMEMESSAGE)
        {
            if ("the great olm is giving its all. this is its final stand.".equals(Text.standardize(event.getMessageNode().getValue()))) {
                olm.headPhase = true;
                //System.out.println("Head phase set");
                if (!config.showHeadPhase()) {
                    this.olm.hardRest();
                    //System.out.println("Resetting olm for head phase");
                }
            }
        }
        String msg = Text.standardize(event.getMessageNode().getValue());

        if (msg.equalsIgnoreCase("you have been kicked from the channel.") || msg.contains("decided to start the raid without you. sorry.")
                || msg.equalsIgnoreCase("you are no longer eligible to lead the party.") || msg.equalsIgnoreCase("the raid has begun!"))
        {
            isCoxLoaded = false;
        }
        else if (msg.equalsIgnoreCase("inviting party...") || msg.equalsIgnoreCase("your party has entered the dungeons! come and join them now."))
        {
            isCoxLoaded = true;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event)
    {
        if (!inRaid())
        {
            return;
        }
        vasa.onNpcChanged(event);
        int npcid = event.getNpc().getId();

        if (npcid == 7553) //olm
        {
            olm.startShit();
            //System.out.println("found olm");
        }
        if (npcid == 7527 && !vanguardsChecked) //melee vanguard
        {
           meleeVanguardSpawn = event.getNpc().getWorldLocation().dx(1).dy(1);
           vanguardsChecked = true;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (!inRaid())
        {
            return;
        }
        GameState gameState = event.getGameState();
        if (gameState == GameState.LOADING)
        {
            //System.out.println("loading line crossed");
            objectHighlights.clear();
            groundObjectHighlights.clear();
            vespTileHighlights.clear();
            crabTileHighlights.clear();
            crabTileWorlds.clear();
            skullObjects.clear();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        int varbitId = event.getVarbitId();
        if (varbitId != enhVarb)
        {
            return;
        }
        int enhVarbValue = client.getVarbitValue(enhVarb);
        if (enhVarbValue >= totalEnhCycles)
        {
            enhRegenInterval = getEnhanceRegenRate();
            totalEnhCycles = enhVarbValue;
            enhRegenTimer = enhRegenInterval;
            enhTimerStarted = true;
        }
        if (enhVarbValue == 0) {
            enhTimerStarted = false;
            enhRegenTimer = -1;
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
    {
        if (!inRaid())
        {
            return;
        }
        olmCrystals.onGraphicsObjectCreated(graphicsObjectCreated);
    }

    @Subscribe
    public void onGameTick (GameTick event)
    {
        if (!inRaidUsingMapCheck())
        {
            reset();
        }
        if (!inRaid())
        {
            return;
        }
        vasa.onGameTick(event);
        tekton.onGameTick(event);
        olmCrystals.onGameTick(event);
        crabTileWorlds.clear();
        crabTileHighlights.clear();
        crabNpcHighlights.clear();
        if (olm.active)
        {
            this.olm.update();
        }
        if (enhTimerStarted)
        {
            decrementEnhCounter();
        }
        if (vespulaStarted) {
            int prayer = client.getBoostedSkillLevel(Skill.PRAYER);
            int health = client.getBoostedSkillLevel(Skill.HITPOINTS);
            if (config.vespSetting() == MafhamCoxConfig.VespulaSetting.EASY) {
                highlightVespula = prayer > 0 || (prayer == 0 && enhRegenTimer == 0) || (prayer == 0 && health > 32);

                if (health < 9) {
                    highlightVespula = false;
                }
            }
            if (config.vespSetting() == MafhamCoxConfig.VespulaSetting.HARD) {
                highlightVespula = prayer > 0 || (prayer == 0 && health > Math.min((enhRegenTimer + 1) * 8, 32));

                if (health < 9) {
                    highlightVespula = false;
                }
            }
        }
        if (MEAT_TREE_NPC != null)
        {
            decrementTreeCounter();
        }
        if (!crabsRoomComplete && ORB_SHOOTER_OBJECT != null && client.getPlane() == ORB_SHOOTER_OBJECT.getPlane())
        {
            checkCrabsRoom();
            for (NPC npc : crabNpcs)
            {
                WorldPoint worldPoint = npc.getWorldLocation();
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                if (crabTileHighlights.contains(localPoint))
                {
                    crabNpcHighlights.add(npc);
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned (NpcSpawned event)
    {
        if (!inRaid())
        {
            return;
        }
        tekton.onNpcSpawned(event);
        NPC npc = event.getNpc();
        if (npc.getId() == VESPULA_PORTALID)
        {
            VESPULA_NPC = npc;
            checkVespulaRoom();
            vespulaStarted = true;
        }
        if (npc.getId() == MUTTADILE_TREE_NPCID)
        {
            MEAT_TREE_NPC = npc;
            WorldPoint worldPoint = MEAT_TREE_NPC.getWorldLocation();
            WorldPoint tile1 = worldPoint.dx(0).dy(-1);
            WorldPoint tile2 = worldPoint.dx(1).dy(-1);
            WorldPoint tile3 = worldPoint.dx(2).dy(0);
            WorldPoint tile4 = worldPoint.dx(2).dy(1);
            WorldPoint tile5 = worldPoint.dx(1).dy(2);
            WorldPoint tile6 = worldPoint.dx(0).dy(2);
            WorldPoint tile7 = worldPoint.dx(-1).dy(1);
            WorldPoint tile8 = worldPoint.dx(-1).dy(0);
            Collections.addAll(treeTiles, tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8);
        }
        if (npc.getId() == CRABS_NPCID)
        {
            crabNpcs.add(npc);
        }
        if (npc.getId() == OLM_ID && config.cameraOlm())
        {
            if (npc.getWorldLocation().getX() > client.getLocalPlayer().getWorldLocation().getX()) {
                clientThread.invoke(() -> {
                    {
                        //1050 = compass, 1 = n, 2 = e, 3 = s, 4 = w.
                        client.runScript(1050, 2);
                    }
                });
            }
            else {
                clientThread.invoke(() -> client.runScript(1050, 4));
            }
        }
    }

    @Subscribe
    public void onNpcDespawned (NpcDespawned event)
    {
        if (!inRaid())
        {
            return;
        }
        vasa.onNpcDespawned(event);
        tekton.onNpcDespawned(event);
        NPC npc = event.getNpc();
        if (npc.getId() == VESPULA_PORTALID)
        {
            vespulaStarted = false;
            vespTileHighlights.clear();
        }
        if (npc.getId() == CRABS_NPCID)
        {
            crabNpcs.remove(npc);
        }
        if (npc.getId() == 7527) //melee vanguard
        {
            meleeVanguardSpawn = null;
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject groundObject = event.getGroundObject();
        int objectId = groundObject.getId();
        if (objectId == ENERGY_WELL_ID)
        {
            groundObjectHighlights.put(groundObject, objectId);
        }
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        GroundObject groundObject = event.getGroundObject();
        int objectId = groundObject.getId();
        if (objectId == ENERGY_WELL_ID)
        {
            groundObjectHighlights.remove(groundObject, objectId);
        }
    }

    @Subscribe
    public void onGameObjectSpawned (GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        int objectId = gameObject.getId();
        if (objectId == VESPULA_OBJECTID)
        {
            VESPULA_OBJECT = gameObject;
        }
        if (CHEST_IDS.contains(gameObject.getId()))
        {
            objectHighlights.put(gameObject, objectId);
        }
        if (objectId == PASSAGE_ID)
        {
            objectHighlights.put(gameObject, objectId);
        }
        if (objectId == SKULL_OBJECTID)
        {
            skullObjects.add(gameObject);
        }
        if (objectId == ORB_SHOOTERID)
        {
            ORB_SHOOTER_OBJECT = gameObject;
        }
        if (objectId == BLACK_CRYSTALID)
        {
            BLACK_CRYSTAL_OBJECT = gameObject;
        }
        switch (objectId)
        {
            case Olm.HEAD_GAMEOBJECT_RISING:
            case Olm.HEAD_GAMEOBJECT_READY:
                if (olm.head == null)
                {
                    olm.startPhase();
                }
                olm.head = event.getGameObject();
                break;
            case Olm.LEFT_HAND_GAMEOBJECT_RISING:
            case Olm.LEFT_HAND_GAMEOBJECT_READY:
                olm.hand = event.getGameObject();
                break;
        }
        if (objectId == BOULDER_ID || objectId == MINING_ID || objectId == MINING_DONE_ID || objectId == TREE_SHORTCUT_ID || objectId == TREE_SHORTCUT_DONE_ID)
        {
            objectHighlights.put(gameObject, objectId);
        }
        if (objectId == CRYSTAL_BOMB_ID)
        {
            crystalBombs.add(gameObject);
        }
    }

    @Subscribe
    public void onGameObjectDespawned (GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        int objectId = gameObject.getId();
        if (objectId == VESPULA_OBJECTID)
        {
            VESPULA_OBJECT = null;
            vespulaStarted = false;
        }
        if (CHEST_IDS.contains(objectId))
        {
            objectHighlights.remove(gameObject, objectId);
        }
        if (objectId == PASSAGE_ID)
        {
            objectHighlights.remove(gameObject, objectId);
        }
        if (objectId == CRABS_END_CRYSTALID)
        {
            crabTileHighlights.clear();
            crabTileWorlds.clear();
            crabsRoomComplete = true;
        }
        if (objectId == ORB_SHOOTERID)
        {
            ORB_SHOOTER_OBJECT = null;
        }
        if (objectId == Olm.HEAD_GAMEOBJECT_READY)
        {
            olm.head = null;
        }
        if (objectId == BOULDER_ID || objectId == MINING_ID || objectId == MINING_DONE_ID || objectId == TREE_SHORTCUT_ID || objectId == TREE_SHORTCUT_DONE_ID)
        {
            objectHighlights.remove(gameObject, objectId);
        }
        if (objectId == CRYSTAL_BOMB_ID)
        {
            crystalBombs.remove(gameObject);
        }
    }

    public void checkVespulaRoom()
    {
        int vespulaX = VESPULA_OBJECT.getWorldLocation().getX();
        int vespulaY = VESPULA_OBJECT.getWorldLocation().getY();
        double centreX = vespulaX + 0.5;
        double centreY = vespulaY + 0.5;
        int orientation = VESPULA_OBJECT.getOrientation();
        int currentPlane = client.getPlane();
        WorldPoint rotatedWorldPoint;
        LocalPoint rotatedLocalPoint;
        double[] rotatedArray;
        //L room
        if (skullObjects.size() == 4)
        {
            //System.out.println("L room found");
            int tileX = vespulaX -5;
            int tileY = vespulaY -10;
            switch (orientation) {
                case 0:
                case 2047:
                    rotatedWorldPoint = new WorldPoint(tileX, tileY, currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 512:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 270);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1024:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 180);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1536:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 90);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
            }
        }
        //I room
        if (skullObjects.size() == 2)
        {
            //System.out.println("I room found");
            int tileX = vespulaX + 5;
            int tileY = vespulaY -10;
            switch (orientation) {
                case 0:
                case 2047:
                    rotatedWorldPoint = new WorldPoint(tileX, tileY, currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 512:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 270);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1024:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 180);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1536:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 90);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
            }
        }
        //T room
        if (skullObjects.size() == 3)
        {
            //System.out.println("T room found");
            int tileX = vespulaX + 5;
            int tileY = vespulaY - 10;
            switch (orientation) {
                case 0:
                case 2047:
                    rotatedWorldPoint = new WorldPoint(tileX, tileY, currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 512:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 270);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1024:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 180);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
                case 1536:
                    rotatedArray = rotatePoint(tileX, tileY, centreX, centreY, 90);
                    rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                    rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                    vespTileHighlights.add(rotatedLocalPoint);
                    break;
            }
        }
    }

    public void checkCrabsRoom()
    {
        WorldPoint orbPoint = ORB_SHOOTER_OBJECT.getWorldLocation();
        WorldPoint blackPoint = BLACK_CRYSTAL_OBJECT.getWorldLocation();
        int dx = Math.abs(orbPoint.getX() - blackPoint.getX());
        int dy = Math.abs(orbPoint.getY() - blackPoint.getY());
        //fast room
        if ((dx == 6 && dy == 3) || (dx == 3 && dy == 6))
        {
            int orientation = ORB_SHOOTER_OBJECT.getOrientation();
            WorldPoint tile1 = orbPoint.dx(-5).dy(1);
            WorldPoint tile2 = orbPoint.dx(-8);
            WorldPoint tile3 = orbPoint.dx(-12);
            WorldPoint tile4 = orbPoint.dx(-11).dy(2);
            WorldPoint tile5 = orbPoint.dx(-9).dy(1);
            WorldPoint tile6 = orbPoint.dx(-8).dy(10);
            Collections.addAll(crabTileWorlds, tile1, tile2, tile3, tile4, tile5, tile6);
            WorldPoint rotatedWorldPoint;
            LocalPoint rotatedLocalPoint;
            double[] rotatedArray;
            int currentPlane = client.getPlane();
            switch (orientation) {
                case 0:
                case 2047:
                    for (WorldPoint worldPoint : crabTileWorlds)
                    {
                        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                        crabTileHighlights.add(localPoint);
                    }
                    break;
                case 512:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 270);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
                case 1024:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 180);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
                case 1536:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 90);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
            }
        }
        //slow room
        if ((dx == 7 && dy == 3) || (dx == 3 && dy == 7))
        {
            int orientation = ORB_SHOOTER_OBJECT.getOrientation();
            WorldPoint tile1 = orbPoint.dx(-13).dy(0);
            WorldPoint tile2 = orbPoint.dx(-12).dy(2);
            WorldPoint tile3 = orbPoint.dx(-10).dy(1);
            WorldPoint tile4 = orbPoint.dx(-6).dy(1);
            WorldPoint tile5 = orbPoint.dx(-2).dy(1);
            Collections.addAll(crabTileWorlds, tile1, tile2, tile3, tile4, tile5);
            WorldPoint rotatedWorldPoint;
            LocalPoint rotatedLocalPoint;
            double[] rotatedArray;
            int currentPlane = client.getPlane();
            switch (orientation) {
                case 0:
                case 2047:
                    for (WorldPoint worldPoint : crabTileWorlds)
                    {
                        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
                        crabTileHighlights.add(localPoint);
                    }
                    break;
                case 512:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 270);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
                case 1024:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 180);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
                case 1536:
                    for (WorldPoint worldPoint : crabTileWorlds) {
                        rotatedArray = rotatePoint(worldPoint.getX(), worldPoint.getY(), orbPoint.getX(), orbPoint.getY(), 90);
                        rotatedWorldPoint = new WorldPoint((int) rotatedArray[0], (int) rotatedArray[1], currentPlane);
                        rotatedLocalPoint = LocalPoint.fromWorld(client, rotatedWorldPoint);
                        crabTileHighlights.add(rotatedLocalPoint);
                    }
                    break;
            }
        }
    }
    boolean isCoxLoaded()
    {
        return ArrayUtils.contains(client.getMapRegions(), COX_REGION_ID);
    }

    public boolean inRaid()
    {
        return client.getVarbitValue(Varbits.IN_RAID) == 1;
    }

    public boolean inRaidUsingMapCheck()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (coxRegionIDs.contains(mapRegion))
            {
                return true;
            }
        }
        return false;
    }

    public int getMaxEnhanceCycles()
    {
        return (int) Math.floor((float)(client.getRealSkillLevel(Skill.PRAYER) / 2) + 31);
    }

    public int getEnhanceRegenRate()
    {
        return (int) Math.floor((float) 500 / getMaxEnhanceCycles());
    }

    public void decrementEnhCounter() {
        if (enhRegenTimer == 0) {
            enhRegenTimer = (enhRegenInterval - 1);
        }
        else {
            enhRegenTimer--;
        }
    }
    public void decrementTreeCounter() {
        boolean isInteracting = client.getLocalPlayer().getInteracting() == MEAT_TREE_NPC;
        if (isInteracting && adjacentTree() && (treeCounter == -1 || treeCounter == 0)) {
            treeCounter = 4;
        } else {
            treeCounter = Math.max(treeCounter - 1, -1);
        }
    }

    boolean adjacentTree()
    {
        WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
        return treeTiles.contains(playerPoint);
    }

    public static double[] rotatePoint(double x, double y, double centerX, double centerY, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);

        double translatedX = x - centerX;
        double translatedY = y - centerY;

        double rotatedX = translatedX * Math.cos(angleInRadians) - translatedY * Math.sin(angleInRadians);
        double rotatedY = translatedX * Math.sin(angleInRadians) + translatedY * Math.cos(angleInRadians);

        double[] result = new double[2];
        result[0] = rotatedX + centerX;
        result[1] = rotatedY + centerY;

        return result;
    }
}