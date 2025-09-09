package net.runelite.client.plugins.mafhamtob.Maiden;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;

@AllArgsConstructor
public class Crab {

    @Getter
    @Setter
    public NPC npc;
    @Getter
    @Setter
    public int health;
    @Getter
    @Setter
    public boolean reachedMaiden;
}