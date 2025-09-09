package net.runelite.client.plugins.dukesucellus;

import lombok.Getter;
import lombok.Setter;

public class SpecialAttack {
    @Getter
    private final int SPEC_REGEN_TICKS = 50;
    @Getter
    @Setter
    private double specialPercentage;
    @Getter
    @Setter
    private int ticksSinceSpecRegen;
    @Getter
    @Setter
    private boolean wearingLightbearer;
}
