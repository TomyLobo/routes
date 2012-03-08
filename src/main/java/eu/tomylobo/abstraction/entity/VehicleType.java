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

package eu.tomylobo.abstraction.entity;

import eu.tomylobo.routes.fakeentity.FakeVehicle;

/**
 * Types of mobs, for use with {@link FakeVehicle}.
 *
 * @author TomyLobo
 *
 */
public enum VehicleType implements EntityType {
	BOAT(1, 0.6f, 270),
	MINECART(10, 0.7f, 270),
	MINECART_CHEST(11, 0.7f, 270),
	MINECART_FURNACE(12, 0.7f, 270),
	PRIMED_TNT(50, 0.98f),
	ENDER_CRYSTAL(51, 2f),
	ARROW(60, 0.5f),
	SNOWBALL(61, 0.25f),
	EGG(62, 0.25f),
	FIREBALL(63, 1f),
	SMALL_FIREBALL(64, 0.3125f),
	ENDER_PEARL(65, 0.25f),
	FALLING_SAND(70, 0.98f),
	FALLING_GRAVEL(71, 0.98f),
	ENDER_EYE(72, 0.25f),
	POTION(73, 0.25f),
	FALLING_DRAGON_EGG(74, 0.98f),
	FISHING_HOOK(90, 0.25f);

	private final int id;
	private final float height;
	private final float yawOffset;

	private VehicleType(int id) {
		this(id, 1.8f);
	}

	private VehicleType(int id, float height) {
		this(id, height, 0);
	}

	private VehicleType(int id, float height, float yawOffset) {
		this.id = id;
		this.height = height;
		this.yawOffset = yawOffset;
	}

	public int getId() {
		return id;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getYawOffset() {
		return yawOffset;
	}
}
