package com.github.dibp.wcomponents.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A simple comparator which compares comparables.
 * @author Yiannis Paschalidis 
 */
public final class ComparableComparator implements Comparator<Comparable<?>>, Serializable
{
    /** {@inheritDoc} */
    public int compare(final Comparable<?> obj1, final Comparable<?> obj2) 
    {
        return Util.compareAllowNull(obj1, obj2);
    }
}
