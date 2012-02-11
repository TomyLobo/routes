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
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Utils {
	public static Location locationFromLookAt(World world, Vector start, Vector lookAt) {
		final Vector diff = lookAt.clone().subtract(start);

		return locationFromEye(world, start, diff);
	}

	public static Location locationFromEye(World world, Vector start, Vector eye) {
		final double eyeX = eye.getX();
		final double eyeZ = eye.getZ();
		final float yaw = (float) Math.toDegrees(Math.atan2(-eyeX, eyeZ));
		final double length = Math.sqrt(eyeX * eyeX + eyeZ * eyeZ);
		final float pitch = (float) Math.toDegrees(Math.atan2(-eye.getY(), length));

		return start.toLocation(world, yaw, pitch);
	}
}
