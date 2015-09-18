package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.subordinate.Condition;
import java.io.Serializable;

/**
 * This interfaces marks an operand which evaluates to a Boolean value.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public interface BooleanExpression extends Serializable {

	/**
	 * @return the subordinate condition for this expression
	 */
	Condition build();

	/**
	 * Evaluates and returns the value of this operand. This method is only used for internal testing purposes, as the
	 * actual expression will be "compiled" to a subordinate for application use.
	 *
	 * @return the value of this operand.
	 */
	Boolean evaluate();
}
