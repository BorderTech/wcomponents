package com.github.bordertech.wcomponents.util.visitor;

import com.github.bordertech.wcomponents.util.TreeUtil;
import com.github.bordertech.wcomponents.util.WComponentTreeVisitor;

/**
 * An implementation of WComponentTreeVisitor which can return a result.
 *
 * @param <T> the result type
 *
 * @author Jonathan Austin
 * @since 1.2.10
 * @see TreeUtil
 */
public abstract class AbstractVisitorWithResult<T> implements WComponentTreeVisitor {

	/**
	 * Result of tree visit.
	 */
	private T result;

	/**
	 * @param result the result
	 */
	public void setResult(final T result) {
		this.result = result;
	}

	/**
	 * @return the result
	 */
	public T getResult() {
		return result;
	}

}
