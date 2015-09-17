package com.github.bordertech.wcomponents.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Helper class used to provide common functionality for <code>Comparator</code> instances used by e.g. table sorting.
 *
 * @author Adam Millard
 */
public abstract class AbstractComparator implements Comparator, Serializable {

	/**
	 * Subclasses must implement this method to obtain a derived comparable to use for comparison. A trivial
	 * implementation would be to return the object itself if it is already a {@link Comparable}.
	 *
	 * @param obj the object to compare.
	 * @return the comparable to compare for the given object.
	 */
	protected abstract Comparable getComparable(Object obj);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final Object obj1, final Object obj2) {
		Comparable<?> comparable1 = getComparable(obj1);
		Comparable<?> comparable2 = getComparable(obj2);

		return Util.compareAllowNull(comparable1, comparable2);
	}
}
