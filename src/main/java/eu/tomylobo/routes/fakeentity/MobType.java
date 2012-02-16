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

public enum MobType {
	CREEPER(50),
	SKELETON(51),
	SPIDER(52),
	GIANT(53),
	ZOMBIE(54),
	SLIME(55),
	GHAST(56),
	PIG_ZOMBIE(57),
	ENDERMAN(58),
	CAVE_SPIDER(59),
	SILVERFISH(60),
	BLAZE(61),
	LAVA_SLIME(62),
	ENDER_DRAGON(63, 180),
	PIG(90),
	SHEEP(91),
	COW(92),
	CHICKEN(93),
	SQUID(94),
	WOLF(95),
	MUSHROOM_COW(96),
	SNOW_MAN(97),
	VILLAGER(120);

	private final int id;
	private final float yawOffset;

	private MobType(int id, float yawOffset) {
		this.id = id;
		this.yawOffset = yawOffset;
	}

	private MobType(int id) {
		this(id, 0);
	}

	public int getId() {
		return id;
	}

	public float getYawOffset() {
		return yawOffset;
	}
}
