package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for WTimeoutWarning.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTimeoutWarning_Test extends AbstractWComponentTestCase {

	/**
	 * The default timeout period (in seconds). If the value is "zero", then use the http session timeout value.
	 */
	private static final int DEFAULT_TIMEOUT_PERIOD = Config.getInstance()
			.getInt("bordertech.wcomponents.timeoutWarning.timeoutPeriod", 0);

	/**
	 * default warning period.
	 */
	private static final int DEFAULT_WARNING_PERIOD = Config.getInstance()
			.getInt("bordertech.wcomponents.timeoutWarning.warningPeriod", 300);

	/**
	 * Test timeout 60.
	 */
	private static final int TIMEOUT = 60;

	/**
	 * Test warning 30.
	 */
	private static final int WARN_AT = 30;

	@Test
	public void testDefaultContructor() {
		final WTimeoutWarning warning = new WTimeoutWarning();
		Assert.assertEquals(DEFAULT_TIMEOUT_PERIOD, warning.getTimeoutPeriod());
		Assert.assertEquals(DEFAULT_WARNING_PERIOD, warning.getWarningPeriod());
	}

	@Test
	public void testContructorWithwarning() {
		WTimeoutWarning warning = new WTimeoutWarning(TIMEOUT);
		Assert.assertEquals(TIMEOUT, warning.getTimeoutPeriod());
		Assert.assertEquals(DEFAULT_WARNING_PERIOD, warning.getWarningPeriod());
	}

	@Test
	public void testConstructorWithTimeoutAndWarning() {
		final WTimeoutWarning warning = new WTimeoutWarning(TIMEOUT, WARN_AT);
		Assert.assertEquals(TIMEOUT, warning.getTimeoutPeriod());
		Assert.assertEquals(WARN_AT, warning.getWarningPeriod());
	}

	@Test
	public void testTimeoutAccessors() {
		assertAccessorsCorrect(new WTimeoutWarning(), "timeoutPeriod", DEFAULT_TIMEOUT_PERIOD, 300,
				TIMEOUT);
	}

	@Test
	public void testWarningAccessors() {
		assertAccessorsCorrect(new WTimeoutWarning(), "warningPeriod", DEFAULT_WARNING_PERIOD, 60,
				WARN_AT);
	}

	@Test
	public void testWarningBelowMinimum() {
		final WTimeoutWarning warning = new WTimeoutWarning();

		try {
			warning.setWarningPeriod(15);
			Assert.assertTrue("Expected to throw an exception", false);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Warning period must be at least 20", e.getMessage());
		}
	}

	@Test
	public void testWarningExactlyZero() {
		final WTimeoutWarning warning = new WTimeoutWarning(TIMEOUT, 0);
		Assert.assertEquals(TIMEOUT, warning.getTimeoutPeriod());
		Assert.assertEquals(0, warning.getWarningPeriod());
	}
}
