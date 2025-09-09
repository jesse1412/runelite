package net.runelite.client.plugins.nightmare;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import static net.runelite.client.ui.overlay.OverlayUtil.renderPolygon;

@Singleton
@Slf4j
class NightmarePrayerOverlay extends Overlay
{
	private final Client client;
	private final NightmarePlugin plugin;
	private final NightmareConfig config;
	private final int prayerWidgetParentID = 541;
	private final int mageWidgetID = 21;
	private final int rangeWidgetID = 22;
	private final int meleeWidgetID = 23;

	@Inject
	private NightmarePrayerOverlay(final Client client, final NightmarePlugin plugin, final NightmareConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInFight() || plugin.getNm() == null)
		{
			return null;
		}

		NightmareAttack attack = plugin.getPendingNightmareAttack();

		if (attack == null)
		{
			return null;
		}

		if (!config.prayerHelper().showWidgetHelper())
		{
			return null;
		}
		if (client.getWidget(prayerWidgetParentID, 0) == null)
		{
			return null;
		}
		Color color = client.isPrayerActive(attack.getPrayer()) ? Color.GREEN : Color.RED;

		if (attack == NightmareAttack.CURSE_MAGIC || attack == NightmareAttack.MELEE)
		{
			Widget widget = client.getWidget(prayerWidgetParentID, meleeWidgetID);
			renderWidgetOverlay(widget, graphics, color);
		}
		if (attack == NightmareAttack.CURSE_RANGE || attack == NightmareAttack.MAGIC)
		{
			Widget widget = client.getWidget(prayerWidgetParentID, mageWidgetID);
			renderWidgetOverlay(widget, graphics, color);
		}
		if (attack == NightmareAttack.CURSE_MELEE || attack == NightmareAttack.RANGE)
		{
			Widget widget = client.getWidget(prayerWidgetParentID, rangeWidgetID);
			renderWidgetOverlay(widget, graphics, color);
		}

		return null;
	}


	private void renderWidgetOverlay(Widget widget, Graphics2D graphics, Color color)
	{
		int widgetX = widget.getCanvasLocation().getX();
		int widgetY = widget.getCanvasLocation().getY();
		int width = widget.getWidth();
		int height = widget.getHeight();
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(2));
		graphics.drawRect(widgetX, widgetY, width, height);
	}
}