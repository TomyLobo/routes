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
import java.util.Arrays;

import org.bukkit.command.CommandSender;

/**
 * Invokes nested commands instead of invoking the instance directly.
 *
 * @author TomyLobo
 *
 */
public class NestedInvoker extends Invoker {
	public NestedInvoker(Method method, CommandContainer instance) {
		super(method, instance);
	}

	@Override
	public void invoke(CommandSender sender, String commandName, String label, String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (args.length < 1) {
			super.invoke(sender, commandName, label, args);
			return;
		}

		final String subCommandName = commandName+"_"+args[0];
		final String subLabel = commandName+" "+args[0];
		final String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

		if (instance.plugin.commandSystem.dispatch(sender, subCommandName, subLabel, subArgs))
			return;

		super.invoke(sender, commandName, label, args);
	}
}
