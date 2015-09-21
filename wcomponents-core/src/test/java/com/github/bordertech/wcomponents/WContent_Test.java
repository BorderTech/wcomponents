package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WContent_Test - unit tests for {@link WContent}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WContent_Test extends AbstractWComponentTestCase {

	/**
	 * The character encoding to use when converting Strings to/from bytes.
	 */
	private static final String CHAR_ENCODING = "UTF-8";

	@Test
	public void testSetContentAccess() {
		WContent wContent = new WContent();
		MockContentAccess content = new MockContentAccess();

		wContent.setLocked(true);
		setActiveContext(createUIContext());
		wContent.setContentAccess(content);
		Assert.assertSame("Incorrect content access returned", content, wContent.getContentAccess());

		resetContext();
		Assert.assertNull("ContentAccess should be null by default", wContent.getContentAccess());
	}

	@Test
	public void testSetWidth() {
		String width1 = "123px";
		String width2 = "456px";

		WContent wContent = new WContent();

		wContent.setWidth(width1);
		Assert.assertEquals("Incorrect width returned", width1, wContent.getWidth());

		wContent.setWidth(width2);
		Assert.assertEquals("Incorrect width returned", width2, wContent.getWidth());
	}

	@Test
	public void testSetHeight() {
		String height1 = "123px";
		String height2 = "456px";

		WContent wContent = new WContent();

		wContent.setHeight(height1);
		Assert.assertEquals("Incorrect height returned", height1, wContent.getHeight());

		wContent.setHeight(height2);
		Assert.assertEquals("Incorrect height returned", height2, wContent.getHeight());
	}

	@Test
	public void testSetResizable() {
		WContent wContent = new WContent();
		Assert.assertTrue("Should be resizable by default", wContent.isResizable());

		wContent.setResizable(false);
		Assert.assertFalse("Incorrect value for resizable after setResizable(false) called",
				wContent.isResizable());

		wContent.setResizable(true);
		Assert.assertTrue("Incorrect value for resizable after setResizable(true) called", wContent.
				isResizable());
	}

	@Test
	public void testSetCacheKey() throws IOException {
		final String defaultKey = "DEFAULT KEY";
		final String testKey = "TEST KEY";

		WContent wContent = new WContent();
		Assert.assertNull("CacheKey should be null by default", wContent.getCacheKey());

		wContent.setCacheKey(defaultKey);
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey, wContent.
				getCacheKey());

		wContent.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect value returned for default cache key with user context",
				defaultKey, wContent.getCacheKey());

		wContent.setCacheKey(testKey);
		Assert.assertEquals("Incorrect value returned for cache key with user context", testKey,
				wContent.getCacheKey());

		resetContext();
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey, wContent.
				getCacheKey());
	}

	@Test
	public void testSetDisplay() throws IOException {
		WContent wContent = new WContent();
		setActiveContext(createUIContext());

		Assert.assertFalse("Display flag should be false by default", wContent.isDisplayRequested());

		wContent.display();
		Assert.assertTrue("Display flag should be true", wContent.isDisplayRequested());
	}

	@Test
	public void testPaintContent() throws IOException {
		byte[] data = "WContent_Test.testPaint".getBytes(CHAR_ENCODING);

		MockContentAccess content = new MockContentAccess();
		content.setBytes(data);

		WContent wContent = new WContent();

		setActiveContext(createUIContext());
		wContent.setContentAccess(content);

		MockRequest request = new MockRequest();
		request.setParameter(Environment.TARGET_ID, wContent.getTargetId());

		// Should produce the content
		try {
			wContent.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(data, CHAR_ENCODING),
					output);
			Assert.assertFalse("Cache flag should not be set", escape.isCacheable());
			Assert.assertEquals("Response should have header set for no caching",
					ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
					response.getHeaders().get("Cache-Control"));
		}

		// Test Cached Response
		wContent.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			wContent.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(data, CHAR_ENCODING),
					output);
			Assert.assertTrue("Cache flag should be set", escape.isCacheable());
			Assert
					.assertEquals("Response should have header set for caching",
							ResponseCacheInterceptor.DEFAULT_CACHE_SETTINGS, response.getHeaders().
							get("Cache-Control"));
		}

		// Test with streamed content
		MockContentStreamAccess contentStream = new MockContentStreamAccess();
		contentStream.setBytes(data);
		wContent.setContentAccess(contentStream);

		try {
			wContent.handleRequest(request);
			Assert.fail("Should have thrown a content escape");
		} catch (ContentEscape escape) {
			MockResponse response = new MockResponse();
			escape.setResponse(response);
			escape.escape();

			String output = new String(response.getOutput(), CHAR_ENCODING);
			Assert.assertEquals("Incorrect content returned", new String(data, CHAR_ENCODING),
					output);
		}
	}
}
