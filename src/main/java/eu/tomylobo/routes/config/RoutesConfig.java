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

@ConfigFile("config.txt")
public class RoutesConfig extends Config {
	// [editor]

	@ConfigItem("editor.tool")
	public int editorTool = 284; // GOLD_SPADE;

	@ConfigItem("editor.dotsPerMeter")
	public double editorDotsPerMeter = 1.0;

	@ConfigItem("editor.flashTicks")
	public long editorFlashTicks = 5;

	@ConfigItem("editor.selectRange")
	public double editorSelectRange = 32;

	// [signs]

	@ConfigItem("signs.routePrefix")
	public String signsRoutePrefix = "@@";

	// [show]

	@ConfigItem("show.dotsPerMeter")
	public double showDotsPerMeter = 1.0;

	@ConfigItem("show.flashTicks")
	public long showFlashTicks = 10;

	@ConfigItem("show.ticks")
	public long showTicks = 600;

	@ConfigItem("travel.maxDistance")
	public double travelMaxDistance = 16;

	@ConfigItem("travel.signMaxDistance")
	public double travelSignMaxDistance = 128;
}
