package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.AbstractRequest;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * An implementation of {@link Request} using HttpServletRequests.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public class ServletRequest extends AbstractRequest {

	/**
	 * The backing servlet request.
	 */
	private final HttpServletRequest backing;

	private final Map parameters = new HashMap();
	private final Map files = new HashMap();

	/**
	 * Creates a ServletRequest.
	 *
	 * @param aBacking the backing servlet request.
	 */
	public ServletRequest(final HttpServletRequest aBacking) {
		backing = aBacking;

		// Take a copy of the parameter map now, so that we don't depend on the request being stable.
		getParameterMap(aBacking);
	}

	@Override
	public Map getParameters() {
		return parameters;
	}

	@Override
	public Map getFiles() {
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
	 * @param backingReq the backing request
	 */
	private void getParameterMap(final HttpServletRequest backingReq) {
		parameters.clear();
		files.clear();

		String contentType = backingReq.getContentType();

		// Can't use the HttpServletRequest.getParameterMap because it's not in Servlet 2.2
		boolean isMultipart = (contentType != null && contentType.toLowerCase().startsWith(
				"multipart/form-data"));

		if (!isMultipart) {
			for (Enumeration en = backingReq.getParameterNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				String[] values = backingReq.getParameterValues(key);

				if (values != null) {
					if (values.length == 1) {
						parameters.put(key, values[0]);
					} else {
						parameters.put(key, values);
					}
				}
			}
		} else {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setFileItemFactory(new DiskFileItemFactory());

			try {
				List fileItems = upload.parseRequest(backingReq);

				uploadFileItems(fileItems, parameters, files);
			} catch (FileUploadException ex) {
				throw new SystemException(ex);
			}
			// Include Query String Parameters (only if parameters were not included in the form fields)
			for (Enumeration en = backingReq.getParameterNames(); en.hasMoreElements();) {
				String key = (String) en.nextElement();
				String[] values = backingReq.getParameterValues(key);
				if (values != null && !parameters.containsKey(key)) {
					if (values.length == 1) {
						parameters.put(key, values[0]);
					} else {
						parameters.put(key, values);
					}
				}
			}
		}
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
