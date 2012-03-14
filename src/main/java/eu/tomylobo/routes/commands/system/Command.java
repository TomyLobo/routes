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

package eu.tomylobo.routes.commands.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a command for use with the command system.<br />
 * Aliases and permissions are managed by the environment.<br />
 * Flags are managed by the commands themselves. 
 *
 * @author TomyLobo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
	public static final String DISABLED = "\u1f4a9";

	/**
	 * One of these permissions is required for the command to work.<br />
	 * Passing {@link #DISABLED} or omitting this field means that no permission check is done.
	 */
	String[] permissions() default DISABLED;

	/**
	 * The command can be run using either of these names.<br />
	 * Spaces in the command name must be represented as underscores.<br />
	 * <br />
	 * Omitting this field instructs the command system to use the method name
	 * as the command name.
	 */
	String[] names() default {};
}
