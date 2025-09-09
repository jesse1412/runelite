package net.runelite.client.plugins.menuentryrecolor;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.MenuEntryAdded;
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

@PluginDescriptor(
        name = "Menu Entry Recolor",
        description = "Recolors menu entries of specified npcs",
        tags = {"Menu", "Entry", "Recolor", "NPC"}
)
public class MenuEntryRecolorPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private MenuEntryRecolorConfig config;

    private List<String> highlights1;
    private List<String> highlights2;
    private List<String> highlights3;
    private List<String> highlights4;
    private List<String> highlights5;

    @Provides
    MenuEntryRecolorConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MenuEntryRecolorConfig.class);
    }

    @Override
    protected void startUp() {
        highlights1 = Text.fromCSV(config.getNpcs1());
        highlights2 = Text.fromCSV(config.getNpcs2());
        highlights3 = Text.fromCSV(config.getNpcs3());
        highlights4 = Text.fromCSV(config.getNpcs4());
        highlights5 = Text.fromCSV(config.getNpcs5());
    }

    @Override
    protected void shutDown() {
    }
    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getKey().equals("group1Npcs"))
        {
            highlights1 = Text.fromCSV(config.getNpcs1());
        }
        if (event.getKey().equals("group2Npcs"))
        {
            highlights2 = Text.fromCSV(config.getNpcs2());
        }
        if (event.getKey().equals("group3Npcs"))
        {
            highlights3 = Text.fromCSV(config.getNpcs3());
        }
        if (event.getKey().equals("group4Npcs"))
        {
            highlights4 = Text.fromCSV(config.getNpcs4());
        }
        if (event.getKey().equals("group5Npcs"))
        {
            highlights5 = Text.fromCSV(config.getNpcs5());
        }
    }


    @Subscribe(priority = -2)
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        final MenuEntry menuEntry = event.getMenuEntry();
        final NPC npc = menuEntry.getNpc();
        if (npc != null)
        {
            String npcName = npc.getName();
            if (highlightMatchesNPCName(npcName, highlights1))
            {
                Color color = config.getGroup1Color();
                String string = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(string);
            }
            if (highlightMatchesNPCName(npcName, highlights2))
            {
                Color color = config.getGroup2Color();
                String string = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(string);
            }
            if (highlightMatchesNPCName(npcName, highlights3))
            {
                Color color = config.getGroup3Color();
                String string = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(string);
            }
            if (highlightMatchesNPCName(npcName, highlights4))
            {
                Color color = config.getGroup4Color();
                String string = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(string);
            }
            if (highlightMatchesNPCName(npcName, highlights5))
            {
                Color color = config.getGroup5Color();
                String string = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(string);
            }
        }
    }

    private boolean highlightMatchesNPCName(String npcName, List<String> filterNames)
    {
        for (String filterName : filterNames)
        {
            if (WildcardMatcher.matches(filterName, npcName))
            {
                return true;
            }
        }

        return false;
    }
}