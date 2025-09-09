package net.runelite.client.plugins.mafhambarrows;

import com.google.common.annotations.VisibleForTesting;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.Hooks;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.SummaryState;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@PluginDescriptor(
        name = "Mafham Barrows",
        description = "Mafham Barrows",
        tags = {"Mafham", "Barrows"}
)
public class MafhamBarrowsPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Hooks hooks;
    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
    private final int barrows_area_id = 14231;
    private final List<Integer> brother_ids = Arrays.asList(
            1672, //ahrim
            1673, //dharok
            1674, //guthan
            1675, //karil
            1676, //torag
            1677 //verac
    );

    private List<NPC> brothers_who_had_arrow = new ArrayList<>();

    @Override
    protected void startUp() throws Exception {
        hooks.registerRenderableDrawListener(drawListener);
    }

    @Override
    protected void shutDown() throws Exception {
        hooks.unregisterRenderableDrawListener(drawListener);
    }
    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        for (NPC npc : client.getNpcs())
        {
            if (brother_ids.contains(npc.getId()) && npc == client.getHintArrowNpc())
            {
               if (!brothers_who_had_arrow.contains(npc))
               {
                   brothers_who_had_arrow.add(npc);
               }
            }
        }
        if (!is_in_barrows())
        {
           brothers_who_had_arrow.clear();
        }
        //System.out.println(brothers_who_had_arrow.size());
    }


    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof Player) {
            Player player = (Player) renderable;
            Player local = client.getLocalPlayer();
            if (is_in_barrows() && player != local)
            {
                return drawingUI;
            }
        }
        if (renderable instanceof NPC) {
            NPC npc = (NPC) renderable;
            if (!brothers_who_had_arrow.contains(npc) && brother_ids.contains(npc.getId()))
            {
                return drawingUI;
            }
        }
        return true;
    }

    private boolean is_in_barrows()
    {
        if (client.getLocalPlayer() != null && client.getLocalPlayer().getWorldLocation() != null && client.getLocalPlayer().getWorldLocation().getRegionID() == barrows_area_id)
        {
            return true;
        }
        return false;
    }
}