package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.CollapsibleGroup;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WCollapsibleToggle;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WText;

/**
 * Example of using AJAX collapsibles. To load the content via AJAX, the collapsibles are created using the
 * {@link WCollapsible.CollapsibleMode#LAZY LAZY} collapsible mode.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class AjaxWCollapsibleExample extends WContainer {

	/**
	 * Creates an AjaxWCollapsibleExample.
	 */
	public AjaxWCollapsibleExample() {
		add(new WHeading(HeadingLevel.H2, "Collapsible Toggle and Collapsibles with Ajax Content."));
		add(new CollapsibleExample());
	}

	/**
	 * A component which contains a collapsible with some AJAX content.
	 */
	private static final class CollapsibleExample extends WContainer {

		/**
		 * Creates a CollapsibleExample.
		 */
		private CollapsibleExample() {

			CollapsibleGroup group = new CollapsibleGroup();
			WCollapsibleToggle wct = new WCollapsibleToggle(group);
			add(wct);

			WText component1 = new WText("Here is some text that is collapsible via ajax.");

			WCollapsible collapsible1 = new WCollapsible(component1,
					"Client Side Ajax Collapsible - initially collapsed",
					WCollapsible.CollapsibleMode.LAZY, group);
			add(collapsible1);

			WText component2 = new WText("Here is some more text that is collapsible via ajax.");

			WCollapsible collapsible2 = new WCollapsible(component2,
					"Client Side Ajax Collapsible - initially expanded",
					WCollapsible.CollapsibleMode.LAZY, group);
			collapsible2.setCollapsed(false);
			add(collapsible2);
		}
	}
}
