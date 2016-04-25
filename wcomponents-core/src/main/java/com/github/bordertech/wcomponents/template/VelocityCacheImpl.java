package com.github.bordertech.wcomponents.template;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;

/**
 * Velocity caching implementation.
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class VelocityCacheImpl implements ResourceCache {

	/**
	 * Cache name.
	 */
	private static final String CACHE_NAME = "wc-velocity-templates";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(final RuntimeServices rs) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource get(final Object resourceKey) {
		return getCache().get(resourceKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource put(final Object resourceKey, final Resource resource) {
		getCache().put(resourceKey, resource);
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource remove(final Object resourceKey) {
		return getCache().getAndRemove(resourceKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator enumerateKeys() {
		return getCache().iterator();
	}

	/**
	 * @return the cache instance
	 */
	protected synchronized Cache<Object, Resource> getCache() {
		Cache<Object, Resource> cache = Caching.getCache(CACHE_NAME, Object.class, Resource.class);
		if (cache == null) {
			final CacheManager mgr = Caching.getCachingProvider().getCacheManager();
			MutableConfiguration<Object, Resource> config = new MutableConfiguration<>();
			config.setTypes(Object.class, Resource.class);
			config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.HOURS, 12)));
			// Velocity template classes are not serializable so use by ref.
			config.setStoreByValue(false);
			cache = mgr.createCache(CACHE_NAME, config);
		}
		return cache;
	}

}
