package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.AjaxHelper;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRendererTestCase;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * WrongStepAjaxInterceptor_Test - unit tests for {@link WrongStepAjaxInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WrongStepAjaxInterceptor_Test extends AbstractWebXmlRendererTestCase {

	/**
	 * Application POST path.
	 */
	private static final String APP_POSTPATH = "/app";

	@After
	public void resetConfig() {
		Config.reset();
	}

	@Test
	public void testInterceptorOkStep() throws XpathException, SAXException, IOException {
		MyApp app = new MyApp();
		app.setLocked(true);

		MockResponse response = doAjaxRequest(app, 1, 1);
		assertXpathEvaluatesTo(app.target.getText(), "//ui:label", response.getWriterOutput());
	}

	@Test
	public void testInterceptorRedirect() throws XpathException, SAXException, IOException {
		Config.getInstance().setProperty(StepCountUtil.STEP_ERROR_URL_PARAMETER_KEY,
				"http://test.test");

		MyApp app = new MyApp();
		app.setLocked(true);

		MockResponse response = doAjaxRequest(app, 1, 99);
		assertXpathEvaluatesTo("http://test.test", "//ui:redirect/@url", response.getWriterOutput());
	}

	@Test
	public void testInterceptorWarp() throws XpathException, SAXException, IOException {
		Config.getInstance().setProperty(StepCountUtil.STEP_ERROR_URL_PARAMETER_KEY, "");

		MyApp app = new MyApp();
		app.setLocked(true);

		// Should redirect to app postpath
		MockResponse response = doAjaxRequest(app, 1, 99);
		assertXpathEvaluatesTo(APP_POSTPATH, "//ui:redirect/@url", response.getWriterOutput());
	}

	/**
	 * Does an AJAX request for the app.
	 *
	 * @param app the MyApp instance to do an AJAX request for.
	 * @param clientStep the client-side step counter
	 * @param serverStep the server-side step counter
	 * @return the response object.
	 */
	private MockResponse doAjaxRequest(final MyApp app, final int clientStep, final int serverStep) {
		UIContext uic = createUIContext();
		WServlet.WServletEnvironment env = new WServlet.WServletEnvironment(APP_POSTPATH,
				"http://localhost", "");
		env.setStep(serverStep);
		env.setSessionToken("T");
		uic.setEnvironment(env);
		uic.setUI(app);
		setActiveContext(uic);

		MockRequest request = new MockRequest();
		MockResponse response = new MockResponse();

		// Create interceptors
		AjaxSetupInterceptor ajaxSetupInterceptor = new AjaxSetupInterceptor();
		WrongStepAjaxInterceptor wrongStepInterceptor = new WrongStepAjaxInterceptor();
		AjaxPageShellInterceptor ajaxPageInterceptor = new AjaxPageShellInterceptor();
		AjaxInterceptor ajaxInterceptor = new AjaxInterceptor();

		ajaxPageInterceptor.setBackingComponent(ajaxInterceptor);
		wrongStepInterceptor.setBackingComponent(ajaxPageInterceptor);
		ajaxSetupInterceptor.setBackingComponent(wrongStepInterceptor);
		ajaxSetupInterceptor.attachUI(app);
		ajaxSetupInterceptor.attachResponse(response);

		// Action phase
		try {
			AjaxHelper.registerComponent(app.target.getId(), request, app.trigger.getId());
			request.setParameter(WServlet.AJAX_TRIGGER_PARAM_NAME, app.trigger.getId());
			request.setParameter(Environment.STEP_VARIABLE, String.valueOf(clientStep));

			ajaxSetupInterceptor.serviceRequest(request);
			ajaxSetupInterceptor.preparePaint(request);

			// Render phase
			ajaxSetupInterceptor.paint(new WebXmlRenderContext(response.getWriter()));
		} catch (ActionEscape ignored) {
			// is thrown to skip render phase
		}

		return response;
	}

	/**
	 * A simple test UI which is AJAX-enabled.
	 */
	private static final class MyApp extends WApplication {

		/**
		 * An AJAX trigger.
		 */
		private final WButton trigger = new WButton("WrongStepAjaxInterceptor_Test.MyApp.trigger");

		/**
		 * An AJAX target.
		 */
		private final WLabel target = new WLabel("WrongStepAjaxInterceptor_Test.MyApp.target");

		/**
		 * Creates the test app.
		 */
		private MyApp() {
			trigger.setAjaxTarget(target);
			add(trigger);
			add(target);
		}
	}
}
