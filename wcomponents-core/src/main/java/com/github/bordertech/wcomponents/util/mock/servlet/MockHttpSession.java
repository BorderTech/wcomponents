package com.github.bordertech.wcomponents.util.mock.servlet;

import com.github.bordertech.wcomponents.util.Enumerator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * A mock HttpSsession, useful for unit testing.
 *
 * Extracted from PortalBridge_Test
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockHttpSession implements HttpSession {

	private final Map attributes = new HashMap();
	private int maxInactiveInterval;
	private boolean invalidated;

	/**
	 * Returns the value of the attribute with the specified name.
	 *
	 * @param name the attribute name.
	 * @return the attribute value, or null if the attribute does not exist.
	 */
	@Override
	public Object getAttribute(final String name) {
		return attributes.get(name);
	}

	/**
	 * @return an enumeration of the attribute names.
	 */
	@Override
	public Enumeration getAttributeNames() {
		return getKeys(attributes);
	}

	/**
	 * Returns the session creation timestamp.
	 *
	 * @return 0, as this is not implemented.
	 */
	@Override
	public long getCreationTime() {
		return 0;
	}

	/**
	 * Returns the session id.
	 *
	 * @return 0, as this is not implemented.
	 */
	@Override
	public String getId() {
		return null;
	}

	/**
	 * Returns the session last accessed timestamp.
	 *
	 * @return 0, as this is not implemented.
	 */
	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	/**
	 * @return the session maximum inactive interval.
	 */
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * Returns the session servlet context.
	 *
	 * @return null, as this is not implemented.
	 */
	@Override
	public ServletContext getServletContext() {
		return null;
	}

	/**
	 * @deprecated @return null, as this is not implemented.
	 */
	@Deprecated
	@Override
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return null;
	}

	/**
	 * @param arg0 ignored.
	 * @return null, as this is not implemented.
	 */
	@Override
	public Object getValue(final String arg0) {
		return null;
	}

	/**
	 * @return null, as this is not implemented.
	 */
	@Override
	public String[] getValueNames() {
		return null;
	}

	/**
	 * Invalidates the session.
	 */
	@Override
	public void invalidate() {
		invalidated = true;
	}

	/**
	 * @return true if {@link #invalidate()} has been called.
	 */
	public boolean isInvalidated() {
		return invalidated;
	}

	/**
	 * Returns whether the session is new.
	 *
	 * @return false, as this is not implemented.
	 */
	@Override
	public boolean isNew() {
		return false;
	}

	/**
	 * No effect.
	 *
	 * @param arg0 ignored.
	 * @param arg1 ignored.
	 */
	@Override
	public void putValue(final String arg0, final Object arg1) {
		// No effect
	}

	/**
	 * Removes the specified attribute.
	 *
	 * @param name the attribute name.
	 */
	@Override
	public void removeAttribute(final String name) {
		attributes.remove(name);
	}

	/**
	 * No effect.
	 *
	 * @param arg0 ignored.
	 */
	@Override
	public void removeValue(final String arg0) {
		// No effect
	}

	/**
	 * Sets the specified attribute.
	 *
	 * @param name the attribute name.
	 * @param value the attribute value.
	 */
	@Override
	public void setAttribute(final String name, final Object value) {
		attributes.put(name, value);
	}

	/**
	 * Sets the maximum inactive interval.
	 *
	 * @param maxInactiveInterval the maximum inactive interval.
	 */
	@Override
	public void setMaxInactiveInterval(final int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	/**
	 * @return the attributes map.
	 */
	public Map getAttributes() {
		return attributes;
	}

	/**
	 * Returns an enumeration of the keys in the specified map.
	 *
	 * @param map the map to enumerate.
	 * @return an enumeration of the map keys.
	 */
	private Enumeration getKeys(final Map map) {
		return new Enumerator(map.keySet().iterator());
	}
}
