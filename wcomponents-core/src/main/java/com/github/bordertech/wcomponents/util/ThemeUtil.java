package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WebUtilities;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static support classes for retrieving the theme version.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public final class ThemeUtil {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ThemeUtil.class);

	/**
	 * The parameter key for the theme name.
	 */
	private static final String THEME_PARAM = "bordertech.wcomponents.theme.name";

	/**
	 * The theme build version number.
	 */
	private static final String THEME_BUILD;

	/**
	 * The theme version properties file name.
	 */
	private static final String THEME_VERSION_FILE_NAME = "version.properties";

	/**
	 * The theme build property name.
	 */
	private static final String THEME_BUILD_NUMBER_PARAM = "build.number";

	/**
	 * The theme wcomponent version property name.
	 */
	private static final String THEME_WC_BUILD_NUMBER_PARAM = "wc.project.version";

	private static final String THEME_NAME;

	/**
	 * The base
	 * This assumes theme names do not change on the fly and a restart is acceptable if they do.
	 */
	private static final String THEME_BASE;

	static {
		THEME_NAME = Config.getInstance().getString(THEME_PARAM);
		THEME_BASE = "/theme/" + THEME_NAME + '/';

		// Load theme build (depends on the theme name)
		String resourceName = THEME_BASE + THEME_VERSION_FILE_NAME;

		// Get theme version property file (if in classpath)
		InputStream resourceStream = null;
		Properties prop = new Properties();
		String themeBuild = null;
		String themeWcVersion = null;
		try {
			resourceStream = ThemeUtil.class.getResourceAsStream(resourceName);
			prop.load(resourceStream);
			// Theme build property
			themeBuild = prop.getProperty(THEME_BUILD_NUMBER_PARAM);
			// WComponent build theme depends on property
			themeWcVersion = prop.getProperty(THEME_WC_BUILD_NUMBER_PARAM);
		} catch (Exception e) {
			LOG.warn("Could not load theme version properties file \"" + resourceName + "\"");
		} finally {
			StreamUtil.safeClose(resourceStream);
		}

		// If theme build not available, then use the wcomponents project version
		if (themeBuild == null) {
			// The theme build number is used to "bust" the cache. As the theme build may not always be available in the
			// classpath (ie the theme is served up from a different server) we at least want to use the wcomponent
			// version to try and bust the cache. However, most projects will have the theme in the classpath.
			LOG.warn(THEME_BUILD_NUMBER_PARAM + " property not found in \"" + resourceName
					+ "\". Will use wcomponent project version.");
			THEME_BUILD = "wc-" + WebUtilities.getProjectVersion();
		} else {
			THEME_BUILD = themeBuild;
		}

		LOG.info("Using theme \"" + THEME_NAME + "\"" + " build \"" + THEME_BUILD + "\"");

		// Check the theme wcomponent version against the project wcomponent version
		if (themeWcVersion != null) {
			String wcProject = WebUtilities.getProjectVersion();
			if (!Util.equals(themeWcVersion, wcProject)) {
				LOG.warn("The theme wcomponent version \"" + themeWcVersion
						+ "\" does not match the project wcomponent version \"" + wcProject + "\".");
			}
		}
	}

	/**
	 * Prevent instantiation of this class.
	 */
	private ThemeUtil() {
	}

	/**
	 * The theme name as determined on instantiation.
	 * Changes require a restart.
	 * @return the current theme name
	 */
	public static String getThemeName() {
		return THEME_NAME;
	}

	/**
	 * @return the current theme build
	 */
	public static String getThemeBuild() {
		return THEME_BUILD;
	}

	/**
	 * Gets the base path of the theme resources.
	 * Note that this path will end with a forward slash.
	 * @return The theme resource path.
	 */
	public static String getThemeBase() {
		return THEME_BASE;
	}

	/**
	 * <p>
	 * Retrieves the complete path to the theme's XSLT. This method takes the current theme and user's locale into
	 * account.
	 * </p>
	 * <p>
	 * Note: The XSLT is the single integration point to the client-side rendering.
	 * </p>
	 *
	 * @param uic the current user's UIContext.
	 * @return the theme XSLT.
	 */
	public static String getThemeXslt(final UIContext uic) {
		String themePath = uic.getEnvironment().getThemePath();
		StringBuffer path = new StringBuffer(themePath.length() + 20);

		// Path
		path.append(themePath);
		if (themePath.length() > 0 && !themePath.endsWith("/")) {
			path.append('/');
		}
		path.append("xslt/");
		path.append(getThemeXsltName(uic));

		// Add cache busting suffix
		path.append("?build=").append(WebUtilities.escapeForUrl(THEME_BUILD))
				.append("&theme=").append(WebUtilities.escapeForUrl(THEME_NAME));

		return path.toString();
	}

	/**
	 * Get the name of the XSLT file to use taking locale and debug mode into consideration.
	 * @param uic The UIContext to use to determine factors such as locale.
	 * @return The name of the XSLT file without any path or cachebuster.
	 */
	public static String getThemeXsltName(final UIContext uic) {
		Locale locale = uic.getLocale();
		StringBuffer xsltName = new StringBuffer(20);

		// Base file name
		xsltName.append("all");

		// Locale
		if (locale != null) {
			xsltName.append('_').append(locale.getLanguage());

			if (!Util.empty(locale.getCountry())) {
				xsltName.append('-').append(locale.getCountry());

				if (!Util.empty(locale.getVariant())) {
					xsltName.append('-').append(locale.getVariant());
				}
			}
		}

		// Debug
		if (DebugUtil.isDebugStructureEnabled()) {
			xsltName.append("_debug");
		}

		// xsl
		xsltName.append(".xsl");
		return xsltName.toString();
	}

}
