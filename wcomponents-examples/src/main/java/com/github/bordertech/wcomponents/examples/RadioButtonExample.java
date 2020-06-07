package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * {@link WRadioButton} example.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class RadioButtonExample extends WPanel {

	/**
	 * Creates a RadioButtonExample.
	 */
	public RadioButtonExample() {
		this.setLayout(new FlowLayout(Alignment.VERTICAL));

		WPanel panel = new WPanel();
		RadioButtonGroup group1 = new RadioButtonGroup();
		panel.add(group1);
		WRadioButton rb1 = group1.addRadioButton(1);
		panel.add(new WLabel("Default", rb1));
		panel.add(rb1);
		this.add(panel);

		panel = new WPanel();
		RadioButtonGroup group2 = new RadioButtonGroup();
		panel.add(group2);
		WRadioButton rb2 = group2.addRadioButton(1);
		rb2.setSelected(true);
		panel.add(new WLabel("Initially selected", rb2));
		panel.add(rb2);
		this.add(panel);

		panel = new WPanel();
		RadioButtonGroup group3 = new RadioButtonGroup();
		panel.add(group3);
		WRadioButton rb3 = group3.addRadioButton(1);
		rb3.setDisabled(true);
		rb3.setToolTip("This is disabled.");
		panel.add(new WLabel("Disabled", rb3));
		panel.add(rb3);
		this.add(panel);

		RadioButtonGroup group = new RadioButtonGroup();
		WRadioButton rb4 = group.addRadioButton("A");
		WRadioButton rb5 = group.addRadioButton("B");
		WRadioButton rb6 = group.addRadioButton("C");

		panel = new WPanel();
		panel.setLayout(new FlowLayout(Alignment.LEFT, Size.MEDIUM));
		add(new WLabel("Group"));
		panel.add(new WLabel("A", rb4));
		panel.add(rb4);
		panel.add(new WLabel("B", rb5));
		panel.add(rb5);
		panel.add(new WLabel("C", rb6));
		panel.add(rb6);

		panel.add(group);

		this.add(panel);
	}
}
