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

package eu.tomylobo.routes.commands;

import java.util.Collection;

import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.infrastructure.editor.RouteEditSession;
import eu.tomylobo.routes.infrastructure.editor.VisualizedRoute;
import eu.tomylobo.routes.util.Remover;
import eu.tomylobo.routes.util.ScheduledTask;

/**
 * Contains all commands connected to route management.
 *
 * @author TomyLobo
 *
 */
public class RoutesCommands extends CommandContainer {
	@NestedCommand
	public void routes(Context context) {
		if (context.length() < 1) {
			context.sendMessage("/"+context.getLabel()+" expects a sub-command.");
		}
		else {
			context.sendMessage("Could not find the specified /"+context.getLabel()+" sub-command.");
		}
	}

	@Command(permissions = "routes.load")
	public void routes_load(Context context) {
		plugin.load();

		context.sendMessage("Reloaded routes.");
	}

	@Command(permissions = "routes.save")
	public void routes_save(Context context) {
		plugin.save();

		context.sendMessage("Saved routes.");
	}

	@Command(permissions = "routes.add")
	public void routes_add(Context context) {
		final Player player = context.getPlayer();
		final String routeName = context.getString(0);

		final Route route = new Route(routeName);
		plugin.transportSystem.addRoute(route);

		plugin.routeEditor.edit(player, route);

		context.sendMessage("Starting a route named '"+routeName+"' here. Right-click with "+plugin.config.editorTool+" to add a waypoint.");
	}

	@Command(permissions = "routes.show")
	public void routes_show(Context context) {
		final String routeName = context.getString(0);
		final Route route = plugin.transportSystem.getRoute(routeName);

		final int segmentIndex = context.getInt(1, -1);
		if (segmentIndex == -1) {
			route.visualize(plugin.config.showDotsPerMeter, plugin.config.showTicks);
		}
		else {
			final VisualizedRoute visualizedRoute = new VisualizedRoute(route, plugin.config.showDotsPerMeter, context.getPlayer());

			final ScheduledTask task = new ScheduledTask(plugin) {
				private int iteration = 0;
				private int ticks = 0;

				@Override
				public void run() {
					++iteration;
					ticks += plugin.config.showFlashTicks;
					if (ticks > plugin.config.showTicks) {
						cancel();
						visualizedRoute.removeEntities();
						return;
					}

					visualizedRoute.showSegment(segmentIndex, iteration%2 == 0);
				}
			};

			task.scheduleSyncRepeating(0, plugin.config.showFlashTicks);
		}

		context.sendMessage("Showing the route '"+routeName+"'.");
	}

	@Command(permissions = "routes.edit")
	public void routes_edit(Context context) {
		final Player player = context.getPlayer();

		final String routeName = context.getString(0);
		final Route route = plugin.transportSystem.getRoute(routeName);

		final Collection<Player> playersEditing = plugin.routeEditor.getPlayersEditing(route);
		if (playersEditing.size() == 1 && playersEditing.iterator().next().equals(player)) {
			context.sendFormattedMessage("You were already editing the route '%s'", routeName);
		}
		else if (!playersEditing.isEmpty()) {
			context.sendFormattedMessage("The following players are already editing the route '%s'", routeName);
			StringBuilder sb = new StringBuilder();
			for (Player editingPlayer : playersEditing) {
				if (sb.length() != 0)
					sb.append(", ");

				sb.append(editingPlayer.getName());
			}
		}
		else {
			context.sendFormattedMessage("Editing the route '%s'.", routeName);
		}

		final int segmentIndex = context.getInt(1, route.getNodes().size()-1);

		RouteEditSession routeEditSession = plugin.routeEditor.edit(player, route);
		routeEditSession.selectSegment(segmentIndex);
	}

	@Command(permissions = "routes.test")
	public void routes_test(Context context) {
		final String routeName = context.getString(0);
		final Route route = plugin.transportSystem.getRoute(routeName);

		final FakeMob dragon = new FakeMob(route.getLocation(0), MobType.ENDER_DRAGON);
		dragon.send();

		plugin.travelAgency.addTraveller(route, dragon, 5.0, new Remover(dragon));

		context.sendMessage("Testing route '"+routeName+"'.");
	}
}
