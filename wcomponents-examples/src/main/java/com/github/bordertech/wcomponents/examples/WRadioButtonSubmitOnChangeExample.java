package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;

/**
 * This example demonstrates the use of the submitOnChange flag available on WDropdown. When you change the selected
 * state in the state dropdown, the options available in the region dropdown are changed to match. Also, if you select
 * "ACT", you get a special message displayed.
 *
 * @author Ming Gao
 * @since 1.0.0
 */
public class WRadioButtonSubmitOnChangeExample extends WContainer {

	private static final String ACT = "ACT";

	private static final String NSW = "NSW";

	private static final String VIC = "VIC";

	private final RadioButtonGroup rbgStateSelector = new RadioButtonGroup();

	private final WRadioButton rbtACT = rbgStateSelector.addRadioButton(ACT);

	private final WRadioButton rbtNSW = rbgStateSelector.addRadioButton(NSW);

	private final WRadioButton rbtVIC = rbgStateSelector.addRadioButton(VIC);

	private final WFieldSet regionFields = new WFieldSet("Region");

	private final WDropdown regionSelector = new WDropdown();

	private final WPanel actMessagePanel = new WPanel();

	/**
	 * Creates a WRadioButtonSubmitOnChangeExample.
	 */
	public WRadioButtonSubmitOnChangeExample() {
		rbgStateSelector.setSubmitOnChange(true);
		rbgStateSelector.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				updateRegion();
				regionSelector.resetData();
			}
		});

		WFieldSet fset = new WFieldSet("State");
		add(fset);
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		fset.add(layout);
		fset.setMargin(new Margin(Size.ZERO, Size.ZERO, Size.SMALL, Size.ZERO));

		layout.addField(ACT, rbtACT);
		layout.addField(NSW, rbtNSW);
		layout.addField(VIC, rbtVIC);

		add(rbgStateSelector);

		add(regionFields);
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		regionFields.add(layout);
		layout.addField("Region", regionSelector);

		regionFields.add(actMessagePanel);
		actMessagePanel.add(new WStyledText("Australian Capital Territory",
				WStyledText.Type.EMPHASISED));
		actMessagePanel.add(new WText(" - the heart of the nation!"));

		updateRegion();

	}

	/**
	 * Updates the visibility and options present in the region selector, depending on the state selector's value.
	 */
	private void updateRegion() {
		actMessagePanel.setVisible(false);

		if (rbtACT.isSelected()) {
			actMessagePanel.setVisible(true);
			regionFields.setVisible(true);
			regionSelector.setOptions(new String[]{null, "Belconnen", "City", "Woden"});
			regionSelector.setVisible(true);
		} else if (rbtNSW.isSelected()) {
			regionFields.setVisible(true);
			regionSelector.setOptions(
					new String[]{null, "Hunter", "Riverina", "Southern Tablelands"});
			regionSelector.setVisible(true);
		} else if (rbtVIC.isSelected()) {
			regionFields.setVisible(true);
			regionSelector.setOptions(
					new String[]{null, "Gippsland", "Melbourne", "Mornington Peninsula"});
			regionSelector.setVisible(true);
		} else {
			regionSelector.setOptions(new Object[]{null});
			regionSelector.setVisible(false);
			regionFields.setVisible(false);
		}
	}
}
