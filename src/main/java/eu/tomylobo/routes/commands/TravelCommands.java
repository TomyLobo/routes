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

import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.CommandException;
import eu.tomylobo.routes.commands.system.NestedCommand;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.FakeVehicle;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.util.Remover;

/**
 * Contains all commands connected to travelling management.
 *
 * @author TomyLobo
 *
 */
public class TravelCommands extends CommandContainer {
	@NestedCommand
	public void travel(Context context) {
		if (context.length() < 1) {
			context.sendMessage("/"+context.getLabel()+" expects a sub-command.");
		}
		else {
			context.sendMessage("Could not find the specified /"+context.getLabel()+" sub-command.");
		}
	}

	@Command
	public void travel_test(Context context) {
		final String routeName = context.getString(0);

		Route route = plugin.transportSystem.getRoute(routeName);
		if (route == null)
			throw new CommandException("Route '"+routeName+"' not found.");

		final Location location = route.getLocation(0);
		final FakeEntity entity = new FakeVehicle(location, VehicleType.MINECART);
		final FakeEntity entity2 = new FakeMob(location, MobType.SKELETON);
		entity.send();
		entity2.send();
		entity.setPassenger(entity2);
		plugin.travelAgency.addTraveller(routeName, entity, 5.0, new Remover(entity, entity2));
		route.visualize(1.0);
		context.sendMessage("Testing route "+routeName+".");
	}
}
