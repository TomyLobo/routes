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

package eu.tomylobo.abstraction.platform.spout;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.spout.api.inventory.ItemStack;
import org.spout.api.math.MathHelper;
import org.spout.api.protocol.Message;
import org.spout.api.util.Parameter;
import org.spout.vanilla.protocol.msg.AttachEntityMessage;
import org.spout.vanilla.protocol.msg.DestroyEntityMessage;
import org.spout.vanilla.protocol.msg.EntityMetadataMessage;
import org.spout.vanilla.protocol.msg.EntityRotationMessage;
import org.spout.vanilla.protocol.msg.EntityStatusMessage;
import org.spout.vanilla.protocol.msg.EntityTeleportMessage;
import org.spout.vanilla.protocol.msg.EntityVelocityMessage;
import org.spout.vanilla.protocol.msg.PlayerPositionMessage;
import org.spout.vanilla.protocol.msg.SpawnMobMessage;
import org.spout.vanilla.protocol.msg.SpawnVehicleMessage;
import org.spout.vanilla.protocol.msg.UpdateSignMessage;

import eu.tomylobo.abstraction.Network;
import eu.tomylobo.abstraction.block.Sign;
import eu.tomylobo.abstraction.entity.Entity;
import eu.tomylobo.abstraction.entity.EntityType;
import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public class SpoutNetwork implements Network {
	private EntityStatusMessage createEffectPacket(int entityId, byte effectData) {
		return new EntityStatusMessage(entityId, effectData);
	}
/*
	@Override
	public void sendEffect(Player player, Entity entity, EntityEffect effect) {
		sendEffect(player, entity.getEntityId(), effect.getData());
	}
*/
	@Override
	public void sendEffect(Player player, int entityId, byte effectData) {
		sendPacket(player, createEffectPacket(entityId, effectData));
	}
/*
	@Override
	public void sendEffect(Collection<Player> players, Entity entity, EntityEffect effect) {
		sendEffect(players, entity.getEntityId(), effect.getData());
	}
*/
	@Override
	public void sendEffect(Collection<Player> players, int entityId, byte effectData) {
		sendPacket(players, createEffectPacket(entityId, effectData));
	}


	private EntityMetadataMessage createSetDataPacket(int entityId, int index, Object value) {
		final int type;

		if (value instanceof Byte) {
			type = Parameter.TYPE_BYTE;
		}
		else if (value instanceof Short) {
			type = Parameter.TYPE_SHORT;
		}
		else if (value instanceof Integer) {
			type = Parameter.TYPE_INT;
		}
		else if (value instanceof Float) {
			type = Parameter.TYPE_FLOAT;
		}
		else if (value instanceof String) {
			type = Parameter.TYPE_STRING;
		}
		else if (value instanceof ItemStack) {
			type = Parameter.TYPE_ITEM;
		}
		else {
			throw new RuntimeException("Cannot use data type");
		}

		Parameter<?> parameter = new Parameter<Object>(type, index, value);
		List<Parameter<?>> parameters = Collections.<Parameter<?>>singletonList(parameter);

		return new EntityMetadataMessage(entityId, parameters);
	}

	@Override
	public void sendSetData(Player player, Entity entity, int index, Object value) {
		sendSetData(player, entity.getEntityId(), index, value);
	}

	@Override
	public void sendSetData(Player player, int entityId, int index, Object value) {
		sendPacket(player, createSetDataPacket(entityId, index, value));
	}

	@Override
	public void sendSetData(Collection<Player> players, Entity entity, int index, Object value) {
		sendSetData(players, entity.getEntityId(), index, value);
	}

	@Override
	public void sendSetData(Collection<Player> players, int entityId, int index, Object value) {
		sendPacket(players, createSetDataPacket(entityId, index, value));
	}


	private EntityTeleportMessage createTeleportPacket(int entityId, double x, double y, double z, float yaw, float pitch) {
		return new EntityTeleportMessage(
				entityId,
				MathHelper.floor(x*32.0D),
				MathHelper.floor(y*32.0D),
				MathHelper.floor(z*32.0D),
				(int) (yaw * 256.0F / 360.0F),
				(int) (pitch * 256.0F / 360.0F)
		);
	}

	@Override
	public void sendTeleport(Player player, Entity entity, Location location) {
		final Vector position = location.getPosition();
		sendTeleport(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendTeleport(Player player, int entityId, double x, double y, double z, float yaw, float pitch) {
		sendPacket(player, createTeleportPacket(entityId, x, y, z, yaw, pitch));
	}

	@Override
	public void sendTeleport(Collection<Player> players, Entity entity, Location location) {
		final Vector position = location.getPosition();
		sendTeleport(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendTeleport(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch) {
		sendPacket(players, createTeleportPacket(entityId, x, y, z, yaw, pitch));
	}


	private static double clamp(double x, double min, double max) {
		if (x <= min)
			return min;

		if (x >= max)
			return max;

		return x;
	}

	private EntityVelocityMessage createVelocityPacket(int entityId, double x, double y, double z) {
		return new EntityVelocityMessage(
				entityId,
				(int) (clamp(x, -3.9, 3.9) * 8000.0),
				(int) (clamp(y, -3.9, 3.9) * 8000.0),
				(int) (clamp(z, -3.9, 3.9) * 8000.0)
		);
	}

	@Override
	public void sendVelocity(Player player, Entity entity, Vector velocity) {
		sendVelocity(player, entity.getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ());
	}

	@Override
	public void sendVelocity(Player player, int entityId, double x, double y, double z) {
		sendPacket(player, createVelocityPacket(entityId, x, y, z));
	}

	@Override
	public void sendVelocity(Collection<Player> players, Entity entity, Vector velocity) {
		sendVelocity(players, entity.getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ());
	}

	@Override
	public void sendVelocity(Collection<Player> players, int entityId, double x, double y, double z) {
		sendPacket(players, createVelocityPacket(entityId, x, y, z));
	}


	private EntityRotationMessage createOrientationPacket(int entityId, float yaw, float pitch) {
		return new EntityRotationMessage(
				entityId,
				(int) (yaw * 256.0F / 360.0F),
				(int) (pitch * 256.0F / 360.0F)
		);
	}

	@Override
	public void sendOrientation(Player player, Entity entity, Location location) {
		sendOrientation(player, entity.getEntityId(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendOrientation(Player player, int entityId, float yaw, float pitch) {
		sendPacket(player, createOrientationPacket(entityId, yaw, pitch));
	}

	@Override
	public void sendOrientation(Collection<Player> players, Entity entity, Location location) {
		sendOrientation(players, entity.getEntityId(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendOrientation(Collection<Player> players, int entityId, float yaw, float pitch) {
		sendPacket(players, createOrientationPacket(entityId, yaw, pitch));
	}


	private AttachEntityMessage createAttachToVehiclePacket(int passengerId, int vehicleId) {
		return new AttachEntityMessage(passengerId, vehicleId);
	}

	@Override
	public void sendAttachToVehicle(Player player, Entity passenger, Entity vehicle) {
		sendAttachToVehicle(player, passenger.getEntityId(), vehicle == null ? -1 : vehicle.getEntityId());
	}

	@Override
	public void sendAttachToVehicle(Player player, int passengerId, int vehicleId) {
		sendPacket(player, createAttachToVehiclePacket(passengerId, vehicleId));
	}

	@Override
	public void sendAttachToVehicle(Collection<Player> players, Entity passenger, Entity vehicle) {
		sendAttachToVehicle(players, passenger.getEntityId(), vehicle == null ? -1 : vehicle.getEntityId());
	}

	@Override
	public void sendAttachToVehicle(Collection<Player> players, int passengerId, int vehicleId) {
		sendPacket(players, createAttachToVehiclePacket(passengerId, vehicleId));
	}


	private DestroyEntityMessage createDestroyPacket(int entityId) {
		return new DestroyEntityMessage(entityId);
	}

	@Override
	public void sendDestroyEntity(Player player, Entity entity) {
		sendDestroyEntity(player, entity.getEntityId());
	}

	@Override
	public void sendDestroyEntity(Player player, int entityId) {
		sendPacket(player, createDestroyPacket(entityId));
	}

	@Override
	public void sendDestroyEntity(Collection<Player> players, Entity entity) {
		sendDestroyEntity(players, entity.getEntityId());
	}

	@Override
	public void sendDestroyEntity(Collection<Player> players, int entityId) {
		sendPacket(players, createDestroyPacket(entityId));
	}


	private SpawnMobMessage createSpawnMobPacket(int entityId, double x, double y, double z, float yaw, float pitch, float headYaw, int creatureTypeId) {
		final List<Parameter<?>> parameters = Collections.emptyList();
		return new SpawnMobMessage(
				entityId, creatureTypeId,
				MathHelper.floor(x * 32.0D),
				MathHelper.floor(y * 32.0D),
				MathHelper.floor(z * 32.0D),
				(int) (yaw * 256.0F / 360.0F),
				(int) (pitch * 256.0F / 360.0F),
				(int) (headYaw * 256.0F / 360.0F),
				parameters
		);
	}

	private SpawnVehicleMessage createSpawnOtherPacket(int entityId, double x, double y, double z, int typeId, int dataValue) {
		return new SpawnVehicleMessage(
				entityId, typeId,
				MathHelper.floor(x * 32.0D),
				MathHelper.floor(y * 32.0D),
				MathHelper.floor(z * 32.0D),
				dataValue,
				0, 0, 0
		);
	}


	@Override
	public void sendSpawn(Player player, Entity entity, Location location, EntityType entityType) {
		if (entityType instanceof MobType) {
			sendSpawnMob(player, entity, location, (MobType) entityType);
		}
		else {
			sendSpawnOther(player, entity, location, (VehicleType) entityType);
		}
	}

	@Override
	public void sendSpawnMob(Player player, Entity entity, Location location, MobType mobType) {
		Vector position = location.getPosition();
		sendSpawnMob(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch(), mobType.getId());
	}

	@Override
	public void sendSpawnMob(Player player, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId) {
		sendPacket(player, createSpawnMobPacket(entityId, x, y, z, yaw, pitch, 0, creatureTypeId));
	}

	@Override
	public void sendSpawnOther(Player player, Entity entity, Location location, VehicleType otherType) {
		final Vector position = location.getPosition();
		sendSpawnOther(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), otherType.getId(), 0);
	}

	@Override
	public void sendSpawnOther(Player player, int entityId, double x, double y, double z, int typeId, int dataValue) {
		sendPacket(player, createSpawnOtherPacket(entityId, x, y, z, typeId, dataValue));
	}

	@Override
	public void sendSpawn(Collection<Player> players, Entity entity, Location location, EntityType entityType) {
		if (entityType instanceof MobType) {
			sendSpawnMob(players, entity, location, (MobType) entityType);
		}
		else {
			sendSpawnOther(players, entity, location, (VehicleType) entityType);
		}
	}

	@Override
	public void sendSpawnMob(Collection<Player> players, Entity entity, Location location, MobType mobType) {
		Vector position = location.getPosition();
		sendSpawnMob(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch(), mobType.getId());
	}

	@Override
	public void sendSpawnMob(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId) {
		sendPacket(players, createSpawnMobPacket(entityId, x, y, z, yaw, pitch, 0, creatureTypeId));
	}

	@Override
	public void sendSpawnOther(Collection<Player> players, Entity entity, Location location, VehicleType otherType) {
		final Vector position = location.getPosition();
		sendSpawnOther(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), otherType.getId(), 0);
	}

	@Override
	public void sendSpawnOther(Collection<Player> players, int entityId, double x, double y, double z, int typeId, int dataValue) {
		sendPacket(players, createSpawnOtherPacket(entityId, x, y, z, typeId, dataValue));
	}


	private UpdateSignMessage createSignUpdatePacket(int x, int y, int z, String[] lines) {
		return new UpdateSignMessage(x, y, z, lines);
	}

	@Override
	public void sendSignUpdate(Player player, Vector position, Sign signState) {
		sendSignUpdate(player, position, signState.getLines());
	}

	@Override
	public void sendSignUpdate(Player player, Vector position, String[] lines) {
		sendSignUpdate(player, (int) position.getX(), (int) position.getY(), (int) position.getZ(), lines);
	}

	@Override
	public void sendSignUpdate(Player player, int x, int y, int z, String[] lines) {
		sendPacket(player, createSignUpdatePacket(x, y, z, lines));
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, Vector position, Sign signState) {
		sendSignUpdate(players, position, signState.getLines());
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, Vector position, String[] lines) {
		sendSignUpdate(players, (int) position.getX(), (int) position.getY(), (int) position.getZ(), lines);
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, int x, int y, int z, String[] lines) {
		sendPacket(players, createSignUpdatePacket(x, y, z, lines));
	}


	private PlayerPositionMessage createPlayerPositionPacket(double x, double y, double z) {
		final double stance = y;
		y += 1.62;
		return new PlayerPositionMessage(x, y, z, stance, false);
	}

	@Override
	public void sendPlayerPosition(Player player, Vector position) {
		sendPlayerPosition(player, position.getX(), position.getY(), position.getZ());
	}

	@Override
	public void sendPlayerPosition(Player player, double x, double y, double z) {
		sendPacket(player, createPlayerPositionPacket(x, y, z));
	}



	private static void sendPacket(Collection<Player> players, Message message) {
		for (Player player : players) {
			sendPacket(player, message);
		}
	}

	private static void sendPacket(final Player player, final Message message) {
		SpoutUtils.unwrap(player).getSession().send(false, message);
	}
}
