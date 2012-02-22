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

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

public class SignShape extends Plane {
	public SignShape(Sign sign) {
		this(getOriginLocation(sign));
	}

	private SignShape(Location originLocation) {
		super(originLocation.toVector(), originLocation.getDirection());
	}

	private static final double signScale = 2.0 / 3.0;
	private static Location getOriginLocation(Sign sign) {
		final Location originLocation = sign.getBlock().getLocation().add(0.5, 0.75*signScale, 0.5); // TODO: Remove ".getBlock()" once the patch got pulled.

		double yOffset = 0.5 * signScale;
		double zOffset = 0.07 * signScale;

		switch (sign.getType()) {
		case SIGN_POST:
			originLocation.setYaw((sign.getRawData() * 360) / 16f);
			break;

		case WALL_SIGN:
			switch (sign.getRawData()) {
			case 2:
				originLocation.setYaw(180);
				break;

			case 4:
				originLocation.setYaw(90);
				break;

			case 5:
				originLocation.setYaw(-90);
				break;

			}

			yOffset -= 0.3125;
			zOffset -= 0.4375;
			break;

		default:
			throw new IllegalArgumentException("Expected a sign, got something else.");
		}

		final Vector normal = originLocation.getDirection();
		originLocation.add(0, yOffset, 0);
		originLocation.add(normal.clone().multiply(zOffset));

		return originLocation;
	}
}
