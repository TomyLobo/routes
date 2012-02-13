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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

import de.tomylobo.routes.Route;
import de.tomylobo.routes.Routes;
import de.tomylobo.routes.fakeentity.FakeEnderEye;
import de.tomylobo.routes.fakeentity.FakeEntity;

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
	private Route route;

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
		test1(sender, commandName, label, args);
		test2(sender, commandName, label, args);
	}

	@Command
	public void test1(CommandSender sender, String commandName, String label, String[] args) {
		Player player = (Player) sender;

		route = new Route(plugin);

		final Location location = player.getLocation();

		route.addNodes(
				location,
				location.clone().add(8,0,0),
				location.clone().add(8,0,8),

				location.clone().add(8,8,8),
				location.clone().add(8,8,0),
				location.clone().add(0,8,0),
				location.clone().add(0,8,8),
				location.clone().add(8,8,8),

				location.clone().add(8,0,8),
				location.clone().add(0,0,8),
				location
		);

		//plugin.routes.put("test", route);
	}
	
	@Command
	public void test2(CommandSender sender, String commandName, String label, String[] args) {
		Player player = (Player) sender;

		Location location = route.getLocation(0);
		//final Entity creature = player.getWorld().spawnCreature(location, CreatureType.PIG);
		//final Entity creature = player.getWorld().spawn(location, Minecart.class);
		final Entity creature = new FakeEnderEye(location);
		((FakeEntity) creature).send();
		//creature.setPassenger(player);
		plugin.travelAgency.addTraveller(creature, route, new Runnable() {
			@Override
			public void run() {
				creature.remove();
			}
		});
		route.visualize(200);
		sender.sendMessage("Created a Pig, put you on it and made it travel in an 8x8 square.");
	}
}
