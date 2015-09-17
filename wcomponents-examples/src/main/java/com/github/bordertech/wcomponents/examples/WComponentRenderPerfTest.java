package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import com.github.bordertech.wcomponents.util.ObjectGraphDump;
import com.github.bordertech.wcomponents.util.ObjectGraphNode;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A WComponent performance test using the KitchenSink example. Each test is run in a fresh VM.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class WComponentRenderPerfTest {

	/**
	 * The class to use for the rendering test.
	 */
	private static final String CLASS_TO_TEST = "com.github.bordertech.wcomponents.examples.KitchenSink";

	/**
	 * This prefix is used to limit the amount of text being piped from the child processes.
	 */
	private static final String LINE_PREFIX = "WComponentRenderPerfTest:";

	/**
	 * A list of all the test methods. This list is used when the program is launched with no command line args.
	 */
	private static final String[] TESTS = {"runDefaultUIManagerImpl"};

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WComponentRenderPerfTest.class);

	/**
	 * The number of renders to perform.
	 */
	private static final int NUM_RENDERS = 10000;

	/**
	 * This class should not be instantiated.
	 */
	private WComponentRenderPerfTest() {
	}

	/**
	 * Entry point for the KitchenSinkPerfTest.
	 *
	 * @param args the command line arguments
	 *
	 * @throws Exception an exception
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 1) {
			// Run one test
			try {
				Method method = WComponentRenderPerfTest.class.getMethod(args[0], new Class[0]);
				method.invoke(null, null);
			} catch (Exception e) {
				LOG.info(e.toString());
				e.printStackTrace(System.out);
			} finally {
				System.exit(0);
			}
		} else {
			// Run all tests
			for (int i = 0; i < TESTS.length; i++) {
				launchTest(TESTS[i]);
			}
		}
	}

	/**
	 * We need to launch each test in a fresh VM to accurately determine memory usage.
	 *
	 * @param testName the name of the test method to run.
	 * @throws IOException if there is an error running the test.
	 */
	private static void launchTest(final String testName) throws IOException {
		String javaRuntime = System.getProperty("java.home")
				+ File.separatorChar + "bin"
				+ File.separatorChar + "java";

		String classPath = System.getProperty("java.class.path");
		String className = WComponentRenderPerfTest.class.getName();

		LOG.info("Running " + className + "." + testName);
		LOG.info(javaRuntime + " -cp " + classPath + ' ' + className + ' ' + testName);

		Process process = Runtime.getRuntime().exec(new String[]{
			javaRuntime,
			"-cp", classPath,
			className,
			testName
		});

		InputStream stdout = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

		// Pipe the input from the process to the logger
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			int index = line.indexOf(LINE_PREFIX);

			if (index != -1) {
				line = line.substring(index + LINE_PREFIX.length());
				LOG.info(line);
			}
		}
	}

	/**
	 * @return the amount of java heap space used, in bytes.
	 */
	private static long getHeapUsed() {
		for (int i = 0; i < 5; i++) {
			try {
				System.gc();
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}

		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	/**
	 * Runs the render test.
	 */
	private static void runRenderTest() {
		UIContextImpl uic = new UIContextImpl();
		PrintWriter printWriter = new PrintWriter(new NullWriter());
		RenderContext renderContext = new WebXmlRenderContext(printWriter);
		WComponent component = null;

		long baseLineMemory = getHeapUsed();

		try {
			component = (WComponent) Class.forName(CLASS_TO_TEST).newInstance();
		} catch (Exception e) {
			String msg = "Unable to instantiate test component: " + CLASS_TO_TEST;
			LOG.error(LINE_PREFIX + msg);
			throw new SystemException(msg, e);
		}

		long memBeforePaint = getHeapUsed() - baseLineMemory;

		// Set up velocity etc. to obtain a memory reading, and
		// so that the performance results aren't skewed too much
		UIContextHolder.pushContext(uic);

		try {
			component.paint(renderContext);
			long memAfterOnePaint = getHeapUsed() - baseLineMemory;

			long startTime = System.currentTimeMillis();

			// Figure out the loop overhead
			for (int i = 0; i < NUM_RENDERS; i++) {
			}

			long loopOverhead = System.currentTimeMillis() - startTime;
			startTime = System.currentTimeMillis();

			// Now run the render test
			for (int i = 0; i < NUM_RENDERS; i++) {
				component.paint(renderContext);
			}

			long elapsedTime = System.currentTimeMillis() - startTime - loopOverhead;

			long memAfterAllPaints = getHeapUsed() - baseLineMemory;

			LOG.info(LINE_PREFIX + "Memory use before paint: " + memBeforePaint);
			LOG.info(LINE_PREFIX + "Memory use after 1 paint: " + memAfterOnePaint);
			LOG.info(
					LINE_PREFIX + "Memory use after " + NUM_RENDERS + " paints: " + memAfterAllPaints);
			LOG.info(LINE_PREFIX + "Render time: " + (elapsedTime / (double) NUM_RENDERS) + "ms");

			Object[] treeAndSession = new Object[]{component, uic};
			ObjectGraphNode root = ObjectGraphDump.dump(treeAndSession);

			LOG.info(LINE_PREFIX + "Component mem use: " + ((ObjectGraphNode) root.getChildAt(0)).
					getSize());
			LOG.info(LINE_PREFIX + "UIC mem use: " + ((ObjectGraphNode) root.getChildAt(1)).
					getSize());
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * Runs the test with the default UI Manager configuration.
	 */
	public static void runDefaultUIManagerImpl() {
		runRenderTest();
	}
}
