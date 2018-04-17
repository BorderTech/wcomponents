package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

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
		final WMessages messages = new WMessages();
		add(messages);
		add(new WHeading(HeadingLevel.H2, "Normal field set"));
		addFieldSet("Enter your address");

		//various settings for frame type
		add(new WHeading(HeadingLevel.H2, "Examples of WFieldSet FrameType"));
		add(new WHeading(HeadingLevel.H3, "FrameType NONE"));
		addFieldSet("Enter your address in a borderless fieldset with no visible legend",
				WFieldSet.FrameType.NONE);
		add(new WHeading(HeadingLevel.H3, "FrameType NO_BORDER"));
		addFieldSet("Enter your address in a borderless fieldset", WFieldSet.FrameType.NO_BORDER);
		add(new WHeading(HeadingLevel.H3, "FrameType NO_TEXT"));
		addFieldSet("Enter your address in a fieldset with a hidden legend",
				WFieldSet.FrameType.NO_TEXT);

		WFieldSet fs = new WFieldSet("Phone");
		add(fs);
		fs.setMargin(new Margin(null, null, Size.LARGE, null));
		fs.setMandatory(true);
		WFieldLayout layout = new WFieldLayout();
		fs.add(layout);
		layout.addField("home", new WPhoneNumberField());
		layout.addField("work", new WPhoneNumberField());
		layout.addField("mobile", new WPhoneNumberField());
		layout.addField("cat", new WPhoneNumberField());
		WButton validateButton = new WButton("Save");
		validateButton.setAction(new ValidatingAction(messages.getValidationErrors(), fs) {
			@Override
			public void executeOnValid(ActionEvent event) {
				messages.reset();
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE, "All done"));
			}
		});
		layout.addField(validateButton);

		add(new WHeading(HeadingLevel.H2, "WFieldSet anti patterns"));
		add(new WHeading(HeadingLevel.H3, "No legend"));
		addFieldSet(null);
		add(new WHeading(HeadingLevel.H3, "Empty legend"));
		addFieldSet("");
		add(new WHeading(HeadingLevel.H3, "Almost empty legend"));
		addFieldSet(" ");
		add(new WHeading(HeadingLevel.H3, "Another almost empty legend"));
		addFieldSet("\u00a0");


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
		fieldset.setMargin(new Margin(null, null, Size.LARGE, null));
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
