package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Util;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A base implementation for WEnvironment.
 *
 * @author Martin Shevchenko
 */
public abstract class AbstractEnvironment implements Environment {

	/**
	 * The URL that responses should be directed to.
	 */
	private String postPath;

	/**
	 * The application host path.
	 */
	private String appHostPath;

	/**
	 * The base url. e.g. for <code>"http://localhost:1234/bar/something?a=b"</code>, it would be
	 * <code>"http://localhost/bar"</code>.
	 */
	private String baseUrl;

	/**
	 * The host free part of the base url. e.g. for <code>"http://localhost:1234/bar/something?a=b"</code>, it would be
	 * <code>"bar"</code>.
	 */
	private String hostFreeBaseUrl;

	/**
	 * The application id, used for namespacing component IDs in the UI.
	 */
	private String appId;

	/**
	 * The session token, used for ensuring requests are for the correct session.
	 */
	private String sessionToken;

	/**
	 * The step counter, used for ensuring requests are not processed out of order or multiple times.
	 */
	private int step;

	/**
	 * The action step counter, used for the request being processed is current.
	 */
	private int actionStep;

	/**
	 * An optional form encoding type, for example "multipart/form-data" if files need to be uploaded.
	 */
	private String formEncType;

	/**
	 * The user-agent information passed by the client's browser.
	 */
	private UserAgentInfo userAgentInfo;

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(AbstractEnvironment.class);

	// === Start WEnvironment implementation ===
	/**
	 * Construct the path to the support servlet. Web components that need to construct a URL to target this servlet
	 * will use this method.
	 *
	 * @return the path to the AJAX servlet.
	 */
	@Override
	public String getWServletPath() {
		if (Config.getInstance().getString(Environment.SUPPORT_SERVLET_PATH) != null) {
			return getServletPath(Environment.SUPPORT_SERVLET_PATH);
		}

		return getPostPath();
	}

	/**
	 * Constructs the path to one of the various helper servlets. This is used by the more specific getXXXPath methods.
	 *
	 * @param paramName the name of the parameter which holds the relative servlet path.
	 * @return the path to the servlet corresponding to the given parameter name.
	 */
	private String getServletPath(final String paramName) {
		String relativePath = Config.getInstance().getString(paramName);

		if (relativePath == null) {
			LOG.error("The servlet path " + paramName + " has not been defined.");
		}

		String context = getHostFreeBaseUrl();

		if (!Util.empty(context)) {
			return context + relativePath;
		}

		return relativePath;
	}

	/**
	 * @return the theme path.
	 */
	@Override
	public String getThemePath() {
		String themePath = Config.getInstance().getString(Environment.THEME_CONTENT_PATH);

		// No theme path, so use the main servlet to feed up the theme resources
		if (Util.empty(themePath)) {
			String path = getWServletPath() + "/" + Environment.THEME_RESOURCE_PATH_NAME;
			return path;
		}

		return themePath;
	}

	/**
	 * @return the post path.
	 */
	@Override
	public String getPostPath() {
		return postPath;
	}

	/**
	 * Sets the post path.
	 *
	 * @param postPath the post path.
	 */
	@Override
	public void setPostPath(final String postPath) {
		this.postPath = postPath;
	}

	/**
	 * @return the app host path.
	 */
	@Override
	public String getAppHostPath() {
		return appHostPath;
	}

	/**
	 * @return the base url.
	 */
	@Override
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return the host name free base url.
	 */
	@Override
	public String getHostFreeBaseUrl() {
		return hostFreeBaseUrl;
	}

	/**
	 * @return the application id.
	 * @deprecated no longer used. Use {@link WApplication#getIdName()} instead.
	 */
	@Override
	@Deprecated
	public String getAppId() {
		return appId;
	}

	/**
	 * Sets the application id. The applicationId must not contain {@link WComponent#ID_CONTEXT_SEPERATOR}.
	 *
	 * @param appId the appId to set.
	 * @deprecated no longer used. Use {@link WApplication#setIdName(String)} instead.
	 */
	@Override
	@Deprecated
	public void setAppId(final String appId) {
		this.appId = appId;
	}

	/**
	 * @return the current session token.
	 */
	@Override
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Sets the current session token.
	 *
	 * @param sessionToken the session token to set.
	 */
	@Override
	public void setSessionToken(final String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * @return the current step.
	 */
	@Override
	public int getStep() {
		return step;
	}

	/**
	 * Sets the current set.
	 *
	 * @param step the step to set.
	 */
	@Override
	public void setStep(final int step) {
		this.step = step;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	@Override
	public int getActionStep() {
		return actionStep;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated portal specific
	 */
	@Deprecated
	@Override
	public void setActionStep(final int actionStep) {
		this.actionStep = actionStep;
	}

	/**
	 * @return the hidden parameter map.
	 */
	@Override
	public Map<String, String> getHiddenParameters() {
		Map<String, String> map = new HashMap<>();
		map.put(STEP_VARIABLE, String.valueOf(getStep()));
		map.put(SESSION_TOKEN_VARIABLE, getSessionToken());
		return map;
	}

	/**
	 * @return the form encoding type.
	 */
	@Override
	public String getFormEncType() {
		return formEncType;
	}

	/**
	 * Sets the form encoding type.
	 *
	 * @param formEncType the form encoding type.
	 */
	@Override
	public void setFormEncType(final String formEncType) {
		this.formEncType = formEncType;
	}

	/**
	 * @return the user agent info.
	 */
	@Override
	public UserAgentInfo getUserAgentInfo() {
		return this.userAgentInfo;
	}

	// === End WEnvironment implementation ===
	/**
	 * Sets the app host path.
	 *
	 * @param appHostPath the app host path.
	 */
	protected void setAppHostPath(final String appHostPath) {
		this.appHostPath = appHostPath;
	}

	/**
	 * Sets the base url.
	 *
	 * @param baseUrl the base url
	 */
	protected void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Sets the host free base url.
	 *
	 * @param hostFreeBaseUrl the host free base url.
	 */
	protected void setHostFreeBaseUrl(final String hostFreeBaseUrl) {
		this.hostFreeBaseUrl = hostFreeBaseUrl;
	}

	/**
	 * Sets the user-agent info.
	 *
	 * @param userAgentInfo the user-agent info to set.
	 */
	public void setUserAgentInfo(final UserAgentInfo userAgentInfo) {
		this.userAgentInfo = userAgentInfo;
	}
}
