package com.github.bordertech.wcomponents.test.selenium.driver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;

/**
 * <p>
 * Static utility testing class to keep a Selenium Web Driver open between tests using the same thread. This is to
 * prevent the expensive WebDriver creation occurring multiple times per test suite.</p>
 * <p>
 * WebDriver implementations are not thread-safe, so the drivers opened by this class will be separated from other
 * threads via a ThreadLocal.</p>
 * <p>
 * Multiple concurrent instances of the same driver type can be created using a different driver id.</p>
 * <p>
 * This class will clean up open drivers when the JVM terminates.</p>
 *
 * @author Joshua Barclay
 * @author Jonathan Austin
 * @since 1.2.0
 */
public final class WebDriverCache {

	/**
	 * Store open WebDriver implementations.
	 */
	private static final Map<String, SeleniumWComponentsWebDriver> RUNNING_DRIVERS = new HashMap<>();

	/**
	 * A list of all drivers opened by all threads so they can be destroyed when the JVM shuts down.
	 */
	private static final List<SeleniumWComponentsWebDriver> DRIVERS_TO_DESTROY = new ArrayList<>();

	/**
	 * A pool of available drivers to the tests.
	 */
	private static final Map<String, List<SeleniumWComponentsWebDriver>> POOL_OF_DRIVERS = new HashMap<>();

	/*
	 * The separator between the driver type and the driver id.
	 */
	private static final String KEY_SEPARATOR = ":";

	/**
	 * Default driver ID for the driver.
	 */
	private static final String DEFAULT_DRIVER_ID = "default";

	/**
	 * No public constructor.
	 */
	private WebDriverCache() {
		//No-impl
	}

