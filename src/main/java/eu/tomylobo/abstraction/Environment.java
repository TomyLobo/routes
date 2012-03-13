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

import java.util.List;

import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.event.Dispatcher;

public abstract class Environment {
	public static void init(Class<? extends Environment> environmentClass) {
		if (instance == null) {
			try {
				instance = environmentClass.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("Could not instantiate "+environmentClass.getSimpleName(), e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Could not instantiate "+environmentClass.getSimpleName(), e);
			}
		}
		else if (!environmentClass.isInstance(instance)) {
			throw new RuntimeException("Could not instantiate "+environmentClass.getSimpleName()+
					": already got a "+instance.getClass().getSimpleName());
		}
	}

	private static Environment instance;
	public static boolean isPlatform(Class<? extends Environment> platform) {
		return platform.isInstance(instance);
	}

	protected abstract World getWorldImpl(String worldName);
	public static World getWorld(String worldName) {
		return instance.getWorldImpl(worldName);
	}

	protected abstract Player getPlayerImpl(String playerName);
	public static Player getPlayer(String playerName) {
		return instance.getPlayerImpl(playerName);
	}

	protected abstract List<Player> getPlayersImpl();
	public static List<Player> getPlayers() {
		return instance.getPlayersImpl();
	}

	protected abstract Network networkImpl();
	public static Network network() {
		return instance.networkImpl();
	}

	protected abstract Scheduler schedulerImpl();
	public static Scheduler scheduler() {
		return instance.schedulerImpl();
	}
	protected abstract Dispatcher dispatcherImpl();
	public static Dispatcher dispatcher() {
		return instance.dispatcherImpl();
	}
}
