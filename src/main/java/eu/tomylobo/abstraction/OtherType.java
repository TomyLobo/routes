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

package eu.tomylobo.abstraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.EntityType;

public enum OtherType {
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
	ENDER_SIGNAL(72, 0.25f),
	SPLASH_POTION(73, 0.25f),
	FALLING_DRAGON_EGG(74, 0.98f),
	FISHING_HOOK(90, 0.25f);

	private final int id;
	private final float height;
	private final float yawOffset;

	private OtherType(int id) {
		this(id, 1.8f);
	}

	private OtherType(int id, float height) {
		this(id, height, 0);
	}

	private OtherType(int id, float height, float yawOffset) {
		this.id = id;
		this.height = height;
		this.yawOffset = yawOffset;
	}

	public int getId() {
		return id;
	}

	public float getHeight() {
		return height;
	}

	public float getYawOffset() {
		return yawOffset;
	}

	public EntityType toEntityType() {
		return otherTypeToEntityType.get(this);
	}

	private static final Map<EntityType, OtherType> entityTypeToOtherType = new HashMap<EntityType, OtherType>();
	private static final Map<OtherType, EntityType> otherTypeToEntityType = new HashMap<OtherType, EntityType>();
	private static final Map<Integer, OtherType> otherIdToOtherType = new HashMap<Integer, OtherType>();
	static {
		entityTypeToOtherType.put(EntityType.BOAT, BOAT);
		entityTypeToOtherType.put(EntityType.MINECART, MINECART); // not 1:1
		entityTypeToOtherType.put(EntityType.PRIMED_TNT, PRIMED_TNT);
		entityTypeToOtherType.put(EntityType.ENDER_CRYSTAL, ENDER_CRYSTAL);
		entityTypeToOtherType.put(EntityType.ARROW, ARROW);
		entityTypeToOtherType.put(EntityType.SNOWBALL, SNOWBALL);
		entityTypeToOtherType.put(EntityType.EGG, EGG);
		entityTypeToOtherType.put(EntityType.FIREBALL, FIREBALL);
		entityTypeToOtherType.put(EntityType.SMALL_FIREBALL, SMALL_FIREBALL);
		entityTypeToOtherType.put(EntityType.ENDER_PEARL, ENDER_PEARL);
		entityTypeToOtherType.put(EntityType.FALLING_BLOCK, FALLING_SAND); // not 1:1
		entityTypeToOtherType.put(EntityType.ENDER_SIGNAL, ENDER_SIGNAL);
		entityTypeToOtherType.put(EntityType.SPLASH_POTION, SPLASH_POTION);
		entityTypeToOtherType.put(EntityType.FISHING_HOOK, FISHING_HOOK);

		for (Entry<EntityType, OtherType> entry : entityTypeToOtherType.entrySet()) {
			otherTypeToEntityType.put(entry.getValue(), entry.getKey());
			otherIdToOtherType.put(entry.getValue().id, entry.getValue());
		}

		otherTypeToEntityType.put(MINECART_CHEST, EntityType.MINECART);
		otherTypeToEntityType.put(MINECART_FURNACE, EntityType.MINECART);
		otherTypeToEntityType.put(FALLING_GRAVEL, EntityType.FALLING_BLOCK);
		otherTypeToEntityType.put(FALLING_DRAGON_EGG, EntityType.FALLING_BLOCK);
	}

	public static OtherType fromEntityType(EntityType entityType) {
		return entityTypeToOtherType.get(entityType);
	}

	public static OtherType fromId(int id) {
		return otherIdToOtherType.get(id);
	}
}
