package net.runelite.client.plugins.mafhamleviathan;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.List;

@PluginDescriptor(
        name = "Mafham Leviathan",
        description = "Mafham Leviathan",
        tags = {"Mafham", "Leviathan"}
)
public class MafhamLeviathanPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MafhamLeviathanOverlay overlay;
    @Getter
    private boolean bossSpawned = false;

    private List<Integer> bossIds = List.of(12215, 12214, 12219);

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        if (bossIds.contains(npcSpawned.getNpc().getId()))
        {
            bossSpawned = true;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned)
    {
        if (bossIds.contains(npcDespawned.getNpc().getId()))
        {
            bossSpawned = false;
        }
    }
}