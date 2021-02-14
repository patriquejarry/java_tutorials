package com.useraccount.di.framework;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.ClassHunter.SearchResult;
import org.burningwave.core.classes.SearchConfig;

import com.useraccount.di.framework.annotations.CustomComponent;
import com.useraccount.di.framework.utils.ClassLoaderUtil;
import com.useraccount.di.framework.utils.InjectionUtil;

/**
 * Injector, to create objects for all @CustomService classes. autowire/inject
 * all dependencies
 */
public class CustomInjector {

	private final Map<Class<?>, Class<?>> diMap;
	private final Map<Class<?>, Object> applicationScope;

	private static CustomInjector injector;

	private CustomInjector() {
		super();
		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
	}

	/**
	 * Start application
	 *
	 * @param mainClass
	 */
	public static void startApplication(final Class<?> mainClass) {
		try {
			synchronized (CustomInjector.class) {
				if (injector == null) {
					injector = new CustomInjector();
					injector.initFramework(mainClass);
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static <T> T getService(final Class<T> classz) {
		try {
			return injector.getBeanInstance(classz);

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * initialize the injector framework
	 */
	/**
	 * initialize the injector framework
	 */
	private void initFramework(final Class<?> mainClass)
		throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {

		final Class<?>[] classes = ClassLoaderUtil.getClasses(mainClass.getPackage().getName(), true);
		final ComponentContainer componentConatiner = ComponentContainer.getInstance();
		final ClassHunter classHunter = componentConatiner.getClassHunter();
		final String packageRelPath = mainClass.getPackage().getName().replace(".", "/");

		try (SearchResult result = classHunter.findBy(

			SearchConfig.forResources(Thread.currentThread().getContextClassLoader(), packageRelPath)
				.by(ClassCriteria.create().allThat(cls -> (cls.getAnnotation(CustomComponent.class) != null))))) {

			final Collection<Class<?>> types = result.getClasses();
			for (final Class<?> implementationClass : types) {

				final Class<?>[] interfaces = implementationClass.getInterfaces();
				if (interfaces.length == 0) {
					diMap.put(implementationClass, implementationClass);
				} else {
					for (final Class<?> iface : interfaces) {
						diMap.put(implementationClass, iface);
					}
				}
			}

			for (final Class<?> classz : classes) {
				if (classz.isAnnotationPresent(CustomComponent.class)) {
					final Object classInstance = classz.newInstance();
					applicationScope.put(classz, classInstance);
					InjectionUtil.autowire(this, classz, classInstance);
				}
			}
		}

	}

	/**
	 * Create and Get the Object instance of the implementation class for input
	 * interface service
	 */
	@SuppressWarnings("unchecked")
	private <T> T getBeanInstance(final Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
		return (T) getBeanInstance(interfaceClass, null, null);
	}

	/**
	 * Overload getBeanInstance to handle qualifier and autowire by type
	 */
	public <T> Object getBeanInstance(final Class<T> interfaceClass, final String fieldName, final String qualifier)
		throws InstantiationException, IllegalAccessException {

		final Class<?> implementationClass = getImplimentationClass(interfaceClass, fieldName, qualifier);

		if (applicationScope.containsKey(implementationClass)) {
			return applicationScope.get(implementationClass);
		}

		synchronized (applicationScope) {
			final Object service = implementationClass.newInstance();
			applicationScope.put(implementationClass, service);
			return service;
		}
	}

	/**
	 * Get the name of the implimentation class for input interface service
	 */
	private Class<?> getImplimentationClass(final Class<?> interfaceClass, final String fieldName,
		final String qualifier) {

		final Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
			.filter(entry -> entry.getValue() == interfaceClass)
			.collect(Collectors.toSet());

		String errorMessage = "";
		if (implementationClasses == null || implementationClasses.isEmpty()) {
			errorMessage = "no implementation found for interface " + interfaceClass.getName();

		} else if (implementationClasses.size() == 1) {
			final Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
			if (optional.isPresent()) {
				return optional.get().getKey();
			}

		} else if (implementationClasses.size() > 1) {
			final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
			final Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream()
				.filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy))
				.findAny();

			if (optional.isPresent()) {
				return optional.get().getKey();
			} else {
				errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
					+ " Expected single implementation or make use of @CustomQualifier to resolve conflict";
			}
		}

		throw new RuntimeErrorException(new Error(errorMessage));
	}
}