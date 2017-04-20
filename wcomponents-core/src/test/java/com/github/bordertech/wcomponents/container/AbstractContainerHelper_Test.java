package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.ActionEscape;
import com.github.bordertech.wcomponents.Environment;
import com.github.bordertech.wcomponents.Escape;
import com.github.bordertech.wcomponents.FatalErrorPage;
import com.github.bordertech.wcomponents.FatalErrorPageFactory;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebComponent;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ContainerHelper_Test - unit tests for {@link AbstractContainerHelper}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AbstractContainerHelper_Test {

	@BeforeClass
	public static void setUp() {
		Config.getInstance().setProperty(ConfigurationProperties.THEME_CONTENT_PATH, "");
		Config.getInstance().setProperty(ConfigurationProperties.DEVELOPER_MODE_CLUSTER_EMULATION, "true");
		Config.getInstance().setProperty(ConfigurationProperties.DEVELOPER_MODE_ERROR_HANDLING, true);
		Config.getInstance().setProperty("bordertech.wcomponents.factory.impl.com.github.bordertech.wcomponents.FatalErrorPageFactory",
				"com.github.bordertech.wcomponents.container.AbstractContainerHelper_Test$MyFatalErrorPageFactory");
		Config.getInstance().setProperty("bordertech.wcomponents.factory.impl.com.github.bordertech.wcomponents.container.PageShell",
				"com.github.bordertech.wcomponents.container.AbstractContainerHelper_Test$MyCustomPageShell");
		TransformXMLTestHelper.reloadTransformer();
	}

	/**
	 * When these tests are done put things back as they were.
	 */
	@AfterClass
	public static void tearDownClass() {
		Config.reset();
		TransformXMLTestHelper.reloadTransformer();
	}

	@Test
	public void testSetWebComponent() {
		AbstractContainerHelper helper = new MyContainerHelper();
		WebComponent webComponent = new WTextField();

		Assert.assertNull("Default interceptor should be null", helper.getInterceptor()); // getUI throws a NPE
		helper.setWebComponent(webComponent);
		Assert.assertSame("Incorrect UI set", webComponent, helper.getUI());

		try {
			helper = new MyContainerHelper();
			helper.setWebComponent(new MyWebComponent());
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertNull("Interceptor should be null", helper.getInterceptor());
		}
	}

	@Test
	public void testGetUI() {
		AbstractContainerHelper helper = new MyContainerHelper();
		WebComponent webComponent = new WTextField();

		helper.setWebComponent(webComponent);
		Assert.assertSame("getUI returned incorrect UI", webComponent, helper.getUI());
	}

	@Test
	public void testProcessAction() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		MyInterceptor interceptor = new MyInterceptor();

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();

		Assert.assertTrue("Action phase not processed", interceptor.serviceRequestCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.preparePaintCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.paintCalled);
	}

	@Test
	public void testProcessActionWhenDisposed() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		MyInterceptor interceptor = new MyInterceptor();

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.dispose();
		helper.processAction();

		Assert.assertFalse("Action phase should not occurr when disposed",
				interceptor.serviceRequestCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.preparePaintCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.paintCalled);
	}

	@Test
	public void testProcessActionWithActionEscape() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		final MyActionEscape escape = new MyActionEscape();

		MyInterceptor interceptor = new MyInterceptor() {
			@Override
			public void serviceRequest(final Request request) {
				super.serviceRequest(request);
				throw escape;
			}
		};

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();

		Assert.assertTrue("Service request should have been called",
				interceptor.serviceRequestCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.preparePaintCalled);
		Assert.assertFalse("Render phase should not have ocurred", interceptor.paintCalled);
		Assert.assertTrue("Helper should be disposed after ActionEscape", helper.isDisposed());
		Assert.assertTrue("ActionEscape should be called during action phase", escape.escapeCalled);
	}

	@Test
	public void testProcessActionWithEscape() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		final MyEscape escape = new MyEscape();

		MyInterceptor interceptor = new MyInterceptor() {
			@Override
			public void serviceRequest(final Request request) {
				super.serviceRequest(request);
				throw escape;
			}
		};

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();

		Assert.assertTrue("Service request should have been called",
				interceptor.serviceRequestCalled);
		Assert.assertFalse("Helper should not be disposed after Escape", helper.isDisposed());
		Assert.assertFalse("Escape should not have been called after action phase",
				escape.escapeCalled);

		helper.render();
		Assert.assertTrue("Helper should be disposed after render", helper.isDisposed());
	}

	@Test
	public void testRender() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		MyInterceptor interceptor = new MyInterceptor();

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();
		helper.render();

		Assert.assertTrue("Action phase not processed", interceptor.serviceRequestCalled);
		Assert.assertTrue("PreparePaint should have been called", interceptor.preparePaintCalled);
		Assert.assertTrue("Paint should have been called", interceptor.paintCalled);
		Assert.assertTrue("Helper should be disposed after render", helper.isDisposed());
	}

	@Test
	public void testErrorDuringActionPhase() throws IOException {
		MyContainerHelper helper = new MyContainerHelper();
		final IllegalStateException error = new IllegalStateException("simulated action failure");

		// Errors should be handled gracefully
		MyInterceptor interceptor = new MyInterceptor() {
			@Override
			public void serviceRequest(final Request request) {
				super.serviceRequest(request);
				throw error;
			}
		};

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();
		helper.render();

		Assert.assertTrue("Action phase not processed", interceptor.serviceRequestCalled);
		Assert.assertFalse("PreparePaint should not have been called",
				interceptor.preparePaintCalled);
		Assert.assertFalse("Paint should not have been called", interceptor.paintCalled);
		Assert.assertTrue("Helper should be disposed after render", helper.isDisposed());

		String output = helper.stringWriter.toString();
		String expected = error.getMessage();
		Assert.assertTrue("Missing error message", output.indexOf(expected) != -1);
	}

	@Test
	public void testErrorDuringRenderPhase() throws IOException {
		// Errors should be handled gracefully
		MyContainerHelper helper = new MyContainerHelper();
		final IllegalStateException error = new IllegalStateException("simulated paint failure");

		MyInterceptor interceptor = new MyInterceptor() {
			@Override
			public void paint(final RenderContext renderContext) {
				super.paint(renderContext);
				throw error;
			}
		};

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.processAction();
		helper.render();

		Assert.assertTrue("Action phase not processed", interceptor.serviceRequestCalled);
		Assert.assertTrue("PreparePaint should have been called", interceptor.preparePaintCalled);
		Assert.assertTrue("Paint should have been called", interceptor.paintCalled);
		Assert.assertTrue("Helper should be disposed after render", helper.isDisposed());

		String output = helper.stringWriter.toString();
		String expected = error.getMessage();
		Assert.assertTrue("Missing error message", output.indexOf(expected) != -1);
	}

	@Test
	public void testRenderWhenDisposed() throws IOException {
		AbstractContainerHelper helper = new MyContainerHelper();
		MyInterceptor interceptor = new MyInterceptor();

		helper.setWebComponent(interceptor);
		helper.prepareUserContext();

		helper.dispose();
		helper.render();

		Assert.assertFalse("PreparePaint should not have been called when disposed",
				interceptor.preparePaintCalled);
		Assert.assertFalse("Paint should not have been called when disposed",
				interceptor.paintCalled);
		Assert.assertTrue("Helper should still be disposed after render", helper.isDisposed());
	}

	@Test(expected = IllegalStateException.class)
	public void testIsNewConversationUnknown() {
		// Will throw an exception until processAction has been called
		new MyContainerHelper().isNewConversation();
	}

	@Test
	public void testIsNewConversation() throws IOException {
		MyContainerHelper helper = new MyContainerHelper();

		helper.prepareUserContext();
		helper.processAction();

		// The default implementation should return false from requestImpliesNew
		Assert.assertFalse("Should not be a new conversation", helper.isNewConversation());
		Assert.assertTrue("Should be a continuing conversation", helper.isContinuingConversation());
	}

	@Test
	public void testHandleErrorDefault() throws IOException {
		MyContainerHelper helper = new MyContainerHelper();
		SystemException error = new SystemException("test");
		helper.handleError(error);
		String output = helper.stringWriter.toString();
		Assert.assertTrue("Should contain default error message", output.contains("currently unavailable"));
	}

	@Test
	public void testHandleErrorCustom() throws IOException {
		MyContainerHelper helper = new MyContainerHelper();
		SystemException error = new SystemException("custom");
		helper.handleError(error);
		String output = helper.stringWriter.toString();
		Assert.assertTrue("Should contain transformed XML", output.contains(TransformXMLTestHelper.EXPECTED));
	}

	/**
	 * A trivial implementation of ContainerHelper for testing.
	 */
	private static final class MyContainerHelper extends AbstractContainerHelper {

		private UIContext uiContext = new AbstractContainerHelper.UIContextWrap();
		private final StringWriter stringWriter = new StringWriter();
		private final Response response = new MockResponse();

		@Override
		protected Environment createEnvironment() {
			return null;
		}

		@Override
		protected Request createRequest() {
			return new MockRequest();
		}

		@Override
		protected PrintWriter getPrintWriter() {
			return new PrintWriter(stringWriter);
		}

		@Override
		protected Response getResponse() {
			return response;
		}

		@Override
		protected UIContext getUIContext() {
			return uiContext;
		}

		@Override
		protected void invalidateSession() {
			// NOP
		}

		@Override
		protected void redirectForLogout() {
			// NOP
		}

		@Override
		protected void setTitle(final String title) {
			// NOP
		}

		@Override
		protected void setUIContext(final UIContext uic) {
			this.uiContext = uic;
		}

		@Override
		protected void updateEnvironment(final Environment env) {
			// NOP
		}

		@Override
		protected void updateRequest(final Request request) {
			// NOP
		}
	}

	/**
	 * A mock WebComponent implementation.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static class MyWebComponent implements WebComponent {

		@Override
		public String getId() {
			return "";
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public void paint(final RenderContext renderContext) {
			// NOP
		}

		@Override
		public void preparePaint(final Request request) {
			// NOP
		}

		@Override
		public void serviceRequest(final Request request) {
			// NOP
		}
	}

	/**
	 * An interceptor that sets flags when certain methods are called.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static class MyInterceptor extends InterceptorComponent {

		private boolean paintCalled;
		private boolean preparePaintCalled;
		private boolean serviceRequestCalled;

		/**
		 * Construct interceptor.
		 */
		MyInterceptor() {
			super(new WLabel("ContainerHelper_Test"));
		}

		@Override
		public void paint(final RenderContext renderContext) {
			paintCalled = true;
			super.paint(renderContext);
		}

		@Override
		public void preparePaint(final Request request) {
			preparePaintCalled = true;
			super.preparePaint(request);
		}

		@Override
		public void serviceRequest(final Request request) {
			serviceRequestCalled = true;
			super.serviceRequest(request);
		}
	}

	/**
	 * An ActionEscape implementation that just sets a flag.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static class MyActionEscape extends ActionEscape {

		/**
		 * A flag to indicate whether the escape method has been called.
		 */
		private boolean escapeCalled = false;

		/**
		 * Sets the escapeCalled flag.
		 */
		@Override
		public void escape() {
			escapeCalled = true;
		}
	}

	/**
	 * An Escape implementation that just sets a flag.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static class MyEscape extends Escape {

		/**
		 * A flag to indicate whether the escape method has been called.
		 */
		private boolean escapeCalled = false;

		/**
		 * Sets the escapeCalled flag.
		 */
		@Override
		public void escape() {
			escapeCalled = true;
		}
	}

	/**
	 * Custom Error Page Factory.
	 */
	public static class MyFatalErrorPageFactory implements FatalErrorPageFactory {

		@Override
		public WComponent createErrorPage(final boolean developerFriendly, final Throwable error) {
			if (error.getMessage().equals("custom")) {
				return new MyCustomErrorPage();
			} else {
				return new FatalErrorPage(developerFriendly, error);
			}
		}
	}

	/**
	 * Custom error page.
	 */
	public static class MyCustomErrorPage extends WText {

		/**
		 * Setup custom error page.
		 */
		public MyCustomErrorPage() {
			super(TransformXMLTestHelper.TEST_XML);
			setEncodeText(false);
		}
	}

	/**
	 * Custom page shell that does not wrap the test component with ui:root as the test xslt does not cater for this.
	 */
	public static class MyCustomPageShell implements PageShell {

		@Override
		public void openDoc(final PrintWriter writer) {
		}

		@Override
		public void writeHeader(final PrintWriter writer) {
		}

		@Override
		public void writeApplicationHeader(final PrintWriter writer) {
		}

		@Override
		public void writeApplicationFooter(final PrintWriter writer) {
		}

		@Override
		public void writeFooter(final PrintWriter writer) {
		}

		@Override
		public void closeDoc(final PrintWriter writer) {
		}
	}
}
