package net.runelite.client.plugins.mafhamaraxxor;

public class Tuple {
    private final int attackTick;
    private final int attackLength;

    public Tuple(int attackTick, int attackLength) {
        this.attackTick = attackTick;
        this.attackLength = attackLength;
    }

    public int getAttackTick() {
        return attackTick;
    }

    public int getAttackLength() {
        return attackLength;
    }
}
