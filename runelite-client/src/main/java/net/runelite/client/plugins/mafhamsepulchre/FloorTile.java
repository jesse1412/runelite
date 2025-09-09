package net.runelite.client.plugins.mafhamsepulchre;

import lombok.AllArgsConstructor;
import net.runelite.api.GraphicsObject;
import net.runelite.api.coords.LocalPoint;

@AllArgsConstructor
public class FloorTile {

    enum Type {
        BLUE,
        YELLOW
    }

    public LocalPoint localPoint;
    public int spawnTick;
    public Type type;
}