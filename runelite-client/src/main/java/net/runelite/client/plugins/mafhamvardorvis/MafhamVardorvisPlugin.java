package net.runelite.client.plugins.mafhamvardorvis;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

@PluginDescriptor(
        name = "Mafham Vardorvis",
        description = "Mafham Vardorvis",
        tags = {"Mafham", "Vardorvis"}
)

public class MafhamVardorvisPlugin extends Plugin {

    public static final File SOUND_FOLDER = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "projectile-sounds");
    public static final File RANGE_FILE = new File(SOUND_FOLDER, "range.wav");
    private Clip clip = null;
    private final int AXE_ID = 12225;
    private final int VARD_ID = 12223;
    private final int THROWN_ID = 12227;
    private final int VARDHEAD_ID = 12226;
    private final int PILLAR_ID = 48423;
    @Getter
    private Integer closeCornerAxeSpawnTick;
    @Getter
    private GameObject pillar;
    @Getter
    private Integer headTimer;
    private Set<NPC> axes = new HashSet<>();
    @Getter
    private Set<NPC> cornerAxes = new HashSet<>();
    @Getter
    private boolean runThrough;
    @Getter
    private Set<WorldPoint> tiles = new HashSet<>();
    @Inject
    private Client client;
    @Inject
    private MafhamVardorvisConfig config;
    @Inject
    private MafhamVardorvisOverlay overlay;
    @Inject
    private MafhamVardorvisOverlayPanel overlayPanel;
    @Inject
    private MafhamVardorvisOverlayWidget overlayWidget;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Hooks hooks;
    @Provides
    MafhamVardorvisConfig getconfig(ConfigManager configManager){return configManager.getConfig(MafhamVardorvisConfig.class);}

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        overlayManager.add(overlayPanel);
        overlayManager.add(overlayWidget);
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(overlayPanel);
        overlayManager.remove(overlayWidget);
        hooks.unregisterRenderableDrawListener(drawListener);
        reset();
    }

    private void reset()
    {
        tiles.clear();
        axes.clear();
        cornerAxes.clear();
        headTimer = null;
        runThrough = false;
        pillar = null;
        closeCornerAxeSpawnTick = null;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
    {
        if (gameObjectSpawned.getGameObject().getId() == PILLAR_ID)
        {
            pillar = gameObjectSpawned.getGameObject();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (npcSpawned.getNpc().getId() == AXE_ID)
        {
            axes.add(npcSpawned.getNpc());
            if (npcSpawned.getNpc().getWorldLocation().distanceTo(pillar.getWorldLocation().dx(2).dy(9)) < 1)
            {
                closeCornerAxeSpawnTick = client.getTickCount();
            }
        }
        if (npcSpawned.getNpc().getId() == VARDHEAD_ID)
        {
            headTimer = 3;
            if (config.playRangeSound())
            {
                playSound(RANGE_FILE, config.masterVolume());
            }
        }
        if (npcSpawned.getNpc().getId() == THROWN_ID)
        {
            WorldPoint worldPoint = pillar.getWorldLocation().dx(-6).dy(1);
            if (npcSpawned.getNpc().getWorldLocation().distanceTo(worldPoint) < 1)
            {
                cornerAxes.add(npcSpawned.getNpc());
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (npcDespawned.getNpc().getId() == AXE_ID)
        {
            axes.remove(npcDespawned.getNpc());
        }
        if (npcDespawned.getNpc().getId() == THROWN_ID || npcDespawned.getNpc().getId() == VARD_ID)
        {
            tiles.clear();
            axes.clear();
            cornerAxes.remove(npcDespawned.getNpc());
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        //Safety reset
        if ((!ArrayUtils.contains(client.getMapRegions(), 4405) && !tiles.isEmpty()) ||
                (!ArrayUtils.contains(client.getMapRegions(), 4405) && !axes.isEmpty()))
        {
            reset();
        }
        runThrough = false;
        for (NPC cornerAxe : cornerAxes)
        {
            WorldPoint worldPoint = pillar.getWorldLocation().dy(7);
            if (cornerAxe.getWorldLocation().distanceTo(worldPoint) < 1)
            {
                runThrough = true;
                cornerAxes.remove(cornerAxe);
            }
        }

        //Grab all the tiles of the axe (is there a better way to do this?)
        int[][] tileOffsets = {
                {0, 0},
                {1, 0},
                {2, 0},
                {0, 1},
                {1, 1},
                {2, 1},
                {0, 2},
                {1, 2},
                {2, 2}
        };

        for (NPC axe : axes) {
            int orientation = axe.getOrientation();
            int x = 0;
            int y = 0;

            switch (orientation) {
                case 1024: // North
                    y = 1;
                    break;
                case 1280: // North-East
                    x = 1; y = 1;
                    break;
                case 1536: // East
                    x = 1;
                    break;
                case 1792: // South-East
                    x = 1; y = -1;
                    break;
                case 0: // South
                    y = -1;
                    break;
                case 256: // South-West
                    x = -1; y = -1;
                    break;
                case 512: // West
                    x = -1;
                    break;
                case 768: // North-West
                    x = -1; y = 1;
                    break;
                default:
                    // aaaa
                    break;
            }

            for (int i = 0; i <= 10; i++) {
                for (int[] offset : tileOffsets) {
                    tiles.add(axe.getWorldLocation().dx(x * i + offset[0]).dy(y * i + offset[1]));
                }
            }
        }
        if (headTimer != null)
        {
            if (headTimer > -1)
            {
                headTimer--;
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

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI) {
        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if ((npc.getId() == AXE_ID || npc.getId() == THROWN_ID) && config.entityHideAxes())
            {
                return false;
            }
        }
        return true;
    }
}