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

package eu.tomylobo.routes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


import eu.tomylobo.routes.util.Ini;

/**
 * Contains the transport network, i.e. all routes and their names.
 *
 * @author TomyLobo
 *
 */
public class TransportSystem {
	private final Routes plugin;
	private final Map<String, Route> routes = new HashMap<String, Route>();

	public TransportSystem(Routes plugin) {
		this.plugin = plugin;
	}

	public Route getRoute(String routeName) {
		return routes.get(routeName);
	}

	public void addRoute(String routeName, Route route) {
		routes.put(routeName, route);
		save();
	}

	public void save() {
		Multimap<String, Multimap<String, String>> sections = LinkedListMultimap.create();
		for (Entry<String, Route> entry : routes.entrySet()) {
			entry.getValue().save(sections, entry.getKey());
		}

		Ini.save(plugin.getConfigFileName("routes.txt"), sections);
	}

	public void load() {
		final Multimap<String, Multimap<String, String>> sections = Ini.load(plugin.getConfigFileName("routes.txt"));

		for (Entry<String, Multimap<String, String>> entry : sections.entries()) {
			final String sectionName = entry.getKey();
			if (!sectionName.startsWith("route "))
				continue;

			final String routeName = sectionName.substring(6);
			final Route route = new Route();

			route.load(sections, routeName);

			routes.put(routeName, route);
		}
	}
}
