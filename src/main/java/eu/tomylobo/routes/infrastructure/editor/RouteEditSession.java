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

package eu.tomylobo.routes.infrastructure.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.util.ScheduledTask;

public class RouteEditSession {
	private final Player player;
	private final Route route;
	private final VisualizedRoute visualizedRoute;
	private int segmentIndex;
	private ScheduledTask flashTask;

	public RouteEditSession(Player player, Route route) {
		this.player = player;
		this.route = route;
		this.segmentIndex = route.getNodes().size();
		this.visualizedRoute = new VisualizedRoute(route, 1.0, player);

		flashTask = new ScheduledTask(Routes.getInstance()) {
			boolean on = true;
			int lastSegmentIndex;

			@Override
			public void run() {
				on = !on;

				if (!on) {
					// If we're turning it off, save the segment index so it'll be turned back on.
					lastSegmentIndex = segmentIndex;
				}

				visualizedRoute.showSegment(lastSegmentIndex, on);
			}
		};

		flashTask.scheduleSyncRepeating(0, 5);
	}

	public void interact(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			route.addNodes(++segmentIndex, player.getLocation());
			visualizedRoute.refresh(segmentIndex - 2, 3, 4);
		}
	}

	public void close() {
		flashTask.cancel();
		visualizedRoute.removeEntities();
	}

	public void selectSegment(int index) {
		if (segmentIndex == index)
			return;

		segmentIndex = index;
		flashTask.run();
		flashTask.run();
	}
}
