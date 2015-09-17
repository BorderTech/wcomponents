package com.github.bordertech.wcomponents.examples.theme.ajax;

import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.examples.WDropdownTriggerActionExample;

/**
 * In this example the {@link WDropdownTriggerActionExample} component is made ajax aware. The state dropdown becomes an
 * ajax trigger that updates the region and suburb dropdowns (ajax targets). The region dropdown becomes an ajax trigger
 * that updates the suburb dropdown. No longer is a full page reload required for the dropdown updates.
 *
 * @author Christina Harris
 * @since 1.0.0
 */
public class AjaxWDropdownExample extends WContainer {

	/**
	 * Creates an AjaxWDropdownExample.
	 */
	public AjaxWDropdownExample() {
		WDropdownTriggerActionExample example = new WDropdownTriggerActionExample();

		// create an ajax control that will update the region and suburb dropdowns when the state dropdown changes.
		WAjaxControl updateRegion = new WAjaxControl(example.getStateDropdown());
		updateRegion.addTarget(example.getRegionDropdown());
		updateRegion.addTarget(example.getSuburbDropdown());

		// create an ajax control that will update the suburb dropdown when the region dropdown changes.
		WAjaxControl updateSuburb = new WAjaxControl(example.getRegionDropdown());
		updateSuburb.addTarget(example.getSuburbDropdown());

		add(example);
		add(updateRegion);
		add(updateSuburb);
	}
}
