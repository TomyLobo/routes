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

package eu.tomylobo.routes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import eu.tomylobo.routes.commands.Commands;

public class Routes extends JavaPlugin {
	private static Routes instance;
	{
		instance = this;
	}

	public static Routes getInstance() {
		return instance;
	}

	public final Commands commands = new Commands(this);

	public TravelAgency travelAgency;

	public final TransportSystem transportSystem = new TransportSystem(this);

	@Override
	public void onEnable() {
		travelAgency = new TravelAgency(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commands.dispatch(sender, command, label, args);
	}

}
