package net.runelite.client.plugins.mafhamlms;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("mafhamlms")
public interface MafhamLMSConfig extends Config {

    int VOLUME_MAX = SoundEffectVolume.HIGH;

    @ConfigItem(
            position = 0,
            keyName = "hideAndOutline",
            name = "Hide and Outline Player Model",
            description = "Replaces your player model with an outline for easier DDing while in LMS"
    )
    default boolean hideAndOutline(){return true;}

    @ConfigItem(
            position = 1,
            keyName = "enableMetronome",
            name = "Enable Metronome",
            description = "Plays a metronome while in LMS"
    )
    default boolean enableMetronome(){return true;}

    @ConfigItem(
            position = 2,
            keyName = "highlightSpells",
            name = "Highlight Spells",
            description = "Adds screen markers for your spells while in LMS"
    )
    default boolean highlightSpells(){return true;}

    @ConfigItem(
            position = 3,
            keyName = "dontClickThis",
            name = "Don't Click This",
            description = "This does naughty naughty things with spells"
    )
    default boolean dontClickThis(){return false;}

    @ConfigItem(
            position = 4,
            keyName = "hideBarrage",
            name = "Hide Barrage Graphic",
            description = "Barrage graphic hides opponent's switches so this hides that"
    )
    default boolean hideBarrage(){return true;}

    @Range(
            max = VOLUME_MAX
    )
    @ConfigItem(
            position = 50,
            keyName = "tickVolume",
            name = "Metronome Volume",
            description = "Configures the volume of the tick sound. A value of 0 will disable tick sounds."
    )
    default int tickVolume()
    {
        return SoundEffectVolume.MEDIUM_HIGH;
    }
}