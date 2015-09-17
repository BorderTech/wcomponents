package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.AjaxTarget;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;

/**
 * This example demonstrates how an {@link Action} object can be associated with a WDropdown (use
 * {@link WDropdown#setActionOnChange(com.github.bordertech.wcomponents.Action)} ). Changing the selection of the
 * dropdown will execute the action.
 *
 * @author Christina Harris
 * @since 1.0.0
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WDropdownTriggerActionExample extends WContainer {

	/**
	 * State options.
	 */
	private static final String[] STATES = {null, "ACT", "VIC"};
	/**
	 * ACT region options.
	 */
	private static final String[] ACT_REGIONS = new String[]{null, "Tuggeranong", "Woden"};
	/**
	 * VIC region options.
	 */
	private static final String[] VIC_REGIONS = new String[]{null, "Melbourne", "Mornington Peninsula"};
	/**
	 * Melbourne suburb options.
	 */
	private static final String[] MELBOURNE_SUBURBS = new String[]{null, "Doncaster", "Clayton", "Blackburn"};
	/**
	 * Mornington Peninsula suburb options.
	 */
	private static final String[] MORNINGTON_SUBURBS = new String[]{null, "Rosebud", "McCrae", "Dromana"};
	/**
	 * Woden suburb options.
	 */
	private static final String[] WODEN_SUBURBS = new String[]{null, "Phillp", "Torrens", "Lyons"};
	/**
	 * Tuggeranong suburb options.
	 */
	private static final String[] TUGGERANONG_SUBURBS = new String[]{null, "Greenway", "Oxley", "Monash"};

	/**
	 * The state selection dropdown.
	 */
	private final WDropdown state = new WDropdown();

	/**
	 * The region selection dropdown.
	 */
	private final WDropdown region = new WDropdown();

	/**
	 * The suburb selection dropdown.
	 */
	private final WDropdown suburb = new WDropdown();

	/**
	 * Creates a WDropdownTriggerActionExample.
	 */
	public WDropdownTriggerActionExample() {
		WFieldSet addressSet = new WFieldSet("Example address segments");
		add(addressSet);
		WFieldLayout flay = new WFieldLayout();
		addressSet.add(flay);
		flay.setLabelWidth(25);
		flay.addField("State", state);
		flay.addField("Region", region);
		flay.addField("Suburb", suburb);

		state.setOptions(getStates());

		// the action on the state dropdown will populate the appropriate
		// regions list and clear the suburb list.
		state.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String stateSelected = (String) state.getSelected();
				region.setOptions(getRegions(stateSelected));
				region.resetData();
				suburb.setOptions((String[]) null);
				suburb.resetData();
			}
		});

		/*
         * You should NEVER use submitOnChange with a WDropdown. Therefore any
         * action on change should be set using AJAX.
		 */
		add(new WAjaxControl(state, new AjaxTarget[]{region, suburb}));

		// the action on the region dropdown populates the appropriate suburb
		// list.
		region.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String regionSelected = (String) region.getSelected();
				suburb.setOptions(getSuburbs(regionSelected));
				suburb.resetData();
			}
		});

		add(new WAjaxControl(region, suburb));

	}

	/**
	 * @return the state selection dropdown.
	 */
	public WDropdown getStateDropdown() {
		return state;
	}

	/**
	 * @return the region selection dropdown.
	 */
	public WDropdown getRegionDropdown() {
		return region;
	}

	/**
	 * @return the suburb selection dropdown.
	 */
	public WDropdown getSuburbDropdown() {
		return suburb;
	}

	// ----------------------------------------------------------
	// Strings for the dropdowns.
	//
	/**
	 * @return the available states.
	 */
	private static String[] getStates() {
		return STATES;
	}

	/**
	 * Retrieves the regions in a state.
	 *
	 * @param state the state.
	 * @return the regions in the given state.
	 */
	private static String[] getRegions(final String state) {
		if ("ACT".equals(state)) {
			return ACT_REGIONS;

		} else if ("VIC".equals(state)) {
			return VIC_REGIONS;
		} else {
			return null;
		}

	}

	/**
	 * Retrieves the suburbs in a region.
	 *
	 * @param region the region.
	 * @return the suburbs in the given region.
	 */
	private static String[] getSuburbs(final String region) {
		if ("Tuggeranong".equals(region)) {
			return TUGGERANONG_SUBURBS;
		} else if ("Woden".equals(region)) {
			return WODEN_SUBURBS;
		} else if ("Melbourne".equals(region)) {
			return MELBOURNE_SUBURBS;
		} else if ("Mornington Peninsula".equals(region)) {
			return MORNINGTON_SUBURBS;
		} else {
			return null;
		}
	}
}
