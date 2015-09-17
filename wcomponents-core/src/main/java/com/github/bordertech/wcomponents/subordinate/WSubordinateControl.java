package com.github.bordertech.wcomponents.subordinate;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.Request;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * WSubordinateControl enables screen logic (in particular visibility) to be abstracted in such a way that the logic can
 * be executed by the UI framework in the client browser. The WSubordinateControl itself does not have a visual
 * representation, but it must still be added to a visible section within the application.
 * </p>
 * <p>
 * To make use of WSubordinateControl, create a {@link Rule} and add it to the WSubordinateControl. Rules combine
 * {@link Condition} functions with {@link Action} functions to form the logic of your WSubordinateControl.
 * </p>
 * <p>
 * Conditions are specified using {@link Equal} tests on input field values and can be combined with {@link And} and
 * {@link Or} logical operators.
 * </p>
 * <p>
 * Actions can be performed on a single component, or a group of components. The following lists the actions that are
 * currently available:
 * </p>
 * <ul>
 * <li>{@link Hide}</li>
 * <li>{@link Show}</li>
 * <li>{@link Enable}</li>
 * <li>{@link Disable}</li>
 * <li>{@link Mandatory}</li>
 * <li>{@link Optional}</li>
 * <li>{@link ShowInGroup}</li>
 * <li>{@link HideInGroup}</li>
 * <li>{@link EnableInGroup}</li>
 * <li>{@link DisableInGroup}</li>
 * </ul>
 * <p>
 * <b>WARNING:</b> Use of this control is likely to cause maintenance issues if it is overused. It is recommended that
 * you use it only for simple situations, as overly complex rules become hard to maintain.
 * </p>
 *
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSubordinateControl extends AbstractWComponent {

	/**
	 * The list of rules to execute.
	 */
	private final List<Rule> rules = new ArrayList<>();

	/**
	 * Add a Rule to the subordinate control.
	 *
	 * @param rule the rule to add
	 */
	public void addRule(final Rule rule) {
		if (rule == null) {
			throw new IllegalArgumentException("Rule must not be null.");
		}

		rules.add(rule);
	}

	/**
	 * Register the Subordinate Control.
	 *
	 * @param request the request being processed
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		SubordinateControlHelper.registerSubordinateControl(getId(), request);
	}

	/**
	 * Run the controls.
	 */
	public void applyTheControls() {
		for (Rule rule : getRules()) {
			rule.execute();
		}
	}

	/**
	 * Run the controls using values from the Request.
	 *
	 * @param request the request being responded to.
	 */
	public void applyTheControls(final Request request) {
		for (Rule rule : getRules()) {
			rule.execute(request);
		}
	}

	/**
	 * @return the list of rules.
	 */
	public List<Rule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Rule rule : getRules()) {
			buf.append("RULE: ");
			buf.append(rule);
		}
		return buf.toString();
	}
}
