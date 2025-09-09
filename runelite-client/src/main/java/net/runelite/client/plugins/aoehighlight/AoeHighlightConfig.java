/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.aoehighlight;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("aoehighlight")
public interface AoeHighlightConfig extends Config {

	@ConfigSection (
			name = "Bosses",
			description = "Configuration for bosses",
			position = 2,
			closedByDefault = true
	)
	String bosses = "bosses";

	@ConfigSection (
			name = "Chambers of Xeric",
			description = "Configuration for CoX bosses",
			position = 3,
			closedByDefault = true
	)
	String cox = "cox";

	@ConfigSection (
			name = "Theatre of Blood",
			description = "Configuration for ToB bosses",
			position = 4,
			closedByDefault = true
	)
	String tob = "tob";

	@ConfigSection (
			name = "Tombs of Amascut",
			description = "Configuration for ToA bosses",
			position = 5,
			closedByDefault = true
	)
	String toa = "toa";

	@ConfigSection (
			name = "Special Settings",
			description = "Extra custom settings",
			position = 6,
			closedByDefault = true
	)
	String specialsettings = "specialsettings";

	//---------------sections end--------------------------

	@ConfigItem (
			keyName = "enabled",
			name = "Enable Highlights",
			description = "Configures whether or not Highlights are drawn",
			position = 0
	)
	default boolean enabled() {
		return true;
	}

	@Alpha
	@ConfigItem (
			keyName = "highlightColour",
			name = "Highlight Colour",
			description = "Colour of the highlights",
			position = 0
	)
	default Color highlightColor() {
		return Color.RED;
	}

