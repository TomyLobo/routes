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

package de.tomylobo.routes.fakeentity;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;

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
import org.bukkit.event.server.Packet;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

/**
 * A client-side-only entity.
 * 
 * This class only implements part of the bukkit Entity interface and doesn't
 * simulate movement beyond what is explicitly set.
 * 
 * @author TomyLobo
 */
public abstract class FakeEntity implements Entity {
	static int lastFakeEntityId = 1000000000;

	public static final void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer)ply).getHandle().netServerHandler.sendPacket((net.minecraft.server.Packet) packet);
	}

	public void sendPacketToRelevantPlayers(final Packet packet) {
		for (Player player : location.getWorld().getPlayers()) {
			sendPacketToPlayer(player, packet);
		}
	}

	public final int entityId;
	public Location location;
	private boolean isDead;
	protected final float yawOffset;
	protected final DataWatcher datawatcher;

	@Override
	public void playEffect(EntityEffect effect) {
		sendPacketToRelevantPlayers(new Packet38EntityStatus(entityId, effect.getData()));
	}

	public FakeEntity(Location location) {
		this(location, 0);
	}

	public FakeEntity(Location location, float yawOffset) {
		this.yawOffset = yawOffset;
		if (location == null)
			throw new IllegalArgumentException("A null Location was passed to the FakeEntity ctor.");

		entityId = ++lastFakeEntityId;
		this.location = location;
		this.datawatcher = new DataWatcher();
	}

	public void send() {
		for (Player player : location.getWorld().getPlayers()) {
			send(player);
		}
	}

	abstract public void send(Player player);

	private void delete() {
		for (Player player : location.getWorld().getPlayers()) {
			delete(player);
		}
	}

	private void delete(Player player) {
		sendPacketToPlayer(player, new Packet29DestroyEntity(entityId));
	}

	@Override
	public void setVelocity(Vector velocity) {
		sendPacketToRelevantPlayers(new Packet28EntityVelocity(entityId, velocity.getX(), velocity.getY(), velocity.getZ()));
	}

	@Override
	public boolean teleport(Location location) {
		this.location = location;
		sendPacketToRelevantPlayers(new Packet34EntityTeleport(
				entityId,
				MathHelper.floor(location.getX()*32.0D),
				MathHelper.floor(location.getY()*32.0D),
				MathHelper.floor(location.getZ()*32.0D),
				(byte) ((int) ((location.getYaw()+yawOffset) * 256.0F / 360.0F)),
				(byte) ((int) (location.getPitch() * 256.0F / 360.0F))
		));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTicksLived() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTicksLived(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return teleport(destination);
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return teleport(location);
	}
}
