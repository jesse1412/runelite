package net.runelite.client.plugins.mafhamplayerindicators;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@PluginDescriptor(
        name = "Mafham Player Indicators",
        description = "Custom Player Indicator Plugin",
        tags = {"Mafham", "player","indicator","indicators"}
)
public class MafhamPlayerIndicatorsPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private MafhamPlayerIndicatorsConfig config;
    @Inject
    private MafhamPlayerIndicatorsOverlay overlay;
    @Inject
    private OverlayManager overlayManager;
    @Provides
    MafhamPlayerIndicatorsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(MafhamPlayerIndicatorsConfig.class);
    }
    @Getter
    public HashSet<LocalPoint> worldHighlights = new HashSet<>();
    @Getter
    public HashSet<Player> localHighlights = new HashSet<>();
    @Getter
    public HashSet<Player> playerHighlights = new HashSet<>();
    private String[] playerNames;

    private static final List<Integer> TOB_REGION_IDS = Arrays.asList(
            12869, //MAIDEN 1
            12613, //MAIDEN 2
            13125, //BLOAT
            13122, //NYLO
            13123, //SOTE OVERWORLD
            13379, //SOTE UNDERWORLD
            12612, //XARPUS
            12611, //VERZIK
            12867 //THRONE ROOM
    );

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        playerNames = config.getPlayerNames().split(",");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        worldHighlights.clear();
        playerHighlights.clear();
        localHighlights.clear();
    }

    private boolean isPlayerInList(Player player)
    {
        return Arrays.asList(playerNames).contains(player.getName());
    }

    private boolean isPlayerInToa()
    {
        if (client.getWidget(481, 6) == null)
        {
            return false;
        }
        return !Objects.requireNonNull(client.getWidget(481, 6)).getChild(0).getText().isEmpty();
    }

    private boolean isPlayerInToB()
    {
        for (int mapRegion : client.getMapRegions())
        {
            if (TOB_REGION_IDS.contains(mapRegion))
            {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("playerNames")) {
            playerNames = config.getPlayerNames().split(",");
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        worldHighlights.clear();
        localHighlights.clear();
        playerHighlights.clear();
        List<Player> players = client.getPlayers();
        Player self = client.getLocalPlayer();
        for (Player player : players) {
            String friendedPlayer = player.getName();
            if (config.highlightFriends()) {
                if (client.isFriended(friendedPlayer, false) && player != self) {
                    WorldPoint worldPoint = player.getWorldLocation();
                    LocalPoint worldHL = LocalPoint.fromWorld(client, worldPoint);
                    worldHighlights.add(worldHL);
                    localHighlights.add(player);
                }
            }
            if (config.highlightAll())
            {
                if (player != self) {
                    WorldPoint worldPoint = player.getWorldLocation();
                    LocalPoint worldHL = LocalPoint.fromWorld(client, worldPoint);
                    worldHighlights.add(worldHL);
                    localHighlights.add(player);
                }
            }
            if (config.highlightList())
            {
                if (isPlayerInList(player))
                {
                    WorldPoint worldPoint = player.getWorldLocation();
                    LocalPoint worldHL = LocalPoint.fromWorld(client, worldPoint);
                    worldHighlights.add(worldHL);
                    localHighlights.add(player);
                }
            }
            if (config.highlightFriendsNames())
            {
                if (client.isFriended(friendedPlayer, false) && player != self)
                {
                    playerHighlights.add(player);
                }
            }
            if (config.highlightAllNames())
            {
                if (player != self)
                {
                    playerHighlights.add(player);
                }
            }
            if (config.highlightListNames())
            {
                if (isPlayerInList(player))
                {
                    playerHighlights.add(player);
                }
            }
            if (isPlayerInToa() || client.getVarbitValue(Varbits.IN_RAID) == 1 || isPlayerInToB())
            {
                if (config.highlightRaids())
                {
                    if (player != self)
                    {
                        WorldPoint worldPoint = player.getWorldLocation();
                        LocalPoint worldHL = LocalPoint.fromWorld(client, worldPoint);
                        worldHighlights.add(worldHL);
                        localHighlights.add(player);
                    }
                }
                if (config.highlightRaidsNames())
                {
                    if (player != self)
                    {
                        playerHighlights.add(player);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        GameState gameState = event.getGameState();
        if (gameState == GameState.HOPPING)
        {
            worldHighlights.clear();
            localHighlights.clear();
            playerHighlights.clear();
        }
    }
}