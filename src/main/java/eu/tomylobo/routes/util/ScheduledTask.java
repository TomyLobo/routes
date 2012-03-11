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

package eu.tomylobo.routes.util;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.plugin.MetaPlugin;

public abstract class ScheduledTask implements Runnable {
	private final MetaPlugin plugin;
	private Object taskId = null;

	public ScheduledTask(MetaPlugin plugin) {
		this.plugin = plugin;
	}

	public void scheduleSyncDelayed(long delay) {
		taskId = Environment.scheduler().scheduleSyncDelayedTask(plugin, this, delay);
	}


	public void scheduleSyncDelayed() {
		taskId = Environment.scheduler().scheduleSyncDelayedTask(plugin, this);
	}


	public void scheduleSyncRepeating(long delay, long period) {
		taskId = Environment.scheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
	}


	public void scheduleAsyncDelayed(long delay) {
		taskId = Environment.scheduler().scheduleAsyncDelayedTask(plugin, this, delay);
	}


	public void scheduleAsyncDelayed() {
		taskId = Environment.scheduler().scheduleAsyncDelayedTask(plugin, this);
	}

	public void scheduleAsyncRepeating(long delay, long period) {
		taskId = Environment.scheduler().scheduleAsyncRepeatingTask(plugin, this, delay, period);
	}

	public void cancel() {
		Environment.scheduler().cancelTask(taskId);
		taskId = null;
	}
}
