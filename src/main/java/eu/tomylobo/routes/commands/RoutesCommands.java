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

import org.bukkit.command.CommandSender;

import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;

/**
 * Contains all commands connected to route management.
 *
 * @author TomyLobo
 *
 */
public class RoutesCommands extends CommandContainer {
	@NestedCommand
	public void routes(CommandSender sender, String commandName, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("/"+label+" expects a sub-command.");
		}
		else {
			sender.sendMessage("Could not find the specified /"+label+" sub-command.");
		}
	}
}
