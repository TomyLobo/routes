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

package eu.tomylobo.routes.sign;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.trace.SignShape;
import eu.tomylobo.routes.trace.SignTraceResult;
import eu.tomylobo.routes.util.Ini;

public class SignHandler implements Listener {
	private final Routes plugin;
	private Map<Block, TrackedSign> trackedSigns = new HashMap<Block, TrackedSign>();
	public Map<Player, SignSession> sessions = new HashMap<Player, SignSession>();

	public SignHandler(Routes plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private SignSession getOrCreateSession(Player player) {
		SignSession session = sessions.get(player);
		if (session == null) {
			sessions.put(player, session = new SignSession(this, player));
		}
		return session;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {
		final Block block = event.getBlock();
		final Sign sign = (Sign) block.getState();

		boolean hasDestinations = false;
		for (int i = 0; i < 4; ++i) {
			final String line = event.getLine(i);
			sign.setLine(i, line);
			if (line.startsWith(plugin.config.signsRoutePrefix)) {
				hasDestinations = true;
			}
		}

		if (hasDestinations) {
			trackedSigns.put(sign.getBlock(), new TrackedSign(sign));
			save();

			event.getPlayer().sendMessage("Added tracked sign.");
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		final TrackedSign trackedSign = trackedSigns.remove(block);
		if (trackedSign == null)
			return;

		save();

		event.getPlayer().sendMessage("Broke tracked sign");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return; // We only want RIGHT_CLICK_BLOCK

		final Block block = event.getClickedBlock();
		if (block == null)
			return; // This can't really happen, but check for it anyway, just in case someone dispatches a broken event...

		final Material type = block.getType();
		if (type != Material.WALL_SIGN && type != Material.SIGN_POST)
			return; // We only want signs

		TrackedSign trackedSign = trackedSigns.get(block);
		if (trackedSign == null)
			return; // We only want tracked signs

		final Player player = event.getPlayer();
		final Location eyeLocation = player.getEyeLocation();

		final SignShape shape = new SignShape((Sign) block.getState());
		final SignTraceResult trace = shape.trace(eyeLocation);

		final int index = trace.index;

		if (index < 0 || index >= 4)
			return; // We only want valid indexes

		if (!trackedSign.hasEntry(index))
			return; // We only want indexes for which the sign has a tracked entry

		final SignSession session = getOrCreateSession(player);
		if (session.isSelected(trackedSign, index)) {
			session.close();

			final String routeName = trackedSign.getEntry(index);

			try {
				plugin.travelAgency.addTravellerWithMount(routeName, player, MobType.ENDER_DRAGON);
				player.sendMessage("Travelling on route '"+routeName+"'.");
			}
			catch (CommandException e) {
				player.sendMessage(e.getMessage());
			}
		}
		else {
			session.select(trackedSign, index);
		}
	}

	public void save() {
		Multimap<String, Multimap<String, String>> sections = LinkedListMultimap.create();
		for (Entry<Block, TrackedSign> entry : trackedSigns.entrySet()) {
			final TrackedSign trackedSign = entry.getValue();

			sections.put("sign", trackedSign.save());
		}

		Ini.save(plugin.getConfigFileName("signs.txt"), sections);
	}

	public void load() {
		trackedSigns.clear();

		final Multimap<String, Multimap<String, String>> sections = Ini.load(plugin.getConfigFileName("signs.txt"));
		if (sections == null)
			return;

		for (Multimap<String, String> section : sections.get("sign")) {
			final TrackedSign trackedSign = new TrackedSign(section);

			trackedSigns.put(trackedSign.getBlock(), trackedSign);
		}
	}
}
