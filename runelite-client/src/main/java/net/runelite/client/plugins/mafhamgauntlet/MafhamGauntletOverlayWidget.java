/*
 * Copyright (c) 2019, lyzrds <https://discord.gg/5eb9Fe>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.mafhamgauntlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.ImageUtil;

@Singleton
public class MafhamGauntletOverlayWidget extends Overlay
{
	private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);
	private final MafhamGauntletPlugin plugin;
	private final MafhamGauntletConfig config;
	private final Client client;
	private final SpriteManager spriteManager;
	private final PanelComponent prayAgainstPanel = new PanelComponent();
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	MafhamGauntletOverlayWidget(MafhamGauntletPlugin plugin, MafhamGauntletConfig config, Client client, SpriteManager spriteManager)
	{
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		this.spriteManager = spriteManager;
		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		this.panelComponent.getChildren().clear();
		this.prayAgainstPanel.getChildren().clear();
		if (!config.showWidget())
		{
			return null;
		}
		if (plugin.getMainBoss() == null)
		{
			return null;
		}
		if (!plugin.getMainBoss().isInteracting())
		{
			return null;
		}

		Prayer prayer;
		if (plugin.getCurrentAttackStyle() == MafhamGauntletPlugin.attackStyle.MAGE)
		{
			prayer = Prayer.PROTECT_FROM_MAGIC;
		}
		else prayer = Prayer.PROTECT_FROM_MISSILES;

		final int scale = this.config.prayBoxSize();
		InfoBoxComponent prayComponent = new InfoBoxComponent();
		BufferedImage prayImg = ImageUtil.resizeImage(
				this.getPrayerImage(prayer), scale, scale
		);
		prayComponent.setImage(prayImg);
		prayComponent.setColor(Color.WHITE);
		prayComponent.setBackgroundColor(this.client.isPrayerActive(prayer)
				? ComponentConstants.STANDARD_BACKGROUND_COLOR
				: NOT_ACTIVATED_BACKGROUND_COLOR
		);
		prayComponent.setPreferredSize(new Dimension(scale + 4, scale + 4));
		this.prayAgainstPanel.getChildren().add(prayComponent);
		this.prayAgainstPanel.setPreferredSize(new Dimension(scale + 4, scale + 4));
		this.prayAgainstPanel.setBorder(new Rectangle(0, 0, 0, 0));
		return this.prayAgainstPanel.render(graphics);
	}

	private BufferedImage getPrayerImage(Prayer prayer)
	{
		switch (prayer)
		{
			case PROTECT_FROM_MAGIC:
				return this.spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
			case PROTECT_FROM_MELEE:
				return this.spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
			case PROTECT_FROM_MISSILES:
				return this.spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
			default:
				return this.spriteManager.getSprite(SpriteID.BARBARIAN_ASSAULT_EAR_ICON, 0);
		}
	}
}
