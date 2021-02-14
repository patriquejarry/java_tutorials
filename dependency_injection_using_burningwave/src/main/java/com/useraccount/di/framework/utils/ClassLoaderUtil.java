package com.useraccount.di.framework.utils;

import java.io.IOException;
import java.util.Collection;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.CacheableSearchConfig;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.ClassHunter.SearchResult;
import org.burningwave.core.classes.SearchConfig;

public class ClassLoaderUtil {

	private ClassLoaderUtil() {
		super();
	}

	/**
	 * Get all the classes for the input package
	 */
	public static Class<?>[] getClasses(final String packageName, final boolean recursive)
		throws ClassNotFoundException, IOException {

		final ComponentContainer componentConatiner = ComponentContainer.getInstance();
		final ClassHunter classHunter = componentConatiner.getClassHunter();
		final String packageRelPath = packageName.replace(".", "/");
		final CacheableSearchConfig config = SearchConfig.forResources(Thread.currentThread().getContextClassLoader(),
			packageRelPath);

		if (!recursive) {
			config.notRecursiveOnPath(packageRelPath, false);
		}

		try (SearchResult result = classHunter.loadInCache(config).find()) {
			final Collection<Class<?>> classes = result.getClasses();
			return classes.toArray(new Class[classes.size()]);
		}
	}

}
