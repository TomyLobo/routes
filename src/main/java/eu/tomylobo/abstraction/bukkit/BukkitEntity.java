package eu.tomylobo.abstraction.bukkit;

import org.bukkit.craftbukkit.entity.CraftEntity;

import eu.tomylobo.abstraction.Entity;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public class BukkitEntity implements Entity {
	final org.bukkit.entity.Entity backend;

	public BukkitEntity(org.bukkit.entity.Entity backend) {
		this.backend = backend;
	}

	@Override
	public void teleport(Location location) {
		teleport(location, true, true);
	}

	@Override
	public void teleport(Location location, boolean withAngles, boolean notify) {
		if (withAngles) {
			backend.teleport(BukkitUtils.unwrap(location));
			// TODO: implement notify=false
		}
		else {
			net.minecraft.server.Entity notchEntity = getCBHandle();

			Vector position = location.getPosition();
			final double x = position.getX();
			final double y = position.getY();
			final double z = position.getZ();

			notchEntity.setPosition(x, y, z);
		}
	}

	private net.minecraft.server.Entity getCBHandle() {
		return ((CraftEntity) backend).getHandle();
	}

	@Override
	public void setVelocity(Vector velocity) {
		backend.setVelocity(BukkitUtils.unwrap(velocity));
	}

	@Override
	public Location getLocation() {
		return BukkitUtils.wrap(backend.getLocation());
	}

	@Override
	public int getEntityId() {
		return backend.getEntityId();
	}

	@Override
	public void remove() {
		backend.remove();
	}

	@Override
	public Entity getPassenger() {
		return BukkitUtils.wrap(backend.getPassenger());
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		return backend.setPassenger(BukkitUtils.unwrap(passenger));
	}

	@Override
	public boolean isDead() {
		return backend.isDead();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BukkitEntity))
			return false;

		return backend.equals(BukkitUtils.unwrap((BukkitEntity) obj));
	}

	@Override
	public int hashCode() {
		return backend.hashCode();
	}
}
