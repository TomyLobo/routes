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

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.infrastructure.editor.RouteEditSession;
import eu.tomylobo.routes.infrastructure.editor.RouteEditor;
import eu.tomylobo.routes.infrastructure.editor.VisualizedRoute;
import eu.tomylobo.routes.util.ScheduledTask;

/**
 * Contains all commands connected to route management.
 *
 * @author TomyLobo
 *
 */
public class RoutesCommands extends CommandContainer implements Listener {
	public RoutesCommands() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

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

		final Route route = new Route();
		plugin.transportSystem.addRoute(routeName, route);

		plugin.routeEditor.edit(player, route);

		context.sendMessage("Starting a route named '"+routeName+"' here. Right-click with "+RouteEditor.toolMaterial+" to add a waypoint.");
	}

	@Command(permissions = "routes.show")
	public void routes_show(Context context) {
		final String routeName = context.getString(0);

		final int segmentIndex = context.getInt(1, -1);

		if (segmentIndex == -1) {
			final Route route = plugin.transportSystem.getRoute(routeName);

			route.visualize(1.0, 600);
		}
		else {
			final Route route = plugin.transportSystem.getRoute(routeName);

			final VisualizedRoute visualizedRoute = new VisualizedRoute(route, 1.0, context.getPlayer());

			final ScheduledTask task = new ScheduledTask(plugin) {
				int iteration = 0;
				@Override
				public void run() {
					if (++iteration > 10) {
						cancel();
						visualizedRoute.removeEntities();
						return;
					}

					visualizedRoute.showSegment(segmentIndex, iteration%2 == 0);
				}
			};

			task.scheduleSyncRepeating(0, 10);
		}

		context.sendMessage("Showing the route '"+routeName+"'.");
	}

	@Command(permissions = "routes.edit")
	public void routes_edit(Context context) {
		final Player player = context.getPlayer();

		final String routeName = context.getString(0);
		final Route route = plugin.transportSystem.getRoute(routeName);

		final int segmentIndex = context.getInt(1, route.getNodes().size()-1);

		RouteEditSession routeEditSession = plugin.routeEditor.edit(player, route);
		routeEditSession.selectSegment(segmentIndex);

		context.sendMessage("Editing the route '"+routeName+"'.");
	}
}
