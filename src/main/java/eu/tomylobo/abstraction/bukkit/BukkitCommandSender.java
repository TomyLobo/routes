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

import eu.tomylobo.abstraction.CommandSender;
import eu.tomylobo.abstraction.PermissionUtils;

public class BukkitCommandSender implements CommandSender {
	final org.bukkit.command.CommandSender backend;

	public BukkitCommandSender(org.bukkit.command.CommandSender backend) {
		this.backend = backend;
	}

	@Override
	public void sendMessage(String message) {
		backend.sendMessage(message);
	}

	@Override
	public String getName() {
		return backend.getName();
	}

	@Override
	public boolean hasPermission(String permission) {
		return PermissionUtils.hasPermission(this, permission);
	}

	@Override
	public boolean hasExactPermission(String permission) {
		return backend.hasPermission(permission);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BukkitCommandSender))
			return false;

		return backend.equals(BukkitUtils.unwrap((BukkitCommandSender) obj));
	}

	@Override
	public int hashCode() {
		return backend.hashCode();
	}
}
