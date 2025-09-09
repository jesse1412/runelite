package net.runelite.client.plugins.mafhamgauntlet;

import java.awt.*;

public enum Tiles {

    //Formatting: R1 = room 1, R2 = room2 (the 3 demi-boss rooms from left to right)
    //N = north line, W = west line, etc.
    //Each line is composed of 2 localPoint coordinates

    R1N(-3008,7232, -960,7232),
    R1S(-3008, 5184, -960, 5184),
    R1W(-3008, 5184, -3008, 7232),
    R2N(-960, 7232, 1088, 7232),
    R2S(-960, 5184, 1088, 5184),
    R2W(-960,5184,-960,7232),
    R2E(1088, 5184, 1088, 7232),
    R3N(1088, 7232, 3136, 7232),
    R3S(1088, 5184, 3136, 5184),
    R3E(3136, 5184, 3136, 7232),

    ;

    private final int x;
    private final int y;
    private final int x2;
    private final int y2;

    Tiles(int x, int y, int x2, int y2)
    {
        this.y = y;
        this.x = x;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getX2(){return x2;}

    public int getY2(){return y2;}

}