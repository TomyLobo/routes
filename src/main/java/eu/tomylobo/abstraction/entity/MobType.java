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

import eu.tomylobo.routes.fakeentity.FakeMob;

/**
 * Types of mobs, for use with {@link FakeMob}.
 *
 * @author TomyLobo
 *
 */
public enum MobType implements EntityType {
	CREEPER(50), // default
	SKELETON(51), // default
	SPIDER(52, 0.9f),
	GIANT(53, 1.8f*6f),
	ZOMBIE(54), // default
	SLIME(55, 0.6f),// * size
	GHAST(56, 4f),
	PIG_ZOMBIE(57), // default
	ENDERMAN(58, 2.9f),
	CAVE_SPIDER(59, 0.6f),
	SILVERFISH(60, 0.7f),
	BLAZE(61), // default
	LAVA_SLIME(62, 0.6f),// * size, like slime
	ENDER_DRAGON(63, 8f, 180),
	PIG(90, 0.9f),
	SHEEP(91, 1.3f),
	COW(92, 1.3f),
	CHICKEN(93, 0.7f),
	SQUID(94, 0.95f),
	WOLF(95, 0.8f),
	MUSHROOM_COW(96, 1.3f),
	SNOW_MAN(97, 1.8f),
	VILLAGER(120); // default

	private final int id;
	private final float height;
	private final float yawOffset;

	private MobType(int id) {
		this(id, 1.8f);
	}

	private MobType(int id, float height) {
		this(id, height, 0);
	}

	private MobType(int id, float height, float yawOffset) {
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
