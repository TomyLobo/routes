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

package eu.tomylobo.abstraction.platform.bukkit;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.PermissionUtils;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.math.Location;

public class BukkitPlayer extends BukkitEntity implements Player {
	public BukkitPlayer(org.bukkit.entity.Player backend) {
		super(backend);
	}

	@Override
	public void teleport(Location location, boolean withAngles, boolean notify) {
		super.teleport(location, withAngles, notify);
		if (notify) {
			Environment.network().sendPlayerPosition(this, location.getPosition());
		}
	}

	@Override
	public int getItemTypeInHand() {
		return ((org.bukkit.entity.Player) backend).getItemInHand().getTypeId();
	}

	@Override
	public boolean getAllowFlight() {
		return ((org.bukkit.entity.Player) backend).getAllowFlight();
	}

	@Override
	public void setAllowFlight(boolean flight) {
		((org.bukkit.entity.Player) backend).setAllowFlight(flight);
	}

	@Override
	public void sendMessage(String message) {
		((org.bukkit.entity.Player) backend).sendMessage(message);
	}

	@Override
	public String getName() {
		return ((org.bukkit.entity.Player) backend).getName();
	}

	@Override
	public boolean hasPermission(String permission) {
		return PermissionUtils.hasPermission(this, permission);
	}

	@Override
	public boolean hasExactPermission(String permission) {
		return ((org.bukkit.entity.Player) backend).hasPermission(permission);
	}

	@Override
	public Location getEyeLocation() {
		return BukkitUtils.wrap(((org.bukkit.entity.Player) backend).getEyeLocation());
	}
}
