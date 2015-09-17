package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.MockContainer;
import com.github.bordertech.wcomponents.MockLabel;
import com.github.bordertech.wcomponents.MockPanel;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import com.github.bordertech.wcomponents.util.mock.servlet.MockServletConfig;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

/**
 * WServlet_Test - unit tests for {@link WServlet}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WServlet_Test extends AbstractWComponentTestCase {

	private static final String LABEL_TEXT = "WServlet_Test.LABEL_TEXT";

	@After
	public void restoreConfig() {
		Config.reset();
	}

	@Test
	public void testServiceNormalRequest() throws ServletException, IOException {
		MockServletConfig config = new MockServletConfig();
		config.setInitParameter(WServlet.WServletHelper.ONGOING_URL_SUFFIX, "foo");

		MockContainer content = new MockContainer();
		content.add(new WText(LABEL_TEXT));

		content.setLocked(true);
		MyWServlet servlet = new MyWServlet(content);
		servlet.init(config);

		MockHttpSession session1 = new MockHttpSession();
		MockHttpSession session2 = new MockHttpSession();

		sendRequest(session1, servlet);
		sendRequest(session2, servlet);
		sendRequest(session1, servlet);

		// check handle request / paint counts for each session
		UIContext uic = getContextForSession(servlet, session1);
		setActiveContext(uic);

		Assert.assertEquals("Incorrect handle request count for session1", 2, content.
				getHandleRequestCount());
		Assert.assertEquals("Incorrect paint count for session1", 2, content.getPaintCount());

		uic = getContextForSession(servlet, session2);
		setActiveContext(uic);

		Assert.assertEquals("Incorrect handle request count for session2", 1, content.
				getHandleRequestCount());
		Assert.assertEquals("Incorrect paint count for session2", 1, content.getPaintCount());
	}

	@Test
	public void testServiceWithException() throws ServletException, IOException {
		// A null UI should result in an exception which should be handled internally
		MyWServlet servlet = new MyWServlet(null);
		servlet.init(new MockServletConfig());

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		MockHttpServletResponse response = new MockHttpServletResponse();

		servlet.service(request, response);

		String output = new String(response.getOutput());
		Assert.assertNotNull("An error message should be written when an exception occurs", output);
	}

	@Test
	public void testServiceAjaxRequest() throws ServletException, IOException {
		AjaxTestUI ui = new AjaxTestUI();
		ui.setLocked(true);

		MockHttpSession session1 = new MockHttpSession();
		UIContextImpl uic1 = new UIContextImpl();
		uic1.setEnvironment(new WServlet.WServletEnvironment("app", "http://localhost", ""));
		uic1.setUI(ui);
		uic1.getEnvironment().setSessionToken("1");
		uic1.getEnvironment().setStep(1);

		MockHttpSession session2 = new MockHttpSession();
		UIContextImpl uic2 = new UIContextImpl();
		uic2.setEnvironment(new WServlet.WServletEnvironment("app", "http://localhost", ""));
		uic2.setUI(ui);
		uic2.getEnvironment().setSessionToken("2");

		// Request cycle for session 1
		setActiveContext(uic1);
		ui.serviceRequest(new ServletRequest(new MockHttpServletRequest(session1)));
		ui.preparePaint(new ServletRequest(new MockHttpServletRequest(session1)));
		ui.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter())));

		// Request cycle for session 2
		setActiveContext(uic2);
		ui.serviceRequest(new ServletRequest(new MockHttpServletRequest(session2)));
		ui.preparePaint(new ServletRequest(new MockHttpServletRequest(session2)));
		ui.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter())));

		// check handle request / paint counts for each session - label should not have been painted for either.
		setActiveContext(uic1);
		Assert.assertEquals("HandleRequest should have been called for main panel in session1", 1,
				ui.mainPanel.getHandleRequestCount());
		Assert.assertEquals("HandleRequest should have been called for ajax panel in session1", 1,
				ui.ajaxPanel.getHandleRequestCount());
		Assert.assertEquals("HandleRequest should have been called for label panel in session1", 1,
				ui.label.getHandleRequestCount());
		Assert.assertEquals("Main panel should have painted in session1", 1, ui.mainPanel.
				getPaintCount());
		Assert.assertEquals("Ajax panel should have painted in session1", 1, ui.ajaxPanel.
				getPaintCount());
		Assert.
				assertEquals("Label should not have painted in session1", 0, ui.label.
						getPaintCount());
		setActiveContext(uic2);
		Assert.assertEquals("HandleRequest should have been called for main panel in session2", 1,
				ui.mainPanel.getHandleRequestCount());
		Assert.assertEquals("HandleRequest should have been called for ajax panel in session2", 1,
				ui.ajaxPanel.getHandleRequestCount());
		Assert.assertEquals("HandleRequest should have been called for label panel in session2", 1,
				ui.label.getHandleRequestCount());
		Assert.assertEquals("Main panel should have painted in session2", 1, ui.mainPanel.
				getPaintCount());
		Assert.assertEquals("Ajax panel should have painted in session2", 1, ui.ajaxPanel.
				getPaintCount());
		Assert.
				assertEquals("Label should not have painted in session2", 0, ui.label.
						getPaintCount());

// TODO Review what test is
//        // This is the actual test of the AJAX servlet's service method
//        // handleRequest should be called on everything, but only the label should be painted.
//        WServlet servlet = new WServlet();
//        servlet.init(new MockServletConfig());
//
//        MockHttpServletRequest request = new MockHttpServletRequest(session1);
//        setActiveContext(uic1);
//        request.setMethod("GET");
//        request.setParameter(WServlet.AJAX_TRIGGER_PARAM_NAME, ui.ajaxPanel.getId());
//        request.setParameter(Environment.SESSION_TOKEN_VARIABLE, uic1.getEnvironment().getSessionToken());
//        request.setParameter(Environment.STEP_VARIABLE, String.valueOf(uic1.getEnvironment().getStep()));
//
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        servlet.service(request, response);
//
//        // check handle request / paint counts for each session - only label should have painted for uic1.
//        setActiveContext(uic1);
//        Assert.assertEquals("HandleRequest should have been called for main panel in session1", 2, ui.mainPanel.getHandleRequestCount());
//        Assert.assertEquals("HandleRequest should have been called for ajax panel in session1", 2, ui.ajaxPanel.getHandleRequestCount());
//        Assert.assertEquals("HandleRequest should have been called for label panel in session1", 2, ui.label.getHandleRequestCount());
//        Assert.assertEquals("Paint should not have been called for main panel in session1", 1, ui.mainPanel.getPaintCount());
//        Assert.assertEquals("Paint should have been called for ajax panel in session1", 2, ui.ajaxPanel.getPaintCount());
//        Assert.assertEquals("Paint should have been called for label in session1", 1, ui.label.getPaintCount());
//        setActiveContext(uic2);
//        Assert.assertEquals("HandleRequest should not have been called for main panel in session2", 1, ui.mainPanel.getHandleRequestCount());
//        Assert.assertEquals("HandleRequest should not have been called for ajax panel in session2", 1, ui.ajaxPanel.getHandleRequestCount());
//        Assert.assertEquals("HandleRequest should not have been called for label panel in session2", 1, ui.label.getHandleRequestCount());
//        Assert.assertEquals("Paint should not have been called for main panel in session1", 1, ui.mainPanel.getPaintCount());
//        Assert.assertEquals("Paint should not been called for ajax panel in session1", 1, ui.ajaxPanel.getPaintCount());
//        Assert.assertEquals("Paint should not have been called for label in session1", 0, ui.label.getPaintCount());
//
//        Assert.assertTrue("Response should contain label text", response.getOutputAsString().contains(LABEL_TEXT));
	}

	@Test
	public void testSubSessionsDisabledNoSSID() throws ServletException, IOException {
		Config.getInstance().setProperty(ServletUtil.ENABLE_SUBSESSIONS, "false");
		MyWServlet servlet = new MyWServlet(new WText("test"));
		servlet.init(new MockServletConfig());

		MockHttpSession session = new MockHttpSession();

		servlet.service(new MockHttpServletRequest(session), new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);

		servlet.service(new MockHttpServletRequest(session), new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 2, servlet.lastInvocationCount);
	}

	@Test
	public void testSubSessionsDisabled() throws ServletException, IOException {
		Config.getInstance().setProperty(ServletUtil.ENABLE_SUBSESSIONS, "false");
		MyWServlet servlet = new MyWServlet(new WText("test"));
		servlet.init(new MockServletConfig());

		MockHttpSession session = new MockHttpSession();

		MockHttpServletRequest request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "1");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);

		request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "2");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 2, servlet.lastInvocationCount);

		request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "1");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 3, servlet.lastInvocationCount);
	}

	@Test
	public void testSubSessionsEnabledNoSSID() throws ServletException, IOException {
		Config.getInstance().setProperty(ServletUtil.ENABLE_SUBSESSIONS, "true");
		MyWServlet servlet = new MyWServlet(new WText("test"));
		servlet.init(new MockServletConfig());

		MockHttpSession session = new MockHttpSession();

		servlet.service(new MockHttpServletRequest(session), new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);

		// No SSID should trigger a new subsession
		servlet.service(new MockHttpServletRequest(session), new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);
	}

	@Test
	public void testSubSessionsEnabled() throws ServletException, IOException {
		Config.getInstance().setProperty(ServletUtil.ENABLE_SUBSESSIONS, "true");
		MyWServlet servlet = new MyWServlet(new WText("test"));
		servlet.init(new MockServletConfig());

		MockHttpSession session = new MockHttpSession();

		// Initial request will be missing a SSID, will create a new subsession (id = 0)
		MockHttpServletRequest request = new MockHttpServletRequest(session);
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);

		// Passing in an invalid SSID should return same subsession (id = 0)
		request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "asdf");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 2, servlet.lastInvocationCount);

		// Missing SSID should create a new subession (id = 1)
		request = new MockHttpServletRequest(session);
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 1, servlet.lastInvocationCount);

		// Should update the primary session (id = 0)
		request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "0");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 3, servlet.lastInvocationCount);

		// Should update the secondary session (id = 1)
		request = new MockHttpServletRequest(session);
		request.setParameter("ssid", "1");
		servlet.service(request, new MockHttpServletResponse());
		Assert.assertEquals("Incorrect invocation count", 2, servlet.lastInvocationCount);
	}

	@Test
	public void testHttpMethodTypes() throws ServletException, IOException {
		MyWServlet servlet = new MyWServlet(new WText("test"));
		servlet.init(new MockServletConfig());

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("http://localhost/foo");

		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setMethod("GET");
		servlet.service(request, response);
		Assert.assertEquals("Incorrect status code", 200, response.getStatus());
		Assert.assertTrue("Should have written content", response.getOutput().length > 0);

		response = new MockHttpServletResponse();
		request.setMethod("POST");
		servlet.service(request, response);
		Assert.assertEquals("Incorrect status code", 200, response.getStatus());
		Assert.assertTrue("Should have written content", response.getOutput().length > 0);

		response = new MockHttpServletResponse();
		request.setMethod("HEAD");
		servlet.service(request, response);
		Assert.assertEquals("Incorrect status code", 200, response.getStatus());
		Assert.assertNull("Should not have written any content for HEAD request", response.
				getOutput());

		response = new MockHttpServletResponse();
		request.setMethod("DELETE");
		servlet.service(request, response);
		Assert.assertEquals("Incorrect status code", 501, response.getStatus());
		Assert.assertNull("Should not have written any content for HEAD request", response.
				getOutput());
	}

	/**
	 * getUIContext is protected in WServlet.WServletHelper, so this is a duplicate.
	 *
	 * @param servlet the servlet instance
	 * @param session the current session.
	 * @return the user context for the session
	 */
	private UIContext getContextForSession(final WServlet servlet, final HttpSession session) {
		return (UIContext) session.getAttribute(servlet.getClass().getName() + ".servlet.model");
	}

	/**
	 * Simulates an HTTP GET request to a WServlet.
	 *
	 * @param session the current user's session
	 * @param servlet the servlet to invoke request processing on.
	 * @throws ServletException a servlet exception
	 * @throws IOException an exception
	 */
	private void sendRequest(final MockHttpSession session, final WServlet servlet) throws
			ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest(session);
		request.setRequestURI("http://localhost/foo");
		request.setMethod("GET");

		MockHttpServletResponse response = new MockHttpServletResponse();
		servlet.service(request, response);

		String output = new String(response.getOutput());
		Assert.assertTrue("Response is missing label text", output.indexOf(LABEL_TEXT) != -1);
	}

	/**
	 * The UI used for testing - contains an AJAX panel with some content.
	 */
	private static final class AjaxTestUI extends WApplication {

		private final MockPanel mainPanel = new MockPanel();
		private final MockPanel ajaxPanel = new MockPanel();
		private final MockLabel label = new MockLabel(LABEL_TEXT);

		/**
		 * Creates the test UI.
		 */
		private AjaxTestUI() {
			add(mainPanel);
			mainPanel.add(ajaxPanel);

			ajaxPanel.add(label);
			ajaxPanel.setMode(WPanel.PanelMode.EAGER);
		}
	}

	/**
	 * A WServlet that lets you specify the root component.
	 */
	private static final class MyWServlet extends WServlet {

		/**
		 * The UI that this servlet provides.
		 */
		private final WComponent component;

		/**
		 * Used to hold the count of invocations per session.
		 */
		private static final String INVOCATION_COUNT_KEY = "MyWServlet.invocationCountKey";

		/**
		 * The invocation count for the last request that was serviced.
		 */
		private int lastInvocationCount = 0;

		/**
		 * Creates a MyWServlet.
		 *
		 * @param component the WComponent UI to serve.
		 */
		private MyWServlet(final WComponent component) {
			this.component = component;
		}

		@Override
		protected void serviceInt(final HttpServletRequest request,
				final HttpServletResponse response)
				throws ServletException, IOException {
			HttpSession session = request.getSession(true);

			// Increment the invocation count.
			Integer count = (Integer) session.getAttribute(INVOCATION_COUNT_KEY);
			count = count == null ? 1 : (count + 1);
			session.setAttribute(INVOCATION_COUNT_KEY, count);
			lastInvocationCount = count;

			super.serviceInt(request, response);
		}

		@Override
		public WComponent getUI(final Object httpServletRequest) {
			return component;
		}
	}
}
