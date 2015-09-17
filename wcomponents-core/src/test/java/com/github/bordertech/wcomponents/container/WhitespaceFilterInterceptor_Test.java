package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * WhitespaceFilterInterceptor_Test - unit tests for {@link WhitespaceFilterInterceptor}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WhitespaceFilterInterceptor_Test extends AbstractWComponentTestCase {

	@After
	public void resetConfig() {
		Config.reset();
	}

	@Test
	public void testPaint() {
		final String testString = "    foo    bar    ";
		final String filteredString = " foo bar ";

		WLabel label = new WLabel(testString);
		WhitespaceFilterInterceptor interceptor = new WhitespaceFilterInterceptor();
		interceptor.attachUI(label);
		label.setLocked(true);
		setActiveContext(createUIContext());

		// Test when disabled
		Config.getInstance().setProperty("bordertech.wcomponents.whitespaceFilter.enabled", "false");

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		interceptor.paint(new WebXmlRenderContext(printWriter));
		printWriter.close();

		Assert.assertTrue("Should not have filtered text when disabled", writer.toString().contains(
				testString));

		// Test when enabled
		Config.getInstance().setProperty("bordertech.wcomponents.whitespaceFilter.enabled", "true");

		writer = new StringWriter();
		printWriter = new PrintWriter(writer);
		interceptor.paint(new WebXmlRenderContext(printWriter));
		printWriter.close();

		Assert.assertTrue("Should have filtered text when enabled", writer.toString().contains(
				filteredString));
	}
}
