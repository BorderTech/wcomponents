package com.github.bordertech.wcomponents.lde.examples;

import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.servlet.ThemeServlet;
import com.github.bordertech.wcomponents.servlet.WServlet;
import java.util.Date;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * <p>
 * This class demonstrates how to run a WComponent from a Servlet. Simply extend WServlet and implement the method
 * getUI() to return a WComponent. In this case we return a really simple WComponent called a WLabel. Of course you
 * could return a complex component that represents an entire web application.</p>
 *
 * <p>
 * To see it work, run this class (it has a main), start a browser and enter the url "http://localhost:8080/".</p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class HelloWServlet extends WServlet {

	/**
	 * @param httpServletRequest the request being responded to.
	 * @return the UI for this WServlet.
	 */
	@Override
	public WComponent getUI(final Object httpServletRequest) {
		return new WText("Hi there from Servlet land.  [" + new Date() + "]");
	}

	/**
	 * This main method exists to make it easy to run this servlet without having to create a web.xml file, build a war
	 * and deploy it.
	 *
	 * @param args command-line arguments, ignored.
	 * @throws Exception Thrown if a problem occurs.
	 */
	public static void main(final String[] args)
			throws Exception {
		// Use jetty to run the servlet.
		Server server = new Server();

		SocketConnector connector = new SocketConnector();
		connector.setMaxIdleTime(0);
		connector.setPort(8080);
		server.addConnector(connector);

		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.addServlet(HelloWServlet.class.getName(), "/*");
		context.addServlet(ThemeServlet.class.getName(), "/theme/*");
		context.setResourceBase(".");

		server.setHandler(context);
		server.start();
	}
}
