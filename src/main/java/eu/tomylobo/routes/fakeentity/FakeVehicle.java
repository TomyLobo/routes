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

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet23VehicleSpawn;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A client-side-only entity spawned through {@link Packet23VehicleSpawn}.
 *
 * @author TomyLobo
 *
 */
public class FakeVehicle extends FakeEntity {
	private final int vehicleTypeId;

	public FakeVehicle(Location location, VehicleType vehicleType) {
		super(location, vehicleType.getYawOffset());

		this.vehicleTypeId = vehicleType.getId();
	}

	@Override
	public void sendImplementation(Player player) {
		final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn();
		p23.a = entityId;
		p23.b = MathHelper.floor(location.getX() * 32.0D);
		p23.c = MathHelper.floor(location.getY() * 32.0D);
		p23.d = MathHelper.floor(location.getZ() * 32.0D);
		p23.h = vehicleTypeId;
		p23.i = 0;

		sendPacketToPlayer(player, p23);
	}
}
