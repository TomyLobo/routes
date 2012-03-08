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

import eu.tomylobo.abstraction.block.Sign;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.fakeentity.FakeVehicle;

public class SignShape extends Plane {
	private static final double SIGN_SCALE = 2.0 / 3.0;
	private static final double FONT_SCALE = SIGN_SCALE / 60.0;

	public SignShape(Location location) {
		this(getOriginLocation(location), true);
	}

	private SignShape(Location originLocation, boolean dummy) {
		super(originLocation.getPosition(), originLocation.getDirection());
	}

	private static Location getOriginLocation(Location location) {
		Sign sign = (Sign) location.getBlockState();
		Location originLocation = location.add(0.5, 0.75*SIGN_SCALE, 0.5);

		double yOffset = 0.5 * SIGN_SCALE;
		double zOffset = 0.07 * SIGN_SCALE;

		float yaw = 0;
		if (sign.isWallSign()) {
			switch (sign.getData()) {
			case 2:
				yaw = 180;
				break;

			case 4:
				yaw = 90;
				break;

			case 5:
				yaw = -90;
				break;

			}

			yOffset -= 0.3125;
			zOffset -= 0.4375;
		}
		else {
			yaw = (sign.getData() * 360) / 16f;
		}

		originLocation = originLocation.setAngles(yaw, 0);

		final Vector normal = originLocation.getDirection();
		originLocation = originLocation.add(0, yOffset, 0).add(normal.multiply(zOffset));

		new FakeVehicle(originLocation, VehicleType.ENDER_EYE).send();

		return originLocation;
	}


	@Override
	public SignTraceResult trace(Location location) {
		return augument(super.trace(location));
	}

	@Override
	public SignTraceResult trace(Vector start, Vector direction) {
		return augument(super.trace(start, direction));
	}

	@Override
	public SignTraceResult traceToPoint(Vector start, Vector end) {
		return augument(super.traceToPoint(start, end));
	}

	private SignTraceResult augument(TraceResult trace) {
		final int index = (int) Math.floor(2.1 - trace.relativePosition.getY() / FONT_SCALE / 10.0);

		return new SignTraceResult(trace, index);
	}
}
