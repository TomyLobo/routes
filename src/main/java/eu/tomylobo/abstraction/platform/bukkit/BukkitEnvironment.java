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

import java.util.ArrayList;
import java.util.List;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.platform.bukkit.event.BukkitDispatcher;

public class BukkitEnvironment extends Environment {
	@Override
	protected BukkitWorld getWorldImpl(String worldName) {
		return BukkitUtils.wrap(org.bukkit.Bukkit.getServer().getWorld(worldName));
	}

	@Override
	protected BukkitPlayer getPlayerImpl(String playerName) {
		return BukkitUtils.wrap(org.bukkit.Bukkit.getServer().getPlayer(playerName));
	}

	@Override
	protected List<Player> getPlayersImpl() {
		final org.bukkit.entity.Player[] players = org.bukkit.Bukkit.getServer().getOnlinePlayers();
		final List<Player> ret = new ArrayList<Player>(players.length);
		for (org.bukkit.entity.Player player : players) {
			ret.add(BukkitUtils.wrap(player));
		}
		return ret;
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

	private final BukkitDispatcher dispatcherInstance = new BukkitDispatcher();
	@Override
	protected BukkitDispatcher dispatcherImpl() {
		return dispatcherInstance;
	}
}
