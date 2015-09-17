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
import com.github.bordertech.wcomponents.WProgressBar;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout;

/**
 * <p>
 * This component demonstrates the use and abuse of {@link WLabel}.</p>
 *
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WLabelExample extends WPanel {

	/**
	 * just a sample WTextField for one of the more obscure examples.
	 */
	private final WTextField whatTextField = new WTextField();

	/**
	 * Creates a WLabelExample.
	 */
	public WLabelExample() {
		add(new WHeading(HeadingLevel.H2, "WLabel Patterns"));

		WFieldLayout fieldsFlat = new WFieldLayout();
		// fieldsFlat.setLabelWidth(33);
		fieldsFlat.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));
		add(fieldsFlat);

		/*
		 * WLabel inferred by WFieldLayout.addField(String, WComponent)
		 */
		fieldsFlat.addField("Normal input component", new WTextField());

		/* hidden WLabel (real label) */
		WLabel hiddenLabel = new WLabel("Address line two");
		hiddenLabel.setHidden(true);
		fieldsFlat.addField(hiddenLabel, new WTextField());
		/* hidden WLabel (fake label) */
		hiddenLabel = new WLabel("A hidden label for a read only field");
		hiddenLabel.setHidden(true);
		WTextField roField = new WTextField();
		roField.setReadOnly(true);
		roField.setText("This is a read only field with a hidden label");
		fieldsFlat.addField(hiddenLabel, roField);
		/*
		 * WFieldLayout does magic things with radio buttons and check boxes
		 */
		fieldsFlat.addField("WCheckBox", new WCheckBox());
		RadioButtonGroup group1 = new RadioButtonGroup();
		add(group1);
		WRadioButton rb1 = group1.addRadioButton(1);
		WRadioButton rb2 = group1.addRadioButton(2);
		fieldsFlat.addField("I like bananas", rb1);
		fieldsFlat.addField("I dislike bananas", rb2);

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
		fieldsFlat.addField("Select one or more options", cbSelect);

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
		fieldsFlat.addField(theLabel, theDropdown);

		/*
		 * Some things are labellable, even though it does not seem sensible. You
		 * must use an explicit WLabel to associate a WLabel with a WComponent
		 * which outputs a HTML labellable element but which is not a WInput
		 */
		WProgressBar pBar = new WProgressBar(100); //yes, WProgressBar outputs a labellable control but please don't do this!
		pBar.setValue(67);
		WLabel pBarLabel = new WLabel("WProgressBar", pBar);
		fieldsFlat.addField(pBarLabel, pBar);

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
		fieldsFlat.addField(theLabel, whatTextField);

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
		innerLayout.addField("from", new WDateField());
		innerLayout.addField("to", new WDateField());
		//add the WFieldLayout to the WFieldSet
		dateRangeFS.add(innerLayout);
		//then just call addField using the WFieldSet as the input WComponent.
		fieldsFlat.addField(labelText, dateRangeFS);


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
		fieldsFlat.addField((WLabel) null, rbLayout);


		/*
		 * WLabel as null - simple example
		 *
		 * You can use addField with a null label if the 'input' does not
		 * require a label. This is useful for adding WFieldSet or complex
		 * nested layouts to a WFieldLayout
		 */
		fieldsFlat.addField((WLabel) null, new WButton("Save"));


		/*
		 * Examples showing WLabel with a nested input control WComponent.
		 * This is VERY dangerous as only a very few WComponents are valid for
		 * this scenario. If you go down this route: stop!!
		 * These are really here for framework testing, not as examples as to
		 * how to do things.
		 */
		add(new WHeading(HeadingLevel.H2, "Label nesting which is technically OK"));
		/*
		 * Just because it is OK to do this does not mean you should!
		 * So these examples have far fewer comments.
		 */
		WPanel errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 12));
		errorLayoutPanel.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));
		add(errorLayoutPanel);
		errorLayoutPanel.add(new ExplanatoryText(
				"This example shows WLabels with a single nested simple form control WTextField. This is not a contravention of the HTML specification but you should not do it."));
		WLabel outerLabel = new WLabel("Label with nested WTextField and not 'for' anything");
		errorLayoutPanel.add(outerLabel);
		outerLabel.add(new WTextField());

		WTextField innerField = new WTextField();
		outerLabel = new WLabel("Label 'for' nested WTextField", innerField);
		errorLayoutPanel.add(outerLabel);
		outerLabel.add(innerField);


		/*
		 * DO NOT use the following as examples of what to do: these are examples
		 * of what NOT to do.
		 */
		add(new WHeading(WHeading.MAJOR, "WLabel anti-patterns"));
		add(new ExplanatoryText(
				"These are here for testing purposes and must not be used as examples to follow.\nTurn on client debugging to get much more information."));
		add(new WHeading(HeadingLevel.H3, "Poor but not erroneous uses of WLabel"));
		errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 12));
		add(errorLayoutPanel);
		//label not for anything should not be a WLabel
		errorLayoutPanel.add(new WLabel("I am not 'for' anything"));
		//WLabel for something which is not labellable
		errorLayoutPanel.add(new WLabel("I am for a component which should not be labelled",
				fieldsFlat));
		//If the WLabel is 'for' something that is not in the tree it becomes 'for' the WApplication
		//TODO: this is not necessarily a good thing!!!
		WCheckBox notHere = new WCheckBox();
		errorLayoutPanel.add(new WLabel("My component wasn't added", notHere));

		/*
		 * The examples which follow MUST NEVER BE USED! They cause ERRORS.
		 * They are here purely for framework testing.
		 */
		add(new WHeading(HeadingLevel.H3, "Very bad uses of WLabel"));
		errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, 0, 12));
		add(errorLayoutPanel);

		/*
		 * Nested WLabels: very bad
		 */
		errorLayoutPanel.add(new ExplanatoryText(
				"This example shows nested WLabels. This is a contravention of the HTML specification."));

		WPanel nestingErrorPanel = new WPanel();
		//nestingErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT,12,0,FlowLayout.ContentAlignment.BASELINE));
		nestingErrorPanel.setLayout(new ColumnLayout(new int[]{50, 50}, 12, 6));
		errorLayoutPanel.add(nestingErrorPanel);
		WTextField outerField = new WTextField();
		outerLabel = new WLabel("I am an outer label", outerField);
		nestingErrorPanel.add(outerLabel);

		innerField = new WTextField();
		WLabel innerLabel = new WLabel("Inner label", innerField);
		//add the inner label to the outer label: this is the ERROR
		outerLabel.add(innerLabel);
		nestingErrorPanel.add(innerField);
		nestingErrorPanel.add(outerField);

		/*
		 * It is permissible to place certain simple form control components into
		 * a WLabel under the following conditions:
		 * there must be no more than one such component in the WLabel;
		 * the component MUST be one which outputs a simple HTML form control
		 * (and I am not going to tell you which they are);
		 * The WLabel must be 'for' the nested component or not 'for' anything.
		 */
		errorLayoutPanel.add(new ExplanatoryText(
				"This example shows a WLabel with a nested simple form control WTextField but the WLabel is not 'for' the WTextField. This is a contravention of the HTML specification."));

		WTextField notMyField = new WTextField();
		notMyField.setToolTip("This field should not be in the label it is in");

		WTextField myField = new WTextField();
		WLabel myFieldLabel = new WLabel("I am not the label for my nested text field", myField);
		nestingErrorPanel = new WPanel();
		nestingErrorPanel.setLayout(new ColumnLayout(new int[]{50, 50}, 12, 6));
		errorLayoutPanel.add(nestingErrorPanel);
		nestingErrorPanel.add(myFieldLabel);
		nestingErrorPanel.add(myField);
		//adding the 'wrong' WTextField to a WLabel is what causes this error
		myFieldLabel.add(notMyField);
		add(new ExplanatoryText("The next field has a label explicitly set to only white space."));
		WTextField emptyLabelTextField = new WTextField();
		WLabel emptyLabel = new WLabel(" ", emptyLabelTextField);
		add(emptyLabel);
		add(emptyLabelTextField);
	}
}
