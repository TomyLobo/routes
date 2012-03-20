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

import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.Context;
import eu.tomylobo.routes.commands.system.CommandContainer;

/**
 * Contains all commands connected to travelling management.
 *
 * @author TomyLobo
 *
 */
public class TravelCommands extends CommandContainer {
	/**
	 * Puts you on a dragon, travelling along the specified route.
	 */
	@Command(usage = "<route>", permissions = "routes.travel")
	public void travel(Context context) {
		final String routeName = context.getString(0);

		plugin.travelAgency.addTravellerWithMount(routeName, context.getPlayer(), MobType.ENDER_DRAGON, "travel");
		context.sendMessage("Travelling on route '"+routeName+"'.");
	}
}
