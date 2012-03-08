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

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Entity;
import eu.tomylobo.abstraction.entity.EntityType;
import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A client-side-only entity.
 * 
 * This class only implements part of the bukkit Entity interface and doesn't
 * simulate movement beyond what is explicitly set.
 * 
 * @author TomyLobo
 *
 */
public abstract class FakeEntity implements Entity {
	static int lastFakeEntityId = 1000000000;

	public final int entityId;
	public Location location;
	private boolean isDead;
	private float height;
	protected final float yawOffset;
	private Entity passenger;
	private Map<Entity, Double> fakePassengers = new HashMap<Entity, Double>();
	private Set<Player> relevantPlayers = new HashSet<Player>();

	public static FakeEntity create(Location location, EntityType entityType) {
		if (entityType instanceof MobType)
			return new FakeMob(location, (MobType) entityType);

		if (entityType instanceof VehicleType)
			return new FakeVehicle(location, (VehicleType) entityType);

		throw new IllegalArgumentException("Unknown entity type");
	}

	protected FakeEntity(Location location, EntityType entityType) {
		if (location == null)
			throw new IllegalArgumentException("A null Location was passed to the FakeEntity ctor.");

		entityId = ++lastFakeEntityId;
		this.location = location;
		this.height = entityType.getHeight();
		this.yawOffset = entityType.getYawOffset();
	}

	public void send() {
		for (Player player : location.getWorld().getPlayers()) {
			send(player);
		}
	}

	public void send(Player player) {
		sendImplementation(player);
		setPassenger(passenger);
		relevantPlayers.add(player);
	}

	abstract public void sendImplementation(Player player);

	public final void delete() {
		Environment.network().sendDestroyEntity(relevantPlayers, entityId);
		relevantPlayers.clear();
	}

	public final void delete(Player player) {
		Environment.network().sendDestroyEntity(player, entityId);
		relevantPlayers.remove(player);
	}

	@Override
	public void setVelocity(Vector velocity) {
		Environment.network().sendVelocity(relevantPlayers, this, velocity);

		for (Entry<Entity, Double> entry : fakePassengers.entrySet()) {
			entry.getKey().setVelocity(velocity);
		}
	}

	public boolean setOrientation(Location location) {
		this.location = this.location.setAngles(location.getYaw(), location.getPitch());

		Environment.network().sendOrientation(
				relevantPlayers, entityId,
				location.getYaw()+yawOffset, location.getPitch()
		);

		return true;
	}

	@Override
	public void teleport(Location location) {
		teleport(location, true, true);
	}

	@Override
	public void teleport(Location location, boolean withAngles, boolean notify) {
		this.location = location;

		final Vector position = location.getPosition();
		Environment.network().sendTeleport(
				relevantPlayers, entityId,
				position.getX(), position.getY(), position.getZ(),
				location.getYaw()+yawOffset, location.getPitch()
		);

		if (passenger != null) {
			passenger.teleport(location.add(0, getMountedYOffset(), 0), false, false);
		}

		for (Entry<Entity, Double> entry : fakePassengers.entrySet()) {
			entry.getKey().teleport(location.add(0, entry.getValue(), 0));
		}
	}

	@Override
	public Location getLocation() {
		return location;
	}
/*
	@Override
	public Vector getVelocity() {
		return new Vector();
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}
*/
	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public void remove() {
		delete();
		isDead = true;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}
/*
	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}
*/
	@Override
	public Entity getPassenger() {
		return passenger;
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		if (passenger == null)  {
			if (this.passenger == null)
				return true;

			Environment.network().sendAttachToVehicle(relevantPlayers, this.passenger, null);
		}
		else {
			Environment.network().sendAttachToVehicle(relevantPlayers, passenger, this);
		}

		this.passenger = passenger;

		return true;
	}
/*
	@Override
	public boolean isEmpty() {
		return getPassenger() == null;
	}

	@Override
	public boolean eject() {
		return setPassenger(null);
	}
*/
	public void addFakePassenger(Entity entity, double yOffset) {
		fakePassengers.put(entity, yOffset);
	}

	public void removeFakePassenger(Entity entity) {
		fakePassengers.remove(entity);
	}

/*
	public byte getDataByte(int index) {
		try {
			return datawatcher.getByte(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public int getDataInteger(int index) {
		try {
			return datawatcher.getInt(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public String getDataString(int index) {
		try {
			return datawatcher.getString(index);
		}
		catch (NullPointerException e) {
			return null;
		}
	}
*/
	public void setData(int index, Object value) {
		Environment.network().sendSetData(relevantPlayers, entityId, index, value);
	}

	public double getMountedYOffset() {
		return height * 0.75;
	}
}
