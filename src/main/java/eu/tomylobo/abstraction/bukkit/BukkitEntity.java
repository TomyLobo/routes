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

package eu.tomylobo.abstraction.bukkit;

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
		return ((org.bukkit.craftbukkit.entity.CraftEntity) backend).getHandle();
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
