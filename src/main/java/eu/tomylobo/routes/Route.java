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

package eu.tomylobo.routes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.interpolation.Interpolation;
import eu.tomylobo.routes.interpolation.KochanekBartelsInterpolation;
import eu.tomylobo.routes.interpolation.ReparametrisingInterpolation;
import eu.tomylobo.routes.util.Ini;
import eu.tomylobo.routes.util.Statistics;
import eu.tomylobo.routes.util.Utils;

/**
 * Represents a route from one point to another, consisting of multiple nodes.
 *
 * @author TomyLobo
 *
 */
public final class Route {
	private World world;

	private final List<Node> nodes = new ArrayList<Node>();
	private boolean nodesDirty = false;

	//private Interpolation interpolation = new LinearInterpolation();
	private Interpolation interpolation = new ReparametrisingInterpolation(new KochanekBartelsInterpolation());

	private List<FakeEntity> visualizationEntities = new ArrayList<FakeEntity>();
	private int taskId = -1;

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
		this.nodesDirty = true;
	}

	public void addNodes(Node... nodes) {
		for (Node node : nodes) {
			this.nodes.add(node);
		}
		this.nodesDirty = true;
	}

	public Location getLocation(double position) {
		ensureClean();

		final Vector vec = interpolation.getPosition(position);
		if (vec == null)
			return null;

		return Utils.locationFromEye(world, vec, interpolation.get1stDerivative(position));
	}

	private void ensureClean() {
		if (nodesDirty) {
			interpolation.setNodes(nodes);
			nodesDirty = false;
		}
	}

	public Vector getVelocity(double position) {
		ensureClean();

		return interpolation.get1stDerivative(position);
	}

	public void visualize(double pointsPerMeter) {
		clearVisualization();

		int points = (int) Math.ceil(pointsPerMeter * length());

		double lastPosition = -1;
		final Statistics stats = new Statistics();

		for (int i = 0; i < points; ++i) {
			final double position = ((double) i) / points;
			final Location location = getLocation(position);
			if (lastPosition != -1) {
				final double distance = interpolation.arcLength(lastPosition, position);

				stats.stat(distance);
			}
			lastPosition = position;

			final FakeEntity a = new FakeVehicle(location, VehicleType.ENDER_EYE);
			a.send();
			a.teleport(location);

			visualizationEntities.add(a);
		}

		System.out.println(stats.format());

		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Routes.getInstance(), new Runnable() {
			@Override
			public void run() {
				clearVisualization();
			}
		}, 600);
	}

	public void save(Multimap<String, Multimap<String, String>> sections, String routeName) {
		final String routeSectionName = "route "+routeName;
		final Multimap<String, String> routeSection = LinkedListMultimap.create();

		Ini.saveWorld(routeSection, "%s", world);
		routeSection.put("nodes", String.valueOf(nodes.size()));

		sections.put(routeSectionName, routeSection);

		for (int i = 0; i < nodes.size(); ++i) {
			final Node node = nodes.get(i);

			final String nodeName = routeName + "-" + i;
			node.save(sections, nodeName);
		}
	}

	public void load(Multimap<String, Multimap<String, String>> sections, String routeName) {
		final String routeSectionName = "route "+routeName;
		final Multimap<String, String> routeSection = Ini.getOnlyValue(sections.get(routeSectionName));

		world = Ini.loadWorld(routeSection, "%s");
		int nNodes = Ini.getOnlyInt(routeSection.get("nodes"));

		nodes.clear();
		((ArrayList<Node>) nodes).ensureCapacity(nNodes);
		for (int i = 0; i < nNodes; ++i) {
			final String nodeName = routeName + "-" + i;

			nodes.add(new Node(sections, nodeName));
		}
		nodesDirty = true;
	}

	public double length() {
		ensureClean();

		return interpolation.arcLength(0, 1);
	}

	private void clearVisualization() {
		Bukkit.getScheduler().cancelTask(taskId);
		for (FakeEntity entity : visualizationEntities) {
			entity.remove();
		}
		visualizationEntities.clear();
	}
}
