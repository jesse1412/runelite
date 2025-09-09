package net.runelite.client.plugins.mafhamtob.Nylocas;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.plugins.mafhamtob.Util.WeaponMap;
import net.runelite.client.plugins.mafhamtob.Util.WeaponStyle;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

public class Nylocas {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamToBConfig config;
    @Inject
    private NylocasOverlay overlay;

    private boolean skipTickCheck = false;
    @Getter
    private int nyloWave = 0;
    private HashMap<NyloNPC, NPC> currentWave = new HashMap<>();
    @Getter
    private boolean nyloActive;
    private boolean nextInstance = true;
    private int varbit6447 = -1;
    @Getter
    private final Map<LocalPoint, Integer> splitsMap = new HashMap();
    @Getter
    private HashMap<NPC, Integer> nylocasPillars = new HashMap<>();
    private final Set<NPC> bigNylos = new HashSet();
    @Getter
    private Instant nyloWaveStart;
    @Getter
    private HashMap<NPC, Integer> nylocasNpcs = new HashMap<>();
    private Set<GameObject> entranceObjects = new HashSet<>();
    @Getter
    private WorldArea nyloArea;
    @Getter
    private Set<WorldPoint> pillarPoints = new HashSet<>();

    @Getter
    private HashSet<NPC> aggressiveNylocas = new HashSet<>();
    private WeaponStyle weaponStyle;

