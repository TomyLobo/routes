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

package de.tomylobo.routes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Entity;

public class TravelAgency implements Runnable {
	@SuppressWarnings("unused")
	private final Routes plugin;
	public final Map<Entity, Traveller> travellers = new HashMap<Entity, Traveller>();

	public TravelAgency(Routes plugin) {
		this.plugin = plugin;

		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
	}

	public void addTraveller(Entity entity, Route route, Runnable finalizer) {
		addTraveller(new Traveller(this, entity, route, finalizer));
	}

	public void addTraveller(Traveller traveller) {
		travellers.put(traveller.getEntity(), traveller);
	}

	@Override
	public void run() {
		for (Iterator<Traveller> it = travellers.values().iterator(); it.hasNext(); ) {
			Traveller traveller = it.next();
			if (!traveller.tick())
				it.remove();
		}
	}
}
