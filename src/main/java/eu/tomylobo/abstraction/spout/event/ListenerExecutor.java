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

package eu.tomylobo.abstraction.spout.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spout.api.event.EventExecutor;
import org.spout.api.exception.EventException;

public class ListenerExecutor implements EventExecutor {
	private final Object listener;
	private final Method handler;
	private final SpoutEvent spoutEvent;
	private final boolean ignoreCancelled;

	public ListenerExecutor(SpoutEvent spoutEvent, Object listener, Method handler, boolean ignoreCancelled) {
		this.spoutEvent = spoutEvent;
		this.listener = listener;
		this.handler = handler;
		this.ignoreCancelled = ignoreCancelled;
	}

	@Override
	public void execute(org.spout.api.event.Event backend) throws EventException {
		if (ignoreCancelled && backend.isCancelled())
			return;

		try {
			spoutEvent.invoke(listener, handler, backend);
		} catch (IllegalAccessException e) {
			throw new EventException("Could not access wrapper", e);
		} catch (InvocationTargetException e) {
			throw new EventException("Exception caught from wrapper", e.getCause());
		}
	}
}
