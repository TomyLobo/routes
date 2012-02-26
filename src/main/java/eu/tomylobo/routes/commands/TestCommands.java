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
import org.bukkit.entity.Player;

import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.infrastructure.Route;
import eu.tomylobo.routes.util.Remover;

/**
 * Contains all commands that are purely temporary and for testing only.
 *
 * @author TomyLobo
 *
 */
public class TestCommands extends CommandContainer {
	@Command
	public void routes_test3(Context context) {
		routes_test1(context);
		routes_test2(context);
	}

	@Command
	public void routes_test1(Context context) {
		Route route = new Route();

		final Player player = context.getPlayer();
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
		context.sendMessage("Created a test route.");
	}

	@Command
	public void routes_test2(Context context) {
		Route route = plugin.transportSystem.getRoute("test");
		final Location location = route.getLocation(0);
		//final FakeEntity entity = new FakeMob(location, MobType.ENDER_DRAGON);
		//final FakeEntity entity = new FakeVehicle(location, VehicleType.ENDER_EYE);
		FakeEntity[] entities = new FakeEntity[17];

		entities[0] = new FakeMob(location, MobType.ENDER_DRAGON);
		entities[0].send();

		// ent to put the stack onto
		entities[1] = new FakeVehicle(location, VehicleType.ENDER_EYE);
		entities[1].send();
		for (int i = 2; i < entities.length; ++i) {
			entities[i] = new FakeVehicle(location, VehicleType.ENDER_EYE);
			entities[i].send();
			entities[i-1].setPassenger(entities[i]);
		}

		FakeEntity skeleton = new FakeMob(location, MobType.SKELETON);
		skeleton.send();
		entities[entities.length-1].setPassenger(context.getPlayer());

		//entity.addFakePassenger(context.getPlayer(), 2.12);
		//entity.setPassenger(entity2);
		plugin.travelAgency.addTraveller("test", entities[0], 8.0, new Remover(entities));
		plugin.travelAgency.addTraveller("test", entities[1], 8.0, new Remover(skeleton));
		route.visualize(1.0, 600);
		context.sendMessage("Testing route.");
	}
}
