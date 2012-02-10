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

package de.tomylobo.routes.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tomylobo.routes.Route;
import de.tomylobo.routes.Routes;

public class Commands {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Command {
	}

	private static final Map<String, Method> commands = new HashMap<String, Method>();

	static {
		for (Method method : Commands.class.getMethods()) {
			if (!method.isAnnotationPresent(Command.class))
				continue;

			commands.put(method.getName(), method);
		}
	}

	private Routes plugin;

	public Commands(Routes plugin) {
		this.plugin = plugin;
	}

	public boolean dispatch(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		return dispatch(sender, command.getName(), label, args);
	}

	public boolean dispatch(CommandSender sender, final String commandName, String label, String[] args) {
		Method method = commands.get(commandName);
		if (method == null)
			return false;

		try {
			method.invoke(this, sender, commandName, label, args);
		}
		catch (IllegalAccessException e) {
			return false;
		}
		catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof CommandException) {
				sender.sendMessage("\u00a7c"+cause.getMessage());
			}
			else {
				sender.sendMessage("\u00a7cException caught while executing command.");
				cause.printStackTrace();
			}

			return true;
		}
		return true;
	}

	@Command
	public void routes(CommandSender sender, String commandName, String label, String[] args) {
		String subCommandName = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);
		dispatch(sender, subCommandName, subCommandName, args);
	}

	@Command
	public void test(CommandSender sender, String commandName, String label, String[] args) {
		Player player = (Player) sender;

		Route route = new Route(plugin);

		final Location location = player.getLocation();

		final Location location2 = location.clone().add(8,0,0);
		final Location location3 = location.clone().add(8,0,8);
		final Location location4 = location.clone().add(0,0,8);

		route.addNodes(location, location2, location3, location4, location);

		//plugin.routes.put("test", route);

		final LivingEntity creature = player.getWorld().spawnCreature(location, CreatureType.PIG);
		creature.setPassenger(player);
		plugin.travelAgent.addTraveller(creature, route);
		route.visualize(100);
		sender.sendMessage("Created a Pig, put you on it and made it travel in an 8x8 square.");
	}
}
