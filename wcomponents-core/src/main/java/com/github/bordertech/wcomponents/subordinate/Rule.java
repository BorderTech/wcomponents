package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.util.SystemException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Rule defines the visibility control logic to be added to a SubordinateControl. A Rule consists of a "condition"
 * that will evaluate to true or false, a list of actions to be executed if the condition is true, and a list of actions
 * to be executed if the condition is false.
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Rule implements Serializable {

	/**
	 * The condition to determine which actions to execute (the IF).
	 */
	private Condition condition;

	/**
	 * The actions to execute if the condition is true (the THEN).
	 */
	private final List<Action> onTrue = new ArrayList<>();

	/**
	 * The actions to execute if the condition is false (the ELSE).
	 */
	private final List<Action> onFalse = new ArrayList<>();

	/**
	 * Creates a Rule with no conditions or actions.
	 */
	public Rule() {
		this(null, null, null);
	}

	/**
	 * Creates a Rule with the specified condition and no actions. Useful if you want to set the actions separately.
	 *
	 * @param condition the condition
	 */
	public Rule(final Condition condition) {
		this(condition, null, null);
	}

	/**
	 * Creates a Rule with an onTrue action.
	 *
	 * @param condition the condition to evaluate.
	 * @param onTrue the action to execute if the condition evaluates to true.
	 */
	public Rule(final Condition condition, final Action onTrue) {
		this(condition, onTrue, null);
	}

	/**
	 * Creates a Rule with an onTrue and onFalse action. When execute() is called. If "condition" evaluates to true,
	 * "onTrue" is executed, else "onFalse" is executed. The "onTrue" and/or "onFalse" functions can be null.
	 *
	 * @param condition the condition to evaluate.
	 * @param onTrue the action to execute if the condition evaluates to true.
	 * @param onFalse the action to execute if the condition evaluates to true.
	 */
	public Rule(final Condition condition, final Action onTrue, final Action onFalse) {
		this.condition = condition;

		if (onTrue != null) {
			this.onTrue.add(onTrue);
		}

		if (onFalse != null) {
			this.onFalse.add(onFalse);
		}
	}

	/**
	 * Sets the condition.
	 *
	 * @param condition the condition to set.
	 */
	public void setCondition(final Condition condition) {
		this.condition = condition;
	}

	/**
	 * @return the condition.
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * Returns the actions to execute when the condition evaluates to true.
	 *
	 * @return the actions to execute on true.
	 */
	public List<Action> getOnTrue() {
		return Collections.unmodifiableList(onTrue);
	}

	/**
	 * Returns the actions to execute when the condition evaluates to false.
	 *
	 * @return the actions to execute on false.
	 */
	public List<Action> getOnFalse() {
		return Collections.unmodifiableList(onFalse);
	}

	/**
	 * Executes the rule.
	 */
	public void execute() {
		if (condition == null) {
			throw new SystemException("Rule cannot be executed as it has no condition");
		}

		if (condition.isTrue()) {
			for (Action action : onTrue) {
				action.execute();
			}
		} else {
			for (Action action : onFalse) {
				action.execute();
			}
		}
	}

	/**
	 * Executes the rule, getting the value from the Request.
	 *
	 * @param request the request being responded to.
	 */
	public void execute(final Request request) {
		if (condition == null) {
			throw new SystemException("Rule cannot be executed as it has no condition");
		}

		if (condition.isTrue(request)) {
			for (Action action : onTrue) {
				action.execute();
			}
		} else {
			for (Action action : onFalse) {
				action.execute();
			}
		}
	}

	/**
	 * Add an action that should be executed if the condition evaluates to true.
	 *
	 * @param action the action to add.
	 */
	public void addActionOnTrue(final Action action) {
		if (action == null) {
			throw new IllegalArgumentException("Action cannot be null");
		}

		onTrue.add(action);
	}

	/**
	 * Add an action that should be executed if the condition evaluates to false.
	 *
	 * @param action the action to add.
	 */
	public void addActionOnFalse(final Action action) {
		if (action == null) {
			throw new IllegalArgumentException("Action cannot be null");
		}

		onFalse.add(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "if (" + condition + ")\nthen\n   " + onTrue + "\nelse\n   " + onFalse + "\n";
	}

}
