package net.runelite.client.plugins.dukesucellus;

import net.runelite.api.coords.LocalPoint;

public class TileHighlight {
    private final String identifier;
    private final int spawnTick;
    private final int hurtTick;
    private final int despawnTick;
    private final LocalPoint location;
    private final int size;

    public TileHighlight(String identifier, int spawnTick, int hurtTick, int despawnTick, LocalPoint location, int size) {
        this.identifier = identifier;
        this.spawnTick = spawnTick;
        this.hurtTick = hurtTick;
        this.despawnTick = despawnTick;

        this.location = location;
        this.size = size;
    }

    public String getIdentifier() {
        return identifier;
    }
    public int getSpawnTick() {
        return spawnTick;
    }
    public int getHurtTick() {
        return hurtTick;
    }
    public int getDespawnTick() {
        return despawnTick;
    }
    public int getSize() {
        return size;
    }
    public LocalPoint getLocation() {
        return location;
    }
}
