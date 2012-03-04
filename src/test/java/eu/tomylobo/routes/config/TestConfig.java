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

import org.bukkit.entity.EntityType;

@ConfigFile("testconfig.txt")
public class TestConfig extends Config {
	@ConfigItem("testsection.teststring")
	public String test = "testvalue";

	@ConfigItem("testsection.testfloat")
	public float test2 = 1.234f;
	
	@ConfigItem("testsection.testint")
	public int test3 = 1234;

	@ConfigItem("testsection.testentitytype")
	public EntityType test4 = EntityType.ENDER_DRAGON;

	@ConfigItem("testsection.testboolean")
	public boolean test5 = true;
}
