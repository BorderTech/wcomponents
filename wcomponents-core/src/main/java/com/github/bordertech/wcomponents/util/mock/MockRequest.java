package com.github.bordertech.wcomponents.util.mock;

import com.github.bordertech.wcomponents.AbstractRequest;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WButton;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.fileupload.FileItem;

/**
 * A mock request is useful when you want to write junits and the like. Normally the container running the components
 * handles this, but in the case of junits, you are the container, so you must supply a request. MockRequest fills this
 * requirement.
 *
 * @author Martin Shevchenko
 * @author Rick Brown
 * @since 1.0.0
 */
public class MockRequest extends AbstractRequest {

	/**
	 * Stores the mock parameters for this request.
	 */
	private final Map<String, Object> parameters = new HashMap<>(0);

	/**
	 * Stores the mock uploaded files for this request.
	 */
	private final Map<String, FileItem> files = new HashMap<>(0);

	/**
	 * A store of arbitrary, application-defined attributes.
	 */
	private final Map<String, Serializable> attributes = new HashMap<>();

	/**
	 * Since there's no backing HTTP session, the session attributes are just stored in a map.
	 */
	private final Map<String, Serializable> sessionAttributes = new HashMap<>();

	/**
	 * The set of roles which the user has.
	 */
	private final Set<String> userRoles = new HashSet<>();

	/**
	 * The mock request method. Defaults to POST.
	 */
	private String method = "POST";

	/**
	 * The mock maxInactiveInterval. Defaults to -1 (no timeout)
	 */
	private int maxInactiveInterval = -1;

	//=== Start MockRequest specific convenience methods ===
	/**
	 * Sets a parameter.
	 *
	 * @param key the parameter key.
	 * @param value the parameter value.
	 */
	public void setParameter(final String key, final String value) {
		parameters.put(key, value);
	}

	/**
	 * Sets a parameter.
	 *
	 * @param key the parameter key.
	 * @param value the parameter values.
	 */
	public void setParameter(final String key, final String[] value) {
		parameters.put(key, value);
	}

	/**
	 * Convenience method that adds a parameter emulating a button press.
	 *
	 * @param uic the current user's UIContext
	 * @param button the button to add a parameter for.
	 */
	public void addParameterForButton(final UIContext uic, final WButton button) {
		UIContextHolder.pushContext(uic);

		try {
			parameters.put(button.getId(), "x");
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * Sets mock file upload contents.
	 *
	 * @deprecated Because it appears unused
	 * @param key the parameter key.
	 * @param contents the file binary data.
	 */
	public void setFileContents(final String key, final byte[] contents) {
		MockFileItem fileItem = new MockFileItem();
		fileItem.set(contents);

		files.put(key, fileItem);
	}

	/**
	 * Retrieves a mock file for the given parameter name.
	 *
	 * @param key the parameter name
	 * @return the MockFileItem with the given key.
	 */
	@Override
	public FileItem getFileItem(final String key) {
		return getFiles().get(key);
	}

	/**
	 * Retrieves mock files for the given parameter name.
	 *
	 * @param key the parameter name
	 * @return the MockFileItems with the given key.
	 */
	@Override
	public FileItem[] getFileItems(final String key) {
		FileItem file = getFiles().get(key);
		if (file != null) {
			return new FileItem[]{(FileItem) file};
		}
		return null;
	}

	/**
	 * Retrieves the contents of a mock file for the given parameter name.
	 *
	 * @param key the parameter name
	 * @return FileContents from the MockFileItem with the given key.
	 */
	@Override
	public byte[] getFileContents(final String key) {
		return getFiles().get(key).get();
	}

	/**
	 * Clears all the parameter, file, and attribute values.
	 */
	public void clearRequest() {
		parameters.clear();
		files.clear();
		attributes.clear();
	}

	/**
	 * Clears the session attributes.
	 */
	public void clearSession() {
		sessionAttributes.clear();
	}

	/**
	 * Sets the roles which the user has for this request.
	 *
	 * @param userRoles the user roles to add.
	 */
	public void setUserRoles(final List<String> userRoles) {
		this.userRoles.clear();
		this.userRoles.addAll(userRoles);
	}

	//=== End   MockRequest specific convenience methods ===
	/**
	 * @return the parameter map.
	 */
	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * @return the file upload map.
	 */
	@Override
	public Map<String, FileItem> getFiles() {
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getAttribute(final String key) {
		return attributes.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttribute(final String key, final Serializable value) {
		attributes.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getSessionAttribute(final String key) {
		return sessionAttributes.get(key);
	}

	/**
	 * Note that this mock request just maps to the global session.
	 *
	 * @param key the attribute key
	 * @return the value of the session attribute with the given key
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
		sessionAttributes.put(key, value);
	}

	/**
	 * Note that this mock request just maps to the global session.
	 *
	 * @param key the session attribute key
	 * @param value the value of the session attribute
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
	 * Returns true if the user is in the given role.
	 *
	 * @param role a String specifying the name of the role.
	 * @return true if the user is in the given role, otherwise false.
	 */
	@Override
	public boolean isUserInRole(final String role) {
		return userRoles.contains(role);
	}

	/**
	 * @return the name of the HTTP method with which this request was made, for example, GET, POST, or PUT.
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * For a mock request, allow the method to be set.
	 *
	 * @param method the name of the HTTP method with which this request was made, for example, GET, POST, or PUT.
	 */
	public void setMethod(final String method) {
		this.method = method;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * Set the mock maxInactiveInterval.
	 *
	 * @param interval the period in seconds to set as the maxInactiveInterval
	 */
	public void setMaxInactiveInterval(final int interval) {
		this.maxInactiveInterval = interval;
	}
}
