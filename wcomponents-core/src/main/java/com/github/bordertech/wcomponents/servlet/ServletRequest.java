package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.AbstractRequest;
import com.github.bordertech.wcomponents.Request;
import java.io.Serializable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;

/**
 * An implementation of {@link Request} using HttpServletRequests.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class ServletRequest extends AbstractRequest {

	/**
	 * Stores the parameters for this request.
	 */
	private final Map<String, String[]> parameters;

	/**
	 * Stores the uploaded files for this request.
	 */
	private final Map<String, FileItem[]> files;
	/**
	 * The backing servlet request.
	 */
	private final HttpServletRequest backing;

	/**
	 * Creates a ServletRequest.
	 *
	 * @param aBacking the backing servlet request.
	 */
	public ServletRequest(final HttpServletRequest aBacking) {
		backing = aBacking;
		parameters = ServletUtil.getRequestParameters(backing);
		files = ServletUtil.getRequestFileItems(backing);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String[]> getParameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, FileItem[]> getFiles() {
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getAttribute(final String key) {
		return (Serializable) backing.getAttribute(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(final String key, final Serializable value) {
		backing.setAttribute(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getSessionAttribute(final String key) {
		HttpSession session = backing.getSession(false);

		if (session == null) {
			return null;
		}

		return (Serializable) session.getAttribute(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getAppSessionAttribute(final String key) {
		return getSessionAttribute(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSessionAttribute(final String key, final Serializable value) {
		HttpSession session = backing.getSession(true);
		session.setAttribute(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAppSessionAttribute(final String key, final Serializable value) {
		setSessionAttribute(key, value);
	}

	/**
	 * Support for Public Render Parameters in Portal. In a Servlet environment, this will be the same as the session.
	 *
	 * @param key The key for the parameter.
	 * @param value The value of the parameter.
	 * @since 1.0.0
	 * @deprecated portal specific. user {@link #setSessionAttribute(String, Serializable)}
	 */
	@Override
	public void setRenderParameter(final String key, final Serializable value) {
		setSessionAttribute(key, value);
	}

	/**
	 * Support for Public Render Parameters in Portal. In a Servlet environment, this will be the same as the session.
	 *
	 * @param key The key for the parameter.
	 * @return The value of the parameter.
	 * @since 1.0.0
	 * @deprecated portal specific. user {@link #getSessionAttribute(String)}
	 */
	@Override
	public Serializable getRenderParameter(final String key) {
		return getSessionAttribute(key);
	}

	/**
	 * Returns a boolean indicating whether the authenticated user is included in the specified logical "role". Roles
	 * and role membership can be defined using deployment descriptors. If the user has not been authenticated, the
	 * method returns false.
	 *
	 * @param role a String specifying the name of the role.
	 * @return a boolean indicating whether the user making this request belongs to a given role; false if the user has
	 * not been authenticated.
	 * @since 1.0.0
	 * @see HttpServletRequest#isUserInRole(String)
	 */
	@Override
	public boolean isUserInRole(final String role) {
		return backing.isUserInRole(role);
	}

	/**
	 * @return the name of the HTTP method with which this request was made, for example, GET, POST, or PUT.
	 */
	@Override
	public String getMethod() {
		return backing.getMethod();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxInactiveInterval() {
		HttpSession session = backing.getSession(false);
		if (session == null) {
			return -1;
		}
		return session.getMaxInactiveInterval();
	}
}
