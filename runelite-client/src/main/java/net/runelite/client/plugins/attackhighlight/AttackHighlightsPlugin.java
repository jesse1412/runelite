package net.runelite.client.plugins.attackhighlight;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.events.AnimationChanged;


@Slf4j
@PluginDescriptor(
	name = "Attack Highlights"
)
public class AttackHighlightsPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private AttackHighlightsOverlay overlay;
	@Inject
	private AttackHighlightsPanelOverlay overlayPanel;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private AttackHighlightsConfig config;


	/**
	 * NPCs in each highlight group
	 */
	private final Set<NPC> highlights1 = new HashSet<>();
	private final Set<NPC> highlights2 = new HashSet<>();
	private final Set<NPC> highlights3 = new HashSet<>();
	private final Set<NPC> highlights4 = new HashSet<>();
	private final Set<NPC> highlights5 = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final List<Set<NPC>> groupHighlights = ImmutableList.of(
		highlights1, highlights2, highlights3, highlights4, highlights5
	);

	@Getter(AccessLevel.PACKAGE)
	public final List<Set<String>> IDs = getIDs();

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlayPanel);
		overlayManager.add(overlay);
		clientThread.invoke(this::buildHighlights);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
		overlayManager.remove(overlayPanel);
		clientThread.invoke(() -> {
			for (Set<NPC> highlights : groupHighlights) {
				highlights.clear();
				overlayPanel.listedIDs.clear();
				overlayPanel.listedActors.clear();
			}
		});
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
			for (Set<NPC> highlights : groupHighlights) {
				highlights.clear();		// prevent highlighting anything when not logged in
				overlayPanel.listedIDs.clear();
				overlayPanel.listedActors.clear();
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals("attackhighlights")) return;
		clientThread.invoke(this::buildHighlights);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null) return;

		int group = 1;
		for (Set<NPC> highlights : groupHighlights) {
			if (matchesNpcName(npcName, getHighlightNames(group))) {
				highlights.add(npc);
			}
			group++;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		final NPC npc = npcDespawned.getNpc();

		for (Set<NPC> highlights : groupHighlights) {
			highlights.remove(npc);
		}
	}
	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged) {
		if (animationChanged.getActor().getName() == null){
			return;
		}

		String npcIDs = getNPCIDs();
		String actor = Objects.requireNonNull(animationChanged.getActor().getName()).toLowerCase();
		int animation = animationChanged.getActor().getAnimation();
		if (!npcIDs.contains(actor) || animation == -1) {
			return;
		}

		overlayPanel.receiveAnimation(animationChanged.getActor().getName());
		overlayPanel.receiveActor(String.valueOf(animationChanged.getActor().getAnimation()));
	}

	@Provides
	AttackHighlightsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(AttackHighlightsConfig.class);
	}

	/**
	 * Add all the highlighted NPCs around to the collection for rendering
	 */
	private void buildHighlights() {
		int group = 1;

		for (Set<NPC> highlights : groupHighlights) {
			highlights.clear();
			overlayPanel.listedIDs.clear();
			overlayPanel.listedActors.clear();

			if (client.getGameState() != GameState.LOGGED_IN && client.getGameState() != GameState.LOADING) {
				return;		// prevent highlighting anything when client open but not logged in
			}

			for (NPC npc : client.getNpcs()) {
				final String npcName = npc.getName();
				if (npcName == null) continue;
				if (matchesNpcName(npcName, getHighlightNames(group))) {
					highlights.add(npc);
				}
			}
			group++;
		}
	}

	private List<String> getHighlightNames(final int groupNum) {
		String npcCsv = "";
		switch (groupNum) {
			case 1: npcCsv = config.getNpcs1(); break;
			case 2: npcCsv = config.getNpcs2(); break;
			case 3: npcCsv = config.getNpcs3(); break;
			case 4: npcCsv = config.getNpcs4(); break;
			case 5: npcCsv = config.getNpcs5(); break;
		}
		return Text.fromCSV(npcCsv);
	}

	private String getNPCIDs() {
		List<String> list = Arrays.asList(config.getNpcs1(), config.getNpcs2(), config.getNpcs3(), config.getNpcs4(), config.getNpcs5());
		String result = String.join(",",list);
		return result.toLowerCase();
	}
	public List<Integer> buildIDs() {
		return Arrays.stream(config.getIDs().split(",")).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
	}

	protected Color getGroupColor(final int groupNum) {
		switch (groupNum) {
			case 1: return config.getGroup1Color();
			case 2: return config.getGroup2Color();
			case 3: return config.getGroup3Color();
			case 4: return config.getGroup4Color();
			case 5: return config.getGroup5Color();
		}
		return null;
	}

	protected Color getGroupFillColor(final int groupNum) {
		// use additional setting for fill opacity so there can be a visible outline
		int alpha = 0;
		switch (groupNum) {
			case 1: alpha = config.getGroup1FillAlpha(); break;
			case 2: alpha = config.getGroup2FillAlpha(); break;
			case 3: alpha = config.getGroup3FillAlpha(); break;
			case 4: alpha = config.getGroup4FillAlpha(); break;
			case 5: alpha = config.getGroup5FillAlpha(); break;
			default: return null;
		}
		Color color = getGroupColor(groupNum);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	private boolean matchesNpcName(String name, List<String> highlightNames) {
		for (String highlight : highlightNames) {
			if (WildcardMatcher.matches(highlight, name)) {
				return true;
			}
		}
		return false;
	}
}