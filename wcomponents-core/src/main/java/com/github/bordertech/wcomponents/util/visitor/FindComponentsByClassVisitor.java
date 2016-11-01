package com.github.bordertech.wcomponents.util.visitor;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.Util;
import java.util.ArrayList;
import java.util.List;

/**
 * A tree visitor implementation which finds components in the tree with a given class.
 *
 * @author Jonathan Austin
 * @since 1.2.10
 * @see TreeUtil
 */
public class FindComponentsByClassVisitor extends AbstractVisitorWithResult<List<ComponentWithContext>> {

	/**
	 * The root component being searched from.
	 */
	private final WComponent root;

	/**
	 * The className the class name to search for.
	 */
	private final String className;

	/**
	 * True if the root should be also be tested for a match, false if not.
	 */
	private final boolean includeRoot;

	/**
	 * Find components of this class name including the root component being searched from.
	 *
	 * @param className the class name to search for. The package name may be omitted for convenience.
	 */
	public FindComponentsByClassVisitor(final String className) {
		this(null, className, true);
	}

	/**
	 * Find components of this class name excluding the root component from the search.
	 *
	 * @param rootToIgnore the root component to ignore
	 * @param className the class name to search for. The package name may be omitted for convenience.
	 */
	public FindComponentsByClassVisitor(final WComponent rootToIgnore, final String className) {
		this(rootToIgnore, className, false);
	}

	/**
	 * Creates a FindComponentByClassVisitor.
	 *
	 * @param root the root component being searched from.
	 * @param className the class name to search for. The package name may be omitted for convenience.
	 * @param includeRoot true if the root should be also be tested for a match, false if not.
	 */
	public FindComponentsByClassVisitor(final WComponent root, final String className,
			final boolean includeRoot) {
		this.setResult(new ArrayList<ComponentWithContext>());
		this.className = className;
		this.root = root;
		this.includeRoot = includeRoot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorResult visit(final WComponent comp) {

		// Check if root component should be included (if ignore then just continue)
		if (!includeRoot && comp == root) {
			return VisitorResult.CONTINUE;
		}

		boolean match = false;
		// Check inherited classes
		outer:
		for (Class<?> clazz = comp.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			// Check if inherited class name is a match
			if (classNamesMatch(clazz.getName(), className)) {
				match = true;
				break;
			}
			// Check interfaces
			for (Class intClazz : clazz.getInterfaces()) {
				if (classNamesMatch(intClazz.getName(), className)) {
					match = true;
					break outer;
				}
			}
		}
		if (match) {
			boolean cont = handleFoundMatch(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
			return cont ? VisitorResult.CONTINUE : VisitorResult.ABORT;
		}
		return VisitorResult.CONTINUE;
	}

	/**
	 * @param comp the matching component to class name
	 * @return true to continue processing
	 */
	protected boolean handleFoundMatch(final ComponentWithContext comp) {
		getResult().add(comp);
		return true;
	}

	/**
	 * Tests two class names for equality, taking partical class names (no package given) into account.
	 *
	 * @param name1 the first class name to compare.
	 * @param name2 the second class name to compare.
	 * @return true if the class names match, false otherwise.
	 */
	protected boolean classNamesMatch(final String name1, final String name2) {
		if (name1 == null || name2 == null) {
			return false;
		}
		int index1 = name1.lastIndexOf('.');
		int index2 = name2.lastIndexOf('.');
		if (index1 == -1 && index2 != -1) {
			return Util.equals(name1, name2.substring(index2 + 1));
		} else if (index1 != -1 && index2 == -1) {
			return Util.equals(name1.substring(index1 + 1), name2);
		} else {
			return Util.equals(name1, name2);
		}
	}

}
