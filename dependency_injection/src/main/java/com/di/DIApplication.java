package com.di;

import com.di.annotations.CustomApplication;

public class DIApplication {

	private final CustomInjector injector;

	public DIApplication() {
		super();
		this.injector = new CustomInjector();
	}

	public void run(final Class<?> mainClassz) {

		final boolean hasCustomApplicationAnnotation = mainClassz.isAnnotationPresent(CustomApplication.class);
		if (hasCustomApplicationAnnotation) {
			System.out.println("Starting CustomDemoApplication...");
			this.startApplication(MainApplicationClass.class);
			this.injector.getService(ClientResource.class).displayUserAccount();
			System.out.println("\nStopping CustomDemoApplication...");

		} else {
			System.out.println("\nRunning as regular java Application...");
		}
	}

	/**
	 * Start application
	 *
	 * @param mainClass
	 */
	public void startApplication(final Class<?> mainClass) {
		try {
			synchronized (DIApplication.class) {
				this.injector.initFramework(mainClass);
				System.out.println("\nCustomDemoApplication Started....");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void run(final Class<?> mainClassz, final String[] args) {
		new DIApplication().run(mainClassz);
	}
}