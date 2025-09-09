/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
 *
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
package net.runelite.client.plugins.aoehighlight;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.time.Instant;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class AoeHighlightOverlay extends Overlay {
	private final Client client;
	private final AoeHighlightPlugin plugin;
	private final AoeHighlightConfig config;

	@Inject
	public AoeHighlightOverlay(@Nullable Client client, AoeHighlightPlugin plugin, AoeHighlightConfig config) {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (!config.enabled()) {
			return null;
		}
		boolean inWardenP3 = Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 15696);
		Instant now = Instant.now();
		List<EntityObject> builtObjects = plugin.getBuiltObjects();
		if (builtObjects.isEmpty()) {
			return null;
		}

		for (Iterator<EntityObject> iterator = builtObjects.iterator(); iterator.hasNext();) {
			EntityObject entityObject = iterator.next();
			int type = entityObject.getEntity().getType();
			//Logic for if it's a tile flip and it's close to wardens, make it disappear 1 tick earlier
			if (entityObject.getEntity() == Entity.WARDENS_TILEFLIP && plugin.getWarden() != null && config.tileFlipSpecial())
			{
				int wardenY = plugin.getWarden().getLocalLocation().getY();
				int tileY = entityObject.getLocation().getY();
				if (tileY < wardenY + 1024 && now.isAfter(entityObject.getStartTime().plus(Duration.ofMillis(entityObject.getEntity().getLifeTime() - 600))))
				{
					iterator.remove();
					continue;
				}
			}

			if (type == 1) {
				if (entityObject.getProjEndCycle() <= client.getGameCycle()) {
					iterator.remove();
					continue;
				}
			}
			else if (type == 2) {
				if (now.isAfter(entityObject.getStartTime().plus(Duration.ofMillis(entityObject.getEntity().getLifeTime())))) {
					iterator.remove();
					continue;
				}
			}

			Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, entityObject.getLocation(), entityObject.getEntity().getSize());
			if (tilePoly == null) {
				continue;
			}

			//Logic to prevent highlighting baba boulders for the last tick at warden p3
			if (entityObject.getEntity() == Entity.BABA_ROCKS_FAST && inWardenP3 && config.lightningSpecial())
			{
				if (now.isAfter(entityObject.getStartTime().plus(Duration.ofMillis(entityObject.getEntity().getLifeTime() - 600))))
				{
					iterator.remove();
					continue;
				}
			}

			Color entityColour = entityObject.getEntity().getColor();
			Color outlineColour = ((entityObject.getEntity().getColor() == null) ? config.highlightColor() : entityColour);
			Color tileColour = new Color(outlineColour.getRed(), outlineColour.getGreen(), outlineColour.getBlue(), 25);

			graphics.setColor(tileColour);
			graphics.fillPolygon(tilePoly);
			if (entityObject.getEntity().showOutline())
			{
				graphics.setColor(outlineColour);
				graphics.drawPolygon(tilePoly);
			}
		}
		return null;
	}
}