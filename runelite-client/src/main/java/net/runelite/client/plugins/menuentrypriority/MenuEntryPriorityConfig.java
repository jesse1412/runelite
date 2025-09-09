package net.runelite.client.plugins.menuentrypriority;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("menuentrypriority")
public interface MenuEntryPriorityConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "filterByList",
            name = "Filter by List",
            description = "Only swap entries for NPCs included in the list (use * as a wildcard, e.g. '*demon' for all demons). Turn this off to swap for all NPCs no matter what"
    )
    default boolean filterByList() {return true;}

    @ConfigItem(
            position = 1,
            keyName = "filterList",
            name = "NPCs",
            description = "List of NPCs to deprio walk here when dead"
    )
    default String filterList()
    {
        return "";
    }

    @ConfigItem(
            position = 2,
            keyName = "deprioObelisk",
            name = "Deprio Obelisk",
            description = "Obelisk is jank so I've added this, makes left click examine for the obelisk at wardens"
    )
    default boolean deprioObelisk() {return true;}
}