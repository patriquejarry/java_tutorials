package com.useraccount.di.framework.utils;

import static org.burningwave.core.assembler.StaticComponentContainer.Fields;

import java.lang.reflect.Field;
import java.util.Collection;

import org.burningwave.core.classes.FieldCriteria;

import com.useraccount.di.framework.CustomInjector;
import com.useraccount.di.framework.annotations.CustomAutowired;
import com.useraccount.di.framework.annotations.CustomQualifier;

public class InjectionUtil {

	private InjectionUtil() {
		super();
	}

	/**
	 * Perform injection recursively, for each service inside the Client class
	 */
	public static void autowire(final CustomInjector injector, final Class<?> classz, final Object classInstance)
		throws InstantiationException, IllegalAccessException {

		final Collection<Field> fields = Fields.findAllAndMakeThemAccessible(
			FieldCriteria.create().allThat(field -> field.isAnnotationPresent(CustomAutowired.class)),
			classz);

		for (final Field field : fields) {
			final String qualifier = field.isAnnotationPresent(CustomQualifier.class)
				? field.getAnnotation(CustomQualifier.class).value()
				: null;

			final Object fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifier);
			field.set(classInstance, fieldInstance);
			autowire(injector, fieldInstance.getClass(), fieldInstance);
		}
	}
}
