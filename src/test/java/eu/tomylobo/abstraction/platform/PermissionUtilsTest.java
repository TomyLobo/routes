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

package eu.tomylobo.abstraction.platform;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

import eu.tomylobo.abstraction.platform.bukkit.BukkitCommandSender;

public class PermissionUtilsTest {
	public static class TestCommandSender extends BukkitCommandSender {
		private final Set<String> permissions = new HashSet<String>();

		public TestCommandSender(String... permissions) {
			super(null);
			this.permissions.addAll(Arrays.asList(permissions));
		}

		@Override
		public boolean hasExactPermission(String permission) {
			return permissions.contains(permission);
		}
	}

	@Test
	public void hasPermissionTest() {
		assertTrue(new TestCommandSender("*").hasPermission("foo.bar.baz"));
		assertTrue(new TestCommandSender("foo.*").hasPermission("foo.bar.baz"));
		assertTrue(new TestCommandSender("foo.bar.*").hasPermission("foo.bar.baz"));
		assertFalse(new TestCommandSender("foo.bar.baz.*").hasPermission("foo.bar.baz"));
	}
}
