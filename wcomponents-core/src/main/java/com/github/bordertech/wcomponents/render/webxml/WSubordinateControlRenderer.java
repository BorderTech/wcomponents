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
				xml.appendTagOpen("ui:subordinate");
				xml.appendAttribute("id", subordinate.getId() + "-c" + seq++);
				xml.appendClose();
				paintRule(rule, xml);
				xml.appendEndTag("ui:subordinate");
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
			paintAction(action, "ui:onTrue", xml);
		}

		for (Action action : rule.getOnFalse()) {
			paintAction(action, "ui:onFalse", xml);
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
			xml.appendTag("ui:and");

			for (Condition operand : ((And) condition).getConditions()) {
				paintCondition(operand, xml);
			}

			xml.appendEndTag("ui:and");
		} else if (condition instanceof Or) {
			xml.appendTag("ui:or");

			for (Condition operand : ((Or) condition).getConditions()) {
				paintCondition(operand, xml);
			}

			xml.appendEndTag("ui:or");
		} else if (condition instanceof Not) {
			xml.appendTag("ui:not");
			paintCondition(((Not) condition).getCondition(), xml);
			xml.appendEndTag("ui:not");
		} else if (condition instanceof AbstractCompare) {
			AbstractCompare compare = (AbstractCompare) condition;
			xml.appendTagOpen("ui:condition");
			xml.appendAttribute("controller", compare.getTrigger().getId());
			xml.appendAttribute("value", compare.getComparePaintValue());
			xml.appendOptionalAttribute("operator", getCompareTypeName(compare.getCompareType()));
			xml.appendEnd();
		} else {
			throw new SystemException("Unknown condition: " + condition);
		}
	}

	/**
	 * Paints an action.
	 *
	 * @param action the action to paint
	 * @param elementName the enclosing element name ("ui:onFalse" or "ui:onTrue").
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
	 * @param elementName the enclosing element name ("ui:onFalse" or "ui:onTrue").
	 * @param xml the output response
	 */
	private void paintStandardAction(final Action action, final String elementName,
			final XmlStringBuilder xml) {
		xml.appendTagOpen(elementName);
		xml.appendAttribute("action", getActionTypeName(action.getActionType()));
		xml.appendClose();
		xml.appendTagOpen("ui:target");

		SubordinateTarget target = action.getTarget();
		if (target instanceof WComponentGroup<?>) {
			xml.appendAttribute("groupId", target.getId());
		} else {
			xml.appendAttribute("id", target.getId());
		}

		xml.appendEnd();
		xml.appendEndTag(elementName);
	}

	/**
	 * Paint an inGroup action - where a single item is being targeted out of a group of items.
	 *
	 * @param action the action to paint
	 * @param elementName the enclosing element name ("ui:onFalse" or "ui:onTrue").
	 * @param xml the output response
	 */
	private void paintInGroupAction(final Action action, final String elementName,
			final XmlStringBuilder xml) {
		xml.appendTagOpen(elementName);
		xml.appendAttribute("action", getActionTypeName(action.getActionType()));
		xml.appendClose();
		xml.appendTagOpen("ui:target");
		xml.appendAttribute("groupId", action.getTarget().getId());
		xml.appendAttribute("id", ((AbstractAction) action).getTargetInGroup().getId());
		xml.appendEnd();
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
				action = "showIn";
				break;
			case HIDE:
				action = "hide";
				break;
			case HIDEIN:
				action = "hideIn";
				break;
			case ENABLE:
				action = "enable";
				break;
			case ENABLEIN:
				action = "enableIn";
				break;
			case DISABLE:
				action = "disable";
				break;
			case DISABLEIN:
				action = "disableIn";
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
