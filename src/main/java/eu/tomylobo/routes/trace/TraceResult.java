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

import eu.tomylobo.math.Vector;

/**
 * Contains the result of a trace.
 *
 * @author TomyLobo
 *
 */
public class TraceResult {
	/**
	 * position = start + t*direction
	 */
	public final double t;

	/**
	 * The position where the ray intersects with the shape.
	 */
	public final Vector position;

	/**
	 * The position where the ray intersects with the shape, relative to the shape's origin.
	 */
	public final Vector relativePosition;

	public TraceResult(double t, Vector position, Vector relativePosition) {
		this.t = t;
		this.position = position;
		this.relativePosition = relativePosition;
	}
}
