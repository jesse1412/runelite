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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

public class AttackHighlightsPanelOverlay extends OverlayPanel
{
	private final AttackHighlightsConfig config;
	public ArrayList<String> listedActors = new ArrayList<String>();
	public ArrayList<String> listedIDs = new ArrayList<String>();
	private final static int MAX_LINES = 5;
	@Getter
	private boolean npcIDOverlayShown = false;

	@Inject
	public AttackHighlightsPanelOverlay(AttackHighlightsPlugin plugin, AttackHighlightsConfig config) {
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		npcIDOverlayShown = shouldShowOverlay();
		if (shouldShowOverlay()) {
			panelComponent.getChildren().add(LineComponent.builder()
					.left("Animation Actors")
					.leftColor(Color.CYAN)
					.build());
			int ID = 0;
			for (String npcID : listedIDs) {
				panelComponent.getChildren().add(LineComponent.builder()
						.right(listedActors.get(ID))
						.left(listedIDs.get(ID))
						.build());
				ID++;
				checkMaxLines();
			}
		}

		return super.render(graphics);
	}

	@Subscribe
	void receiveAnimation(String animation) {
		listedIDs.add(animation);
	}

	void receiveActor(String actor) {
		listedActors.add(actor);
	}

	private boolean shouldShowOverlay() {
		return config.showIDs();
	}

	private void checkMaxLines()
	{
		while (panelComponent.getChildren().size() > MAX_LINES)
		{
			panelComponent.getChildren().remove(1);
		}
	}
}