package com.github.bordertech.wcomponents.util;

import java.util.List;

/**
 * The LookupTable interface describes a mechanism for providing WComponents with data sourced from an application
 * look-up table. Applications must implement this class and provide configure the Factory by setting the following
 * parameter in the application's {@link Config Configuration}:
 *
 * <code>factory.impl.com.github.bordertech.wcomponents.util.LookupTable=<i>com.something.MyLookupTable</i></code>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface LookupTable {

	/**
	 * Retrieves the contents of a given table by a table identifier. This identifier can be any object appropriate to
	 * your application.
	 *
	 * @param table the table to look up.
	 * @return a list of table objects.
	 */
	List<?> getTable(Object table);

	/**
	 * <p>
	 * Retrieves the cache id for the given table. This is used to enable caching of data sets, including on the
	 * client.</p>
	 *
	 * <p>
	 * If your data sets are session- or user-specific, your cache id must include this information so that the cache is
	 * invalidated.</p>
	 *
	 * <p>
	 * A null return value indicates that the table is not cacheable.</p>
	 *
	 * @param table the table to look up.
	 * @return the cache key for the given table, or null if the table is not cacheable.
	 */
	String getCacheKeyForTable(Object table);

	/**
	 * Retrieves the source table for the cache key.
	 *
	 * @param key the table's cache key, as returned by {@link #getCacheKeyForTable(Object)}.
	 * @return the table to lookup.
	 */
	Object getTableForCacheKey(String key);

	/**
	 * Retrieves the code for a given table entry, or null if the type of object is not supported.
	 *
	 * @param table the source table
	 * @param entry the table entry.
	 * @return the code for the given entry.
	 */
	String getCode(Object table, Object entry);

	/**
	 * Retrieves the description for a given table entry, or null if the type of object is not supported.
	 *
	 * @param table the source table.
	 * @param entry the table entry.
	 * @return the description for the given entry.
	 */
	String getDescription(Object table, Object entry);
}
