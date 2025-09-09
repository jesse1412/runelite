package net.runelite.client.plugins.valuabledroprecolor;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Valuable Drop Recolor",
        description = "Recolors valuable drop chat messages",
        tags = {"valuable", "drop", "recolor", "item", "items", "color"}
)
public class ValuableDropRecolorPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private ValuableDropRecolorConfig config;

    @Provides
    ValuableDropRecolorConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ValuableDropRecolorConfig.class);
    }
    private List<String> highlightedItems;

    @Override
    protected void startUp() {
        highlightedItems = Text.fromCSV(config.getHighlightItems());
    }

    @Override
    protected void shutDown() {
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("highlightedItems")) {
            highlightedItems = Text.fromCSV(config.getHighlightItems());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        MessageNode messageNode = chatMessage.getMessageNode();
        if (messageNode.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        String valuableDropPatternString = "Valuable drop: ((?:\\d+ x )?(.*?)) \\((\\d{1,3}(?:,\\d{3})*|\\d+) coin(?:s?)\\)";
        Pattern valuableDropPattern = Pattern.compile(valuableDropPatternString);
        Matcher valuableDropMatcher = valuableDropPattern.matcher(messageNode.getValue());

        String untradeablePatternString = "Untradeable drop:\\s*(\\d+\\s*x\\s*)?([^<]+)";
        Pattern untradeablePattern = Pattern.compile(untradeablePatternString);
        Matcher untradeableMatcher = untradeablePattern.matcher(messageNode.getValue());

        Color color;
        Color defaultColor;
        Color highlightedColor;
        Color lowValueColor;
        Color mediumValueColor;
        Color highValueColor;
        Color insaneValueColor;
        Color untradeableColor;
        if (client.getVarbitValue(Varbits.TRANSPARENT_CHATBOX) == 0 || !client.isResized())
        {
            defaultColor = config.defaultColorOpaque();
            highlightedColor = config.highlightedColorOpaque();
            lowValueColor = config.lowValueColorOpaque();
            mediumValueColor = config.mediumValueColorOpaque();
            highValueColor = config.highValueColorOpaque();
            insaneValueColor = config.insaneValueColorOpaque();
            untradeableColor = config.untradeableDropOpaque();
        }
        else
        {
            defaultColor = config.defaultColorTransparent();
            highlightedColor = config.highlightedColorTransparent();
            lowValueColor = config.lowValueColorTransparent();
            mediumValueColor = config.mediumValueColorTransparent();
            highValueColor = config.highValueColorTransparent();
            insaneValueColor = config.insaneValueColorTransparent();
            untradeableColor = config.untradeableDropTransparent();
        }

        if (untradeableMatcher.find())
        {
            if (highlightMatchesItemName(untradeableMatcher.group(2)))
            {
                String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), highlightedColor);
                messageNode.setValue(string);
                return;
            }
            if (config.recolorHighlightedOnly())
            {
                String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), defaultColor);
                messageNode.setValue(string);
                return;
            }
            String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), untradeableColor);
            messageNode.setValue(string);
            return;
        }

        if (valuableDropMatcher.find())
        {
            if (highlightMatchesItemName(valuableDropMatcher.group(2)))
            {
                String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), highlightedColor);
                messageNode.setValue(string);
                return;
            }
            if (config.recolorHighlightedOnly())
            {
                String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), defaultColor);
                messageNode.setValue(string);
                return;
            }
            int coins = Integer.parseInt(valuableDropMatcher.group(3).replaceAll(",", ""));
            if (coins >= 0 && coins < config.lowValuePrice()) {
                color = defaultColor;
            } else if (coins >= config.lowValuePrice() && coins < config.mediumValuePrice()) {
                color = lowValueColor;
            } else if (coins >= config.mediumValuePrice() && coins < config.highValuePrice()) {
                color = mediumValueColor;
            } else if (coins >= config.highValuePrice() && coins < config.insaneValuePrice()) {
                color = highValueColor;
            } else if (coins >= config.insaneValuePrice()) {
                color = insaneValueColor;
            } else {
                color = defaultColor;
            }
            String string = ColorUtil.prependColorTag(Text.removeTags(messageNode.getValue()), color);
            messageNode.setValue(string);
        }
    }

    private boolean highlightMatchesItemName(String itemName)
    {
        for (String name : highlightedItems)
        {
            if (WildcardMatcher.matches(name, itemName))
            {
                return true;
            }
        }

        return false;
    }
}