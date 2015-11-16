package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;

/**
 * This example demonstrates a {@link WPanel} of type {@link PanelMode#LAZY} that loads its content via AJAX when made
 * visible.
 * <p>
 * It uses a {@link WSubordinateControl} on a {@link WRadioButtonSelect} to toggle the visibility of the {@link WPanel}.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class AjaxWPanelExample extends WPanel {

	/**
	 * Show option of the radio button select.
	 */
	private static final String OPTION_SHOW = "Show panel";
	/**
	 * Hide option of the radio button select.
	 */
	private static final String OPTION_HIDE = "Hide panel";

	/**
	 * Construct the "lazy" WPanel AJAX example.
	 */
	public AjaxWPanelExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		// Create Radio Button Select
		WRadioButtonSelect rbSelect = new WRadioButtonSelect();
		rbSelect.setToolTip("AJAX Panel controls");
		rbSelect.setFrameless(true);
		rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);
		rbSelect.setOptions(new String[]{OPTION_SHOW, OPTION_HIDE});
		rbSelect.setSelected(OPTION_HIDE);

		// Create the "lazy" WPanel
		WPanel panel = new WPanel();
		panel.setMode(WPanel.PanelMode.LAZY);
		panel.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 6));
		panel.add(new WText("This is the content in a lazy panel"));

		// Subordinate to Show/Hide the panel
		WSubordinateControl control = new WSubordinateControl();
		Rule rule = new Rule();
		rule.setCondition(new Equal(rbSelect, OPTION_SHOW));
		rule.addActionOnTrue(new Show(panel));
		rule.addActionOnFalse(new Hide(panel));
		control.addRule(rule);

		// Set up a CheckBox and TextArea to be used by a Subordinate Control on the panel
		WCheckBox box = new WCheckBox();
		WTextArea text = new WTextArea();
		text.setToolTip("Extra content");

		// Subordinate on the panel to Show/Hide a TextArea
		WSubordinateControl control2 = new WSubordinateControl();
		Rule rule2 = new Rule();
		rule2.setCondition(new Equal(box, "true"));
		rule2.addActionOnTrue(new Show(text));
		rule2.addActionOnFalse(new Hide(text));
		control2.addRule(rule2);

		panel.add(control2);
		panel.add(new WLabel("Show extra content", box));
		panel.add(box);
		panel.add(text);
		panel.add(new WButton("submit"));

		add(rbSelect);
		add(new WHorizontalRule());
		add(panel);
		add(control);
	}
}
