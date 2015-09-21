package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ContextCleanupInterceptor_Test - unit tests for {@link ContextCleanupInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ContextCleanupInterceptor_Test extends AbstractWComponentTestCase {

	@Test
	public void testPaint() {
		MyWLabel label = new MyWLabel();
		label.setText("Hello world");
		InterceptorComponent interceptor = new ContextCleanupInterceptor();
		interceptor.setBackingComponent(label);

		StringWriter writer = new StringWriter();
		setActiveContext(createUIContext());
		interceptor.paint(new WebXmlRenderContext(new PrintWriter(writer)));

		// After the paint, the component should have been painted, and the context cleared up
		Assert.assertTrue("tidyUpUIContextForTree was not called", label.tidiedUp);
		Assert.assertTrue("Component was not painted",
				writer.toString().indexOf(label.getText()) != -1);
	}

	/**
	 * A WLabel extension that sets a flag when tidyUpUIContextForTree is called.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class MyWLabel extends WLabel {

		private boolean tidiedUp = false;

		@Override
		public void tidyUpUIContextForTree() {
			super.tidyUpUIContextForTree();
			tidiedUp = true;
		}
	}
}
