package net.runelite.client.plugins.mafhamgauntlet;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
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

@PluginDescriptor(
		name = "Mafham Gauntlet",
		description = "Gauntlet",
		tags = {"gauntlet, Mafham"},
		enabledByDefault = true
)

public class MafhamGauntletPlugin extends Plugin
{

	public static final int ONEHAND_SLASH_AXE_ANIMATION = 395;
	public static final int ONEHAND_CRUSH_PICKAXE_ANIMATION = 400;
	public static final int ONEHAND_CRUSH_AXE_ANIMATION = 401;
	public static final int UNARMED_PUNCH_ANIMATION = 422;
	public static final int UNARMED_KICK_ANIMATION = 423;
	public static final int BOW_ATTACK_ANIMATION = 426;
	public static final int ONEHAND_STAB_HALBERD_ANIMATION = 428;
	public static final int ONEHAND_SLASH_HALBERD_ANIMATION = 440;
	public static final int ONEHAND_SLASH_SWORD_ANIMATION = 390;
	public static final int ONEHAND_STAB_SWORD_ANIMATION = 386;
	public static final int HIGH_LEVEL_MAGIC_ATTACK = 1167;
	public static final int HUNLEFF_TORNADO = 8418;

	private static final Set<Integer> MELEE_ANIM_IDS = Set.of(
			ONEHAND_STAB_SWORD_ANIMATION, ONEHAND_SLASH_SWORD_ANIMATION,
			ONEHAND_SLASH_AXE_ANIMATION, ONEHAND_CRUSH_PICKAXE_ANIMATION,
			ONEHAND_CRUSH_AXE_ANIMATION, UNARMED_PUNCH_ANIMATION,
			UNARMED_KICK_ANIMATION, ONEHAND_STAB_HALBERD_ANIMATION,
			ONEHAND_SLASH_HALBERD_ANIMATION
	);

	private static final Set<Integer> ATTACK_ANIM_IDS = new HashSet<>();

	static
	{
		ATTACK_ANIM_IDS.addAll(MELEE_ANIM_IDS);
		ATTACK_ANIM_IDS.add(BOW_ATTACK_ANIMATION);
		ATTACK_ANIM_IDS.add(HIGH_LEVEL_MAGIC_ATTACK);
	}

