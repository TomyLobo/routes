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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.fakeentity.VehicleType;

/**
 * Contains all commands that are purely temporary and for testing only.
 *
 * @author TomyLobo
 *
 */
public class TestCommands extends CommandContainer {
	@Command
	public void routes_test(CommandSender sender, String commandName, String label, String[] args) {
		routes_test1(sender, commandName, label, args);
		routes_test2(sender, commandName, label, args);
	}

	@Command
	public void routes_test1(CommandSender sender, String commandName, String label, String[] args) {
		Route route = new Route();

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

		plugin.transportSystem.addRoute("test", route);
		sender.sendMessage("Created a test route.");
	}

	@Command
	public void routes_test2(CommandSender sender, String commandName, String label, String[] args) {
		plugin.transportSystem.load();
		Route route = plugin.transportSystem.getRoute("test");
		final Location location = route.getLocation(0);
		//final FakeEntity entity = new FakeMob(location, MobType.SPIDER);
		final FakeEntity entity = new FakeVehicle(location, VehicleType.MINECART);
		final FakeEntity entity2 = new FakeMob(location, MobType.SKELETON);
		entity.send();
		entity2.send();
		entity.setPassenger(entity2);
		plugin.travelAgency.addTraveller("test", entity, 5.0, new Remover(entity, entity2));
		route.visualize(1.0);
		sender.sendMessage("Testing route.");
	}

	public class Remover implements Runnable {
		private final Entity[] entities;

		public Remover(Entity... entities) {
			this.entities = entities;
		}

		@Override
		public void run() {
			for (Entity entity : entities) {
				entity.remove();
			}
		}
	}
}
