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
import java.util.ArrayList;
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
				// In traversing the tree, special components like WInvisbleContainer, WRepeatRoot are still traversed (so ignore them)
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

		AbstractTreeVisitorWithResult<WComponent> visitor = new AbstractTreeVisitorWithResult<WComponent>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public VisitorResult visit(final WComponent comp) {
				// Match
				if (id.equals(comp.getId())) {
					setResult(comp);
					return VisitorResult.ABORT;
				}

				// Check name context. If different, then never match
				VisitorResult check = checkCorrectNameContext(comp, id);
				if (check != null) {
					return check;
				}

				return VisitorResult.CONTINUE;
			}
		};

		doTraverse(root, visibleOnly, visitor);

		return visitor.getResult();
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
		AbstractTreeVisitorWithResult<UIContext> visitor = new AbstractTreeVisitorWithResult<UIContext>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public VisitorResult visit(final WComponent comp) {

				// Match
				if (id.equals(comp.getId())) {
					setResult(UIContextHolder.getCurrent());
					return VisitorResult.ABORT;
				}

				// Check name context. If different, then never match
				VisitorResult check = checkCorrectNameContext(comp, id);
				if (check != null) {
					return check;
				}

				return VisitorResult.CONTINUE;
			}
		};

		doTraverse(root, visibleOnly, visitor);

		return visitor.getResult();
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
		AbstractTreeVisitorWithResult<ComponentWithContext> visitor = new AbstractTreeVisitorWithResult<ComponentWithContext>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public VisitorResult visit(final WComponent comp) {

				// Match
				if (id.equals(comp.getId())) {
					setResult(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
					return VisitorResult.ABORT;
				}

				// Check name context. If different, then never match
				VisitorResult check = checkCorrectNameContext(comp, id);
				if (check != null) {
					return check;
				}

				return VisitorResult.CONTINUE;
			}
		};

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
		AbstractTreeVisitorWithResult<UIContext> visitor = new AbstractTreeVisitorWithResult<UIContext>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public VisitorResult visit(final WComponent comp) {
				// Match
				if (id.equals(comp.getId())) {
					setResult(UIContextHolder.getCurrent());
					return VisitorResult.ABORT;
				}

				// Check name context. If different, then never match
				VisitorResult check = checkCorrectNameContext(comp, id);
				if (check != null) {
					return check;
				}

				// Save closest UIC as processing tree
				setResult(UIContextHolder.getCurrent());

				return VisitorResult.CONTINUE;
			}
		};

		doTraverse(root, visibleOnly, visitor);

		return visitor.getResult();
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
		/**
		 * Visit visible components to find the matching ID and check the components are not hidden.
		 */
		AbstractTreeVisitorWithResult<Boolean> visitor = new AbstractTreeVisitorWithResult<Boolean>() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public VisitorResult visit(final WComponent comp) {
				// Match (Only here if visible)
				if (id.equals(comp.getId())) {
					setResult(!comp.isHidden());
					return VisitorResult.ABORT;
				}

				// Check name context. If different, then never match
				VisitorResult check = checkCorrectNameContext(comp, id);
				if (check != null) {
					return check;
				}

				// If hidden then abort branch
				if (comp.isHidden()) {
					return VisitorResult.ABORT_BRANCH;
				}

				return VisitorResult.CONTINUE;
			}
		};

		// Only traverse visible
		visitor.setResult(false);
		doTraverse(root, true, visitor);

		return visitor.getResult();
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

		if (VisitorResult.ABORT_BRANCH.equals(result)) {
			// Continue processing, but not down this branch
			return VisitorResult.CONTINUE;
		} else if (VisitorResult.CONTINUE.equals(result)) {
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
		} else {
			// Abort entire traversal
			return VisitorResult.ABORT;
		}
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
	 * @return the component matching the given path, or null if not found.
	 */
	public static ComponentWithContext[] findWComponents(final WComponent component,
			final String[] path) {
		List<ComponentWithContext> matchAtLevel = new ArrayList<>();
		matchAtLevel.add(new ComponentWithContext(component, UIContextHolder.getCurrent()));

		for (int i = 0; i < path.length; i++) {
			List<ComponentWithContext> matchAtLastLevel = matchAtLevel;
			matchAtLevel = new ArrayList<>();

			for (ComponentWithContext comp : matchAtLastLevel) {
				String[] parts = path[i].trim().split("[\\[\\]]");
				String className = parts[0].trim();
				int index = parts.length == 2 ? Integer.parseInt(parts[1]) : -1;

				FindComponentByClassVisitor visitor = new FindComponentByClassVisitor(comp,
						className, i == 0);
				UIContextHolder.pushContext(comp.getContext());

				try {
					TreeUtil.traverse(comp.getComponent(), visitor);
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
	 *
	 * @param component the component to search from.
	 * @param path the path to the WComponent.
	 * @return the first component matching the given path, or null if not found.
	 */
	public static ComponentWithContext findWComponent(final WComponent component,
			final String[] path) {
		ComponentWithContext[] components = findWComponents(component, path);

		return components.length == 0 ? null : components[0];
	}

	/**
	 * An implementation of WComponentTreeVisitor which can return a result.
	 *
	 * @param <T> the result type
	 */
	public abstract static class AbstractTreeVisitorWithResult<T> implements WComponentTreeVisitor {

		/**
		 * Result of tree visit.
		 */
		private T result;

		/**
		 * @param result the result
		 */
		protected void setResult(final T result) {
			this.result = result;
		}

		/**
		 * @return the result
		 */
		protected T getResult() {
			return result;
		}

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
			} else if (comp instanceof WRepeater && !id.startsWith(compId)) {  // Check for a WRepeater and if we need to check the rows.
				// Only process the repeater rows if the ID matches its row contexts
				return VisitorResult.ABORT_BRANCH;
			}
			return null;
		}
	}

	/**
	 * A tree visitor implementation which finds components in the tree with a given class.
	 */
	private static final class FindComponentByClassVisitor extends
			AbstractTreeVisitorWithResult<List<ComponentWithContext>> {

		/**
		 * The root component being searched from.
		 */
		private final ComponentWithContext root;

		/**
		 * The className the class name to search for.
		 */
		private final String className;

		/**
		 * True if the root should be also be tested for a match, false if not.
		 */
		private final boolean includeRoot;

		/**
		 * Creates a FindComponentByClassVisitor.
		 *
		 * @param root the root component being searched from.
		 * @param className the class name to search for. The package name may be omitted for convenience.
		 * @param includeRoot true if the root should be also be tested for a match, false if not.
		 */
		private FindComponentByClassVisitor(final ComponentWithContext root, final String className,
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
			for (Class<?> clazz = comp.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
				if (classNamesMatch(clazz.getName(), className) && (includeRoot || comp != root.
						getComponent())) {
					getResult().add(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
				}
			}

			return VisitorResult.CONTINUE;
		}

		/**
		 * Tests two class names for equality, taking partical class names (no package given) into account.
		 *
		 * @param name1 the first class name to compare.
		 * @param name2 the second class name to compare.
		 * @return true if the class names match, false otherwise.
		 */
		private boolean classNamesMatch(final String name1, final String name2) {
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
}
