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

package eu.tomylobo.routes.config;

import java.io.File;

import org.junit.*;

import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.abstraction.plugin.AbstractMetaPlugin;
import static org.junit.Assert.*;

public class ConfigTest {
	@Test
	public void configTest() {
		final TestConfig config = new TestConfig(new AbstractMetaPlugin() {
			@Override
			public File getDataFolder() {
				return new File(".");
			}
		});

		config.save();
		config.test = "falsch";
		config.test2 = -1;
		config.test3 = -1;
		config.test4 = VehicleType.BOAT;
		config.test5 = false;
		config.load();

		assertEquals("testvalue", config.test);
		assertEquals(1.234f, config.test2, 0);
		assertEquals(1234, config.test3);
		assertEquals(MobType.ENDER_DRAGON, config.test4);
		assertTrue(config.test5);
	}
}
