package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.Map;

/**
 * Methods for finding out about the environment in which wcomponents are hosted.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public interface Environment extends Serializable {

	/**
	 * The URL/Post variable that will store the session token. The session token is used to prevent Cross-site request
	 * forgery (CSRF) attacks.
	 */
	String SESSION_TOKEN_VARIABLE = "wc_t";

	/**
	 * The URL/Post variable that will store the step number. The step number is used to identify the request is from
	 * the most currently rendered view.
	 */
	String STEP_VARIABLE = "wc_s";

	/**
	 * The URL/Post variable that will store the action step number. The action step number is used to identify the
	 * request is still related to the current action step.
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	String STEP_ACTION_VARIABLE = "wc_a";

	/**
	 * The key used for caching content.
	 */
	String CONTENT_CACHE_KEY = "contentCacheKey";

	/**
	 * The URL/Post variable that will identify a specific wcomponent to process the request. A request that includes
	 * this variable is called a targeted request. Targeted requests are used to return document content such as image
	 * data and PDFs. It is also used to support AJAX regions. Targeted requests are handled by the
	 * WContentHelperServlet.
	 */
	String TARGET_ID = "wc_target";

	/**
	 * The name of the theme path that indicates a theme resource has been requested.
	 */
	String THEME_RESOURCE_PATH_NAME = "wc_th";

	/**
	 * The parameter variable that contains the URL path to the support servlet that services targeted requests for a
	 * Portlet application. This parameter is only applicable to portlet applications
	 */
	String SUPPORT_SERVLET_PATH = "bordertech.wcomponents.servlet.support.path";

	/**
	 * The parameter variable that contains the URL path to the web content for the Theme.
	 */
	String THEME_CONTENT_PATH = "bordertech.wcomponents.theme.content.path";

	/**
	 * The URL query string parameter variable name that is used to ensure that the URL appears unique to the browser.
	 * This is used to prevent caching of PDFs, TR5s etc by the client.
	 */
	String UNIQUE_RANDOM_PARAM = "no-cache";

	/**
	 * The key we use to store the primary contexts in the user's session. The AJAX and content helper servlets will use
	 * this key to retrieve the appropriate UIC and process the request. The map is keyed by application Id, and will
	 * contain one entry per Portlet in the application. If the application does not use portlets, the map will only
	 * contain the single UIContext for the main servlet.
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	String PRIMARY_UIC_MAP_SESSION_KEY = "ajax.control.uic";

	/**
	 * Gets the relative URL path to which this WComponent application should post its requests.
	 * <p>
	 * Examples of what this method may return:
	 * </p>
	 * <dl>
	 * <dt>Servlet</dt><dd>/app</dd>
	 * </dl>
	 *
	 * @return the relative URL path to which this WComponent application should post its requests.
	 */
	String getPostPath();

	/**
	 * Sets the relative URL path to which this WComponent application should post its requests.
	 *
	 * @param postPath the post path.
	 */
	void setPostPath(final String postPath);

	/**
	 * Gets the URL path for the WComponent support servlets, which handle serving content for Targetable WComponents,
	 * AJAX and data lists. There needs to be a servlet (WServlet) deployed to serve Targetable content. This method
	 * returns the URL to that servlet.
	 * <p>
	 * Examples of what this method may return:
	 * </p>
	 * <dl>
	 * <dt>Servlet</dt><dd>/app</dd>
	 * </dl>
	 *
	 * @return the URL path suitable for serving the content of Targetable wcomponents.
	 */
	String getWServletPath();

	/**
	 * Gets the base URL under which web applications are hosted. This AppHostPath can be used to create url links to
	 * applications that have been given fixed relative names.
	 *
	 * @return the base URL under which web applications are hosted.
	 */
	String getAppHostPath();

	/**
	 * Gets the base URL under which the web content for the Theme is hosted. Web content is static content such as css,
	 * js, html, and image files.
	 *
	 * @return the base URL under which the web content for the Theme is hosted.
	 */
	String getThemePath();

	/**
	 * Get the base url at which this web application is hosted.
	 * <p>
	 * Implementations ensure that this method returns a URL WITHOUT a trailing slash, as in above example.
	 * </p>
	 *
	 * @return the base url at which this web application is hosted.
	 */
	String getBaseUrl();

	/**
	 * Get the "host-free" part of the base url for this web application. Eg, if the base url is
	 * http://localhost:8080/visas, then the "host-free" part is "/visas"
	 * <p>
	 * Examples of what this method may return:
	 * </p>
	 * <dl>
	 * <dt>Servlet</dt><dd></dd>
	 * </dl>
	 *
	 * @return Get the "host-free" part of the base url for this web application.
	 */
	String getHostFreeBaseUrl();

	/**
	 * Get a unique "app" id. This is intended to help with portals where multiple applications are present in the one
	 * document. The app id will be a machine generated unique identifier, but must not contain the character
	 * {@link WComponent#ID_CONTEXT_SEPERATOR}.
	 *
	 * @return the unique application id for this application.
	 * @deprecated no longer used. Use {@link WApplication#getIdName()} instead.
	 */
	@Deprecated
	String getAppId();

	/**
	 * Sets the unique "app" id of the portlet.
	 *
	 * @param appId the unique application id.
	 * @deprecated no longer used. Use {@link WApplication#setIdName(String)} instead.
	 */
	@Deprecated
	void setAppId(String appId);

	/**
	 * Gets the session token. This is intended to make sure the request is for the correct session.
	 *
	 * @return the session token.
	 */
	String getSessionToken();

	/**
	 * Sets the session token.
	 *
	 * @param sessionToken the session token.
	 */
	void setSessionToken(String sessionToken);

	/**
	 * Gets the page step number. This is intended to make sure the request being processed is on the correct step.
	 *
	 * @return the page step number.
	 */
	int getStep();

	/**
	 * Sets the step page number.
	 *
	 * @param step the step number.
	 */
	void setStep(int step);

	/**
	 * Gets the page action step number. This is intended to make sure the request being processed is on the correct
	 * action step.
	 *
	 * @return the page action step number.
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	int getActionStep();

	/**
	 * Sets the page action step number.
	 *
	 * @param actionStep the step action number.
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	void setActionStep(int actionStep);

	/**
	 * Return a Map containing the hidden parameters that are needed for the WServlet to work correctly. Amongst these
	 * are the step number and the window id. This map may be mutated (ie, a new Map instance is created and returned).
	 * The map may have additional variables added, and it may be turned into a path using {@link WebUtilities#getPath}
	 *
	 * @return a Map containing the hidden parameters required by WServlet.
	 */
	Map<String, String> getHiddenParameters();

	/**
	 * Sets the form encoding type, e.g. "multipart/form-data" when there are files being uploaded.
	 *
	 * @param enctype the encoding type.
	 */
	void setFormEncType(String enctype);

	/**
	 * @return the form encoding type.
	 */
	String getFormEncType();

	/**
	 * @return Details about the client browser.
	 */
	UserAgentInfo getUserAgentInfo();
}
