package net.runelite.client.plugins.alchemicalhydra;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.GameTick;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
        name = "Alchemical Hydra",
        description = "Highlights poison and shows prayer",
        tags = {"hydra"},
        enabledByDefault = true
)

public class AlchemicalHydraPlugin extends Plugin {

    private static final List<Integer> HYDRA_IDS = Arrays.asList(
            NpcID.ALCHEMICAL_HYDRA,
            NpcID.ALCHEMICAL_HYDRA_8616,
            NpcID.ALCHEMICAL_HYDRA_8617,
            NpcID.ALCHEMICAL_HYDRA_8618,
            NpcID.ALCHEMICAL_HYDRA_8619,
            NpcID.ALCHEMICAL_HYDRA_8620,
            NpcID.ALCHEMICAL_HYDRA_8621,
            NpcID.ALCHEMICAL_HYDRA_8622
    );

    enum attackStyle {
        RANGE,
        MAGE
    }

    private static final List<Integer> POISON_TILE_IDS = Arrays.asList(
            1645, 1660, 1661
    );

    private static final int RANGE_PROJECTILE_ID = 1663;
    private static final int MAGE_PROJECTILE_ID = 1662;
    private static final int RED_VENT_ID = 34568;
    private static final int GREEN_VENT_ID = 34569;
    private static final int BLUE_VENT_ID = 34570;
    public static final File SOUND_FOLDER = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "projectile-sounds");
    public static final File MAGE_FILE =  new File(SOUND_FOLDER, "mage.wav");
    public static final File RANGE_FILE = new File(SOUND_FOLDER, "range.wav");

    @Getter(AccessLevel.PACKAGE)
    private NPC hydra;

    @Getter(AccessLevel.PACKAGE)
    private boolean poisonActive;
    @Getter(AccessLevel.PACKAGE)
    private boolean lastPhase;
    @Getter(AccessLevel.PACKAGE)
    private boolean thirdPhase;

    @Getter(AccessLevel.PACKAGE)
    private boolean checkedProjectileThisTick;

    @Getter(AccessLevel.PACKAGE)
    private int rangeAttackCount;

    @Getter(AccessLevel.PACKAGE)
    private int mageAttackCount;

    @Getter(AccessLevel.PACKAGE)
    private int attacksPerSwitch;

    @Getter(AccessLevel.PACKAGE)
    private int flameAttackCount;

    @Getter(AccessLevel.PACKAGE)
    private int poistonAttackCount;

    @Getter(AccessLevel.PACKAGE)
    private attackStyle previousAttackStyle;

    @Getter(AccessLevel.PACKAGE)
    private attackStyle nextAttackStyle;

    @Getter(AccessLevel.PACKAGE)
    private final List<WorldPoint> PoisonTiles = new ArrayList<>();
    @Getter
    private GameObject redVent;
    @Getter
    private GameObject greenVent;
    @Getter
    private GameObject blueVent;
    @Getter
    private int ventCounter = -1;
    private int previousVentAnim;
    private int currentVentAnim;
    @Getter
    private boolean ventStarted = false;

    @Inject
    private Client client;
    private Clip clip = null;
    @Inject
    private AlchemicalHydraConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AlchemicalHydraOverlay overlay;


    @Provides
    AlchemicalHydraConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AlchemicalHydraConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        hydra = null;
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
    }

    private void reset()
    {
        hydra = null;
        greenVent = null;
        blueVent = null;
        redVent = null;
        ventCounter = -1;
        ventStarted = false;
        thirdPhase = false;
        currentVentAnim = 0;
        previousVentAnim = 0;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        NPC npc = npcSpawned.getNpc();

        if (HYDRA_IDS.contains(npc.getId())) {
            hydra = npc;
            // If final phase, we flip the projectile.
                attacksPerSwitch = 3;
                lastPhase = false;

        }
    }

    public int getAttacksToSwitch() {
        if (hydra == null) {
            return 0;
        }

        return ((rangeAttackCount + (attacksPerSwitch - 1)) % attacksPerSwitch) +
                ((mageAttackCount + (attacksPerSwitch - 1)) % attacksPerSwitch) -
                (attacksPerSwitch - 1);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (hydra == null) {
            return;
        }
        NPC npc = npcDespawned.getNpc();
        if (hydra.getId() == npc.getId()) {
            hydra = null;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() == GREEN_VENT_ID)
        {
            greenVent = gameObject;
        }
        if (gameObject.getId() == BLUE_VENT_ID)
        {
            blueVent = gameObject;
        }
        if (gameObject.getId() == RED_VENT_ID)
        {
            redVent = gameObject;
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        //If not final phase
        Projectile projectile = event.getProjectile();
        if(projectile == null)
        {
            return;
        }

        if(projectile.getId() != AlchemicalHydraPlugin.MAGE_PROJECTILE_ID
            && projectile.getId() != AlchemicalHydraPlugin.RANGE_PROJECTILE_ID)
        {
            return;
        }

        if(checkedProjectileThisTick)
        {
            return;
        }

        if (client.getGameCycle() >= projectile.getStartCycle())
        {
            return;
        }

        if (hydra.getId() != NpcID.ALCHEMICAL_HYDRA_8621) {
            if (event.getProjectile().getId() == RANGE_PROJECTILE_ID) {
                if(nextAttackStyle == null)
                {
                    nextAttackStyle = attackStyle.RANGE;
                }
                rangeAttackCount++;
                //.println("Range attack: " + rangeAttackCount);
                //System.out.println("Switch in: " + (getAttacksPerSwitch() - getAttacksToSwitch()));

                if ((rangeAttackCount) % 3 == 0) {
                    nextAttackStyle = attackStyle.MAGE;
                    playSound(MAGE_FILE, config.masterVolume());
                }
                previousAttackStyle = attackStyle.RANGE;
                if (thirdPhase)
                {
                    flameAttackCount--;
                }
            } else if (event.getProjectile().getId() == MAGE_PROJECTILE_ID) {
                if(nextAttackStyle == null)
                {
                    nextAttackStyle = attackStyle.MAGE;
                }
                mageAttackCount++;
                //System.out.println("Mage attack: " + mageAttackCount);
                //System.out.println("Attacks per switch: " + attacksPerSwitch);
                //System.out.println("Attacks to switch: " + getAttacksToSwitch());
                //System.out.println("Switch in: " + (getAttacksPerSwitch() - getAttacksToSwitch()));
                if ((mageAttackCount) % 3 == 0) {
                    nextAttackStyle = attackStyle.RANGE;
                    playSound(RANGE_FILE, config.masterVolume());
                }
                previousAttackStyle = attackStyle.MAGE;
                if (thirdPhase)
                {
                    flameAttackCount--;
                }
            }
        } else {
            if (nextAttackStyle == attackStyle.RANGE)
            {
                nextAttackStyle = attackStyle.MAGE;
            }
            else
            {
                nextAttackStyle = attackStyle.RANGE;
            }
            if (poistonAttackCount > 0)
            {
                poistonAttackCount--;
            }
            else
            {
                poistonAttackCount = 8;
            }
        }
        checkedProjectileThisTick = true;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (redVent != null)
        {
            currentVentAnim = ((DynamicObject) redVent.getRenderable()).getAnimation().getId();
        }
        if (!ArrayUtils.contains(client.getMapRegions(), 5536))
        {
            reset();
            return;
        }

        if (redVent != null && currentVentAnim == 8279 && currentVentAnim != previousVentAnim)
        {
            ventStarted = true;
            ventCounter = 2;
            //System.out.println("synced vent!");
        }

        if (ventStarted)
        {
            if (ventCounter == 0)
            {
                ventCounter = 7;
            }
            else ventCounter--;
        }

        if (redVent != null)
        {
            previousVentAnim = currentVentAnim;
        }

        if (hydra == null) {
            rangeAttackCount = 0;
            mageAttackCount = 0;
            previousAttackStyle = null;
            nextAttackStyle = null;
            return;
        }

        if(hydra.getId() == NpcID.ALCHEMICAL_HYDRA_8621 && !lastPhase)
        {
            attacksPerSwitch = 1;
            if (previousAttackStyle == attackStyle.RANGE) {
                nextAttackStyle = attackStyle.MAGE;
            } else {
                nextAttackStyle = attackStyle.RANGE;
            }
            lastPhase = true;
            poistonAttackCount = 3;
            thirdPhase = false;
        }
        if (hydra.getId() == NpcID.ALCHEMICAL_HYDRA_8620 && !thirdPhase)
        {
            flameAttackCount = 3;
            thirdPhase = true;
        }

        checkedProjectileThisTick = false;

        PoisonTiles.clear();

        for (GraphicsObject o : client.getGraphicsObjects()) {
            if (POISON_TILE_IDS.contains(o.getId())) {
                PoisonTiles.add(WorldPoint.fromLocal(client, o.getLocation()));
            }
        }
    }
    private void setVolume(int volume)
    {
        float vol = volume/100.0f;
        vol *= config.masterVolume()/100.0f;
        FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20.0f * (float) Math.log10(vol));
    }
    private void playSound(File f, int volume)
    {
        try
        {
			/* Leaving this removed for now. Calling this too many times causes client to hang.
			if (clip != null)
			{
				clip.close();
			}
			 */

            AudioInputStream is = AudioSystem.getAudioInputStream(f);
            AudioFormat format = is.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(is);
            setVolume(volume);
            clip.start();
        }
        catch (LineUnavailableException | UnsupportedAudioFileException | IOException e)
        {
        }
    }
}
