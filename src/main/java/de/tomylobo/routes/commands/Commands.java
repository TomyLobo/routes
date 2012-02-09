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

package de.tomylobo.routes.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import de.tomylobo.routes.Routes;

public class Commands {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Command {
	}

	private static final Map<String, Method> commands = new HashMap<String, Method>();

	static {
		for (Method method : Command.class.getMethods()) {
			if (!method.isAnnotationPresent(Command.class))
				continue;

			commands.put(method.getName(), method);
		}
	}

	@SuppressWarnings("unused")
	private Routes plugin;

	public Commands(Routes plugin) {
		this.plugin = plugin;
	}

	public boolean dispatch(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		Method method = commands.get(command.getName());
		if (method == null)
			return false;

		try {
			method.invoke(this, sender, label, args);
		}
		catch (IllegalAccessException e) {
			return false;
		}
		catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			if (cause instanceof CommandException) {
				sender.sendMessage("\u00a73"+cause.getMessage());
			}
			else {
				sender.sendMessage("\u00a73Exception caught while executing command.");
			}

			return true;
		}
		return true;
	}

	@Command
	public void routes(CommandSender sender, String label, String[] args) {
		throw new CommandException("hello");
	}
}