	//Register a shutdown hook to clean up all drivers when the JVM is terminated.
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			/**
			 * {@inheritDoc}.
			 */
			@Override
			public void run() {
				closeDriversForAllThreads();
			}
		});
	}

	/**
	 * Get or create the driver for the given driver type.
	 *
	 * @param <T> the type of the WebDriver.
	 * @param driverType the WebDriver type wrapper.
	 * @return A shared (ThreadLocal) instance of the given WebDriver type.
	 */
	public static <T extends WebDriver> SeleniumWComponentsWebDriver<T> getDriver(final WebDriverType<T> driverType) {
		return getDriver(driverType, DEFAULT_DRIVER_ID);
	}

	/**
	 * Get or create the driver for the given driver type and unique driver id.
	 *
	 * @param <T> the type of the WebDriver.
	 * @param driverType the WebDriver type wrapper.
	 * @param driverId the unique identifier for this driver implementation.
	 * @return A shared (ThreadLocal) instance of the given WebDriver type and unique driver id.
	 */
	public static <T extends WebDriver> SeleniumWComponentsWebDriver<T> getDriver(final WebDriverType<T> driverType, final String driverId) {
		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}

		synchronized (RUNNING_DRIVERS) {
			SeleniumWComponentsWebDriver<T> driver = RUNNING_DRIVERS.get(getKey(driverType, driverId));
			if (driver == null) {
				return createDriver(driverType, driverId);
			}
			return driver;
		}
	}

	/**
	 * <p>
	 * Open a new instance of the given driver type with a unique driver id</p>
	 * <p>
	 * Will close and replace any existing driver with the same type and driver id.</p>
	 *
	 * @param <T> the type of the WebDriver.
	 * @param driverType the WebDriver type wrapper.
	 * @param driverId the unique identifier for this driver implementation.
	 * @return A shared (ThreadLocal) instance of the given WebDriver type.
	 */
	private static <T extends WebDriver> SeleniumWComponentsWebDriver<T> createDriver(final WebDriverType<T> driverType, final String driverId) {
		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}

		// Check pool of drivers
		SeleniumWComponentsWebDriver<T> wcompDriver = checkPoolOfDrivers(driverType);
		if (wcompDriver == null) {
			wcompDriver = SeleniumWComponentsWebDriverFactory.createDriver(driverType);
		}
		return registerDriver(driverType, wcompDriver, driverId);
	}

	/**
	 * <p>
	 * Register the given driver implementation to be shared with this class using the given type and driver Id</p>
	 * <p>
	 * Will close and replace any existing driver with the same type and driver id.</p>
	 *
	 * @param <T> the type of the WebDriver.
	 * @param driverType the WebDriver type wrapper.
	 * @param driver the WebDriver implementation to use.
	 * @param driverId the unique identifier for this driver implementation.
	 * @return A shared (ThreadLocal) instance of the given WebDriver type.
	 */
	private static <T extends WebDriver> SeleniumWComponentsWebDriver<T> registerDriver(final WebDriverType<T> driverType,
			final SeleniumWComponentsWebDriver<T> driver,
			final String driverId) {

		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}
		if (driver == null) {
			throw new IllegalArgumentException("driver must not be null.");
		}

		final String key = getKey(driverType, driverId);

		//Close the old driver if one exists.
		synchronized (RUNNING_DRIVERS) {
			if (RUNNING_DRIVERS.get(key) != null) {
				closeDriver(driverType, driverId);
			}
			RUNNING_DRIVERS.put(key, driver);
			DRIVERS_TO_DESTROY.add(driver);
		}
		return driver;
	}

	/**
	 * <p>
	 * Close all drivers for <b>all threads</b></p>
	 * <p>
	 * <b>Warning: </b> this method will not clear out the ThreadLocals for any thread. Any thread reusing this class
	 * after this method was invoked must manually close and reopen all driver handles.</p>
	 */
	private static void closeDriversForAllThreads() {
		closeDrivers(DRIVERS_TO_DESTROY);
		for (List<SeleniumWComponentsWebDriver> driver : POOL_OF_DRIVERS.values()) {
			if (driver != null) {
				closeDrivers(driver);
			}
		}
	}

	/**
	 * <p>
	 * Close all drivers in the given list and empty the list.</p>
	 * <p>
	 * All drivers will attempt to be closed regardless of any exceptions encountered. The first caught exception will
	 * be thrown at the end of the process.</p>
	 *
	 * @param drivers the list of open drivers.
	 */
	private static void closeDrivers(final Collection<SeleniumWComponentsWebDriver> drivers) {
		//Track and throw whichever exception occurs first.
		RuntimeException e = null;
		for (WebDriver d : drivers) {
			try {
				d.quit();
			} catch (final RuntimeException ex) {

				//Only store the first exception.
				if (e == null) {
					e = ex;
				}
			}
		}

		drivers.clear();

		if (e != null) {
			throw e;
		}
	}

	/**
	 * Close the driver of the given type and driver id.
	 *
	 * @param driverType the type of the driver to close.
	 * @param driverId the unique driver id of the driver to close
	 */
	public static void closeDriver(final WebDriverType driverType, final String driverId) {

		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}

		//Remove the driver from the list before quitting in case an exception is thrown.
		synchronized (RUNNING_DRIVERS) {
			SeleniumWComponentsWebDriver driver = RUNNING_DRIVERS.get(getKey(driverType, driverId));
			if (driver != null) {
				RUNNING_DRIVERS.remove(getKey(driverType, driverId));
				DRIVERS_TO_DESTROY.remove(driver);
				driver.quit();
			}
		}
	}

	/**
	 * Close the driver of the given type and driver id.
	 *
	 * @param driverType the type of the driver to close.
	 * @param driverId the unique driver id of the driver to close
	 */
	public static void releaseDriver(final WebDriverType driverType, final String driverId) {

		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}

		//Remove the driver from the list before quitting in case an exception is thrown.
		synchronized (RUNNING_DRIVERS) {
			SeleniumWComponentsWebDriver driver = RUNNING_DRIVERS.get(getKey(driverType, driverId));
			if (driver != null) {
				RUNNING_DRIVERS.remove(getKey(driverType, driverId));
				DRIVERS_TO_DESTROY.remove(driver);
				saveDriverInPool(driverType, driver);
			}
		}
	}

	/**
	 * Get the lookup key for the driver type and driver id.
	 *
	 * @param driverType the WebDriverType
	 * @param driverId the driver id.
	 * @return the unique lookup key.
	 */
	private static String getKey(final WebDriverType driverType, final String driverId) {

		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null.");
		}

		if (driverId == null) {
			throw new IllegalArgumentException("driverId must not be null.");
		}

		return driverType.getDriverTypeName() + KEY_SEPARATOR + driverId;
	}

	/**
	 * Check if there is a driver available in the pool.
	 *
	 * @param type the web driver type
	 * @return the driver from the pool or null if none available
	 */
	private static SeleniumWComponentsWebDriver checkPoolOfDrivers(final WebDriverType type) {
		synchronized (POOL_OF_DRIVERS) {
			List<SeleniumWComponentsWebDriver> drivers = POOL_OF_DRIVERS.get(type.getDriverTypeName());
			if (drivers == null || drivers.isEmpty()) {
				return null;
			}
			SeleniumWComponentsWebDriver driver = drivers.remove(0);
			return driver;
		}
	}

	/**
	 * Put the driver back in the pool.
	 *
	 * @param type the web driver type
	 * @param driver the driver to put back in the pool
	 */
	private static void saveDriverInPool(final WebDriverType type, final SeleniumWComponentsWebDriver driver) {
		synchronized (POOL_OF_DRIVERS) {
			List<SeleniumWComponentsWebDriver> drivers = POOL_OF_DRIVERS.get(type.getDriverTypeName());
			if (drivers == null) {
				drivers = new ArrayList<>();
				POOL_OF_DRIVERS.put(type.getDriverTypeName(), drivers);
			}
			drivers.add(driver);
		}
	}

}
