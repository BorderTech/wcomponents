package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;

/**
 * This component demonstrates the usage of the {@link WFieldSet} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldSetExample extends WPanel {

	/**
	 * Creates a WFieldSetExample.
	 */
	public WFieldSetExample() {
		add(new WHeading(WHeading.MAJOR, "Normal field set"));
		addFieldSet("Enter your address");

		//various settings for frame type
		add(new WHeading(WHeading.MAJOR, "Examples of WFieldSet FrameType"));
		add(new WHeading(WHeading.SECTION, "FrameType NONE"));
		addFieldSet("Enter your address in a borderless fieldset with no visible legend",
				WFieldSet.FrameType.NONE);
		add(new WHeading(WHeading.SECTION, "FrameType NO_BORDER"));
		addFieldSet("Enter your address in a borderless fieldset", WFieldSet.FrameType.NO_BORDER);
		add(new WHeading(WHeading.SECTION, "FrameType NO_TEXT"));
		addFieldSet("Enter your address in a fieldset with a hidden legend",
				WFieldSet.FrameType.NO_TEXT);
	}

	/**
	 * Creates a WFieldSet with content and a given FrameType.
	 *
	 * @param title The title to give to the WFieldSet.
	 * @param type The decorative model of the WFieldSet
	 * @return a WFieldSet with form control content.
	 */
	private WFieldSet addFieldSet(final String title, final WFieldSet.FrameType type) {
		final WFieldSet fieldset = new WFieldSet(title);
		fieldset.setFrameType(type);
		fieldset.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
		final WFieldLayout layout = new WFieldLayout();
		fieldset.add(layout);
		layout.setLabelWidth(25);
		layout.addField("Street address", new WTextField());
		final WField add2Field = layout.addField("Street address line 2", new WTextField());
		add2Field.getLabel().setHidden(true);
		layout.addField("Suburb", new WTextField());
		layout.addField("State/Territory", new WDropdown(
				new String[]{"", "ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"}));
		//NOTE: this is an Australia-specific post code field. An Australian post code is not a number as they may contain a leading zero.
		final WTextField postcode = new WTextField();
		postcode.setMaxLength(4);
		postcode.setColumns(4);
		postcode.setMinLength(3);
		layout.addField("Postcode", postcode);
		add(fieldset);
		return fieldset;
	}

	/**
	 * Creates a WFieldSet with a normal frame and visible legend. The legend content is the WFieldSet title.
	 *
	 * @param title The title of the WFieldSet.
	 * @return a WFieldSet with a frame and form control content.
	 */
	private WFieldSet addFieldSet(final String title) {
		return addFieldSet(title, WFieldSet.FrameType.NORMAL);
	}
}
