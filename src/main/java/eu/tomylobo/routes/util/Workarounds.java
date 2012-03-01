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

package eu.tomylobo.routes.util;

import net.minecraft.server.Packet11PlayerPosition;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.util.network.CraftNetwork;
import eu.tomylobo.routes.util.network.Network;

public class Workarounds {
	public static Location getEyeLocation(final Player player) {
		final Location eyeLocation = player.getLocation();
		double eyeHeight = player.getEyeHeight(true);
		if (player.isSneaking()) {
			eyeHeight -= 0.08;
		}
		eyeLocation.add(0, eyeHeight, 0);
		return eyeLocation;
	}

	public static Location getLocation(BlockState state) {
		return state.getBlock().getLocation();
	}

	public static void setPosition(Entity entity, Vector vector, boolean sendUpdate) {
		net.minecraft.server.Entity notchEntity = ((CraftEntity) entity).getHandle();

		final double x = vector.getX();
		final double y = vector.getY();
		final double z = vector.getZ();

		notchEntity.setPosition(x, y, z);
		if (sendUpdate && entity instanceof Player) {
			final Packet11PlayerPosition packet = new Packet11PlayerPosition();
			packet.x = x;
			packet.y = y + 1.62;
			packet.stance = y;
			packet.z = z;
			FakeEntity.sendPacketToPlayer((Player) entity, packet);
		}
	}

	private static final CraftNetwork networkInstance = new CraftNetwork();
	public static Network getNetwork() {
		return networkInstance;
	}
}
