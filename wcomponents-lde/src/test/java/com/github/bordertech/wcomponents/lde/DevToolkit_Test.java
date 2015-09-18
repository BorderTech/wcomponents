package com.github.bordertech.wcomponents.lde;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DevToolkit_Test - unit tests for {@link DevToolkit}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DevToolkit_Test {

	@Before
	public void setUp() {
		UIContext uic = new UIContextImpl();
		uic.setUI(new WText("dummy"));
		UIContextHolder.pushContext(uic);

		Config.getInstance().setProperty("bordertech.wcomponents.lde.devToolkit.enabled", "true");
	}

	@After
	public void tearDown() {
		Config.reset();
		UIContextHolder.reset();
	}

	@Test
	public void testEnableDisableToolkit() {
		Config.getInstance().clearProperty("bordertech.wcomponents.lde.devToolkit.enabled");
		Assert.assertFalse("DevToolkit should be disabled by default", DevToolkit.isEnabled());

		Config.getInstance().setProperty("bordertech.wcomponents.lde.devToolkit.enabled", "false");
		Assert.assertFalse("DevToolkit should be disabled", DevToolkit.isEnabled());

		Config.getInstance().setProperty("bordertech.wcomponents.lde.devToolkit.enabled", "true");
		Assert.assertTrue("DevToolkit should be enabled", DevToolkit.isEnabled());
	}

	@Test
	public void testServiceRequest() {
		DevToolkit toolkit = new DevToolkit();

		// Test when disabled
		Config.getInstance().clearProperty("bordertech.wcomponents.lde.devToolkit.enabled");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("wc_devToolkit", "x");
		request.setParameter("devToolkit_showTree", "true");
		toolkit.serviceRequest(request);
		Assert.assertFalse("Should have ignored the request when disabled", toolkit.isShowTree());

		// Test when enabled
		Config.getInstance().setProperty("bordertech.wcomponents.lde.devToolkit.enabled", "true");
		request = new MockHttpServletRequest();
		request.setParameter("wc_devToolkit", "x");
		request.setParameter("devToolkit_showTree", "true");
		toolkit.serviceRequest(request);
		Assert.assertTrue("Should have set show tree", toolkit.isShowTree());
	}

	@Test
	public void testShowTree() {
		DevToolkit toolkit = new DevToolkit();
		Assert.assertFalse("Should not show the tree by default", toolkit.isShowTree());

		sendToolkitRequest(toolkit, "devToolkit_showTree", "true");
		Assert.assertTrue("Should have set show tree to true", toolkit.isShowTree());

		sendToolkitRequest(toolkit, "devToolkit_showTree", "false");
		Assert.assertFalse("Should have set show tree to false", toolkit.isShowTree());
	}

	@Test
	public void testShowConfig() {
		DevToolkit toolkit = new DevToolkit();
		Assert.assertFalse("Should not show the configuration by default", toolkit.isShowConfig());

		sendToolkitRequest(toolkit, "devToolkit_showConfig", "true");
		Assert.assertTrue("Should have set show config to true", toolkit.isShowConfig());

		sendToolkitRequest(toolkit, "devToolkit_showConfig", "false");
		Assert.assertFalse("Should have set show config to false", toolkit.isShowConfig());
	}

	@Test
	public void testShowUicStats() {
		DevToolkit toolkit = new DevToolkit();
		Assert.assertFalse("Should not show the uic stats by default", toolkit.isShowUicStats());

		sendToolkitRequest(toolkit, "devToolkit_showUicStats", "true");
		Assert.assertTrue("Should have set show uic stats to true", toolkit.isShowUicStats());

		sendToolkitRequest(toolkit, "devToolkit_showUicStats", "false");
		Assert.assertFalse("Should have set show uic stats to false", toolkit.isShowUicStats());
	}

	@Test
	public void testShowRequest() {
		DevToolkit toolkit = new DevToolkit();
		Assert.assertFalse("Should not show the request by default", toolkit.isShowRequest());

		sendToolkitRequest(toolkit, "devToolkit_showRequest", "true");
		Assert.assertTrue("Should have set show request to true", toolkit.isShowRequest());

		sendToolkitRequest(toolkit, "devToolkit_showRequest", "false");
		Assert.assertFalse("Should have set show request to false", toolkit.isShowRequest());
	}

	@Test
	public void testResetSession() {
		MockHttpSession session = new MockHttpSession();
		Assert.assertFalse("Session should not be invalidated", session.isInvalidated());

		DevToolkit toolkit = new DevToolkit();

		MockHttpServletRequest request = new MockHttpServletRequest(session);
		request.setParameter("wc_devToolkit", "x");
		toolkit.serviceRequest(request);
		Assert.assertFalse("Session should not be invalidated", session.isInvalidated());

		request = new MockHttpServletRequest(session);
		request.setParameter("wc_devToolkit", "x");
		request.setParameter("devToolkit_resetSession", "x");
		toolkit.serviceRequest(request);
		Assert.assertTrue("Session should be invalidated", session.isInvalidated());
	}

	@Test
	public void testPaintHeaderShowRequest() {
		DevToolkit toolkit = new DevToolkit();

		String header = renderHeader(toolkit);
		Assert.assertTrue("Should not contain request info",
				header.indexOf("request headers") == -1);

		sendToolkitRequest(toolkit, "devToolkit_showRequest", "true");

		final String paramKey1 = "DevToolkit_Test.testPaintHeaderShowRequest.pkey1";
		final String paramKey2 = "DevToolkit_Test.testPaintHeaderShowRequest.pkey2";
		final String paramValue1 = "DevToolkit_Test.testPaintHeaderShowRequest.pvalue1";
		final String paramValue2 = "DevToolkit_Test.testPaintHeaderShowRequest.pvalue2";
		final String headerKey1 = "DevToolkit_Test.testPaintHeaderShowRequest.hkey1";
		final String headerKey2 = "DevToolkit_Test.testPaintHeaderShowRequest.hkey2";
		final String headerValue1 = "DevToolkit_Test.testPaintHeaderShowRequest.hvalue1";
		final String headerValue2 = "DevToolkit_Test.testPaintHeaderShowRequest.hvalue2";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter(paramKey1, paramValue1);
		request.setParameter(paramKey2, paramValue2);
		request.setHeader(headerKey1, headerValue1);
		request.setHeader(headerKey2, headerValue2);
		toolkit.serviceRequest(request);

		header = renderHeader(toolkit);
		Assert.assertTrue("Should contain request info", header.indexOf("request headers") != -1);
		Assert.assertTrue("Output should contain parameter key1", header.contains(paramKey1));
		Assert.assertTrue("Output should contain parameter key2", header.contains(paramKey2));
		Assert.assertTrue("Output should contain parameter value1", header.contains(paramValue1));
		Assert.assertTrue("Output should contain parameter value2", header.contains(paramValue2));
		Assert.assertTrue("Output should contain header key1", header.contains(headerKey1));
		Assert.assertTrue("Output should contain header key2", header.contains(headerKey2));
		Assert.assertTrue("Output should contain header value1", header.contains(headerValue1));
		Assert.assertTrue("Output should contain header value2", header.contains(headerValue2));
	}

	@Test
	public void testPaintHeaderShowConfig() {
		DevToolkit toolkit = new DevToolkit();

		String header = renderHeader(toolkit);
		Assert.assertTrue("Should not contain config",
				header.indexOf("WComponent configuration") == -1);

		sendToolkitRequest(toolkit, "devToolkit_showConfig", "true");
		header = renderHeader(toolkit);
		Assert.assertTrue("Should contain config",
				header.indexOf("WComponent configuration") != -1);

		// check that all the configuration parameters are present.
		for (Iterator i = Config.getInstance().getKeys(); i.hasNext();) {
			String key = (String) i.next();
			String value = Config.getInstance().getString(key);

			Assert.assertTrue("Should contain key " + key,
					header.indexOf(WebUtilities.encode(key)) != -1);
			Assert.assertTrue("Should contain value " + value, header.indexOf(WebUtilities.encode(
					value)) != -1);
		}
	}

	/**
	 * Sends a request to the given toolkit.
	 *
	 * @param toolkit the toolkit
	 * @param key the request parameter key
	 * @param value the request parameter value
	 */
	private void sendToolkitRequest(final DevToolkit toolkit, final String key,
			final String value) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("wc_devToolkit", "x");
		request.setParameter(key, value);
		toolkit.serviceRequest(request);
	}

	/**
	 * Renders the toolkit header.
	 *
	 * @param toolkit the toolkit to render the header for.
	 * @return the rendered header.
	 */
	private String renderHeader(final DevToolkit toolkit) {
		StringWriter buf = new StringWriter();
		PrintWriter writer = new PrintWriter(buf);
		toolkit.paintHeader(writer);
		writer.close();

		return buf.toString();
	}
}
