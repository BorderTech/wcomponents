package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.container.ResponseCacheInterceptor.CacheType;
import com.github.bordertech.wcomponents.render.webxml.AbstractWebXmlRendererTestCase;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.Config;
import com.github.bordertech.wcomponents.util.mock.MockResponse;
import junit.framework.Assert;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

/**
 * ResponseCacheInterceptor_Test - unit tests for {@link ResponseCacheInterceptor}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ResponseCacheInterceptor_Test extends AbstractWebXmlRendererTestCase {

	@Test
	public void testCache() {
		// Create interceptor
		ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(CacheType.CONTENT_CACHE);
		interceptor.setBackingComponent(new WText());

		// Mock Response
		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);

		// Render phase
		interceptor.paint(new WebXmlRenderContext(response.getWriter()));

		Assert
				.assertEquals("Cache-Control header not set correctly for CACHE",
						ResponseCacheInterceptor.DEFAULT_CACHE_SETTINGS, response.getHeaders().get(
								"Cache-Control"));
		Assert.assertNull("Pragma header should be null for CACHE", response.getHeaders().get(
				"Pragma"));
		Assert.assertNull("Expires header should be null for CACHE", response.getHeaders().get(
				"Expires"));
	}

	@Test
	public void testNoCache() {
		// Create interceptor
		ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(
				CacheType.CONTENT_NO_CACHE);
		interceptor.setBackingComponent(new WText());

		// Mock Response
		MockResponse response = new MockResponse();
		interceptor.attachResponse(response);

		// Render phase
		interceptor.paint(new WebXmlRenderContext(response.getWriter()));

		Assert.assertEquals("Cache-Control header not set correctly for NO_CACHE",
				ResponseCacheInterceptor.DEFAULT_NO_CACHE_SETTINGS,
				response.getHeaders().get("Cache-Control"));
		Assert.assertEquals("Pragma header not set correctly for NO_CACHE", "no-cache",
				response.getHeaders().get("Pragma"));
		Assert
				.assertEquals("Expires header not set correctly for NO_CACHE", "-1", response.
						getHeaders().get("Expires"));
	}

	@Test
	public void testOverrideDefaultCache() {
		/**
		 * Original config.
		 */
		Configuration originalConfig;
		originalConfig = Config.getInstance();

		String override = "OVERRIDE CACHE";

		try {
			// Test override cache settings
			Configuration config = Config.copyConfiguration(originalConfig);
			config.setProperty("bordertech.wcomponents.response.header.default.cache", override);
			Config.setConfiguration(config);

			// Create interceptor
			ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(
					CacheType.CONTENT_CACHE);
			interceptor.setBackingComponent(new WText());

			// Mock Response
			MockResponse response = new MockResponse();
			interceptor.attachResponse(response);

			// Render phase
			interceptor.paint(new WebXmlRenderContext(response.getWriter()));

			// Check Override
			Assert.assertEquals("Cache-Control header not overriden correctly for CACHE", override,
					response
					.getHeaders().get("Cache-Control"));

		} finally {
			// Remove overrides
			Config.setConfiguration(originalConfig);
		}
	}

	@Test
	public void testOverrideDefaultNoCache() {
		/**
		 * Original config.
		 */
		Configuration originalConfig;
		originalConfig = Config.getInstance();

		String override = "OVERRIDE NO CACHE";

		try {
			// Test override cache settings
			Configuration config = Config.copyConfiguration(originalConfig);
			config.setProperty("bordertech.wcomponents.response.header.default.nocache", override);
			Config.setConfiguration(config);

			// Create interceptor
			ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(
					CacheType.CONTENT_NO_CACHE);
			interceptor.setBackingComponent(new WText());

			// Mock Response
			MockResponse response = new MockResponse();
			interceptor.attachResponse(response);

			// Render phase
			interceptor.paint(new WebXmlRenderContext(response.getWriter()));

			// Check Override
			Assert.assertEquals("Cache-Control header not overriden correctly for NO CACHE",
					override, response
					.getHeaders().get("Cache-Control"));

		} finally {
			// Remove overrides
			Config.setConfiguration(originalConfig);
		}

	}

	@Test
	public void testOverrideContentCache() {
		/**
		 * Original config.
		 */
		Configuration originalConfig;
		originalConfig = Config.getInstance();

		String override = "OVERRIDE CONTENT CACHE";

		try {
			// Test override cache settings
			Configuration config = Config.copyConfiguration(originalConfig);
			config.setProperty("bordertech.wcomponents.response.header.content.cache", override);
			Config.setConfiguration(config);

			// Create interceptor
			ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(
					CacheType.CONTENT_CACHE);
			interceptor.setBackingComponent(new WText());

			// Mock Response
			MockResponse response = new MockResponse();
			interceptor.attachResponse(response);

			// Render phase
			interceptor.paint(new WebXmlRenderContext(response.getWriter()));

			// Check Override
			Assert.assertEquals("Cache-Control header not overriden correctly for CONTENT CACHE",
					override, response
					.getHeaders().get("Cache-Control"));

		} finally {
			// Remove overrides
			Config.setConfiguration(originalConfig);
		}
	}

	@Test
	public void testOverridePageNoCache() {
		/**
		 * Original config.
		 */
		Configuration originalConfig;
		originalConfig = Config.getInstance();

		String override = "OVERRIDE CONTENT NO CACHE";

		try {
			// Test override cache settings
			Configuration config = Config.copyConfiguration(originalConfig);
			config.setProperty("bordertech.wcomponents.response.header.content.nocache", override);
			Config.setConfiguration(config);

			// Create interceptor
			ResponseCacheInterceptor interceptor = new ResponseCacheInterceptor(
					CacheType.CONTENT_NO_CACHE);
			interceptor.setBackingComponent(new WText());

			// Mock Response
			MockResponse response = new MockResponse();
			interceptor.attachResponse(response);

			// Render phase
			interceptor.paint(new WebXmlRenderContext(response.getWriter()));

			// Check Override
			Assert.assertEquals("Cache-Control header not overriden correctly for CONTENT NO CACHE",
					override, response
					.getHeaders().get("Cache-Control"));

		} finally {
			// Remove overrides
			Config.setConfiguration(originalConfig);
		}

	}

}
