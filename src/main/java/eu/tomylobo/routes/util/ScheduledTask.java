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

import eu.tomylobo.abstraction.Factory;

public abstract class ScheduledTask implements Runnable {
	private Object taskId = null;

	public ScheduledTask() { // TODO: add reference to an abstract plugin
	}

	public void scheduleSyncDelayed(long delay) {
		taskId = Factory.scheduler().scheduleSyncDelayedTask(this, delay);
	}


	public void scheduleSyncDelayed() {
		taskId = Factory.scheduler().scheduleSyncDelayedTask(this);
	}


	public void scheduleSyncRepeating(long delay, long period) {
		taskId = Factory.scheduler().scheduleSyncRepeatingTask(this, delay, period);
	}


	public void scheduleAsyncDelayed(long delay) {
		taskId = Factory.scheduler().scheduleAsyncDelayedTask(this, delay);
	}


	public void scheduleAsyncDelayed() {
		taskId = Factory.scheduler().scheduleAsyncDelayedTask(this);
	}

	public void scheduleAsyncRepeating(long delay, long period) {
		taskId = Factory.scheduler().scheduleAsyncRepeatingTask(this, delay, period);
	}

	public void cancel() {
		Factory.scheduler().cancelTask(taskId);
		taskId = null;
	}
}
