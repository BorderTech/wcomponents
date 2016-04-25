package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import java.io.Serializable;

/**
 * This interface marks condition functions that evaluate to true or false. Examples of condition functions include
 * "equal", "and", "or", "not" etc.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface Condition extends Serializable {

	/**
	 * Indicates whether this condition evaluates to 'true' in the given context.
	 *
	 * @return <code>true</code> if this condition evaluates to 'true', otherwise <code>false</code>.
	 */
	boolean isTrue();

	/**
	 * Indicates whether this condition evaluates to 'false' in the given context.
	 *
	 * @return <code>true</code> if this condition evaluates to 'false', otherwise <code>false</code>.
	 */
	boolean isFalse();

	/**
	 * Indicates whether this condition evaluates to 'true' in the given context by getting the values from the Request.
	 *
	 * @param request the request being processed.
	 * @return <code>true</code> if this condition evaluates to 'true', otherwise <code>false</code>.
	 */
	boolean isTrue(Request request);

	/**
	 * Indicates whether this condition evaluates to 'false' in the given context by testing the value in the Request.
	 *
	 * @param request the request being processed.
	 * @return <code>true</code> if this condition evaluates to 'false', otherwise <code>false</code>.
	 */
	boolean isFalse(Request request);
}
