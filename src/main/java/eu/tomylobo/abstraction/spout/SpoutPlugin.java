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

package eu.tomylobo.abstraction.spout;

import java.util.Arrays;

import org.spout.api.command.CommandSource;
import org.spout.api.command.RawCommandExecutor;
import org.spout.api.exception.CommandException;
import org.spout.api.plugin.CommonPlugin;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.plugin.FrameworkPlugin;
import eu.tomylobo.abstraction.plugin.MetaPlugin;

public abstract class SpoutPlugin extends CommonPlugin implements FrameworkPlugin, RawCommandExecutor {
	static {
		Environment.init(SpoutEnvironment.class);
	}

	private final MetaPlugin metaPlugin;

	public SpoutPlugin(MetaPlugin metaPlugin) {
		this.metaPlugin = metaPlugin;
		metaPlugin.setFrameworkPlugin(this);
	}

	@Override
	public void onLoad() {
		metaPlugin.onLoad();
	}

	@Override
	public void onEnable() {
		metaPlugin.onEnable();
	}

	@Override
	public void onDisable() {
		metaPlugin.onDisable();
	}

	@Override
	public MetaPlugin getMetaPlugin() {
		return metaPlugin;
	}

	@Override
	public void execute(CommandSource source, String[] args, int baseIndex, boolean fuzzyLookup) throws CommandException {
		final String label = args[0];
		final String commandName = label; // TODO: spout
		args = Arrays.copyOfRange(args, 1, args.length);
		metaPlugin.onCommand(SpoutUtils.wrap(source), commandName, label, args);
	}
}
