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

import net.minecraft.server.DataWatcher;
import net.minecraft.server.Packet;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import eu.tomylobo.routes.util.Workarounds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

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

	public static final void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer)ply).getHandle().netServerHandler.sendPacket(packet);
	}

	public void sendPacketToRelevantPlayers(final Packet packet) {
		for (Player player : relevantPlayers) {
			sendPacketToPlayer(player, packet);
		}
	}

	public final int entityId;
	public Location location;
	private boolean isDead;
	private float height;
	protected final float yawOffset;
	protected final DataWatcher datawatcher;
	private Entity passenger;
	private Map<Entity, Double> fakePassengers = new HashMap<Entity, Double>();
	private Set<Player> relevantPlayers = new HashSet<Player>();

	@Override
	public void playEffect(EntityEffect effect) {
		Workarounds.getNetwork().sendEffect(relevantPlayers, entityId, effect.getData());
	}

	public FakeEntity(Location location, EntityType entityType) {
		if (location == null)
			throw new IllegalArgumentException("A null Location was passed to the FakeEntity ctor.");

		entityId = ++lastFakeEntityId;
		this.location = location;
		this.height = entityType.getHeight();
		this.yawOffset = entityType.getYawOffset();
		this.datawatcher = new DataWatcher();
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
		Workarounds.getNetwork().sendDestroyEntity(relevantPlayers, entityId);
		relevantPlayers.clear();
	}

	public final void delete(Player player) {
		Workarounds.getNetwork().sendDestroyEntity(player, entityId);
		relevantPlayers.remove(player);
	}

	@Override
	public void setVelocity(Vector velocity) {
		Workarounds.getNetwork().sendVelocity(relevantPlayers, this, velocity);

		for (Entry<Entity, Double> entry : fakePassengers.entrySet()) {
			entry.getKey().setVelocity(velocity);
		}
	}

	public boolean setOrientation(Location location) {
		this.location.setYaw(location.getYaw());
		this.location.setPitch(location.getPitch());

		Workarounds.getNetwork().sendOrientation(
				relevantPlayers, entityId,
				location.getYaw()+yawOffset, location.getPitch()
		);

		return true;
	}

	@Override
	public boolean teleport(Location location) {
		this.location = location;

		Workarounds.getNetwork().sendTeleport(
				relevantPlayers, entityId,
				location.getX(), location.getY(), location.getZ(),
				location.getYaw()+yawOffset, location.getPitch()
		);

		if (passenger != null) {
			Workarounds.setPosition(passenger, location.toVector().add(new Vector(0, getMountedYOffset(), 0)), false);
		}

		for (Entry<Entity, Double> entry : fakePassengers.entrySet()) {
			entry.getKey().teleport(location.clone().add(0, entry.getValue(), 0));
		}

		return true;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Vector getVelocity() {
		return new Vector();
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return null;
		/*
		EntityPlayer entity = new EntityPlayer(null, null, null, null);
		@SuppressWarnings("unchecked")
		List<Entity> notchEntityList = ((CraftWorld)world).getHandle().b(entity, entity.boundingBox.b(x, y, z));
		List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

		for (Entity e: notchEntityList) {
			bukkitEntityList.add(e.getBukkitEntity());
		}
		return bukkitEntityList;
		*/
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public int getFireTicks() {
		return 0;
	}

	@Override
	public int getMaxFireTicks() {
		return 0;
	}

	@Override
	public void setFireTicks(int ticks) {
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

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public Entity getPassenger() {
		return passenger;
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		if (passenger == null)  {
			if (this.passenger == null)
				return true;

			Workarounds.getNetwork().sendAttachToVehicle(relevantPlayers, this.passenger, null);
		}
		else {
			Workarounds.getNetwork().sendAttachToVehicle(relevantPlayers, passenger, this);
		}

		this.passenger = passenger;

		return true;
	}

	@Override
	public boolean isEmpty() {
		return getPassenger() == null;
	}

	@Override
	public boolean eject() {
		return setPassenger(null);
	}

	@Override
	public float getFallDistance() {
		return 0;
	}

	@Override
	public void setFallDistance(float distance) {
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return null;
	}

	@Override
	public UUID getUniqueId() {
		return null;
	}

	@Override
	public int getTicksLived() {
		return 0;
	}

	@Override
	public void setTicksLived(int value) {
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return teleport(destination);
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return teleport(location);
	}

	@Override
	public boolean isInsideVehicle() {
		return false;
	}

	@Override
	public boolean leaveVehicle() {
		return false;
	}

	@Override
	public Entity getVehicle() {
		return null;
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return null;
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return false;
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
	}

	public void addFakePassenger(Entity entity, double yOffset) {
		fakePassengers.put(entity, yOffset);
	}

	public void removeFakePassenger(Entity entity) {
		fakePassengers.remove(entity);
	}


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

	public void setData(int index, Object value) {
		Workarounds.getNetwork().sendSetData(relevantPlayers, entityId, index, value);
	}

	public double getMountedYOffset() {
		return height * 0.75;
	}
}
