package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WebUtilities;
import java.util.Locale;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * ThemeUtil_Test - Unit tests for {@link ThemeUtil}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ThemeUtil_Test extends AbstractWComponentTestCase {

	@After
	public void restoreConfig() {
		Config.reset();
	}

	@Test
	public void testGetThemeBuild() {
		Assert.assertEquals("Incorrect theme build", "TEST_BUILD_NUMBER", ThemeUtil.getThemeBuild());
	}

	@Test
	public void testGetThemeName() {
		Assert.assertEquals("Incorrect theme name", "wcomponents-theme", ThemeUtil.getThemeName());
	}

	@Test
	public void testGetThemeXslt() {
		String themePath = "/testGetThemeXslt";
		Config.getInstance().setProperty(Environment.THEME_CONTENT_PATH, themePath);

		String build = ThemeUtil.getThemeBuild();
		String themeName = ThemeUtil.getThemeName();
		String versionSuffix = "?build=" + WebUtilities.escapeForUrl(build) + "&theme=" + WebUtilities.
				escapeForUrl(themeName);

		UIContext uic = createUIContext();
		Assert.assertEquals("Incorrect theme path", themePath + "/xslt/all.xsl" + versionSuffix,
				ThemeUtil.getThemeXslt(uic));

		uic.setLocale(Locale.ENGLISH);
		Assert.assertEquals("Incorrect theme path", themePath + "/xslt/all_en.xsl" + versionSuffix,
				ThemeUtil.getThemeXslt(uic));

		uic.setLocale(Locale.CANADA_FRENCH);
		Assert.assertEquals("Incorrect theme path",
				themePath + "/xslt/all_fr-CA.xsl" + versionSuffix, ThemeUtil.getThemeXslt(uic));
	}
}
