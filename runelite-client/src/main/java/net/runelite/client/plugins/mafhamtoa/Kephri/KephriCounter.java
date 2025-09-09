package net.runelite.client.plugins.mafhamtoa.Kephri;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.plugins.mafhamtoa.Util.MessageUpdate;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

public class KephriCounter {

    @Inject
    private Client client;
    @Inject
    private KephriCounterOverlay overlay;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private NpcUtil npcUtil;
    @Inject
    private ClientThread clientThread;
    @Inject
    private PartyService party;
    @Getter
    private NPC kephri;
    private boolean lastPhase = false;
    private boolean scarabCapReached = false;
    private boolean dungIncoming = false;
    private boolean phaseOneEnded = false;
    @Getter
    private Integer kephriCounter;
    @Getter
    private Spec nextSpec;
    @Getter
    private Set<Player> dungPlayers = new HashSet<>();
    private Spec previousSpec;
    private int members;
    @Getter
    private Integer downedTimer = null;

    private final int[] memberVarbits = {Varbits.TOA_MEMBER_0_HEALTH, Varbits.TOA_MEMBER_1_HEALTH, Varbits.TOA_MEMBER_2_HEALTH, Varbits.TOA_MEMBER_3_HEALTH,
            Varbits.TOA_MEMBER_4_HEALTH, Varbits.TOA_MEMBER_5_HEALTH, Varbits.TOA_MEMBER_6_HEALTH, Varbits.TOA_MEMBER_7_HEALTH};
    private static final int SOLDIER_SCARAB_ID = 11724;
    private static final int SPITTING_SCARAB_ID = 11725;
    private static final int KEPHRI_NPC_ID = 11719;
    private static final int KEPHRI_LASTPHASE_NPC_ID = 11721;
    private static final int KEPHRI_DOWNED_NPC_ID = 11720;
    private static final int KEPHRI_DEAD_NPC_ID = 11722;
    private static final int KEPHRI_SMALLEGG_NPC_ID = 11728;
    private static final int AGILE_SCARAB_NPC_ID = 11727;
    private final int SCARAB_SWAM_HEAL_KEPHRI_NPC_ID = 11723;
    private static final int KEPHRI_SMALLEGG_PROJ_ID = 2165;
    private static final int KEPHRI_AUTO_ID = 1481;
    private static final int KEPHRI_DUNG_FLIES_ID = 2146;
    private Instant lastPhaseSwitchTime;
    private Integer lastAttackTime;
    private Set<NPC> agileScarabs = new HashSet<>();
    @Getter
    private Set<NPC> scarabSwarmHealKephri = new HashSet<>();
    private Set<NPC> scarabSwarmNextToKephri = new HashSet<>();
    private Set<NPC> attackedScarabSwarm = new HashSet<>();
    private Set<Projectile> projectileCache = new HashSet<>();
    private HashMap<NPC, Integer> moreOverlordsCheckMap = new HashMap();
    @Getter
    private WorldArea kephriArea;
    private Instant NPEFixDelay;
    private static final int[] previous_exp = new int[Skill.values().length - 1];
    private boolean resetXpTrackerLingerTimerFlag = false;

    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    public void startUp()
    {
        overlayManager.add(overlay);
        hooks.registerRenderableDrawListener(drawListener);
        NPEFixDelay = Instant.now();
        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invokeLater(() ->
            {
                int[] xps = client.getSkillExperiences();
                System.arraycopy(xps, 0, previous_exp, 0, previous_exp.length);
            });
        } else {
            Arrays.fill(previous_exp, 0);
        }
    }

    public void shutDown()
    {
        overlayManager.remove(overlay);
        hooks.unregisterRenderableDrawListener(drawListener);
        reset();
    }

    private void reset()
    {
        lastPhase = false;
        downedTimer = null;
        phaseOneEnded = false;
        scarabCapReached = false;
        dungIncoming = false;
        kephri = null;
        kephriCounter = null;
        kephriArea = null;
        members = 0;
        agileScarabs.clear();
        scarabSwarmHealKephri.clear();
        scarabSwarmNextToKephri.clear();
        attackedScarabSwarm.clear();
        moreOverlordsCheckMap.clear();
        nextSpec = null;
        lastAttackTime = null;
    }

    private enum Spec
    {
        Dung,
        Eggs
        ,Unknown
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.HOPPING) {
            Arrays.fill(previous_exp, 0);
            resetXpTrackerLingerTimerFlag = true;
        }
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN && resetXpTrackerLingerTimerFlag) {
            resetXpTrackerLingerTimerFlag = false;
            NPEFixDelay = Instant.now();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged)
    {
        if (kephri == null)
        {
            return;
        }
        if (animationChanged.getActor() != kephri)
        {
            return;
        }
        if (dungIncoming)
        {
            if (animationChanged.getActor().getAnimation() == 9578) //dung/eggs jump anim
            {
                previousSpec = Spec.Dung;
                if (!scarabCapReached)
                {
                    nextSpec = Spec.Eggs;
                }
            }
            //wait 5 seconds to make sure we didn't double dung otherwise it would erroneously
            //set the counter to 2 immediately after switching
            if (lastPhase && Instant.now().isAfter(lastPhaseSwitchTime.plusSeconds(5)))
            {
                kephriCounter = 2;
            }
            else kephriCounter = 5;
        }
        //if kephri does *any* animation we know the dung is over
        dungIncoming = false;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Kephri.";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        if (checkStrings(message, challengeComplete, diedFinal, leftRaid))
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
    public void onMessageUpdate(MessageUpdate message) {
        if (message.getScarabID() != null)
        {
           for (NPC npc : client.getNpcs())
           {
               if (npc.getId() == SCARAB_SWAM_HEAL_KEPHRI_NPC_ID)
               {
                   if (npc.getIndex() == message.getScarabID())
                   {
                       attackedScarabSwarm.add(npc);
                       scarabSwarmHealKephri.remove(npc);
                   }
               }
           }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == SPITTING_SCARAB_ID)
        {
            moreOverlordsCheckMap.put(npcSpawned.getNpc(), client.getTickCount());
        }
        //Soldier scarab spawns 1 tick after spitting scarab
        //First down we need to decrement 3 ticks from the down counter
        //Second down we already know MO is on so we set the timer correctly
        if (npcSpawned.getNpc().getId() == SOLDIER_SCARAB_ID)
        {
            //If it's first down, only one spitting
            if (moreOverlordsCheckMap.size() == 1)
            {
                moreOverlordsCheckMap.put(npcSpawned.getNpc(), client.getTickCount());
                if (downedTimer != null && moreOverlordsBoolean(moreOverlordsCheckMap))
                {
                    downedTimer = (downedTimer - 3);
                }
            }
        }
        if (npcSpawned.getNpc().getId() == KEPHRI_NPC_ID)
        {
            kephri = npcSpawned.getNpc();
            kephriCounter = 3;
            nextSpec = Spec.Dung;
            lastPhase = false;
            kephriArea = new WorldArea(kephri.getWorldLocation().getX(), kephri.getWorldLocation().getY(), 5, 5, client.getPlane());
        }
        if (npcSpawned.getNpc().getId() == AGILE_SCARAB_NPC_ID)
        {
            agileScarabs.add(npcSpawned.getNpc());
            if (agileScarabs.size() >= 5)
            {
                scarabCapReached = true;
            }
        }
        if (npcSpawned.getNpc().getId() == SCARAB_SWAM_HEAL_KEPHRI_NPC_ID)
        {
            //don't highlight scarabs that can never reach kephri
            if (downedTimer == null || downedTimer > 8)
            {
                scarabSwarmHealKephri.add(npcSpawned.getNpc());
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc().getId() == AGILE_SCARAB_NPC_ID)
        {
            scarabCapReached = false;
            agileScarabs.remove(npcDespawned.getNpc());
            //Cap no longer reached, put spec back to eggs if necessary
            if (!scarabCapReached && previousSpec == Spec.Dung)
            {
                nextSpec = Spec.Eggs;
            }
        }
        if (npcDespawned.getNpc().getId() == SCARAB_SWAM_HEAL_KEPHRI_NPC_ID)
        {
            scarabSwarmHealKephri.remove(npcDespawned.getNpc());
            scarabSwarmNextToKephri.remove(npcDespawned.getNpc());
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        if (client.getLocalPlayer() == null) {
            return;
        }
        if (NPEFixDelay == null || !Instant.now().isAfter(NPEFixDelay.plusSeconds(2))) {
            return;
        }
        int currentXp = event.getXp();
        int previousXp = previous_exp[event.getSkill().ordinal()];
        if (previousXp > 0 && currentXp - previousXp > 0) {
            if (event.getSkill() == net.runelite.api.Skill.RANGED) {
                for (NPC npc : client.getNpcs())
                {
                    if (npc.getId() == SCARAB_SWAM_HEAL_KEPHRI_NPC_ID)
                    {
                        if (client.getLocalPlayer().getInteracting() == npc)
                        {
                            attackedScarabSwarm.add(npc);
                            scarabSwarmHealKephri.remove(npc);
                            //I tried adding just the npc but for some reason it causes a crash
                            //instead I send the npc's world location
                            //we use that to work out which one we need to hide
                            sendScarabsToParty(npc.getIndex());
                        }
                    }
                }
            }
        }

        previous_exp[event.getSkill().ordinal()] = event.getXp();
    }

    @Subscribe
    public void onNpcChanged(NpcChanged npcChanged)
    {
        if (kephri == null)
        {
            return;
        }
        if (kephri != npcChanged.getNpc())
        {
            return;
        }
        if (npcChanged.getNpc().getId() == KEPHRI_NPC_ID)
        {
            phaseOneEnded = true;
            downedTimer = null;
        }
        if (npcChanged.getNpc().getId() == KEPHRI_LASTPHASE_NPC_ID)
        {
            lastPhase = true;
            kephriCounter = 5;
            lastPhaseSwitchTime = Instant.now();
            if (!scarabCapReached)
            {
                nextSpec = Spec.Eggs;
            }
        }
        if (npcChanged.getNpc().getId() == KEPHRI_DOWNED_NPC_ID)
        {
            lastPhaseSwitchTime = Instant.now();
            switch (kephriCounter)
            {
                case 0:
                    kephriCounter = 2;
                    break;
                case 1:
                    kephriCounter = 3;
                    break;
                case 2:
                    kephriCounter = 4;
                    break;
                default:
                    kephriCounter = 5;
                    break;
            }
            if (!scarabCapReached)
            {
                nextSpec = Spec.Unknown;
            }
        }
        if (npcChanged.getNpc().getId() == KEPHRI_DEAD_NPC_ID)
        {
            reset();
        }

    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (kephri == null)
        {
            return;
        }
        for (Projectile projectile : client.getProjectiles())
        {
            if (projectileCache.contains(projectile))
            {
                continue;
            }
            if (projectile.getId() == KEPHRI_AUTO_ID)
            {
                if (!lastPhase)
                {
                    kephriCounter--;
                    lastAttackTime = client.getTickCount();
                }
                if (lastPhase && Instant.now().isAfter(lastPhaseSwitchTime.plusMillis(400))) //case to fix where if he autos and switches to last phase at same time he starts phase 3 with a count of 4
                {
                    kephriCounter--;
                    lastAttackTime = client.getTickCount();
                }
            }
            if (projectile.getId() == KEPHRI_SMALLEGG_PROJ_ID)
            {
                nextSpec = Spec.Dung;
                previousSpec = Spec.Eggs;
                //waiting 5 secs here too for safety
                if (lastPhase && Instant.now().isAfter(lastPhaseSwitchTime.plusSeconds(5)))
                {
                    kephriCounter = 2;
                }
                else kephriCounter = 5;
            }
            projectileCache.add(projectile);
        }
        if (kephri.getId() == KEPHRI_DOWNED_NPC_ID)
        {
            //if phase 1
            if (!phaseOneEnded && downedTimer == null)
            {
                downedTimer = 54;
            }
            //if phase 2
            if (phaseOneEnded && downedTimer == null)
            {
                if (moreOverlordsBoolean(moreOverlordsCheckMap))
                {
                    downedTimer = 48;
                }
                else downedTimer = 51;
            }
            if (downedTimer > -1)
            {
                downedTimer--;
            }
        }
        Iterator<NPC> iterator = scarabSwarmHealKephri.iterator();
        while (iterator.hasNext()) {
            NPC swarm = iterator.next();
            if (swarm.getWorldLocation().distanceTo(kephriArea) < 2) {
                scarabSwarmNextToKephri.add(swarm);
                iterator.remove();
            }
            if (npcUtil.isDying(swarm))
            {
                iterator.remove();
            }
        }

        dungPlayers.clear();
        //System.out.println("Scarab count: " + agileScarabs.size() + " Scarab reached: " + scarabCapReached);

        //if it's been 6 ticks since the last attack and the kephri counter is 0 then we know dung is about to happen
        if (lastAttackTime != null && client.getTickCount() - lastAttackTime > 5 && kephriCounter != null && kephriCounter == 0)
        {
            dungIncoming = true;
        }
        if (dungIncoming)
        {
            int members = 0;
            members = findGroupSize(members);
            if (members < 3)
            {
                dungPlayers.addAll(client.getPlayers());
            }
            else
            {
                for (Player player : client.getPlayers())
                {
                    if (player.getGraphic() == KEPHRI_DUNG_FLIES_ID)
                    {
                        dungPlayers.add(player);
                    }
                }
            }
        }
        if (!projectileCache.isEmpty())
        {
            projectileCache.removeIf(projectile -> projectile.getRemainingCycles() <= 0);
        }
    }

    private int findGroupSize(int members) {
        for (int varbit : memberVarbits) {
            members += Math.min(client.getVarbitValue(varbit), 1);
        }

        return members;
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI) {
        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if (npcUtil.isDying(npc) && npc.getId() == SCARAB_SWAM_HEAL_KEPHRI_NPC_ID)
            {
                return false;
            }
            if (scarabSwarmNextToKephri.contains(npc) && config.hideScarabs())
            {
                return false;
            }
            if (attackedScarabSwarm.contains(npc) && config.hideAttackedSwarms())
            {
                return false;
            }
        }
        return true;
    }

    private void sendScarabsToParty(Integer scarabID)
    {
        if (party.isInParty())
        {
            party.send(new MessageUpdate(null, null, null, null, scarabID));
        }
    }

    private boolean moreOverlordsBoolean(HashMap<NPC, Integer> map)
    {
        if (moreOverlordsCheckMap.isEmpty())
        {
            return false;
        }

        List<Integer> sortedValues = new ArrayList<>(map.values());
        Collections.sort(sortedValues);
        // We just check if a ranger and melee spawned 1 tick apart. This can only happen with MO on
        for (int i = 0; i < sortedValues.size() - 1; i++) {
            int diff = Math.abs(sortedValues.get(i) - sortedValues.get(i + 1));
            if (diff == 1) {
                return true;
            }
        }

        return false;
    }
}