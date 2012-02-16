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

import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.interpolation.Interpolation;
import eu.tomylobo.routes.interpolation.KochanekBartelsInterpolation;
import eu.tomylobo.routes.interpolation.ReparametrisingInterpolation;
import eu.tomylobo.routes.util.Utils;

public final class Route {
	private World world;

	private final List<Node> nodes = new ArrayList<Node>();
	private boolean nodesDirty = false;

	//private Interpolation interpolation = new LinearInterpolation();
	private Interpolation interpolation = new ReparametrisingInterpolation(new KochanekBartelsInterpolation(0, 0, 0));

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
		if (nodesDirty) {
			interpolation.setNodes(nodes);
			nodesDirty = false;
		}

		final Vector vec = interpolation.getPosition(position);
		if (vec == null)
			return null;

		return Utils.locationFromEye(world, vec, interpolation.get1stDerivative(position));
	}

	public Vector getVelocity(double position) {
		if (nodesDirty) {
			interpolation.setNodes(nodes);
			nodesDirty = false;
		}

		return interpolation.get1stDerivative(position);
	}

	public void visualize(int points) {
		final List<FakeEntity> entities = new ArrayList<FakeEntity>();
		double lastPosition = -1;
		final List<Double> distances = new ArrayList<Double>();
		double sum = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int i = 0; i < points; ++i) {
			final double position = ((double) i) / points;
			final Location location = getLocation(position);
			if (lastPosition != -1) {
				final double distance = interpolation.arcLength(lastPosition, position);

				distances.add(distance);
				sum += distance;

				if (distance > max)
					max = distance;

				if (distance < min)
					min = distance;
			}
			lastPosition = position;

			final FakeEntity a = new FakeVehicle(location, VehicleType.ENDER_EYE);
			a.send();
			a.teleport(location);

			entities.add(a);
		}

		final double mean = sum/distances.size();
		double sumSqErrors = 0;
		for (double distance : distances) {
			distance -= mean;
			sumSqErrors += distance * distance;
		}

		System.out.println(String.format("min/mean/max/rmse: %f/%f/%f/%f\n", min, mean, max, Math.sqrt(sumSqErrors / distances.size())));

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Routes.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (FakeEntity entity : entities) {
					entity.remove();
				}
			}
		}, 600);
	}
}
