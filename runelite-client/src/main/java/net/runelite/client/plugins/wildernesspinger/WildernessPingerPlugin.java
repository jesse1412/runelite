/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
package net.runelite.client.plugins.wildernesspinger;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;
import net.runelite.client.plugins.friendlist.FriendListPlugin;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
	name = "Wilderness Pinger",
	description = "Pings wilderness flowers",
	tags = {"wilderness", "attack", "range"}
)
public class WildernessPingerPlugin extends Plugin
{
	private static final int MIN_COMBAT_LEVEL = 3;

	@Inject
	private Client client;

	@Inject
	private WildernessPingerConfig config;
	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private Notifier notifier;

	private String[] playerNames;
	private List<String> storedFriends = new ArrayList<>();
	private Integer loggedInTime;

	@Provides
	WildernessPingerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WildernessPingerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		lastPinged = Instant.now();
		playerNames = config.getPlayerNames().split(",");
	}

	@Override
	protected void shutDown() throws Exception
	{
		lastPinged = null;
		playerNames = null;
	}
	private static final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile("\\b(\\d+)\\b");
	private Instant lastPinged;
	private final Duration waitDuration = Duration.ofMillis(2000);

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState().getState() == GameState.LOGGED_IN.getState())
		{
			loggedInTime = client.getTickCount();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		//wait a tick before grabbing friends list otherwise it returns empty
		if (loggedInTime != null && client.getTickCount() > loggedInTime + 1)
		{
			for (Friend friend : client.getFriendContainer().getMembers())
			{
				String playerName = Text.sanitize(friend.getName());
				storedFriends.add(playerName);
			}
			loggedInTime = null;
		}
	}

	public void checkPlayer(Player player)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}
		if (player == client.getLocalPlayer())
		{
			return;
		}
		// In wilderness
		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		if (wildernessLevelWidget == null)
		{
			//System.out.println("level widget is null." + player.getName());
			return;
		}
		boolean inWilderness = client.getVarbitValue(Varbits.IN_WILDERNESS) == 1;
		if (!inWilderness)
		{
			//System.out.println("not in wilderness." + player.getName());
			return;
		}

		String playerName = player.getName();
		final String wildernessLevelText = Objects.requireNonNull(client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL)).getText();
		final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
		if (!m.find())
		{
			//System.out.println("wildy level widget missing." + player.getName());
			return;
		}
		if (WorldType.isPvpWorld(client.getWorldType()))
		{
			//System.out.println("pvp world." + player.getName());
			return;
		}
		// We store the friends list and compare to that since the friends list isn't available on the first tick of hopping
		if (config.ignoreFriends() && isInStoredFriends(player))
		{
			//System.out.println("is a friend." + player.getName());
			return;
		}
		if (isPlayerInExceptionList(player))
		{
			//System.out.println("is in exception list." + player.getName());
			return;
		}
		final int wildernessLevel = Integer.parseInt(m.group(0));
		final int combatLevel = client.getLocalPlayer().getCombatLevel();
		int lower = Math.max(MIN_COMBAT_LEVEL, combatLevel - wildernessLevel);
		int upper = Math.min(Experience.MAX_COMBAT_LEVEL, combatLevel + wildernessLevel);

		// player spawned can attack us
		if (player.getCombatLevel() >= lower && player.getCombatLevel() <= upper || config.ignoreLevels())
		{
			sendChatMessage(player.getName() + " " + player.getCombatLevel());
			if (lastPinged != null && Instant.now().compareTo(lastPinged.plus(waitDuration)) >=0)
			{
				client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DONG, SoundEffectVolume.HIGH);
				client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DONG, SoundEffectVolume.HIGH);
				notifier.notify("PKER!!!!!");

				lastPinged = Instant.now();
			}
		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event) {
		clientThread.invokeAtTickEnd(() -> checkPlayer(event.getPlayer()));
	}

	private boolean isPlayerInExceptionList(Player player)
	{
		String playerName = player.getName().toLowerCase();
		List<String> lowercasePlayerNames = Arrays.stream(playerNames)
				.map(String::toLowerCase)
				.collect(Collectors.toList());
		return lowercasePlayerNames.contains(playerName);
	}

	private boolean isInStoredFriends(Player player)
	{
		if (storedFriends.isEmpty())
		{
			//this shouldn't print, leaving here for weird edge case debug
			System.out.println("stored friends empty");
			return false;
		}
		String playerName = player.getName();
		return storedFriends.contains(playerName);
	}

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equals("playerNames")) {
			playerNames = config.getPlayerNames().split(",");
		}
	}
}
