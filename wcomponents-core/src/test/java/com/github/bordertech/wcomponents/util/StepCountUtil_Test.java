package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link StepCountUtil} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class StepCountUtil_Test {

	private static Configuration originalConfig;

	@Before
	public void setUp() {
		originalConfig = Config.getInstance();
	}

	@After
	public void tearDown() {
		// Remove overrides
		Config.setConfiguration(originalConfig);
	}

	@Test
	public void testErrorUrl() {
		String testUrl = "test";

		Configuration config = Config.copyConfiguration(originalConfig);
		// Clear current property (if exists)
		config.clearProperty(ConfigurationProperties.STEP_ERROR_URL);
		// Set redirect url
		config.addProperty(ConfigurationProperties.STEP_ERROR_URL, testUrl);
		Config.setConfiguration(config);

		Assert.assertEquals("Incorrect error url returned", testUrl, StepCountUtil.getErrorUrl());
	}

	@Test
	public void testErrorRedirect() {
		// Default for redirect should be false
		Assert.assertFalse("Redirect flag should be false", StepCountUtil.isErrorRedirect());

		// Set redirect flag
		Configuration config = Config.copyConfiguration(originalConfig);
		config.addProperty(ConfigurationProperties.STEP_ERROR_URL, "test.url");
		Config.setConfiguration(config);
		Assert.assertTrue("Redirect flag should be true", StepCountUtil.isErrorRedirect());
	}

}
