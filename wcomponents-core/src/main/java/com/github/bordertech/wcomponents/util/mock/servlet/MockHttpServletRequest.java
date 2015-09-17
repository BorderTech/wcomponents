package com.github.bordertech.wcomponents.util.mock.servlet;

import com.github.bordertech.wcomponents.util.Enumerator;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A mock HTTP Servlet request, useful for unit testing.
 *
 * Extracted from PortalBridge_Test
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockHttpServletRequest implements HttpServletRequest {

	private HttpSession session;
	private final Map parameters = new HashMap();
	private final Map attributes = new HashMap();
	private final Map headers = new HashMap();
	private int localPort;
	private int remotePort;
	private boolean secure;
	private byte[] content;
	private String contentType;
	private String requestURI;
	private String scheme;
	private String serverName;
	private int serverPort;
	private String pathInfo;
	private String method;

	/**
	 * Creates a MockHttpServletRequest without a session.
	 */
	public MockHttpServletRequest() {
		// No session
	}

	/**
	 * Creates a MockHttpServletRequest with the given session.
	 *
	 * @param session the current session.
	 */
	public MockHttpServletRequest(final HttpSession session) {
		this.session = session;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getAuthType() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getContextPath() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public Cookie[] getCookies() {
		return null;
	}

	/**
	 * Unimplemented.
	 *
	 * @param name ignored.
	 * @return null.
	 */
	@Override
	public long getDateHeader(final String name) {
		return 0;
	}

	/**
	 * Returns the value of the given header.
	 *
	 * @param name the header name.
	 * @return the value of the given header, or null if no header exists with that name.
	 */
	@Override
	public String getHeader(final String name) {
		return (String) headers.get(name);
	}

	/**
	 * Returns the value of the given header.
	 *
	 * @param name the header name.
	 * @param value the header value
	 */
	public void setHeader(final String name, final String value) {
		headers.put(name, value);
	}

	/**
	 * @return an enumeration of the header names.
	 */
	@Override
	public Enumeration getHeaderNames() {
		return new Enumerator(headers.keySet().iterator());
	}

	/**
	 * Unimplemented.
	 *
	 * @param name ignored.
	 * @return null.
	 */
	@Override
	public Enumeration getHeaders(final String name) {
		return null;
	}

	/**
	 * Unimplemented.
	 *
	 * @param name ignored.
	 * @return 0.
	 */
	@Override
	public int getIntHeader(final String name) {
		return 0;
	}

	/**
	 * @return the request method, e.g. "GET" or "POST".
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the request method, e.g. "GET" or "POST".
	 *
	 * @param method the request method.
	 */
	public void setMethod(final String method) {
		this.method = method;
	}

	/**
	 * @return the path info.
	 */
	@Override
	public String getPathInfo() {
		return pathInfo;
	}

	/**
	 * Sets the path info.
	 *
	 * @param pathInfo the path info.
	 */
	public void setPathInfo(final String pathInfo) {
		this.pathInfo = pathInfo;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getPathTranslated() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getQueryString() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getRemoteUser() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getRequestedSessionId() {
		return null;
	}

	/**
	 * Sets the request URI.
	 *
	 * @param requestURI the request URI.
	 */
	public void setRequestURI(final String requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * @return the request URI.
	 */
	@Override
	public String getRequestURI() {
		return requestURI;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public String getServletPath() {
		return null;
	}

	/**
	 * @return the current HttpSession, may be null.
	 */
	@Override
	public HttpSession getSession() {
		return session;
	}

	/**
	 * Returns the current HttpSession, or creates a new one if there is no current session and the <code>create</code>
	 * flag is set.
	 *
	 * @param create if true, creates a new session if one does not exist.
	 * @return the current HttpSession, may be null if the create flag is false.
	 */
	@Override
	public HttpSession getSession(final boolean create) {
		if (create && session == null) {
			session = new MockHttpSession();
		}

		return session;
	}

	/**
	 * @return null, as this is unimplemented.
	 */
	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	/**
	 * @return false, as this is unimplemented.
	 */
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	/**
	 * @return false, as this is unimplemented.
	 */
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/**
	 * @return false, as this is unimplemented.
	 */
	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	/**
	 * @return false, as this is unimplemented.
	 */
	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	/**
	 * Unimplemented.
	 *
	 * @param arg0 ignored.
	 * @return false, as this is unimplemented.
	 */
	@Override
	public boolean isUserInRole(final String arg0) {
		return false;
	}

	/**
	 * Returns the value of the attribute with the specified name.
	 *
	 * @param name the attribute name.
	 * @return the attribute value, or null if it has not been defined.
	 */
	@Override
	public Object getAttribute(final String name) {
		return attributes.get(name);
	}

	/**
	 * @return an enumeration of the defined attribute names.
	 */
	@Override
	public Enumeration getAttributeNames() {
		return getKeys(attributes);
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getCharacterEncoding() {
		return null;
	}

	/**
	 * Sets the binary content of the request.
	 *
	 * @param content the request content.
	 */
	public void setContent(final byte[] content) {
		this.content = content;
	}

	/**
	 * @return the request content length.
	 */
	@Override
	public int getContentLength() {
		return content == null ? 0 : content.length;
	}

	/**
	 * @return the request content type.
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the request content type.
	 *
	 * @param contentType the content type.
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Returns a stream to read the request content. If the content has not been set previously by
	 * {@link #setContent(byte[])}, an empty input stream is returned.
	 *
	 * @return the request input stream.
	 */
	@Override
	public ServletInputStream getInputStream() {
		byte[] data = content == null ? new byte[0] : content;

		return new MockServletInputStream(new ByteArrayInputStream(data));
	}

	/**
	 * @return the default Locale.
	 */
	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

	/**
	 * @return an enumeration of the available locales.
	 */
	@Override
	public Enumeration getLocales() {
		List locales = Arrays.asList(Locale.getAvailableLocales());
		return new Enumerator(locales.iterator());
	}

	/**
	 * Returns the value of the parameter with the specified name.
	 *
	 * @see #setParameter(String, String)
	 *
	 * @param name the parameter name
	 * @return the value of the given parameter, or null if it does not exist.
	 */
	@Override
	public String getParameter(final String name) {
		String[] values = (String[]) parameters.get(name);

		if (values == null || values.length == 0) {
			return null;
		}

		return values[0];
	}

	/**
	 * @return the parameter map
	 */
	@Override
	public Map getParameterMap() {
		return parameters;
	}

	/**
	 * @return an enumeration of the parameter names
	 */
	@Override
	public Enumeration getParameterNames() {
		return getKeys(parameters);
	}

	/**
	 * Returns an enumeration of the keys in the given map.
	 *
	 * @param map the map
	 * @return an enumeration of the map's keys
	 */
	private Enumeration getKeys(final Map map) {
		return new Enumerator(map.keySet().iterator());
	}

	/**
	 * Returns the parameter values for the given parameter.
	 *
	 * @param name the parameter name
	 * @return the values of the given parameter, or null if the parameter does not exist.
	 */
	@Override
	public String[] getParameterValues(final String name) {
		return (String[]) parameters.get(name);
	}

	/**
	 * Sets a parameter. If the parameter already exists, another value will be added to the parameter values.
	 *
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public void setParameter(final String name, final String value) {
		String[] currentValue = (String[]) parameters.get(name);

		if (currentValue == null) {
			currentValue = new String[]{value};
		} else {
			// convert the current values into a new array..
			String[] newValues = new String[currentValue.length + 1];
			System.arraycopy(currentValue, 0, newValues, 0, currentValue.length);
			newValues[newValues.length - 1] = value;
			currentValue = newValues;
		}

		parameters.put(name, currentValue);
	}

	/**
	 * Removes the parameter with the given name.
	 *
	 * @param name the name of the parameter to remove.
	 */
	public void removeParameter(final String name) {
		parameters.remove(name);
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getProtocol() {
		return null;
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public BufferedReader getReader() {
		return null;
	}

	/**
	 * Unimplemented.
	 *
	 * @param path ignored.
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getRealPath(final String path) {
		return null;
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getRemoteAddr() {
		return null;
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getRemoteHost() {
		return null;
	}

	/**
	 * Unimplemented.
	 *
	 * @param path ignored.
	 * @return null, as this has not been implemented.
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(final String path) {
		return null;
	}

	/**
	 * @return the URI scheme.
	 */
	@Override
	public String getScheme() {
		return scheme;
	}

	/**
	 * @return the server name.
	 */
	@Override
	public String getServerName() {
		return serverName;
	}

	/**
	 * @return the server port.
	 */
	@Override
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param scheme The scheme to set.
	 */
	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

	/**
	 * @param serverName The serverName to set.
	 */
	public void setServerName(final String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @param serverPort The serverPort to set.
	 */
	public void setServerPort(final int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * Sets the secure flag.
	 *
	 * @param secure the secure flag.
	 */
	public void setSecure(final boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return the secure flag.
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Removes the attribute with the given name.
	 *
	 * @param name the attribute name.
	 */
	@Override
	public void removeAttribute(final String name) {
		attributes.remove(name);
	}

	/**
	 * Sets a request attribute.
	 *
	 * @param name the attribute name.
	 * @param value the attribute value.
	 */
	@Override
	public void setAttribute(final String name, final Object value) {
		if (value == null) {
			removeAttribute(name);
		} else {
			attributes.put(name, value);
		}
	}

	/**
	 * Unimplemented.
	 *
	 * @param env ignored.
	 */
	@Override
	public void setCharacterEncoding(final String env) {
		// No effect
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getLocalAddr() {
		return null;
	}

	/**
	 * @return null, as this has not been implemented.
	 */
	@Override
	public String getLocalName() {
		return null;
	}

	/**
	 * Sets the local port number.
	 *
	 * @param localPort the local port number.
	 */
	public void setLocalPort(final int localPort) {
		this.localPort = localPort;
	}

	/**
	 * @return the local port number.
	 */
	@Override
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * Sets the remove port number.
	 *
	 * @param remotePort the remote port number.
	 */
	public void setRemotePort(final int remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * @return the remote port number.
	 */
	@Override
	public int getRemotePort() {
		return remotePort;
	}
}
