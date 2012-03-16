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

import java.util.List;

import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.event.PlayerClickEvent;
import eu.tomylobo.abstraction.plugin.MetaPlugin;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.infrastructure.Node;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.util.ScheduledTask;

public class RouteEditSession {
	private final class FlashTask extends ScheduledTask {
		boolean on = true;
		int lastSegmentIndex;

		private FlashTask(MetaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			on = !on;

			if (!on) {
				// If we're turning it off, save the segment index so it'll be turned back on.
				lastSegmentIndex = segmentIndex;
			}

			visualizedRoute.showSegment(lastSegmentIndex, on);
		}

		public void reset() {
			on = true;
		}
	}

	private static final double NODE_RADIUS = 1.2;
	private static final double NODE_RADIUS_SQ = NODE_RADIUS * NODE_RADIUS;

	private final Player player;
	private final Route route;
	private final VisualizedRoute visualizedRoute;
	private int segmentIndex;
	private FlashTask flashTask;

	public RouteEditSession(Player player, Route route) {
		this.player = player;
		this.route = route;
		this.segmentIndex = route.getNodes().size() - 1;
		final Routes plugin = Routes.getInstance();
		this.visualizedRoute = new VisualizedRoute(route, plugin.config.editorDotsPerMeter, player);

		flashTask = new FlashTask(plugin);

		flashTask.scheduleSyncRepeating(0, plugin.config.editorFlashTicks);
	}

	public Object getRoute() {
		return route;
	}

	void interact(PlayerClickEvent event) {
		if (event.isRightClick()) {
			route.addNodes(++segmentIndex, player.getLocation());
			visualizedRoute.refresh(segmentIndex - 2, 3, 4);
		}
		else {
			final Location location = event.getPlayer().getEyeLocation();
			final Vector playerPosition = location.getPosition();
			final Vector playerDirection = location.getDirection();

			double minPlayerDistanceSq = Routes.getInstance().config.editorSelectRange;
			minPlayerDistanceSq *= minPlayerDistanceSq;
			int closestNodeIndex = -1;

			final List<Node> nodes = route.getNodes();
			for (int i = 0; i < nodes.size(); ++i) {
				Node node = nodes.get(i);
				final Vector position = node.getPosition();
				final Vector diff = playerPosition.subtract(position);

				final double playerDistanceSq = diff.lengthSq();
				if (playerDistanceSq > minPlayerDistanceSq)
					continue; // Exclude points that are too far away or further away than the closest point so far

				final double dot = diff.dot(playerDirection);
				if (dot > 0)
					continue; // Exclude points behind us

				final double tangentialDistanceSq = diff.distanceSq(playerDirection.multiply(dot));
				if (tangentialDistanceSq > NODE_RADIUS_SQ)
					continue; // Exclude points that the line of sight missed

				minPlayerDistanceSq = playerDistanceSq;
				closestNodeIndex = i;
			}

			if (closestNodeIndex == -1)
				return;

			selectSegment(closestNodeIndex);
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

		// Run flash task twice to make the flashing portion update instantly
		flashTask.run();
		flashTask.run();
	}

	public void refreshNode(int index) {
		visualizedRoute.refresh(index - 2, 4, 4); // TODO: maybe 1/3 is enough?
		flashTask.reset();
	}


	@Command(permissions = "routes.edit")
	public void routes_close(Context context) {
		Routes.getInstance().routeEditor.close(player);

		context.sendMessage("Closed editor session.");
	}

	@Command(names = { "routes_nodeproperties", "routes_np" }, permissions = "routes.edit")
	public void routes_nodeproperties(Context context) {
		// Tension     T = +1-->Tight             T = -1--> Round
		// Bias        B = +1-->Post Shoot        B = -1--> Pre shoot
		// Continuity  C = +1-->Inverted corners  C = -1--> Box corners

		double tension = context.getDouble(0);
		double bias = context.getDouble(1);
		double continuity = context.getDouble(2);

		Node node = route.getNodes().get(segmentIndex);
		node.setTension(tension);
		node.setBias(bias);
		node.setContinuity(continuity);

		refreshNode(segmentIndex);

		context.sendFormattedMessage("Set node #%d properties: tension=%.2f", segmentIndex, tension, bias, continuity);
	}

	@Command(names = { "routes_removenode", "routes_rmnode" }, permissions = "routes.edit")
	public void routes_removenode(Context context) {
		route.removeNode(segmentIndex);
		visualizedRoute.refresh(segmentIndex - 2, 4, 3);

		if (segmentIndex > 0) {
			selectSegment(segmentIndex - 1);
		}

		context.sendFormattedMessage("Deleted node #%d from route %s", segmentIndex, route.getName());
	}

	@Command(names = { "routes_movenode", "routes_mvnode" }, permissions = "routes.edit")
	public void routes_movenode(Context context) {
		Node node = route.getNodes().get(segmentIndex);
		node.setPosition(player.getLocation().getPosition());

		refreshNode(segmentIndex);
	}
}
