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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.commands.system.CommandException;
import eu.tomylobo.routes.fakeentity.EntityType;
import eu.tomylobo.routes.fakeentity.MobType;
import eu.tomylobo.routes.fakeentity.VehicleType;
import eu.tomylobo.routes.util.Ini;

public class Config {
	public class Entry {
		public String sectionName;
		public String key;
		public Field field;

		public Entry(String sectionName, String key, Field field) {
			this.sectionName = sectionName;
			this.key = key;
			this.field = field;
		}
	}

	private final String configFileName;
	private final List<Entry> entries = new ArrayList<Entry>();

	private static final Pattern itemPattern = Pattern.compile("^([^.]*)\\.(.*)$");

	{
		final Class<? extends Config> thisClass = this.getClass();

		final ConfigFile fileAnnotation = thisClass.getAnnotation(ConfigFile.class);
		if (fileAnnotation == null)
			throw new RuntimeException("ConfigFile annotation not found on Config subclass.");

		configFileName = fileAnnotation.value();

		for (Field field : thisClass.getFields()) {
			final ConfigItem itemAnnotation = field.getAnnotation(ConfigItem.class);
			if (itemAnnotation == null)
				continue;

			final String itemName = itemAnnotation.value();

			final Matcher matcher = itemPattern.matcher(itemName);
			if (!matcher.matches())
				continue; // TODO: handle better and all

			final String sectionName = matcher.group(1);
			final String key = matcher.group(2);

			entries.add(new Entry(sectionName, key, field));
		}
	}

	public void save() throws CommandException {
		Multimap<String, Multimap<String, String>> sections = LinkedListMultimap.create();
		for (Entry entry : entries) {
			final String sectionName = entry.sectionName;

			final Collection<Multimap<String, String>> matchingSections = sections.get(sectionName);
			final Multimap<String, String> section;
			if (matchingSections.isEmpty()) {
				sections.put(sectionName, section = LinkedListMultimap.create());
			}
			else {
				section = matchingSections.iterator().next();
			}

			final String key = entry.key;
			final Field field = entry.field;

			final Object value;
			try {
				value = field.get(this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}

			section.put(key, convertFrom(value));
		}

		Ini.save(Routes.getInstance().getConfigFileName(configFileName), sections);
	}

	public void load() throws CommandException {
		final Multimap<String, Multimap<String, String>> sections = Ini.load(Routes.getInstance().getConfigFileName(configFileName));
		if (sections == null) {
			System.out.println("Config file missing, creating default.");
			save();
			return;
		}

		for (Entry entry : entries) {
			final String sectionName = entry.sectionName;

			final Collection<Multimap<String, String>> matchingSections = sections.get(sectionName);
			final Multimap<String, String> section;
			if (matchingSections.isEmpty()) {
				sections.put(sectionName, section = LinkedListMultimap.create());
			}
			else {
				section = matchingSections.iterator().next();
			}

			final String key = entry.key;
			Collection<String> values = section.get(key);

			if (values.size() < 1) {
				System.out.println("Key '"+key+"' not found in section '"+sectionName+"' of '"+configFileName+"'.");
				continue;
			}

			if (values.size() > 1) {
				System.out.println("Key '"+key+"' found "+values.size()+" times in section '"+sectionName+"' of '"+configFileName+"'.");
				continue;
			}

			String input = values.iterator().next();

			final Field field = entry.field;
			Class<?> type = field.getType();
			try {
				if (type.isPrimitive()) {
					type = field.get(this).getClass();
				}

				field.set(this, convertTo(type, input));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private String convertFrom(Object value) {
		if (value instanceof String)
			return (String) value;

		if (value instanceof Number || value instanceof Boolean || value instanceof Character)
			return value.toString();

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		throw new CommandException("No converter found for class '"+value.getClass()+"'.");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object convertTo(Class<?> type, String value) {
		try {
			if (type == String.class)
				return value;

			if (type == boolean.class || type == Boolean.class)
				return Boolean.valueOf(value);

			if (type == char.class || type == Character.class)
				return value.charAt(0);

			if (Number.class.isAssignableFrom(type))
				return type.getMethod("valueOf", String.class).invoke(null, value);

			if (type.isEnum())
				return Enum.valueOf((Class<Enum>) type, value);

			if (EntityType.class.isAssignableFrom(type)) {
				try {
					return MobType.valueOf(value);
				} catch (IllegalArgumentException e) {
					return VehicleType.valueOf(value);
				}
			}
		}
		catch (Exception e) {
			throw new CommandException("Exception encountered while parsing '"+value+"' as a '"+type.getSimpleName()+"'.", e);
		}

		throw new CommandException("No converter found for class '"+type+"'.");
	}
}
