package com.github.bordertech.wcomponents.util.mock.servlet;

import com.github.bordertech.wcomponents.util.Enumerator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * MockServletConfig - mock servlet config for unit testing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class MockServletConfig implements ServletConfig {

	private String servletName;
	private final Map initParameters = new HashMap();

	/**
	 * Sets an init parameter value.
	 *
	 * @param name the name of the parameter.
	 * @param value the value of the parameter.
	 */
	public void setInitParameter(final String name, final String value) {
		initParameters.put(name, value);
	}

	/**
	 * Returns the value of the init parameter with the specified name.
	 *
	 * @param name the name of the parameter to read.
	 * @return the init parameter value, or null if not set.
	 */
	@Override
	public String getInitParameter(final String name) {
		return (String) initParameters.get(name);
	}

	/**
	 * @return an enumeration of the parameter names
	 */
	@Override
	public Enumeration getInitParameterNames() {
		return new Enumerator(initParameters.keySet().iterator());
	}

	/**
	 * @return null, as this is not implemented
	 */
	@Override
	public ServletContext getServletContext() {
		return null;
	}

	/**
	 * @return the servlet name
	 */
	@Override
	public String getServletName() {
		return servletName;
	}

	/**
	 * Sets the servlet name.
	 *
	 * @param servletName the servlet name to set.
	 */
	public void setServletName(final String servletName) {
		this.servletName = servletName;
	}
}
