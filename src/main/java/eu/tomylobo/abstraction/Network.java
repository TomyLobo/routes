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

import java.util.Collection;

import eu.tomylobo.abstraction.block.Sign;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.fakeentity.EntityType;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.fakeentity.VehicleType;

public interface Network {
	//void sendEffect(Player player, Entity entity, EntityEffect effect);
	void sendEffect(Player player, int entityId, byte effectData);
	//void sendEffect(Collection<Player> players, Entity entity, EntityEffect effect);
	void sendEffect(Collection<Player> players, int entityId, byte effectData);

	void sendSetData(Player player, Entity entity, int index, Object value);
	void sendSetData(Player player, int entityId, int index, Object value);
	void sendSetData(Collection<Player> players, Entity entity, int index, Object value);
	void sendSetData(Collection<Player> players, int entityId, int index, Object value);

	void sendTeleport(Player player, Entity entity, Location location);
	void sendTeleport(Player player, int entityId, double x, double y, double z, float yaw, float pitch);
	void sendTeleport(Collection<Player> players, Entity entity, Location location);
	void sendTeleport(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch);

	void sendVelocity(Player player, Entity entity, Vector velocity);
	void sendVelocity(Player player, int entityId, double x, double y, double z);
	void sendVelocity(Collection<Player> players, Entity entity, Vector velocity);
	void sendVelocity(Collection<Player> players, int entityId, double x, double y, double z);

	void sendOrientation(Player player, Entity entity, Location location);
	void sendOrientation(Player player, int entityId, float yaw, float pitch);
	void sendOrientation(Collection<Player> players, Entity entity, Location location);
	void sendOrientation(Collection<Player> players, int entityId, float yaw, float pitch);

	void sendAttachToVehicle(Player player, Entity passenger, Entity vehicle);
	void sendAttachToVehicle(Player player, int passengerId, int vehicleId);
	void sendAttachToVehicle(Collection<Player> players, Entity passenger, Entity vehicle);
	void sendAttachToVehicle(Collection<Player> players, int passengerId, int vehicleId);

	void sendDestroyEntity(Player player, Entity entity);
	void sendDestroyEntity(Player player, int entityId);
	void sendDestroyEntity(Collection<Player> players, Entity entity);
	void sendDestroyEntity(Collection<Player> players, int entityId);

	void sendSpawn(Player player, Entity entity, Location location, EntityType entityType);
	void sendSpawnMob(Player player, Entity entity, Location location, MobType otherType);
	void sendSpawnMob(Player player, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId);
	void sendSpawnOther(Player player, Entity entity, Location location, VehicleType otherType);
	void sendSpawnOther(Player player, int entityId, double x, double y, double z, int typeId, int dataValue);
	void sendSpawn(Collection<Player> players, Entity entity, Location location, EntityType entityType);
	void sendSpawnMob(Collection<Player> players, Entity entity, Location location, MobType otherType);
	void sendSpawnMob(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId);
	void sendSpawnOther(Collection<Player> players, Entity entity, Location location, VehicleType otherType);
	void sendSpawnOther(Collection<Player> players, int entityId, double x, double y, double z, int typeId, int dataValue);

	void sendSignUpdate(Player player, Vector position, Sign signState);
	void sendSignUpdate(Player player, Vector position, String[] lines);
	void sendSignUpdate(Player player, int x, int y, int z, String[] lines);
	void sendSignUpdate(Collection<Player> players, Vector position, Sign signState);
	void sendSignUpdate(Collection<Player> players, Vector position, String[] lines);
	void sendSignUpdate(Collection<Player> players, int x, int y, int z, String[] lines);

	void sendPlayerPosition(Player player, Vector position);
	void sendPlayerPosition(Player player, double x, double y, double z);
}
