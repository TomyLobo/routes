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

package eu.tomylobo.math;

import org.junit.*;

import eu.tomylobo.abstraction.World;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import static org.junit.Assert.*;

public class MathTest {
	private static final double DELTA = 1e-15;

	@Test
	public void locationFromEyeTest() {
		final World world = null;
		final Vector position = new Vector(0, 0, 0);

		for (float yaw = -180; yaw <= 180; ++yaw) {
			for (float pitch = -90; pitch <= 90; ++pitch) {
				Location location = new Location(world, position, yaw, pitch);
				Location location2 = Location.fromEye(world, position, location.getDirection());

				assertEquals("yaw for "   + yaw + "/" + pitch, yaw  , location2.getYaw()  , DELTA);
				assertEquals("pitch for " + yaw + "/" + pitch, pitch, location2.getPitch(), DELTA);
			}
		}
	}
}
