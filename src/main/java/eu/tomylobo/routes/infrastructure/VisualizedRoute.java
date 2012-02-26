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

package eu.tomylobo.routes.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.util.Statistics;

public class VisualizedRoute {
	private final List<FakeEntity> visualizationEntities;

	public VisualizedRoute(Route route, double pointsPerMeter) {
		visualizationEntities = new ArrayList<FakeEntity>();

		int points = (int) Math.ceil(pointsPerMeter * route.length());

		double lastPosition = -1;
		final Statistics stats = new Statistics();

		for (int i = 0; i < points; ++i) {
			final double position = ((double) i) / points;
			final Location location = route.getLocation(position);

			// begin statistics
			if (lastPosition != -1) {
				final double distance = route.getArcLength(lastPosition, position);

				stats.stat(distance);
			}
			lastPosition = position;
			// end statistics

			final FakeEntity a = new FakeVehicle(location, VehicleType.ENDER_EYE);
			a.send();

			visualizationEntities.add(a);
		}

		for (Node node : route.getNodes()) {
			final FakeEntity a = new FakeVehicle(node.getPosition().toLocation(route.getWorld()), VehicleType.ENDER_CRYSTAL);
			a.send();

			visualizationEntities.add(a);
		}

		System.out.println(stats.format());
	}

	public void removeEntities() {
		for (FakeEntity entity : visualizationEntities) {
			entity.remove();
		}
	}
}
