package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Window_Test - unit tests for {@link WWindow}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WWindow_Test extends AbstractWComponentTestCase {

	@Test
	public void testSetWidth() {
		assertAccessorsCorrect(new WWindow(), "width", 800, 123, 456);
	}

	@Test
	public void testSetHeight() {
		assertAccessorsCorrect(new WWindow(), "height", 600, 123, 456);
	}

	@Test
	public void testSetContent() {
		final WComponent content1 = new WTextField();
		final WComponent content2 = new WTextField();

		WWindow window = new WWindow();
		window.setContent(content1);
		Assert.assertSame("Incorrect content returned", content1, window.getContent());
		WComponent parentWindow = WebUtilities.getAncestorOfClass(WWindow.class, content1);
		Assert.assertSame("Content's parent window is incorrect", window, parentWindow);

		window.setContent(new WLabel());
		window = new WWindow(content1);

		Assert.assertSame("Incorrect content returned", content1, window.getContent());
		parentWindow = WebUtilities.getAncestorOfClass(WWindow.class, content1);
		Assert.assertSame("Content's parent window is incorrect", window, parentWindow);

		window.setContent(content2);
		Assert.assertSame("Incorrect content returned", content2, window.getContent());
		Assert.assertNull("Old content's parent should be null", content1.getParent());
		parentWindow = WebUtilities.getAncestorOfClass(WWindow.class, content2);
		Assert.assertSame("Content's parent window is incorrect", window, parentWindow);
	}

	@Test
	public void testSetResizable() {
		WWindow window = new WWindow();
		Assert.assertTrue("Window should be resizable by default", window.isResizable());

		window.setResizable(false);
		Assert.assertFalse("Window should not be resizable after setResizable(false)", window.
				isResizable());

		window.setResizable(true);
		Assert.assertTrue("Window should be resizable after setResizable(true)", window.
				isResizable());
	}

	@Test
	public void testSetScrollbar() {
		WWindow window = new WWindow();
		Assert.assertFalse("Window should not have scrollbar by default", window.isScrollable());

		window.setScrollable(true);
		Assert.assertTrue("Window should have scrollbar after setScrollable(true)", window.
				isScrollable());

		window.setScrollable(false);
		Assert.assertFalse("Window should not have scrollbar after setScrollable(false)", window.
				isScrollable());

	}

	@Test
	public void testPaint() throws SAXException, IOException {
		final int width = 123;
		final int height = 456;

		MockRequest request = new MockRequest();
		StringWriter writer = new StringWriter();

		MockContainer content = new MockContainer();
		WWindow window = new WWindow();
		window.setContent(content);
		window.setWidth(width);
		window.setHeight(height);
		window.setScrollable(true);
		window.setLocked(true);

		// Test paint when hidden
		setActiveContext(createUIContext());
		window.handleRequest(request);
		window.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		Assert.assertEquals("Window should be hidden by default", "", writer.toString());
		Assert.assertEquals("Content should not have been painted", 0, content.getPaintCount());

		// Test paint when displaying
		window.handleRequest(request);
		window.display();
		window.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		String xhtml = writer.toString();
		Assert.assertTrue("Window should have emitted tag", xhtml.indexOf("<ui:popup") != -1);
		Assert.assertTrue("Incorrect window width", xhtml.indexOf("width=\"" + width) != -1);
		Assert.assertTrue("Incorrect window height", xhtml.indexOf("height=\"" + height) != -1);
		Assert.assertTrue("Window should be resizable", xhtml.indexOf("resizable=\"true") != -1);
		Assert.assertEquals("Content should not have been painted", 0, content.getPaintCount());

		writer.getBuffer().setLength(0);

		// Test paint when targetted
		request.setParameter(WWindow.WWINDOW_REQUEST_PARAM_KEY, window.getId());
		window.handleRequest(request);
		window.paint(new WebXmlRenderContext(new PrintWriter(writer)));
		Assert.assertEquals("Content should have been painted", 1, content.getPaintCount());
	}
}
