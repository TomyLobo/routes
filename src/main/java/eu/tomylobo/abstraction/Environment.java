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

package eu.tomylobo.abstraction;

import eu.tomylobo.abstraction.bukkit.BukkitEnvironment;
import eu.tomylobo.abstraction.entity.Player;

public abstract class Environment {
	private static final Environment instance = new BukkitEnvironment();

	protected abstract World getWorldImpl(String worldName);
	public static World getWorld(String worldName) {
		return instance.getWorldImpl(worldName);
	}

	protected abstract Player getPlayerImpl(String playerName);
	public static Player getPlayer(String playerName) {
		return instance.getPlayerImpl(playerName);
	}

	protected abstract Network networkImpl();
	public static Network network() {
		return instance.networkImpl();
	}

	protected abstract Scheduler schedulerImpl();
	public static Scheduler scheduler() {
		return instance.schedulerImpl();
	}
}
