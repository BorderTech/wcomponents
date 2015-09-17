package com.github.bordertech.wcomponents.examples.transientcontainer;

import java.util.HashMap;
import java.util.Map;

/**
 * A dodgy cache.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class DummyApplicationCache {

	/**
	 * The cache map.
	 */
	private static final Map<String, Object> MAP = new HashMap<>();

	/**
	 * Prevent instantiation.
	 */
	private DummyApplicationCache() {
	}

	/**
	 * Adds an object to the cache.
	 *
	 * @param key the cache key.
	 * @param value the value.
	 */
	public static void put(final String key, final Object value) {
		MAP.put(key, value);
	}

	/**
	 * Retrieves an object from the cache.
	 *
	 * @param key the cache key.
	 * @return the cached object.
	 */
	public static Object get(final String key) {
		return MAP.get(key);
	}
}
