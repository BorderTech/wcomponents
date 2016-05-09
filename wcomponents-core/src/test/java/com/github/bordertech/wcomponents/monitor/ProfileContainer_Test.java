package com.github.bordertech.wcomponents.monitor;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ProfileContainer}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class ProfileContainer_Test extends AbstractWComponentTestCase {

	/**
	 * template for first header line of expected text from afterPaint.
	 */
	private static final String PROFILER_UIC_HEADER = "<h2>Serialization Profile of UIC</h2>";

	/**
	 * template for first line of expected text from afterPaint.
	 */
	private static final String PROFILER_LINE1 = "<dt>Total root wcomponents found in UIC</dt><dd><<NUM_ROOTS>></dd>";

	/**
	 * template for second line of expected text from afterPaint.
	 */
	private static final String PROFILER_LINE2 = "<strong>Number of components in tree:</strong> <<NUM_COMPONENTS>>";

	/**
	 * template for third line of expected text from afterPaint.
	 */
	private static final String PROFILER_LINE3 = "<td><<CLASS_NAME>></td><td>Serializable</td>";

	/**
	 * template for second header line of expected text from afterPaint.
	 */
	private static final String PROFILER_PROFILE_HEADER = "<h2>ObjectProfiler - <<CLASS_NAME>>";

	/**
	 * Test afterPaint.
	 */
	@Test
	public void testAfterPaint() {
		ProfileContainer app = new ProfileContainer();
		app.setLocked(true);

		UIContext uic = new UIContextImpl();
		uic.setUI(app);
		setActiveContext(uic);

		WButton button = new WButton("PUSH");
		app.add(button);
		WLabel label = new WLabel("HERE");
		app.add(label);

		StringWriter outStr = new StringWriter();
		PrintWriter writer = new PrintWriter(outStr);
		RenderContext renderContext = new WebXmlRenderContext(writer);

		app.afterPaint(renderContext);

		// expecting 1 root class, 3 components, class names as shown, profiler
		// class
		String profileLine0 = PROFILER_UIC_HEADER;
		String profileLine1 = PROFILER_LINE1.replaceAll("<<NUM_ROOTS>>", "1");
		String profileLine2 = PROFILER_LINE2.replaceAll("<<NUM_COMPONENTS>>", "4");
		String profileLine31 = PROFILER_LINE3.replaceAll("<<CLASS_NAME>>",
				"com.github.bordertech.wcomponents.WButton");
		String profileLine32 = PROFILER_LINE3.replaceAll("<<CLASS_NAME>>",
				"com.github.bordertech.wcomponents.monitor.ProfileContainer");
		String profileLine33 = PROFILER_LINE3.replaceAll("<<CLASS_NAME>>",
				"com.github.bordertech.wcomponents.WLabel");
		String profileLine4 = PROFILER_PROFILE_HEADER.replaceAll("<<CLASS_NAME>>",
				"com.github.bordertech.wcomponents.monitor.ProfileContainer");

		String[] expectedResults = {profileLine0, profileLine1, profileLine2, profileLine31, profileLine32,
			profileLine33, profileLine4};

		String result = outStr.toString();
		for (int i = 0; i < expectedResults.length; i++) {
			Assert.assertTrue("result should contain substring " + i + "  ", result.indexOf(
					expectedResults[i]) != -1);
		}
	}
}
