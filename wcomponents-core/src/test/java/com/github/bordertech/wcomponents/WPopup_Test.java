package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.NullWriter;
import java.io.PrintWriter;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WPopup_Test - unit tests for {@link WPopup}.
 *
 * @author Anthony O'Connor, Jonathan Austin
 * @since 1.0.0
 */
public class WPopup_Test extends AbstractWComponentTestCase {

	/**
	 * test url.
	 */
	private static final String TEST_URL = "www.testurl.org";
	/**
	 * another test url.
	 */
	private static final String TEST_URL2 = "www.testurl2.org";

	@Test
	public void testEmptyConstructor() {
		WPopup popup = new WPopup();
		Assert.assertNull("URL should be null", popup.getUrl());
	}

	@Test
	public void testConstructorUrl() {
		WPopup popup = new WPopup(TEST_URL);
		Assert.assertEquals("URL should be updated", TEST_URL, popup.getUrl());
	}

	@Test
	public void testSetHeight() {
		final int height = 50;
		final int height2 = 100;
		WPopup popup = new WPopup();

		// Default
		popup.setHeight(height);
		Assert.assertEquals("Height should be set", height, popup.getHeight());

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setHeight(height2);
		Assert.assertEquals("User context height should be set", height2, popup.getHeight());

		resetContext();
		Assert.assertEquals("Default height should not have changed", height, popup.getHeight());
	}

	@Test
	public void testSetWidth() {
		final int width = 50;
		final int width2 = 100;
		WPopup popup = new WPopup();

		// Default
		popup.setWidth(width);
		Assert.assertEquals("Default width should be set", width, popup.getWidth());

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setWidth(width2);
		Assert.assertEquals("User context width should be set", width2, popup.getWidth());

		resetContext();
		Assert.assertEquals("Default width should not have changed", width, popup.getWidth());
	}

	@Test
	public void testSetResizable() {
		final boolean resizable = true;
		final boolean resizable2 = false;
		WPopup popup = new WPopup();

		// Default
		popup.setResizable(resizable);
		Assert.assertEquals("Resizable should be set", popup.isResizable(), resizable);

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setResizable(resizable2);
		Assert.assertEquals("User context resizable should be set", resizable2, popup.isResizable());

		resetContext();
		Assert.assertEquals("Default resizable should not have changed", resizable, popup.
				isResizable());
	}

	@Test
	public void testSetScrollable() {
		final boolean scrollable = true;
		final boolean scrollable2 = false;
		WPopup popup = new WPopup();

		// Default
		popup.setScrollable(scrollable);
		Assert.assertEquals("Default scrollable should be set", scrollable, popup.isScrollable());

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setScrollable(scrollable2);
		Assert.assertEquals("User context scrollable should be set", scrollable2, popup.
				isScrollable());

		resetContext();
		Assert.assertEquals("Default scrollable should not have changed", scrollable, popup.
				isScrollable());
	}

	@Test
	public void testSetUrl() {
		WPopup popup = new WPopup();

		// Default
		popup.setUrl(TEST_URL);
		Assert.assertEquals("URL should be set", TEST_URL, popup.getUrl());

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setUrl(TEST_URL2);
		Assert.assertEquals("User context URL should be set", TEST_URL2, popup.getUrl());

		resetContext();
		Assert.assertEquals("Default URL should not have changed", TEST_URL, popup.getUrl());
	}

	@Test
	public void testSetTargetWindow() {
		final String targetWindow1 = "target window1";
		final String targetWindow2 = "target window2";
		WPopup popup = new WPopup();

		// Default
		popup.setTargetWindow(targetWindow1);
		Assert.assertEquals("Target Window should be set", targetWindow1, popup.getTargetWindow());

		// With user context
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setTargetWindow(targetWindow2);
		Assert.assertEquals("User context Target Window should be set", targetWindow2, popup.
				getTargetWindow());

		resetContext();
		Assert.assertEquals("Default Target Window should not have changed", targetWindow1, popup.
				getTargetWindow());
	}

	@Test
	public void testNotVisibleAfterPaint() {
		WPopup popup = new WPopup();

		// Should default to not visible
		Assert.assertFalse("Popup should not be visible by default", popup.isVisible());

		// Make visible
		popup.setLocked(true);
		setActiveContext(createUIContext());
		popup.setVisible(true);

		// Check not visible after paint
		popup.paint(new WebXmlRenderContext(new PrintWriter(new NullWriter())));
		Assert.assertFalse("Popup should not be visible after paint", popup.isVisible());
	}
}
