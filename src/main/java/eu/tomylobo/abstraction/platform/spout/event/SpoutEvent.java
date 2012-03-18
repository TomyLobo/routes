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

package eu.tomylobo.abstraction.platform.spout.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.entity.Controller;
import org.spout.api.entity.PlayerController;
import org.spout.api.event.Cancellable;
import org.spout.api.event.Order;
import org.spout.api.event.block.BlockChangeEvent;
import org.spout.api.event.entity.EntityMoveEvent;
import org.spout.api.event.player.PlayerInteractEvent;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.event.player.PlayerLeaveEvent;

import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.event.Event;
import eu.tomylobo.abstraction.platform.spout.SpoutUtils;
import eu.tomylobo.abstraction.plugin.FrameworkPlugin;

public enum SpoutEvent {
	onSpoutEvent(org.spout.api.event.Event.class) {
		@Override
		public Event wrap(org.spout.api.event.Event backend) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void register(Object listener, Method method, FrameworkPlugin frameworkPlugin, Order spoutPriority, boolean ignoreCancelled) {
			SpoutSimpleEventExecutor listenerExecutor = new SpoutSimpleEventExecutor(listener, method);

			
			@SuppressWarnings("unchecked")
			final Class<? extends org.spout.api.event.Event> eventClass = (Class<? extends org.spout.api.event.Event>) method.getParameterTypes()[0];
			Spout.getEventManager().registerEvent(eventClass, spoutPriority, listenerExecutor, frameworkPlugin);
		}
	},
	onSignChange(BlockChangeEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		// TODO: spout
		return null;
		/*final Event event = new Event();

		final SignChangeEvent signChangeEvent = (SignChangeEvent) backend;
		final Block block = signChangeEvent.getBlock();

		event.setLocation(SpoutUtils.wrap(block.getLocation()));
		event.setBlockState(new (block.getTypeId(), block.getData(), signChangeEvent.getLines()));
		event.setPlayer(SpoutUtils.wrap(signChangeEvent.getPlayer()));

		return event;*/
	}},

	onBlockPlace(BlockChangeEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final BlockChangeEvent blockChangeEvent = (BlockChangeEvent) backend;
		if (isBreak(blockChangeEvent))
			return null;

		final Source source = blockChangeEvent.getSource();
		if (!(source instanceof org.spout.api.player.Player))
			return null;

		return SpoutEventUtils.wrap(blockChangeEvent)
				.setPlayer(SpoutUtils.wrap((org.spout.api.player.Player) source));
	}},

	onBlockBreak(BlockChangeEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final BlockChangeEvent blockChangeEvent = (BlockChangeEvent) backend;
		if (!isBreak(blockChangeEvent))
			return null;

		final Source source = blockChangeEvent.getSource();
		if (!(source instanceof org.spout.api.player.Player))
			return null;

		return SpoutEventUtils.wrap(blockChangeEvent)
				.setPlayer(SpoutUtils.wrap((org.spout.api.player.Player) source));
	}},

	onPlayerJoin(PlayerJoinEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		return SpoutEventUtils.wrap((PlayerJoinEvent) backend);
	}},

	onPlayerQuit(PlayerLeaveEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		return SpoutEventUtils.wrap((PlayerLeaveEvent) backend);
	}},

	onPlayerClick(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK:
		case LEFT_CLICK:
			return SpoutEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) backend;
		if (playerInteractEvent.isAir())
			return null;

		switch (playerInteractEvent.getAction()) {
		case RIGHT_CLICK:
		case LEFT_CLICK:
			return SpoutEventUtils.wrapClick(playerInteractEvent);
		default:
			return null;
		}
	}},

	onPlayerLeftClick(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case LEFT_CLICK:
			return SpoutEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerLeftClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) backend;
		if (playerInteractEvent.isAir())
			return null;

		switch (playerInteractEvent.getAction()) {
		case LEFT_CLICK:
			return SpoutEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerRightClick(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		switch (((PlayerInteractEvent) backend).getAction()) {
		case RIGHT_CLICK:
			return SpoutEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerRightClickBlock(PlayerInteractEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) backend;
		if (playerInteractEvent.isAir())
			return null;

		switch (playerInteractEvent.getAction()) {
		case RIGHT_CLICK:
			return SpoutEventUtils.wrapClick((PlayerInteractEvent) backend);
		default:
			return null;
		}
	}},

	onPlayerMove(EntityMoveEvent.class) { @Override public Event wrap(org.spout.api.event.Event backend) {
		final EntityMoveEvent entityMoveEvent = (EntityMoveEvent) backend;
		org.spout.api.entity.Entity entity = entityMoveEvent.getEntity();
		Controller controller = entity.getController();

		if (!(controller instanceof PlayerController))
			return null;

		org.spout.api.player.Player spoutPlayer = ((PlayerController) controller).getPlayer();
		Player player = SpoutUtils.wrap(spoutPlayer);

		final Event event = new Event();

		event.setPlayer(player);
		event.setLocation(SpoutUtils.wrap(entityMoveEvent.getTo()));

		return event;
	}},
	;

	private final Class<? extends org.spout.api.event.Event> spoutEventClass;

	private SpoutEvent(Class<? extends org.spout.api.event.Event> spoutEventClass) {
		this.spoutEventClass = spoutEventClass;
	}

	public Class<? extends org.spout.api.event.Event> getSpoutEventClass() {
		return spoutEventClass;
	}


	public abstract Event wrap(org.spout.api.event.Event backend);

	public void invoke(Object listener, Method handler, org.spout.api.event.Event backend) throws IllegalAccessException, InvocationTargetException {
		final Event event = wrap(backend);
		if (event == null)
			return;

		if (backend instanceof Cancellable) {
			final boolean oldCancelled = ((Cancellable) backend).isCancelled();
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

	public void register(Object listener, Method method, FrameworkPlugin frameworkPlugin, Order spoutPriority, boolean ignoreCancelled) {
		SpoutEventTransposer listenerExecutor = new SpoutEventTransposer(this, listener, method, ignoreCancelled);

		Spout.getEventManager().registerEvent(spoutEventClass, spoutPriority, listenerExecutor, frameworkPlugin);
	}

	private static boolean isBreak(final BlockChangeEvent blockPlaceEvent) {
		return blockPlaceEvent.getSnapshot().getMaterial().getId() == 0;
	}
}
