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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import eu.tomylobo.abstraction.Scheduler;
import eu.tomylobo.routes.Routes;

public class BukkitScheduler implements Scheduler {
	Plugin plugin = Routes.getInstance().plugin;

	@Override
	public Object scheduleSyncDelayedTask(Runnable task, long delay) {
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public Object scheduleSyncDelayedTask(Runnable task) {
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public Object scheduleSyncRepeatingTask(Runnable task, long delay, long period) {
		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delay, period);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public Object scheduleAsyncDelayedTask(Runnable task, long delay) {
		int taskId = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, task, delay);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public Object scheduleAsyncDelayedTask(Runnable task) {
		int taskId = Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, task);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public Object scheduleAsyncRepeatingTask(Runnable task, long delay, long period) {
		int taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, task, delay, period);
		if (taskId == -1)
			return null;

		return taskId;
	}

	@Override
	public void cancelTask(Object taskId) {
		if (taskId == null)
			return;

		Bukkit.getScheduler().cancelTask((Integer) taskId);
	}
}
