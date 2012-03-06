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

package eu.tomylobo.routes.travel;

import eu.tomylobo.abstraction.Entity;
import eu.tomylobo.math.Location;
import eu.tomylobo.routes.infrastructure.Route;

/**
 * A Traveller is an {@link #entity} travelling along a {@link #route}
 * at the specified speed.<br />
 * At the end of the route, a {@link #finalizer} is called.
 *
 * @author TomyLobo
 *
 */
public class Traveller {
	private final Route route;
	private final Entity entity;
	private double increment;
	private Runnable finalizer;

	private double position = 0.0;

	/**
	 * Constructs a traveller
	 *
	 * @param route The route to travel on
	 * @param entity An entity to move along the route
	 * @param speed Speed in m/s
	 * @param finalizer A Runnable to invoke after the route was finished
	 */
	public Traveller(Route route, Entity entity, double speed, Runnable finalizer) {
		this.route = route;
		this.entity = entity;
		this.increment = speed * 0.05 / route.length();
		this.finalizer = finalizer;
	}

	int index = 0;
	public boolean tick() {
		position += increment;

		final Location location = route.getLocation(position);
		if (location == null) {
			runFinalizer();
			return false;
		}

		entity.teleport(location);
		entity.setVelocity(route.getVelocity(position).multiply(increment));

		++index;
		return true;
	}

	public void runFinalizer() {
		if (finalizer != null) {
			finalizer.run();
		}
	}

	public Entity getEntity() {
		return entity;
	}

	public Route getRoute() {
		return route;
	}
}
