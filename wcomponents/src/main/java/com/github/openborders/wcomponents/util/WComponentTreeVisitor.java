package com.github.openborders.wcomponents.util;

import com.github.openborders.wcomponents.WComponent;

/**
 * A visitor interface used when traversing WComponent trees.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface WComponentTreeVisitor
{
    /**
     * An enumeration used to short-circuit tree traversal for efficiency.
     */
    public enum VisitorResult
    {
        /** Continue tree traversal. */
        CONTINUE,
        /** Continue tree traversal, but not in this branch. */
        ABORT_BRANCH,
        /** Stop tree traversal altogether. */
        ABORT
    }
    
    /**
     * Called for each component in the WComponent hierarchy.
     * 
     * @param comp the component in the tree being observed
     * 
     * @return how the traversal should proceed.
     */
    VisitorResult visit(WComponent comp);
}
