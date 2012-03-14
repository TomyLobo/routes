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

package eu.tomylobo.abstraction.platform.bukkit.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import eu.tomylobo.abstraction.block.Sign;
import eu.tomylobo.abstraction.event.Event;
import eu.tomylobo.abstraction.platform.bukkit.BukkitUtils;

public enum BukkitEvent {
	onBukkitEvent(org.bukkit.event.Event.class) {
		@Override public Event wrap(org.bukkit.event.Event backend) {
			return null;
		}

		@Override
		public void register(Object listener, Method method, Plugin bukkitPlugin, EventPriority bukkitPriority, boolean ignoreCancelled) {
			BukkitSimpleEventExecutor listenerExecutor = new BukkitSimpleEventExecutor(listener, method);

			@SuppressWarnings("unchecked")
			final Class<? extends org.bukkit.event.Event> eventClass = (Class<? extends org.bukkit.event.Event>) method.getParameterTypes()[0];
			org.bukkit.Bukkit.getPluginManager().registerEvent(eventClass, listenerExecutor, bukkitPriority, listenerExecutor, bukkitPlugin, ignoreCancelled);
		}
	},
	onSignChange(SignChangeEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		final Event event = new Event();

		final SignChangeEvent signChangeEvent = (SignChangeEvent) backend;
		final Block block = signChangeEvent.getBlock();

		event.setLocation(BukkitUtils.wrap(block.getLocation()));
		event.setBlockState(new Sign(block.getTypeId(), block.getData(), signChangeEvent.getLines()));
		event.setPlayer(BukkitUtils.wrap(signChangeEvent.getPlayer()));

		return event;
	}},

	onBlockPlace(BlockPlaceEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		final BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) backend;
		return BukkitEventUtils.wrap(blockPlaceEvent)
				.setPlayer(BukkitUtils.wrap(blockPlaceEvent.getPlayer()));
	}},

	onBlockBreak(BlockBreakEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		final BlockBreakEvent blockBreakEvent = (BlockBreakEvent) backend;
		return BukkitEventUtils.wrap(blockBreakEvent)
				.setPlayer(BukkitUtils.wrap(blockBreakEvent.getPlayer()));
	}},

	onPlayerJoin(PlayerJoinEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		return BukkitEventUtils.wrap((PlayerJoinEvent) backend);
	}},

	onPlayerQuit(PlayerQuitEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		return BukkitEventUtils.wrap((PlayerQuitEvent) backend);
	}},

	onPlayerClick(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK_BLOCK:
		case LEFT_CLICK_BLOCK:
		case RIGHT_CLICK_AIR:
		case LEFT_CLICK_AIR:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK_BLOCK:
		case LEFT_CLICK_BLOCK:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerLeftClick(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case LEFT_CLICK_BLOCK:
		case LEFT_CLICK_AIR:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerLeftClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case LEFT_CLICK_BLOCK:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerRightClick(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK_BLOCK:
		case RIGHT_CLICK_AIR:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerRightClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.bukkit.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK_BLOCK:
			return BukkitEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},
	;

	private final Class<? extends org.bukkit.event.Event> bukkitEventClass;

	private BukkitEvent(Class<? extends org.bukkit.event.Event> bukkitEventClass) {
		this.bukkitEventClass = bukkitEventClass;
	}

	public Class<? extends org.bukkit.event.Event> getBukkitEventClass() {
		return bukkitEventClass;
	}


	public abstract Event wrap(org.bukkit.event.Event backend);

	public void invoke(Object listener, Method handler, org.bukkit.event.Event backend) throws IllegalAccessException, InvocationTargetException {
		final Event event = wrap(backend);
		if (event == null)
			return;

		if (backend instanceof Cancellable) {
			final boolean oldCancelled;
			if (backend instanceof PlayerInteractEvent) {
				PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) backend;
				oldCancelled = playerInteractEvent.useInteractedBlock() == Result.DENY && playerInteractEvent.useItemInHand() == Result.DENY;
			}
			else {
				oldCancelled = ((Cancellable) backend).isCancelled();
			}
			event.setCancelled(oldCancelled);
			handler.invoke(listener, event);
			if (oldCancelled != event.isCancelled()) {
				((Cancellable) backend).setCancelled(event.isCancelled());
			}
		}
		else {
			handler.invoke(listener, event);
		}
	}

	public void register(Object listener, Method method, org.bukkit.plugin.Plugin bukkitPlugin, EventPriority bukkitPriority, final boolean ignoreCancelled) {
		BukkitEventTransposer listenerExecutor = new BukkitEventTransposer(this, listener, method);

		org.bukkit.Bukkit.getPluginManager().registerEvent(bukkitEventClass, listenerExecutor, bukkitPriority, listenerExecutor, bukkitPlugin, ignoreCancelled);
	}
}
