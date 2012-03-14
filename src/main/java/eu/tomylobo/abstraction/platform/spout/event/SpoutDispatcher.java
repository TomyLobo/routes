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

package eu.tomylobo.abstraction.platform.spout.event;

import java.lang.reflect.Method;

import org.spout.api.event.Order;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.event.Dispatcher;
import eu.tomylobo.abstraction.event.EventHandler;
import eu.tomylobo.abstraction.event.Platform;
import eu.tomylobo.abstraction.plugin.FrameworkPlugin;
import eu.tomylobo.abstraction.plugin.MetaPlugin;

public class SpoutDispatcher implements Dispatcher {
	Order[] priorityMap = { Order.EARLIEST, Order.EARLY, Order.DEFAULT, Order.LATE, Order.LATEST, Order.MONITOR };
	Order[] priorityMapCancelled = { Order.EARLIEST, Order.EARLY_IGNORE_CANCELLED, Order.DEFAULT_IGNORE_CANCELLED, Order.LATE_IGNORE_CANCELLED, Order.LATEST_IGNORE_CANCELLED, Order.MONITOR };

	@Override
	public void registerEvents(Object listener, MetaPlugin plugin) {
		final FrameworkPlugin frameworkPlugin = plugin.getFrameworkPlugin();

		final Class<? extends Object> listenerClass = listener.getClass();
		final Platform platform = listenerClass.getAnnotation(Platform.class);
		if (platform != null && !Environment.isPlatform(platform.value()))
			return;

		for (Method method : listenerClass.getMethods()) {
			final EventHandler eventHandler = method.getAnnotation(EventHandler.class);

			if (eventHandler == null)
				continue;

			String eventName = eventHandler.event();
			if (EventHandler.USE_METHOD_NAME.equals(eventName)) {
				eventName = method.getName();
			}

			final SpoutEvent spoutEvent;
			try {
				spoutEvent = SpoutEvent.valueOf(eventName);
			} catch (IllegalArgumentException e) {
				continue;
			}

			final eu.tomylobo.abstraction.event.EventPriority priority = eventHandler.priority();
			final boolean ignoreCancelled = eventHandler.ignoreCancelled();

			final Order spoutPriority = (ignoreCancelled ? priorityMapCancelled : priorityMap)[priority.getIndex()];

			spoutEvent.register(listener, method, frameworkPlugin, spoutPriority, ignoreCancelled);
		}
	}
}
