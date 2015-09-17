package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.ContentEscape;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.ErrorCodeEscape;
import com.github.bordertech.wcomponents.MockContentAccess;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WWindow;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.StepCountUtil;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * WrongStepContentComponent_Test - unit tests for {@link WrongStepContentInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WrongStepContentInterceptor_Test extends AbstractWComponentTestCase {

	@After
	public void resetConfig() {
		Config.reset();
	}

	@Test(expected = SystemException.class)
	public void testServiceRequestNoTarget() {
		setActiveContext(createUIContext());
		new WrongStepContentInterceptor().serviceRequest(new MockRequest());
	}

	@Test
	public void testServiceRequestNoStepError() {
		MyApp app = new MyApp();
		app.setLocked(true);
		MockResponse response = sendContentRequest(app.appContent, 1, 1);
		Assert.assertEquals("Should have returned content", MyContent.CONTENT, new String(response.
				getOutput()));
	}

	@Test
	public void testErrorDirectContent() {
		Config.getInstance().setProperty(StepCountUtil.STEP_ERROR_URL_PARAMETER_KEY,
				"http://test.test");

		MyApp app = new MyApp();
		app.setLocked(true);
		MockResponse response = sendContentRequest(app.appContent, 1, 99);
		Assert.assertEquals("Should have returned error", HttpServletResponse.SC_BAD_REQUEST,
				response.getErrorCode());
	}

	/**
	 * Utility method to send a mock request to the application.
	 *
	 * @param target the target content.
	 * @param clientStep the client-side step count
	 * @param serverStep the server-side step count
	 * @return the response.
	 */
	private MockResponse sendContentRequest(final WComponent target, final int clientStep,
			final int serverStep) {
		UIContext uic = createUIContext();
		uic.setUI(WebUtilities.getTop(target));
		WServlet.WServletEnvironment env = new WServlet.WServletEnvironment("/app",
				"http://localhost", "");
		env.setStep(serverStep);
		uic.setEnvironment(env);
		setActiveContext(uic);

		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, target.getId());
		request.setParameter(Environment.STEP_VARIABLE, String.valueOf(clientStep));
		MockResponse response = new MockResponse();

		InterceptorComponent interceptor = new WrongStepContentInterceptor();
		interceptor.attachUI(uic.getUI());
		interceptor.attachResponse(response);

		// Handle the request. This will either return the content or a step error
		try {
			interceptor.serviceRequest(request);
			interceptor.preparePaint(request);
			interceptor.paint(new WebXmlRenderContext(response.getWriter()));
		} catch (ContentEscape escape) {
			try {
				// Content has been returned
				escape.setRequest(request);
				escape.setResponse(response);
				escape.escape();
			} catch (IOException e) {
				Assert.fail("Failed to write content");
			}
		} catch (ErrorCodeEscape escape) {
			try {
				escape.setRequest(request);
				escape.setResponse(response);
				escape.escape();
			} catch (IOException e) {
				Assert.fail("Failed to write error content");
			}
		} catch (ActionEscape ignored) {
			// don't care
		}

		// Step error
		return response;
	}

	/**
	 * Some mock content for testing.
	 */
	private static final class MyContent extends WContent {

		/**
		 * The content which will be served up.
		 */
		private static final String CONTENT = "ABC";

		/**
		 * Creates the mock content.
		 */
		private MyContent() {
			MockContentAccess content = new MockContentAccess();
			content.setMimeType("text/plain");
			content.setBytes(CONTENT.getBytes());
			content.setDescription("content");
			setContentAccess(content);
		}
	}

	/**
	 * A test WApplication with content.
	 */
	private static final class MyApp extends WApplication {

		/**
		 * Some arbitrary content added directly to the test application.
		 */
		private final WContent appContent = new MyContent();

		/**
		 * For testing that step errors for content inside a WWindow are handled correctly.
		 */
		private final WWindow window = new WWindow();

		/**
		 * Creates a MyApp.
		 */
		private MyApp() {
			add(appContent);
			add(window);

			window.setContent(new WLabel("test"));
		}
	}
}
