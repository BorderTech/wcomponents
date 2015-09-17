package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.Message;
import java.util.Locale;
import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

/**
 * Unit tests for {@link I18nUtilities}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class I18nUtilities_Test {

	/**
	 * This key matches a simple mapping in the test resource bundle.
	 */
	private static final String SIMPLE_TEXT_KEY = "SIMPLE_TEXT";

	/**
	 * This key matches a parameterised mapping in the test resource bundle.
	 */
	private static final String PARAMETERISED_TEXT_KEY = "PARAMETERISED_TEXT";

	/**
	 * A locale with no internationalisation provided for tests.
	 */
	private static final Locale NON_PROVIDED_LOCALE = new Locale("de");

	/**
	 * A locale which has internationalisation provided for tests.
	 */
	private static final Locale PROVIDED_LOCALE = new Locale("fr", "CA");

	/**
	 * Tests that the Utilities class returns the correct bundle base name. The bundle base name is configured in
	 * wcomponents-test.properties.
	 */
	@Test
	public void testGetResourceBundleBaseName() {
		Assert.assertEquals("Incorrect bundle base name", "i18n/test", I18nUtilities.
				getResourceBundleBaseName());
	}

	@Test
	public void testAsMessage() {
		Assert.assertNull("A null string should give a null message", I18nUtilities.asMessage(null));
		Assert.assertNull("A null string should give a null message", I18nUtilities.asMessage(null,
				"dummy"));

		Assert.assertEquals("A message with no params should be a String", "test", I18nUtilities.
				asMessage("test"));

		Assert.assertEquals("Incorrect message", new Message("text", "param"), I18nUtilities.
				asMessage("text", "param"));
		Assert.assertEquals("Incorrect message", "text", I18nUtilities.asMessage("text"));
	}

	@Test
	public void testFormat() {
		String result = I18nUtilities.format(PROVIDED_LOCALE, (String) null);
		Assert.assertNull("Null text should give a null result", result);

		result = I18nUtilities.format(PROVIDED_LOCALE, I18nUtilities.asMessage(null));
		Assert.assertNull("Null text should give a null result", result);

		result = I18nUtilities.format(NON_PROVIDED_LOCALE, SIMPLE_TEXT_KEY);
		Assert.assertEquals("Incorrect simple text for default locale", "default text", result);

		result = I18nUtilities.format(PROVIDED_LOCALE, SIMPLE_TEXT_KEY);
		Assert.assertEquals("Incorrect simple text for locale", "fr_CA text", result);

		result = I18nUtilities.format(NON_PROVIDED_LOCALE, new Message(SIMPLE_TEXT_KEY));
		Assert.assertEquals("Incorrect simple text for default locale", "default text", result);

		result = I18nUtilities.format(PROVIDED_LOCALE, new Message(SIMPLE_TEXT_KEY));
		Assert.assertEquals("Incorrect simple text for locale", "fr_CA text", result);

		result = I18nUtilities.format(NON_PROVIDED_LOCALE, PARAMETERISED_TEXT_KEY, "arg1");
		Assert.assertEquals("Incorrect parameterised text for default locale", "default arg1",
				result);

		result = I18nUtilities.format(PROVIDED_LOCALE, PARAMETERISED_TEXT_KEY, "arg1");
		Assert.assertEquals("Incorrect parameterised text for locale", "fr_CA arg1", result);
	}

	@Test
	public void testFormatInternalMessage() {
		// With resource bundle set but no locale-specific text, should drop back to text in messages.properties.
		String result = I18nUtilities.format(PROVIDED_LOCALE,
				"bordertech.wcomponents.message.printButton");
		Assert.assertEquals("Incorrect text", "Print", result);

		// Without resource bundle set, should still drop back to text in messages.properties
		try {
			Configuration config = Config.getInstance();
			config.clearProperty(I18nUtilities.RESOURCE_BUNDLE_BASE_NAME_CONFIG_KEY);

			result = I18nUtilities.format(PROVIDED_LOCALE,
					"bordertech.wcomponents.message.printButton");
			Assert.assertEquals("Incorrect text", "Print", result);
		} finally {
			Config.reset();
		}
	}
}
