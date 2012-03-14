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
import java.util.Map;

import eu.tomylobo.abstraction.CommandSender;

public class SenderMappedInvoker extends Invoker {
	private final Map<? extends CommandSender, ?> map;

	public <T> SenderMappedInvoker(CommandSystem commandSystem, Method method, Map<? extends CommandSender, T> map, String[] permissions) {
		super(commandSystem, method, null, permissions);
		this.map = map;
	}

	@Override
	public void invoke(Context context) throws CommandException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object instance = map.get(context.getSender());
		if (instance == null)
			throw new CommandException("You cannot currently run this command.");

		invokeInternal(context, instance);
	}
}
