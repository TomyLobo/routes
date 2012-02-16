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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.MobType;

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
		route = new Route(plugin);

		final Player player = (Player) sender;
		final Location location = player.getLocation();

		final int routeScale = 32;
		route.addNodes(
				location,
				location.clone().add(routeScale,0,0),
				location.clone().add(routeScale,0,routeScale),

				location.clone().add(routeScale,routeScale,routeScale),
				location.clone().add(routeScale,routeScale,0),
				location.clone().add(0,routeScale,0),
				location.clone().add(0,routeScale,routeScale),
				location.clone().add(routeScale,routeScale,routeScale),

				location.clone().add(routeScale,0,routeScale),
				location.clone().add(0,0,routeScale),
				location
		);

		//plugin.routes.put("test", route);
		sender.sendMessage("Created a test route.");
	}

	@Command
	public void test2(CommandSender sender, String commandName, String label, String[] args) {
		final Location location = route.getLocation(0);
		//final Entity entity = new FakeVehicle(location, VehicleType.MINECART);
		final Entity entity = new FakeMob(location, MobType.PIG);
		((FakeEntity) entity).send();
		entity.setPassenger((Player) sender);
		plugin.travelAgency.addTraveller(entity, route, new Runnable() {
			@Override
			public void run() {
				entity.remove();
			}
		});
		route.visualize(400);
		sender.sendMessage("Testing route.");
	}
}
