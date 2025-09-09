package net.runelite.client.plugins.mafhamtoa.Monkey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum GameMessage {
    GAINED_SIGHT("You have been granted Apmeken's Sight."),
    LOST_SIGHT("You no longer have Apmeken's Sight."),
    TEAMMATE_SIGHT("PLAYER has been granted Apmeken's Sight."),
    PILLARS("You sense an issue with the roof supports."),
    PILLAR_SOLVED("You repair the damaged roof support."),
    PILLARS_FAILED("Damaged roof supports cause some debris to fall on you!"),
    PILLARS_ALL_SOLVED("Apmeken's Sight guides you into repairing the roof supports."),
    PILLARS_ALL_SOLVED_GROUP("Apmeken's Sight guides your group into repairing the roof supports."),
    VENTS("You sense some strange fumes coming from holes in the floor."),
    VENT_SOLVED("You neutralise the fumes coming from the hole."),
    VENTS_FAILED("The fumes filling the room suddenly ignite!"),
    VENTS_ALL_SOLVED("Apmeken's Sight guides you into neutralising some dangerous fumes."),
    VENTS_ALL_SOLVED_GROUP("Apmeken's Sight guides your group into neutralising some dangerous fumes."),
    CORRUPTION("You sense Amascut's corruption beginning to take hold."),
    CORRUPTION_CURED("You neutralise PLAYER's corruption."),
    CORRUPTION_COMPLETE("Apmeken's Sight guides your group into overcoming Amascut's corruption."),
    CORRUPTION_FAILED("Your group is overwhelmed by Amascut's corruption!"),
    ROOM_COMPLETE("Challenge complete: Path of Apmeken. Duration: TIME"),
    DIED_TRY_AGAIN("Your party failed to complete the challenge. You may try again..."),
    DIED_FINAL("You failed to survive the Tombs of Amascut."),
    LEFT_RAID("You abandon the raid and leave the Tombs of Amascut.")

    ;

    private final String value;

    GameMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static GameMessage compareString(String input) {
        for (GameMessage gameMessage : GameMessage.values()) {
            String messageText = gameMessage.getValue();

            if (messageText.contains("PLAYER")) {
                String regex = messageText.replace("PLAYER", ".*");

                Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(input);

                if (matcher.find()) {
                    return gameMessage;
                }
            }
            else if (messageText.contains("TIME")) {
                String regex = messageText.replace("TIME", ".*");

                Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    return gameMessage;
                }
            }
            else if (input.contains(gameMessage.getValue())) {
                return gameMessage;
            }
        }
        return null;
    }
}
