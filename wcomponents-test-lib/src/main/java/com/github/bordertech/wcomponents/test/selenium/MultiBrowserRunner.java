package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.util.Config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
	 * The configuration parameter key for which browsers to use.
	 */
	private static final String BROWSERS_PARAM_KEY = "bordertech.wcomponents.test.selenium.browsers";

	/**
	 * The configuration parameter key for whether to run the browser tests in parallel.
	 */
	private static final String RUN_PARALLEL_PARAM_KEY = "bordertech.wcomponents.test.selenium.runParallel";

	/**
	 * Only called reflectively. Do not use programmatically.
	 *
	 * @param clazz the test case to run.
	 * @throws InitializationError if there is an error.
	 */
	public MultiBrowserRunner(final Class<?> clazz) throws InitializationError {
		super(clazz, Collections.<Runner>emptyList());
		String[] browsers = Config.getInstance().getStringArray(BROWSERS_PARAM_KEY);

		for (int i = 0; i < browsers.length; i++) {
			runners.add(new TestClassRunnerForBrowser(getTestClass().getJavaClass(), browsers, i));
		}

		boolean runParallel = Config.getInstance().getBoolean(RUN_PARALLEL_PARAM_KEY, false);

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
		 * The index of the browser being tested.
		 */
		private final int parameterIndex;

		/**
		 * The complete set of browsers which will be tested.
		 */
		private final String[] browsers;

		/**
		 * Creates a TestClassRunnerForBrowser.
		 *
		 * @param type the test class to run.
		 * @param browsers te complete set of browsers which will be tested.
		 * @param parameterIndex the browser index number.
		 * @throws InitializationError if there is an error creating the runner.
		 */
		private TestClassRunnerForBrowser(final Class<?> type, final String[] browsers, final int parameterIndex) throws
				InitializationError {
			super(type);
			this.browsers = browsers;
			this.parameterIndex = parameterIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object createTest() throws Exception {
			Object test = super.createTest();

			if (test instanceof WComponentSeleniumTestCase) {
				((WComponentSeleniumTestCase) test).setBrowser(browsers[parameterIndex]);
			}

			return test;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getName() {
			return String.format("%s", browsers[parameterIndex]);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), browsers[parameterIndex]);
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
