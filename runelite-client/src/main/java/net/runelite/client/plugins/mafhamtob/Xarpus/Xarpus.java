package net.runelite.client.plugins.mafhamtob.Xarpus;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtob.Direction;
import net.runelite.client.plugins.mafhamtob.MafhamToBConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;
import java.util.spi.AbstractResourceBundleProvider;

public class Xarpus {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private XarpusOverlay overlay;
    @Inject
    private MafhamToBConfig config;
    public static final int CYCLES_PER_GAME_TICK = Constants.GAME_TICK_LENGTH / Constants.CLIENT_TICK_LENGTH;
    private static final int OVERHEAD_TEXT_TICK_TIMEOUT = 5;
    private static final int CYCLES_FOR_OVERHEAD_TEXT = OVERHEAD_TEXT_TICK_TIMEOUT * CYCLES_PER_GAME_TICK;
    private int Xarpus_previousOrientation;
    private static final int GROUNDOBJECT_ID_EXHUMED = 32743;
    private static final int ANIMATION_ID_XARPUS = 8059;
    @Getter
    private Integer exhumedCounter;

    @Getter(AccessLevel.PACKAGE)
    private boolean Xarpus_Stare;
    @Getter
    private boolean Xarpus_Stare_Dangerous;
    @Getter(AccessLevel.PACKAGE)
    private final Map<GroundObject, Integer> Xarpus_Exhumeds = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private int Xarpus_TicksUntilShoot = 8;

    @Getter(AccessLevel.PACKAGE)
    private NPC Xarpus_NPC;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        Xarpus_NPC = null;
        Xarpus_TicksUntilShoot = 8;
        Xarpus_Exhumeds.clear();
        Xarpus_Stare = false;
        Xarpus_previousOrientation = 0;
        Xarpus_Stare_Dangerous = false;
        exhumedCounter = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId()) {
            case NpcID.XARPUS:
            case NpcID.XARPUS_8339:
            case NpcID.XARPUS_8340:
            case NpcID.XARPUS_8341:
            case NpcID.XARPUS_10767:
            case NpcID.XARPUS_10766:
            case NpcID.XARPUS_10768:
            case NpcID.XARPUS_10770:
            case NpcID.XARPUS_10769:
            case NpcID.XARPUS_10771:
            case NpcID.XARPUS_10772:
            case NpcID.XARPUS_10773:
            case NpcID.XARPUS_11187:
                Xarpus_NPC = npc;
                Xarpus_Stare = false;
                Xarpus_TicksUntilShoot = 8;
                Xarpus_previousOrientation = 0;
                break;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        NPC npc = npcDespawned.getNpc();
        switch (npc.getId()) {
            case NpcID.XARPUS:
            case NpcID.XARPUS_8339:
            case NpcID.XARPUS_8340:
            case NpcID.XARPUS_8341:
            case NpcID.XARPUS_10767:
            case NpcID.XARPUS_10766:
            case NpcID.XARPUS_10768:
            case NpcID.XARPUS_10770:
            case NpcID.XARPUS_10769:
            case NpcID.XARPUS_10771:
            case NpcID.XARPUS_10772:
            case NpcID.XARPUS_10773:
            case NpcID.XARPUS_11187:
                Xarpus_NPC = null;
                Xarpus_Stare = false;
                Xarpus_Stare_Dangerous = false;
                Xarpus_TicksUntilShoot = 8;
                Xarpus_previousOrientation = 0;
                Xarpus_Exhumeds.clear();
                break;
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject o = event.getGroundObject();
        int exhumedLength;
        if (o.getId() == GROUNDOBJECT_ID_EXHUMED)
        {
            if (Xarpus_NPC.getId() == NpcID.XARPUS_8339 || Xarpus_NPC.getId() == NpcID.XARPUS_10767) //entry or normal
            {
                if (exhumedCounter == null)
                {
                    int partySize = 0;
                    for (int i = 330; i < 335; i++)
                    {
                        if (!Objects.equals(client.getVarcStrValue(i), "")) //tob party names
                        {
                            partySize++;
                        }
                    }
                    switch (partySize)
                    {
                        case 1:
                            exhumedCounter = 7;
                            break;
                        case 2:
                            exhumedCounter = 9;
                            break;
                        case 3:
                            exhumedCounter = 12;
                            break;
                        case 4:
                            exhumedCounter = 15;
                            break;
                        case 5:
                            exhumedCounter = 18;
                            break;
                        default:
                            exhumedCounter = 100;
                            break;
                    }
                }
                exhumedLength = 10;
            }
            else
            {
                if (exhumedCounter == null)
                {
                    int partySize = 0;
                    for (int i = 330; i < 335; i++)
                    {
                        if (!Objects.equals(client.getVarcStrValue(i), "")) //tob party names
                        {
                            partySize++;
                        }
                    }
                    switch (partySize)
                    {
                        case 1:
                            exhumedCounter = 9;
                            break;
                        case 2:
                            exhumedCounter = 13;
                            break;
                        case 3:
                            exhumedCounter = 16;
                            break;
                        case 4:
                            exhumedCounter = 20;
                            break;
                        case 5:
                            exhumedCounter = 24;
                            break;
                        default:
                            exhumedCounter = 100;
                            break;
                    }
                }
                exhumedLength = 8;
            }
            Xarpus_Exhumeds.put(o, exhumedLength);
            if (exhumedCounter != null)
            {
                exhumedCounter--;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (Xarpus_NPC == null)
        {
            return;
        }
        for (Iterator<GroundObject> it = Xarpus_Exhumeds.keySet().iterator(); it.hasNext();)
        {
            GroundObject key = it.next();
            Xarpus_Exhumeds.replace(key, Xarpus_Exhumeds.get(key) - 1);
            if (Xarpus_Exhumeds.get(key) < 0)
            {
                it.remove();
            }
        }
        if (Xarpus_NPC.getOverheadText() != null && !Xarpus_Stare)
        {
            Xarpus_Stare = true;
            Xarpus_TicksUntilShoot = 8;
            for (Player player : client.getPlayers())
            {
                if (config.screechIndicator())
                {
                    player.setOverheadText("Screeeeech!");
                    player.setOverheadCycle(CYCLES_FOR_OVERHEAD_TEXT);
                }
            }
        }
        if (Xarpus_Stare)
        {
            if (Xarpus_TicksUntilShoot > 0)
            {
                Xarpus_TicksUntilShoot--;
            }
            else
            {
                Xarpus_TicksUntilShoot = 7;
                Xarpus_Stare_Dangerous = true;
            }
        }
        else if (Xarpus_NPC.getId() == NpcID.XARPUS_8340 || Xarpus_NPC.getId() == 10768 || Xarpus_NPC.getId() == 10772)
        {
            exhumedCounter = null;
            if (Xarpus_TicksUntilShoot > 0)
            {
                Xarpus_TicksUntilShoot--;
            }
            else Xarpus_TicksUntilShoot = 3;
            if (Xarpus_NPC.getOrientation() != Xarpus_previousOrientation) //sync case, if he turns, set to 3
            {
                //System.out.println("orientation change");
                Xarpus_TicksUntilShoot = 3;
            }
            Xarpus_previousOrientation = Xarpus_NPC.getOrientation();
        }
    }
}