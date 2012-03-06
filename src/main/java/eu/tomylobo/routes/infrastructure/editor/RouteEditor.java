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

package eu.tomylobo.routes.infrastructure.editor;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.tomylobo.abstraction.Player;
import eu.tomylobo.abstraction.bukkit.BukkitUtils;
import eu.tomylobo.routes.BukkitRoutes;
import eu.tomylobo.routes.infrastructure.Route;

public class RouteEditor implements Listener {
	private final Map<Player, RouteEditSession> editedRoutes = new HashMap<Player, RouteEditSession>();

	private final BukkitRoutes plugin;

	public RouteEditor(BukkitRoutes plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// TODO: convert event handler
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Result.DENY)
			return;

		final Player player = BukkitUtils.wrap(event.getPlayer());

		final int inHand = player.getItemTypeInHand();
		if (inHand == plugin.config.editorTool) {
			final RouteEditSession routeEditSession = editedRoutes.get(player);
			if (routeEditSession == null)
				return;

			routeEditSession.interact(event);
		}
	}

	// TODO: convert event handler
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		final RouteEditSession routeEditSession = editedRoutes.remove(event.getPlayer());
		if (routeEditSession != null) {
			routeEditSession.close();
		}
	}

	/**
	 * Creates a new edit session for the player and returns it.<br />
	 * Closes the player's previous edit session.
	 *
	 * @param player
	 * @param route The route to edit
	 * @return the new edit session
	 */
	public RouteEditSession edit(Player player, Route route) {
		final RouteEditSession oldSession = editedRoutes.remove(player);
		if (oldSession != null) {
			oldSession.close();
		}

		final RouteEditSession newSession = new RouteEditSession(player, route);
		editedRoutes.put(player, newSession);
		return newSession;
	}

	/**
	 * Closes the player's current edit session.
	 *
	 * @param player
	 * @return
	 */
	public void close(Player player) {
		final RouteEditSession oldSession = editedRoutes.remove(player);
		if (oldSession != null) {
			oldSession.close();
		}
	}
}
