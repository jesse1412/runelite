package net.runelite.client.plugins.mafhamgauntlet;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

public class MafhamGauntletOverlay extends Overlay
{
	private final Client client;
	private final MafhamGauntletPlugin plugin;
	private final MafhamGauntletConfig config;
	private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
	private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
	private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
	private static final int OVERLAY_ICON_DISTANCE = 50;
	private static final int OVERLAY_ICON_MARGIN = 8;

	@Inject
	private SkillIconManager iconManager;

	@Inject
	private MafhamGauntletOverlay(Client client, MafhamGauntletPlugin plugin, MafhamGauntletConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	private BufferedImage getIcon(MafhamGauntletPlugin.attackStyle attackStyle)
	{
		switch (attackStyle)
		{
			case RANGE:
				return iconManager.getSkillImage(Skill.RANGED);
			case MAGE:
				return iconManager.getSkillImage(Skill.MAGIC);
		}
		return null;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getMainBoss() == null)
		{
			return null;
		}

		if (plugin.getPlayerAttackCount() != null && config.showAttackCounter())
		{
			String string = plugin.getPlayerAttackCount().toString();
			NPC npc = plugin.getMainBoss();
			Point canvasPoint = npc.getCanvasTextLocation(graphics, string, 0);
			Color color = (plugin.getPlayerAttackCount() == 0) ? Color.yellow : Color.white;
			renderTextLocation(graphics,string,18, 4, color, canvasPoint, true);
		}
		if (!plugin.getTornadoNPCs().isEmpty() && plugin.getTornadoTimer() != null)
		{
			for (NPC tornado : plugin.getTornadoNPCs())
			{
				String string = plugin.getTornadoTimer().toString();
				WorldPoint worldPoint = tornado.getWorldLocation();
				LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
				Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, string, 0);
				Point canvasPoint = tornado.getCanvasTextLocation(graphics, string, 0);
				if (config.showTornadoTimer())
				{
					renderTextLocation(graphics, string, 10, 4, Color.cyan, canvasPoint, false);
				}
				if (config.showTornadoTimerTrue())
				{
					renderTextLocation(graphics, string, 10, 4, Color.cyan, canvasTextLocation, false);
				}
			}
		}

		if (config.ShowPrayers())
		{
			if (plugin.getCurrentAttackStyle() == null)
			{
				return null;
			}

			LocalPoint lp = plugin.getMainBoss().getLocalLocation();
			if (lp != null)
			{
				net.runelite.api.Point point = Perspective.localToCanvas(client, lp, client.getPlane(),
						16);
				if (point != null)
				{
					point = new Point(point.getX(), point.getY());
					BufferedImage icon = getIcon(plugin.getCurrentAttackStyle());

					int bgPadding = 4;
					int currentPosX = 0;

					graphics.setStroke(new BasicStroke(2));
					graphics.setColor(COLOR_ICON_BACKGROUND);
					graphics.fillOval(
							point.getX() - currentPosX - bgPadding,
							point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
							icon.getWidth() + bgPadding * 2,
							icon.getHeight() + bgPadding * 2);

					graphics.setColor(COLOR_ICON_BORDER);
					graphics.drawOval(
							point.getX() - currentPosX - bgPadding,
							point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
							icon.getWidth() + bgPadding * 2,
							icon.getHeight() + bgPadding * 2);

					graphics.drawImage(
							icon,
							point.getX() - currentPosX,
							point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE,
							null);
					//System.out.println(-360.0 * (plugin.getAttacksPerSwitch() - (plugin.getAttacksToSwitch())) / plugin.getAttacksPerSwitch());
					//System.out.println(plugin.getAttacksPerSwitch() - (plugin.getAttacksToSwitch()));

					//System.out.println((plugin.getAttacksPerSwitch() - plugin.getAttacksToSwitch()) / plugin.getAttacksPerSwitch());
					//System.out.println(plugin.getAttacksPerSwitch());
					//System.out.println(plugin.getAttacksToSwitch());
					graphics.setColor(COLOR_ICON_BORDER_FILL);
					Arc2D.Double arc = new Arc2D.Double(
							point.getX() - currentPosX - bgPadding,
							point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
							icon.getWidth() + bgPadding * 2,
							icon.getHeight() + bgPadding * 2,
							90.0,
							360.0 * (-plugin.getBossAttackCounter()) / 4,
							Arc2D.OPEN);
					graphics.draw(arc);

					currentPosX += icon.getWidth() + OVERLAY_ICON_MARGIN;
				}
			}
		}
		return null;
	}

	private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, net.runelite.api.Point canvasPoint, boolean drawShadow)
	{
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null)
		{
			final net.runelite.api.Point canvasCenterPoint = new net.runelite.api.Point(
					canvasPoint.getX(),
					canvasPoint.getY());
			final net.runelite.api.Point canvasCenterPoint_shadow = new Point(
					canvasPoint.getX() + 1,
					canvasPoint.getY() + 1);
			if (drawShadow)
			{
				OverlayUtil.renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
			}
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}
}