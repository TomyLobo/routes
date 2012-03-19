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
import eu.tomylobo.abstraction.plugin.MetaPlugin;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.CommandException;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.infrastructure.Node;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.util.ScheduledTask;

public class RouteEditSession {
	public abstract class State {
		void enter(State from) { }
		void leave(State to) { }

		void onPlayerClick(boolean rightClick) { }
		void onPlayerMove(Location to) { }
		void onSelectSegment() { }
		void refreshSegment(int startIndex, int oldAmount, int newAmount) {
			visualizedRoute.refresh(startIndex, oldAmount, newAmount);
		}
	}

	public final State SELECT = new State() {
		private FlashTask flashTask;

		@Override void onPlayerClick(boolean rightClick) {
			if (rightClick) {
				route.addNodes(++segmentIndex, player.getLocation());
				broadcastRefreshSegment(segmentIndex - 2, 3, 4);
				return;
			}
			final Location location = player.getEyeLocation();
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

		@Override void enter(State from) {
			final Routes plugin = Routes.getInstance();

			flashTask = new FlashTask(plugin);
			flashTask.scheduleSyncRepeating(0, plugin.config.editorFlashTicks);

			player.sendMessage("Left-click to select a node.");
			player.sendMessage("Right-click to to insert a node after the current one.");
}
		@Override void leave(State to) {
			flashTask.cancel();
			if (!flashTask.on) {
				flashTask.run();
			}
		}

		@Override void onSelectSegment() {
			// Run flash task twice to make the flashing portion update instantly
			if (flashTask.on) {
				flashTask.run();
				flashTask.run();
			}
		}

		@Override void refreshSegment(int startIndex, int oldAmount, int newAmount) {
			super.refreshSegment(startIndex, oldAmount, newAmount);

			flashTask.reset();
		}
	};

	public final State MOVE = new State() {
		private ScheduledTask moveTask = new ScheduledTask(Routes.getInstance()) { @Override public void run() {
			handleMove(player.getEyeLocation());
		}};

		private Node node;
		private double moveDistance;

		@Override void onPlayerClick(boolean rightClick) {
			if (rightClick) {
				changeState(MOVE_PAUSED);
			}
			else {
				changeState(SELECT);
			}
		}

		@Override void onPlayerMove(Location to) {
			handleMove(to.add(new Vector(0, player.getEyeHeight(), 0)));
		}

		@Override void enter(State from) {
			node = route.getNodes().get(segmentIndex);

			final Vector diff = node.getPosition().subtract(player.getEyeLocation().getPosition());
			moveDistance = diff.length();

			final Location playerLocation = player.getLocation();
			final Location newLocation = Location.fromEye(playerLocation.getWorld(), playerLocation.getPosition(), diff);

			player.teleport(newLocation);

			moveTask.scheduleSyncRepeating(0, 10);

			player.sendMessage(String.format("You can now move the node with your editor tool.", segmentIndex, route.getName()));
			player.sendMessage("Left-click returns to select mode. Right-click pauses moving.");
		}
		@Override void leave(State to) {
			moveTask.cancel();
			state = SELECT;
		}

		private void handleMove(Location eyeLocation) {
			final Vector newPosition = eyeLocation.getDirection().multiply(moveDistance).add(eyeLocation.getPosition());

			if (node.getPosition().equals(newPosition))
				return;

			node.setPosition(newPosition);
			refreshNode(segmentIndex);
		}
	};

	public final State MOVE_PAUSED = new State() {
		@Override void onPlayerClick(boolean rightClick) {
			if (rightClick) {
				changeState(MOVE);
			}
			else {
				changeState(SELECT);
			}
		}

		@Override
		void enter(State from) {
			player.sendMessage(String.format("Paused moving.", segmentIndex, route.getName()));
			player.sendMessage("Left-click returns to select mode. Right-click resumes moving.");
		}
	};

	public final class FlashTask extends ScheduledTask {
		private boolean on = true;
		private int lastSegmentIndex;

		public FlashTask(MetaPlugin plugin) {
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
			if (!on) {
				on = true;
				run();
			}
		}
	}

	private static final double NODE_RADIUS = 1.2;
	private static final double NODE_RADIUS_SQ = NODE_RADIUS * NODE_RADIUS;

	private final Player player;
	private final Route route;
	private final VisualizedRoute visualizedRoute;
	private int segmentIndex;

	State state;

	private void changeState(State newState) {
		if (state == newState)
			return;

		final State oldState = state;

		if (oldState != null) oldState.leave(newState);
		state = newState;
		if (newState != null) newState.enter(oldState);
	}

	public RouteEditSession(Player player, Route route) {
		this.player = player;
		this.route = route;
		this.segmentIndex = route.getNodes().size() - 1;
		this.visualizedRoute = new VisualizedRoute(route, Routes.getInstance().config.editorDotsPerMeter, player);

		changeState(SELECT);
	}

	public Object getRoute() {
		return route;
	}

	public void close() {
		changeState(null);
		visualizedRoute.removeEntities();
	}

	public void selectSegment(int index) {
		if (segmentIndex == index)
			return;

		segmentIndex = index;

		state.onSelectSegment();
	}

	private void refreshNode(int index) {
		broadcastRefreshSegment(index - 2, 4, 4);
	}

	private void broadcastRefreshSegment(int startIndex, int oldAmount, int newAmount) {
		Routes.getInstance().routeEditor.broadcastRefreshRouteSegment(route, startIndex, oldAmount, newAmount);
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
		broadcastRefreshSegment(segmentIndex - 2, 4, 3);

		if (segmentIndex > 0) {
			selectSegment(segmentIndex - 1);
		}

		context.sendFormattedMessage("Deleted node #%d from route '%s'", segmentIndex, route.getName());
	}

	@Command(names = { "routes_movenode", "routes_mvnode" }, permissions = "routes.edit")
	public void routes_movenode(Context context) {
		if (state == SELECT) {
			changeState(MOVE);
		}
		else if (state == MOVE || state == MOVE_PAUSED) {
			changeState(SELECT);
		}
		else {
			throw new CommandException("You cannot currently run this command.");
		}
	}
}
