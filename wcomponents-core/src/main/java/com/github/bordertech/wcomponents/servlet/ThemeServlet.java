package com.github.bordertech.wcomponents.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * This servlet is designed to serve up theme files from within the WComponent project or jar file. This saves
 * developers from having to configure where the webdocs directory is, or extract the theme into their WAR file.
 * </p>
 * <p>
 * The ThemeServlet expects all static resources to be available in the classpath under <code>/theme/XXXXXX/</code>.
 * Where "XXXXX" is the value of the configuration parameter "<code>theme.name</code>".
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated no longer required. Theme is now available via WServlet.The bordertech.wcomponents.theme.content.path
 * parameter im wcomponents-app.properties must not be set and there must be servlet-mappings to the
 * url-patterns "/{my-mapping}"  and "/{my-mapping}/*" where you specify the value of {my-mapping}.
 */
@Deprecated
public class ThemeServlet extends HttpServlet {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws
			ServletException,
			IOException {
		doGet(req, resp);
	}

	/**
	 * Serves up a file from the theme.
	 *
	 * @param req the request with the file name in parameter "f", or following the servlet path.
	 * @param resp the response to write to.
	 * @throws ServletException on error.
	 * @throws IOException if there is an error reading the file / writing the response.
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws
			ServletException,
			IOException {
		ServletUtil.handleThemeResourceRequest(req, resp);
	}
}
