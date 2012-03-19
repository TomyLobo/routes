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

package eu.tomylobo.routes.infrastructure.editor;

import java.util.ArrayList;
import java.util.List;

import eu.tomylobo.abstraction.World;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.infrastructure.Node;
import eu.tomylobo.routes.infrastructure.Route;

public class VisualizedRoute {
	private final Route route;
	private final double pointsPerMeter;

	private final List<FakeVehicle> waypointMarkers = new ArrayList<FakeVehicle>();
	private final List<List<FakeVehicle>> lineMarkers = new ArrayList<List<FakeVehicle>>();
	private final Player player;

	/**
	 * Visualizes a route for everyone.
	 * 
	 * @param route
	 * @param pointsPerMeter
	 */
	public VisualizedRoute(Route route, double pointsPerMeter) {
		this(route, pointsPerMeter, null);
	}

	/**
	 * Visualizes a route for the specified player.
	 * 
	 * @param route The route to visualize
	 * @param pointsPerMeter How many points to draw each meter
	 * @param player The player to visualize for
	 */
	public VisualizedRoute(Route route, double pointsPerMeter, Player player) {
		this.route = route;
		this.pointsPerMeter = pointsPerMeter;
		this.player = player;

		refresh(0, 0, route.getNodes().size());
	}

	private void createEntities(int startIndex, int amount) {
		if (amount == 0)
			return;

		final World world = route.getWorld();
		final List<Node> nodes = route.getNodes();

		final int endIndex = Math.min(waypointMarkers.size(), startIndex + amount);
		for (int i = startIndex; i < endIndex; ++i) {
			Node node = nodes.get(i);
			final FakeVehicle waypointMarker = FakeVehicle.poolCreate(new Location(world, node.getPosition()), VehicleType.ENDER_CRYSTAL);
			sendFakeEntity(waypointMarker);

			waypointMarkers.set(i, waypointMarker);
		}

		int points = (int) Math.ceil(pointsPerMeter * route.length());

		for (int i = 0; i < points; ++i) {
			final double position = ((double) i) / points;

			final int index = route.getSegment(position);

			if (index < startIndex)
				continue;

			if (index >= endIndex)
				continue;

			final Location location = route.getLocation(position);

			final FakeVehicle lineMarker = FakeVehicle.poolCreate(location, VehicleType.ENDER_EYE);
			sendFakeEntity(lineMarker);

			lineMarkers.get(index).add(lineMarker);
		}
	}

	/**
	 * Removes all entities. Should be called when discarding this visualization. 
	 */
	public void removeEntities() {
		removeEntities(0, waypointMarkers.size());

		clearFakeVehiclePools();
	}

	public void clearFakeVehiclePools() {
		FakeVehicle.poolClear(VehicleType.ENDER_CRYSTAL);
		FakeVehicle.poolClear(VehicleType.ENDER_EYE);
	}

	private void removeEntities(int startIndex, int amount) {
		final int endIndex = Math.min(waypointMarkers.size(), startIndex + amount);
		for (int i = startIndex; i < endIndex; ++i) {
			waypointMarkers.set(i, null).poolFree();

			List<FakeVehicle> list = lineMarkers.get(i);
			for (FakeVehicle entity : list) {
				entity.poolFree();
			}
			list.clear();
		}
	}

	/**
	 * Shows or hides the specified segment.
	 *
	 * @param index segment index
	 * @param show If true, show the segment, else hide it.
	 */
	public void showSegment(int index, boolean show) {
		if (index < 0)
			return;

		if (index >= waypointMarkers.size())
			return;

		if (show) {
			sendFakeEntity(waypointMarkers.get(index));
			for (FakeEntity lineMarker : lineMarkers.get(index)) {
				sendFakeEntity(lineMarker);
			}
		}
		else {
			waypointMarkers.get(index).delete();
			for (FakeEntity lineMarker : lineMarkers.get(index)) {
				lineMarker.delete();
			}
		}
	}

	/**
	 * Refreshes the specified segment range
	 *
	 * @param startIndex The index at which to start refreshing
	 * @param oldAmount The amount of segments to remove
	 * @param newAmount The amount of segments to insert
	 */
	public void refresh(int startIndex, int oldAmount, int newAmount) {
		while (startIndex < 0) {
			++startIndex;
			--oldAmount;
			--newAmount;
		}

		removeEntities(startIndex, oldAmount);

		while (oldAmount < newAmount) {
			++oldAmount;
			waypointMarkers.add(startIndex, null);
			lineMarkers.add(startIndex, new ArrayList<FakeVehicle>());
		}

		while (oldAmount > newAmount) {
			--oldAmount;
			waypointMarkers.remove(startIndex);
			lineMarkers.remove(startIndex);
		}

		createEntities(startIndex, newAmount);

		clearFakeVehiclePools();
	}

	private void sendFakeEntity(final FakeEntity fakeEntity) {
		if (player == null) {
			fakeEntity.send();
		}
		else {
			fakeEntity.send(player);
		}
	}
}