	@ConfigItem (
			keyName = "lizardmanaoe",
			name = "Lizardman Shamans",
			description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans is displayed",
			section = bosses
	)
	default boolean isShamansEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "archaeologistaoe",
			name = "Crazy Archaeologist",
			description = "Configures whether or not AoE Projectile Warnings for Archaeologist is displayed",
			section = bosses
	)
	default boolean isArchaeologistEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "demonicGorillas",
			name = "Demonic Gorillas",
			description = "Configures whether or not AoE Projectile Warnings for gorillaz is displayed",
			section = bosses
	)
	default boolean isDemonicGorillasEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "icedemon",
			name = "Ice Demon",
			description = "Configures whether or not AoE Projectile Warnings for Ice Demon is displayed",
			section = cox
	)
	default boolean isIceDemonEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "vasa",
			name = "Vasa",
			description = "Configures whether or not AoE Projectile Warnings for Vasa is displayed",
			section = cox
	)
	default boolean isVasaEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "tekton",
			name = "Tekton",
			description = "Configures whether or not AoE Projectile Warnings for Tekton is displayed",
			section = cox
	)
	default boolean isTektonEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "vorkath",
			name = "Vorkath",
			description = "Configures whether or not AoE Projectile Warnings for Vorkath are displayed",
			section = bosses
	)
	default boolean isVorkathEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "hydra",
			name = "Hydra",
			description = "Configures whether or not AoE Projectile Warnings for Hydra are displayed",
			section = bosses
	)
	default boolean isHydraEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "grotesque",
			name = "Grotesque Guardians",
			description = "Configures whether or not AoE Projectile Warnings for Grotesque Guardians are displayed",
			section = bosses
	)
	default boolean isGrotesqueEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "cerberus",
			name = "Cerberus",
			description = "Configures whether or not AoE Projectile Warnings for Cerberus are displayed",
			section = bosses
	)
	default boolean isCerberusEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "galvek",
			name = "Galvek",
			description = "Configures whether or not AoE Projectile Warnings for Galvek are displayed",
			section = bosses
	)
	default boolean isGalvekEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "vetion",
			name = "Vet'ion",
			description = "Configures whether or not AoE Projectile Warnings for Vet'ion are displayed",
			section = bosses
	)
	default boolean isVetionEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "chaosfanatic",
			name = "Chaos Fanatic",
			description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic are displayed",
			section = bosses
	)
	default boolean isChaosFanaticEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "olm",
			name = "Great Olm",
			description = "Configures whether or not AoE Projectile Warnings for The Great Olm are displayed",
			section = cox
	)
	default boolean isOlmEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "corp",
			name = "Corporeal Beast",
			description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast are displayed",
			section = bosses
	)
	default boolean isCorpEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "wintertodt",
			name = "Wintertodt Snow Fall",
			description = "Configures whether or not AOE Projectile Warnings for the Wintertodt snow fall are displayed",
			section = bosses
	)
	default boolean isWintertodtEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "vardorvis",
			name = "Vardorvis",
			description = "Configures whether or not AOE Projectile Warnings for the Vardorvis are displayed",
			section = bosses
	)
	default boolean isVardorvisEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "sire",
			name = "Sire",
			description = "Configures whether or not AOE Projectile Warnings for Sire are displayed",
			section = bosses
	)
	default boolean isSireEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "wardensp2",
			name = "Wardens P2",
			description = "Configures whether or not AoE Projectile Warnings for the Wardens P2 are displayed",
			position = 4,
			section = toa
	)
	default boolean isWardensP2Enabled() {
		return true;
	}

	@ConfigItem (
			keyName = "wardenslightning",
			name = "Wardens Lightning",
			description = "Configures whether or not AoE Projectile Warnings for the Wardens lightning are displayed",
			position = 5,
			section = toa
	)
	default boolean isWardensLightningEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "wardenstileflip",
			name = "Wardens Tile Flip",
			description = "Configures whether or not AoE Projectile Warnings for the Wardens tile flip are displayed",
			position = 6,
			section = toa
	)
	default boolean isWardensTileFlipEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "kephri",
			name = "Kephri",
			description = "Configures whether or not AoE Projectile Warnings for Kephri (3x3) are displayed",
			section = toa
	)
	default boolean isKephriEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "zebak",
			name = "Zebak",
			description = "Configures whether or not AoE Projectile Warnings for Zebak are displayed",
			section = toa
	)
	default boolean isZebakEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "baba",
			name = "Baba",
			description = "Configures whether or not AoE Projectile Warnings for Baba are displayed",
			section = toa
	)
	default boolean isBabaEnabled() {
		return true;
	}
	@ConfigItem (
			keyName = "babaRocks",
			name = "Baba Boulders",
			description = "These boulders appear at both Baba and Wardens final phase",
			section = toa
	)
	default boolean isBabaRocksEnabled() {
		return true;
	}

	@ConfigItem (
			keyName = "zulrah",
			name = "Zulrah",
			description = "AoE and ranged proj for tick eating",
			section = bosses
	)
	default boolean isZulrahEnabled() {
		return false;
	}

	@ConfigItem (
			keyName = "lightningSpecial",
			name = "Shorter Warden AoEs",
			description = "Makes the P3 Warden lightning and baba boulders 1 tick shorter to show you when it's safe to click rather than how long the aoe lasts",
			section = specialsettings
	)
	default boolean lightningSpecial() {
		return true;
	}

	@ConfigItem (
			keyName = "tileFlipSpecial",
			name = "Show Tile Flip Timings",
			description = "Makes the front half of the room disappear before the back half for Warden P3, to show you how each half of the room's timings are",
			section = specialsettings
	)
	default boolean tileFlipSpecial() {
		return true;
	}

	@ConfigItem (
			keyName = "maidenSplat",
			name = "Maiden Splats",
			description = "Splat blood",
			section = tob
	)
	default boolean maidenSplat() {
		return true;
	}

	@ConfigItem (
			keyName = "bloatFeet",
			name = "Bloat Feet",
			description = "feet uwu",
			section = tob
	)
	default boolean bloatFeet() {
		return true;
	}

	@ConfigItem (
			keyName = "verzikSplat",
			name = "Verzik P2 Bombs",
			description = "Verzik's green skull bomb thingies",
			section = tob
	)
	default boolean verzikSplat() {
		return true;
	}

	@ConfigItem (
			keyName = "muspahSpike",
			name = "Muspah Spikes",
			description = "Muspah Spikes",
			section = bosses
	)
	default boolean muspahSpikes() {
		return true;
	}

	@ConfigItem (
			keyName = "solHeredit",
			name = "Sol Heredit",
			description = "Sol Heredit",
			section = bosses
	)
	default boolean solHeredit() {
		return true;
	}

	@ConfigItem (
			keyName = "araxxor",
			name = "Araxxor",
			description = "Araxxor",
			section = bosses
	)
	default boolean araxxor() {
		return true;
	}

	@ConfigItem (
			keyName = "venenatis",
			name = "Venenatis",
			description = "Venenatis",
			section = bosses
	)
	default boolean venenatis() {
		return true;
	}

	@ConfigItem (
			keyName = "verzikWeb",
			name = "Verzik Web",
			description = "Verzik Web",
			section = tob
	)
	default boolean verzikWeb() {
		return true;
	}

	@ConfigItem (
			keyName = "wardenSkull",
			name = "Warden Skull",
			description = "Warden Skull",
			section = toa
	)
	default boolean wardenSkull() {
		return true;
	}

}