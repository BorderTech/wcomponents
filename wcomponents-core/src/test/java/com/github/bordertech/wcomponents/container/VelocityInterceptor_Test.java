package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.apache.velocity.VelocityContext;
import org.junit.Test;

/**
 * VelocityComponent_Test - unit tests for {@link VelocityInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class VelocityInterceptor_Test extends AbstractWComponentTestCase {

	private static final String TEST_KEY = "VelocityComponent_Test_key";
	private static final String TEST_VALUE = "VelocityComponent_Test_value";

	@Test
	public void testPaint() {
		setActiveContext(createUIContext());

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		RenderContext renderContext = new WebXmlRenderContext(printWriter);
		MyVelocityComponent component = new MyVelocityComponent();

		component.setTemplate(
				"com/github/bordertech/wcomponents/container/VelocityComponent_Test1.vm");
		component.paint(renderContext);
		String renderedFormat = stringWriter.toString().trim();
		Assert.assertEquals("Incorrect rendered format", TEST_VALUE, renderedFormat);

		// Errors in a template should not throw an exception, or render anything
		// Method called using reflection that results in a MethodInvocationException
		component.setTemplate(
				"com/github/bordertech/wcomponents/container/VelocityComponent_Test2.vm");
		stringWriter.getBuffer().setLength(0);
		component.paint(renderContext);
		renderedFormat = stringWriter.toString().trim();
		Assert.assertEquals("Incorrect rendered format", "", renderedFormat);

		// Template that #includes a non-existant template
		component.setTemplate(
				"com/github/bordertech/wcomponents/container/VelocityComponent_Test3.vm");
		stringWriter.getBuffer().setLength(0);
		component.paint(renderContext);
		renderedFormat = stringWriter.toString().trim();
		Assert.assertEquals("Incorrect rendered format", "", renderedFormat);
	}

	/**
	 * An extension of VelocityComponent which ensures that a test object is placed in the context.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class MyVelocityComponent extends VelocityInterceptor {

		@Override
		protected void fillContext(final VelocityContext context) {
			super.fillContext(context);
			context.put(TEST_KEY, new TestObject());
		}
	}

	/**
	 * A test object containing a "bad method" that throws an exception. This class needs to be public so that the
	 * methods can be used from a Velocity template.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static final class TestObject {

		/**
		 * @return nothing as will throw exception
		 */
		public boolean badMethod() {
			throw new IllegalStateException("Bad method");
		}

		@Override
		public String toString() {
			return TEST_VALUE;
		}
	}
}
