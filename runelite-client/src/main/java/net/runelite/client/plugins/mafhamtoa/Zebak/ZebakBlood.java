package net.runelite.client.plugins.mafhamtoa.Zebak;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class ZebakBlood {

    @Inject
    private Client client;
    @Inject
    private MafhamToAConfig config;
    @Inject
    private ZebakBloodOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Getter
    private Integer bloodTick;
    @Provides
    MafhamToAConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(MafhamToAConfig.class);
    }

    public void startUp() {
        overlayManager.add(overlay);
    }

    public void shutDown() throws Exception {
        overlayManager.remove(overlay);
        bloodTick = null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        String message = chatMessage.getMessage();
        String challengeComplete = "Challenge complete: Zebak.";
        String diedTryAgain = "Your party failed to complete the challenge. You may try again...";
        String diedFinal = "You failed to survive the Tombs of Amascut.";
        String leftRaid = "You abandon the raid and leave the Tombs of Amascut.";
        if (checkStrings(message, challengeComplete, diedTryAgain, diedFinal, leftRaid))
        {
            bloodTick = null;
        }

    }
    public static boolean checkStrings(String string1, String... stringsToCheck) {
        for (String str : stringsToCheck) {
            if (string1.contains(str)) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        GraphicsObject graphicsObject = event.getGraphicsObject();
        if (graphicsObject.getId() == 377)
        {
            bloodTick = 3;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (bloodTick != null)
        {
            if (bloodTick > 0)
            {
                bloodTick--;
            }
            else bloodTick = null;
        }
    }

}