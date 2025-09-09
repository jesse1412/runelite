package net.runelite.client.plugins.dukesucellus;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Duke Sucellus",
        description = "Show helpful information for the Duke Sucellus fight",
        tags = {"duke", "sucellus"}
)
public class DukeSucellusPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private DukeSucellusSceneOverlay sceneOverlay;
    @Inject
    private DukeSucellusItemOverlay itemOverlay;
    @Inject
    private AttackHandler attackHandler;
    @Inject
    private SpecialAttack specialAttack;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private DukeSucellusConfig config;
    @Provides
    DukeSucellusConfig getConfig(ConfigManager configManager){return configManager.getConfig(DukeSucellusConfig.class);}

    // Duke NPC IDs
    private final int DUKE_SPAWNED = 12166;
    private final int DUKE_IDLE = 12167;
    private final int DUKE_FIGHTING = 12191;
    private final int DUKE_DEAD = 12192;

    // Highlight IDs
        // NPC IDs
    private final int POISON_VENT = 12198;
    private final List<Integer> EYE_LASERS = Arrays.asList(12199, 12200, 12201);
        // Projectile IDs
    private final int DUKE_POISON_ATTACK = 2436;
        // GFX IDs
    private final int DUKE_STUN_SHADOW = 1447;
    private final List<Integer> DUKE_STOMP_ATTACK = Arrays.asList(2440, 2441, 2442, 2443);
        // Animation IDs
    private final int DUKE_EYE_ATTACK = 10180;
        // Scene Overlays
    @Getter
    private List<TileHighlight> Highlights = new ArrayList<>();
    @Getter
    private List<LocalPoint> safeLocations = new ArrayList<>();

    // Fight Tracking
        // Locations
    private final List<LocalPoint> ventRotation = Arrays.asList(new LocalPoint(6720, 8512),
            new LocalPoint(7104, 8512),
            new LocalPoint(7488, 8512));
    @Getter
    private final LocalPoint leftDuke = new LocalPoint(6720, 8512);
    @Getter
    private final LocalPoint rightDuke = new LocalPoint(7488, 8512);
    @Getter
    private final LocalPoint leftMining = new LocalPoint(6720, 8000);
    @Getter
    private final LocalPoint rightMining = new LocalPoint(7488, 8000);
    @Getter
    private LocalPoint dukeLocation;
        // Fight States
    @Getter
    private boolean dukeRoom;
    @Getter
    private boolean dukeFight;
    @Getter
    private boolean dukePrep;
    @Getter
    private int ventStage;
    // Eye Attack Variables
    @Getter
    private int nextEyeAttackTick = -1;
    @Getter
    private final int eyeAttackTicks = 4;
    private Set<LocalPoint> runTiles = Set.of(new LocalPoint(8256, 7104, -1),
            new LocalPoint(8256, 7360, -1),
            new LocalPoint(8256, 7616, -1),
            new LocalPoint(8256, 7872, -1),
            new LocalPoint(8256, 8128, -1),
            new LocalPoint(8256, 8384, -1),
            new LocalPoint(8256, 8640, -1),
            new LocalPoint(5952, 7616, -1),
            new LocalPoint(5952, 7872, -1),
            new LocalPoint(5952, 8128, -1),
            new LocalPoint(5952, 8384, -1),
            new LocalPoint(5952, 8640, -1),
            new LocalPoint(5952, 7104, -1),
            new LocalPoint(5952, 7360, -1),
            new LocalPoint(7872, 6720, -1),
            new LocalPoint(6080, 6848, -1),
            new LocalPoint(8000, 6848, -1),
            new LocalPoint(6208, 6848, -1),
            new LocalPoint(6336, 6720, -1),
            new LocalPoint(8128, 6848, -1)
    );

    @Getter
    private Integer respawnTimer;
    private Integer arderMushroomAmount;

    @Override
    protected void startUp() {
        overlayManager.add(sceneOverlay);
        overlayManager.add(itemOverlay);
        attackHandler.startUp();
    }
    @Override
    protected void shutDown() {
        overlayManager.remove(sceneOverlay);
        overlayManager.remove(itemOverlay);
        attackHandler.shutDown();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        attackHandler.onNpcSpawned(npcSpawned);
        final int npcId = npcSpawned.getNpc().getId();

        if (npcId == DUKE_SPAWNED) {
            dukeLocation = npcSpawned.getNpc().getLocalLocation();
            dukeRoom = true;
            dukeFight = false;
            dukePrep = false;
            return;
        } else if (npcSpawned.getNpc().getComposition() == null || npcId != POISON_VENT && !EYE_LASERS.contains(npcId)) {
            return;
        }

        String identifier = npcId == POISON_VENT ? "POISON_VENT" : "EYE_LASER";
        int spawnTick = client.getTickCount();
        int hurtTick = spawnTick + (npcId == POISON_VENT ? 5 : 3);
        int despawnTick = hurtTick + (npcId == POISON_VENT ? 5 : 1);

        LocalPoint location = npcSpawned.getNpc().getLocalLocation();
        int size = npcSpawned.getNpc().getComposition().getSize();

        TileHighlight tileHighlight = new TileHighlight(identifier, spawnTick, hurtTick, despawnTick, location, size);
        Highlights.add(tileHighlight);

        if (npcId == POISON_VENT && !isDukeFight()) {
            LocalPoint localPoint = npcSpawned.getNpc().getLocalLocation();
            if (localPoint.equals(ventRotation.get(0)) && ventStage != 1) {
                ventStage = 1;

                safeLocations.clear();
                safeLocations.add(rightDuke);
                safeLocations.add(leftMining);
            }
            if (localPoint.equals(ventRotation.get(1))) {
                ventStage = 2;

                safeLocations.clear();
                safeLocations.add(leftDuke);
                safeLocations.add(leftMining);
            }
            if (localPoint.equals(ventRotation.get(2))) {
                ventStage = 4;

                safeLocations.clear();
                safeLocations.add(leftDuke);
                safeLocations.add(rightMining);
            }
        }
    }
    @Subscribe
    public void onNpcChanged(NpcChanged newNpc) {
        if (!dukeRoom) {
            return;
        }
        attackHandler.onNpcChanged(newNpc);
        safeLocations.clear();

        final int newId = newNpc.getNpc().getId();
        switch (newId) {
            case DUKE_FIGHTING:
                dukeFight = true;
                dukePrep = false;
                break;
            case DUKE_IDLE:
                dukeFight = false;
                dukePrep = true;
                break;
            case DUKE_DEAD:
                dukeFight = false;
                dukePrep = false;
                break;
            default:
                System.out.println("Unfamiliar ID Detected: " + newId);
                break;
        }
    }
    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        attackHandler.onNpcDespawned(npcDespawned);
        if (!dukeRoom) {
            return;
        }

        final int npcId = npcDespawned.getNpc().getId();
        if (npcId == DUKE_SPAWNED || npcId == DUKE_IDLE || npcId == DUKE_FIGHTING || npcId == DUKE_DEAD ) {
            dukeRoom = false;
            dukeFight = false;
            safeLocations.clear();
            Highlights.clear();
            arderMushroomAmount = null;
        }

        if (npcId == POISON_VENT && !isDukeFight()) {
            LocalPoint localPoint = npcDespawned.getNpc().getLocalLocation();
            if (localPoint.equals(ventRotation.get(1))) {
                ventStage = 3;

                safeLocations.clear();
                safeLocations.add(leftDuke);
                safeLocations.add(rightMining);
            }
            if (localPoint.equals(ventRotation.get(2))) {
                ventStage = 1;

                safeLocations.clear();
                safeLocations.add(rightDuke);
                safeLocations.add(leftMining);
            }
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved) {
        if (!dukeRoom) {
            return;
        }

        Projectile projectile = projectileMoved.getProjectile();
        int projectileId = projectile.getId();

        if (projectileId != DUKE_POISON_ATTACK) {
            return;
        }

        String identifier = "DUKE_POISON_ATTACK";
        int spawnTick = client.getTickCount();
        int hurtTick = spawnTick + 3;
        int despawnTick = hurtTick + 1;

        LocalPoint location = projectileMoved.getPosition();
        int size = 3;

        TileHighlight tileHighlight = new TileHighlight(identifier, spawnTick, -1, despawnTick, location, size);
        Highlights.add(tileHighlight);
    }
    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated) {
        if (!dukeRoom) {
            return;
        }
        clientThread.invokeAtTickEnd(() -> handleGraphicsObjects(graphicsObjectCreated));
    }

    public void handleGraphicsObjects(GraphicsObjectCreated graphicsObjectCreated)
    {
        boolean farAway = false;
        boolean notOnRunTiles = false;
        GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
        int graphicsObjectId = graphicsObject.getId();

        if (graphicsObjectId != DUKE_STUN_SHADOW && !DUKE_STOMP_ATTACK.contains(graphicsObjectId)) {
            return;
        }
        String identifier = graphicsObjectId == DUKE_STUN_SHADOW ? "DUKE_STUN_SHADOW" : "DUKE_STOMP";
        int spawnTick = client.getTickCount();
        int hurtTick = spawnTick + (graphicsObjectId == DUKE_STUN_SHADOW ? 4 : 2);
        int despawnTick = hurtTick + 1;

        LocalPoint location = graphicsObject.getLocation();
        int size = 1;

        WorldPoint worldPoint = WorldPoint.fromLocal(client, location);
        int ticksUntilHurt = hurtTick - client.getTickCount();
        int distance = worldPoint.distanceTo(client.getLocalPlayer().getWorldLocation());
        int tickDistance = (int)Math.ceil((double)distance / 2);
        if ((tickDistance + 1) > ticksUntilHurt && Objects.equals(identifier, "DUKE_STUN_SHADOW"))
        {
            farAway = true;
        }
        if (Objects.equals(identifier, "DUKE_STUN_SHADOW") && !runTiles.contains(location))
        {
            notOnRunTiles = true;
        }
        TileHighlight tileHighlight = new TileHighlight(identifier, spawnTick, hurtTick, despawnTick, location, size);
        if (!farAway && !notOnRunTiles)
        {
            Highlights.add(tileHighlight);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        if (!dukeRoom) {
            return;
        }
        attackHandler.onAnimationChanged(animationChanged);

        String actor = animationChanged.getActor().getName();
        if (actor == null || !actor.equalsIgnoreCase("Duke Sucellus")){
            return;
        }

        int animationId = animationChanged.getActor().getAnimation();
        if (animationId == DUKE_EYE_ATTACK) {
            nextEyeAttackTick = client.getTickCount() + eyeAttackTicks;
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (!dukeRoom) {
            return;
        }
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
            specialAttack.setTicksSinceSpecRegen(0);
        }
    }
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (!dukeRoom) {
            return;
        }
        if (itemContainerChanged.getContainerId() != InventoryID.EQUIPMENT.getId()) {
            return;
        }

        ItemContainer equipment = itemContainerChanged.getItemContainer();
        final boolean hasLightbearer = equipment.contains(ItemID.LIGHTBEARER);
        if (hasLightbearer == specialAttack.isWearingLightbearer()) {
            return;
        }

        specialAttack.setTicksSinceSpecRegen(0);
        specialAttack.setWearingLightbearer(hasLightbearer);
    }
    @Subscribe
    public void onGameTick(GameTick event) {
        if (!dukeRoom) {
            return;
        }

        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null) {
            for (Item item : inventory.getItems()) {
                if (item.getId() == ItemID.ARDER_POWDER)
                {
                    if (arderMushroomAmount != null)
                    {
                        if (item.getQuantity() < arderMushroomAmount)
                        {
                            if (config.enableMetronome())
                            {
                                Preferences preferences = client.getPreferences();
                                int previousVolume = preferences.getSoundEffectVolume();
                                preferences.setSoundEffectVolume(config.tickVolume());
                                client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.tickVolume());
                                client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP, config.tickVolume());
                                preferences.setSoundEffectVolume(previousVolume);
                            }

                        }
                        if (item.getQuantity() == 3 && arderMushroomAmount == 4)
                        {
                            Preferences preferences = client.getPreferences();
                            int previousVolume = preferences.getSoundEffectVolume();
                            preferences.setSoundEffectVolume(config.dingVolume());
                            client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DING, config.dingVolume());
                            client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DING, config.dingVolume());
                            preferences.setSoundEffectVolume(previousVolume);
                        }
                    }
                    arderMushroomAmount = item.getQuantity();
                }
            }
        }

        if (respawnTimer != null)
        {
            respawnTimer--;
            if (respawnTimer == -1)
            {
                respawnTimer = null;
            }
        }

        attackHandler.onGameTick(event);
        final int ticksPerSpecRegen = specialAttack.isWearingLightbearer() ? specialAttack.getSPEC_REGEN_TICKS() / 2 : specialAttack.getSPEC_REGEN_TICKS();

        if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000)
        {
            // The recharge doesn't tick when at 100%
            specialAttack.setTicksSinceSpecRegen(0);
        }
        else
        {
            specialAttack.setTicksSinceSpecRegen((specialAttack.getTicksSinceSpecRegen() + 1) % ticksPerSpecRegen);
        }
        specialAttack.setSpecialPercentage(specialAttack.getTicksSinceSpecRegen() / (double) ticksPerSpecRegen);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (!dukeRoom) {
            return;
        }
        MessageNode messageNode = chatMessage.getMessageNode();
        if (messageNode.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }
        String message = messageNode.getValue();
        if (message.contains("Your Duke Sucellus kill count is"))
        {
            respawnTimer = 19;
        }

    }

    public boolean shouldEquipLightbearer() {
        return specialAttack.getSpecialPercentage() <= 0.5;
    }
    public double getPieProgress(int currentTick, int finalTick, int divisor) {
        return (double) (finalTick - currentTick) / divisor;
    }
}