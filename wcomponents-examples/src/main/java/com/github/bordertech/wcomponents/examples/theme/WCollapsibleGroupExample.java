package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.CollapsibleGroup;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WCollapsibleToggle;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.GridLayout;

/**
 * This example demonstrates grouping of <code>WCollapsible</code> components. The {@link CollapsibleGroup} class can
 * contain a number of <code>WCollapsible</code> components and one <code>WCollapsibleToggle</code> component. If the
 * <code>WCollasibleToggle</code> component belongs to a group it will only toggle collapsible in the same group.
 *
 * @author Christina Harris
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WCollapsibleGroupExample extends WPanel {

	/**
	 * Creates a WCollapsibleGroupExample.
	 */
	public WCollapsibleGroupExample() {

		WPanel panel = new WPanel();
		panel.setLayout(new GridLayout(3, 2, 6, 12));
		panel.setMargin(new com.github.bordertech.wcomponents.Margin(Size.ZERO, Size.ZERO, Size.LARGE, Size.ZERO));
		add(panel);

		CollapsibleGroup group1 = new CollapsibleGroup();
		CollapsibleGroup group2 = new CollapsibleGroup();

		WCollapsibleToggle toggle1 = new WCollapsibleToggle(group1);
		panel.add(new WText(
				"Group one toggle controls will only toggle the state of those collapsibles marked as being in group 1."));
		panel.add(toggle1);

		WCollapsibleToggle toggle2 = new WCollapsibleToggle(group2);
		panel.add(new WText(
				"Group two toggle controls will only toggle the state of those collapsibles marked as being in group 2."));
		panel.add(toggle2);

		WCollapsibleToggle toggle3 = new WCollapsibleToggle();
		panel.
				add(new WText(
						"No group toggle controls should toggle every collapsible in the page."));
		panel.add(toggle3);

		WText component1 = new WText("Some content in a collapsible.");
		WCollapsible collapsible1 = new WCollapsible(component1, "Group 1 - initially collapsed",
				WCollapsible.CollapsibleMode.DYNAMIC, group1);
		add(collapsible1);

		WText component2 = new WText("Some content in a collapsible.");
		WCollapsible collapsible2 = new WCollapsible(component2, "Group 2 - initially expanded",
				WCollapsible.CollapsibleMode.DYNAMIC, group2);
		collapsible2.setCollapsed(false);
		add(collapsible2);

		WText component3 = new WText("Some content in a collapsible.");
		WCollapsible collapsible3 = new WCollapsible(component3, "Group 1 - initially collapsed",
				WCollapsible.CollapsibleMode.DYNAMIC, group1);
		add(collapsible3);

		WText component4 = new WText("Some content in a collapsible.");
		WCollapsible collapsible4 = new WCollapsible(component4, "Group 2 - initially expanded",
				WCollapsible.CollapsibleMode.DYNAMIC, group2);
		collapsible4.setCollapsed(false);
		add(collapsible4);

		add(new WHorizontalRule());

		WText component5 = new WText(
				"Here is some more text that is collapsible via the client side.");
		WCollapsible collapsible5 = new WCollapsible(component5, "Collapsible - no group",
				WCollapsible.CollapsibleMode.CLIENT);
		collapsible5.setCollapsed(true);
		add(collapsible5);


		WTabSet accordion = new WTabSet(WTabSet.TabSetType.ACCORDION);
		add(accordion);
		accordion.setGroup(group2);
		accordion.addTab(new WText("Tab 1 content"), "Tab 1", WTabSet.TabMode.CLIENT);
		accordion.addTab(new WText("Tab 2 content"), "Tab 2", WTabSet.TabMode.CLIENT);
		accordion.addTab(new WText("Tab 3 content"), "Tab 3", WTabSet.TabMode.CLIENT);
		accordion.addTab(new WText("Tab 4 content"), "Tab 4", WTabSet.TabMode.CLIENT);
	}
}
