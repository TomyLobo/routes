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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.fakeentity.FakeEntity;
import eu.tomylobo.routes.fakeentity.FakeMob;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.trace.SignShape;
import eu.tomylobo.routes.trace.SignTraceResult;
import eu.tomylobo.routes.util.Ini;
import eu.tomylobo.routes.util.Remover;
import eu.tomylobo.routes.util.Workarounds;

public class SignHandler implements Listener {
	private final Routes plugin;
	private Map<Block, TrackedSign> trackedSigns = new HashMap<Block, TrackedSign>();

	public SignHandler(Routes plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		final Block block = event.getBlock();
		final Sign sign = (Sign) block.getState();

		boolean hasDestinations = false;
		for (int i = 0; i < 4; ++i) {
			final String line = event.getLine(i);
			sign.setLine(i, line);
			if (line.startsWith("@@")) {
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

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		final TrackedSign trackedSign = trackedSigns.remove(block);
		if (trackedSign == null)
			return;

		save();

		event.getPlayer().sendMessage("Broke tracked sign");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			final Player player = event.getPlayer();

			switch (player.getItemInHand().getType()) {
			case ARROW:
				final Block block = event.getClickedBlock();
				if (block == null)
					return;

				TrackedSign trackedSign = trackedSigns.get(block);
				if (trackedSign == null)
					return;

				final Sign sign = (Sign) block.getState();
				final SignShape shape = new SignShape(sign);

				final Location eyeLocation = Workarounds.getEyeLocation(player);
				final SignTraceResult trace = shape.trace(eyeLocation);

				final int index = trace.index;

				if (index < 0 || index >= 4)
					return;

				if (trackedSign.isSelected(index)) {
					trackedSign.select(-1);

					final String routeName = trackedSign.getEntry(index);

					FakeEntity dragon = new FakeMob(eyeLocation, MobType.ENDER_DRAGON);
					dragon.send();
					dragon.setPassenger(player);

					plugin.travelAgency.addTraveller(routeName, dragon, 5.0, new Remover(dragon));
					player.sendMessage("Travelling on route '"+routeName+"'.");
				}
				else {
					trackedSign.select(index);
				}

				break;
			}

			break;
		}
	}

	public void save() {
		Multimap<String, Multimap<String, String>> sections = LinkedListMultimap.create();
		for (Entry<Block, TrackedSign> entry : trackedSigns.entrySet()) {
			sections.put("sign", entry.getValue().save());
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
