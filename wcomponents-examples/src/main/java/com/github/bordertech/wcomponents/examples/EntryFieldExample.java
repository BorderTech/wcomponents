package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WSingleSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExampleLookupTable.TableEntry;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.List;

/**
 * <p>
 * This is a non exhaustive list of the various kinds of Entry Fields available within wComponents.
 * </p>
 * <p>
 * See individual example for further configuration options
 * </p>
 *
 * @author Yiannis Paschalidis - extracted from KitchenSink.
 * @since 1.0.0
 */
public class EntryFieldExample extends WPanel {

	private final WTextField tf1, tf2, tf3, tf4;
	private final WNumberField nf1, nf2, nf3;
	private final WEmailField ef1;
	private final WDropdown drop1, drop2;
	private final WSingleSelect single1;
	private final WMultiSelect multi1, multi2;
	private final WCheckBoxSelect multiCb;
	private final WRadioButtonSelect rbSelect;
	private final RadioButtonGroup radioButtonGroup;
	private final WRadioButton rb1, rb2, rb3;
	private final WCheckBox cb1, cb2, cb3;
	private final WTextArea ta;

	/**
	 * Creates an EntryFieldExample.
	 */
	public EntryFieldExample() {
		this.setLayout(new FlowLayout(Alignment.VERTICAL));

		WFieldLayout fieldLayout;
		WValidationErrors errors = new WValidationErrors();
		add(errors);

		//
		// Textfields
		//
		WHeading heading = new WHeading(WHeading.MAJOR, "TextFields");
		add(heading);
		fieldLayout = new WFieldLayout();

		tf1 = new WTextField();
		fieldLayout.addField("WTextField - Plain", tf1);

		tf2 = new WTextField();
		tf2.setColumns(5);
		tf2.setMaxLength(6);
		tf2.setMandatory(true);
		fieldLayout.addField("WTextField - Mandatory, 5 columns, max length 6", tf2);

		tf3 = new WTextField();
		tf3.setColumns(5);
		fieldLayout.addField("WTextField - Disabled, 5 columns", tf3);

		tf4 = new WTextField();
		fieldLayout.addField("WTextField - Plain and initially null", tf4);
		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Numeric fields
		//
		heading = new WHeading(WHeading.MAJOR, "Numeric fields");
		add(heading);
		fieldLayout = new WFieldLayout();
		nf1 = new WNumberField();
		fieldLayout.addField("WNumberField - Plain", nf1);

		nf2 = new WNumberField();
		nf2.setDecimalPlaces(0);
		fieldLayout.addField("WNumberField - Zero Decimal Places", nf2);

		nf3 = new WNumberField();
		nf3.setMinValue(0);
		fieldLayout.addField("WNumberField - Minimum of zero", nf3);

		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// email fields.
		//
		heading = new WHeading(WHeading.MAJOR, "Email fields");
		add(heading);
		fieldLayout = new WFieldLayout();
		ef1 = new WEmailField();
		fieldLayout.addField("WEmailField", ef1);

		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Dropdowns
		//
		heading = new WHeading(WHeading.MAJOR, "Dropdowns");
		add(heading);
		fieldLayout = new WFieldLayout();
		drop1 = new WDropdown(new String[]{"One", "Two", "Three"});
		fieldLayout.addField("WDropdown", drop1);

		drop2 = new WDropdown(new String[]{null, "Cat", "Dog", "Elephant", "Mouse"});
		fieldLayout.addField("WDropdown - blank option", drop2);
		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Single Select
		//
		heading = new WHeading(WHeading.MAJOR, "Single Select");
		add(heading);
		fieldLayout = new WFieldLayout();
		single1 = new WSingleSelect(new String[]{"One", "Two", "Three"});
		fieldLayout.addField("WSingleSelect", single1);
		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Multi Select
		//
		heading = new WHeading(WHeading.MAJOR, "Multi select - List Box");
		add(heading);
		fieldLayout = new WFieldLayout();
		multi1 = new WMultiSelect(new String[]{"Circle", "Oval", "Rectangle", "Square", "Triangle"});
		fieldLayout.addField("WMultiSelect - String[]", multi1);

		multi2 = new WMultiSelect("icao");
		multi2.setRows(10);
		fieldLayout.addField("WMultiSelect - table with 10 rows", multi2);

		multiCb = new WCheckBoxSelect("sex");
		fieldLayout.addField("WCheckBoxSelect", multiCb);

		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Radios
		//
		heading = new WHeading(WHeading.MAJOR, "Radio Button Select");
		add(heading);
		rbSelect = new WRadioButtonSelect(new String[]{"poor", "good", "great"});
		rbSelect.setButtonLayout(WRadioButtonSelect.LAYOUT_FLAT);

		fieldLayout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		fieldLayout.addField("WRadioButtonSelect", rbSelect);

		radioButtonGroup = new RadioButtonGroup();
		add(radioButtonGroup);

		rb1 = radioButtonGroup.addRadioButton("good");
		rb2 = radioButtonGroup.addRadioButton("better");
		rb3 = radioButtonGroup.addRadioButton("best");

		// this has been added to its own field layout so the
		// "RadioButtonGroup" label could be added
		// to the example. if this is being done for a
		// real system and this label is required
		// it really should be done as a WRadioButtonSelect.
		WFieldLayout rbFieldLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		rbFieldLayout.addField("good", rb1);
		rbFieldLayout.addField("better", rb2);
		rbFieldLayout.addField("best", rb3);

		WFieldSet rbFieldSet = new WFieldSet("How good?");
		rbFieldSet.add(rbFieldLayout);
		fieldLayout.addField("RadioButtonGroup", rbFieldSet);

		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Checkboxes
		//
		heading = new WHeading(WHeading.MAJOR, "Check Boxes");
		add(heading);
		cb1 = new WCheckBox();
		cb2 = new WCheckBox();
		cb3 = new WCheckBox();

		fieldLayout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		fieldLayout.addField("Breakfast", cb1);
		fieldLayout.addField("Lunch", cb2);
		fieldLayout.addField("Dinner", cb3);
		add(fieldLayout);
		add(new WHorizontalRule());

		//
		// Textareas
		//
		heading = new WHeading(WHeading.MAJOR, "Text Areas");
		add(heading);
		fieldLayout = new WFieldLayout();
		ta = new WTextArea();
		ta.setColumns(40);
		ta.setRows(8);

		fieldLayout.addField("WTextArea", ta);
		add(fieldLayout);
		add(new WHorizontalRule());

		// Button
		heading = new WHeading(WHeading.MAJOR, "Buttons");
		add(heading);

		WText description = new WText("Press button to copy values into textarea");
		add(description);

		fieldLayout = new WFieldLayout();

		WButton copyBtn = new WButton("Copy");
		fieldLayout.addField("WButton - Plain", copyBtn);

		WButton copylnk = new WButton("Copy");
		copylnk.setRenderAsLink(true);
		fieldLayout.addField("WButton - render as link", copylnk);

		add(fieldLayout);
		add(new WHorizontalRule());

		ValidatingAction action = new ValidatingAction(errors, this) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				copy();
			}
		};

		copyBtn.setAction(action);
		copylnk.setAction(action);
	}

	/**
	 * Override preparePaintComponent to test that dynamic attributes are handled correctly.
	 *
	 * @param request the request that triggered the paint.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			// Default some values.
			tf3.setDisabled(true);

			tf1.setText("blah blah");
			tf2.setText("abc");
			tf3.setText("abc");

			setInitialised(true);
		}
	}

	/**
	 * Implementation of control logic in handleRequest.
	 */
	public void copy() {
		StringBuffer sb = new StringBuffer();

		sb.append("Text 1 = ");
		if (tf1.getText() != null) {
			sb.append(tf1.getText());
		}
		sb.append("\nText 2 = ");
		if (tf2.getText() != null) {
			sb.append(tf2.getText());
		}
		sb.append("\nText 3 = ");
		if (tf3.getText() != null) {
			sb.append(tf3.getText());
		}
		sb.append("\nText 4 = ");
		if (tf4.getText() != null) {
			sb.append(tf4.getText());
		}

		sb.append("\nNumeric 1 = ");
		if (nf1.getText() != null) {
			sb.append(nf1.getText());
		}
		sb.append("\nNumeric 2 = ");
		if (nf2.getText() != null) {
			sb.append(nf2.getText());
		}
		sb.append("\nNumeric 3 = ");
		if (nf3.getText() != null) {
			sb.append(nf3.getText());
		}

		sb.append("\nEmail = ");
		if (ef1.getText() != null) {
			sb.append(ef1.getText());
		}

		if (drop1.getSelected() != null) {
			sb.append("\nDrop 1 = ").append(drop1.getSelected());
		}

		if (drop2.getSelected() != null) {
			sb.append("\nDrop 2 = ").append(drop2.getSelected());
		}

		sb.append("\nMulti1 = ");
		List selected = multi1.getSelected();

		if (selected != null) {
			for (int i = 0; i < selected.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}

				sb.append(selected.get(i).toString());
			}
		}

		sb.append("\nMulti2 = ");
		selected = multi2.getSelected();

		if (selected != null) {
			for (int i = 0; i < selected.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}

				TableEntry entry = (TableEntry) selected.get(i);
				sb.append(entry.getCode());
			}
		}

		sb.append("\nMultiCb = ");
		List selectedOptions = multiCb.getSelected();

		for (int i = 0; i < selectedOptions.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}

			TableEntry entry = (TableEntry) selectedOptions.get(i);
			sb.append(entry.getCode());
		}

		if (rbSelect.getSelected() != null) {
			String radioValue = rbSelect.getSelected().toString();
			sb.append("\nWRadioButtonSelect = ").append(radioValue);
		}

		if (radioButtonGroup.getSelectedValue() != null) {
			String radioValue = radioButtonGroup.getSelectedValue().toString();
			sb.append("\nRadioButtonGroup = ").append(radioValue);
		}

		sb.append("\nCheckboxes =");

		if (cb1.isSelected()) {
			sb.append(' ').append(cb1.getLabel().getText());
		}

		if (cb2.isSelected()) {
			sb.append(' ').append(cb2.getLabel().getText());
		}

		if (cb3.isSelected()) {
			sb.append(' ').append(cb3.getLabel().getText());
		}

		sb.append('\n');

		ta.setText(sb.toString());
	}

}
