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

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.tomylobo.routes.Utils;

/**
 * A client-side-only entity spawned through {@link Packet23VehicleSpawn}.
 *
 * @author TomyLobo
 */
public class FakeMob extends FakeEntity {
	private final int mobTypeId;

	public FakeMob(Location location, MobType mobType) {
		super(location, mobType.getYawOffset());

		this.mobTypeId = mobType.getId();
	}

	@Override
	public void send(Player player) {
		final Packet24MobSpawn p24 = new Packet24MobSpawn();
		p24.a = entityId;
		p24.b = mobTypeId;
		p24.c = MathHelper.floor(location.getX() * 32.0D);
		p24.d = MathHelper.floor(location.getY() * 32.0D);
		p24.e = MathHelper.floor(location.getZ() * 32.0D);
		p24.f = (byte) ((int) ((location.getYaw() + yawOffset) * 256.0F / 360.0F));
		p24.g = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "h", datawatcher);

		sendPacketToPlayer(player, p24);
	}
}
