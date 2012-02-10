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

import de.tomylobo.routes.fakeentity.FakeEnderEye;
import de.tomylobo.routes.fakeentity.FakeEntity;

public final class Route {
	private final Routes plugin;

	private final List<Node> nodes = new ArrayList<Node>();
	private World world;
	private Interpolation interpolation = new LinearInterpolation();

	public Route(Routes plugin) {
		this.plugin = plugin;
	}

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

	public Location getLocation(double position) {
		final Vector vec = interpolation.getPosition(position, nodes);
		if (vec == null)
			return null;

		return vec.toLocation(world);
	}

	public void visualize(int points) {
		final List<FakeEntity> entities = new ArrayList<FakeEntity>();

		for (int i = 0; i < points; ++i) {
			final double position = ((double) i) / points;
			final Location location = getLocation(position);

			final FakeEntity a = new FakeEnderEye(location);
			a.send();
			a.teleport(location);

			entities.add(a);
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (FakeEntity entity : entities) {
					entity.remove();
				}
			}
		}, 400);
	}
}
