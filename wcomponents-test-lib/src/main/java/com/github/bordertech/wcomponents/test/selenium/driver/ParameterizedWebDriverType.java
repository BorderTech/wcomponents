package com.github.bordertech.wcomponents.test.selenium.driver;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * <p>
 * WebDriverType implementation for a WebDriver created at runtime via
 * configuration.</p>
 * <p>
 * Subclasses can override to alter the configuration or change the
 * implementation.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class ParameterizedWebDriverType extends WebDriverType<WebDriver> {

	/**
	 * The driver class parameter name in the configuration.
	 */
	public static final String DRIVER_PARAM_NAME = "bordertech.wcomponents.test.selenium.web_driver";

	/**
	 * The system properties parameter name.
	 */
	public static final String DRIVER_SYS_PROPERTIES_PARAM_NAME = "bordertech.wcomponents.test.selenium.system_properties";

	/**
	 * An optional String for the name of the current test.
	 */
	private final String testClassName;

	/**
	 * Default constructor to use standard global configuration.
	 */
	public ParameterizedWebDriverType() {
		testClassName = null;
	}

	/**
	 * Construct this instance to look for configuration for a specific test
	 * class.
	 *
	 * @param testClassName the name of the test class. Nullable.
	 */
	public ParameterizedWebDriverType(final String testClassName) {
		this.testClassName = testClassName;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String getDriverTypeName() {
		return getDriverClassName();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public WebDriver getDriverImplementation() {

		setSystemProperty();
		return WComponentWebDriverFactory.createBackingDriver(getDriverClassName());
	}

	/**
	 * Used to set any necessary system properties prior to the driver's
	 * construction.
	 */
	protected void setSystemProperty() {

		// Properties may be empty, but will not be null.
		Properties props = Config.getInstance().getProperties(DRIVER_SYS_PROPERTIES_PARAM_NAME);
		System.getProperties().putAll(props);

	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public DesiredCapabilities getDefaultDriverCapabilities() {
		throw new SystemException("Capabilities not supported by ParameterizedWebDriverType.");
	}

	/**
	 * @return the class name for the driver implementation.
	 */
	private String getDriverClassName() {
		if (StringUtils.isNotBlank(testClassName)) {
			String parameterName = DRIVER_PARAM_NAME + "." + testClassName;
			final String classname = Config.getInstance().getString(parameterName);

			if (StringUtils.isNotBlank(classname)) {
				return classname;
			}

			//If the test class has no specific value, fall through to default.
		}

		final String classname = Config.getInstance().getString(DRIVER_PARAM_NAME);
		if (StringUtils.isBlank(classname)) {
			throw new SystemException("No parameter defined for " + getClass().getName() + " expected parameter: " + DRIVER_PARAM_NAME);
		}

		return classname;
	}

}
