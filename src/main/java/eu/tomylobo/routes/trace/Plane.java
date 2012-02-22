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

package eu.tomylobo.routes.trace;

import org.bukkit.util.Vector;

public class Plane extends AbstractShape {
	private final Vector origin;
	private final Vector normal;

	public Plane(Vector origin, Vector normal) {
		this.origin = origin;
		this.normal = normal;
	}

	@Override
	public TraceResult trace(Vector start, Vector direction) {
		final double numerator = origin.clone().subtract(start).dot(normal);
		final double denominator = direction.dot(normal);

		if (denominator == 0)
			return null;

		final double t = numerator / denominator;
		final Vector position = direction.clone().multiply(t).add(start);

		Vector localPosition = position.clone().subtract(origin);
		return new TraceResult(t, position, localPosition);
	}

	/*
	I  (p-origin)*normal = 0                         | plane equation
	II p=t*direction+start                           | ray equation

	=> plug II into I =>

	(t*direction+start-origin)*normal = 0            | split up the dot product
	t*direction*normal+(start-origin)*normal = 0     | - (start-origin)*normal
	t*direction*normal = -(start-origin)*normal
	t*direction*normal = (origin-start)*normal       | / (direction*normal)
	t = ((origin-start)*normal) / (direction*normal) |
	*/
}
