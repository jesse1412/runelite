package net.runelite.client.plugins.mafhamtob.Maiden;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

public class Maiden {

    @Inject
    private Client client;
    @Inject
    private MaidenOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Getter
    private NPC maiden;
    @Getter
    private Integer maidenCounter;
    @Getter
    private Integer maidenAttackSpeed = 10;
    private Integer leakCounter = 0;
    private List<Crab> crabList = new ArrayList<>();

    private final int MAIDEN_NPC_ID = 8360;

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        reset();
    }

    public void reset()
    {
        maiden = null;
        maidenCounter = null;
        leakCounter = 0;
        maidenAttackSpeed = 10;
        crabList.clear();
        //System.out.println("Maiden reset");
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Wave 'The Maiden of Sugadinti'";
        String diedTryAgain = "You have failed. The vampyres take pity";
        if (checkStrings(message, challengeComplete, diedTryAgain))
        {
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
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        if (crabList.isEmpty())
        {
            return;
        }
        if (!(hitsplatApplied.getActor() instanceof NPC))
        {
            return;
        }
        NPC npc = (NPC) hitsplatApplied.getActor();
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        int amount = hitsplat.getAmount();
        for (Crab crab : crabList)
        {
            if (crab.getNpc() == npc)
            {
                int health = crab.getHealth();
                NPC crabNPC = crab.getNpc();
                if (crabNPC == null || !hitsplat.isMine() && !hitsplat.isOthers())
                {
                    return;
                }
                crab.setHealth(health - amount);
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        NPC npc = npcSpawned.getNpc();
        switch (npc.getId()) {
            case NpcID.THE_MAIDEN_OF_SUGADINTI:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10814:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10822:
                maiden = npcSpawned.getNpc();
                break;
        }
        if (npc.getId() == 10828) //hard mode crab id
        {
            int partySize = 0;

            for (int i = 330; i < 335; i++)
            {
                if (!Objects.equals(client.getVarcStrValue(i), "")) //tob party names
                {
                    partySize++;
                }
            }
            int hp;
            switch (partySize)
            {
                case 1:
                case 2:
                case 3:
                    hp = 75;
                    break;
                case 4:
                    hp = 87;
                    break;
                default:
                    hp = 100;
                    break;
            }
            crabList.add(new Crab(npc, hp, false));
        }


    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc() == maiden)
        {
            maiden = null;
        }
        crabList.removeIf(crab -> crab.getNpc() == npcDespawned.getNpc());
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (!crabList.isEmpty())
        {
            clientThread.invokeAtTickEnd(this::handleLeaks);
        }

        if (maidenCounter != null)
        {
            if (maidenCounter > -1)
            {
                maidenCounter--;
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (animationChanged.getActor() == maiden)
        {
            if (animationChanged.getActor().getAnimation() != -1)
            {
                switch (leakCounter)
                {
                    case 1:
                    case 2:
                        maidenAttackSpeed = 9;
                        break;
                    case 3:
                    case 4:
                        maidenAttackSpeed = 8;
                        break;
                    case 5:
                    case 6:
                        maidenAttackSpeed = 7;
                        break;
                    case 7:
                    case 8:
                        maidenAttackSpeed = 6;
                        break;
                    case 9:
                    case 10:
                        maidenAttackSpeed = 5;
                        break;
                    default:
                        break;

                }
                maidenCounter = maidenAttackSpeed;
            }
        }
    }

    public void handleLeaks()
    {
        Iterator<Crab> iterator = crabList.iterator();
        while (iterator.hasNext())
        {
            Crab crab = iterator.next();
            NPC npc = crab.getNpc();
            if (crab.isReachedMaiden() && crab.getHealth() > 0)
            {
                leakCounter++;
                iterator.remove();
            }
            if (npc.getWorldArea().distanceTo(maiden.getWorldArea()) == 1)
            {
                crab.setReachedMaiden(true);
            }
        }
    }
}