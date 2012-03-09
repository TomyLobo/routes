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

package eu.tomylobo.abstraction.event;

import eu.tomylobo.abstraction.block.BlockState;
import eu.tomylobo.abstraction.entity.Entity;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.math.Location;

public class Event {
	private Location location;
	private BlockState blockState;
	private Entity entity;
	private Player player;
	private boolean cancelled;


	public Location getLocation() {
		return location;
	}

	public Event setLocation(Location location) {
		this.location = location;
		return this;
	}


	public BlockState getBlockState() {
		return blockState;
	}

	public Event setBlockState(BlockState blockState) {
		this.blockState = blockState;
		return this;
	}


	public Entity getEntity() {
		return entity;
	}

	public Event setEntity(Entity entity) {
		this.entity = entity;
		return this;
	}


	public Player getPlayer() {
		return player;
	}

	public Event setPlayer(Player player) {
		this.player = player;
		return this;
	}


	public boolean isCancelled() {
		return cancelled;
	}

	public Event setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		return this;
	}
}
