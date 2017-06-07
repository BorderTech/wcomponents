package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.NamingContextable;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WInvisibleContainer;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WRepeater.WRepeatRoot;
import com.github.bordertech.wcomponents.WWindow;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.WComponentTreeVisitor.VisitorResult;
import com.github.bordertech.wcomponents.util.visitor.AbstractVisitorWithResult;
import com.github.bordertech.wcomponents.util.visitor.FindComponentByIdVisitor;
import com.github.bordertech.wcomponents.util.visitor.FindComponentsByClassVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for navigating WComponent trees.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class TreeUtil {

	/**
	 * Prevent instantiation of utility class.
	 */
	private TreeUtil() {
	}

	/**
	 * Obtains a list of components which are visible in the given tree. Repeated components will be returned multiple
	 * times, one for each row which they are visible in.
	 *
	 * @param comp the root component to search from.
	 * @return a list of components which are visible in the given context.
	 */
	public static List<ComponentWithContext> collateVisibles(final WComponent comp) {
		final List<ComponentWithContext> list = new ArrayList<>();

		WComponentTreeVisitor visitor = new WComponentTreeVisitor() {
			@Override
			public VisitorResult visit(final WComponent comp) {
				// In traversing the tree, special components like WInvisbleContainer, WRepeatRoot are still traversed
				// (so ignore them)
				if (comp.isVisible()) {
					list.add(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
				}
				return VisitorResult.CONTINUE;
			}
		};

		traverseVisible(comp, visitor);

		return list;
	}

	/**
	 * Retrieves the root component of a WComponent hierarchy.
	 *
	 * @param uic the context to retrieve the root component for.
	 * @param comp a component in the tree.
	 * @return the root of the tree.
	 */
	public static WComponent getRoot(final UIContext uic, final WComponent comp) {
		UIContextHolder.pushContext(uic);

		try {
			return WebUtilities.getTop(comp);
		} finally {
			UIContextHolder.popContext();
		}
	}

	/**
	 * Search for components implementing a particular class name.
	 * <p>
	 * Only search visible components and include the root component in the matching logic.
	 * </p>
	 *
	 * @param root the root component to search from
	 * @param className the class name to search for
	 *
	 * @return the list of components implementing the class name
	 */
	public static List<ComponentWithContext> findComponentsByClass(final WComponent root, final String className) {
		return findComponentsByClass(root, className, true, true);
	}

	/**
	 * Search for components implementing a particular class name.
	 *
	 * @param root the root component to search from
	 * @param className the class name to search for
	 * @param includeRoot check the root component as well
	 * @param visibleOnly true if process visible only
	 *
	 * @return the list of components implementing the class name
	 */
	public static List<ComponentWithContext> findComponentsByClass(final WComponent root, final String className,
			final boolean includeRoot, final boolean visibleOnly) {

		FindComponentsByClassVisitor visitor = new FindComponentsByClassVisitor(root, className, includeRoot);

		doTraverse(root, visibleOnly, visitor);

		return visitor.getResult() == null ? Collections.EMPTY_LIST : visitor.getResult();
	}

	/**
	 * Retrieves the component with the given Id.
	 * <p>
	 * Searches visible and not visible components.
	 * </p>
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @return the component with the given id, or null if not found.
	 */
	public static WComponent getComponentWithId(final WComponent root, final String id) {
		return getComponentWithId(root, id, false);
	}

	/**
	 * Retrieves the component with the given Id.
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @param visibleOnly true if process visible only
	 * @return the component with the given id, or null if not found.
	 */
	public static WComponent getComponentWithId(final WComponent root, final String id,
			final boolean visibleOnly) {
		ComponentWithContext comp = getComponentWithContextForId(root, id, visibleOnly);
		return comp == null ? null : comp.getComponent();
	}

	/**
	 * Retrieves the context for the component with the given Id.
	 * <p>
	 * Searches visible and not visible components.
	 * </p>
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @return the context for the component with the given id, or null if not found.
	 */
	public static UIContext getContextForId(final WComponent root, final String id) {
		return getContextForId(root, id, false);
	}

	/**
	 * Retrieves the context for the component with the given Id.
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @param visibleOnly true if process visible only
	 * @return the context for the component with the given id, or null if not found.
	 */
	public static UIContext getContextForId(final WComponent root, final String id,
			final boolean visibleOnly) {
		ComponentWithContext comp = getComponentWithContextForId(root, id, visibleOnly);
		return comp == null ? null : comp.getContext();
	}

	/**
	 * Retrieves the context for the component with the given Id.
	 * <p>
	 * Searches visible and not visible components.
	 * </p>
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @return the context for the component with the given id, or null if not found.
	 */
	public static ComponentWithContext getComponentWithContextForId(final WComponent root,
			final String id) {
		return getComponentWithContextForId(root, id, false);
	}

	/**
	 * Retrieves the context for the component with the given Id.
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @param visibleOnly true if process visible only
	 * @return the context for the component with the given id, or null if not found.
	 */
	public static ComponentWithContext getComponentWithContextForId(final WComponent root,
			final String id,
			final boolean visibleOnly) {
		FindComponentByIdVisitor visitor = new FindComponentByIdVisitor(id);
		doTraverse(root, visibleOnly, visitor);
		return visitor.getResult();
	}

	/**
	 * Retrieves the closest context for the component with the given Id.
	 * <p>
	 * Searches visible and not visible components.
	 * </p>
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @return the closest context for the component with the given id, or null if not found.
	 */
	public static UIContext getClosestContextForId(final WComponent root, final String id) {
		return getClosestContextForId(root, id, false);
	}

	/**
	 * Retrieves the closest context for the component with the given Id.
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @param visibleOnly true if process visible only
	 * @return the closest context for the component with the given id, or null if not found.
	 */
	public static UIContext getClosestContextForId(final WComponent root, final String id,
			final boolean visibleOnly) {

		FindComponentByIdVisitor visitor = new FindComponentByIdVisitor(id) {
			@Override
			public VisitorResult visit(final WComponent comp) {
				VisitorResult result = super.visit(comp);
				if (result == VisitorResult.CONTINUE) {
					// Save closest UIC as processing tree
					setResult(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
				}
				return result;
			}
		};

		doTraverse(root, visibleOnly, visitor);

		return visitor.getResult() == null ? null : visitor.getResult().getContext();
	}

	/**
	 * Check if this ID is focusable.
	 * <p>
	 * Considered focusable if the component and all its ancestors are visible and not hidden.
	 * </p>
	 *
	 * @param root the root component to search from.
	 * @param id the id to search for.
	 * @return the component with context if it is focusable, otherwise null
	 */
	public static boolean isIdFocusable(final WComponent root, final String id) {

		FindComponentByIdVisitor visitor = new FindComponentByIdVisitor(id) {
			@Override
			public VisitorResult visit(final WComponent comp) {
				VisitorResult result = super.visit(comp);
				// If hidden then abort branch
				if (result == VisitorResult.CONTINUE && comp.isHidden()) {
					return VisitorResult.ABORT_BRANCH;
				}
				return result;
			}
		};

		// Only traverse visible
		doTraverse(root, true, visitor);

		// Check if matching component is hidden
		ComponentWithContext result = visitor.getResult();
		return result == null ? false : !result.getComponent().isHidden();
	}

	/**
	 * General utility method to visit every WComponent in the tree, taking repeaters etc. into account.
	 *
	 * @param node the node to traverse.
	 * @param visitor the visitor to notify as the tree is traversed.
	 */
	public static void traverse(final WComponent node, final WComponentTreeVisitor visitor) {
		doTraverse(node, false, visitor);
	}

	/**
	 * General utility method to visit every visible WComponent in the tree, taking repeaters etc. into account.
	 *
	 * @param node the node to traverse.
	 * @param visitor the visitor to notify as the tree is traversed.
	 */
	public static void traverseVisible(final WComponent node, final WComponentTreeVisitor visitor) {
		doTraverse(node, true, visitor);
	}

	/**
	 * Internal implementation of tree traversal method.
	 *
	 * @param node the node to traverse.
	 * @param visibleOnly if true, only visit visible components.
	 * @param visitor the visitor to notify as the tree is traversed.
	 * @return how the traversal should continue.
	 */
	private static VisitorResult doTraverse(final WComponent node, final boolean visibleOnly,
			final WComponentTreeVisitor visitor) {
		if (visibleOnly) {
			// Push through Invisible Containers
			// Certain components have their visibility altered to implement custom processing.
			if (node instanceof WInvisibleContainer) {
				WComponent parent = node.getParent();
				// If inside a CardManager, skip the InvisibleContainer and process the visible card.
				if (parent instanceof WCardManager) {
					WComponent visible = ((WCardManager) node.getParent()).getVisible();
					if (visible == null) {
						return VisitorResult.ABORT_BRANCH;
					}
					return doTraverse(visible, visibleOnly, visitor);
				} else if (parent instanceof WWindow) { // If inside a WWindow, only process if it is Active
					// Abort branch if WWindow is not in ACTIVE state
					if (((WWindow) parent).getState() != WWindow.ACTIVE_STATE) {
						return VisitorResult.ABORT_BRANCH;
					}
				}
			} else if (node instanceof WRepeatRoot) {
				// Let be processed.
			} else if (!node.isVisible()) {
				// For most components, we just need to see if they're marked as visible
				return VisitorResult.ABORT_BRANCH;
			}
		}

		VisitorResult result = visitor.visit(node);

		switch (result) {

			case ABORT_BRANCH:
				// Continue processing, but not down this branch
				return VisitorResult.CONTINUE;

			case CONTINUE:
				// Process repeater rows
				if (node instanceof WRepeater) {
					// Get parent repeater
					WRepeater repeater = (WRepeater) node;
					// Get row contexts
					List<UIContext> rowContextList = repeater.getRowContexts();
					WRepeatRoot repeatRoot = (WRepeatRoot) repeater.getRepeatedComponent().getParent();

					for (UIContext rowContext : rowContextList) {
						UIContextHolder.pushContext(rowContext);

						try {
							result = doTraverse(repeatRoot, visibleOnly, visitor);
						} finally {
							UIContextHolder.popContext();
						}

						if (VisitorResult.ABORT.equals(result)) {
							return VisitorResult.ABORT;
						}
					}
				} else if (node instanceof Container) {
					Container container = (Container) node;

					for (int i = 0; i < container.getChildCount(); i++) {
						result = doTraverse(container.getChildAt(i), visibleOnly, visitor);

						if (VisitorResult.ABORT.equals(result)) {
							return VisitorResult.ABORT;
						}
					}
				}

				return VisitorResult.CONTINUE;

			default:
				// Abort entire traversal
				return VisitorResult.ABORT;
		}
	}

	/**
	 * Retrieves WComponents by their path in the WComponent tree. See {@link #findWComponents(WComponent, String[])}
	 * for a description of paths.
	 * <p>
	 * Searches only visible components.
	 * </p>
	 *
	 * @param component the component to search from.
	 * @param path the path to the WComponent.
	 * @return the component matching the given path, or null if not found.
	 */
	public static ComponentWithContext[] findWComponents(final WComponent component, final String[] path) {
		return findWComponents(component, path, true);
	}

	/**
	 * Retrieves WComponents by their path in the WComponent tree.
	 * <p>
	 * Paths are specified using class names, starting from the furthest ancestor. To reduce the path lengths, class
	 * names do not need to be fully-qualified. The path does not need to explicitly state intermediate components
	 * between components, and may include an index suffix to select a particular instance of a component in e.g. a
	 * repeater or a set of fields. Some example paths are shown below.
	 * </p>
	 * Example paths.
	 * <dl>
	 * <dt><code>{ "MyComponent" }</code></dt>
	 * <dd>Matches the first instance of MyComponent.</dd>
	 * <dt><code>{ "MyComponent[0]" }</code></dt>
	 * <dd>Also matches the first instance of MyComponent.</dd>
	 * <dt><code>{ "MyComponent[1]" }</code></dt>
	 * <dd>Matches the second instance of MyComponent.</dd>
	 * <dt><code>{ "MyPanel", "MyComponent" }</code></dt>
	 * <dd>Matches the first instance of MyComponent which is nested anywhere under a MyPanel.</dd>
	 * <dt><code>{ "MyApp", "MyPanel", "MyComponent" }</code></dt>
	 * <dd>Matches the first instance of MyComponent, nested within a MyPanel, which is in turn nested somewhere within
	 * a MyApp.</dd>
	 * </dl>
	 *
	 * @param component the component to search from.
	 * @param path the path to the WComponent.
	 * @param visibleOnly visible only
	 * @return the component matching the given path, or null if not found.
	 */
	public static ComponentWithContext[] findWComponents(final WComponent component,
			final String[] path, final boolean visibleOnly) {
		UIContext uic = UIContextHolder.getCurrent();
		if (uic == null) {
			throw new IllegalStateException("No user context available.");
		}
		List<ComponentWithContext> matchAtLevel = new ArrayList<>();
		matchAtLevel.add(new ComponentWithContext(component, uic));

		for (int i = 0; i < path.length; i++) {
			List<ComponentWithContext> matchAtLastLevel = matchAtLevel;
			matchAtLevel = new ArrayList<>();

			for (ComponentWithContext comp : matchAtLastLevel) {
				String[] parts = path[i].trim().split("[\\[\\]]");
				String className = parts[0].trim();
				int index = parts.length == 2 ? Integer.parseInt(parts[1]) : -1;

				FindComponentsByClassVisitor visitor = new FindComponentsByClassVisitor(comp.getComponent(),
						className, i == 0);
				UIContextHolder.pushContext(comp.getContext());

				try {
					if (visibleOnly) {
						TreeUtil.traverseVisible(comp.getComponent(), visitor);
					} else {
						TreeUtil.traverse(comp.getComponent(), visitor);
					}
				} finally {
					UIContextHolder.popContext();
				}

				if (index >= 0) {
					if (index < visitor.getResult().size()) {
						matchAtLevel.add(visitor.getResult().get(index));
					}
				} else {
					matchAtLevel.addAll(visitor.getResult());
				}
			}
		}

		return matchAtLevel.toArray(new ComponentWithContext[matchAtLevel.size()]);
	}

	/**
	 * Retrieves the first WComponent by its path in the WComponent tree. See
	 * {@link #findWComponents(WComponent, String[])} for a description of paths.
	 * <p>
	 * Searches only visible components.
	 * </p>
	 *
	 * @param component the component to search from.
	 * @param path the path to the WComponent.
	 * @return the first component matching the given path, or null if not found.
	 */
	public static ComponentWithContext findWComponent(final WComponent component,
			final String[] path) {
		return findWComponent(component, path, true);
	}

	/**
	 * Retrieves the first WComponent by its path in the WComponent tree. See
	 * {@link #findWComponents(WComponent, String[])} for a description of paths.
	 *
	 * @param component the component to search from.
	 * @param path the path to the WComponent.
	 * @param visibleOnly visible only
	 * @return the first component matching the given path, or null if not found.
	 */
	public static ComponentWithContext findWComponent(final WComponent component,
			final String[] path, final boolean visibleOnly) {
		ComponentWithContext[] components = findWComponents(component, path, visibleOnly);

		return components.length == 0 ? null : components[0];
	}

	/**
	 * An implementation of WComponentTreeVisitor which can return a result.
	 *
	 * @param <T> the result type
	 * @deprecated Use {@link AbstractVisitorWithResult} instead
	 */
	public abstract static class AbstractTreeVisitorWithResult<T> extends AbstractVisitorWithResult<T> {

		/**
		 * The current context name as processing the tree.
		 */
		private String currentContextName = null;

		/**
		 * @param comp the current node in the tree
		 * @param id the id to locate in the tree
		 * @return null if continue or ABORT or ABORT_BRANCH
		 */
		protected VisitorResult checkCorrectNameContext(final WComponent comp, final String id) {
			// Simple check - if ids are a match, then continue
			String compId = comp.getId();
			if (compId.equals(id)) {
				return null;
			}

			// Check if already in a context and it has changed. Dont want to start going down sibling branches to the
			// current context.
			if (currentContextName != null && !compId.startsWith(currentContextName)) {
				return VisitorResult.ABORT;
			}

			// Check for Name Context
			if (WebUtilities.isActiveNamingContext(comp)) {
				String context = ((NamingContextable) comp).getNamingContextId();
				if (!Util.empty(context)) {
					context += WComponent.ID_CONTEXT_SEPERATOR;
				}
				// Only process branch if ID starts with the name context
				if (id.startsWith(context)) {
					currentContextName = context;
					return null;
				}
				// If target id doesn't start with the current component's id, they will never match
				return VisitorResult.ABORT_BRANCH;
			} else if (comp instanceof WRepeater && !id.startsWith(compId)) {
				// Check for a WRepeater and if we need to check the rows.
				// Only process the repeater rows if the ID matches its row contexts
				return VisitorResult.ABORT_BRANCH;
			}
			return null;
		}
	}

}
