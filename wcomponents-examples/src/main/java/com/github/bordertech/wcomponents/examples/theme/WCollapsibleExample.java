package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.CollapsibleGroup;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WCollapsibleToggle;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import java.util.Date;

/**
 * This component demonstrates the usage of the {@link WCollapsible} component. It shows both client side (JavaScript)
 * and server side usage.
 *
 * @author Adam Millard
 */
public class WCollapsibleExample extends WPanel {

	/**
	 * Creates a WCollapsibleExample.
	 */
	public WCollapsibleExample() {
		setLayout(new FlowLayout(FlowLayout.Alignment.VERTICAL, 0, 12));

		WText component3 = new WText() {
			// We want some dynamic text to show that there's a trip to the server.
			@Override
			public String getText() {
				return "Here is some more text that was generated at " + new Date() + '.';
			}
		};

		WCollapsible collapsible3 = new WCollapsible(component3,
				"Ajax collapsible - initially collapsed");
		collapsible3.setMode(WCollapsible.CollapsibleMode.DYNAMIC);
		collapsible3.setCollapsed(true);
		add(collapsible3);

		// A client side toggle can only work on client side collapsibles.
		CollapsibleGroup group = new CollapsibleGroup();
		WCollapsibleToggle wct = new WCollapsibleToggle();
		wct.setGroup(group);
		add(wct);

		WText component4 = new WText("Here is some text that is collapsible via the client side.");
		WCollapsible collapsible4 = new WCollapsible(component4,
				"Client Side Collapsible - initially expanded", WCollapsible.CollapsibleMode.CLIENT,
				group);
		collapsible4.setCollapsed(false);
		add(collapsible4);

		WText component5 = new WText(
				"Here is some more text that is collapsible via the client side.");
		WCollapsible collapsible5 = new WCollapsible(component5, "Nested collapsible",
				WCollapsible.CollapsibleMode.CLIENT, group);
		collapsible5.setCollapsed(false);
		//add(collapsible5);

		WCollapsible collapsible5a = new WCollapsible(collapsible5,
				"Client Side Collapsible - initially collapsed", WCollapsible.CollapsibleMode.CLIENT,
				group);
		collapsible5a.setCollapsed(true);
		add(collapsible5a);

		WCollapsible collapsible6 = new WCollapsible(new WText(
				"Here is some more text that is collapsible via the client side."),
				"With heading level set 2", WCollapsible.CollapsibleMode.CLIENT);
		collapsible6.setCollapsed(true);
		collapsible6.setHeadingLevel(HeadingLevel.H2);
		add(collapsible6);


		add(new WHeading(HeadingLevel.H2, "CollapsibleMode.SERVER"));
		add(new ExplanatoryText("These examples are here for completeness but there is no excuse for using WCollapsible.CollapsibleMode.SERVER"));
		WText component1 = new WText("Here is some text that is collapsible via the server side.");
		WCollapsible collapsible1 = new WCollapsible(component1,
				"Server Side Collapsible - initially collapsed", WCollapsible.CollapsibleMode.SERVER);
		add(collapsible1);

		WText component2 = new WText(
				"Here is some more text that is collapsible via the server side.");
		WCollapsible collapsible2 = new WCollapsible(component2,
				"Server Side Collapsible - initially expanded", WCollapsible.CollapsibleMode.SERVER);
		collapsible2.setCollapsed(false);
		add(collapsible2);
	}
}
