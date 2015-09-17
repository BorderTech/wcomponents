package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;

/**
 * This example demonstrates the use of the submitOnChange flag available on {@link WDropdown}. When you change the
 * selected state in the state dropdown, the options available in the region dropdown are changed to match. Also, if you
 * select "ACT", you get a special message displayed. You should endeavour to avoid this setting.
 *
 * @author Martin Shevchenko
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WDropdownSubmitOnChangeExample extends WContainer {

	/**
	 * State constant for the ACT.
	 */
	private static final String STATE_ACT = "ACT";
	/**
	 * State constant for NSW.
	 */
	private static final String STATE_NSW = "NSW";
	/**
	 * State constant for VIC.
	 */
	private static final String STATE_VIC = "VIC";

	/**
	 * The dropdown used to select a state.
	 */
	private final WDropdown stateSelector = new WDropdown();

	/**
	 * The dropdown used to select a region within a state.
	 */
	private final WDropdown regionSelector = new WDropdown();

	/**
	 * A Message to display when "ACT" is selected from the state selector.
	 */
	private final WText actMessage = new WText(
			"<strong>Australian Capital Territory</strong> - the heart of the nation!");

	/**
	 * Creates a WDropdownSubmitOnChangeExample.
	 */
	public WDropdownSubmitOnChangeExample() {
		actMessage.setEncodeText(false);

		WStyledText text = new WStyledText(
				"Any form control component which is not a WButton will show a visiable warning in its label if its submitOnChange property is set true.");
		text.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		add(text);

		WFieldLayout flay = new WFieldLayout();
		add(flay);
		flay.setLabelWidth(25);
		flay.setMargin(new Margin(0, 0, 12, 0));
		flay.addField("State", stateSelector);
		flay.addField("Region", regionSelector);

		// Used to test control of visibility as part of submit on change.
		add(actMessage);
		actMessage.setVisible(false);

		stateSelector.setOptions(new String[]{null, STATE_ACT, STATE_NSW, STATE_VIC});

		//This is the flag which causes accessibility problems. It may be removed completely in future versions of WComponents.
		//you should use AJAX instead
		stateSelector.setSubmitOnChange(true);
		stateSelector.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				updateRegion();
				regionSelector.resetData();
			}
		});
	}

	/**
	 * Updates the options present in the region selector, depending on the state selector's value.
	 */
	private void updateRegion() {
		actMessage.setVisible(false);

		String state = (String) stateSelector.getSelected();

		if (STATE_ACT.equals(state)) {
			actMessage.setVisible(true);
			regionSelector.setOptions(new String[]{null, "Belconnen", "City", "Woden"});
		} else if (STATE_NSW.equals(state)) {
			regionSelector.setOptions(
					new String[]{null, "Hunter", "Riverina", "Southern Tablelands"});
		} else if (STATE_VIC.equals(state)) {
			regionSelector.setOptions(
					new String[]{null, "Gippsland", "Melbourne", "Mornington Peninsula"});
		} else {
			regionSelector.setOptions(new Object[]{null});
		}
	}
}
