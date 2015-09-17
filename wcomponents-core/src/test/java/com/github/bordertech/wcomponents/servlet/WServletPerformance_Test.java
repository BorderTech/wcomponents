package com.github.bordertech.wcomponents.servlet;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.PerformanceTests;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.servlet.WServlet.WServletHelper;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import com.github.bordertech.wcomponents.util.mock.servlet.MockServletConfig;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests to check the performance of WComponent servlet processing. This test does not check for correct behaviour - see
 * {@link WServlet_Test} instead.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(PerformanceTests.class)
public class WServletPerformance_Test extends AbstractWComponentTestCase {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WServletPerformance_Test.class);

	/**
	 * Basic sanity-test to ensure that the WComponent app is performing all the processing that it should.
	 *
	 * @throws Exception an exception
	 */
	@Test
	public void testWServletAppCorrectness() throws Exception {
		SimpleWServlet servlet = new SimpleWServlet();
		servlet.init(new MockServletConfig());
		MockHttpSession session = new MockHttpSession();

		// First request
		sendWServletRequest(servlet, session, 0, null);

		// Second request
		WServletHelper helper = new WServletHelper(servlet, new MockHttpServletRequest(session),
				new MockHttpServletResponse());
		UIContext uic = helper.getUIContext();
		SimpleApp app = (SimpleApp) uic.getUI();

		sendWServletRequest(servlet, session, 1, uic.getEnvironment().getSessionToken());

		setActiveContext(uic);
		Assert.assertEquals("Incorrect step", 2, uic.getEnvironment().getStep());
		Assert.assertEquals("Incorrect property1 value", "p1_1",
				((SimpleFormBean) app.beanContainer.getBean()).getProperty1());
		Assert.assertEquals("Incorrect property2 value", "p2_1",
				((SimpleFormBean) app.beanContainer.getBean()).getProperty2());
	}

	/**
	 * Basic sanity-test to ensure that the other app is performing all the processing that it should.
	 *
	 * @throws Exception an exception
	 */
	@Test
	public void testOtherServletAppCorrectness() throws Exception {
		SimpleServlet servlet = new SimpleServlet();
		servlet.init(new MockServletConfig());
		MockHttpSession session = new MockHttpSession();

		sendOtherServletRequest(servlet, session, 0);
		sendOtherServletRequest(servlet, session, 1);

		SimpleFormBean bean = servlet.getFormBean(new MockHttpServletRequest(session));
		Assert.assertEquals("Incorrect property1 value", "p1_1", bean.getProperty1());
		Assert.assertEquals("Incorrect property2 value", "p2_1", bean.getProperty2());
	}

	@Test
	public void testServletPerformance() throws Exception {
		final int numLoops = 2000;

		// Run the test with the simple servlet
		long simpleTime = timeOtherServlet(numLoops) / numLoops;
		long wservletTime = timeWServlet(numLoops) / numLoops;

		LOG.info("Simple servlet time: " + (simpleTime / 1000000.0) + "ms");
		LOG.info("WComponent servlet time: " + (wservletTime / 1000000.0) + "ms");

		Assert.assertTrue("WComponent servlet time should not exceed 10x simple time",
				wservletTime < simpleTime * 10);
	}

	/**
	 * Times the WServlet execution looping the given number of times and returns the elapsed time.
	 *
	 * @param count the number of times to loop.
	 * @return the elapsed time, in nanoseconds.
	 * @throws Exception an exception
	 */
	private long timeWServlet(final int count) throws Exception {
		final SimpleWServlet servlet = new SimpleWServlet();
		servlet.init(new MockServletConfig());
		final MockHttpSession simpleWServletSession = new MockHttpSession();

		// Do first request to get session token
		sendWServletRequest(servlet, simpleWServletSession, 0, null);

		// Get token
		WServletHelper helper = new WServletHelper(servlet, new MockHttpServletRequest(
				simpleWServletSession), new MockHttpServletResponse());
		String token = helper.getUIContext().getEnvironment().getSessionToken();

		// JIT warm-up
		for (int i = 1; i < count; i++) {
			sendWServletRequest(servlet, simpleWServletSession, i, token);
		}

		simpleWServletSession.getAttributes().clear();

		// Do first request to get session token
		sendWServletRequest(servlet, simpleWServletSession, 0, null);
		final String token2 = helper.getUIContext().getEnvironment().getSessionToken();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 1; i < count; i++) {
						sendWServletRequest(servlet, simpleWServletSession, i, token2);
					}
				} catch (Exception e) {
					LOG.error("Failed to execute test", e);
				}
			}
		};

		return time(runnable);
	}

	/**
	 * Times the other servlet execution looping the given number of times and returns the elapsed time.
	 *
	 * @param count the number of times to loop.
	 * @return the elapsed time, in nanoseconds.
	 * @throws Exception an exception
	 */
	private long timeOtherServlet(final int count) throws Exception {
		final SimpleServlet servlet = new SimpleServlet();
		servlet.init(new MockServletConfig());
		final MockHttpSession simpleServletSession = new MockHttpSession();

		// JIT warm-up
		for (int i = 0; i < count; i++) {
			sendOtherServletRequest(servlet, simpleServletSession, i);
		}

		simpleServletSession.getAttributes().clear();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < count; i++) {
						sendOtherServletRequest(servlet, simpleServletSession, i);
					}
				} catch (Exception e) {
					LOG.error("Failed to execute test", e);
				}
			}
		};

		return time(runnable);
	}

	/**
	 * Invokes WComponent servlet processing.
	 *
	 * @param servlet the servlet to invoke request processing on.
	 * @param session the current session.
	 * @param step the step count
	 * @param token the session token
	 * @throws Exception an exception
	 */
	private void sendWServletRequest(final WServlet servlet, final HttpSession session,
			final int step, final String token) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(session);
		MockHttpServletResponse response = new MockHttpServletResponse();

		if (step > 0) {
			request.setMethod("POST");
			request.setRequestURI("http://localhost/app");

			// These are hard-coded to avoid overhead during performance testing
			request.setParameter(Environment.STEP_VARIABLE, String.valueOf(step));
			request.setParameter(Environment.SESSION_TOKEN_VARIABLE, token);
			request.setParameter("_0b", "p1_" + step);
			request.setParameter("_0d", "p2_" + step);
			request.setParameter("_0e", "x");
		} else {
			request.setMethod("GET");
			request.setRequestURI("http://localhost/app");
		}

		servlet.service(request, response);
	}

	/**
	 * Invokes the other servlet request processing.
	 *
	 * @param servlet the servlet to invoke request processing on.
	 * @param session the current session.
	 * @param step the step count
	 * @throws Exception an exception
	 */
	private void sendOtherServletRequest(final SimpleServlet servlet, final HttpSession session,
			final int step) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest(session);
		MockHttpServletResponse response = new MockHttpServletResponse();

		if (step > 0) {
			request.setMethod("POST");
			request.setRequestURI("http://localhost/app");
			request.setParameter("formBean.property1", "p1_" + step);
			request.setParameter("formBean.property2", "p2_" + step);
			request.setParameter("submit", "Submit");
		} else {
			request.setMethod("GET");
			request.setRequestURI("http://localhost/app");
		}

		servlet.service(request, response);
	}

	/**
	 * A WServlet extension that serves up the SimpleApp.
	 */
	private static final class SimpleWServlet extends WServlet {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public WComponent getUI(final Object httpServletRequest) {
			return UIRegistry.getInstance().getUI(SimpleApp.class.getName());
		}
	}

	/**
	 * A simple WComponent application.
	 */
	public static final class SimpleApp extends WApplication {

		private final WBeanContainer beanContainer = new WBeanContainer();

		/**
		 * Creates a SimpleApp.
		 */
		public SimpleApp() {
			add(beanContainer);

			WTextField property1 = new WTextField();
			property1.setBeanProperty("property1");
			WTextField property2 = new WTextField();
			property2.setBeanProperty("property2");

			beanContainer.add(new WLabel("Property 1:", property1));
			beanContainer.add(property1);
			beanContainer.add(new WLabel("Property 2:", property2));
			beanContainer.add(property2);

			WButton submit = new WButton("Submit");

			submit.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					WebUtilities.updateBeanValue(beanContainer);
				}
			});

			beanContainer.add(submit);
		}

		@Override
		protected void preparePaintComponent(final Request request) {
			super.preparePaintComponent(request);

			if (!isInitialised()) {
				beanContainer.setBean(new SimpleFormBean());
				setInitialised(true);
			}
		}
	}

	/**
	 * A simple struts-like servlet.
	 */
	private static final class SimpleServlet extends HttpServlet {

		/**
		 * The logger instance for this class.
		 */
		private static final Log LOG = LogFactory.getLog(SimpleServlet.class);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void service(final HttpServletRequest request, final HttpServletResponse response)
				throws ServletException {
			try {
				// Match logging overhead
				LOG.info("Service called " + this + " from " + request.getRequestURL());
				doAction(request);
				doRender(request, response);
				LOG.info("service complete");
			} catch (Exception e) {
				throw new ServletException("Internal error", e);
			}
		}

		/**
		 * Processes the data coming in on the request.
		 *
		 * @param request the request being handled
		 * @throws Exception on error
		 */
		private void doAction(final HttpServletRequest request) throws Exception {
			if (request.getParameter("submit") != null) {
				SimpleFormBean formBean = getFormBean(request);
				Map<String, String[]> properties = new HashMap<>();

				for (Enumeration<?> names = request.getParameterNames(); names.hasMoreElements();) {
					String key = (String) names.nextElement();

					if (key.startsWith("formBean")) {
						properties.put(key.substring("formBean.".length()), request.
								getParameterValues(key));
					}
				}

				BeanUtils.populate(formBean, properties);
			}
		}

		/**
		 * Renders the HTML content for the browser.
		 *
		 * @param request the request being handled.
		 * @param response the response to write to.
		 * @throws Exception on error
		 */
		private void doRender(final HttpServletRequest request, final HttpServletResponse response)
				throws Exception {
			SimpleFormBean formBean = getFormBean(request);
			String title = "Some title";

			PrintWriter writer = response.getWriter();

			writer.print("<!DOCTYPE html>");
			writer.print(
					"\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
			writer.print("\n<head>");
			writer.
					print("\n<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">");
			writer.print("<title>" + title + "</title>");
			writer.print(
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/my.css/\" media=\"all\">");
			writer.print("\n</head>");
			writer.print("\n<body>");

			writer.print(
					"\n<form method=\"post\" action=\"" + request.getRequestURI() + "\" id=\"mainForm\">");
			writer.print(
					"\n<label id=\"label_formBean.property1\" for=\"formBean.property1\">Property 1:</label>");
			writer.print(
					"\n<input type=\"text\" id=\"formBean.property1\" name=\"formBean.property1\" value=\"" + BeanUtils.
					getProperty(formBean, "property1") + "\"/>");
			writer.print(
					"\n<label id=\"label_formBean.property2\" for=\"formBean.property2\">Property 2:</label>");
			writer.print(
					"\n<input type=\"text\" id=\"formBean.property2\" name=\"formBean.property2\" value=\"" + BeanUtils.
					getProperty(formBean, "property2") + "\"/>");
			writer.print("\n<input type=\"submit\" id=\"submit\" name=\"submit\" value=\"Submit\">");
			writer.print("\n</form>");

			writer.print("\n</body>");
			writer.print("\n</html>");
		}

		/**
		 * Retrieves the form bean instance from the session, creating a new bean if one does not already exist.
		 *
		 * @param request the request being handled.
		 * @return the form bean instance.
		 */
		private SimpleFormBean getFormBean(final HttpServletRequest request) {
			HttpSession session = request.getSession(true);
			SimpleFormBean formBean = (SimpleFormBean) session.getAttribute("formBean");

			if (formBean == null) {
				formBean = new SimpleFormBean();
				session.setAttribute("formBean", formBean);
			}

			return formBean;
		}
	}

	/**
	 * A simple bean with arbitrary properties.
	 */
	public static final class SimpleFormBean {

		/**
		 * An arbitrary property.
		 */
		private String property1;

		/**
		 * An arbitrary property.
		 */
		private String property2;

		/**
		 * @return Returns the property1.
		 */
		public String getProperty1() {
			return property1;
		}

		/**
		 * @param property1 The property1 to set.
		 */
		public void setProperty1(final String property1) {
			this.property1 = property1;
		}

		/**
		 * @return Returns the property2.
		 */
		public String getProperty2() {
			return property2;
		}

		/**
		 * @param property2 The property2 to set.
		 */
		public void setProperty2(final String property2) {
			this.property2 = property2;
		}
	}
}
