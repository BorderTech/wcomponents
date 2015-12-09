package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;

/**
 * The interface that the Web framework needs to see from the HttpServletRequest. The support for "session scope"
 * variables should not be used except for communicating between separate WComponent trees (eg in separate servlets).
 * <p>
 * The {@link com.github.bordertech.wcomponents.servlet.WServlet} class is the generic servlet that sits between the
 * servlet container and the WComponent framework. The WServlet class is responsible for creating Request objects that
 * represent the incoming HTTP requests, and for dispatching them to the WComponent tree.
 *
 * @see com.github.bordertech.wcomponents.servlet.WServlet
 * @author James Gifford
 * @since 1.0.0
 */
public interface Request {
	// -------------------------------------
	// Request scope parameters.

	/**
	 * Returns the value of a request parameter as a <code>String</code>, or <code>null</code> if the parameter does not
	 * exist. Request parameters are extra information sent with the request. For HTTP servlets, parameters are
	 * contained in the query string or posted form data.
	 * <p>
	 * You should only use this method when you are sure the parameter has only one value. If the parameter might have
	 * more than one value, use {@link #getParameterValues}.
	 * <p>
	 * If you use this method with a multivalued parameter, the value returned is equal to the first value in the array
	 * returned by <code>getParameterValues</code>.
	 *
	 * @param key a <code>String</code> specifying the key/name of the parameter
	 * @return a <code>String</code> representing the single value of the parameter
	 * @see #getParameterValues
	 */
	String getParameter(String key);

	/**
	 * Returns an array of <code>String</code> objects containing all of the values the given request parameter has, or
	 * <code>null</code> if the parameter does not exist.
	 * <p>
	 * If the parameter has a single value, the array has a length of 1.
	 *
	 * @param key a <code>String</code> containing the key/name of the parameter whose value is requested
	 * @return an array of <code>String</code> objects containing the parameter's values
	 * @see #getParameter
	 */
	String[] getParameterValues(String key);

	/**
	 * If the request parameter is a file attachment, use this method to access the content of the attached file.
	 *
	 * @param key the name of the parameter used to pass the file content.
	 * @return the binary data for the uploaded file.
	 */
	byte[] getFileContents(String key);

	/**
	 * If the request parameter is a file attachement, use this method to access the parsed {@link FileItem}.
	 *
	 * @param key the name of the parameter used to pass the file content.
	 * @return {@link FileItem} representing the updloaded file.
	 * @since 1.0.0
	 * @deprecated As of 25/05/2015, replaced by {@link #getFileItems(java.lang.String)}
	 */
	FileItem getFileItem(String key);

	/**
	 * If the request parameter is a file attachement, use this method to access the parsed {@link FileItem[]}.
	 *
	 * @param key the name of the parameter used to pass the file content.
	 * @return {@link FileItem[]} representing the updloaded files.
	 * @since 1.0.0
	 */
	FileItem[] getFileItems(String key);

	/**
	 * @return an <code>Enumeration</code> of <code>String</code> objects containing the names of the parameters
	 * contained in this request. If the request has no parameters, the method returns an empty
	 * <code>Enumeration</code>.
	 */
	Enumeration getParameterNames();

	/**
	 * @return the complete list of parameters contained in this request. If the request contains no parameters, the
	 * method returns an empty <code>Map</code>.
	 */
	Map getParameters();

	/**
	 * Indicates whether the given request contains the same set of parameters as this one.
	 *
	 * @param other the request to check.
	 * @return true if the other request contains the same parameters as this one.
	 */
	boolean containsSameData(Request other);

	/**
	 * <p>
	 * Returns the value of the named attribute as an <code>Object</code>, or <code>null</code> if no attribute of the
	 * given name exists.</p>
	 *
	 * <p>
	 * Attributes can be set two ways. The container may set attributes to make available custom information about a
	 * request. Attributes can also be set programatically using setAttribute.</p>
	 *
	 * @param key the request attribute key
	 * @return an <code>Object</code> containing the value of the attribute, or <code>null</code> if the attribute does
	 * not exist
	 */
	Serializable getAttribute(String key);

	/**
	 * Stores an attribute in this request. Attributes are reset between requests. If the object passed in is null, the
	 * effect is the same as removing the attribute.
	 *
	 * @param key the attribute key
	 * @param value the attribute value
	 */
	void setAttribute(String key, Serializable value);

	// -------------------------------------
	// Session scope variables.
	/**
	 * Returns the object bound with the specified name in this session, or <code>null</code> if no object is bound
	 * under the name.
	 *
	 * @param key the session attribute key
	 * @return an <code>Object</code> containing the value of the attribute, or <code>null</code> if the attribute does
	 * not exist
	 */
	Serializable getSessionAttribute(String key);

