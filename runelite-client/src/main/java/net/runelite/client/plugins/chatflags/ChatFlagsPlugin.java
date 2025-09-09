package net.runelite.client.plugins.chatflags;

import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.EnumID;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Chat Flags",
        description = "Chat Flags",
        tags = {"Chat", "Flags"}
)
public class ChatFlagsPlugin extends Plugin
{
    private static final Pattern WORLD_PATTERN = Pattern.compile("(?<!\\d)\\d{3}(?!\\d)");
    private static final int LOCATION_US_WEST = -73;
    private static final int LOCATION_US_EAST = -42;
    private int[] iconIds;

    @Inject
    private Client client;
    @Inject
    private WorldService worldService;
    @Inject
    private ChatIconManager chatIconManager;

    @Override
    protected void startUp()
    {
        loadIcons();
    }

    @Override
    protected void shutDown()
    {
    }

    private void loadIcons()
    {
        BufferedImage flagAUS = ImageUtil.loadImageResource(WorldHopperPlugin.class, "flag_aus.png");
        BufferedImage flagUK = ImageUtil.loadImageResource(WorldHopperPlugin.class, "flag_uk.png");
        BufferedImage flagUSEast = ImageUtil.loadImageResource(WorldHopperPlugin.class, "flag_us_east.png");
        BufferedImage flagUSWest = ImageUtil.loadImageResource(WorldHopperPlugin.class, "flag_us_west.png");
        BufferedImage flagGER = ImageUtil.loadImageResource(WorldHopperPlugin.class, "flag_ger.png");

        iconIds = new int[5];
        iconIds[0] = chatIconManager.registerChatIcon(flagGER);
        iconIds[1] = chatIconManager.registerChatIcon(flagAUS);
        iconIds[2] = chatIconManager.registerChatIcon(flagUK);
        iconIds[3] = chatIconManager.registerChatIcon(flagUSEast);
        iconIds[4] = chatIconManager.registerChatIcon(flagUSWest);
    }

    private String appendIcon(String message, int iconId)
    {
        String iconTag = "<img=" + chatIconManager.chatIconIndex(iconId) + ">";
        return message.replaceAll("(?<!\\d)(\\d{3})(?!\\d)", "$1" + Matcher.quoteReplacement(iconTag));
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        MessageNode messageNode = event.getMessageNode();
        if (messageNode.getType() != ChatMessageType.FRIENDSCHAT)
        {
            return;
        }

        String message = messageNode.getValue();
        Matcher matcher = WORLD_PATTERN.matcher(message);

        if (!matcher.find())
        {
            return;
        }

        int worldNumber = Integer.parseInt(matcher.group(0));
        WorldResult worldResult = worldService.getWorlds();

        if (worldResult == null)
        {
            return;
        }

        net.runelite.http.api.worlds.World world = worldResult.findWorld(worldNumber);
        if (world == null)
        {
            return;
        }

        String updatedMessage = null;
        WorldRegion region = world.getRegion();

        switch (region)
        {
            case UNITED_STATES_OF_AMERICA:
                int coast = client.getEnum(EnumID.WORLD_LOCATIONS).getIntValue(worldNumber);
                if (coast == LOCATION_US_WEST)
                {
                    updatedMessage = appendIcon(message, iconIds[4]);
                }
                else //us east
                {
                    updatedMessage = appendIcon(message, iconIds[3]);
                }
                break;
            case UNITED_KINGDOM:
                updatedMessage = appendIcon(message, iconIds[2]);
                break;
            case GERMANY:
                updatedMessage = appendIcon(message, iconIds[0]);
                break;
            case AUSTRALIA:
                updatedMessage = appendIcon(message, iconIds[1]);
                break;
            default:
                break;
        }


        if (updatedMessage != null)
        {
            event.getMessageNode().setValue(updatedMessage);
        }
    }
}
