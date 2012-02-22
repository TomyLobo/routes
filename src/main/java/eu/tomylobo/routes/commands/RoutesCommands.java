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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;
import eu.tomylobo.routes.trace.Shape;
import eu.tomylobo.routes.trace.SignShape;
import eu.tomylobo.routes.trace.TraceResult;
import eu.tomylobo.routes.util.Workarounds;

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

	@Command
	public void routes_load(Context context) {
		plugin.load();

		context.sendMessage("Reloaded routes.");
	}

	@Command
	public void routes_save(Context context) {
		plugin.save();

		context.sendMessage("Saved routes.");
	}

	Material toolMaterial = Material.GOLD_SPADE;
	@Command
	public void routes_add(Context context) {
		final Player player = context.getPlayer();
		final String routeName = context.getString(0);

		final Route route = new Route();
		plugin.transportSystem.addRoute(routeName, route);

		editedRoutes.put(player, route);

		context.sendMessage("Starting a route named '"+routeName+"' here. Right-click with "+toolMaterial+" to add a waypoint.");
	}

	Map<Player, Route> editedRoutes = new HashMap<Player, Route>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			final Player player = event.getPlayer();

			final Material materialInHand = player.getItemInHand().getType();
			if (materialInHand == toolMaterial) {
				final Route route = editedRoutes.get(player);
				if (route == null)
					return;

				route.addNodes(player.getLocation());
				route.visualize(1.0);
				break;
			}

			switch (materialInHand) {
			case ARROW:
				final Block block = event.getClickedBlock();
				if (block == null)
					return;

				final Sign sign = (Sign) block.getState();

				final Shape shape = new SignShape(sign);

				final Location eyeLocation = Workarounds.getEyeLocation(player);

				final TraceResult trace = shape.trace(eyeLocation);
				final double signScale = 2.0 / 3.0;
				final double fontScale = signScale / 60.0;
				final int index = (int) Math.floor(2.1 - trace.relativePosition.getY() / fontScale / 10.0);

				if (index >= 0 && index < 4) {
					markSignLine(sign, index);
				}

				/*FakeVehicle entity = new FakeVehicle(originLocation, VehicleType.ENDER_EYE);
				entity.send();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Remover(entity), 40);*/

				break;
			}

			break;
		}
	}

	private static void markSignLine(final Sign sign, final int index) {
		final String[] lines = sign.getLines();
		for (int i = 0; i < 4; ++i) {
			final String cleaned = lines[i].replaceAll("\u00a7.", "");
			if (i == index) {
				sign.setLine(i, "\u00a7c"+cleaned);
			}
			else {
				sign.setLine(i, cleaned);
			}
		}
		sign.update();
	}
}
