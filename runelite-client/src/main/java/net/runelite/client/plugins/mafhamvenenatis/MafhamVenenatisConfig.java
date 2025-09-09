package net.runelite.client.plugins.mafhamvenenatis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mafhamvenenatis")
public interface MafhamVenenatisConfig extends Config {

    @ConfigItem(
            keyName = "iconsPath",
            name = "Icons Folder Path",
            description = "Path to the folder where the icons for web and spiderling phase icons are"
    )
    default String iconsPath() {return "C:/Users/Mafham/IdeaProjects/runelite/runelite-client/src/main/java/net/runelite/client/plugins/mafhamvenenatis/icons/";}
}