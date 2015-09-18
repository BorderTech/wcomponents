package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.container.PageShellInterceptor;
import com.github.bordertech.wcomponents.monitor.ProfileContainer;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class enables easy running of a shared WComponent instance in a Jetty servlet container for development and
 * testing purposes.
 * <p>
 * You need to set the class name of the WComponent you want to run. Do this by setting the parameter
 * "bordertech.wcomponents.lde.component.to.launch" in your "local_app.properties" file. E.g.
 *
 * <pre>
 * ui.web.component.to.launch = com.github.bordertech.wcomponents.examples.picker.ExamplePicker
 * </pre>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class PlainLauncher extends TestServlet {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(PlainLauncher.class);

	/**
	 * The {@link Config configuration} property key for which component to launch.
	 */
	public static final String COMPONENT_TO_LAUNCH_PARAM_KEY = "bordertech.wcomponents.lde.component.to.launch";

	/**
	 * The {@link Config configuration} property key for whether to display the memory profile.
	 */
	protected static final String SHOW_MEMORY_PROFILE_PARAM_KEY = "bordertech.wcomponents.lde.show.memory.profile";

	/**
	 * The singleton instance of the UI which is being run by the PlainLauncher.
	 */
	private WApplication sharedUI;

	/**
	 * The fully qualified name of the WComponent class which is being served as the UI.
	 */
	private String uiClassName;

	/**
	 * The dev toolkit instance for this LDE.
	 */
	private static final DevToolkit TOOLKIT = new DevToolkit();

	/**
	 * This method has been overridden to load a WComponent from parameters.
	 *
	 * @param httpServletRequest the servlet request being handled.
	 * @return the top-level WComponent for this servlet.
	 */
	@Override
	public synchronized WComponent getUI(final Object httpServletRequest) {
		String configuredUIClassName = Config.getInstance()
				.getString(COMPONENT_TO_LAUNCH_PARAM_KEY);

		if (sharedUI == null || !Util.equals(configuredUIClassName, uiClassName)) {
			uiClassName = configuredUIClassName;
			WComponent ui = createUI();

			if (ui instanceof WApplication) {
				sharedUI = (WApplication) ui;
			} else {
				LOG.warn(
						"Top-level component should be a WApplication."
						+ " Creating WApplication wrapper...");

				sharedUI = new WApplication();
				ui.setLocked(false);
				sharedUI.add(ui);
				sharedUI.setLocked(true);
			}

			if (Config.getInstance().getBoolean(SHOW_MEMORY_PROFILE_PARAM_KEY, false)) {
				ProfileContainer profiler = new ProfileContainer();

				sharedUI.setLocked(false);
				sharedUI.add(profiler);
				sharedUI.setLocked(true);
			}
		}

		return sharedUI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// The toolkit must access the request before any WComponent processing occurs
		TOOLKIT.serviceRequest(request);
		super.service(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InterceptorComponent createInterceptorChain(final Object request) {
		InterceptorComponent chain = super.createInterceptorChain(request);

		// The toolkit must render itself within the page shell
		// and wrap the main WComponent output.
		InterceptorComponent.replaceInterceptor(PageShellInterceptor.class,
				new PageShellInterceptor() {
			@Override
			protected void beforePaint(final PrintWriter writer) {
				super.beforePaint(writer);
				TOOLKIT.paintHeader(writer);
			}

			@Override
			protected void afterPaint(final PrintWriter writer) {
				TOOLKIT.paintFooter(writer);
				super.afterPaint(writer);
			}
		}, chain);

		return chain;
	}

	/**
	 * Creates the UI which the launcher displays. If there is misconfiguration or error, a UI containing an error
	 * message is returned.
	 *
	 * @return the UI which the launcher displays.
	 */
	protected WComponent createUI() {
		// Check if the parameter COMPONENT_TO_LAUNCH_PARAM_KEY has been
		// configured with the name of a component to launch.

		WComponent sharedApp = null;

		Configuration config = Config.getInstance();
		uiClassName = config.getString(COMPONENT_TO_LAUNCH_PARAM_KEY);

		if (uiClassName == null) {
			sharedApp = new WText(
					"You need to set the class name of the WComponent you want to run.<br />"
					+ "Do this by setting the parameter \""
					+ COMPONENT_TO_LAUNCH_PARAM_KEY
					+ "\" in your \"local_app.properties\" file.<br />"
					+ "Eg.  <code>" + COMPONENT_TO_LAUNCH_PARAM_KEY
					+ "=com.github.bordertech.wcomponents.examples.picker.ExamplePicker</code>");

			((WText) sharedApp).setEncodeText(false);
		} else {
			UIRegistry registry = UIRegistry.getInstance();
			sharedApp = registry.getUI(uiClassName);

			if (sharedApp == null) {
				sharedApp = new WText(
						"Unable to load the component \""
						+ uiClassName
						+ "\".<br />"
						+ "Either the component does not exist as a resource in the classpath,"
						+ " or is not a WComponent.<br />"
						+ "Check that the parameter \""
						+ COMPONENT_TO_LAUNCH_PARAM_KEY
						+ "\" is set correctly.");

				((WText) sharedApp).setEncodeText(false);
			}
		}

		return sharedApp;
	}

	/**
	 * The entry point when the launcher is run as a java application.
	 *
	 * @param args command-line arguments, ignored.
	 * @throws Exception on error
	 */
	public static void main(final String[] args) throws Exception {
		PlainLauncher launcher = new PlainLauncher();
		launcher.run();
	}
}
