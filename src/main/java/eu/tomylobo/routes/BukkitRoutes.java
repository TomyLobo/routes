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

import eu.tomylobo.abstraction.bukkit.BukkitUtils;
import eu.tomylobo.routes.commands.system.CommandSystem;
import eu.tomylobo.routes.config.RoutesConfig;
import eu.tomylobo.routes.infrastructure.TransportSystem;
import eu.tomylobo.routes.infrastructure.editor.RouteEditor;
import eu.tomylobo.routes.sign.SignHandler;
import eu.tomylobo.routes.travel.TravelAgency;

public class BukkitRoutes extends JavaPlugin {
	private static BukkitRoutes instance;
	{
		instance = this;
	}

	public static BukkitRoutes getInstance() {
		return instance;
	}

	public RoutesConfig config = new RoutesConfig();

	public CommandSystem commandSystem;
	public TravelAgency travelAgency;
	public final TransportSystem transportSystem = new TransportSystem(this);
	public SignHandler signHandler;
	public RouteEditor routeEditor;

	@Override
	public void onEnable() {
		commandSystem = new CommandSystem();
		travelAgency = new TravelAgency(this);
		signHandler = new SignHandler(this);
		routeEditor = new RouteEditor(this);
		load();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandSystem.dispatch(BukkitUtils.wrap(sender), command.getName(), label, args);
	}

	public void save() {
		config.save();
		transportSystem.save();
		signHandler.save();
	}

	public void load() {
		config.load();
		transportSystem.load();
		signHandler.load();
	}

	public String getConfigFileName(String baseFileName) {
		return getDataFolder() + "/" + baseFileName;
	}
}
