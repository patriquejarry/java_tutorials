package com.di.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassLoaderUtil {

	/** Get all the classes for the input package */
	public static Class<?>[] getClasses(final String packageName)
		throws ClassNotFoundException, IOException {

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;

		final String path = packageName.replace('.', '/');
		final Enumeration<URL> resources = classLoader.getResources(path);
		final List<File> dirs = new ArrayList<>();

		while (resources.hasMoreElements()) {
			final URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}

		final List<Class<?>> classes = new ArrayList<>();
		for (final File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Get all the classes for the input package, inside the input directory
	 */
	public static List<Class<?>> findClasses(final File directory, final String packageName)
		throws ClassNotFoundException {

		final List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}

		final File[] files = directory.listFiles();
		for (final File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));

			} else if (file.getName().endsWith(".class")) {
				final String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				classes.add(Class.forName(className));
			}
		}

		return classes;
	}
}
