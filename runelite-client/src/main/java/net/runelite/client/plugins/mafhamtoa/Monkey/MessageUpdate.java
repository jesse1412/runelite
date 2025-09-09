package net.runelite.client.plugins.mafhamtoa.Monkey;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.party.messages.PartyMemberMessage;


@Value
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageUpdate extends PartyMemberMessage
{
    String issue;
    WorldPoint pillar;
    WorldPoint vent;
    String playerName;
}