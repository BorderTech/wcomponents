package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.subordinate.AbstractAction;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare;
import com.github.bordertech.wcomponents.subordinate.AbstractCompare.CompareType;
import com.github.bordertech.wcomponents.subordinate.Action;
import com.github.bordertech.wcomponents.subordinate.Action.ActionType;
import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Not;
import com.github.bordertech.wcomponents.subordinate.Or;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.util.SystemException;

/**
 * The Renderer for SubordinateControl.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class WSubordinateControlRenderer extends AbstractWebXmlRenderer {
	public static final String TAG_SUBORDINATE = "wc-subordinate";
	public static final String TAG_CONDITION = "wc-condition";
	public static final String TAG_TARGET = "wc-target";
	public static final String TAG_OR = "wc-or";
	public static final String TAG_AND = "wc-and";
	public static final String TAG_NOT = "wc-not";
	public static final String TAG_ONTRUE = "wc-ontrue";
	public static final String TAG_ONFALSE = "wc-onfalse";

	/**
	 * Paints the given SubordinateControl.
	 *
	 * @param component the SubordinateControl to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WSubordinateControl subordinate = (WSubordinateControl) component;
		XmlStringBuilder xml = renderContext.getWriter();

		if (!subordinate.getRules().isEmpty()) {
			int seq = 0;

			for (Rule rule : subordinate.getRules()) {
				xml.appendTagOpen(TAG_SUBORDINATE);
				xml.appendAttribute("id", subordinate.getId() + "-c" + seq++);
				xml.appendClose();
				paintRule(rule, xml);
				xml.appendEndTag(TAG_SUBORDINATE);
			}
		}
	}

	/**
	 * Paints a single rule.
	 *
	 * @param rule the rule to paint.
	 * @param xml the writer to send the output to
	 */
	private void paintRule(final Rule rule, final XmlStringBuilder xml) {
		if (rule.getCondition() == null) {
			throw new SystemException("Rule cannot be painted as it has no condition");
		}

		paintCondition(rule.getCondition(), xml);

		for (Action action : rule.getOnTrue()) {
			paintAction(action, TAG_ONTRUE, xml);
		}

		for (Action action : rule.getOnFalse()) {
			paintAction(action, TAG_ONFALSE, xml);
		}
	}

	/**
	 * Paints a condition.
	 *
	 * @param condition the condition to paint.
	 * @param xml the writer to send the output to
	 */
	private void paintCondition(final Condition condition, final XmlStringBuilder xml) {
		if (condition instanceof And) {
			xml.appendTag(TAG_AND);

			for (Condition operand : ((And) condition).getConditions()) {
				paintCondition(operand, xml);
			}

			xml.appendEndTag(TAG_AND);
		} else if (condition instanceof Or) {
			xml.appendTag(TAG_OR);

			for (Condition operand : ((Or) condition).getConditions()) {
				paintCondition(operand, xml);
			}

			xml.appendEndTag(TAG_OR);
		} else if (condition instanceof Not) {
			xml.appendTag(TAG_NOT);
			paintCondition(((Not) condition).getCondition(), xml);
			xml.appendEndTag(TAG_NOT);
		} else if (condition instanceof AbstractCompare) {
			AbstractCompare compare = (AbstractCompare) condition;
			xml.appendTagOpen(TAG_CONDITION);
			xml.appendAttribute("controller", compare.getTrigger().getId());
			xml.appendAttribute("value", compare.getComparePaintValue());
			xml.appendOptionalAttribute("operator", getCompareTypeName(compare.getCompareType()));
			xml.appendClose();
			xml.appendEndTag(TAG_CONDITION);
		} else {
			throw new SystemException("Unknown condition: " + condition);
		}
	}

	/**
	 * Paints an action.
	 *
	 * @param action the action to paint
	 * @param elementName the enclosing element name (TAG_ONFALSE or TAG_ONTRUE).
	 * @param xml the writer to send the output to
	 */
	private void paintAction(final Action action, final String elementName,
			final XmlStringBuilder xml) {
		switch (action.getActionType()) {
			case SHOW:
			case HIDE:
			case ENABLE:
			case DISABLE:
			case OPTIONAL:
			case MANDATORY:
				paintStandardAction(action, elementName, xml);
				break;

			case SHOWIN:
			case HIDEIN:
			case ENABLEIN:
			case DISABLEIN:
				paintInGroupAction(action, elementName, xml);
				break;

			default:
				break;
		}
	}

	/**
	 * Paint a standard action - where a single item or single group is targeted.
	 *
	 * @param action the action to paint
	 * @param elementName the enclosing element name (TAG_ONFALSE or TAG_ONTRUE).
	 * @param xml the output response
	 */
	private void paintStandardAction(final Action action, final String elementName,
			final XmlStringBuilder xml) {
		xml.appendTagOpen(elementName);
		xml.appendAttribute("action", getActionTypeName(action.getActionType()));
		xml.appendClose();
		xml.appendTagOpen(TAG_TARGET);

		SubordinateTarget target = action.getTarget();
		if (target instanceof WComponentGroup<?>) {
			xml.appendAttribute("groupid", target.getId());
		} else {
			xml.appendAttribute("refid", target.getId());
		}
		xml.appendClose();
		xml.appendEndTag(TAG_TARGET);
		xml.appendEndTag(elementName);
	}

	/**
	 * Paint an inGroup action - where a single item is being targeted out of a group of items.
	 *
	 * @param action the action to paint
	 * @param elementName the enclosing element name (TAG_ONFALSE or TAG_ONTRUE).
	 * @param xml the output response
	 */
	private void paintInGroupAction(final Action action, final String elementName,
			final XmlStringBuilder xml) {
		xml.appendTagOpen(elementName);
		xml.appendAttribute("action", getActionTypeName(action.getActionType()));
		xml.appendClose();
		xml.appendTagOpen(TAG_TARGET);
		xml.appendAttribute("groupid", action.getTarget().getId());
		xml.appendAttribute("refid", ((AbstractAction) action).getTargetInGroup().getId());
		xml.appendClose();
		xml.appendEndTag(TAG_TARGET);
		xml.appendEndTag(elementName);
	}

	/**
	 * Helper method to determine the compare type name.
	 *
	 * @param type the enumerated CompareType
	 * @return the name of the CompareType
	 */
	private String getCompareTypeName(final CompareType type) {
		String compare = null;

		switch (type) {
			case EQUAL:
				break;
			case NOT_EQUAL:
				compare = "ne";
				break;
			case LESS_THAN:
				compare = "lt";
				break;
			case LESS_THAN_OR_EQUAL:
				compare = "le";
				break;
			case GREATER_THAN:
				compare = "gt";
				break;
			case GREATER_THAN_OR_EQUAL:
				compare = "ge";
				break;
			case MATCH:
				compare = "rx";
				break;
			default:
				throw new SystemException("Invalid compare type: " + type);
		}

		return compare;
	}

	/**
	 * Helper method to determine the action name.
	 *
	 * @param type the enumerated ActionType
	 * @return the name of the action
	 */
	private String getActionTypeName(final ActionType type) {
		String action = null;

		switch (type) {
			case SHOW:
				action = "show";
				break;
			case SHOWIN:
				action = "showin";
				break;
			case HIDE:
				action = "hide";
				break;
			case HIDEIN:
				action = "hidein";
				break;
			case ENABLE:
				action = "enable";
				break;
			case ENABLEIN:
				action = "enablein";
				break;
			case DISABLE:
				action = "disable";
				break;
			case DISABLEIN:
				action = "disablein";
				break;
			case OPTIONAL:
				action = "optional";
				break;
			case MANDATORY:
				action = "mandatory";
				break;
			default:
				break;
		}

		return action;
	}
}
