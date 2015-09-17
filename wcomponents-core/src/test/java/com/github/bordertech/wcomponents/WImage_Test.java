package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WImage_Test - unit tests for {@link WImage}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WImage_Test extends AbstractWComponentTestCase {

	/**
	 * The character encoding to use when converting Strings to/from byte arrays.
	 */
	private static final String CHAR_ENCODING = "UTF-8";

	@Test
	public void testSetImage() {
		MockImage sessionImage = new MockImage();
		MockImage defaultImage = new MockImage();

		WImage image = new WImage();
		image.setImage(defaultImage);

		image.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertSame("Default image should be returned when no user specific image set",
				defaultImage, image.getImage());
		Assert.assertTrue("Should be in default state when no user specific image set", image.
				isDefaultState());

		setActiveContext(createUIContext());
		image.setImage(sessionImage);
		Assert.assertSame("Session image should be returned when set", sessionImage, image.
				getImage());
		Assert.assertFalse("Should not be in default state when session image set", image.
				isDefaultState());

		resetContext();
		Assert.assertSame("Default image should not be changed", defaultImage, image.getImage());
	}

	@Test
	public void testSetCacheKey() throws IOException {
		final String defaultKey = "DEFAULT KEY";
		final String testKey = "TEST KEY";

		WImage image = new WImage();
		Assert.assertNull("CacheKey should be null by default", image.getCacheKey());

		image.setCacheKey(defaultKey);
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey, image.
				getCacheKey());

		image.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertEquals("Incorrect value returned for default cache key with user context",
				defaultKey, image.getCacheKey());

		image.setCacheKey(testKey);
		Assert.assertEquals("Incorrect value returned for cache key with user context", testKey,
				image.getCacheKey());

		resetContext();
		Assert.assertEquals("Incorrect value returned for default cache key", defaultKey, image.
				getCacheKey());
	}

	@Test
	public void testStaticImageUrl() {
		WImage image1 = new WImage("/image/x1.gif", "text1");
		WImage image2 = new WImage("/image/x1.gif", "text2");
		WImage image3 = new WImage("/image/x2.gif", "text2");

		WContainer container = new WContainer();
		container.add(image1);
		container.add(image2);
		container.add(image3);

		setActiveContext(createUIContext());
		Assert.assertEquals("Image urls should match for the same image", image1.getTargetUrl(),
				image2.getTargetUrl());
		Assert.assertFalse("Image urls should differ for different images", image1.getTargetUrl().
				equals(image3.getTargetUrl()));
	}

	@Test
	public void testHandleRequest()
			throws IOException {
		byte[] data = "WImage_Test.testHandleRequest".getBytes(CHAR_ENCODING);

		MockRequest request = new MockRequest();
		MockImage content = new MockImage();
		content.setBytes(data);

		WImage image = new WImage();
		image.setLocked(true);

		setActiveContext(createUIContext());
		image.setImage(content);

		// Should not do anything when target is not present
		image.handleRequest(request);

		try {
			request.setParameter(Environment.TARGET_ID, image.getTargetId());
			image.handleRequest(request);
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
		image.setCacheKey("key");

		// Should produce the content with cache flag set
		try {
			image.handleRequest(request);
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
	}
}
