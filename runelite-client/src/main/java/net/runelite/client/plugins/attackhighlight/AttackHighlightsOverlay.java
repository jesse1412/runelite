/*
 * Copyright (c) 2021, Scott Foster <scott@sgfost.com>
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
package net.runelite.client.plugins.attackhighlight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.util.List;

public class AttackHighlightsOverlay extends OverlayPanel
{
	private final AttackHighlightsPlugin plugin;
	private final AttackHighlightsConfig config;

	@Inject
	public AttackHighlightsOverlay(AttackHighlightsPlugin plugin, AttackHighlightsConfig config) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		List<Integer> animationIDs = new ArrayList<>();
		if (!config.getIDs().isEmpty()){
			animationIDs = plugin.buildIDs();
		}

		int group = 1;
		for (Set<NPC> highlights : plugin.getGroupHighlights()) {
			for (NPC npc : highlights) {
				if (animationIDs.contains(npc.getAnimation()))
				{
					renderNpcOverlay(graphics, npc, plugin.getGroupColor(group), plugin.getGroupFillColor(group));
				}
			}
			group++;
		}

		return super.render(graphics);
	}

	private void renderNpcOverlay(Graphics2D graphics, NPC npc, Color color, Color fillColor) {
		if (config.highlightHullOutline()) {
			Shape shape = npc.getConvexHull();
			if (shape != null) {
				graphics.setColor(color);
				graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
				graphics.draw(shape);
			}
		}

		if (config.highlightHullFill()) {
			Shape shape = npc.getConvexHull();
			if (shape != null) {
				graphics.setColor(fillColor);
				graphics.fill(shape);
			}
		}

		if (config.highlightTile()) {
			Shape shape = npc.getCanvasTilePoly();
			if (shape != null) {
				graphics.setColor(color);
				graphics.setStroke(new BasicStroke(config.getOutlineStrokeWidth()));
				graphics.draw(shape);
			}
		}
	}
}