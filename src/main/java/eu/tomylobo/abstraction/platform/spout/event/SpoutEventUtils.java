/*
 * Copyright (C) 2012 TomyLobo
 *
 * This file is part of Routes.
 *
 * Routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.tomylobo.abstraction.platform.spout.event;

import org.spout.api.event.player.PlayerEvent;

import eu.tomylobo.abstraction.event.Event;
import eu.tomylobo.abstraction.event.PlayerClickEvent;
import eu.tomylobo.abstraction.platform.spout.SpoutUtils;
import eu.tomylobo.math.Location;

public class SpoutEventUtils {
	static Event wrap(org.spout.api.event.Event backend) {
		final Event event = new Event();

		if (backend instanceof org.spout.api.event.player.PlayerEvent) {
			event.setPlayer(SpoutUtils.wrap(((PlayerEvent) backend).getPlayer()));
		}

		return event;
	}

	static Event wrap(org.spout.api.event.block.BlockEvent backend) {
		final Event event = wrap((org.spout.api.event.block.BlockEvent) backend);

		final eu.tomylobo.math.Location location = SpoutUtils.wrap(backend.getBlock().getBase());

		event.setLocation(location);
		event.setBlockState(location.getBlockState());

		return event;
	}

	static PlayerClickEvent wrapClick(org.spout.api.event.player.PlayerInteractEvent backend) {
		final PlayerClickEvent event = new PlayerClickEvent();

		event.setPlayer(SpoutUtils.wrap(backend.getPlayer()));
		switch (backend.getAction()) {
		case RIGHT_CLICK:
			event.setRightClick(true);
			break;
		}

		if (backend.isAir()) {
			event.setAir(true);
		}
		else {
			final Location location = SpoutUtils.wrap(backend.getInteractedPoint());
			event.setLocation(location);
			event.setBlockState(location.getBlockState());
		}

		return event;
	}

	static Event wrap(org.spout.api.event.entity.EntityEvent backend) {
		final Event event = wrap((org.spout.api.event.Event) backend);

		event.setEntity(SpoutUtils.wrap(backend.getEntity()));

		return event;
	}
}
