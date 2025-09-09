package net.runelite.client.plugins.menuentrypriority;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@PluginDescriptor(
        name = "Menu Entry Priority",
        description = "Hides walk here for dying NPCs",
        tags = {"menu, entry, priority, dead, dying, npc"}
)
public class MenuEntryPriorityPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private MenuEntryPriorityConfig config;
    private List<String> filterNames;

    @Provides
    MenuEntryPriorityConfig getConfig(ConfigManager configManager){return configManager.getConfig(MenuEntryPriorityConfig.class);}

    @Override
    protected void startUp()
    {
        filterNames = Text.fromCSV(config.filterList());
    }

    @Override
    protected void shutDown()
    {
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        if (shiftHeld())
        {
            return;
        }
        boolean containsDyingNPC = false;
        for (MenuEntry menuEntry : client.getMenuEntries())
        {
            if (menuEntry.getNpc() != null)
            {
                NPC npc = menuEntry.getNpc();
                String npcName = npc.getName();
                if (npcName != null) {
                    //colour tag remover
                    String npcNameFixed = npcName.replaceAll("<[^>]*>", "");
                    if (
                            isDyingBetterVersion(npc) &&
                                    (highlightMatchesNPCName(npcNameFixed, filterNames) || !config.filterByList())
                    ) {
                        containsDyingNPC = true;
                    }
                    if (config.deprioObelisk() &&
                            (npc.getId() == 11750 || npc.getId() == 11752)
                    )
                    {
                        containsDyingNPC = true;
                    }
                }

            }
        }

        if (containsDyingNPC) {
            Arrays.stream(client.getMenuEntries())
                    .filter(menuEntry -> menuEntry.getOption().contains("Walk here"))
                    .forEach(menuEntry -> menuEntry.setDeprioritized(true));
        }

    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("filterList")) {
            filterNames = Text.fromCSV(config.filterList());
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

    private boolean isDyingBetterVersion(NPC npc)
    {
        final NPCComposition npcComposition = npc.getTransformedComposition();
        if (npcComposition == null)
        {
            return false;
        }
        boolean canHaveAttack = ArrayUtils.contains(npcComposition.getActions(), "Attack");
        boolean hasAttack = Arrays.stream(client.getMenuEntries())
                .anyMatch(menuEntry -> menuEntry.getOption().contains("Attack"));
        if (!hasAttack && canHaveAttack)
        {
            return true;
        }
        return false;
    }

    private boolean shiftHeld()
    {
        return client.isKeyPressed(KeyCode.KC_SHIFT);
    }
}