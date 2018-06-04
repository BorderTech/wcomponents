package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.driver.WebDriverType;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

/**
 * MultiBrowserRunner is a jUnit Suite which will run a single {@link WComponentSeleniumTestCase} using multiple
 * browsers. It should only be used as follows:
 *
 * <pre>
 *   &#64;RunWith(MultiBrowserRunner.class)
 *   public class MySeleniumTest extends WComponentSeleniumTestCase
 *   {
 *      // your test code here
 *   }
 * </pre>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MultiBrowserRunner extends Suite {

	/**
	 * The runners to use for the "suite" - one for each browser.
	 */
	private final List<Runner> runners = new ArrayList<>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 *
	 * @param clazz the test case to run.
	 * @throws InitializationError if there is an error.
	 */
	public MultiBrowserRunner(final Class<?> clazz) throws InitializationError {
		super(clazz, Collections.<Runner>emptyList());

		final String testClassName = getTestClass().getName();

		String[] drivers = ConfigurationProperties.getTestSeleniumMultiBrowserDrivers(testClassName);

		//Configuration error - no drivers defined.
		if (ArrayUtils.isEmpty(drivers)) {
			throw new SystemException("Cannot run the MultiBrowserRunner without drivers defined in default param ["
					+ ConfigurationProperties.TEST_SELENIUM_MULTI_BROWSER_DRIVERS + "] or test-specific param ["
					+ ConfigurationProperties.TEST_SELENIUM_MULTI_BROWSER_DRIVERS + "." + testClassName + "]");
		}

		boolean runParallel = ConfigurationProperties.getTestSeleniumMultiBrowserDriverParallel();

		for (String driverClassName : drivers) {
			try {
				Class<?> driverClass = Class.forName(driverClassName);
				if (!WebDriverType.class.isAssignableFrom(driverClass)) {
					throw new SystemException("parameter defined WebDriverType does not implement WebDriverType inteface. driverClass=["
							+ driverClass + "]");
				}

				WebDriverType driverType = ((Class<WebDriverType>) driverClass).newInstance();
				//Reuse the driver between tests if not parallel.
				String driverId = runParallel ? UUID.randomUUID().toString() : null;
				runners.add(new TestClassRunnerForBrowser(getTestClass().getJavaClass(), driverType, driverId));
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
				throw new SystemException("class parameter defined WebDriverType could not be instantiated. driverClassName=["
						+ driverClassName + "]", ex);
			}
		}

		if (runParallel) {
			setScheduler(new ThreadPoolScheduler());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	/**
	 * This class will run all the tests for a given test class using a single browsers.
	 */
	private static final class TestClassRunnerForBrowser extends BlockJUnit4ClassRunner {

		/**
		 * The unique id for this driver "Session".
		 */
		private final String driverId;

		/**
		 * The WebDriverType to run the test.
		 */
		private final WebDriverType driverType;

		/**
		 * Creates a TestClassRunnerForBrowser.
		 *
		 * @param type the test class to run.
		 * @param driverType the WebDriverType for this test.
		 * @param driverId the unique ID for this driver "session".
		 * @throws InitializationError if there is an error creating the runner.
		 */
		private TestClassRunnerForBrowser(final Class<?> type, final WebDriverType driverType, final String driverId
		) throws
				InitializationError {
			super(type);
			this.driverType = driverType;
			this.driverId = driverId;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object createTest() throws Exception {
			Object test = super.createTest();

			if (!(test instanceof WComponentSeleniumTestCase)) {
				throw new SystemException("MultiBrowserRunner cannot be used for test that does not extend WComponentSeleniumTestCase."
						+ " test class: " + test.getClass().getName());
			}
			((WComponentSeleniumTestCase) test).setDriver(driverType, driverId);

			return test;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getName() {
			return super.getName() + getTestDetails();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), getTestDetails());
		}

		@Override
		protected void validateConstructor(final List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Statement classBlock(final RunNotifier notifier) {
			return childrenInvoker(notifier);
		}

		/**
		 * @return the details of the test as a String to suffix on the name.
		 */
		private String getTestDetails() {
			return "." + driverType.getDriverTypeName() + ":" + driverId;
		}
	}

	/**
	 * This jUnit runner scheduler extension allows multiple threads.
	 */
	private static final class ThreadPoolScheduler implements RunnerScheduler {

		/**
		 * The executor which will run the tests.
		 */
		private final ExecutorService executor;

		/**
		 * Creates a ThreadPoolScheduler.
		 */
		private ThreadPoolScheduler() {
			executor = Executors.newCachedThreadPool();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void finished() {
			executor.shutdown();

			try {
				executor.awaitTermination(10 * 60, TimeUnit.SECONDS); // 10 mins, overly generous
			} catch (InterruptedException exc) {
				throw new IllegalStateException("Test execution timed out", exc);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void schedule(final Runnable childStatement) {
			executor.submit(childStatement);
		}
	}
}
