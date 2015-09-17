package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;

/**
 * Shows the various properties of WRadioButton.
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WRadioButtonExample extends WContainer {

	/**
	 * Construct example.
	 */
	public WRadioButtonExample() {
		//Normal WRadioButtons in a RadioButtonGroup
		add(new WHeading(WHeading.MAJOR, "Radio buttons are answers to a particular question."));

		add(new ExplanatoryText(
				"It is not required that all WRadioButtons in a group be in the same field set (or even in the same part of the UI)"
				+ " but it is strongly recommended. The radio buttons represent possible answers and only one may be selected so the question context must be provided."
				+ " A table column (or, less commonly a row) may server to provide the question context. There are no other arrangements which can be"
				+ " guaranteed to provide adequate accessible context for the relationship between the question and the answers."));

		add(new WHeading(WHeading.SECTION, "Normal, interactive radio buttons."));
		RadioButtonGroup quest = new RadioButtonGroup();
		add(quest);

		final WRadioButton rb1 = quest.addRadioButton("grail");
		final WRadioButton rb2 = quest.addRadioButton("pail");
		final WRadioButton rb3 = quest.addRadioButton("ale");

		WFieldSet fset = new WFieldSet("What is your quest?");
		add(fset);
		fset.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(layout);
		layout.addField("To seek the holy grail", rb1);
		layout.addField("To mend the holey pail", rb2);
		layout.addField("To imbibe the whole ale", rb3);

		add(new WHorizontalRule());

		//mandatory
		add(new ExplanatoryText(
				"Radio buttons may be mandatory but the required marker has to go on the 'question', not on the 'answers'."));
		fset = new WFieldSet("Choose your nearest neighbour");
		add(fset);
		fset.setMargin(new Margin(12, 0, 0, 0));
		fset.setMandatory(true);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(layout);
		RadioButtonGroup group = new RadioButtonGroup();
		fset.add(group);
		layout.addField("Scylla", group.addRadioButton(true));
		layout.addField("Charibdis", group.addRadioButton(false));

		//now for the other properties
		//disabled
		add(new WHeading(WHeading.SECTION, "Radio buttons may be disabled."));
		fset = new WFieldSet("What ails thee my Lord?");
		add(fset);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(layout);
		//start a new group so we can have one checked
		group = new RadioButtonGroup();
		add(group);
		WRadioButton rb = group.addRadioButton(true);
		rb.setDisabled(true);
		layout.addField("This turbulent priest.", rb);

		rb = group.addRadioButton(false);
		rb.setSelected(true);
		rb.setDisabled(true);
		layout.addField("Speak not of mild and bitter in this holy house.", rb);

		add(new WHorizontalRule());
		add(new WHeading(WHeading.SECTION, "Radio buttons may be read-only."));
		add(new ExplanatoryText(
				"Radio Buttons may be read-only. In this case the widget is replaced with a render which does not allow for malicious or accidental change of state."));
		fset = new WFieldSet("Here's some we answered earlier.");
		add(fset);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(layout);

		//read only - start a new group so we can have one checked
		group = new RadioButtonGroup();
		add(group);
		rb = group.addRadioButton(true);
		rb.setReadOnly(true);
		layout.addField("Read only unchecked radio button", rb);

		rb = group.addRadioButton(false);
		rb.setSelected(true);
		rb.setReadOnly(true);
		layout.addField("Read only checked radio button", rb);
		add(new WHorizontalRule());

	}

}
