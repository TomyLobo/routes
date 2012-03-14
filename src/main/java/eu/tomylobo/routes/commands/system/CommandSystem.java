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

import eu.tomylobo.abstraction.CommandSender;

/**
 * The framework for command invocation.<br />
 * Command classes must be hardcoded for now.
 *
 * @author TomyLobo
 *
 */
public class CommandSystem {
	private final Map<String, Invoker> commands = new HashMap<String, Invoker>();

	/**
	 * Registers a command class instance for all players.
	 *
	 * @param instances
	 */
	public void register(Object... instances) {
		for (Object instance : instances) {
			for (Method method : instance.getClass().getMethods()) {
				if (method.isAnnotationPresent(Command.class)) {
					final Command commandAnnotation = method.getAnnotation(Command.class);
					final String[] permissions = commandAnnotation.permissions();

					final Invoker invoker = new Invoker(this, method, instance, permissions);

					if (commandAnnotation.names().length > 0) {
						registerInvoker(invoker, commandAnnotation.names());
					}
					else {
						registerInvoker(invoker, method.getName());
					}
				}
				else if (method.isAnnotationPresent(NestedCommand.class)) {
					final NestedCommand nestedCommandAnnotation = method.getAnnotation(NestedCommand.class);

					final Invoker invoker = new NestedInvoker(this, method, instance);

					if (nestedCommandAnnotation.names().length > 0) {
						registerInvoker(invoker, nestedCommandAnnotation.names());
					}
					else {
						registerInvoker(invoker, method.getName());
					}
				}
			}
		}
	}

	/**
	 * Registers a command class, where each player has their own instance.
	 *
	 * @param instances
	 */
	public <T> void registerPlayerMap(Class<T> cls, Map<? extends CommandSender, T> map) {
		for (Method method : cls.getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				final Command commandAnnotation = method.getAnnotation(Command.class);
				final String[] permissions = commandAnnotation.permissions();

				final Invoker invoker = new SenderMappedInvoker(this, method, map, permissions);

				if (commandAnnotation.names().length > 0) {
					registerInvoker(invoker, commandAnnotation.names());
				}
				else {
					registerInvoker(invoker, method.getName());
				}
			}
			else if (method.isAnnotationPresent(NestedCommand.class)) {
				throw new RuntimeException("Cannot have @NestedCommand on "+method+", because it was registered with registerPlayerMap.");
			}
		}
	}

	private void registerInvoker(Invoker invoker, String... names) {
		for (String name : names) {
			commands.put(name, invoker);
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
	public boolean dispatch(CommandSender sender, String commandName, String label, String[] args) {
		return dispatch(new Context(sender, commandName, label, args));
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
