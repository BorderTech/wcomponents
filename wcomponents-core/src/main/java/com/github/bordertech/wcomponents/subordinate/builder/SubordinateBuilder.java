package com.github.bordertech.wcomponents.subordinate.builder;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.subordinate.Action.ActionType;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.SystemException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * The SubordinateBuilder is a utility class which provides a convenient way to build up simple logic rules so that they
 * executed on both the client and server. See the {@link WSubordinateControl} documentation for more information.
 * </p>
 * <p>
 * Consider a user interface which contains two "yes"/"no" radio button groups and two text inputs. To create a rule
 * that makes the text inputs mandatory if either "yes" option has been selected:
 * </p>
 *
 * <pre>
 * SubordinateBuilder builder = new SubordinateBuilder();
 *
 * // Set condition to be: (radioButtonGroup1.getValueAsString().equals(&quot;Yes&quot;) ||
 * // radioButtonGroup2.getValueAsString().equals(&quot;Yes&quot;)).
 * builder.condition().equals(radioButtonGroup1, &quot;Yes&quot;).or().equals(radioButtonGroup2, &quot;Yes&quot;);
 *
 * // When the condition evaluates to true, we make the fields mandatory.
 * builder.whenTrue().setMandatory(textInput1, textInput2);
 *
 * // When the condition evaluates to false, we need to set the fields back to being optional.
 * builder.whenFalse().setOptional(textInput1, textInput2);
 *
 * // Finally, add the rule to the UI
 * SubordinateControl control = builder.build();
 * add(control);
 * </pre>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 */
public final class SubordinateBuilder {

	/**
	 * The actions to execute when the {@link #condition} evaluates to 'true'.
	 */
	private final List<Action> onTrue = new ArrayList<>();

	/**
	 * The actions to execute when the {@link #condition} evaluates to 'false'.
	 */
	private final List<Action> onFalse = new ArrayList<>();

	/**
	 * The condition controls which actions should be executed.
	 */
	private final MutableExpression condition = new MutableExpression();

	/**
	 * Action builder for on true actions.
	 */
	private final ActionBuilder onTrueActionBuilder = new ActionBuilder(onTrue);

	/**
	 * Action builder for on false actions.
	 */
	private final ActionBuilder onFalseActionBuilder = new ActionBuilder(onFalse);

