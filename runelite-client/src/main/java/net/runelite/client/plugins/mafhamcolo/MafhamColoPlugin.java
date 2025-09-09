package net.runelite.client.plugins.mafhamcolo;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Colo",
        description = "Mafham Colo",
        tags = {"Mafham", "Colo"}
)
public class MafhamColoPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private MafhamColoConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamColoOverlay overlay;
    @Inject
    private MafhamColoEquipmentOverlay equipmentOverlay;
    @Inject
    private MafhamColoPieOverlay pieOverlay;
    @Provides
    MafhamColoConfig getConfig(ConfigManager configManager){return configManager.getConfig(MafhamColoConfig.class);}


    @Getter
    private EquipmentSlot equipmentSlot;
    @Getter
    private HashMap<WorldPoint, Integer> highlightTiles = new HashMap<>();
    @Getter
    private Set<GameObject> pillars = new HashSet<>();
    @Getter
    private Set<Javelin> javelins = new HashSet<>();
    @Getter
    private Set<Fremennik> fremenniks = new HashSet<>();
    private final int spearID = 10883;
    private final int shieldID = 10885;
    private final int colRegionID = 7216;
    private final int pillarID = 52490;
    private final int javelinID = 12817;
    private final int javelinAttackID = 10892;
    private final int javelinThrowID = 10893;
    private final int seerID = 12815;
    private final int berserkerID = 12816;
    private final int archerID = 12814;

    private final Set<Integer> outsideRegionIDs = Set.of(7059, 7060, 7061, 7315, 7316, 7317);
    private boolean stompAnimHasPlayed = false;
    private int previousAttack = -1;
    private int lastAnimChangedTime = 0;
    @Getter
    private Integer meleeAttack1Tick;
    @Getter
    private Integer meleeAttack2Tick;
    @Getter
    private Integer waveStartTime; //when the wave actually starts
    @Getter
    private Integer waveContinueTime; //when you press continue, for tile run timing

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        overlayManager.add(equipmentOverlay);
        overlayManager.add(pieOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        overlayManager.remove(equipmentOverlay);
        overlayManager.remove(pieOverlay);
        reset();
    }

    private void reset()
    {
        highlightTiles.clear();
        previousAttack = -1;
        stompAnimHasPlayed = false;
        lastAnimChangedTime = 0;
        equipmentSlot = null;
        waveStartTime = null;
        waveContinueTime = null;
        pillars.clear();
        javelins.clear();
        fremenniks.clear();
        meleeAttack1Tick = null;
        meleeAttack2Tick = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        int id = npcSpawned.getNpc().getId();
        if (id == javelinID)
        {
            javelins.add(new Javelin(npcSpawned.getNpc(), 4));
        }
        if (id == seerID || id == berserkerID || id == archerID)
        {
            fremenniks.add(new Fremennik(npcSpawned.getNpc(), client.getTickCount(), 0));
        }
    }
    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        javelins.removeIf(javelin -> javelin.npc == npcDespawned.getNpc());
        fremenniks.removeIf(fremennik -> fremennik.npc == npcDespawned.getNpc());
        if (playerIsInColo())
        {
            if (npcDespawned.getNpc().getId() == 12808)
            {
                waveContinueTime = client.getTickCount();
            }
        }
    }

    /*
    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
    {
        if (soundEffectPlayed.getSoundId() == 2677) //range
        {
            NPC solboi = null;
            for (NPC npc : client.getNpcs())
            {
                if (Objects.equals(npc.getName(), "Sol Heredit"))
                {
                    solboi = npc;
                }
            }
            if (solboi != null)
            {
                equipmentSlot = EquipmentSlot.BODY;
            }
        }
        if (soundEffectPlayed.getSoundId() == 2675) //mage
        {
            NPC solboi = null;
            for (NPC npc : client.getNpcs())
            {
                if (Objects.equals(npc.getName(), "Sol Heredit"))
                {
                    solboi = npc;
                }
            }
            if (solboi != null)
            {
                equipmentSlot = EquipmentSlot.FEET;
            }
        }
    }
     */



    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() == pillarID)
        {
            pillars.add(gameObjectSpawned.getGameObject());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }
        if (!playerIsInColo())
        {
            return;
        }
        String message = chatMessage.getMessage();

        if (message.contains("Wave:"))
        {
            waveStartTime = client.getTickCount();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (playerIsOutside())
        {
            //System.out.println("we're outside");
            reset();
        }
        if (!fremenniks.isEmpty())
        {
            for (Fremennik fremennik : fremenniks)
            {
                switch (fremennik.npc.getId())
                {
                    case seerID:
                        int startTick = (fremennik.spawnTick + 3);
                        fremennik.ticksUntilAttack = (Math.floorMod(startTick - client.getTickCount(), 6));
                        break;
                    case berserkerID:
                        int startTick2 = (fremennik.spawnTick + 2);
                        fremennik.ticksUntilAttack = (Math.floorMod(startTick2 - client.getTickCount(), 6));
                        break;
                    case archerID:
                        int startTick3 = (fremennik.spawnTick + 4);
                        fremennik.ticksUntilAttack = (Math.floorMod(startTick3 - client.getTickCount(), 6));
                        break;
                }
            }
        }
        if (!highlightTiles.isEmpty())
        {
            Iterator<Map.Entry<WorldPoint, Integer>> iterator = highlightTiles.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<WorldPoint, Integer> entry = iterator.next();
                if (client.getTickCount() > entry.getValue() + 1)
                {
                    iterator.remove();
                }
            }
        }
        for (NPC npc : client.getNpcs())
        {
            if (Objects.equals(npc.getName(), "Sol Heredit"))
            {
                if (client.getTickCount() > lastAnimChangedTime + 3 && stompAnimHasPlayed)
                {
                    npc.setAnimation(-1);
                    stompAnimHasPlayed = false;
                }
                if (npc.getOverheadText() != null)
                {
                    //System.out.println("Overhead text: " + npc.getOverheadText() + " Game tick: " + client.getTickCount());
                    if (npc.getOverheadText().contains("BODY"))
                    {
                        equipmentSlot = EquipmentSlot.BODY;
                        previousAttack = -1;
                    }
                    if (npc.getOverheadText().contains("BACK"))
                    {
                        equipmentSlot = EquipmentSlot.BACK;
                        previousAttack = -1;
                    }
                    if (npc.getOverheadText().contains("HANDS"))
                    {
                        equipmentSlot = EquipmentSlot.HANDS;
                        previousAttack = -1;
                    }
                    if (npc.getOverheadText().contains("LEGS"))
                    {
                        equipmentSlot = EquipmentSlot.LEGS;
                        previousAttack = -1;
                    }
                    if (npc.getOverheadText().contains("FEET"))
                    {
                        equipmentSlot = EquipmentSlot.FEET;
                        previousAttack = -1;
                    }
                }
                if (npc.getOverheadText() == null)
                {
                    equipmentSlot = null;
                }
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        String id = String.valueOf(event.getActor().getAnimation());
        int animationID = event.getActor().getAnimation();
        for (Javelin javelin : javelins)
        {
            if (javelin.npc == event.getActor())
            {
                if (animationID == javelinAttackID)
                {
                    javelin.attackCount--;
                }
                if (animationID == javelinThrowID)
                {
                    javelin.attackCount = 4;
                }
            }
        }
        if (Objects.equals(event.getActor().getName(), "Sol Heredit"))
        {
            System.out.println("Animtion ID: " + id + " Game tick: " + client.getTickCount());
            if (animationID == shieldID)
            {
                stompAnimHasPlayed = true;
                lastAnimChangedTime = client.getTickCount();
                HighlightShield(event.getActor());
            }
            if (animationID == spearID)
            {
                stompAnimHasPlayed = true;
                lastAnimChangedTime = client.getTickCount();
                HighlightSpear(event.getActor());

            }
            if (animationID == 10887) // >50 melee prayer attack
            {
                previousAttack = -1;
                meleeAttack1Tick = client.getTickCount();
                System.out.println("we set the melee attack tick to " + client.getTickCount());
            }
            if (animationID == 10886) // <50 melee prayer attack
            {
                previousAttack = -1;
                meleeAttack2Tick = client.getTickCount();
                System.out.println("we set the melee 2 attack tick to " + client.getTickCount());
            }
            if (animationID == -1) //if not doing melee attacks
            {
                meleeAttack1Tick = null;
                meleeAttack2Tick = null;
                System.out.println("Sol wasn't doing melee attack so we reset the ticks on " + client.getTickCount());
            }
        }
    }

    public void HighlightShield(Actor npc)
    {
        int tick = client.getTickCount();
        WorldPoint wp = npc.getWorldLocation();
        if (previousAttack != shieldID)
        {
            highlightTiles.put(wp.dx(0).dy(-2), tick);
            highlightTiles.put(wp.dx(1).dy(-2), tick);
            highlightTiles.put(wp.dx(2).dy(-2), tick);
            highlightTiles.put(wp.dx(3).dy(-2), tick);
            highlightTiles.put(wp.dx(4).dy(-2), tick);
            highlightTiles.put(wp.dx(5).dy(-2), tick);
            highlightTiles.put(wp.dx(6).dy(-2), tick);
            highlightTiles.put(wp.dx(6).dy(-1), tick);
            highlightTiles.put(wp.dx(6).dy(0), tick);
            highlightTiles.put(wp.dx(6).dy(1), tick);
            highlightTiles.put(wp.dx(6).dy(2), tick);
            highlightTiles.put(wp.dx(6).dy(3), tick);
            highlightTiles.put(wp.dx(6).dy(4), tick);
            highlightTiles.put(wp.dx(6).dy(5), tick);
            highlightTiles.put(wp.dx(6).dy(6), tick);
            highlightTiles.put(wp.dx(5).dy(6), tick);
            highlightTiles.put(wp.dx(4).dy(6), tick);
            highlightTiles.put(wp.dx(3).dy(6), tick);
            highlightTiles.put(wp.dx(2).dy(6), tick);
            highlightTiles.put(wp.dx(1).dy(6), tick);
            highlightTiles.put(wp.dx(0).dy(6), tick);
            highlightTiles.put(wp.dx(-1).dy(6), tick);
            highlightTiles.put(wp.dx(-2).dy(6), tick);
            highlightTiles.put(wp.dx(-2).dy(5), tick);
            highlightTiles.put(wp.dx(-2).dy(4), tick);
            highlightTiles.put(wp.dx(-2).dy(3), tick);
            highlightTiles.put(wp.dx(-2).dy(2), tick);
            highlightTiles.put(wp.dx(-2).dy(1), tick);
            highlightTiles.put(wp.dx(-2).dy(0), tick);
            highlightTiles.put(wp.dx(-2).dy(-1), tick);
            highlightTiles.put(wp.dx(-2).dy(-2), tick);
            highlightTiles.put(wp.dx(-1).dy(-2), tick);
            previousAttack = shieldID;
        }
        else
        {
            highlightTiles.put(wp.dx(0).dy(-3), tick);
            highlightTiles.put(wp.dx(1).dy(-3), tick);
            highlightTiles.put(wp.dx(2).dy(-3), tick);
            highlightTiles.put(wp.dx(3).dy(-3), tick);
            highlightTiles.put(wp.dx(4).dy(-3), tick);
            highlightTiles.put(wp.dx(5).dy(-3), tick);
            highlightTiles.put(wp.dx(6).dy(-3), tick);
            highlightTiles.put(wp.dx(7).dy(-3), tick);
            highlightTiles.put(wp.dx(7).dy(-2), tick);
            highlightTiles.put(wp.dx(7).dy(-1), tick);
            highlightTiles.put(wp.dx(7).dy(0), tick);
            highlightTiles.put(wp.dx(7).dy(1), tick);
            highlightTiles.put(wp.dx(7).dy(2), tick);
            highlightTiles.put(wp.dx(7).dy(3), tick);
            highlightTiles.put(wp.dx(7).dy(4), tick);
            highlightTiles.put(wp.dx(7).dy(5), tick);
            highlightTiles.put(wp.dx(7).dy(6), tick);
            highlightTiles.put(wp.dx(7).dy(7), tick);
            highlightTiles.put(wp.dx(6).dy(7), tick);
            highlightTiles.put(wp.dx(5).dy(7), tick);
            highlightTiles.put(wp.dx(4).dy(7), tick);
            highlightTiles.put(wp.dx(3).dy(7), tick);
            highlightTiles.put(wp.dx(2).dy(7), tick);
            highlightTiles.put(wp.dx(1).dy(7), tick);
            highlightTiles.put(wp.dx(0).dy(7), tick);
            highlightTiles.put(wp.dx(-1).dy(7), tick);
            highlightTiles.put(wp.dx(-2).dy(7), tick);
            highlightTiles.put(wp.dx(-3).dy(7), tick);
            highlightTiles.put(wp.dx(-3).dy(6), tick);
            highlightTiles.put(wp.dx(-3).dy(5), tick);
            highlightTiles.put(wp.dx(-3).dy(4), tick);
            highlightTiles.put(wp.dx(-3).dy(3), tick);
            highlightTiles.put(wp.dx(-3).dy(2), tick);
            highlightTiles.put(wp.dx(-3).dy(1), tick);
            highlightTiles.put(wp.dx(-3).dy(0), tick);
            highlightTiles.put(wp.dx(-3).dy(-1), tick);
            highlightTiles.put(wp.dx(-3).dy(-2), tick);
            highlightTiles.put(wp.dx(-3).dy(-3), tick);
            highlightTiles.put(wp.dx(-2).dy(-3), tick);
            highlightTiles.put(wp.dx(-1).dy(-3), tick);
            previousAttack = -1;
        }
    }

    public void HighlightSpear(Actor npc)
    {
        int tick = client.getTickCount();
        WorldPoint wp = npc.getWorldLocation();
        if (previousAttack != spearID)
        {
            highlightTiles.put(wp.dx(0).dy(-2), tick);
            highlightTiles.put(wp.dx(2).dy(-2), tick);
            highlightTiles.put(wp.dx(4).dy(-2), tick);

            highlightTiles.put(wp.dx(6).dy(0), tick);
            highlightTiles.put(wp.dx(6).dy(2), tick);
            highlightTiles.put(wp.dx(6).dy(4), tick);

            highlightTiles.put(wp.dx(4).dy(6), tick);
            highlightTiles.put(wp.dx(2).dy(6), tick);
            highlightTiles.put(wp.dx(0).dy(6), tick);

            highlightTiles.put(wp.dx(-2).dy(4), tick);
            highlightTiles.put(wp.dx(-2).dy(2), tick);
            highlightTiles.put(wp.dx(-2).dy(0), tick);
            previousAttack = spearID;
        }
        else
        {
            highlightTiles.put(wp.dx(1).dy(-2), tick);
            highlightTiles.put(wp.dx(3).dy(-2), tick);

            highlightTiles.put(wp.dx(6).dy(1), tick);
            highlightTiles.put(wp.dx(6).dy(3), tick);

            highlightTiles.put(wp.dx(3).dy(6), tick);
            highlightTiles.put(wp.dx(1).dy(6), tick);

            highlightTiles.put(wp.dx(-2).dy(3), tick);
            highlightTiles.put(wp.dx(-2).dy(1), tick);
            previousAttack = -1;
        }
    }

    private boolean playerIsOutside()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (outsideRegionIDs.contains(mapRegion))
            {
                return true;
            }
        }
        return false;
    }

    private boolean playerIsInColo()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (colRegionID == mapRegion)
            {
                return true;
            }
        }
        return false;
    }
}