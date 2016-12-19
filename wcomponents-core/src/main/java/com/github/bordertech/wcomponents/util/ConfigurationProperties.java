package com.github.bordertech.wcomponents.util;

import java.text.MessageFormat;
import java.util.Properties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class contains references to all constants and configuration options used by WComponents.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class ConfigurationProperties {

	/**
	 * The AntiSamy lax sanitization configuration file.
	 */
	private static final String ANTISAMY_LAX_CONFIG_PARAM = "com.github.bordertech.wcomponents.AntiSamyLax.config";

	/**
	 * The AntiSamy strict sanitization configuration file.
	 */
	private static final String ANTISAMY_STRICT_CONFIG_PARAM = "com.github.bordertech.wcomponents.AntiSamy.config";

	/**
	 * The URL for the application's icon.
	 */
	public static final String APPLICATION_ICON_URL = get().getString("bordertech.wcomponents.application.icon.url");

	/**
	 * This flag controls if component ids should be checked for duplicates. As verifying requires extra resources and
	 * memory, this can be disabled if required. It is encouraged projects at least have this set true in development
	 * environment.
	 */
	public static final boolean CHECK_DUPLICATE_IDS = get().getBoolean("bordertech.wcomponents.check.duplicate.ids.enabled", true);

	/**
	 * The flag that controls Data list caching.
	 */
	public static final String DATALIST_CACHING_PARAM_KEY = "bordertech.wcomponents.dataListCaching.enabled";

	/**
	 * The default locale (you should probably never change this).
	 */
	public static final String DEFAULT_LOCALE = "bordertech.wcomponents.locale.defaultLocale";

	/**
	 * The default mime type for a file.
	 */
	public static final String DEFAULT_MIME_TYPE = "bordertech.wcomponents.mimeType.defaultMimeType";

	/**
	 * The prefix for factory class lookups.
	 */
	public static final String FACTORY_PREFIX = "bordertech.wcomponents.factory.impl.";

	/**
	 * The prefix for the configuration parameter name to lookup a file's mime type by extension.
	 */
	public static final String FILE_MIME_TYPE_PREFIX = "bordertech.wcomponents.mimeType.";

	/**
	 * The flag indicating whether to use a fatal error page factory.
	 *
	 */
	public static final String HANDLE_ERROR_WITH_FATAL_PAGE_FACTORY = "bordertech.wcomponents.servlet.handleErrorWithFatalErrorPageFactory";

	/**
	 * The flag indicating whether to use a fatal error page factory. This parameter is deprecated as it incorrectly
	 * used a fully qualified classname as the prefix.
	 *
	 * @deprecated use HANDLE_ERROR_WITH_FATAL_PAGE_FACTORY instead.
	 */
	@Deprecated
	public static final String HANDLE_ERROR_WITH_FATAL_PAGE_FACTORY_DEPRECATED
			= "com.github.bordertech.wcomponents.servlet.WServlet.handleErrorWithFatalErrorPageFactory";

	/**
	 * The flag indicating whether handlebars should cache.
	 */
	public static final String HANDLEBARS_CACHE = "bordertech.wcomponents.handlebars.cache.enabled";

	/**
	 * The prefix for all HTML Class icon configuration properties.
	 */
	public static final String HTML_ICON_CLASS_PREFIX = "com.github.bordertech.wcomponents.HtmlClass.icon.";

	/**
	 * The HTML class for the help icon.
	 */
	public static final String HTML_ICON_CLASS_HELP = HTML_ICON_CLASS_PREFIX + "help";

	/**
	 * The HTML class for the info icon.
	 */
	public static final String HTML_ICON_CLASS_INFO = HTML_ICON_CLASS_PREFIX + "info";

	/**
	 * The HTML class for the warn icon.
	 */
	public static final String HTML_ICON_CLASS_WARN = HTML_ICON_CLASS_PREFIX + "warn";

	/**
	 * The HTML class for the error icon.
	 */
	public static final String HTML_ICON_CLASS_ERROR = HTML_ICON_CLASS_PREFIX + "error";

	/**
	 * The HTML class for the success icon.
	 */
	public static final String HTML_ICON_CLASS_SUCCESS = HTML_ICON_CLASS_PREFIX + "success";

	/**
	 * The HTML class for the add icon.
	 */
	public static final String HTML_ICON_CLASS_ADD = HTML_ICON_CLASS_PREFIX + "add";

	/**
	 * The HTML class for the delete icon.
	 */
	public static final String HTML_ICON_CLASS_DELETE = HTML_ICON_CLASS_PREFIX + "delete";

	/**
	 * The HTML class for the edit icon.
	 */
	public static final String HTML_ICON_CLASS_EDIT = HTML_ICON_CLASS_PREFIX + "edit";

	/**
	 * The HTML class for the save icon.
	 */
	public static final String HTML_ICON_CLASS_SAVE = HTML_ICON_CLASS_PREFIX + "save";

	/**
	 * The HTML class for the search icon.
	 */
	public static final String HTML_ICON_CLASS_SEARCH = HTML_ICON_CLASS_PREFIX + "search";

	/**
	 * The HTML class for the cancel icon.
	 */
	public static final String HTML_ICON_CLASS_CANCEL = HTML_ICON_CLASS_PREFIX + "cancel";

	/**
	 * The HTML class for the menu icon.
	 */
	public static final String HTML_ICON_CLASS_MENU = HTML_ICON_CLASS_PREFIX + "menu";

	/**
	 * The HTML class for the print icon.
	 */
	public static final String HTML_ICON_CLASS_PRINT = HTML_ICON_CLASS_PREFIX + "print";

	/**
	 * The resource bundle base name.
	 */
	public static final String I18N_RESOURCE_BUNDLE_BASE_NAME = "bordertech.wcomponents.i18n.baseName";

	/**
	 * The resource bundle base name.
	 */
	public static final String I18N_THEME_RESOURCE_BUNDLE_BASE_NAME = "bordertech.wcomponents.i18n.theme.baseName";

	/**
	 * The flag that controls throwing errors for integrity issues.
	 */
	public static final String INTEGRITY_ERROR_MODE = "bordertech.wcomponents.integrity.terminate.mode";

	/**
	 * The prefix for internal messages.
	 */
	public static final String INTERNAL_MESSAGE_PREFIX = "bordertech.wcomponents.message.";

	/**
	 * The logout URL.
	 */
	public static final String LOGOUT_URL = "bordertech.wcomponents.logout.url";

	/**
	 * The flag indicating whether plaintext should cache.
	 */
	public static final String PLAINTEXT_CACHE = "bordertech.wcomponents.plaintext.cache.enabled";

	/**
	 * The current project version.
	 */
	public static final String PROJECT_VERSION = "bordertech.wcomponents.version";

	/**
	 * The prefix (classname appended) to override the renderer instance for a class.
	 */
	public static final String RENDERER_OVERRIDE_PREFIX = "bordertech.wcomponents.UIManager.renderer.";

	/**
	 * The response cache String for each type of header.
	 */
	public static final String RESPONSE_CACHE_HEADER_SETTINGS = "bordertech.wcomponents.response.header.{0}";

	/**
	 * The response cache String.
	 */
	public static final String RESPONSE_CACHE_SETTINGS = "bordertech.wcomponents.response.header.default.cache";

	/**
	 * Default cache settings for the response.
	 */
	public static final String RESPONSE_DEFAULT_CACHE_SETTINGS = "public, max-age=31536000";

	/**
	 * Default no cache settings for the response.
	 */
	public static final String RESPONSE_DEFAULT_NO_CACHE_SETTINGS = "no-cache, no-store, must-revalidate, private";

	/**
	 * The response no-cache settings.
	 */
	public static final String RESPONSE_NO_CACHE_SETTINGS = "bordertech.wcomponents.response.header.default.nocache";

	/**
	 * The flag indicating whether the servlet should enable subsessions.
	 */
	public static final String SERVLET_ENABLE_SUBSESSIONS = "bordertech.wcomponents.servlet.subsessions.enabled";

	/**
	 * The URL users are redirected to when a step error occurs.
	 */
	public static final String STEP_ERROR_URL = "bordertech.wcomponents.wrongStep.redirect.url";

	/**
	 * Whether to use sticky focus.
	 */
	public static final String STICKY_FOCUS = "bordertech.wcomponents.stickyFocus";
	/**
	 * The parameter variable that contains the URL path to the support servlet that services targeted requests for a
	 * Portlet application. This parameter is only applicable to portlet applications
	 */
	public static final String SUPPORT_SERVLET_PATH = "bordertech.wcomponents.servlet.support.path";

	/**
	 * The default rendering engine for templates. Also used as a prefix for a specific template type.
	 */
	public static final String TEMPLATE_RENDERING_ENGINE = "bordertech.wcomponents.template.renderer";

	/**
	 * Default template engine name.
	 */
	public static final String TEMPLATE_RENDERING_ENGINE_DEFAULT = get().getString(TEMPLATE_RENDERING_ENGINE);

	/**
	 * The render mode for template rendering, can be "on", "off" or "sniff".
	 */
	public static final String TEMPLATE_RENDERING_MODE = "bordertech.wcomponents.template.render.mode";

	/**
	 * The flag indicating whether to terminate the session on error.
	 */
	public static final String TERMINATE_SESSION_ON_ERROR = "bordertech.wcomponents.terminateSessionOnError";

	/**
	 * The parameter variable that contains the URL path to the web content for the Theme.
	 */
	public static final String THEME_CONTENT_PATH = "bordertech.wcomponents.theme.content.path";

	/**
	 * The theme name.
	 */
	public static final String THEME_NAME = "bordertech.wcomponents.theme.name";

	/**
	 * The timeout period (in seconds).
	 */
	public static final int TIMEOUT_PERIOD = get().getInt("bordertech.wcomponents.timeoutWarning.timeoutPeriod", 0);

	/**
	 * The timeout warning period (in seconds).
	 */
	public static final int TIMEOUT_WARNING_PERIOD = get().getInt("bordertech.wcomponents.timeoutWarning.warningPeriod", 300);

	/**
	 * The tracking application name.
	 */
	public static final String TRACKING_APPLICATION_NAME = "bordertech.wcomponents.tracking.applicationname";
	/**
	 * The tracking client id.
	 */
	public static final String TRACKING_CLIENT_ID = "bordertech.wcomponents.tracking.clientid";

	/**
	 * The tracking cookie domain.
	 */
	public static final String TRACKING_COOKIE_DOMAIN = "bordertech.wcomponents.tracking.cookiedomain";

	/**
	 * The tracking data collection domain.
	 */
	public static final String TRACKING_DATA_COLLECTION_DOMAIN = "bordertech.wcomponents.tracking.datacollectiondomain";

	/**
	 * The flag indicating whether the whitespace filter is enabled.
	 */
	public static final String WHITESPACE_FILTER = "bordertech.wcomponents.whitespaceFilter.enabled";

	/**
	 * The flag indicating whether to cache velocity output.
	 */
	public static final String VELOCITY_CACHE = "bordertech.wcomponents.velocity.cache.enabled";

	/**
	 * The flag indicating whether to cache velocity templates.
	 */
	public static final String VELOCITY_CACHE_TEMPLATES = "bordertech.wcomponents.velocity.cacheTemplates.enabled";

	/**
	 * The location of the velocity file templates.
	 */
	public static final String VELOCITY_FILE_TEMPLATES = "bordertech.wcomponents.velocity.fileTemplatesDir";

	/**
	 * The velocity macro library.
	 */
	public static final String VELOCITY_MACRO_LIBRARY = "bordertech.wcomponents.velocity.macroLibrary";

	/**
	 * Whether to perform server-side XSLT.
	 * @deprecated 1.3.1 No longer used: no replacement, will be removed in v2.0.0.
	 */
	public static final String XSLT_SERVER_SIDE = "bordertech.wcomponents.xslt.enabled";

	/**
	 * Whether to allow corrupt characters for XSLT processing.
	 */
	public static final String XSLT_ALLOW_CORRUPT_CHARACTER = "bordertech.wcomponents.xslt.allow.corrupt.characters";

	/* ****************************
	* DEVELOPER PROPERTIES - used for local debugging.
	* *****************************/
	/**
	 * The flag indicating whether to debug the client side.
	 */
	public static final String DEVELOPER_DEBUG_CLIENT_SIDE = "bordertech.wcomponents.debug.clientSide.enabled";
	/**
	 * The flag indicating whether debug is enabled.
	 */
	public static final String DEVELOPER_DEBUG_ENABLED = "bordertech.wcomponents.debug.enabled";

	/**
	 * The flag indicating whether to dump the UIContext.
	 */
	public static final String DEVELOPER_DUMP_UICONTEXT = "bordertech.wcomponents.developer.UIContextDump.enabled";
	/**
	 * The flag indicating whether to use cluster emulation.
	 */
	public static final String DEVELOPER_MODE_CLUSTER_EMULATION = "bordertech.wcomponents.developer.clusterEmulation.enabled";

	/**
	 * The flag indicating whether to use developer error handling.
	 */
	public static final String DEVELOPER_MODE_ERROR_HANDLING = "bordertech.wcomponents.developer.errorHandling.enabled";

	/**
	 * The flag indicating whether to use the developer toolkit.
	 */
	public static final String DEVELOPER_TOOKIT = "bordertech.wcomponents.lde.devToolkit.enabled";
	/**
	 * The flag indicating whether to validate XML.
	 */
	public static final String DEVELOPER_VALIDATE_XML = "bordertech.wcomponents.debug.validateXML.enabled";
	/**
	 * The flag indicating whether to debug velocity templates.
	 */
	public static final String DEVELOPER_VELOCITY_DEBUG = "bordertech.wcomponents.velocity.debugLayout";

	/* ****************************
	* LDE PROPERTIES - used for running the LDE.
	* *****************************/
	/**
	 * The flag indicating whether to persist sessions in the LDE.
	 */
	public static final String LDE_PERSIST_SESSION = "bordertech.wcomponents.lde.session.persist";

	/**
	 * The flag indicating whether to load persistent sessions in the LDE.
	 */
	public static final String LDE_LOAD_PERSISTENT_SESSION = "bordertech.wcomponents.lde.session.loadPersisted";

	/**
	 * The flag indicating which component to launch in the PlainLauncher LDE.
	 */
	public static final String LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH = "bordertech.wcomponents.lde.component.to.launch";

	/**
	 * The port to launch the LDE server.
	 */
	public static final String LDE_SERVER_PORT = "bordertech.wcomponents.lde.server.port";

	/**
	 * This configuration parameter allows the developer to configure the LDE to use a different set of servlets.
	 */
	public static final String LDE_SERVER_WEBDOCS_DIR = "bordertech.wcomponents.lde.webdocs.dir";

	/**
	 * This configuration parameter allows the developer to configure the LDE to use an external theme.
	 */
	public static final String LDE_SERVER_WEBDOCS_THEME_DIR = "bordertech.wcomponents.lde.theme.dir";

	/**
	 * This configuration parameter allows the developer to configure the LDE to set a resources directory.
	 */
	public static final String LDE_SERVER_WEBDOCS_RESOURCE_DIR = "bordertech.wcomponents.lde.resource.dir";

	/**
	 * This configuration parameter sets up the Jetty realm file for authenticated access.
	 */
	public static final String LDE_SERVER_JETTY_REALM_FILE = "bordertech.wcomponents.lde.server.realm.file";

	/**
	 * This configuration parameter sets up the Jetty realm file for authenticated access.
	 */
	public static final String LDE_SERVER_ENABLE_SHUTDOWN = "bordertech.wcomponents.lde.shutdown.enabled";

	/**
	 * This configuration parameter sets the Jetty session timeout interval.
	 */
	public static final String LDE_SERVER_SESSION_TIMEOUT = "bordertech.wcomponents.lde.session.inactive.interval";

	/**
	 * The flag indicating whether to show the memory profile in the LDE.
	 */
	public static final String LDE_SHOW_MEMORY_PROFILE = "bordertech.wcomponents.lde.show.memory.profile";

	/* ****************************
	* TEST PROPERTIES - used for testing WComponents.
	* *****************************/
	/**
	 * The attribute to check for whether the page is ready.
	 */
	public static final String TEST_SELENIUM_DATA_READY_TAG = "bordertech.wcomponents.test.selenium.pageReadyAttribute";

	/**
	 * The capabilities of each Web Driver.
	 */
	public static final String TEST_SELENIUM_DRIVER_CAPABILITIES = "bordertech.wcomponents.test.selenium.webdriver.{0}.capabilities";

	/**
	 * How many seconds to wait for an element if it is not found.
	 */
	public static final String TEST_SELENIUM_IMPLICIT_WAIT = "bordertech.wcomponents.test.selenium.implicitWait";

	/**
	 * The list of drivers to use for the MultiBrowserRunner.
	 */
	public static final String TEST_SELENIUM_MULTI_BROWSER_DRIVERS = "bordertech.wcomponents.test.selenium.driverTypes";

	/**
	 * The flag indicating whether to run the MultiBrowserRunner in parallel.
	 */
	public static final String TEST_SELENIUM_MULTI_BROWSER_PARALLEL = "bordertech.wcomponents.test.selenium.runParallel";

	/**
	 * The duration in seconds to wait for the page to be ready.
	 */
	public static final String TEST_SELENIUM_PAGE_READY_TIMEOUT = "bordertech.wcomponents.test.selenium.pageReadyTimeout";

	/**
	 * The type of driver to use for the ParameterisedDriver.
	 */
	public static final String TEST_SELENIUM_PARAMETERISED_DRIVER = "bordertech.wcomponents.test.selenium.webdriver";

	/**
	 * The system properties to set for the ParameterisedDriver.
	 */
	public static final String TEST_SELENIUM_PARAMETERISED_DRIVER_SYS_PROPERTIES = "bordertech.wcomponents.test.selenium.systemProperties";
	/**
	 * The duration in milliseconds between poll attempts for page ready.
	 */
	public static final String TEST_SELENIUM_PAGE_READY_POLL_INTERVAL = "bordertech.wcomponents.test.selenium.pageReadyPollInterval";

	/**
	 * The height in pixels of the Selenium screen.
	 */
	public static final String TEST_SELENIUM_SCREEN_HEIGHT = "bordertech.wcomponents.test.selenium.screenHeight";

	/**
	 * The width in pixels of the Selenium screen.
	 */
	public static final String TEST_SELENIUM_SCREEN_WIDTH = "bordertech.wcomponents.test.selenium.screenWidth";

	/**
	 * The flag indicating whether to start the server when the test case launches.
	 */
	public static final String TEST_SELENIUM_SERVER_START = "bordertech.wcomponents.test.selenium.launchServer";

	/**
	 * The URL of the server to connect to for the Selenium test case.
	 */
	public static final String TEST_SELENIUM_SERVER_URL = "bordertech.wcomponents.test.selenium.serverUrl";

	/**
	 * The Antisamy lax configuration file.
	 *
	 * @return the parameter value, or "com/github/bordertech/wcomponents/sanitizers/antisamy-wc-lax.xml" if not set.
	 */
	public static String getAntisamyLaxConfigurationFile() {
		return get().getString(ANTISAMY_LAX_CONFIG_PARAM, "com/github/bordertech/wcomponents/sanitizers/antisamy-wc-lax.xml");
	}

	/**
	 * The Antisamy strict configuration file.
	 *
	 * @return the parameter value, or "com/github/bordertech/wcomponents/sanitizers/antisamy-wc.xml" if not set.
	 */
	public static String getAntisamyStrictConfigurationFile() {
		return get().getString(ANTISAMY_STRICT_CONFIG_PARAM, "com/github/bordertech/wcomponents/sanitizers/antisamy-wc.xml");
	}

	/**
	 * The URL for the application's icon.
	 *
	 * @return the cached valued that configured at initialization, or null if not set.
	 */
	public static String getApplicationIconUrl() {
		return APPLICATION_ICON_URL;
	}

	/**
	 * This flag controls if bean containers using a bean provider should hold the bean in the scratch map for the
	 * entire request processing.
	 *
	 * @return true if hold bean for request, otherwise false
	 */
	public static boolean getBeanProviderRequestScopeEnabled() {
		return get().getBoolean("bordertech.wcomponents.bean.provider.request.scope.enabled", false);
	}

	/**
	 * This flag controls if component ids should be checked for duplicates. As verifying requires extra resources and
	 * memory, this can be disabled if required. It is encouraged projects at least have this set true in development
	 * environment.
	 *
	 * @return the cached valued that configured at initialization, or true if null.
	 */
	public static boolean getCheckDuplicateIds() {
		return CHECK_DUPLICATE_IDS;
	}

	/**
	 * Flag for which bean logic will be used.
	 *
	 * @return whether to use the 'correct' bean logic, or true if null.
	 * @deprecated Will be removed and correct logic always used. Projects should set this parameter to true.
	 */
	@Deprecated
	public static boolean getCorrectBeanLogic() {
		return get().getBoolean("bordertech.wcomponents.bean.logic.correct", true);
	}

	/**
	 * The flag that controls Data list caching.
	 *
	 * @return the parameter value or false if null.
	 */
	public static boolean getDatalistCaching() {
		return get().getBoolean(DATALIST_CACHING_PARAM_KEY, false);
	}

	/**
	 * The default locale used by WComponents.
	 *
	 * @return the parameter value or "en" if not set.
	 */
	public static String getDefaultLocale() {
		return get().getString(DEFAULT_LOCALE, "en");
	}

	/**
	 * The default mime type for a file.
	 *
	 * @return the parameter value or application/octet-stream if not set.
	 */
	public static String getDefaultMimeType() {
		return get().getString(DEFAULT_MIME_TYPE, "application/octet-stream");
	}

	/**
	 * The default rendering engine for templates.
	 *
	 * @return the cached valued that configured at initialization, or null if not set.
	 */
	public static String getDefaultRenderingEngine() {
		return TEMPLATE_RENDERING_ENGINE_DEFAULT;
	}

	/**
	 * Get the implementation classname for a factory method on the given interface classname.
	 *
	 * @param interfaceName the classname of the interface.
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getFactoryImplementation(final String interfaceName) {
		return get().getString(FACTORY_PREFIX + interfaceName);
	}

	/**
	 * The file's mime type based on its extension.
	 *
	 * @param extension the extension to lookup.
	 * @return the parameter value or null if not set.
	 */
	public static String getFileMimeTypeForExtension(final String extension) {
		return get().getString(FILE_MIME_TYPE_PREFIX + extension.toLowerCase());
	}

	/**
	 * The flag indicating whether handlebars should cache.
	 *
	 * @return the parameter value if set, or true if not set.
	 */
	public static boolean getHandlebarsCache() {
		return get().getBoolean(HANDLEBARS_CACHE, true);
	}

	/**
	 * The flag indicating whether to handle an error with a fatal error page factory.
	 *
	 * @return the new parameter value if set, else the old parameter value if set, or false if neither set.
	 */
	public static boolean getHandleErrorWithFatalErrorPageFactory() {
		Boolean parameterValue = get().getBoolean(HANDLE_ERROR_WITH_FATAL_PAGE_FACTORY, null);
		if (parameterValue != null) {
			return parameterValue;
		}

		// fall-back to the old parameter value if the new value is not set.
		return get().getBoolean(HANDLE_ERROR_WITH_FATAL_PAGE_FACTORY_DEPRECATED, false);
	}

	/**
	 * Get the Html Icon class for the help icon.
	 *
	 * @return the parameter value, or "fa-question-circle" if not set.
	 */
	public static String getHtmlIconClassHelp() {
		return get().getString(HTML_ICON_CLASS_HELP, "fa-question-circle");
	}

	/**
	 * Get the Html Icon class for the info icon.
	 *
	 * @return the parameter value, or "fa-info-circle" if not set.
	 */
	public static String getHtmlIconClassInfo() {
		return get().getString(HTML_ICON_CLASS_INFO, "fa-info-circle");
	}

	/**
	 * Get the Html Icon class for the warn icon.
	 *
	 * @return the parameter value, or "fa-exclamation-triangle" if not set.
	 */
	public static String getHtmlIconClassWarn() {
		return get().getString(HTML_ICON_CLASS_WARN, "fa-exclamation-triangle");
	}

	/**
	 * Get the Html Icon class for the error icon.
	 *
	 * @return the parameter value, or "fa-minus-circle" if not set.
	 */
	public static String getHtmlIconClassError() {
		return get().getString(HTML_ICON_CLASS_ERROR, "fa-minus-circle");
	}

	/**
	 * Get the Html Icon class for the success icon.
	 *
	 * @return the parameter value, or "fa-check-circle" if not set.
	 */
	public static String getHtmlIconClassSuccess() {
		return get().getString(HTML_ICON_CLASS_SUCCESS, "fa-check-circle");
	}

	/**
	 * Get the Html Icon class for the add icon.
	 *
	 * @return the parameter value, or "fa-plus-square" if not set.
	 */
	public static String getHtmlIconClassAdd() {
		return get().getString(HTML_ICON_CLASS_ADD, "fa-plus-square");
	}

	/**
	 * Get the Html Icon class for the delete icon.
	 *
	 * @return the parameter value, or "fa-minus-square" if not set.
	 */
	public static String getHtmlIconClassDelete() {
		return get().getString(HTML_ICON_CLASS_DELETE, "fa-minus-square");
	}

	/**
	 * Get the Html Icon class for the edit icon.
	 *
	 * @return the parameter value, or "fa-pencil" if not set.
	 */
	public static String getHtmlIconClassEdit() {
		return get().getString(HTML_ICON_CLASS_EDIT, "fa-pencil");
	}

	/**
	 * Get the Html Icon class for the save icon.
	 *
	 * @return the parameter value, or "fa-floppy-o" if not set.
	 */
	public static String getHtmlIconClassSave() {
		return get().getString(HTML_ICON_CLASS_SAVE, "fa-floppy-o");
	}

	/**
	 * Get the Html Icon class for the search icon.
	 *
	 * @return the parameter value, or "fa-search" if not set.
	 */
	public static String getHtmlIconClassSearch() {
		return get().getString(HTML_ICON_CLASS_SEARCH, "fa-search");
	}

	/**
	 * Get the Html Icon class for the cancel icon.
	 *
	 * @return the parameter value, or "fa-ban" if not set.
	 */
	public static String getHtmlIconClassCancel() {
		return get().getString(HTML_ICON_CLASS_CANCEL, "fa-ban");
	}

	/**
	 * Get the Html Icon class for the menu icon.
	 *
	 * @return the parameter value, or "fa-bars" if not set.
	 */
	public static String getHtmlIconClassMenu() {
		return get().getString(HTML_ICON_CLASS_MENU, "fa-bars");
	}

	/**
	 * Get the HTML Icon class for the print icon.
	 *
	 * @return the parameter value, or "fa-print" if not set.
	 */
	public static String getHtmlIconClassPrint() {
		return get().getString(HTML_ICON_CLASS_PRINT, "fa-print");
	}

	/**
	 * The I18n resource bundle base name.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getI18nResourceBundleBaseName() {
		return get().getString(I18N_RESOURCE_BUNDLE_BASE_NAME);
	}

	/**
	 * The I18n theme resource bundle base name.
	 *
	 * @return the parameter value if set, otherwise the default.
	 */
	public static String getI18nThemeResourceBundleBaseName() {
		return get().getString(I18N_THEME_RESOURCE_BUNDLE_BASE_NAME, "com/github/bordertech/wcomponents/theme-messages");
	}

	/**
	 * The flag that controls throwing errors for integrity issues.
	 *
	 * @return the parameter value, or false if null.
	 */
	public static boolean getIntegrityErrorMode() {
		return get().
				getBoolean(INTEGRITY_ERROR_MODE, false);
	}

	/**
	 * The internal message content.
	 *
	 * @param key the key for the message.
	 * @return the parameter value, or null if not set.
	 */
	public static String getInternalMessage(final String key) {
		return get().getString(key);
	}

	/**
	 * The logout URL.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getLogoutUrl() {
		return get().getString(LOGOUT_URL);
	}

	/**
	 * The flag indicating whether plaintext should cache.
	 *
	 * @return the parameter value if set, or true if not set.
	 */
	public static boolean getPlaintextCache() {
		return get().getBoolean(PLAINTEXT_CACHE, true);
	}

	/**
	 * The current project version.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getProjectVersion() {
		return get().getString(PROJECT_VERSION);
	}

	/**
	 * The overridden renderer class for the given fully qualified classname.
	 *
	 * @param classname the fully qualified classname of the WComponent to check for an overridden renderer.
	 * @return the parameter value, else null if not set.
	 */
	public static String getRendererOverride(final String classname) {
		if (StringUtils.isBlank(classname)) {
			throw new IllegalArgumentException("classname cannot be blank.");
		}

		return get().getString(RENDERER_OVERRIDE_PREFIX + classname);
	}

	/**
	 * The response cache header settings for the given contentType.
	 *
	 * @param contentType the content type in the format 'type.[cache | nocache]', e.g. 'page.nocache'.
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getResponseCacheHeaderSettings(final String contentType) {
		String parameter = MessageFormat.format(RESPONSE_CACHE_HEADER_SETTINGS, contentType);
		return get().getString(parameter);
	}

	/**
	 * The response cache settings.
	 *
	 * @return the parameter value or 'public, max-age=31536000' if not set.
	 */
	public static String getResponseCacheSettings() {
		return get().getString(RESPONSE_CACHE_SETTINGS, RESPONSE_DEFAULT_CACHE_SETTINGS);
	}

	/**
	 * The response no cache settings.
	 *
	 * @return the parameter value or 'no-cache, no-store, must-revalidate, private' if not set.
	 */
	public static String getResponseNoCacheSettings() {
		return get().getString(RESPONSE_NO_CACHE_SETTINGS, RESPONSE_DEFAULT_NO_CACHE_SETTINGS);
	}

	/**
	 * Whether to enable subsessions in the servlet.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getServletEnableSubsessions() {
		return get().getBoolean(SERVLET_ENABLE_SUBSESSIONS, false);
	}

	/**
	 * The parameter variable that contains the URL path to the support servlet that services targeted requests for a
	 * Portlet application. This parameter is only applicable to portlet applications
	 *
	 * @return The parameter value, or null if not set.
	 */
	public static String getServletSupportPath() {
		return get().getString(SUPPORT_SERVLET_PATH);
	}

	/**
	 * The step error URL.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getStepErrorUrl() {
		return get().getString(STEP_ERROR_URL);
	}

	/**
	 * Whether to use sticky focus.
	 *
	 * @return the parameter value or false if not set.
	 */
	public static boolean getStickyFocus() {
		return get().getBoolean(STICKY_FOCUS, false);
	}

	/**
	 * The templating engine classname for the given engine.
	 *
	 * @param engineName the name of the templating engine.
	 * @return the parameter value, or null if not set.
	 */
	public static String getTemplateRenderingEngine(final String engineName) {
		return get().getString(TEMPLATE_RENDERING_ENGINE + "." + engineName);
	}

	/**
	 * @return The render mode, can be either "on", "off" or "sniff". "on" means the render will occur. "off" means the
	 * render will not occur. "sniff" means the render will occur based on user agent.
	 */
	public static String getTemplateRenderingMode() {
		return get().getString(TEMPLATE_RENDERING_MODE, "off");
	}

	/**
	 * The flag indicating whether to terminate the session on error.
	 *
	 * @return the parameter value or false if not set.
	 */
	public static boolean getTerminateSessionOnError() {
		return get().getBoolean(TERMINATE_SESSION_ON_ERROR, false);
	}

	/**
	 * The parameter variable that contains the URL path to the web content for the Theme.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getThemeContentPath() {
		return get().getString(THEME_CONTENT_PATH);
	}

	/**
	 * The theme name.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getThemeName() {
		return get().getString(THEME_NAME);
	}

	/**
	 * The timeout period in seconds.
	 *
	 * @return the cached valued that configured at initialization, or 0 if null.
	 */
	public static int getTimeoutPeriod() {
		return TIMEOUT_PERIOD;
	}

	/**
	 * The timeout warning period in seconds.
	 *
	 * @return the cached valued that configured at initialization, or 300.
	 */
	public static int getTimeoutWarningPeriod() {
		return TIMEOUT_WARNING_PERIOD;
	}

	/**
	 * The tracking application name.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getTrackingApplicationName() {
		return get().getString(TRACKING_APPLICATION_NAME);
	}

	/**
	 * The tracking client id.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getTrackingClientId() {
		return get().getString(TRACKING_CLIENT_ID);
	}

	/**
	 * The tracking cookie domain.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getTrackingCookieDomain() {
		return get().getString(TRACKING_COOKIE_DOMAIN);
	}

	/**
	 * The tracking data collection domain.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getTrackingDataCollectionDomain() {
		return get().getString(TRACKING_DATA_COLLECTION_DOMAIN);
	}

	/**
	 * The flag indicating whether to cache velocity output.
	 *
	 * @return the parameter value, or true if not set.
	 */
	public static boolean getVelocityCache() {
		return get().getBoolean(VELOCITY_CACHE, true);
	}

	/**
	 * The flag indicating whether to cache velocity templates.
	 *
	 * @return the parameter value if set, or false if not set.
	 */
	public static boolean getVelocityCacheTemplates() {
		return get().getBoolean(VELOCITY_CACHE_TEMPLATES);
	}

	/**
	 * The directory containing the velocity templates.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getVelocityFileTemplates() {
		return get().getString(VELOCITY_FILE_TEMPLATES);
	}

	/**
	 * The velocity macro library.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getVelocityMacroLibrary() {
		return get().getString(VELOCITY_MACRO_LIBRARY);
	}

	/**
	 * The flag indicating whether the whitespace filter is enabled.
	 *
	 * @return the parameter value if set, or true if not set.
	 */
	public static boolean getWhitespaceFilter() {
		return get().getBoolean(WHITESPACE_FILTER, true);
	}

	/**
	 * Whether to perform XSLT on the server side.
	 *
	 * @return true
	 * @deprecated 1.3.1 No longer used: no replacement, will be removed in v2.0.0.
	 */
	public static boolean getXsltServerSide() {
		return true;
	}

	/**
	 * Whether to allow corrupt characters for XSLT processing.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getXsltAllowCorruptCharacters() {
		return get().getBoolean(XSLT_ALLOW_CORRUPT_CHARACTER, false);
	}

	/* ****************************
	* DEVELOPER PROPERTIES - used for local debugging.
	* *****************************/
	/**
	 * The flag indicating whether to use cluster emulation.
	 *
	 * @return the parameter value or false if not set.
	 */
	public static boolean getDeveloperClusterEmulation() {
		return get().getBoolean(DEVELOPER_MODE_CLUSTER_EMULATION, false);
	}

	/**
	 * The flag indicating whether to debug the client side.
	 *
	 * @return the parameter value if set, else false if not set
	 */
	public static boolean getDeveloperDebugClientSide() {
		return get().getBoolean(DEVELOPER_DEBUG_CLIENT_SIDE, false);
	}

	/**
	 * The flag indicating whether developer debug is enabled.
	 *
	 * @return the parameter value if set, or false if not set.
	 */
	public static boolean getDeveloperDebugEnabled() {
		return get().getBoolean(DEVELOPER_DEBUG_ENABLED, false);
	}

	/**
	 * The flag indicating whether to dump the UIContext.
	 *
	 * @return the parameter value or false if not set.
	 */
	public static boolean getDeveloperDumpUIContext() {
		return get().getBoolean(DEVELOPER_DUMP_UICONTEXT, false);
	}

	/**
	 * The flag indicating whether to use developer error handling.
	 *
	 * @return the parameter value or false if not set.
	 */
	public static boolean getDeveloperErrorHandling() {
		return get().getBoolean(DEVELOPER_MODE_ERROR_HANDLING, false);
	}

	/**
	 * The flag indicating whether to use the developer toolkit.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getDeveloperToolkit() {
		return get().getBoolean(DEVELOPER_TOOKIT, false);
	}

	/**
	 * The flag indicating whether to enable validate XML.
	 *
	 * @return the parameter value if set, or false if not set.
	 */
	public static boolean getDeveloperValidateXml() {
		return get().getBoolean(DEVELOPER_VALIDATE_XML, false);
	}

	/**
	 * The flag indicating whether to debug velocity templates.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getDeveloperVelocityDebug() {
		return get().getBoolean(DEVELOPER_VELOCITY_DEBUG, false);
	}

	/* ****************************
	* LDE PROPERTIES - used for running the LDE.
	* *****************************/
	/**
	 * Indicates whether new sessions should be populated from a persisted session.
	 *
	 * @return the parameter value if set, or false if not set.
	 */
	public static boolean getLdeLoadPersistedSessionEnabled() {
		return get().getBoolean(LDE_LOAD_PERSISTENT_SESSION, false);
	}

	/**
	 * Indicates whether sessions should be persisted.
	 *
	 * @return the parameter value if set, or false if not set.
	 */
	public static boolean getLdePersistSessionEnabled() {
		return get().getBoolean(LDE_PERSIST_SESSION, false);
	}

	/**
	 * The component for the PlainLauncher to launch.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String getLdePlainLauncherComponentToLaunch() {
		return get().getString(LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH);
	}

	/**
	 * The flag indicating whether the LDE server will shutdown on receiving a shutdown request.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getLdeServerEnableShutdown() {
		return get().getBoolean(LDE_SERVER_ENABLE_SHUTDOWN, false);
	}

	/**
	 * The LDE server port.
	 *
	 * @return the parameter value if set, or 8080 if not set.
	 */
	public static int getLdeServerPort() {
		return get().getInt(LDE_SERVER_PORT, 8080);
	}

	/**
	 * The LDE server jetty realm file.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getLdeServerJettyRealmFile() {
		return get().getString(LDE_SERVER_JETTY_REALM_FILE);
	}

	/**
	 * The flag indicating whether the LDE server should show the memory profile.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getLdeServerShowMemoryProfile() {
		return get().getBoolean(LDE_SHOW_MEMORY_PROFILE, false);
	}

	/**
	 * The LDE server session timeout.
	 *
	 * @return the parameter value if set, or 0 if not set.
	 */
	public static int getLdeServerSessionTimeout() {
		return get().getInt(LDE_SERVER_SESSION_TIMEOUT, 0);
	}

	/**
	 * The LDE server Web Docs Dir.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String[] getLdeServerWebDocsDir() {
		return get().getStringArray(LDE_SERVER_WEBDOCS_DIR);
	}

	/**
	 * The LDE server Resource Web Docs Dir.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String[] getLdeServerWebDocsResourcesDir() {
		return get().getStringArray(LDE_SERVER_WEBDOCS_RESOURCE_DIR);
	}

	/**
	 * The LDE server Theme Web Docs Dir.
	 *
	 * @return the parameter value if set, or null if not set.
	 */
	public static String[] getLdeServerWebDocsThemeDir() {
		return get().getStringArray(LDE_SERVER_WEBDOCS_THEME_DIR);
	}

	/* ****************************
	* TEST PROPERTIES - used for testing WComponents.
	* *****************************/
	/**
	 * The HTML body tag indicating the page is ready.
	 *
	 * @return the parameter value, or "data-wc-domready" if not set.
	 */
	public static String getTestSeleniumDataReadyTag() {
		return get().getString(TEST_SELENIUM_DATA_READY_TAG, "data-wc-domready");
	}

	/**
	 * The WebDriver capabilities for the given driver type.
	 *
	 * @param driverType the driver type.
	 * @return the parameter value if set, or empty properties if not set.
	 */
	public static Properties getTestSeleniumDriverCapabilities(final String driverType) {
		String paramName = MessageFormat.format(TEST_SELENIUM_DRIVER_CAPABILITIES, driverType);

		return get().getProperties(paramName);
	}

	/**
	 * The duration in seconds to wait for an element if it is not found.
	 *
	 * @return the parameter value if set, or 5 if not set.
	 */
	public static long getTestSeleniumImplicitWait() {
		return get().getLong(TEST_SELENIUM_IMPLICIT_WAIT, 5);
	}

	/**
	 * The list of drivers to use in the MultiBrowserRunner.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String[] getTestSeleniumMultiBrowserDrivers() {
		return get().getStringArray(TEST_SELENIUM_MULTI_BROWSER_DRIVERS);
	}

	/**
	 * The list of drivers to use in the MultiBrowserRunner for the given test class.
	 *
	 * @param testClassName the name of the current test class.
	 * @return the parameter value, or the global default if not set.
	 */
	public static String[] getTestSeleniumMultiBrowserDrivers(final String testClassName) {
		//See if there are specific drivers set up for this test.
		final String testSpecificDriversParam = TEST_SELENIUM_MULTI_BROWSER_DRIVERS + "." + testClassName;
		String[] drivers = get().getStringArray(testSpecificDriversParam);

		//If there are none for this test, use the default list.
		return ArrayUtils.isEmpty(drivers) ? getTestSeleniumMultiBrowserDrivers() : drivers;
	}

	/**
	 * The flag indicating whether the MultiBrowserRunner should run in parallel.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getTestSeleniumMultiBrowserDriverParallel() {
		return get().getBoolean(TEST_SELENIUM_MULTI_BROWSER_PARALLEL, false);
	}

	/**
	 * The interval in milliseconds between page ready poll attempts.
	 *
	 * @return the parameter value, or 50 if not set.
	 */
	public static long getTestSeleniumPageReadyPollInterval() {
		return get().getLong(TEST_SELENIUM_PAGE_READY_POLL_INTERVAL, 50);
	}

	/**
	 * The duration in seconds to wait for the page to be ready.
	 *
	 * @return the parameter value if set, or 10 if not set.
	 */
	public static int getTestSeleniumPageReadyTimeout() {
		return get().getInt(TEST_SELENIUM_PAGE_READY_TIMEOUT, 10);
	}

	/**
	 * The driver to use for the ParameterisedDriver.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getTestSeleniumParameterisedDriver() {
		return get().getString(TEST_SELENIUM_PARAMETERISED_DRIVER);
	}

	/**
	 * The driver to use for the ParameterisedDriver for the given test class.
	 *
	 * @param testClassName the name of the current test class.
	 * @return the parameter value, or the global default if not set.
	 */
	public static String getTestSeleniumParameterisedDriver(final String testClassName) {
		String driver = get().getString(TEST_SELENIUM_PARAMETERISED_DRIVER + "." + testClassName);
		return driver == null ? getTestSeleniumParameterisedDriver() : driver;
	}

	/**
	 * The system properties to be set in the ParameterisedDriver.
	 *
	 * @return the parameter value, or empty Properties if null.
	 */
	public static Properties getTestSeleniumParameterisedDriverSysProperties() {
		return get().getProperties(TEST_SELENIUM_PARAMETERISED_DRIVER_SYS_PROPERTIES);
	}

	/**
	 * The height in pixels of the selenium screen.
	 *
	 * @return the parameter value, or 1080 if not set.
	 */
	public static int getTestSeleniumScreenHeight() {
		return get().getInt(TEST_SELENIUM_SCREEN_HEIGHT, 1080);
	}

	/**
	 * The width in pixels of the selenium screen.
	 *
	 * @return the parameter value, or 1920 if not set.
	 */
	public static int getTestSeleniumScreenWidth() {
		return get().getInt(TEST_SELENIUM_SCREEN_WIDTH, 1920);
	}

	/**
	 * The flag indicating whether to start the server when the test case launches.
	 *
	 * @return the parameter value, or false if not set.
	 */
	public static boolean getTestSeleniumServerStart() {
		return get().getBoolean(TEST_SELENIUM_SERVER_START, false);
	}

	/**
	 * The flag indicating whether to start the server when the test case launches for this specific test class.
	 *
	 * @param testClassName the name of the test class.
	 * @return the parameter value, or the global default if not set.
	 */
	public static boolean getTestSeleniumServerStart(final String testClassName) {
		Boolean testClassValue = get().getBoolean(TEST_SELENIUM_SERVER_START + "." + testClassName, null);
		return testClassValue == null ? getTestSeleniumServerStart() : testClassValue;
	}

	/**
	 * The URL to run a Selenium test against.
	 *
	 * @return the parameter value, or null if not set.
	 */
	public static String getTestSeleniumServerUrl() {
		return get().getString(TEST_SELENIUM_SERVER_START);
	}

	/**
	 * The URL to run a Selenium test against for this specific test class.
	 *
	 * @param testClassName the name of the test class.
	 * @return the parameter value, or the global default if not set.
	 */
	public static String getTestSeleniumServerUrl(final String testClassName) {
		String testClassValue = get().getString(TEST_SELENIUM_SERVER_URL + "." + testClassName);
		return testClassValue == null ? getTestSeleniumServerUrl() : testClassValue;
	}

	/**
	 * Shorthand convenience method to get the Configuration instance.
	 *
	 * @return the Configuration instance.
	 */
	private static Configuration get() {
		return Config.getInstance();
	}

	/**
	 * Private constructor for static class.
	 */
	private ConfigurationProperties() {
		//No-impl
	}

}
