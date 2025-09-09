package net.runelite.client.plugins.mafhamtob.Verzik;

import java.awt.*;

public class WarningInfo {
    private String text;
    private Color color;

    public WarningInfo(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