	/**
	 * Expression builder for the condition.
	 */
	private final ExpressionBuilder conditionBuilder = new ExpressionBuilder(condition);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "if (" + condition + ")\nthen\n   " + onTrue + "\nelse\n   " + onFalse;
	}

	/**
	 * This method is used to append actions to execute when the {@link #condition() condition} evaluates to 'true'.
	 *
	 * @return a builder used to append actions to the "true" branch.
	 */
	public ActionBuilder whenTrue() {
		return onTrueActionBuilder;
	}

	/**
	 * This method is used to append actions to execute when the {@link #condition() condition} evaluates to 'false'.
	 *
	 * @return a builder used to append actions to the "false" branch.
	 */
	public ActionBuilder whenFalse() {
		return onFalseActionBuilder;
	}

	/**
	 * This method is used to build the conditional statement which controls when the FlowRule should execute.
	 *
	 * @return an expression builder to append expressions to the condition.
	 */
	public ExpressionBuilder condition() {
		return conditionBuilder;
	}

	/**
	 * @return the condition.
	 */
	public BooleanExpression getCondition() {
		return condition.getExpression();
	}

	/**
	 * @return a read-only copy of the actions to execute when the {@link #getCondition() condition} evaluates to true.
	 */
	public List<Action> getActionsWhenTrue() {
		return Collections.unmodifiableList(onTrue);
	}

	/**
	 * @return a read-only copy of the actions to execute when the {@link #getCondition() condition} evaluates to false.
	 */
	public List<Action> getActionsWhenFalse() {
		return Collections.unmodifiableList(onFalse);
	}

	/**
	 * This builds the SubordinateControl. This method will throw a SystemException if the condition is invalid, or
	 * there are no actions specified.
	 *
	 * @return a SubordinateControl built using this builder.
	 */
	public WSubordinateControl build() {
		if (!condition().validate()) {
			throw new SystemException("Invalid condition: " + condition);
		}

		if (getActionsWhenTrue().isEmpty() && getActionsWhenFalse().isEmpty()) {
			throw new SystemException("No actions to execute");
		}

		WSubordinateControl subordinate = new WSubordinateControl();
		Rule rule = new Rule();

		BooleanExpression expression = getCondition();
		rule.setCondition(expression.build());

		for (Action action : getActionsWhenTrue()) {
			rule.addActionOnTrue(action.build());
		}

		for (Action action : getActionsWhenFalse()) {
			rule.addActionOnFalse(action.build());
		}

		subordinate.addRule(rule);
		return subordinate;
	}

	/**
	 * Helper class to build the actions to be executed on the subordinate control.
	 */
	public static class ActionBuilder {

		/**
		 * The action list to update.
		 */
		private final List<Action> actions;

		/**
		 * Creates an ActionBuilder.
		 *
		 * @param actions the list which actions will be added to.
		 */
		public ActionBuilder(final List<Action> actions) {
			this.actions = actions;
		}

		/**
		 * Adds actions which will set the given targets to be mandatory.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder setMandatory(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.MANDATORY, target));
			}

			return this;
		}

		/**
		 * Adds actions which will set the given targets to be optional.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder setOptional(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.OPTIONAL, target));
			}

			return this;
		}

		/**
		 * Adds actions which will hide the given targets.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder hide(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.HIDE, target));
			}

			return this;
		}

		/**
		 * Adds actions which will show the given targets.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder show(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.SHOW, target));
			}

			return this;
		}

		/**
		 * Adds actions which will disable the given targets.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder disable(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.DISABLE, target));
			}

			return this;
		}

		/**
		 * Adds actions which will enable the given targets.
		 *
		 * @param targets the action targets.
		 * @return this ActionBuilder.
		 */
		public ActionBuilder enable(final SubordinateTarget... targets) {
			for (SubordinateTarget target : targets) {
				actions.add(new Action(ActionType.ENABLE, target));
			}

			return this;
		}

		/**
		 * Adds actions which will show the given target in a group of targets.
		 *
		 * @param showTarget the target to show in the group
		 * @param targets the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder showIn(final SubordinateTarget showTarget,
				final SubordinateTarget... targets) {
			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			for (SubordinateTarget target : targets) {
				group.addToGroup(target);
			}
			actions.add(new Action(ActionType.SHOWIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will show the given target in a group of targets.
		 *
		 * @param showTarget the target to show in the group
		 * @param group the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder showIn(final SubordinateTarget showTarget,
				final WComponentGroup<SubordinateTarget> group) {
			actions.add(new Action(ActionType.SHOWIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will hide the given target in a group of targets.
		 *
		 * @param showTarget the target to hide in the group
		 * @param targets the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder hideIn(final SubordinateTarget showTarget,
				final SubordinateTarget... targets) {
			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			for (SubordinateTarget target : targets) {
				group.addToGroup(target);
			}
			actions.add(new Action(ActionType.HIDEIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will hide the given target in a group of targets.
		 *
		 * @param showTarget the target to hide in the group
		 * @param group the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder hideIn(final SubordinateTarget showTarget,
				final WComponentGroup<SubordinateTarget> group) {
			actions.add(new Action(ActionType.HIDEIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will enable the given target in a group of targets.
		 *
		 * @param showTarget the target to enable in the group
		 * @param targets the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder enableIn(final SubordinateTarget showTarget,
				final SubordinateTarget... targets) {
			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			for (SubordinateTarget target : targets) {
				group.addToGroup(target);
			}
			actions.add(new Action(ActionType.ENABLEIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will enable the given target in a group of targets.
		 *
		 * @param showTarget the target to enable in the group
		 * @param group the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder enableIn(final SubordinateTarget showTarget,
				final WComponentGroup<SubordinateTarget> group) {
			actions.add(new Action(ActionType.ENABLEIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will disable the given target in a group of targets.
		 *
		 * @param showTarget the target to disable in the group
		 * @param targets the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder disableIn(final SubordinateTarget showTarget,
				final SubordinateTarget... targets) {
			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			for (SubordinateTarget target : targets) {
				group.addToGroup(target);
			}
			actions.add(new Action(ActionType.DISABLEIN, showTarget, group));

			return this;
		}

		/**
		 * Adds actions which will disable the given target in a group of targets.
		 *
		 * @param showTarget the target to disable in the group
		 * @param group the group of targets
		 * @return this ActionBuilder.
		 */
		public ActionBuilder disableIn(final SubordinateTarget showTarget,
				final WComponentGroup<SubordinateTarget> group) {
			actions.add(new Action(ActionType.DISABLEIN, showTarget, group));

			return this;
		}

	}

}
