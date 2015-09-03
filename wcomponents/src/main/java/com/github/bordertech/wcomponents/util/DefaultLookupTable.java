package com.github.bordertech.wcomponents.util; 

import java.util.List;

/** 
 * This default implementation of {@link LookupTable} 
 * does not provide any look-up tables.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class DefaultLookupTable implements LookupTable
{
    /** {@inheritDoc} */
    public List<?> getTable(final Object table)
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getCacheKeyForTable(final Object table)
    {
        return null;
    }

    /** {@inheritDoc} */
    public Object getTableForCacheKey(final String key)
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getCode(final Object table, final Object entry)
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getDescription(final Object table, final Object entry)
    {
        return null;
    }
}
