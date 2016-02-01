package com.github.bordertech.wcomponents.template;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.TemplateSource;
import java.io.IOException;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;

/**
 *
 * @author jonathan
 */
public class HandlebarsCacheImpl implements TemplateCache {

	/**
	 * Cache name.
	 */
	private static final String CACHE_NAME = "handlebars-templates";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		getCache().clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void evict(final TemplateSource source) {
		getCache().remove(source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Template get(final TemplateSource source, final Parser parser) throws IOException {
		Template template = getCache().get(source);
		if (template == null) {
			template = parser.parse(source);
			getCache().put(source, template);
		}
		return template;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TemplateCache setReload(final boolean reload) {
		// Dont need to support auto reload
		return this;
	}

	/**
	 * @return the cache instance
	 */
	protected synchronized Cache<TemplateSource, Template> getCache() {
		Cache<TemplateSource, Template> cache = Caching.getCache(CACHE_NAME, TemplateSource.class, Template.class);
		if (cache == null) {
			final CacheManager mgr = Caching.getCachingProvider().getCacheManager();
			MutableConfiguration<TemplateSource, Template> config = new MutableConfiguration<>();
			config.setTypes(TemplateSource.class, Template.class);
			config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(Duration.ONE_HOUR));
			// Handlebars template classes are not serializable so use by ref.
			config.setStoreByValue(false);
			cache = mgr.createCache(CACHE_NAME, config);
		}
		return cache;
	}

}
