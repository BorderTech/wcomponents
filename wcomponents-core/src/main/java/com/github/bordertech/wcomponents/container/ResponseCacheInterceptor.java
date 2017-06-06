package com.github.bordertech.wcomponents.container;

import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.util.ConfigurationProperties;

/**
 * Interceptor that sets the appropriate response headers for caching or not caching the response.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ResponseCacheInterceptor extends InterceptorComponent {

	/**
	 * Cache Type allows different cache settings to be set for different response types, such as page responses,
	 * content, theme resources and datalists.
	 * <p>
	 * All the cache types uses the same default settings. However, the default setting can be overridden or each
	 * individual cache type can be overridden.
	 * </p>
	 */
	public enum CacheType {
		/**
		 * Page and AJAX responses no cache.
		 */
		NO_CACHE("page.nocache", false),
		/**
		 * Theme cache.
		 */
		THEME_CACHE("theme.cache", true),
		/**
		 * Datalist no cache.
		 */
		DATALIST_CACHE("datalist.cache", true),
		/**
		 * Content cache.
		 */
		CONTENT_CACHE("content.cache", true),
		/**
		 * Content no cache.
		 */
		CONTENT_NO_CACHE("content.nocache", false);

		/**
		 * Parameter key.
		 */
		private final String param;

		/**
		 * Caching flag.
		 */
		private final boolean cache;

		/**
		 * @param param the parameter key
		 * @param cache the caching flag
		 */
		CacheType(final String param, final boolean cache) {
			this.param = param;
			this.cache = cache;
		}

		/**
		 * @return the cache settings
		 */
		public String getSettings() {
			String cacheSettings = ConfigurationProperties.getResponseCacheHeaderSettings(param);
			return (cacheSettings == null) ? getDefaultSettings() : cacheSettings;
		}

		/**
		 * @return true if cache, otherwise false
		 */
		public boolean isCache() {
			return cache;
		}

		/**
		 * @return the default settings
		 */
		private String getDefaultSettings() {
			if (isCache()) {
				return ConfigurationProperties.getResponseCacheSettings();
			} else {
				return ConfigurationProperties.getResponseNoCacheSettings();
			}

		}
	}

	/**
	 * Caching type.
	 */
	private final CacheType type;

	/**
	 * @param type the caching type
	 */
	public ResponseCacheInterceptor(final CacheType type) {
		this.type = type;
	}

	/**
	 * Set the appropriate response headers for caching or not caching the response.
	 *
	 * @param renderContext the rendering context
	 */
	@Override
	public void paint(final RenderContext renderContext) {
		// Set headers
		getResponse().setHeader("Cache-Control", type.getSettings());

		// Add extra no cache headers
		if (!type.isCache()) {
			getResponse().setHeader("Pragma", "no-cache");
			getResponse().setHeader("Expires", "-1");
		}

		super.paint(renderContext);
	}
}
