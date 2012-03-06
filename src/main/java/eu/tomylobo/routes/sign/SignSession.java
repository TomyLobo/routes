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

package eu.tomylobo.routes.sign;

import eu.tomylobo.abstraction.Player;
import eu.tomylobo.routes.util.ScheduledTask;

public class SignSession {
	private final SignHandler signHandler;
	private final Player player;
	private TrackedSign currentTrackedSign;
	private int currentIndex = -1;
	private ScheduledTask signResetTask = new ScheduledTask() {
		@Override
		public void run() {
			close();
		}
	};

	public SignSession(SignHandler signHandler, Player player) {
		this.signHandler = signHandler;
		this.player = player;
	}

	public void close() {
		signHandler.sessions.remove(player);

		cleanup();
	}

	private void cleanup() {
		if (currentTrackedSign != null) {

			currentTrackedSign.select(player, -1);
			currentIndex = -1;
			currentTrackedSign = null;
		}

		signResetTask.cancel();
	}

	public boolean isSelected(TrackedSign trackedSign, int index) {
		if (trackedSign == null)
			throw new IllegalArgumentException("trackedSign cannot be null");

		return trackedSign == currentTrackedSign && index == currentIndex;
	}

	public void select(TrackedSign trackedSign, int index) {
		if (currentTrackedSign != trackedSign) {
			if (currentTrackedSign != null) {
				currentTrackedSign.select(player, -1);
			}
			currentTrackedSign = trackedSign;
		}

		trackedSign.select(player, index);
		currentIndex = index;

		signResetTask.cancel();
		signResetTask.scheduleSyncDelayed(2*20);
	}
}
