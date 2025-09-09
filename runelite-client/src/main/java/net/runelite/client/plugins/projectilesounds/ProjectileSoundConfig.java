package net.runelite.client.plugins.projectilesounds;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("projectilesoundplugin")
public interface ProjectileSoundConfig extends Config {

    @ConfigItem(
            keyName = "projectileRIds",
            name = "Range IDs",
            description = "A comma-separated list of projectile IDs to detect",
            position = 0
    )
    default String projectileRIds() {
        return "";
    }

    @ConfigItem(
            keyName = "projectileMIds",
            name = "Mage IDs",
            description = "A comma-separated list of projectile IDs to detect",
            position = 1
    )
    default String projectileMIds() {
        return "";
    }
    @Range(max=100)
    @ConfigItem(
            keyName = "masterVolume",
            name = "Master Volume",
            description = "Sets the master volume of all ground item sounds",
            position = 0
    )
    default int masterVolume()
    {
        return 50;
    }
}