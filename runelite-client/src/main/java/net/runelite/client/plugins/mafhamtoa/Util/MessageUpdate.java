package net.runelite.client.plugins.mafhamtoa.Util;

import lombok.*;
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
	Integer scarabID;
}