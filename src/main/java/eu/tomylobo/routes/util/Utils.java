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

package eu.tomylobo.routes.util;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Contains general purpose utility functions.
 * 
 * @author TomyLobo
 *
 */
public class Utils {
	public static Location locationFromLookAt(World world, Vector start, Vector lookAt) {
		final Vector diff = lookAt.clone().subtract(start);

		return locationFromEye(world, start, diff);
	}

	public static Location locationFromEye(World world, Vector start, Vector eye) {
		final double eyeX = eye.getX();
		final double eyeZ = eye.getZ();
		final float yaw = (float) Math.toDegrees(Math.atan2(-eyeX, eyeZ));
		final double length = Math.sqrt(eyeX * eyeX + eyeZ * eyeZ);
		final float pitch = (float) Math.toDegrees(Math.atan2(-eye.getY(), length));

		return start.toLocation(world, yaw, pitch);
	}

	
	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E instance, String field) {
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(instance);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
		try
		{
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);


			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);
			if ((modifiers & 0x10) != 0)
				field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);
			f.setAccessible(true);
			f.set(instance, value);
		}
		catch (Exception e) { }
	}
}