    private static final String MAGE_NYLO = "Nylocas Hagios";
    private static final String RANGE_NYLO = "Nylocas Toxobolos";
    private static final String MELEE_NYLO = "Nylocas Ischyros";
    private static final int MELEE_PRINCE_1 = 10803;
    private static final int MELEE_PRINCE_2 = 10804;
    private static final int MAGE_PRINCE = 10805;
    private static final int RANGE_PRINCE = 10806;
    private static final int NPCID_NYLOCAS_PILLAR = 8358;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        splitsMap.clear();
        bigNylos.clear();
        nyloWaveStart = null;
        aggressiveNylocas.clear();
        setNyloWave(0);
        currentWave.clear();
        nylocasNpcs.clear();
        nylocasPillars.clear();
        entranceObjects.clear();
        nyloArea = null;
        pillarPoints.clear();
        weaponStyle = null;
    }

    private void setNyloWave(int wave)
    {
        nyloWave = wave;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        boolean inRoomRegion = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 13122);
        if (!inRoomRegion)
        {
            return;
        }
        if (gameObjectSpawned.getGameObject().getId() == 32755) //entrance doors
        {
            ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
            int weaponSlotId = EquipmentInventorySlot.WEAPON.getSlotIdx();
            int weaponItemId = equipment.getItems()[weaponSlotId].getId();
            if (WeaponMap.StyleMap.containsKey(weaponItemId))
            {
                weaponStyle = WeaponMap.StyleMap.get(weaponItemId);
            }
            entranceObjects.add(gameObjectSpawned.getGameObject());
            if (entranceObjects.size() == 2)
            {
                GameObject lowestXGameObject = null;
                int lowestX = Integer.MAX_VALUE;
                for (GameObject gameObject : entranceObjects)
                {
                    WorldPoint worldLocation = gameObject.getWorldLocation();

                    int x = worldLocation.getX();

                    if (x < lowestX) {
                        lowestX = x;
                        lowestXGameObject = gameObject;
                    }
                }
                if (lowestXGameObject != null)
                {
                    nyloArea = new WorldArea(lowestXGameObject.getWorldLocation().dx(-5).dy(-12), 12, 12);
                }
            }
        }
        if (gameObjectSpawned.getGameObject().getId() == 32862) //pillars
        {
            WorldPoint worldPoint = gameObjectSpawned.getGameObject().getWorldLocation();
            for (int x = -1; x < 2; x++)
            {
                for (int y = -1; y < 2; y++)
                {
                    pillarPoints.add(worldPoint.dx(x).dy(y));
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
            case NPCID_NYLOCAS_PILLAR:
            case 10790:
            case 10811:
                nyloActive = true;
                if (nylocasPillars.size() > 3)
                {
                    nylocasPillars.clear();
                }
                if (!nylocasPillars.containsKey(npc))
                {
                    nylocasPillars.put(npc, 100);
                }
                break;
            case 8342:
            case 8343:
            case 8344:
            case 8345:
            case 8346:
            case 8347:
            case 8348:
            case 8349:
            case 8350:
            case 8351:
            case 8352:
            case 8353:
            case 10774:
            case 10775:
            case 10776:
            case 10777:
            case 10778:
            case 10779:
            case 10780:
            case 10781:
            case 10782:
            case 10783:
            case 10784:
            case 10785:
            case 10791:
            case 10792:
            case 10793:
            case 10794:
            case 10795:
            case 10796:
            case 10797:
            case 10798:
            case 10799:
            case 10800:
            case 10801:
            case 10802:
                if (nyloActive)
                {
                    nylocasNpcs.put(npc, 52);

                    NyloNPC nyloNPC = matchNpc(npc);
                    if (nyloNPC != null)
                    {
                        currentWave.put(nyloNPC, npc);
                        if (currentWave.size() > 2)
                        {
                            matchWave();
                        }
                    }
                }
                break;
            case NpcID.NYLOCAS_VASILIAS:
            case NpcID.NYLOCAS_VASILIAS_8355:
            case NpcID.NYLOCAS_VASILIAS_8356:
            case NpcID.NYLOCAS_VASILIAS_8357:
            case 10786:
            case 10787:
            case 10788:
            case 10789:
            case 10807:
            case 10808:
            case 10809:
            case 10810:
                break;
        }

        int id = npc.getId();
        switch (id)
        {
            case 8345:
            case 8346:
            case 8347:
            case 10794:
            case 10795:
            case 10796:
                bigNylos.add(npc);
                break;
        }
    }

    private void matchWave()
    {
        HashSet<NyloNPC> potentialWave;
        Set<NyloNPC> currentWaveKeySet = currentWave.keySet();

        for (int wave = nyloWave + 1; wave <= NylocasWave.MAX_WAVE; wave++)
        {
            boolean matched = true;
            potentialWave = NylocasWave.waves.get(wave).getWaveData();
            for (NyloNPC nyloNpc : potentialWave)
            {
                if (!currentWaveKeySet.contains(nyloNpc))
                {
                    matched = false;
                    break;
                }
            }

            if (matched)
            {
                setNyloWave(wave);
                for (NyloNPC nyloNPC : potentialWave)
                {
                    if (nyloNPC.isAggressive())
                    {
                        aggressiveNylocas.add(currentWave.get(nyloNPC));
                    }
                }
                currentWave.clear();
                return;
            }
        }
    }

    private NyloNPC matchNpc(NPC npc)
    {
        WorldPoint p = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
        Point point = new Point(p.getRegionX(), p.getRegionY());
        NylocasSpawnPoint spawnPoint = NylocasSpawnPoint.getLookupMap().get(point);

        if (spawnPoint == null)
        {
            return null;
        }

        NylocasType nylocasType = NylocasType.getLookupMap().get(npc.getId());

        if (nylocasType == null)
        {
            return null;
        }

        return new NyloNPC(nylocasType, spawnPoint);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        NPC npc = npcDespawned.getNpc();
        switch (npc.getId())
        {
            case NPCID_NYLOCAS_PILLAR:
            case 10790:
            case 10811:
                if (nylocasPillars.containsKey(npc))
                {
                    nylocasPillars.remove(npc);
                }
                if (nylocasPillars.size() < 1)
                {
                    nyloWaveStart = null;
                    nyloActive = false;
                }
                break;
            case 8342:
            case 8343:
            case 8344:
            case 8345:
            case 8346:
            case 8347:
            case 8348:
            case 8349:
            case 8350:
            case 8351:
            case 8352:
            case 8353:
            case 10774:
            case 10775:
            case 10776:
            case 10777:
            case 10778:
            case 10779:
            case 10780:
            case 10781:
            case 10782:
            case 10783:
            case 10784:
            case 10785:
            case 10791:
            case 10792:
            case 10793:
            case 10794:
            case 10795:
            case 10796:
            case 10797:
            case 10798:
            case 10799:
            case 10800:
            case 10801:
            case 10802:
                aggressiveNylocas.remove(npc);
                break;
            case NpcID.NYLOCAS_VASILIAS:
            case NpcID.NYLOCAS_VASILIAS_8355:
            case NpcID.NYLOCAS_VASILIAS_8356:
            case NpcID.NYLOCAS_VASILIAS_8357:
            case 10786:
            case 10787:
            case 10788:
            case 10789:
            case 10807:
            case 10808:
            case 10809:
            case 10810:
                break;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (actor instanceof NPC)
        {
            switch (((NPC) actor).getId())
            {
                case 8355:
                case 8356:
                case 8357:
                case 10787:
                case 10788:
                case 10789:
                case 10808:
                case 10809:
                case 10810:
                    if (event.getActor().getAnimation() == 8004 ||
                            event.getActor().getAnimation() == 7999 ||
                            event.getActor().getAnimation() == 7989)
                    {
                    }
            }
        }
        if (!bigNylos.isEmpty() && event.getActor() instanceof NPC)
        {
            NPC npc = (NPC) event.getActor();
            if (bigNylos.contains(npc))
            {
                int anim = npc.getAnimation();
                if (anim == 8005 || anim == 7991 || anim == 7998)
                {
                    splitsMap.putIfAbsent(npc.getLocalLocation(), 6);
                    bigNylos.remove(npc);
                }
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        int npcId = npcChanged.getNpc().getId();

        switch (npcId)
        {
            case 8355:
            case 8356:
            case 8357:
            case 10787:
            case 10788:
            case 10789:
            case 10808:
            case 10809:
            case 10810:
            {
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        int[] varps = client.getVarps();
        int newVarbit6447 = client.getVarbitValue(varps, 6447);
        boolean inRoomRegion = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 13122);
        if (inRoomRegion && newVarbit6447 != 0 && newVarbit6447 != varbit6447)
        {
            nyloWaveStart = Instant.now();
        }

        varbit6447 = newVarbit6447;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        boolean inRoomRegion = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 13122);
        if (inRoomRegion)
        {
        }
        else
        {
            reset();
        }
        nextInstance = true;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        boolean inRoomRegion = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 13122);
        if (inRoomRegion && nyloActive)
        {
            if (skipTickCheck)
            {
                skipTickCheck = false;
            }
            else
            {
                if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null)
                {
                    return;
                }
            }

            for (Iterator<NPC> it = nylocasNpcs.keySet().iterator(); it.hasNext(); )
            {
                NPC npc = it.next();
                int ticksLeft = nylocasNpcs.get(npc);

                if (ticksLeft < 0)
                {
                    it.remove();
                    continue;
                }
                nylocasNpcs.replace(npc, ticksLeft - 1);
            }

            for (NPC pillar : nylocasPillars.keySet())
            {
                int healthPercent = pillar.getHealthRatio();
                if (healthPercent > -1)
                {
                    nylocasPillars.replace(pillar, healthPercent);
                }
            }

            if (!splitsMap.isEmpty())
            {
                splitsMap.values().removeIf((value) -> value <= 1);
                splitsMap.replaceAll((key, value) -> value - 1);
            }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
    {
        String s = menuOptionClicked.getMenuOption();
        if (Objects.equals(s, "Wield") || Objects.equals(s, "Equip") || Objects.equals(s, "Wear"))
        {
            if (WeaponMap.StyleMap.containsKey(menuOptionClicked.getItemId()))
            {
                weaponStyle = WeaponMap.StyleMap.get(menuOptionClicked.getItemId());
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded entry)
    {
        if (!nyloActive)
        {
            return;
        }

        String target = entry.getTarget();
        NPC npc = entry.getMenuEntry().getNpc();
        int id = 0;
        if (npc != null)
        {
            id = entry.getMenuEntry().getNpc().getId();
        }

        if (config.removeNyloEntries() && entry.getMenuEntry().getType() == MenuAction.NPC_SECOND_OPTION && weaponStyle != null)
        {
            switch (weaponStyle)
            {
                case MAGIC:
                    if (target.contains(MELEE_NYLO) || target.contains(RANGE_NYLO) || id == MELEE_PRINCE_1 || id == MELEE_PRINCE_2 || id == RANGE_PRINCE)
                    {
                        entry.getMenuEntry().setDeprioritized(true);
                    }
                    break;
                case MELEE:
                    if (target.contains(RANGE_NYLO) || target.contains(MAGE_NYLO) || id == RANGE_PRINCE || id == MAGE_PRINCE)
                    {
                        entry.getMenuEntry().setDeprioritized(true);
                    }
                    break;
                case RANGE:
                    if (target.contains(MELEE_NYLO) || target.contains(MAGE_NYLO) || id == MELEE_PRINCE_1 || id == MELEE_PRINCE_2 || id == MAGE_PRINCE)
                    {
                        entry.getMenuEntry().setDeprioritized(true);
                    }
                    break;
            }
        }
    }
}