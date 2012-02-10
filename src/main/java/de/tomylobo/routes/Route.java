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

package de.tomylobo.routes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public final class Route {
	private final List<Node> nodes = new ArrayList<Node>();
	private World world;

	public List<Node> getNodes() {
		return nodes;
	}

	public void addNodes(Location... locations) {
		for (Location location : locations) {
			World world = location.getWorld();
			if (this.world == null) {
				this.world = world;
			}
			else if (this.world != world) {
				throw new IllegalArgumentException("New node must be in the same world.");
			}

			this.nodes.add(new Node(location.toVector()));
		}
	}

	public void addNodes(Node... nodes) {
		for (Node node : nodes) {
			this.nodes.add(node);
		}
	}

	public Vector getPosition(double position) {
		if (position >= nodes.size()-1)
			return null;

		final int index1 = (int) Math.floor(position);
		final double remainder = position - index1;

		final Vector position1 = nodes.get(index1).getPosition().clone();
		final Vector position2 = nodes.get(index1 + 1).getPosition().clone();

		return position1.multiply(1.0 - remainder).add(position2.multiply(remainder));
	}

	public Location getLocation(double position) {
		return getPosition(position).toLocation(world);
	}
}
