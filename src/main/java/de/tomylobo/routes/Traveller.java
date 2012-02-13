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

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class Traveller {
	@SuppressWarnings("unused")
	private final TravelAgency travelAgent;
	private final Entity entity;
	private final Route route;
	private double position = 0.0;
	private double increment;
	private Runnable finalizer;

	public Traveller(TravelAgency travelAgent, Entity entity, Route route, Runnable finalizer) {
		this.travelAgent = travelAgent;
		this.entity = entity;
		this.route = route;
		this.increment = 0.02 / route.getNodes().size();
		this.finalizer = finalizer;
	}

	public boolean tick() {
		position += increment;

		Location location = route.getLocation(position);
		if (location == null) {
			runFinalizer();
			return false;
		}

		entity.teleport(location);
		return true;
	}

	public void runFinalizer() {
		finalizer.run();
	}

	public Entity getEntity() {
		return entity;
	}

	public Route getRoute() {
		return route;
	}
}
