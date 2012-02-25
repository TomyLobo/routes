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

import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.util.Ini;

public class TrackedSign {
	private final Block block;
	private final String[] entries;
	private int selected = -1;

	public TrackedSign(Sign sign) {
		block = sign.getBlock();
		String[] lines = sign.getLines();
		entries = new String[lines.length];

		for (int i = 0; i < lines.length; ++i) {
			final String line = lines[i];

			if (line.startsWith("@@")) {
				entries[i] = line.substring(2);
				setMarked(sign, i, false);
			}
		}

		sign.update();
	}

	public TrackedSign(Multimap<String, String> section) {
		block = Ini.loadLocation(section, "%s", false).getBlock();

		entries = new String[4];
		for (int i = 0; i < 4; ++i) {
			final Collection<String> entries2 = section.get("line"+i);
			if (entries2.size() != 1)
				continue;

			entries[i] = Ini.getOnlyValue(entries2);
		}
	}

	public Multimap<String, String> save() {
		Multimap<String, String> section = LinkedListMultimap.create();

		Ini.saveLocation(section, "%s", block.getLocation(), false);

		for (int i = 0; i < entries.length; ++i) {
			if (entries[i] == null)
				continue;

			section.put("line"+i, entries[i]);
		}

		return section;
	}

	public Block getBlock() {
		return block;
	}

	public boolean hasIndex(int index) {
		return entries[index] != null;
	}

	/**
	 * Retrieves the specified entry if there is one, otherwise null.
	 *
	 * @param index The index of the entry 0..3
	 * @return The entry, if it is tracked, otherwise null. 
	 */
	public String getEntry(int index) {
		return entries[index];
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrackedSign))
			return false;

		TrackedSign trackedSign = (TrackedSign) obj;

		return block.equals(trackedSign.block);
	}

	@Override
	public int hashCode() {
		return block.hashCode();
	}

	private static final String UNMARKED_COLOR = "\u00a79";
	private static final String MARKED_COLOR = "\u00a7c";
	public void mark(int index) {
		if (selected == index)
			return;

		final Sign sign = (Sign) block.getState();

		if (selected != -1) {
			setMarked(sign, selected, false);
		}
		selected = index;
		if (selected != -1) {
			if (entries[selected] == null) {
				selected = -1;
			}
			else {
				setMarked(sign, selected, true);
			}
		}

		sign.update();
	}

	private void setMarked(final Sign sign, int index, boolean marked) {
		sign.setLine(index, (marked ? MARKED_COLOR : UNMARKED_COLOR) + entries[index]);
	}

	public boolean isMarked(int index) {
		return selected == index;
	}
}
