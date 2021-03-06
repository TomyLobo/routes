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

package eu.tomylobo.abstraction.plugin;

import java.io.File;

import eu.tomylobo.abstraction.CommandSender;
import eu.tomylobo.routes.commands.system.CommandSystem;

public interface MetaPlugin {
	FrameworkPlugin getFrameworkPlugin();
	void onLoad();
	void onEnable();
	void onDisable();
	boolean onCommand(CommandSender sender, String commandName, String label, String[] args);
	void setFrameworkPlugin(FrameworkPlugin plugin);
	CommandSystem getCommandSystem();
	File getDataFolder();
}
