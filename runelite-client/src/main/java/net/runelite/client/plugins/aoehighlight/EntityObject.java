package net.runelite.client.plugins.aoehighlight;

import net.runelite.api.coords.LocalPoint;
import java.time.Instant;

public class EntityObject {
    private final Instant startTime;
    private int startTimeTicks;
    private final int projEndCycle;
    private final LocalPoint location;
    private final Entity entity;

    public EntityObject(Instant startTime,int startTimeTicks, int projEndCycle, LocalPoint location, Entity entity) {
        this.startTime = startTime;
        this.startTimeTicks = startTimeTicks;
        this.projEndCycle = projEndCycle;
        this.entity = entity;
        this.location = location;
    }

    public Instant getStartTime() {
        return startTime;
    }
    public int getProjEndCycle() { return projEndCycle; }

    public LocalPoint getLocation() {
        return location;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getStartTimeTicks(){return startTimeTicks;}
}
