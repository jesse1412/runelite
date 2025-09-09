package net.runelite.client.plugins.dukesucellus;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("dukesuccc")
public interface DukeSucellusConfig extends Config {

    int VOLUME_MAX = SoundEffectVolume.HIGH;
    @ConfigItem(
            position = 1,
            keyName = "enableMetronome",
            name = "Enable Metronome",
            description = "Plays tick sound when using shroom on duke"
    )
    default boolean enableMetronome(){return true;}

    @ConfigItem(
            position = 2,
            keyName = "enrageHighlight",
            name = "Enrage Highlight",
            description = ""
    )
    default boolean enrageHighlight(){return true;}

    @ConfigItem(
            position = 3,
            keyName = "cameraThing",
            name = "Camera Thing",
            description = ""
    )
    default boolean cameraThing(){return true;}

    @Range(
            min = 1,
            max = 127
    )
    @ConfigItem(
            position = 52,
            keyName = "counterSize",
            name = "Attack Counter Size",
            description = ""
    )
    default int counterSize()
    {
        return 16;
    }

    @Range(
            min = 1,
            max = 3000
    )
    @ConfigItem(
            position = 53,
            keyName = "counterHeight",
            name = "Attack Counter Height",
            description = ""
    )
    default int counterHeight()
    {
        return 90;
    }

    @Alpha
    @ConfigItem(
            position = 54,
            keyName = "counterTextColor",
            name = "Counter Text Color",
            description = ""
    )
    default Color counterTextColor() {return Color.GREEN;}

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

    @Range(
            max = VOLUME_MAX
    )
    @ConfigItem(
            position = 51,
            keyName = "dingVolume",
            name = "Ding Volume",
            description = "Configures the volume of the ding sound. A value of 0 will disable tick sounds."
    )
    default int dingVolume()
    {
        return SoundEffectVolume.MEDIUM_HIGH;
    }
}