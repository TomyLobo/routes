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

import eu.tomylobo.abstraction.CommandSender;

/**
 * Wraps a method+instance to be invoked by the command system.
 *
 * @author TomyLobo
 *
 */
public class Invoker {
	protected final CommandSystem commandSystem;
	protected final Method method;
	protected final Object instance;
	protected final String[] permissions;

	public Invoker(CommandSystem commandSystem, Method method, Object instance, String[] permissions) {
		this.commandSystem = commandSystem;
		this.method = method;
		this.instance = instance;
		this.permissions = permissions;
	}

	public void invoke(Context context) throws CommandException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		invokeInternal(context, instance);
	}

	protected void invokeInternal(Context context, Object instance) throws IllegalAccessException, InvocationTargetException {
		if (!hasPermission(context.getSender()))
			throw new PermissionDeniedException();

		try {
			method.invoke(instance, context);
		}
		catch (InvocationTargetException e) {
			if (!(e.getCause() instanceof CommandException))
				throw e;

			throw (CommandException) e.getCause();
		}
	}

	protected boolean hasPermission(CommandSender sender) {
		for (String permission : permissions) {
			if (permission.equals(Command.DISABLED))
				return true;

			if (sender.hasPermission(permission))
				return true;
		}

		return false;
	}
}
