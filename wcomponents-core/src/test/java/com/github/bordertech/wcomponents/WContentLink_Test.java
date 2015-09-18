package com.github.bordertech.wcomponents;

import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WContentLink_Test - unit tests for {@link WContentLink}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WContentLink_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		final String text = "WContentLink_Test.testConstructors";
		final char accessKey = 'W';

		WContentLink contentLink = new WContentLink();
		Assert.assertNull("Incorrect link text", contentLink.getText());

		contentLink = new WContentLink(text);
		Assert.assertEquals("Incorrect link text", text, contentLink.getText());

		contentLink = new WContentLink(text, accessKey);
		Assert.assertEquals("Incorrect link text", text, contentLink.getText());
	}

	@Test
	public void testSetContentAccess() {
		MockContentAccess content = new MockContentAccess();

		WContentLink contentLink = new WContentLink();
		contentLink.setLocked(true);

		setActiveContext(new UIContextImpl());
		contentLink.setContentAccess(content);
		Assert.assertSame("Incorrect content access returned", content, contentLink.
				getContentAccess());

		resetContext();
		Assert.assertNull("ContentAccess should be null by default", contentLink.getContentAccess());
	}

	@Test
	public void testSetText() {
		String text1 = "text1";
		String text2 = "text2";

		WContentLink contentLink = new WContentLink();

		contentLink.setText(text1);
		Assert.assertEquals("Incorrect default text returned", text1, contentLink.getText());

		contentLink.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect text returned with uic", text1, contentLink.getText());

		contentLink.setText(text2);
		Assert.assertEquals("Incorrect text returned with uic", text2, contentLink.getText());

		resetContext();
		Assert.assertEquals("Incorrect default text returned", text1, contentLink.getText());
	}

	@Test
	public void testSetWidth() {
		int width1 = 123;
		int width2 = 456;

		WContentLink contentLink = new WContentLink();

		contentLink.setWidth(width1);
		Assert.assertEquals("Incorrect width returned", width1, contentLink.getWidth());

		contentLink.setWidth(width2);
		Assert.assertEquals("Incorrect width returned", width2, contentLink.getWidth());

		contentLink.setLocked(true);
	}

	@Test
	public void testSetHeight() {
		int height1 = 123;
		int height2 = 456;

		WContentLink contentLink = new WContentLink();

		contentLink.setHeight(height1);
		Assert.assertEquals("Incorrect height returned", height1, contentLink.getHeight());

		contentLink.setHeight(height2);
		Assert.assertEquals("Incorrect height returned", height2, contentLink.getHeight());
	}

	@Test
	public void testSetResizable() {
		WContentLink contentLink = new WContentLink();
		Assert.assertTrue("Should be resizable by default", contentLink.isResizable());

		contentLink.setResizable(false);
		Assert.assertFalse("Incorrect value for resizable after setResizable(false) called",
				contentLink.isResizable());

		contentLink.setResizable(true);
		Assert.assertTrue("Incorrect value for resizable after setResizable(true) called",
				contentLink.isResizable());
	}

	@Test
	public void testSetRenderAsButton() {
		WContentLink contentLink = new WContentLink();
		Assert.
				assertFalse("Should not be RenderAsButton by default", contentLink.
						isRenderAsButton());

		contentLink.setRenderAsButton(true);
		Assert.assertTrue("Incorrect value for RenderAsButton after setRenderAsButton(true) called",
				contentLink.isRenderAsButton());

		contentLink.setRenderAsButton(false);
		Assert.assertFalse(
				"Incorrect value for RenderAsButton after setRenderAsButton(false) called",
				contentLink.isRenderAsButton());
	}

	@Test
	public void testSetCacheKey() throws IOException {
		final String defaultKey = "DEFAULT KEY";
		final String testKey = "TEST KEY";

		WContentLink contentLink = new WContentLink();
		Assert.assertNull("CacheKey should be null by default", contentLink.getCacheKey());

		contentLink.setCacheKey(defaultKey);
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey,
				contentLink.getCacheKey());

		contentLink.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect value returned for default cache key with user context",
				defaultKey,
				contentLink.getCacheKey());

		contentLink.setCacheKey(testKey);
		Assert.assertEquals("Incorrect value returned for cache key with user context", testKey,
				contentLink.getCacheKey());

		resetContext();
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey,
				contentLink.getCacheKey());
	}

	@Test
	public void testSetHidden() {
		WContentLink contentLink = new WContentLink();
		Assert.assertFalse("Should not be hidden by default", contentLink.isHidden());

		contentLink.setLocked(true);
		setActiveContext(createUIContext());
		contentLink.setHidden(true);
		Assert.assertTrue("Incorrect value for hidden after setHidden(true) called", contentLink.
				isHidden());

		contentLink.setHidden(false);
		Assert.assertFalse("Incorrect value for hidden after setHidden(uic, false) called",
				contentLink.isHidden());

		resetContext();
		Assert.assertFalse("Should not be hidden by default", contentLink.isHidden());
	}

	@Test
	public void testSetDisabled() {
		WContentLink contentLink = new WContentLink();
		Assert.assertFalse("Should not be disabled by default", contentLink.isDisabled());

		contentLink.setLocked(true);
		setActiveContext(createUIContext());
		contentLink.setDisabled(true);
		Assert.assertTrue("Incorrect value for disabled after setDisabled(true) called",
				contentLink.isDisabled());

		contentLink.setDisabled(false);
		Assert.assertFalse("Incorrect value for disabled after setDisabled(false) called",
				contentLink.isDisabled());

		resetContext();
		Assert.assertFalse("Should not be disabled by default", contentLink.isDisabled());
	}
}
