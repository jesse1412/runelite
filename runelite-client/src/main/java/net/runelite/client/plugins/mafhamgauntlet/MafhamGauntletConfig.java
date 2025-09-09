package net.runelite.client.plugins.mafhamgauntlet;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("mafhamgauntlet")

public interface MafhamGauntletConfig extends Config
{
	@ConfigItem(
			keyName = "ShowPrayers",
			name = "Show Prayers",
			description = "Configures whether prayers should be shown.",
			position = 0
	)
	default boolean ShowPrayers()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAttackCounter",
			name = "Show Attack Counter",
			description = "Shows how many attacks until Hunllef switches prayer",
			position = 0
	)
	default boolean showAttackCounter()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showTornadoTimer",
			name = "Show Tornado Timer",
			description = "Shows how many ticks until tornadoes disappear",
			position = 0
	)
	default boolean showTornadoTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showTornadoTimer2",
			name = "Show Tornado Timer True Tile",
			description = "Shows the number over the true tile rather than the graphic",
			position = 0
	)
	default boolean showTornadoTimerTrue()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showLines",
			name = "Show Demiboss Room Lines",
			description = "Displays lines on the scene where the demi boss rooms are",
			position = 1
	)
	default boolean showLines()
	{
		return true;
	}

	@ConfigItem(
			keyName = "playSounds",
			name = "Play Attack Switch Sounds",
			description = "Plays sounds when Hunllef switches attack style",
			position = 2
	)
	default boolean playSounds()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showPrayerBox",
			name = "Show Prayer Widget",
			description = "Shows a widget telling you what to pray",
			position = 3
	)
	default boolean showWidget()
	{
		return true;
	}

	@Range(max=100)
	@ConfigItem(
			keyName = "masterVolume",
			name = "Master Volume",
			description = "Sets the master volume of Hunllef attack style swap sound",
			position = 4
	)
	default int masterVolume()
	{
		return 50;
	}

	@Range(min = 0, max = 50)
	@ConfigItem(
			position = 5,
			keyName = "lineWidth",
			name = "Line Width",
			description = "Sets the width of the room lines"
	)
	default int lineWidth() {return 1;}

	@Alpha
	@ConfigItem(
			position = 6,
			keyName = "lineColor",
			name = "Line Color",
			description = "Sets the color of the room lines"
	)
	default Color lineColor() {return new Color(0,255,255,155);}

	@Range(
			min = 40,
			max = 100
	)
	@ConfigItem(
			position = 7,
			keyName = "prayBoxSize",
			name = "Prayer Box Size",
			description = "Change the Size of the prayer Infobox."
	)
	@Units(Units.PIXELS)
	default int prayBoxSize()
	{
		return 40;
	}
}
