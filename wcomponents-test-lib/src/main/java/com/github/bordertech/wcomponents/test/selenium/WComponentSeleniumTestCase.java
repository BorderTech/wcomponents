package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.driver.WebDriverCache;
import com.github.bordertech.wcomponents.test.selenium.driver.ParameterizedWebDriverType;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import com.github.bordertech.wcomponents.test.selenium.driver.WebDriverType;
import com.github.bordertech.wcomponents.test.selenium.server.ServerCache;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An abstract class which provides convenience methods to test the UI using Selenium to drive a web browser.</p>
 * <p>
 * The implementation of various features (ServerCache, WebDriverCache) has been extracted out to static utility classes
 * for applications that cannot extend this class for tests.
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public abstract class WComponentSeleniumTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WComponentSeleniumTestCase.class);

	/**
	 * The WebDriverType to use for this test instance.
	 */
	private WebDriverType driverType;

	/**
	 * The (optional) unique id of the driver for multiple concurrent instances of the same driver type.
	 */
	private String driverId = null;

	/**
	 * The URL of the server.
	 */
	private String url;

	/**
	 * Whether the driver has been launched.
	 */
	private boolean driverLaunched = false;

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase.</p>
	 * <p>
	 * Most tests should use this constructor to get the default WebDriverType.</p>
	 */
	public WComponentSeleniumTestCase() {
		this(true);
	}

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase.</p>
	 * <p>
	 * Most tests should use a 'true' value to get the default WebDriverType.</p>
	 * <p>
	 * The driver will be launched if the driverType is set and the Url is set or configured.</p>
	 * <p>
	 * Tests using the MultiBrowserRunner must use a 'false' parameter so the creation is deferred until the test is
	 * run.</p>
	 *
	 * @param useDefaultDriver - if true, a default ParameterizedWebDriverType will be used. If false, the driver must
	 * be set after the constructor.
	 */
	public WComponentSeleniumTestCase(final boolean useDefaultDriver) {
		this(useDefaultDriver, null);
	}

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase.</p>
	 * <p>
	 * Most tests should use a 'true' value to get the default WebDriverType.</p>
	 * <p>
	 * A non-null URL parameter specifies that the server has already been launched at the given URL.</p>
	 * <p>
	 * The driver will be launched if the driverType is set and the Url is set or configured.</p>
	 * <p>
	 * Tests using the MultiBrowserRunner must use a 'false' parameter so the creation is deferred until the test is
	 * run.</p>
	 *
	 * @param useDefaultDriver - if true, a default ParameterizedWebDriverType will be used. If false, the driver must
	 * be set after the constructor.
	 * @param url the url of the server. A non-null URL will prevent a server from being launched.
	 */
	public WComponentSeleniumTestCase(final boolean useDefaultDriver, final String url) {

		if (useDefaultDriver) {
			//Can't call the other constructor as we need 'this' to be available.
			driverType = new ParameterizedWebDriverType(this.getClass().getName());
		}
		this.url = url;
		configureUrlAndServerFromConfig();
	}

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase with the given driver type.</p>
	 * <p>
	 * The driver will be launched if the driverType is set and the Url is set or configured.</p>
	 *
	 * @param driverType the type of WebDriver to use.
	 */
	public WComponentSeleniumTestCase(final WebDriverType driverType) {
		this(driverType, null);
	}

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase with the given driver type.</p>
	 * <p>
	 * The driver will be launched if the driverType is set and the Url is set or configured.</p>
	 *
	 * @param driverType the type of WebDriver to use.
	 * @param driverId - the id to use to separate multiple instances of the same driver.
	 */
	public WComponentSeleniumTestCase(final WebDriverType driverType, final String driverId) {
		this(driverType, driverId, null);
	}

	/**
	 * <p>
	 * Creates a WComponentSeleniumTestCase with the given driver type.</p>
	 * <p>
	 * A non-null URL parameter specifies that the server has already been launched at the given URL.</p>
	 * <p>
	 *
	 * @param driverType the type of WebDriver to use.
	 * @param driverId - the id to use to separate multiple instances of the same driver.
	 * @param url the url of the server. A non-null URL will prevent a server from being launched.
	 */
	public WComponentSeleniumTestCase(final WebDriverType driverType, final String driverId, final String url) {
		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null");
		}

		this.driverType = driverType;
		this.driverId = driverId;
		this.url = url;
		configureUrlAndServerFromConfig();
	}

	/**
	 * <p>
	 * Whether to use the Config to set the URL and launch the server.</p>
	 *
	 * @return true if config should be used, else false.
	 */
	private boolean isConfigureUrlFromConfig() {

		return StringUtils.isBlank(url);
	}

	/**
	 * Configures the URL from parameters if it has not been set.
	 */
	private void configureUrlAndServerFromConfig() {

		if (!isConfigureUrlFromConfig()) {
			return;
		}

		final String testClassName = this.getClass().getName();

		if (ConfigurationProperties.getTestSeleniumServerStart(testClassName)) {
			ServerCache.startServer();
			url = ServerCache.getUrl();
		} else {
			//Might be null at this stage - assume it will be manually assigned later.
			url = ConfigurationProperties.getTestSeleniumServerUrl(testClassName);
		}
	}

	/**
	 * Launch the driver against the configured Url, but only if configuration is complete.
	 */
	private void launchDriver() {
		if (!driverLaunched) {
			if (driverType == null) {
				throw new SystemException("Attempted to launch driver prior to configuring the driverType.");
			}

			if (StringUtils.isBlank(url)) {
				throw new SystemException("Attempted to launch driver prior to configuring the url.");
			}

			SeleniumWComponentsWebDriver driver = getDriverWithoutLaunching();
			if (driver.hasSession()) {
				driver.newSession(getUrl());
			} else {
				driver.get(getUrl());
			}
			driverLaunched = true;
		}
	}

	/**
	 * <p>
	 * Set the driver type and ID for this test.</p>
	 * <p>
	 * Use a null driverId if not using multiple drivers of the same type.</p>
	 *
	 * @param driverType the WebDriverType to use.
	 * @param driverId OPTIONAL the driver Id to use.
	 */
	public void setDriver(final WebDriverType driverType, final String driverId) {
		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null");
		}

		this.driverType = driverType;
		this.driverId = driverId;

		driverLaunched = false;
	}

	/**
	 * Set the URL to use for the test.
	 *
	 * @param url the URL to set.
	 */
	public void setUrl(final String url) {
		this.url = url;

		driverLaunched = false;
	}

	/**
	 * @return the URL.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>
	 * Retrieves the current driver instance, launching it for the URL if it is not already running. Subclasses should
	 * use this to obtain a driver instance for their tests. Note that the driver is retained between tests unless
	 * explicitly closed.</p>
	 * <p>
	 * If not already running, the driver will attempt to load the configured Url. An exception will be thrown if the
	 * driver or URL is not configured.</p>
	 *
	 * @return the driver to use during testing.
	 */
	public SeleniumWComponentsWebDriver getDriver() {

		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null."
					+ " Ensure the correct constructor was called or the setter has been invoked.");
		}

		launchDriver();

		return getDriverWithoutLaunching();
	}

	/**
	 * <p>
	 * Retrieve the driver without launching it.</p>
	 * <p>
	 * An exception will be thrown if the driverType has not been configured.</p>
	 *
	 * @return the driver to use during testing.
	 */
	public SeleniumWComponentsWebDriver getDriverWithoutLaunching() {
		if (driverType == null) {
			throw new IllegalArgumentException("driverType must not be null."
					+ " Ensure the correct constructor was called or the setter has been invoked.");
		}

		if (StringUtils.isBlank(driverId)) {
			return WebDriverCache.getDriver(driverType);
		}

		return WebDriverCache.getDriver(driverType, driverId);
	}

}
