package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WServlet.WServletEnvironment;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.net.URL;
import java.net.URLConnection;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * PlainLauncher_Test - unit tests for {@link PlainLauncher}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class PlainLauncher_Test {

	private PlainLauncher launcher;

	@After
	public void tearDown() throws InterruptedException {
		Config.reset();
		UIContextHolder.reset();

		// Shut down the server
		if (launcher != null) {
			launcher.stop();
			launcher = null;
		}
	}

	@Test
	public void testCreateUI() {
		Config.getInstance().clearProperty(ConfigurationProperties.LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH);

		launcher = new PlainLauncher();
		WComponent ui = launcher.createUI();
		Assert.assertTrue("UI should be an instance of WText", ui instanceof WText);
		Assert.assertTrue("UI should contain instructions on configuring the LDE",
				((WText) ui).getText().contains(PlainLauncher.COMPONENT_TO_LAUNCH_PARAM_KEY));
	}

	@Test
	public void testGetUI() {
		Config.getInstance().setProperty(ConfigurationProperties.LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH,
				MyTestApp.class.getName());
		launcher = new PlainLauncher();

		WComponent ui1 = launcher.getUI(new MockHttpServletRequest());
		Assert.assertTrue("UI should be an instance of MyTestComponent", ui1 instanceof MyTestApp);

		// Call getUI again, the same instance should be returned
		WComponent ui2 = launcher.getUI(new MockHttpServletRequest());
		Assert.assertSame("Should have returned the same UI instance", ui1, ui2);
	}

	@Test
	public void testGetUINonWApplication() {
		Config.getInstance().setProperty(ConfigurationProperties.LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH,
				MyTestComponent.class.getName());
		PlainLauncher plain = new PlainLauncher();

		WComponent ui1 = plain.getUI(new MockHttpServletRequest());
		Assert.assertTrue("Root UI should be a WApplication", ui1 instanceof WApplication);

		ui1 = ((WApplication) ui1).getChildAt(0);
		Assert.assertTrue("UI should be an instance of MyTestComponent",
				ui1 instanceof MyTestComponent);

		// Call getUI again, the same instance should be returned
		WComponent ui2 = ((WApplication) plain.getUI(new MockHttpServletRequest())).getChildAt(0);
		Assert.assertSame("Should have returned the same UI instance", ui1, ui2);
	}

	@Test
	public void testServer() throws Exception {
		Config.getInstance().setProperty(ConfigurationProperties.LDE_PLAINLAUNCHER_COMPONENT_TO_LAUNCH,
				MyTestApp.class.getName());
		// random port
		Config.getInstance().setProperty(ConfigurationProperties.LDE_SERVER_PORT, "0");
		Config.getInstance().
				setProperty(ConfigurationProperties.WHITESPACE_FILTER, "false");

		launcher = new PlainLauncher();
		launcher.run();

		// Access the server and record the output
		URL url = new URL(launcher.getUrl());
		URLConnection conn = url.openConnection();
		byte[] result = StreamUtil.getBytes(conn.getInputStream());
		String content = new String(result, "UTF-8");

		Assert.assertEquals("HandleRequest should have been called once", 1,
				MyTestApp.handleRequestCount);
		Assert.assertEquals("PaintComponent should have been called once", 1, MyTestApp.paintCount);
		Assert.assertTrue("Content should contain the rendered application", content.contains((new MyTestApp()).getContent()));
	}

	/**
	 * A non-WApplication component, to test that LDE component wrapping works.
	 */
	public static final class MyTestComponent extends AbstractWComponent {
	}

	/**
	 * A Test component to use as the UI.
	 */
	public static final class MyTestApp extends WApplication {

		/**
		 * Some text which is rendered to the client.
		 */
		private static final String HELLO_WORLD = "Hello world!";

		/**
		 * The number of times that the handleRequest method has been called.
		 */
		private static int handleRequestCount = 0;

		/**
		 * The number of times that the paint method has been called.
		 */
		private static int paintCount = 0;

		/**
		 * Creates the test component.
		 */
		public MyTestApp() {
			add(new WText(HELLO_WORLD));
		}

		/**
		 *
		 * @return the content of the TestApp
		 */
		public String getContent() {
			return HELLO_WORLD;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleRequest(final Request request) {
			super.handleRequest(request);

			synchronized (MyTestApp.class) {
				handleRequestCount++;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void paintComponent(final RenderContext renderContext) {
			super.paintComponent(renderContext);

			synchronized (MyTestApp.class) {
				paintCount++;
			}
		}
	}
}
