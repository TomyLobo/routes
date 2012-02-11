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
import org.junit.*;
import static org.junit.Assert.*;

public class UtilsTest {
	private static final double DELTA = 1e-15;

	@Test
	public void locationFromEyeTest() {
		final World world = null;
		final Vector position = new Vector();

		for (float yaw = -180; yaw <= 180; ++yaw) {
			for (float pitch = -90; pitch <= 90; ++pitch) {
				Location location = position.toLocation(world, yaw, pitch);
				Location location2 = Utils.locationFromEye(world, position, location.getDirection());

				assertEquals("yaw for "   + yaw + "/" + pitch, yaw  , location2.getYaw()  , DELTA);
				assertEquals("pitch for " + yaw + "/" + pitch, pitch, location2.getPitch(), DELTA);
			}
		}
	}
}
