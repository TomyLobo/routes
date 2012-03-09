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

import eu.tomylobo.abstraction.Environment;

public class BukkitEnvironment extends Environment {
	@Override
	protected BukkitWorld getWorldImpl(String worldName) {
		return BukkitUtils.wrap(org.bukkit.Bukkit.getServer().getWorld(worldName));
	}

	private final BukkitNetwork networkInstance = new BukkitNetwork();
	@Override
	protected BukkitNetwork networkImpl() {
		return networkInstance;
	}

	private final BukkitScheduler schedulerInstance = new BukkitScheduler();
	@Override
	protected BukkitScheduler schedulerImpl() {
		return schedulerInstance;
	}
}
