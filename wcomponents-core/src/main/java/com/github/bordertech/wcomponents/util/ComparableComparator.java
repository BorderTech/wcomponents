package com.github.bordertech.wcomponents.util;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.lang3.ObjectUtils;

/**
 * A simple comparator which compares comparables.
 *
 * @author Yiannis Paschalidis
 */
public final class ComparableComparator implements Comparator<Comparable<?>>, Serializable {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final Comparable<?> obj1, final Comparable<?> obj2) {
		return ObjectUtils.compare((Comparable) obj1, (Comparable) obj2);
	}
}
