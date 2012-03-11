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

package eu.tomylobo.abstraction.bukkit.event;

import java.lang.reflect.Method;

import org.bukkit.event.EventPriority;

import eu.tomylobo.abstraction.event.Dispatcher;
import eu.tomylobo.abstraction.event.EventHandler;
import eu.tomylobo.abstraction.plugin.MetaPlugin;

public class BukkitDispatcher implements Dispatcher {
	EventPriority[] priorityMap = { EventPriority.LOWEST, EventPriority.LOW, EventPriority.NORMAL, EventPriority.HIGH, EventPriority.HIGHEST, EventPriority.MONITOR };

	@Override
	public void registerEvents(Object listener, MetaPlugin plugin) {
		org.bukkit.plugin.Plugin bukkitPlugin =  (org.bukkit.plugin.Plugin) plugin.getFrameworkPlugin();

		for (Method method : listener.getClass().getMethods()) {
			EventHandler eventHandler = method.getAnnotation(EventHandler.class);

			if (eventHandler == null)
				continue;

			String eventName = eventHandler.event();
			if (EventHandler.USE_METHOD_NAME.equals(eventName)) {
				eventName = method.getName();
			}

			final BukkitEvent bukkitEvent = BukkitEvent.valueOf(eventName);
			Class<? extends org.bukkit.event.Event> eventClass = bukkitEvent.getBukkitEventClass();

			final eu.tomylobo.abstraction.event.EventPriority priority = eventHandler.priority();
			EventPriority bukkitPriority = priorityMap[priority.getIndex()];

			BukkitEventTransposer listenerExecutor = new BukkitEventTransposer(bukkitEvent, listener, method);

			final boolean ignoreCancelled = eventHandler.ignoreCancelled();

			org.bukkit.Bukkit.getPluginManager().registerEvent(eventClass, listenerExecutor, bukkitPriority, listenerExecutor, bukkitPlugin, ignoreCancelled);
		}
	}
}
