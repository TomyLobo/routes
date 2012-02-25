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

import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.MobType;
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
		final Route route = plugin.transportSystem.getRoute(routeName);

		final FakeMob dragon = new FakeMob(route.getLocation(0), MobType.ENDER_DRAGON);
		dragon.send();

		plugin.travelAgency.addTraveller(route, dragon, 5.0, new Remover(dragon));

		context.sendMessage("Testing route '"+routeName+"'.");
	}
}
