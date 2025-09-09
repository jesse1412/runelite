package net.runelite.client.plugins.mafhamcox.reloader;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;
import net.runelite.client.plugins.mafhamcox.MafhamCoxPlugin;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;

public class InputListener extends MouseAdapter
{
    @Inject
    ReloaderOverlay reloaderOverlay;
    @Inject
    Client client;
    @Inject
    ClientThread clientThread;
    @Inject
    MafhamCoxPlugin plugin;
    @Inject
    MafhamCoxConfig config;

    @Override
    public MouseEvent mouseClicked(MouseEvent event)
    {
        if (!config.showReloader())
        {
            return event;
        }
        if (!plugin.isCoxLoaded)
        {
            return event;
        }
        if (plugin.clickBox != null && plugin.clickBox.getBounds().contains(event.getPoint()))
        {
            clientThread.invoke(() ->
                    client.setGameState(GameState.CONNECTION_LOST));
            event.consume();
        }
        return event;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event)
    {
        if (!plugin.isCoxLoaded)
        {
            return event;
        }
        if (plugin.clickBox != null && plugin.clickBox.getBounds().contains(event.getPoint()))
        {
            event.consume();
        }
        return event;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent event)
    {
        if (!plugin.isCoxLoaded)
        {
            return event;
        }
        if (plugin.clickBox != null && plugin.clickBox.getBounds().contains(event.getPoint()))
        {
            event.consume();
        }
        return event;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent event)
    {
        if (!plugin.isCoxLoaded)
        {
            return event;
        }
        plugin.setHovered(false);
        if (plugin.clickBox != null && plugin.clickBox.getBounds().contains(event.getPoint()))
        {
            plugin.setHovered(true);
        }
        return event;
    }
}