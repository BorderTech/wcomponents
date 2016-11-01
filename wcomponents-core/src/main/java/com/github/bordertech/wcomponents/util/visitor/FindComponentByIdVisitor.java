package com.github.bordertech.wcomponents.util.visitor;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.NamingContextable;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.Util;

/**
 * An implementation of WComponentTreeVisitor which searches for a component with a matching id.
 *
 * @author Jonathan Austin
 * @since 1.2.10
 * @see TreeUtil
 */
public class FindComponentByIdVisitor extends AbstractVisitorWithResult<ComponentWithContext> {

	/**
	 * The current context name as processing the tree.
	 */
	private String currentContextName = null;

	/**
	 * The ID to search for.
	 */
	private final String findId;

	/**
	 * @param findId id of component to find
	 */
	public FindComponentByIdVisitor(final String findId) {
		this.findId = findId;
	}

	/**
	 * @return the component id to search for
	 */
	public String getFindId() {
		return findId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorResult visit(final WComponent comp) {
		// Match
		if (findId.equals(comp.getId())) {
			handleFoundMatch(new ComponentWithContext(comp, UIContextHolder.getCurrent()));
			return VisitorResult.ABORT;
		}

		// Check name context. If different, then never match
		VisitorResult check = checkCorrectNameContext(comp, findId);
		if (check != null) {
			return check;
		}

		return VisitorResult.CONTINUE;
	}

	/**
	 * @param comp the matching component
	 */
	protected void handleFoundMatch(final ComponentWithContext comp) {
		setResult(comp);
	}

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
