package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSelectToggle;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Example showing how to use the {@link WSelectToggle} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WSelectToggleExample extends WPanel {

	/**
	 * Creates a WSelectToggleExample.
	 */
	public WSelectToggleExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		add(new WHeading(WHeading.MAJOR, "Client-side, as button"));
		WSelectToggle toggle = new WSelectToggle(true);

		NestedContent target = new NestedContent();
		toggle.setTarget(target);
		add(toggle);
		add(target);

		add(new WHeading(WHeading.MAJOR, "Client-side, as text"));
		toggle = new WSelectToggle(true);
		toggle.setRenderAsText(true);

		target = new NestedContent();
		toggle.setTarget(target);
		add(toggle);
		add(target);

		add(new WHeading(WHeading.MAJOR, "Server-side, as button"));
		toggle = new WSelectToggle(false);

		target = new NestedContent();
		toggle.setTarget(target);
		add(toggle);
		add(target);

		add(new WHeading(WHeading.MAJOR,
				"Client-side targeting of grouped checkboxes not in a WFieldSet"));
		WComponentGroup<WCheckBox> group = new WComponentGroup<>();
		add(group);

		WCheckBox checkBox1 = new WCheckBox();
		checkBox1.setGroup(group);

		WCheckBox checkBox2 = new WCheckBox();
		checkBox2.setGroup(group);

		add(new WSelectToggle(true, checkBox1));

		WPanel panel = new WPanel();
		panel.add(new WLabel("Check-box 1", checkBox1));
		panel.add(checkBox1);
		panel.add(new WLabel("Check-box 2", checkBox2));
		panel.add(checkBox2);
		add(panel);
		//Targeting a WCheckBoxSelect - simple
		add(new WHeading(WHeading.MAJOR, "Client side targeting a WCheckBoxSelect"));
		WCheckBoxSelect select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setButtonColumns(3);
		WSelectToggle stateToggle = new WSelectToggle(true, select);
		stateToggle.setRenderAsText(true);
		stateToggle.setToolTip("Select all states and territories");
		add(stateToggle);
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(33);
		layout.addField("States and Territories", select);
		add(layout);

		//targeting a WCheckBoxSelect AND labelling the selectToggle
		add(new WHeading(WHeading.MAJOR, "Client side targeting a WCheckBoxSelect and with a label"));
		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setButtonColumns(3);
		layout = new WFieldLayout();
		layout.setLabelWidth(33);
		stateToggle = new WSelectToggle(true, select);

		WLabel stateToggleLabel = new WLabel("Select all states and territories", stateToggle);
		layout.addField(stateToggleLabel, stateToggle);

		layout.addField("States and Territories", select);
		add(layout);

		//targeting a WCheckBoxSelect AND labelling the selectToggle
		add(new WHeading(WHeading.MAJOR,
				"Client side as text targeting a WCheckBoxSelect and with a label"));
		select = new WCheckBoxSelect("australian_state");
		select.setFrameless(true);
		select.setButtonColumns(3);
		layout = new WFieldLayout();
		layout.setLabelWidth(33);
		stateToggle = new WSelectToggle(true, select);

		stateToggle.setRenderAsText(true);
		stateToggleLabel = new WLabel("Select states and territories:", stateToggle);
		layout.addField(stateToggleLabel, stateToggle);

		layout.addField("States and Territories", select);
		add(layout);
	}

	/**
	 * A panel containing some selectable controls, to demonstrate toggle behaviour.
	 */
	private static final class NestedContent extends WPanel {

		/**
		 * Creates a NestedContent panel.
		 */
		private NestedContent() {
			WFieldSet fieldSet = new WFieldSet("Fields");
			add(fieldSet);

			WFieldLayout fieldLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
			fieldLayout.addField("Checkbox 1", new WCheckBox());
			fieldLayout.addField("Checkbox 2", new WCheckBox());

			fieldSet.add(fieldLayout);
		}
	}
}
