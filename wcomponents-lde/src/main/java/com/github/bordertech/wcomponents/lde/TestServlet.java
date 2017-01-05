package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Enables WComponents to be run in a LDE environment.
 *
 * @author James Gifford
 * @since 1.0.0
 */
public abstract class TestServlet extends WServlet implements LdeLauncher {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TestServlet.class);

	private final List<Resource> union = new ArrayList<>();

	/**
	 * The Jetty server instance for this VM. Only one LDE can be run per VM.
	 */
	private static Server server;

	/**
	 * The URL where this servlet can be accessed from.
	 */
	private String url;

	/**
	 * Runs the server.
	 *
	 * @throws Exception if the LDE fails to start.
	 */
	@Override
	public void run() throws Exception {
		synchronized (TestServlet.class) {
			if (server != null) {
				stop();
			}

			server = new Server();
		}

		SocketConnector connector = new SocketConnector();
		connector.setMaxIdleTime(0);
		connector.setPort(getLdePort());
		server.addConnector(connector);

		WebAppContext webapp = createWebApp(server);

		try {
			server.start();
		} catch (BindException e) {
			if (isShutdownEnabled()) {
				// The port is in use, possibly by another LDE instance.
				// Attempt to shut down the other LDE and start up again.
				LOG.info("Attempting remote shutdown of existing LDE");
				shutDown();

				Thread.sleep(100); // give the OS a chance to release the port
				server.start();
			} else {
				throw e;
			}
		} catch (Exception e) {
			// Failed to start the server
			server = null;
			throw e;
		}

		// We have to set the timeout after the server is started, as Jetty reads
		// the value from webdefault.xml during start-up.
		int timeout = ConfigurationProperties.getLdeServerSessionTimeout();

		if (timeout > 0) {
			webapp.getSessionHandler().getSessionManager().setMaxInactiveInterval(timeout);
		}

		// Server started successfully, log the URL for the LDE.
		url = "http://localhost:" + connector.getLocalPort() + "/app";
		LOG.info("URL  ==>  " + url);
	}

	@Override
	public void service(final ServletRequest req, final ServletResponse res) throws ServletException,
			IOException {
		if (req.getParameter("lde.shutdown") != null && isShutdownEnabled()) {
			LOG.info("Received LDE shutdown request, stopping server.");
			res.getOutputStream().close();

			try {
				stop();
			} catch (InterruptedException e) {
				LOG.error("Failed to shut down LDE server", e);
			}

			return;
		}

		super.service(req, res);
	}

	/**
	 * Retrieves the port number which the LDE should run on.
	 *
	 * @return the LDE port.
	 */
	private static int getLdePort() {

		return ConfigurationProperties.getLdeServerPort();
	}

	/**
	 * Attempts to shutdown an existing TestServlet, possibly in another VM.
	 */
	protected void shutDown() {
		try {
			URL shutdownUrl = new URL("http://localhost:" + getLdePort()
					+ "/app?lde.shutdown=true");

			HttpURLConnection conn = (HttpURLConnection) shutdownUrl.openConnection();
			conn.getResponseCode();
		} catch (ConnectException expected) {
			// This will be thrown if either the other LDE is not running,
			// or it was running and this connection has caused it to terminate.
			return;
		} catch (Exception e) {
			LOG.error("Failed to shut down other LDE instance", e);
		}
	}

	/**
	 * Creates the Web app context to use in the LDE. The context will be registered with the given server.
	 *
	 * @param srv the Jetty server.
	 * @return the newly created Web app context.
	 * @throws Exception an exception
	 */
	protected WebAppContext createWebApp(final Server srv) throws Exception {
		String[] webdocs = getWebdocsDir();
		String[] themeWebdocs = getThemeWebdocs();
		String[] resourceDirs = getResourceDir();

		if (webdocs != null) {
			for (int i = 0; i < webdocs.length; i++) {
				union.add(Resource.newResource(webdocs[i]));
			}
		}

		if (themeWebdocs != null) {
			for (int i = 0; i < themeWebdocs.length; i++) {
				union.add(Resource.newResource(themeWebdocs[i]));
			}
		}

		if (resourceDirs != null) {
			for (int i = 0; i < resourceDirs.length; i++) {
				union.add(Resource.newResource(resourceDirs[i]));
			}
		}

		HandlerCollection handlers = new HandlerCollection();
		WebAppContext webapp = null;

		// If there is no external web.xml override, register the default servlets
		if (webdocs == null) {
			webapp = new WebAppContext();
			webapp.setContextPath("/");
			registerServlets(webapp);
		} else {
			webapp = new WebAppContext(webdocs[0], "/");
		}

		// Must have at least one resource
		if (union.isEmpty()) {
			webapp.setResourceBase(".");
		} else {
			webapp.setBaseResource(
					new ResourceCollection(union.toArray(new Resource[union.size()])));
		}

		webapp.addServlet(new ServletHolder(this), "/app/*");

		// This is required if projects define their own web.xml,
		// we still need to serve up the theme from inside a jar
		// file using the theme servlet.
		if (themeWebdocs == null) {
			WebAppContext themeWebapp = new WebAppContext();
			themeWebapp.setContextPath("/theme");
			themeWebapp.addServlet("com.github.bordertech.wcomponents.servlet.ThemeServlet", "/*");
			themeWebapp.setResourceBase(".");
			handlers.addHandler(themeWebapp);
		} else {
			WebAppContext themeWebapp = new WebAppContext();
			themeWebapp.setContextPath("/theme");
			themeWebapp.setResourceBase(themeWebdocs[0]);
			handlers.addHandler(themeWebapp);
		}

		// Initialise security
		String realmFile = ConfigurationProperties.getLdeServerJettyRealmFile();

		if (realmFile != null) {
			HashLoginService loginService = new HashLoginService("LdeRunner", realmFile);
			webapp.getSecurityHandler().setLoginService(loginService);
		}

		handlers.addHandler(webapp);
		srv.setHandler(handlers);

		return webapp;
	}

	/**
	 * Stops the server.
	 *
	 * @throws java.lang.InterruptedException an interrupted exception
	 */
	@Override
	public void stop() throws InterruptedException {
		synchronized (TestServlet.class) {
			if (server != null) {
				try {
					server.stop();
				} catch (Exception e) {
					LOG.warn("Failed to stop server", e);
				}

				server = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return server != null && server.isRunning();
	}

	/**
	 * @return the URL where this servlet can be accessed from.
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/**
	 * @param jarname the jar name to add
	 */
	public void addIndirectJar(final String jarname) {

		try (JarFile jarfile = new JarFile(jarname)) {
			Manifest man = jarfile.getManifest();
			Attributes atts = man.getMainAttributes();
			String jarlist = atts.getValue("Class-Path");
			StringTokenizer tokenizer = new StringTokenizer(jarlist, " ", false);
			while (tokenizer.hasMoreTokens()) {
				String tokenUrl = tokenizer.nextToken();
				union.add(Resource.newResource("jar:" + tokenUrl + "!/"));
				LOG.info("Added webdocs at " + tokenUrl);
			}
		} catch (IOException ex) {
			throw new SystemException("Could handle indirect jar " + jarname,
					ex);
		}
	}

	/**
	 * @return the webdocs directory, or null if not defined.
	 */
	protected String[] getWebdocsDir() {
		String[] docs = ConfigurationProperties.getLdeServerWebDocsDir();
		return docs == null || docs.length == 0 ? null : docs;
	}

	/**
	 * @return the theme webdocs directory, or null if not defined.
	 */
	protected String[] getThemeWebdocs() {
		String[] docs = ConfigurationProperties.getLdeServerWebDocsThemeDir();
		return docs == null || docs.length == 0 ? null : docs;
	}

	/**
	 * @return the resource directory, or null if not defined.
	 */
	protected String[] getResourceDir() {
		String[] docs = ConfigurationProperties.getLdeServerWebDocsResourcesDir();
		return docs == null || docs.length == 0 ? null : docs;
	}

	/**
	 * Override service in order to support persistant sessions.
	 *
	 * @param request the request being processed
	 * @param response the response
	 * @throws javax.servlet.ServletException a servlet exception
	 * @throws java.io.IOException an IO exception
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// This is a new session, do we want to load a persisted session?
		if (request.getSession(false) == null && LdeSessionUtil.isLoadPersistedSessionEnabled()) {
			getUI(request); // need to ensure that the UI has been loaded
			LdeSessionUtil.deserializeSessionAttributes(request.getSession(true));
		}

		super.service(request, response);

		// Persist the session if necessary.
		if (LdeSessionUtil.isPersistSessionEnabled()) {
			LdeSessionUtil.serializeSessionAttributes(request.getSession());
		}
	}

	/**
	 * Subclasses may override this to register additional servlets with the server.
	 *
	 * @param webapp the webapp to register the servlets with.
	 */
	protected void registerServlets(final WebAppContext webapp) {
		//webapp.addServlet("themeServlet", "/theme/*",
		// "com.github.bordertech.wcomponents.lde.LdeThemeServlet");
	}

	/**
	 * Indicates whether remote LDE shutdown is enabled.
	 *
	 * @return true if shutdown is enabled, false otherwise.
	 */
	protected boolean isShutdownEnabled() {
		return ConfigurationProperties.getLdeServerEnableShutdown();
	}
}
