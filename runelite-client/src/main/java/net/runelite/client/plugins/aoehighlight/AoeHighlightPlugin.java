/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.aoehighlight;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import com.google.inject.Binder;
import com.google.inject.Provides;

import java.time.Instant;
import java.util.*;
import javax.inject.Inject;

@PluginDescriptor (
		name = "Aoe Highlights",
		description = "Highlights various aoe attacks",
		tags = {"npcs", "aoe", "highlight", "npc", "projectile", "gfx"}
)

public class AoeHighlightPlugin extends Plugin {

	@Inject
	private Client client;
	@Inject
	AoeHighlightOverlay overlay;
	@Inject
	AoeHighlightConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Getter
	private NPC warden;
	@Override
	public void configure(Binder binder) {
		binder.bind(AoeHighlightOverlay.class);
	}

	@Provides
	AoeHighlightConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(AoeHighlightConfig.class);
	}

	@Override
	protected void startUp() {
		overlayManager.add(overlay);
	}
	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
	}

	private final List<EntityObject> Highlights = new ArrayList<>();
	private final Set<Projectile> projectileList = new HashSet<>();
	private Set<Projectile> projectileCache = new HashSet<>();
	public List<EntityObject> getBuiltObjects() {
		return Highlights;
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated e) {
		GraphicsObject g = e.getGraphicsObject();
		int id = g.getId();

		Instant startTime = Instant.now();
		int endCycle = 0;
		int startTimeTicks = client.getTickCount();
		LocalPoint location = g.getLocation();
		Entity entityObject = Entity.getEntity(id);

		if (entityObject != null && entityObject.getType()==2 && isConfigEnabled(id)) {
			createObject(startTime, startTimeTicks, endCycle, location, entityObject);
		}
	}

	public void createObject(Instant startTime, int startTimeTicks, int endCycle, LocalPoint location, Entity entityObject) {
		EntityObject builtObject = new EntityObject(startTime, startTimeTicks, endCycle, location, entityObject);
		Highlights.add(builtObject);
	}

	private boolean isConfigEnabled(int id) {
		Entity entityObject = Entity.getEntity(id);
		if (entityObject == null) {
			return false;
		}

		switch (entityObject) {
			case WARDENS_LIGHTNING:
				return config.isWardensLightningEnabled();
			case WARDENS_TILEFLIP:
				return config.isWardensTileFlipEnabled();
			case BABA_MIDDLE_SHADOW:
			case BABA_OUTER_SHADOW:
			case BABA_SARC_DISCHARGE:
				return config.isBabaEnabled();
			case BABA_ROCKS:
			case BABA_ROCKS_FAST:
			case KEPHRI_PUZZLE_ROCKS:
				return config.isBabaRocksEnabled();
			case GROTESQUE_ENERGY_BEAMS:
			case GROTESQUE_FALLING_ROCK:
			case GROTESQUE_FREEZE_BALL:
				return config.isGrotesqueEnabled();
			case HYDRA_LIGHTNING:
				return config.isHydraEnabled();
			case WARDENS_LIGHTNING_BOX:
			case WARDENS_DIRT:
				return config.isWardensP2Enabled();
			case LIZARDMAN_SHAMAN_AOE:
				return config.isShamansEnabled();
			case CRAZY_ARCHAEOLOGIST_AOE:
				return config.isArchaeologistEnabled();
			case ICE_DEMON_RANGED_AOE:
			case ICE_DEMON_ICE_BARRAGE_AOE:
				if (inRaid()) {
					return config.isIceDemonEnabled();
				}
				else {
					return false;
				}
			case VASA_AWAKEN_AOE:
			case VASA_RANGED_AOE:
				return config.isVasaEnabled();
			case TEKTON_METEOR_AOE:
				return config.isTektonEnabled();
			case VORKATH_BOMB:
			case VORKATH_POISON_POOL:
			case VORKATH_SPAWN:
			case VORKATH_TICK_FIRE:
				return config.isVorkathEnabled();
			case VETION_LIGHTNING_BLUE:
			case VETION_LIGHTNING_ORANGE:
				return config.isVetionEnabled();
			case CHAOS_FANATIC:
				return config.isChaosFanaticEnabled();
			case GALVEK_BOMB:
			case GALVEK_MINE:
				return config.isGalvekEnabled();
			//case OLM_FALLING_CRYSTAL:
			case OLM_BURNING:
			case OLM_POISON:
			case OLM_CHOSEN_CRYSTAL:
				return config.isOlmEnabled();
			case CORPOREAL_BEAST_SPERM:
			case CORPOREAL_BEAST_DARK_CORE:
				return config.isCorpEnabled();
			case WINTERTODT_SNOW_FALL:
				return config.isWintertodtEnabled();
			case ZEBAK_POISON_BIG:
			case ZEBAK_POISON_MINI:
			case ZEBAK_WATER:
				return config.isZebakEnabled();
			case KEPHRI_DUNG_BIG:
			case KEPHRI_KAMIKAZE:
				return config.isKephriEnabled();
			case CERBERUS_LAVA:
				return config.isCerberusEnabled();
			case VARDORVIS:
				return config.isVardorvisEnabled();
			case DEMONIC_GORILLAS:
				return config.isDemonicGorillasEnabled();
			case SIRE_MIASMA:
				return config.isSireEnabled();
			case ZULRAH_POISON:
			case ZULRAH_RANGED:
				return config.isZulrahEnabled();
			case MAIDEN_BLOOD:
				return config.maidenSplat();
			case VERZIK_P2_BOMB:
				return config.verzikSplat();
			case BLOAT_FEET:
				return config.bloatFeet();
			case MUSPAH_1:
			case MUSPAH_2:
			case MUSPAH_3:
				return config.muspahSpikes();
			case SOL_SPLAT:
			case SOL_BEAMS:
				return config.solHeredit();
			case ARAXXOR_POISON:
				return config.araxxor();
			case VENENATIS:
				return config.venenatis();
			case VERZIK_SPIDERWEB:
				return config.verzikWeb();
			case WARDEN_SKULLS:
				return config.wardenSkull();
		}
		return false;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		for (Projectile p : client.getProjectiles())
		{
			int id = p.getId();
			Instant startTime = Instant.EPOCH;
			int startTimeTicks = client.getTickCount();
			int endCycle = p.getEndCycle();
			LocalPoint location = p.getTarget();
			Entity entityObject = Entity.getEntity(id);

			if (entityObject  != null &&
					entityObject.getType()==1 &&
					isConfigEnabled(id) &&
					!projectileList.contains(p) &&
					!projectileCache.contains(p)
			) {
				createObject(startTime, startTimeTicks, endCycle, location, entityObject);
				projectileList.add(p);
				projectileCache.add(p);
			}
		}
		boolean inWardenP3 = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 15696);
		if (!inWardenP3)
		{
			warden = null;
		}
		if (projectileList.isEmpty())
		{
			return;
		}
		projectileList.clear();
		if (!projectileCache.isEmpty())
		{
			projectileCache.removeIf(projectile -> projectile.getRemainingCycles() <= 0);
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getId() == 11762 || npc.getId() == 11761) //elid or tumek wardens
		{
			warden = npc;
		}
	}

	public boolean inRaid()
	{
		return client.getVarbitValue(Varbits.IN_RAID) == 1;
	}
}
