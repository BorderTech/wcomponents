package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRendererTestCase;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * DebugStructureInterceptor_Test - unit tests for {@link DebugStructureInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class DebugStructureInterceptor_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testInterceptorDisabled() throws XpathException, SAXException, IOException {
		Config.getInstance().setProperty("bordertech.wcomponents.debug.enabled", "false");
		Config.notifyListeners();

		// No debug info should be rendered
		String xml = doRequest();
		assertXpathEvaluatesTo("0", "count(//ui:debug/ui:debugInfo)", xml);
	}

	@Test
	public void testInterceptor() throws XpathException, SAXException, IOException {
		Config.getInstance().setProperty("bordertech.wcomponents.debug.enabled", "true");
		Config.getInstance().setProperty("bordertech.wcomponents.debug.clientSide.enabled", "true");
		Config.notifyListeners();

		// Only the label should have debug info rendered
		String xml = doRequest();
		assertXpathEvaluatesTo("1", "count(//ui:debug/ui:debugInfo)", xml);
		assertXpathEvaluatesTo(new WApplication().getId(), "//ui:debugInfo/@for", xml);
		assertXpathEvaluatesTo(WApplication.class.getName(), "//ui:debug/ui:debugInfo/@class", xml);
		assertXpathEvaluatesTo(WApplication.class.getName(), "//ui:debug/ui:debugInfo/@type", xml);
		assertXpathEvaluatesTo("false",
				"//ui:debug/ui:debugInfo/ui:debugDetail[@key='defaultState']/@value", xml);
	}

	/**
	 * Does a request/response cycle for a WApplication.
	 *
	 * @return the xml output.
	 */
	private String doRequest() {
		WApplication app = new WApplication();
		app.setLocked(true);

		UIContext uic = createUIContext();
		uic.setUI(app);
		setActiveContext(uic);

		// Create interceptor
		PageShellInterceptor pageInterceptor = new PageShellInterceptor();
		DebugStructureInterceptor debugInterceptor = new DebugStructureInterceptor();

		pageInterceptor.setBackingComponent(debugInterceptor);
		pageInterceptor.attachUI(app);

		// Action phase
		MockRequest request = new MockRequest();
		pageInterceptor.serviceRequest(request);
		pageInterceptor.preparePaint(request);

		// Render phase
		MockResponse response = new MockResponse();
		pageInterceptor.attachResponse(response);
		pageInterceptor.paint(new WebXmlRenderContext(response.getWriter()));

		return response.getWriterOutput();
	}
}