	public static final File SOUND_FOLDER = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "gauntlet");
	public static final File MAGE_FILE =  new File(SOUND_FOLDER, "mage.wav");
	public static final File RANGE_FILE = new File(SOUND_FOLDER, "range.wav");
	private static final int HUNLLEF_RANGED_NPC_ID = 9036;
	private static final int HUNLLEF_MAGE_NPC_ID = 9037;
	private static final int HUNLLEF_MELEE_NPC_ID = 9035;
	private static final Set<Integer> TORNADO_IDS = Set.of(NullNpcID.NULL_9025, NullNpcID.NULL_9039);
	@Getter
	private Set<NPC> tornadoNPCs = new HashSet<>();

	private static final List<Integer> BOSS_IDS = Arrays.asList(
			NpcID.CORRUPTED_HUNLLEF,
			NpcID.CORRUPTED_HUNLLEF_9036,
			NpcID.CORRUPTED_HUNLLEF_9037,
			NpcID.CORRUPTED_HUNLLEF_9038,
			NpcID.CRYSTALLINE_HUNLLEF,
			NpcID.CRYSTALLINE_HUNLLEF_9022,
			NpcID.CRYSTALLINE_HUNLLEF_9023,
			NpcID.CRYSTALLINE_HUNLLEF_9024
	);

	private static final List<Integer> RANGE_PROJ_IDS = Arrays.asList(1711, 1712);
	private static final List<Integer> MAGE_PROJ_IDS = Arrays.asList(1707, 1708);
	private int STOMP_ANIM_ID = 8420;
	private final int DOOR_ID = 37337;
	private final int DOOR_REGULAR_ID = 37339;
	@Getter
	private Integer playerAttackCount;
	@Getter
	private Integer tornadoTimer;
	private boolean animCheckDelay = false;
	private boolean tornadoTimerStarted = false;
	private Clip clip = null;

	enum attackStyle
	{
		RANGE,
		MAGE
	}

	@Inject
	private Notifier notifier;

	@Getter(AccessLevel.PACKAGE)
	private NPC mainBoss;

	@Getter(AccessLevel.PACKAGE)
	private int bossAttackCounter;

	@Getter(AccessLevel.PACKAGE)
	private attackStyle currentAttackStyle;

	private final ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private List<WorldPoint> doorPoints = new ArrayList<>();
	@Getter
	private WorldPoint centrePoint;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private MafhamGauntletConfig config;
	@Inject
	private MafhamGauntletOverlay overlay;
	@Inject
	private MafhamGauntletSceneOverlay sceneOverlay;
	@Inject
	private MafhamGauntletOverlayWidget overlayWidget;

	@Provides
	MafhamGauntletConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MafhamGauntletConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(sceneOverlay);
		overlayManager.add(overlayWidget);
		mainBoss = null;
		currentAttackStyle = null;
		bossAttackCounter = 0;
		animCheckDelay = false;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(sceneOverlay);
		overlayManager.remove(overlayWidget);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final MenuEntry menuEntry = event.getMenuEntry();
		if (menuEntry.getOption().contains("Light") && shiftModifier())
		{
			menuEntry.setDeprioritized(true);
		}
	}

	@Subscribe
	private void onAnimationChanged(final AnimationChanged event) {
		if (mainBoss == null) {
			return;
		}

		final Actor actor = event.getActor();
		final int animationId = actor.getAnimation();

		if (actor instanceof Player) {
			if (doorPoints.size() < 4)
			{
				return;
			}
			WorldArea bossRoom = findBossRoomArea();
			if (!bossRoom.contains(client.getLocalPlayer().getWorldLocation()))
			{
				return;
			}

			if (!ATTACK_ANIM_IDS.contains(animationId)) {
				return;
			}

			final boolean validAttack = isAttackAnimationValid(animationId);

			if (validAttack) {
				if (playerAttackCount != null && playerAttackCount > 0) {
					playerAttackCount--;
				}
				else playerAttackCount = 5;
			}
		}
	}

	private boolean isAttackAnimationValid(final int animationId)
	{
		int npcID = mainBoss.getId();

		switch (npcID)
		{
			case HUNLLEF_MELEE_NPC_ID:
				if (MELEE_ANIM_IDS.contains(animationId))
				{
					return false;
				}
				break;
			case HUNLLEF_RANGED_NPC_ID:
				if (animationId == BOW_ATTACK_ANIMATION)
				{
					return false;
				}
				break;
			case HUNLLEF_MAGE_NPC_ID:
				if (animationId == HIGH_LEVEL_MAGIC_ATTACK)
				{
					return false;
				}
				break;
		}

		return true;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		if (gameObjectSpawned.getGameObject().getId() == DOOR_ID || gameObjectSpawned.getGameObject().getId() == DOOR_REGULAR_ID)
		{
			doorPoints.add(gameObjectSpawned.getGameObject().getWorldLocation());
			if (doorPoints.size() == 4)
			{
				centrePoint = findCenterPoint(
						doorPoints.get(0),
						doorPoints.get(1),
						doorPoints.get(2),
						doorPoints.get(3)
				);
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (BOSS_IDS.contains(npc.getId()))
		{
			mainBoss = npc;
			playerAttackCount = 5;
		}
		if (TORNADO_IDS.contains(npc.getId()))
		{
			tornadoNPCs.add(npc);
			tornadoTimerStarted = true;
			if (mainBoss != null && npc.getIndex() < mainBoss.getIndex())
			{
				tornadoTimer = 21;
			}
			else tornadoTimer = 20;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		if (TORNADO_IDS.contains(npcDespawned.getNpc().getId()))
		{
			tornadoNPCs.remove(npcDespawned.getNpc());
		}
		if (mainBoss == null)
		{
			return;
		}
		NPC npc = npcDespawned.getNpc();
		if (mainBoss.getId() == npc.getId() || !client.isInInstancedRegion())
		{
			mainBoss = null;
			currentAttackStyle = null;
			bossAttackCounter = 0;
			animCheckDelay = false;
			//notifier.notify("Full var reset");
		}
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		//notifier.notify("Projectile detected");
		if (currentAttackStyle != null)
		{
			//notifier.notify("Returning early");
			return;
		}
		if (projectiles.contains(event.getProjectile()))
		{
			return;
		}
		//projectiles.add(event.getProjectile());
		if (RANGE_PROJ_IDS.contains(event.getProjectile().getId()))
		{
			currentAttackStyle = attackStyle.RANGE;
			//notifier.notify("Initial selection: Range");
		}
		else if (MAGE_PROJ_IDS.contains(event.getProjectile().getId()))
		{
			currentAttackStyle = attackStyle.MAGE;
			//notifier.notify("Initial selection: Mage");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (mainBoss == null)
		{
			currentAttackStyle = null;
			bossAttackCounter = 0;
			animCheckDelay = false;
			centrePoint = null;
			doorPoints.clear();
			tornadoNPCs.clear();
			playerAttackCount = null;
			tornadoTimerStarted = false;
			tornadoTimer = null;
			//notifier.notify("Full var reset 2");
			return;
		}
		if (tornadoTimerStarted && tornadoTimer != null)
		{
			if (tornadoTimer > 0)
			{
				tornadoTimer--;
			}
			else tornadoTimer = null;
		}

		//these 3 ids at the end are new style switching animations that didn't exist when jesse made this plugin. I can't be arsed making a proper fix for this
		//it really should just be checking only for the attack anims and not for anything that isn't an attack animation, that's stupid
		//but I don't care!
		if (mainBoss.getAnimation() != -1 && mainBoss.getAnimation() != STOMP_ANIM_ID && mainBoss.getAnimation() != 8754 && mainBoss.getAnimation() != 8753 && mainBoss.getAnimation() != 8755)
		{
			if (animCheckDelay)
			{
				return;
			}
			bossAttackCounter++;
			animCheckDelay = true;
			if (bossAttackCounter == 4)
			{
				//notifier.notify("4th Attack detected");
				bossAttackCounter = 0;
				if (currentAttackStyle == attackStyle.MAGE)
				{
					currentAttackStyle = attackStyle.RANGE;
					if (config.playSounds())
					{
						playSound(RANGE_FILE, config.masterVolume());
					}
					//notifier.notify("Switched to range");
				}
				else
				{
					currentAttackStyle = attackStyle.MAGE;
					if (config.playSounds())
					{
						playSound(MAGE_FILE, config.masterVolume());
					}
					//notifier.notify("Switched to mage");
				}
			}
		}
		else
		{
			animCheckDelay = false;
		}
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

	private void setVolume(int volume)
	{
		float vol = volume/100.0f;
		vol *= config.masterVolume()/100.0f;
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20.0f * (float) Math.log10(vol));
	}

	private WorldPoint findCenterPoint(WorldPoint point1, WorldPoint point2, WorldPoint point3, WorldPoint point4) {
		int averageX = (point1.getX() + point2.getX() + point3.getX() + point4.getX()) / 4;
		int averageY = (point1.getY() + point2.getY() + point3.getY() + point4.getY()) / 4;
		int plane = point1.getPlane();

		return new WorldPoint(averageX, averageY, plane);
	}

	private boolean shiftModifier()
	{
		return client.isKeyPressed(KeyCode.KC_SHIFT);
	}

	private WorldArea findBossRoomArea()
	{
		int x1;
		int x2;
		int y1;
		int y2;
		int minx1;
		int minx2;
		int miny1;
		int miny2;
		int maxx1;
		int maxx2;
		int maxy1;
		int maxy2;
		minx1 = Math.min(doorPoints.get(0).getX(), doorPoints.get(1).getX());
		minx2 = Math.min(doorPoints.get(2).getX(), doorPoints.get(3).getX());
		x1 = Math.min(minx1, minx2);

		maxx1 = Math.max(doorPoints.get(0).getX(), doorPoints.get(1).getX());
		maxx2 = Math.max(doorPoints.get(2).getX(), doorPoints.get(3).getX());
		x2 = Math.max(maxx1, maxx2);

		miny1 = Math.min(doorPoints.get(0).getY(), doorPoints.get(1).getY());
		miny2 = Math.min(doorPoints.get(2).getY(), doorPoints.get(3).getY());
		y1 = Math.min(miny1, miny2);

		maxy1 = Math.max(doorPoints.get(0).getY(), doorPoints.get(1).getY());
		maxy2 = Math.max(doorPoints.get(2).getY(), doorPoints.get(3).getY());
		y2 = Math.max(maxy1, maxy2);
		WorldPoint worldPoint = new WorldPoint(x1, y1, client.getPlane());
		int height = y2 - y1;
		int width = x2 - x1;

		return new WorldArea(worldPoint, width, height);
	}
}
