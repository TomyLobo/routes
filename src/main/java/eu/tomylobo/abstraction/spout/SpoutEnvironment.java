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

package eu.tomylobo.abstraction.spout;

import java.util.ArrayList;
import java.util.List;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.spout.event.SpoutDispatcher;

public class SpoutEnvironment extends Environment {
	@Override
	protected SpoutWorld getWorldImpl(String worldName) {
		return SpoutUtils.wrap(org.spout.api.Spout.getGame().getWorld(worldName));
	}

	@Override
	protected SpoutPlayer getPlayerImpl(String playerName) {
		return SpoutUtils.wrap(org.spout.api.Spout.getGame().getPlayer(playerName, true));
	}

	@Override
	protected List<Player> getPlayersImpl() {
		final org.spout.api.player.Player[] players = org.spout.api.Spout.getGame().getOnlinePlayers();
		final List<Player> ret = new ArrayList<Player>(players.length);
		for (org.spout.api.player.Player player : players) {
			ret.add(SpoutUtils.wrap(player));
		}
		return ret;
	}

	private final SpoutNetwork networkInstance = new SpoutNetwork();
	@Override
	protected SpoutNetwork networkImpl() {
		return networkInstance;
	}

	private final SpoutScheduler schedulerInstance = new SpoutScheduler();
	@Override
	protected SpoutScheduler schedulerImpl() {
		return schedulerInstance;
	}

	private final SpoutDispatcher dispatcherInstance = new SpoutDispatcher();
	@Override
	protected SpoutDispatcher dispatcherImpl() {
		return dispatcherInstance;
	}
}
