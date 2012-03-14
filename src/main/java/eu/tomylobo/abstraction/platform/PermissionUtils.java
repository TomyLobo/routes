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

package eu.tomylobo.abstraction.platform;

import eu.tomylobo.abstraction.CommandSender;

public class PermissionUtils {
	public static boolean hasPermission(CommandSender sender, String permission) {
		if (sender.hasExactPermission(permission))
			return true;

		if (sender.hasExactPermission("*"))
			return true;

		String[] parts = permission.split("\\.");
		if (parts.length < 2)
			return false;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length-1; ++i) {
			sb.append(parts[i]);
			sb.append('.');

			if (sender.hasExactPermission(sb.toString()+'*'))
				return true;
		}

		return false;
	}
}
