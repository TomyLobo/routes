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

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Ini {
	private Ini() { }

	private static final Pattern sectionStartPattern = Pattern.compile("^\\[(.+)\\]$");
	public static Multimap<String, Multimap<String, String>> load(String fileName) {

		final LinkedListMultimap<String, Multimap<String, String>> sections = LinkedListMultimap.create();

		try {
			final BufferedReader stream = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = stream.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				final Matcher matcher = sectionStartPattern.matcher(line);

				if (!matcher.matches()) {
					System.err.println("Malformed line in "+fileName+".");
					continue;
				}

				final String sectionName = matcher.group(1);
				final Multimap<String, String> section = loadSection(stream);

				sections.put(sectionName, section);
			}
			stream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return sections;
	}

	private static final Pattern linePattern = Pattern.compile("^([^=]+)=(.*)$");
	private static Multimap<String, String> loadSection(BufferedReader stream) throws IOException {
		Multimap<String,String> section = LinkedListMultimap.create();

		String line;
		while ((line = stream.readLine()) != null) {
			if (line.trim().isEmpty())
				break;

			Matcher matcher = linePattern.matcher(line);

			if (!matcher.matches()) {
				System.err.println("Malformed line in file.");
				continue;
			}

			String key = matcher.group(1);
			String value = matcher.group(2);

			section.put(key, value);
		}
		return section;
	}


	public static void save(String fileName, Multimap<String, Multimap<String, String>> sections) {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter(fileName));
			for (Entry<String, Multimap<String, String>> entry : sections.entries()) {
				stream.write("["+entry.getKey()+"]");
				stream.newLine();
				saveSection(stream, entry.getValue());
				stream.newLine();
			}
			stream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveSection(BufferedWriter stream, Multimap<String, String> section) throws IOException {
		for (Entry<String, String> entry : section.entries()) {
			stream.write(entry.getKey());
			stream.write("=");
			stream.write(entry.getValue());
			stream.newLine();
		}
	}


	private static String getFirstValue(Iterable<String> iterable) {
		return iterable.iterator().next();
	}

	public static World loadWorld(Multimap<String, String> section, String format, Server server) {
		return server.getWorld(getFirstValue(section.get(String.format(format, "world"))));
	}


	public static Vector loadVector(Multimap<String, String> section, String format) {
		return new Vector(
				Double.valueOf(getFirstValue(section.get(String.format(format, "x")))),
				Double.valueOf(getFirstValue(section.get(String.format(format, "y")))),
				Double.valueOf(getFirstValue(section.get(String.format(format, "z"))))
		);
	}

	public static Location loadLocation(Multimap<String, String> section, String format, Server server) {
		try {
			return loadVector(section, format).toLocation(
					loadWorld(section, format, server),
					Float.valueOf(getFirstValue(section.get(String.format(format, "yaw")))),
					Float.valueOf(getFirstValue(section.get(String.format(format, "pitch"))))
			);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void saveWorld(Multimap<String, String> section, String format, World world) {
		section.put(String.format(format, "world"), world.getName());
	}

	public static void saveVector(Multimap<String, String> section, String format, Vector vector) {
		section.put(String.format(format, "x"), (String.valueOf(vector.getX())));
		section.put(String.format(format, "y"), (String.valueOf(vector.getY())));
		section.put(String.format(format, "z"), (String.valueOf(vector.getZ())));
	}

	public static void saveLocation(Multimap<String, String> section, String format, Location location) {
		saveWorld(section, format, location.getWorld());
		saveVector(section, format, location.toVector());
		section.put(String.format(format, "yaw"), String.valueOf(location.getYaw()));
		section.put(String.format(format, "pitch"), String.valueOf(location.getPitch()));
	}
}
