package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.WComponent;

/**
 * A visitor interface used when traversing WComponent trees.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public interface WComponentTreeVisitor {

	/**
	 * An enumeration used to short-circuit tree traversal for efficiency.
	 */
	enum VisitorResult {
		/**
		 * Continue tree traversal.
		 */
		CONTINUE,
		/**
		 * Continue tree traversal, but not in this branch.
		 */
		ABORT_BRANCH,
		/**
		 * Stop tree traversal altogether.
		 */
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
