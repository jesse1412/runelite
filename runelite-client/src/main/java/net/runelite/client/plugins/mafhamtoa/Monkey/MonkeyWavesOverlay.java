package net.runelite.client.plugins.mafhamtoa.Monkey;

import net.runelite.api.Client;
import net.runelite.client.plugins.mafhamtoa.MafhamToAConfig;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class MonkeyWavesOverlay extends OverlayPanel {

    @Inject
    private MonkeyWaves monkeyWaves;

    @Inject
    private Client client;

    @Inject
    private MafhamToAConfig config;

    private final Color brawlerColor = new Color(255, 0, 0);
    private final Color throwerColor = new Color(0, 255, 0);
    private final Color mageColor = new Color(51, 204, 255);
    private final Color shamanColor = new Color(255, 255, 0);
    private final Color volatileColor = new Color(255, 153, 51);
    private final Color cursedColor = new Color(0, 204, 153);
    private final Color specialColor = new Color(255,102, 255);

    @Inject
    public MonkeyWavesOverlay(MonkeyWaves monkeyWaves, Client client, MafhamToAConfig config)
    {
        this.monkeyWaves = monkeyWaves;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showWavePanel())
        {
            return null;
        }
        if (monkeyWaves.getWaveCounter() == null || monkeyWaves.getGroupSize() == null)
        {
            return null;
        }
        panelComponent.getChildren().add(LineComponent.builder()
                        .left("Next wave: (" + (monkeyWaves.getWaveCounter() + 1) + ")")
                        .build());
        if (monkeyWaves.getGroupSize() == 1)
        {
            switch (monkeyWaves.getWaveCounter())
            {
                case 0:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(brawlerColor)
                            .left("2x Brawler")
                            .build());
                    break;
                case 1:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(throwerColor)
                            .left("1x Thrower")
                            .build());
                            panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(mageColor)
                            .left("1x Mage")
                            .build());
                    break;
                case 2:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(brawlerColor)
                            .left("2x Brawler")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(shamanColor)
                            .left("1x Shaman")
                            .build());
                    break;
                case 3:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(throwerColor)
                            .left("2x Thrower")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(volatileColor)
                            .left("1x Volatile")
                            .build());
                    break;
                case 4:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(mageColor)
                            .left("2x Mage")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(cursedColor)
                            .left("1x Cursed")
                            .build());
                    break;
                case 5:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(throwerColor)
                            .left("2x Thrower")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(volatileColor)
                            .left("1x Volatile")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(cursedColor)
                            .left("1x Cursed")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(specialColor)
                            .left("1x Special")
                            .build());
                    break;
                case 6:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(mageColor)
                            .left("2x Mage")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(volatileColor)
                            .left("1x Volatile")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(shamanColor)
                            .left("1x Shaman")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(specialColor)
                            .left("1x Special")
                            .build());
                    break;
                case 7:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(brawlerColor)
                            .left("2x Brawler")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(shamanColor)
                            .left("1x Shaman")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(cursedColor)
                            .left("1x Cursed")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(specialColor)
                            .left("1x Special")
                            .build());
                    break;
                case 8:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(brawlerColor)
                            .left("1x Brawler")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(throwerColor)
                            .left("1x Thrower")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(shamanColor)
                            .left("1x Shaman")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(cursedColor)
                            .left("2x Cursed")
                            .build());
                    break;
                case 9:
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(shamanColor)
                            .left("1x Shaman")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(volatileColor)
                            .left("2x Volatile")
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(specialColor)
                            .left("2x Special")
                            .build());
                    break;
                default:
                    return null;

            }
        }
        else switch (monkeyWaves.getWaveCounter())
        {
            case 0:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(brawlerColor)
                        .left("3x Brawler")
                        .build());
                break;
            case 1:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(throwerColor)
                        .left("2x Thrower")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(mageColor)
                        .left("1x Mage")
                        .build());
                break;
            case 2:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(brawlerColor)
                        .left("3x Brawler")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(shamanColor)
                        .left("1x Shaman")
                        .build());
                break;
            case 3:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(throwerColor)
                        .left("3x Thrower")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(volatileColor)
                        .left("1x Volatile")
                        .build());
                break;
            case 4:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(mageColor)
                        .left("3x Mage")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(cursedColor)
                        .left("1x Cursed")
                        .build());
                break;
            case 5:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(throwerColor)
                        .left("3x Thrower")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(volatileColor)
                        .left("1x Volatile")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(cursedColor)
                        .left("1x Cursed")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(specialColor)
                        .left("1x Special")
                        .build());
                break;
            case 6:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(mageColor)
                        .left("3x Mage")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(volatileColor)
                        .left("1x Volatile")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(shamanColor)
                        .left("1x Shaman")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(specialColor)
                        .left("1x Special")
                        .build());
                break;
            case 7:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(brawlerColor)
                        .left("3x Brawler")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(shamanColor)
                        .left("1x Shaman")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(cursedColor)
                        .left("1x Cursed")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(specialColor)
                        .left("1x Special")
                        .build());
                break;
            case 8:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(brawlerColor)
                        .left("1x Brawler")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(throwerColor)
                        .left("1x Thrower")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(shamanColor)
                        .left("2x Shaman")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(cursedColor)
                        .left("2x Cursed")
                        .build());
                break;
            case 9:
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(shamanColor)
                        .left("1x Shaman")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(volatileColor)
                        .left("3x Volatile")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(specialColor)
                        .left("2x Special")
                        .build());
                break;
            default:
                return null;

        }
        return super.render(graphics);
    }
}