package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link FatalErrorPage}.
 *
 * @author Anthony O'Connor
 * @since 1.0.0
 */
public class FatalErrorPage_Test extends AbstractWComponentTestCase {

	@Test
	public void testPaintComponentNotDeveloperFriendly() {
		setActiveContext(createUIContext());

		TestSampleException exception = new TestSampleException("sample exception only");
		FatalErrorPage fatalErrPage = new FatalErrorPage(false, exception);
		String correctMsg = fatalErrPage.getMessage();

		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		fatalErrPage.paintComponent(new WebXmlRenderContext(writer));

		Assert.assertTrue("Should equal the contents of getMessage()", strWriter.toString().equals(
				correctMsg + System.getProperty("line.separator")));
	}

	@Test
	public void testPaintComponentDeveloperFriendly() {
		setActiveContext(createUIContext());

		TestSampleException exception = new TestSampleException("sample exception only");
		FatalErrorPage fatalErrPage = new FatalErrorPage(true, exception);
		String correctMsg = fatalErrPage.getMessage();

		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		fatalErrPage.paintComponent(new WebXmlRenderContext(writer));

		String result = strWriter.toString();

		Assert.assertTrue("should contain contents of getMessage()",
				result.indexOf(correctMsg) != -1);
		Assert.assertTrue("should contain the name of the Exception", result.indexOf(exception.
				getClass().getName()) != -1);
	}
}
