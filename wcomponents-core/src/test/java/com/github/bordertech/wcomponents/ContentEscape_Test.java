package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 * ContentEscape_Test - unit tests for {@link ContentEscape}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ContentEscape_Test {

	@Test
	public void testEscapeNoContent() throws IOException {
		// ContentEscape with no content
		MockResponse response = new MockResponse();
		ContentEscape contentEscape = new ContentEscape(null);
		contentEscape.setResponse(response);
		contentEscape.escape();

		Assert.assertEquals("Content length should be zero", 0, response.getOutput().length);
		Assert.assertNull("Mime type should be null", response.getContentType());
		Assert.assertTrue("Headers should be empty", response.getHeaders().isEmpty());
	}

	@Test
	public void testEscape() throws IOException {
		MockContentAccess contentAccess = new MockContentAccess();
		contentAccess.setBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 9, 8, 7, 6, 5, 4, 3, 2, 1});
		contentAccess.setMimeType("ContentEscape_Test/mimeType");

		MockResponse response = new MockResponse();
		ContentEscape contentEscape = new ContentEscape(contentAccess);
		contentEscape.setResponse(response);
		contentEscape.escape();

		Assert.assertTrue("Incorrect content written", Arrays.equals(contentAccess.getBytes(),
				response.getOutput()));
		Assert.assertEquals("Incorrect mime type set", contentAccess.getMimeType(), response.
				getContentType());
		Assert.assertFalse("Content-Disposition header should not be set", response.getHeaders().
				containsKey("Content-Disposition"));
		Assert.assertEquals("Response should have header set for no caching",
				ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
				response.getHeaders().get("Cache-Control"));

		// ContentEscape with content and description
		contentAccess.setDescription("ContentEscape_Test.contentAccessDescription");

		response = new MockResponse();
		contentEscape.setResponse(response);
		contentEscape.escape();

		Assert.assertTrue("Incorrect content written", Arrays.equals(contentAccess.getBytes(),
				response.getOutput()));
		Assert.assertEquals("Incorrect mime type set", contentAccess.getMimeType(), response.
				getContentType());
		Assert.assertEquals("Incorrect content-Disposition", "inline; filename=" + contentAccess.
				getDescription(), response.getHeaders().get("Content-Disposition"));

		// ContentEscape with Cacheable True
		contentEscape.setCacheable(true);
		response = new MockResponse();
		contentEscape.setResponse(response);
		contentEscape.escape();

		Assert
				.assertEquals("Response should have header set for caching",
						ResponseCacheInterceptor.DEFAULT_CACHE_SETTINGS, response.getHeaders().get(
								"Cache-Control"));
	}

	@Test
	public void testSetCacheable() throws IOException {
		MockContentAccess contentAccess = new MockContentAccess();
		ContentEscape contentEscape = new ContentEscape(contentAccess);

		Assert.assertFalse("Cacheable flag should be false by default", contentEscape.isCacheable());
		contentEscape.setCacheable(true);
		Assert.assertTrue("Cacheable flag should be true", contentEscape.isCacheable());
	}
}
