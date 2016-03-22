package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 * <p>
 * This component demonstrates the use and abuse of {@link WLabel}.</p>
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WFieldInputWidthExample extends WPanel {

	/**
	 * just a sample WTextField for one of the more obscure examples.
	 */
	private final WTextField whatTextField = new WTextField();

	/**
	 * Creates a WLabelExample.
	 */
	public WFieldInputWidthExample() {
		add(new WHeading(HeadingLevel.H2, "WLabel Patterns"));

		WFieldLayout fieldsFlat = new WFieldLayout();
		// fieldsFlat.setLabelWidth(25);
		// fieldsFlat.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));
		add(fieldsFlat);

		/*
		 * WLabel inferred by WFieldLayout.addField(String, WComponent)
		 */
		fieldsFlat.addField("Normal input component", new WTextField()).setInputWidth(100);

		/* hidden WLabel (real label) */
		WLabel hiddenLabel = new WLabel("Address line two");
		hiddenLabel.setHidden(true);
		fieldsFlat.addField(hiddenLabel, new WTextField()).setInputWidth(100);
		/* hidden WLabel (fake label) */
		hiddenLabel = new WLabel("A hidden label for a read only field");
		hiddenLabel.setHidden(true);
		WTextField roField = new WTextField();
		roField.setReadOnly(true);
		roField.setText("This is a read only field with a hidden label");
		fieldsFlat.addField(hiddenLabel, roField).setInputWidth(100);

		fieldsFlat.addField("A date", new WDateField()).setInputWidth(100);
		fieldsFlat.addField("A partial date", new WPartialDateField()).setInputWidth(100);



		WSuggestions suggestions = new WSuggestions("icao");
		add(suggestions);
		WTextField text = new WTextField();
		text.setSuggestions(suggestions);
		fieldsFlat.addField("Suggestions", text).setInputWidth(100);
		/*
		 * WFieldLayout does magic things with radio buttons and check boxes
		 */
		fieldsFlat.addField("WCheckBox", new WCheckBox());
		RadioButtonGroup group1 = new RadioButtonGroup();
		add(group1);
		WRadioButton rb1 = group1.addRadioButton(1);
		WRadioButton rb2 = group1.addRadioButton(2);
		fieldsFlat.addField("I like bananas", rb1).setInputWidth(100);
		fieldsFlat.addField("I dislike bananas", rb2).setInputWidth(100);

		/*
		 * WLabel can be 'for' a form input component which is NOT a labellable
		 * component in HTML. These are usually components which output a rich
		 * set of controls. In this case the WLabel will become the (off-screen)
		 * legend of the controls fieldset and the on-screen version in the
		 * WFieldLayout is just a placeholder
		 */
		WCheckBoxSelect cbSelect = new WCheckBoxSelect(
				new String[]{"Apple", "Cherry", "Orange", "Pineapple"});
		cbSelect.setFrameless(true);
		fieldsFlat.addField("Select one or more options", cbSelect).setInputWidth(100);

		/*
 * Explicit use of WLabel in addField
 * Sometimes you have to add other things to a WLabel...
		 */
		WLabel theLabel = new WLabel("WDropdown");
		theLabel.setHint("required");
		WDropdown theDropdown = new WDropdown(new String[]{null,
			"option one",
			"option two",
			"option three"});
		theDropdown.setMandatory(true);
		fieldsFlat.addField(theLabel, theDropdown).setInputWidth(100);

		/*
		 * Some things are labellable, even though it does not seem sensible. You
		 * must use an explicit WLabel to associate a WLabel with a WComponent
		 * which outputs a HTML labellable element but which is not a WInput
		 */
		WProgressBar pBar = new WProgressBar(100); //yes, WProgressBar outputs a labellable control but please don't do this!
		pBar.setValue(67);
		WLabel pBarLabel = new WLabel("WProgressBar", pBar);
		fieldsFlat.addField(pBarLabel, pBar).setInputWidth(100);

		/*
		 * You would also have to do that if you needed the WLabel elsewhere or
		 * has to override something.
		 * NOTE: This is a VERY BAD example.
		 */
		theLabel = new WLabel() {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				//you would normally do something sensible here
				String inputText = whatTextField.getText();
				if (inputText != null) {
					setText(inputText);
					whatTextField.setText("");
				} else {
					setText("What?");
				}
			}
		};
		fieldsFlat.addField(theLabel, whatTextField).setInputWidth(100);

		/*
		 * Adding another WComponent to WLabel. WARNING: very few components are
		 * valid. If you ever do this you MUST check your UI in debug mode with
		 * client debugging turned on.
		 */
		theLabel = new WLabel("WTextArea");
		WStyledText msg = new WStyledText(" these notes are important");
		msg.setType(WStyledText.Type.HIGH_PRIORITY);
		theLabel.add(msg);
		fieldsFlat.addField(theLabel, new WTextArea()).setInputWidth(100);


		/*
		 * The use of a WLabel "for" a WFieldSet is a special case...
		 * You probably shouldn't do this, but it does allow a WFieldSet
		 * With a complex inner set of fields to be included in a WFieldLayout
		 * and still meet rigid layout constraints.
		 *
		 * It would be better to not setFrameType NONE and use a null label
		 */
		// We re-use this string for both the WLabel and the WFieldSet's label
		String labelText = "Enter the dates of entry and exit";

		//make a WFieldSet
		WFieldSet dateRangeFS = new WFieldSet(labelText);
		dateRangeFS.setFrameType(WFieldSet.FrameType.NONE);
		//Make a WFieldLayout to add the WDateFields */
		WFieldLayout innerLayout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		innerLayout.setLabelWidth(17);
		innerLayout.addField("from", new WDateField()).setInputWidth(100);
		innerLayout.addField("to", new WDateField()).setInputWidth(100);
		//add the WFieldLayout to the WFieldSet
		dateRangeFS.add(innerLayout);
		//then just call addField using the WFieldSet as the input WComponent.
		fieldsFlat.addField(labelText, dateRangeFS).setInputWidth(100);


		/* WLabel as null - convoluted example:
		 *
		 * If, for some reason, you have to add individual radio buttons rather than using
		 * WRadioButtonSelect you are going to need this WFieldSet trick because
		 * WRadioButtons are always the answers, never the questions.
		 * In the light of this, the next sample is not one I would ever like to see.
		 * It uses a null label to prevent double labelling a form control. If we
		 * had used a WLabel or String in addField then the first WRadioButton
		 * would have had two labels.
		 *
		 * We are going to add a couple of WRadioButtons which have images in their labels
		 */
		group1 = new RadioButtonGroup();
		add(group1);
		rb1 = group1.addRadioButton(1);
		rb2 = group1.addRadioButton(2);

		//make the labels for the radio buttons
		WLabel rb1Label = new WLabel("", rb1);
		rb1Label.add(new WImage("/image/success.png", "I still like bananas"));
		WLabel rb2Label = new WLabel("", rb2);
		rb2Label.add(new WImage("/image/error.png", "I still dislike bananas"));
		// Now it gets confusing. We want the radio buttons to flow with their labels but be apart from each other...
		//The WPanel which flowLayout and hgap will make the two control:label pairs sit apart from each other
		WPanel rbLayout = new WPanel();
		rbLayout.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0,
				FlowLayout.ContentAlignment.BASELINE));
		//then we use WContainer to add each control:label pair to the WPanel
		WContainer rbContainer = new WContainer();
		rbLayout.add(rbContainer);
		//if you do not use WFieldLayout you have to remember that you MUST put a
		//WRadioButton or WCheckBox BEFORE its label and any other component AFTER
		//its label.
		rbContainer.add(rb1);
		rbContainer.add(rb1Label);
		//repeat for the second pair
		rbContainer = new WContainer();
		rbLayout.add(rbContainer);
		rbContainer.add(rb2);
		rbContainer.add(rb2Label);
		//finally we add the WPanel to the WFieldLayout using a null label
		fieldsFlat.addField((WLabel) null, rbLayout).setInputWidth(100);


		/*
		 * WLabel as null - simple example
		 *
		 * You can use addField with a null label if the 'input' does not
		 * require a label. This is useful for adding WFieldSet or complex
		 * nested layouts to a WFieldLayout
		 */
		fieldsFlat.addField((WLabel) null, new WButton("Save"));

	}
}
