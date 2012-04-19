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

import eu.tomylobo.abstraction.entity.EntityType;
import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.abstraction.plugin.MetaPlugin;
import eu.tomylobo.routes.util.Ini;

public class Config {
	private final MetaPlugin plugin;

	public Config(MetaPlugin plugin) {
		this.plugin = plugin;
	}

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

	public void save() {
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

		Ini.save(getConfigFileName(), sections);
	}

	private String getConfigFileName() {
		return plugin.getDataFolder() + "/" + configFileName;
	}

	public void load() {
		final Multimap<String, Multimap<String, String>> sections = Ini.load(getConfigFileName());
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

				final Object converted = convertTo(type, input);
				if (converted == null)
					continue;

				field.set(this, converted);
			}
			catch (IllegalAccessException e) {
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

		throw new RuntimeException("No converter found for class '"+value.getClass()+"'.");
	}

	/**
	 * 
	 * @param type
	 * @param value
	 * @return null in the event of a conversion error.
	 */
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
			System.out.println("Exception encountered while parsing '"+value+"' as a '"+type.getSimpleName()+"'.");
			e.printStackTrace();
			return null;
		}

		throw new RuntimeException("No converter found for class '"+type+"'.");
	}
}
