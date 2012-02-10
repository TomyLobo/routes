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

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Traveller {
	private final TravelAgency travelAgent;
	private final Entity entity;
	private final Route route;
	private double position = 0.0;

	public Traveller(TravelAgency travelAgent, Entity entity, Route route) {
		this.travelAgent = travelAgent;
		this.entity = entity;
		this.route = route;
	}

	public void tick() {
		position += 0.02;
		if (position >= route.getNodes().size()-1) {
			travelAgent.travellers.remove(entity);
			entity.remove();
			return;
		}

		int index1 = (int) Math.floor(position);
		int index2 = index1 + 1;
		double remainder = position - index1;

		List<Node> nodes = route.getNodes();

		Location location1 = nodes.get(index1).getLocation().clone();
		Location location2 = nodes.get(index2).getLocation().clone();

		location1.multiply(1.0 - remainder);
		location2.multiply(remainder);

		Location location = location1.add(location2);

		entity.teleport(location);
	}

	public Entity getEntity() {
		return entity;
	}

	public Route getRoute() {
		return route;
	}
}
