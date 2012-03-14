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

package eu.tomylobo.abstraction.platform.bukkit.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class BukkitSimpleEventExecutor implements Listener, EventExecutor {
	private final Object listener;
	private final Method handler;

	public BukkitSimpleEventExecutor(Object listener, Method handler) {
		this.listener = listener;
		this.handler = handler;
	}

	@Override
	public void execute(Listener me, org.bukkit.event.Event backend) throws EventException {
		if (me != this)
			throw new EventException("ListenerExecutor got passed a different listener!");

		try {
			handler.invoke(listener,  backend);
		} catch (IllegalAccessException e) {
			throw new EventException(e, "Could not access handler");
		} catch (InvocationTargetException e) {
			throw new EventException(e.getCause(), "Exception caught from handler");
		}
	}
}
