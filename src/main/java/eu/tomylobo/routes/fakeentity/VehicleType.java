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

package eu.tomylobo.routes.fakeentity;

/**
 * Types of mobs, for use with {@link FakeVehicle}.
 *
 * @author TomyLobo
 *
 */
public enum VehicleType {
	BOAT(1, 270),
	MINECART(10, 270),
	MINECART_CHEST(11, 270),
	MINECART_FURNACE(12, 270),
	PRIMED_TNT(50),
	ENDER_CRYSTAL(51),
	ARROW(60),
	SNOWBALL(61),
	EGG(62),
	FIREBALL(63),
	SMALL_FIREBALL(64),
	ENDER_PEARL(65),
	FALLING_SAND(70),
	FALLING_GRAVEL(71),
	ENDER_EYE(72),
	POTION(73),
	FALLING_DRAGON_EGG(74),
	FISHING_HOOK(90);

	private final int id;
	private final float yawOffset;

	private VehicleType(int id, float yawOffset) {
		this.id = id;
		this.yawOffset = yawOffset;
	}

	private VehicleType(int id) {
		this(id, 0);
	}

	public int getId() {
		return id;
	}

	public float getYawOffset() {
		return yawOffset;
	}
}
