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

import org.bukkit.util.Vector;

public class LinearInterpolation implements Interpolation {
	@Override
	public Vector getPosition(double position, List<Node> nodes) {
		if (position >= 1)
			return null;

		position *= nodes.size() - 1;

		final int index1 = (int) Math.floor(position);
		final double remainder = position - index1;

		final Vector position1 = nodes.get(index1).getPosition().clone();
		final Vector position2 = nodes.get(index1 + 1).getPosition().clone();

		return position1.multiply(1.0 - remainder).add(position2.multiply(remainder));
	}
}
