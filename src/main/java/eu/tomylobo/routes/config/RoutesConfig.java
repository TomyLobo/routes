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

	/**
	 * The item ID of the tool the editor uses.
	 */
	@ConfigItem("editor.tool")
	public int editorTool = 284; // GOLD_SPADE;

	/**
	 * The number of dots per meter that are displayed in the route editor.
	 */
	@ConfigItem("editor.dotsPerMeter")
	public double editorDotsPerMeter = 1.0;

	/**
	 * The number of ticks between the selected segment in the route editor flashing on and off.
	 */
	@ConfigItem("editor.flashTicks")
	public long editorFlashTicks = 5;

	/**
	 * The maximum range the editor tool can select something at in select mode.
	 */
	@ConfigItem("editor.selectRange")
	public double editorSelectRange = 32;

	// [show]

	/**
	 * The number of dots per meter that are displayed for <code>/routes show</code>.
	 */
	@ConfigItem("show.dotsPerMeter")
	public double showDotsPerMeter = 1.0;

	/**
	 * The number of ticks between the selected segment in <code>/routes show</code> flashing on and off.
	 */
	@ConfigItem("show.flashTicks")
	public long showFlashTicks = 10;

	/**
	 * The number of ticks <code>/routes show</code> shows the route.
	 */
	@ConfigItem("show.ticks")
	public long showTicks = 600;

	// [travel]

	/**
	 * The maximum distance the player can be from the first node of a route when using <code>/travel</code>.
	 */
	@ConfigItem("travel.maxDistance")
	public double travelMaxDistance = 16;

	/**
	 * The maximum distance the player can be from the first node of a route when using a sign.
	 */
	@ConfigItem("travel.signMaxDistance")
	public double travelSignMaxDistance = 128;

	/**
	 * The prefix to be used on signs.<br />
	 * When using this prefix on a sign, the remaining part of the line will be interpreted as a route name and made clickable.
	 */
	@ConfigItem("travel.signPrefix")
	public String signsRoutePrefix = "@@";
}