	/**
	 * Returns the object bound with the specified name in the application session, or <code>null</code> if no object is
	 * bound under the name.
	 * <p>
	 * This method is relevant for portlets in which case getAppSessionAttribute accesses a portlet scoped session,
	 * while getSessionAttribute accesses the global session.
	 *
	 * @param key the session attribute key
	 * @return an <code>Object</code> containing the value of the attribute, or <code>null</code> if the attribute does
	 * not exist
	 */
	Serializable getAppSessionAttribute(String key);

	/**
	 * Binds an object to this session, using the name specified. If an object of the same name is already bound to the
	 * session, the object is replaced.
	 * <p>
	 * If the value passed in is null, this has the same effect as removing the attribute.
	 *
	 * @param key the session attribute key.
	 * @param value an <code>Object</code> containing the value of the attribute.
	 */
	void setSessionAttribute(String key, Serializable value);

	/**
	 * Binds an object to the application session, using the name specified. If an object of the same name is already
	 * bound to the session, the object is replaced.
	 * <p>
	 * If the value passed in is null, this has the same effect as removing the attribute.
	 * <p>
	 * This method is relevant for portlets in which case getAppSessionAttribute accesses a portlet scoped session,
	 * while getSessionAttribute accesses the global session.
	 *
	 * @param key the session attribute key.
	 * @param value an <code>Object</code> containing the value of the attribute.
	 */
	void setAppSessionAttribute(String key, Serializable value);

	/**
	 * Gets the parameter for the given key. If no such parameter is defined, returns null. This method differs from
	 * Parameters.getInstance().get(key) in that the value returned may be customised to suit the "app". In a portlet
	 * environment an "app" is represented by a portlet.
	 *
	 * @param key the preference paramter key.
	 * @return the app preference paramter, or null if not found.
	 */
	String getAppPreferenceParameter(String key);

	// -------------------------------------
	// Interface for communicating to the servlet/portlet
	/**
	 * Signal to the servlet that we want to log out (terminate the session). The container should react by invalidating
	 * the session and redirecting the client to some suitable page.
	 *
	 * @see com.github.bordertech.wcomponents.container.AbstractContainerHelper#redirectForLogout()
	 */
	void logout();

	/**
	 * @return true if {@link #logout} has been called
	 */
	boolean isLogout();

	/**
	 * Triggers the publishing event using the <code>value</code> as the event payload.
	 *
	 * @param action name of the publishing event to trigger
	 * @param parameter the key for the event payload value
	 * @param value the value of the event payload
	 * @since 1.0.0
	 * @deprecated portal specific
	 */
	void setEvent(String action, String parameter, Serializable value);

	/**
	 * Triggers the publishing event using the <code>eventMap</code> as the event payload.
	 *
	 * @param action name of the publishing event to trigger
	 * @param eventMap the key/value pair for the event payload
	 * @since 1.0.0
	 * @deprecated portal specific
	 */
	void setEvent(final String action, final HashMap<String, Serializable> eventMap);

	/**
	 * Support for Public Render Parameters in Portal. In a Servlet environment, this will be the same as the session.
	 *
	 * @param key The key for the parameter.
	 * @param value The value of the parameter.
	 * @since 1.0.0
	 * @deprecated portal specific. user {@link #setSessionAttribute(String, Serializable)}
	 */
	void setRenderParameter(String key, Serializable value);

	/**
	 * Support for Public Render Parameters in Portal. In a Servlet environment, this will be the same as the session.
	 *
	 * @param key The key for the parameter.
	 * @return The value of the parameter.
	 * @since 1.0.0
	 * @deprecated portal specific. user {@link #getSessionAttribute(String)}
	 */
	Serializable getRenderParameter(String key);

	/**
	 * Returns a boolean indicating whether the authenticated user is included in the specified logical "role". Roles
	 * and role membership can be defined using deployment descriptors. If the user has not been authenticated, the
	 * method returns false.
	 *
	 * @param role a String specifying the name of the role.
	 * @return a boolean indicating whether the user making this request belongs to a given role; false if the user has
	 * not been authenticated.
	 * @since 1.0.0
	 */
	boolean isUserInRole(String role);

	/**
	 * Returns the name of the HTTP method with which this request was made, for example, GET, POST, or PUT. Same as the
	 * value of the CGI variable REQUEST_METHOD.
	 *
	 * @return a <code>String</code> specifying the name of the method with which this request was made
	 */
	String getMethod();

	/**
	 * Returns the maximum time interval, in seconds, that the servlet container will keep this session open between
	 * client accesses.
	 *
	 * @return an int specifying the maximum inactive interval in seconds. Negative if the session never expires.
	 */
	int getMaxInactiveInterval();
}
