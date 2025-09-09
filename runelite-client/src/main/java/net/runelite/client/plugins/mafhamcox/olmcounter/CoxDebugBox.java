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
package net.runelite.client.plugins.mafhamcox.olmcounter;
import net.runelite.client.plugins.mafhamcox.MafhamCoxConfig;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.plugins.mafhamcox.MafhamCoxPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;


public class CoxDebugBox extends Overlay
{
	private final Client client;
	private final MafhamCoxPlugin plugin;
	private final MafhamCoxConfig config;
	private final Olm olm;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	CoxDebugBox(Client client, MafhamCoxPlugin plugin, MafhamCoxConfig config, Olm olm)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.olm = olm;
		this.setPosition(OverlayPosition.BOTTOM_LEFT);
		this.setPriority(OverlayPriority.HIGH);
		this.panelComponent.setPreferredSize(new Dimension(270, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!this.config.olmDebug() || !plugin.inRaid())
		{
			return null;
		}
		this.panelComponent.getChildren().clear();
		net.runelite.client.plugins.mafhamcox.olmcounter.table.TableComponent tableComponent = new net.runelite.client.plugins.mafhamcox.olmcounter.table.TableComponent();
		tableComponent.addRow("ticks", String.valueOf(client.getTickCount()));
		tableComponent.addRow("active", String.valueOf(this.olm.active));
		tableComponent.addRow("handAnim", String.valueOf(this.olm.handAnimation));
		tableComponent.addRow("headAnim", String.valueOf(this.olm.headAnimation));
		tableComponent.addRow("firstPhase", String.valueOf(this.olm.firstPhase));
		tableComponent.addRow("finalPhase", String.valueOf(this.olm.finalPhase));
		tableComponent.addRow("attackTicks", String.valueOf(this.olm.ticksUntilNextAttack));
		tableComponent.addRow("attackCycle", String.valueOf(this.olm.attackCycle));
		tableComponent.addRow("specialCycle", String.valueOf(this.olm.specialCycle));
		tableComponent.addRow("handCrippled", String.valueOf(this.olm.crippled));
		tableComponent.addRow("crippleTicks", String.valueOf(this.olm.crippleTicks));
		tableComponent.addRow("savedCrippleTimer" + " " + this.olm.savedcripplecounter);
		tableComponent.addRow("interPhaseBool" + " " + this.olm.interPhase);
		tableComponent.addRow("interPhaseTimer" + " " + this.olm.interPhaseTimer);
		this.panelComponent.getChildren().add(tableComponent);

		return this.panelComponent.render(graphics);
	}
}
