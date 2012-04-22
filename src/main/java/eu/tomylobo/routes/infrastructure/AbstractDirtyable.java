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

package eu.tomylobo.routes.infrastructure;

import java.util.Map;
import java.util.WeakHashMap;

public class AbstractDirtyable implements Dirtyable {
	private boolean dirty = false;
	private final Map<Dirtyable, Boolean> dirtyClients = new WeakHashMap<Dirtyable, Boolean>();

	@Override
	public void setDirty() {
		dirty = true;
		for (Dirtyable client : dirtyClients.keySet()) {
			client.setDirty();
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void ensureClean() {
		if (!dirty)
			return;

		dirty = false;
		for (Dirtyable client : dirtyClients.keySet()) {
			client.ensureClean();
		}
	}

	@Override
	public void addDirtyClient(Dirtyable client) {
		dirtyClients.put(client, true);
	}
}
