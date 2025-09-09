package net.runelite.client.plugins.projectilesounds;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


@PluginDescriptor(
        name = "Projectile sounds",
        description = "Plugin that plays sounds when a specific projectile is detected",
        tags = {"projectile", "sound"}
)
public class ProjectileSoundPlugin extends Plugin {

    public static final File SOUND_FOLDER = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "projectile-sounds");
    public static final File MAGE_FILE =  new File(SOUND_FOLDER, "mage.wav");
    public static final File RANGE_FILE = new File(SOUND_FOLDER, "range.wav");

    @Inject
    private Client client;

    @Inject
    private ProjectileSoundConfig config;

    @Provides
    ProjectileSoundConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ProjectileSoundConfig.class);
    }

    private Map<Integer, Boolean> cachedProjectileRIds;
    private Map<Integer, Boolean> cachedProjectileMIds;
    private boolean isRProjectileDetected = false;
    private boolean isMProjectileDetected = false;
    private int RstartCycle = 0;
    private int MstartCycle = 0;

    private Clip clip = null;

    @Override
    protected void startUp() {
        reloadProjectileRIds();
        reloadProjectileMIds();
    }

    @Override
    protected void shutDown() throws Exception {
        cachedProjectileRIds.clear();
        cachedProjectileMIds.clear();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("projectilesoundplugin")) {
            reloadProjectileRIds();
            reloadProjectileMIds();
        }
    }

    private void reloadProjectileRIds() {
        cachedProjectileRIds = new HashMap<>();
        String[] Rids = config.projectileRIds().split(",");
        for (String rid : Rids) {
            try {
                int projectileRId = Integer.parseInt(rid.trim());
                cachedProjectileRIds.put(projectileRId, true);
            } catch (NumberFormatException ex) {
                rid.trim();
            }
        }
    }

    private void reloadProjectileMIds() {
        cachedProjectileMIds = new HashMap<>();
        String[] Mids = config.projectileMIds().split(",");
        for (String mid : Mids) {
            try {
                int projectileMId = Integer.parseInt(mid.trim());
                cachedProjectileMIds.put(projectileMId, true);
            } catch (NumberFormatException ex) {
                mid.trim();
            }
        }
    }


    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        Projectile projectile = event.getProjectile();
        if (cachedProjectileRIds.containsKey(projectile.getId())) {
            isRProjectileDetected = true;
            RstartCycle = projectile.getStartCycle();
        }
        if (cachedProjectileMIds.containsKey(projectile.getId())) {
            isMProjectileDetected = true;
            MstartCycle = projectile.getStartCycle();
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

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (isRProjectileDetected && client.getGameCycle() <= RstartCycle) {
            playSound(RANGE_FILE, config.masterVolume());
            isRProjectileDetected = false;
        }
        if (isMProjectileDetected && client.getGameCycle() <= MstartCycle) {
            playSound(MAGE_FILE, config.masterVolume());
            isMProjectileDetected = false;
        }
    }
}
