package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.AllComponents;
import com.github.bordertech.wcomponents.PerformanceTests;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Response;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.container.InterceptorComponent;
import com.github.bordertech.wcomponents.servlet.ServletUtil;
import com.github.bordertech.wcomponents.servlet.WServlet;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import com.github.bordertech.wcomponents.util.mock.servlet.MockHttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Tests to check the performance of WComponent XML rendering. This test does not check that the XML output is correct -
 * see the tests for each Renderer.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
@Category(PerformanceTests.class)
public class WebXmlRenderingPerformance_Test extends AbstractWComponentTestCase {

	/**
	 * The number of repetitions to use for testing serialization time. This should be set to be greater than the
	 * minimum number of invocations required to trigger JIT compilation.
	 */
	private static final int NUM_REPETITIONS = 2000;

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WebXmlRenderingPerformance_Test.class);

	@Test
	public void testRenderingPerformance() throws Exception {
		final AllComponents component = new AllComponents();
		final UIContext uic = createUIContext();
		sendRequest(component, uic);

		// Render and store the XML to compare against
		setActiveContext(uic);
		StringWriter tempStringWriter = new StringWriter();
		PrintWriter tempPrintWriter = new PrintWriter(tempStringWriter);
		component.paint(new WebXmlRenderContext(tempPrintWriter));
		tempPrintWriter.close();
		resetContext();

		// Run the test writing raw bytes to a writer - no computation necessary
		final byte[] xml = tempStringWriter.toString().getBytes("UTF-8");
		final CountingNullPrintWriter nullWriter = new CountingNullPrintWriter();

		LOG.info("Rendered UI size: " + xml.length + " bytes");

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < NUM_REPETITIONS; i++) {
					for (int j = 0; j < xml.length; j++) {
						nullWriter.write(xml[j]);
					}
				}
			}
		};

		// JIT warm-up
		runnable.run();
		nullWriter.resetCount();

		long rawTime = time(runnable) / NUM_REPETITIONS;
		Assert.assertEquals("Incorrect amount of raw data written", xml.length * NUM_REPETITIONS,
				nullWriter.getCount());
		nullWriter.resetCount();

		// Run the test using WComponent rendering
		runnable = new Runnable() {
			@Override
			public void run() {
				setActiveContext(uic);

				for (int i = 0; i < NUM_REPETITIONS; i++) {
					component.paint(new WebXmlRenderContext(nullWriter));
				}
			}
		};

		// JIT warm-up
		runnable.run();
		nullWriter.resetCount();

		long renderTime = time(runnable) / NUM_REPETITIONS;
		Assert.assertEquals("Incorrect amount of rendered data written",
				xml.length * NUM_REPETITIONS, nullWriter.getCount());
		nullWriter.resetCount();

		LOG.info("Raw write time: " + (rawTime / 1000000.0) + "ms");
		LOG.info("WComponent render time: " + (renderTime / 1000000.0) + "ms");
		Assert.assertTrue("WComponent render time should not exceed 5x raw write time",
				renderTime < rawTime * 5);
	}

	@Test
	public void testRenderingScaling() throws Exception {
		final RenderContext renderContext = new WebXmlRenderContext(
				new PrintWriter(new NullWriter()));

		final AllComponents component1 = new AllComponents();
		final UIContext uic1 = createUIContext();
		sendRequest(component1, uic1);

		final AllComponents component10 = new AllComponents(10);
		final UIContext uic10 = createUIContext();
		sendRequest(component10, uic10);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				setActiveContext(uic1);

				for (int i = 0; i < NUM_REPETITIONS; i++) {
					component1.paint(renderContext);
				}
			}
		};

		// JIT warm-up
		runnable.run();

		long renderTime1 = time(runnable) / NUM_REPETITIONS;

		runnable = new Runnable() {
			@Override
			public void run() {
				setActiveContext(uic10);

				for (int i = 0; i < NUM_REPETITIONS; i++) {
					component10.paint(renderContext);
				}
			}
		};

		// JIT warm-up
		runnable.run();

		long renderTime10 = time(runnable) / NUM_REPETITIONS;

		LOG.info("Render 1x time: " + (renderTime1 / 1000000.0) + "ms");
		LOG.info("Render 10x time: " + (renderTime10 / 1000000.0) + "ms");
		Assert.assertTrue("Render time scaling should be O(n)", renderTime10 < renderTime1 * 12); // TODO: Figure out why this doesn't scale nicely.
	}

	/**
	 * Invokes WComponent request processing, so that this test case can more closely match a production scenario.
	 *
	 * @param comp the component to invoke request processing on.
	 * @param uic the user context to use.
	 */
	private void sendRequest(final WComponent comp, final UIContext uic) {
		PrintWriter writer = new PrintWriter(new NullWriter());
		uic.setEnvironment(new WServlet.WServletEnvironment("", "http://localhost", ""));
		uic.setUI(comp);

		InterceptorComponent root = ServletUtil.createInterceptorChain(new MockHttpServletRequest());
		root.attachUI(comp);

		Response response = new MockResponse();
		root.attachResponse(response);

		setActiveContext(uic);
		MockRequest request = new MockRequest();

		try {
			root.serviceRequest(request);
			root.preparePaint(request);
			root.paint(new WebXmlRenderContext(writer));
		} finally {
			resetContext();
		}
	}

	/**
	 * A null writer which counts the amount of data written.
	 */
	private static final class CountingNullPrintWriter extends PrintWriter {

		/**
		 * The number of characters written to the writer.
		 */
		private static int count;

		/**
		 * Creates the writer.
		 */
		private CountingNullPrintWriter() {
			super(new Writer() {
				@Override
				public void write(final char[] cbuf, final int off, final int len) {
					count += len;
				}

				@Override
				public void flush() {
				}

				@Override
				public void close() {
				}
			});
		}

		/**
		 * @return the number of characters written to the writer.
		 */
		public int getCount() {
			return count;
		}

		/**
		 * Resets the counter.
		 */
		public void resetCount() {
			count = 0;
		}
	}
}
