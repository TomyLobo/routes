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

public final class Route {
	private final List<Node> nodes = new ArrayList<Node>();

	public List<Node> getNodes() {
		return nodes;
	}

	public void addNodes(Location... locations) {
		for (Location location : locations) {
			final Node node = new Node(location);
			this.nodes.add(node);
		}
	}

	public void addNodes(Node... nodes) {
		for (Node node : nodes) {
			this.nodes.add(node);
		}
	}
}
