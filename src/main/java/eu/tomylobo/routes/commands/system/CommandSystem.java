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

package eu.tomylobo.routes.commands.system;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import eu.tomylobo.routes.commands.RoutesCommands;
import eu.tomylobo.routes.commands.TestCommands;
import eu.tomylobo.routes.commands.TravelCommands;

/**
 * The framework for command invocation.<br />
 * Command classes must be hardcoded for now.
 *
 * @author TomyLobo
 *
 */
public class CommandSystem {
	private final Map<String, Invoker> commands = new HashMap<String, Invoker>();

	public CommandSystem() {
		@SuppressWarnings("unchecked")
		Class<? extends CommandContainer>[] classes = (Class<? extends CommandContainer>[]) new Class[] {
			RoutesCommands.class,
			TravelCommands.class,
			TestCommands.class
		};

		for (Class<? extends CommandContainer> clazz : classes) {
			try {
				CommandContainer instance = clazz.newInstance();
				for (Method method : clazz.getMethods()) {
					if (method.isAnnotationPresent(Command.class)) {
						final Command commandAnnotation = method.getAnnotation(Command.class);
						final String[] permissions = commandAnnotation.permissions();

						commands.put(method.getName(), new Invoker(method, instance, permissions));
					}
					else if (method.isAnnotationPresent(NestedCommand.class)) {
						commands.put(method.getName(), new NestedInvoker(method, instance));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * Wraps up the command in a {@link Context} and dispatches it.
	 *
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 * @return
	 */
	public boolean dispatch(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		return dispatch(new Context(sender, command.getName(), label, args));
	}

	/**
	 * Dispatches the given context.
	 *
	 * @param context The context to dispatch
	 * @return false if context is null or no closure was found. true otherwise.
	 */
	public boolean dispatch(Context context) {
		if (context == null)
			return false;

		Invoker closure = commands.get(context.getCommandName());
		if (closure == null)
			return false;

		try {
			closure.invoke(context);
		}
		catch (CommandException e) {
			context.sendMessage("\u00a7c"+e.getMessage());
		}
		catch (IllegalAccessException e) {
			context.sendMessage("\u00a7cInternal error while executing command.");
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			context.sendMessage("\u00a7cException caught while executing command.");
			cause.printStackTrace();
		}
		return true;
	}
}
