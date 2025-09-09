package net.runelite.client.plugins.mafhamtoa.Monkey;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.mafhamtoa.Util.MessageUpdate;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class PathOfApmeken {
	@Inject
	private Client client;
	@Inject
	private PathOfApmekenOverlay overlay;
	@Inject
	private PathofApmekenOverlayPanel overlayPanel;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private PartyService party;
	private final int[] memberVarbits = {Varbits.TOA_MEMBER_0_HEALTH, Varbits.TOA_MEMBER_1_HEALTH, Varbits.TOA_MEMBER_2_HEALTH, Varbits.TOA_MEMBER_3_HEALTH,
			Varbits.TOA_MEMBER_4_HEALTH, Varbits.TOA_MEMBER_5_HEALTH, Varbits.TOA_MEMBER_6_HEALTH, Varbits.TOA_MEMBER_7_HEALTH};
	private final int PILLAR_ID = 45494;
	private final int VENT_ID = 45499;
	private int corruptionCounter = 0;
	@Getter
	private String currentSight = "None";
	@Getter
	private String currentIssue = "None";
	private int members;
	@Getter
	private List<GameObject> pillars = new ArrayList<>();
	@Getter
	private List<GameObject> highlightedPillars = new ArrayList<>();
	private List<GroundObject> vents = new ArrayList<>();
	@Getter
	private List<GroundObject> highlightedVents = new ArrayList<>();
	@Getter
	private List<Player> corruptedPlayers = new ArrayList<>();
	@Getter
	private Player doctor;

	public void startUp()
	{
		overlayManager.add(overlayPanel);
		overlayManager.add(overlay);
	}

	public void shutDown()
	{
		overlayManager.remove(overlayPanel);
		overlayManager.remove(overlay);
		reset();
	}

	private void reset()
	{
		vents.clear();
		pillars.clear();
		highlightedVents.clear();
		highlightedPillars.clear();
		corruptedPlayers.clear();
		members = 0;
		corruptionCounter = 0;
		currentSight = "None";
		currentIssue = "None";
		doctor = null;
		//System.out.println("Resetting...");
	}

	@Subscribe
	public void onMessageUpdate(MessageUpdate message) {
		//Pillar
		if (message.getPillar() != null && !highlightedPillars.isEmpty())
		{
			for (GameObject pillar : pillars)
			{
				if (message.getPillar().equals(pillar.getWorldLocation()))
				{
					//System.out.println("Pillar fixed, removing...");
					highlightedPillars.remove(pillar);
					return;
				}
			}
		}
		//Vent
		if (message.getVent() != null && !highlightedVents.isEmpty())
		{
			for (GroundObject vent : vents)
			{
				if (message.getVent().equals(vent.getWorldLocation()))
				{
					//System.out.println("Vent fixed, removing...");
					highlightedVents.remove(vent);
					return;
				}
			}
		}
		//Corruption
		if (message.getPlayerName() != null && !corruptedPlayers.isEmpty())
		{
			String playerNameFixed = message.getPlayerName().replaceAll("\\p{Zs}", " ");
			for (Player player : corruptedPlayers)
			{
				if (Objects.equals(playerNameFixed, player.getName()))
				{
					//System.out.println("Player cured, removing player: " + message.getPlayerName());
					corruptedPlayers.remove(player);
					if (corruptedPlayers.isEmpty())
					{
						doctor = null;
					}
					return;
				}
			}
		}
		if (currentSight.equals(client.getLocalPlayer().getName())) {
			return;
		}
		//Issue
		if (message.getIssue() != null) {
			currentIssue = message.getIssue();
			switch (message.getIssue()) {
				case "Pillars":
					for (GameObject pillar : pillars) {
						highlightedPillars.add(pillar);
					}
					//System.out.println("Issue received, hling all pillars");
					break;
				case "Vents":
					for (GroundObject vent : vents) {
						highlightedVents.add(vent);
					}
					//System.out.println("Issue received, hling all vents");
					break;
				case "Corruption":
					for (Player player : client.getPlayers())
					{
						String currentSightFixed = currentSight.replaceAll("\\p{Zs}", " ");
						if (!currentSightFixed.equals(player.getName()))
						{
							corruptedPlayers.add(player);
						}
						if (currentSightFixed.equals(player.getName()))
						{
							doctor = player;
						}
					}
					//System.out.println("Issue received, hling all corrupted players");
					break;
				case "None":
					// Do Nothing?
					break;
				default:
					// What happened at this point honestly
					break;
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		String message = chatMessage.getMessage();
		GameMessage match = GameMessage.compareString(message);

		if (message == null || match == null) {
			return;
		}

		switch (match) {
			case GAINED_SIGHT:
				currentSight = client.getLocalPlayer().getName();
				break;
			case LOST_SIGHT:
				currentSight = "None";
				break;
			case TEAMMATE_SIGHT:
				currentSight = getSight(message);
				break;
			case PILLARS:
				currentIssue = "Pillars";
				sendIssue();
				highlightedPillars.addAll(pillars);
				//System.out.println("Issue sent, hling all pillars");
				break;
			case VENTS:
				currentIssue = "Vents";
				sendIssue();
				highlightedVents.addAll(vents);
				//System.out.println("Issue sent, hling all vents");
				break;
			case CORRUPTION:
				currentIssue = "Corruption";
				sendIssue();
				doctor = client.getLocalPlayer();
				for (Player player : client.getPlayers()) {
					if (player != client.getLocalPlayer()) {
						//System.out.println("Issue sent, hling players");
						corruptedPlayers.add(player);
					}
				}
				break;
			case CORRUPTION_CURED: // Count for players in raid
				Pattern regexPattern = Pattern.compile("<col=[A-Fa-f\\d]+>You neutralise (.*?)'s corruption.");
				Matcher matcher = regexPattern.matcher(message);
				if (matcher.find()) {
					String extractedString = matcher.group(1);
					sendCuredPlayer(extractedString);
				}
				corruptionCounter++;
				break;
			case CORRUPTION_COMPLETE:
			case CORRUPTION_FAILED:
				corruptedPlayers.clear();
				currentIssue = "None";
				corruptionCounter = 0;
				doctor = null;
				sendIssue();
				//System.out.println("Corruption complete/failed!");
				break;
			case PILLAR_SOLVED:
				sendPillar();
				highlightedPillars.clear();
				break;
			case PILLARS_FAILED:
			case PILLARS_ALL_SOLVED:
			case PILLARS_ALL_SOLVED_GROUP:
				currentIssue = "None";
				highlightedPillars.clear();
				sendIssue();
				//System.out.println("Pillars all complete/failed!");
				break;
			case VENT_SOLVED:
				sendVent();
				highlightedVents.clear();
				break;
			case VENTS_FAILED:
			case VENTS_ALL_SOLVED:
			case VENTS_ALL_SOLVED_GROUP:
				currentIssue = "None";
				highlightedVents.clear();
				sendIssue();
				//System.out.println("Vents all complete/failed!");
				break;
			case ROOM_COMPLETE:
			case DIED_FINAL:
			case DIED_TRY_AGAIN:
			case LEFT_RAID:
				reset();
				break;
		}

		if (match == GameMessage.CORRUPTION_CURED && corruptionCounter == findGroupSize(members)) {
			currentIssue = "None";
			corruptionCounter = 0;
		}
				//System.out.println(
				//"Current Sight Belongs To: " + currentSight + "\n" +
				//"Current Issue Is: " + currentIssue + "\n");

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (event.getGameObject().getId() == PILLAR_ID)
		{
			pillars.add(event.getGameObject());
		}
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		if (event.getGroundObject().getId() == VENT_ID)
		{
			vents.add(event.getGroundObject());
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject().getId() == PILLAR_ID)
		{
			pillars.remove(event.getGameObject());
		}
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		if (event.getGroundObject().getId() == VENT_ID)
		{
			vents.remove(event.getGroundObject());
		}
	}
	private String getSight(String input) {
		String regex = "(?:<[^>]+>)?([^<]+) has been granted Apmeken's Sight.";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		if (matcher.find() && matcher.groupCount() > 0) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	private int findGroupSize(int members) {
		for (int varbit : memberVarbits) {
			members += Math.min(client.getVarbitValue(varbit), 1);
		}

		return members;
	}

	private WorldPoint findRepairedPillar()
	{
		for (GameObject pillar : pillars)
		{
			if (client.getLocalPlayer().getWorldLocation().distanceTo(pillar.getWorldLocation()) < 4)
			{
				return pillar.getWorldLocation();
			}
		}
		return null;
	}

	private WorldPoint findFixedVent()
	{
		for (GroundObject vent : vents)
		{
			if (client.getLocalPlayer().getWorldLocation().distanceTo(vent.getWorldLocation()) < 1)
			{
				return vent.getWorldLocation();
			}
		}
		return null;
	}

	private void sendIssue()
	{
		if (party.isInParty())
		{
			party.send(new MessageUpdate(currentIssue, null, null, null, null));
		}
	}

	private void sendPillar()
	{
		if (party.isInParty())
		{
			party.send(new MessageUpdate(null, findRepairedPillar(), null, null, null));
		}
	}

	private void sendVent()
	{
		if (party.isInParty())
		{
			party.send (new MessageUpdate(null, null, findFixedVent(), null, null));
		}
	}
	private void sendCuredPlayer(String playerName)
	{
		if (party.isInParty())
		{
			party.send (new MessageUpdate(null,null,null, playerName, null));
		}
	}
}
