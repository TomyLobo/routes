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

package eu.tomylobo.abstraction.bukkit.event;

import org.bukkit.event.player.PlayerEvent;

import eu.tomylobo.abstraction.bukkit.BukkitUtils;
import eu.tomylobo.abstraction.event.Event;
import eu.tomylobo.abstraction.event.PlayerClickEvent;
import eu.tomylobo.math.Location;

public class BukkitEventUtils {
	static Event wrap(org.bukkit.event.Event backend) {
		final Event event = new Event();

		if (backend instanceof PlayerEvent) {
			event.setPlayer(BukkitUtils.wrap(((PlayerEvent) backend).getPlayer()));
		}

		return event;
	}

	static Event wrap(org.bukkit.event.block.BlockEvent backend) {
		final Event event = wrap((org.bukkit.event.Event) backend);

		final eu.tomylobo.math.Location location = BukkitUtils.wrap(backend.getBlock().getLocation());

		event.setLocation(location);
		event.setBlockState(location.getBlockState());

		return event;
	}

	static PlayerClickEvent wrapClick(org.bukkit.event.player.PlayerInteractEvent backend) {
		final PlayerClickEvent event = new PlayerClickEvent();

		event.setPlayer(BukkitUtils.wrap(backend.getPlayer()));
		switch (backend.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			event.setRightClick(true);
			break;
		}

		if (backend.hasBlock()) {
			final Location location = BukkitUtils.wrap(backend.getClickedBlock().getLocation());
			event.setLocation(location);
			event.setBlockState(location.getBlockState());
		}
		else {
			event.setAir(true);
		}

		return event;
	}

	static Event wrap(org.bukkit.event.entity.EntityEvent backend) {
		final Event event = wrap((org.bukkit.event.Event) backend);

		event.setEntity(BukkitUtils.wrap(backend.getEntity()));

		return event;
	}
}
