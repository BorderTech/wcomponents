package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
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
public final class WLabelExample extends WPanel {

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
		fieldsFlat.setMargin(new Margin(null, null, Size.XL, null));
		add(fieldsFlat);
		// WLabel inferred by WFieldLayout.addField(String, WComponent)
		fieldsFlat.addField("Normal input component", new WTextField());
		// hidden WLabel (real label)
		WLabel hiddenLabel = new WLabel("Address line two");
		hiddenLabel.setHidden(true);
		fieldsFlat.addField(hiddenLabel, new WTextField());
		// hidden WLabel (fake label)
		hiddenLabel = new WLabel("A hidden label for a read only field");
		hiddenLabel.setHidden(true);
		WTextField roField = new WTextField();
		roField.setReadOnly(true);
		roField.setText("This is a read only field with a hidden label");
		fieldsFlat.addField(hiddenLabel, roField);
		hiddenLabel = new WLabel("Hidden label for a compound control");
		hiddenLabel.setHidden(true);
		WCheckBoxSelect cbSelect = new WCheckBoxSelect(new String[]{"Apple", "Cherry", "Orange", "Pineapple", "control label is hidden"});
		fieldsFlat.addField(hiddenLabel, cbSelect);
		// WFieldLayout does magic things with radio buttons and check boxes
		fieldsFlat.addField("WCheckBox", new WCheckBox());
		RadioButtonGroup group1 = new RadioButtonGroup();
		add(group1);
		WRadioButton rb1 = group1.addRadioButton(1);
		WRadioButton rb2 = group1.addRadioButton(2);
		fieldsFlat.addField("I like bananas", rb1);
		fieldsFlat.addField("I dislike bananas", rb2);
		/* WLabel can be 'for' a form input component which is NOT a labellable component in HTML. These are usually components which output a rich
		 * set of controls. In this case the WLabel will become the (off-screen) legend of the controls fieldset and the on-screen version in the
		 * WFieldLayout is just a placeholder. */
		cbSelect = new WCheckBoxSelect(new String[]{"Apple", "Cherry", "Orange", "Pineapple"});
		cbSelect.setFrameless(true);
		fieldsFlat.addField("Select one or more options", cbSelect);
		/* Explicit use of WLabel in addField because sometimes you have to add other things to a WLabel. */
		WLabel theLabel = new WLabel("WDropdown");
		theLabel.setHint("required");
		WDropdown theDropdown = new WDropdown(new String[]{null, "option one", "option two", "option three"});
		theDropdown.setMandatory(true);
		fieldsFlat.addField(theLabel, theDropdown);
		/* Some things are labellable, even though it does not seem sensible. You must use an explicit WLabel to associate a WLabel with a WComponent
		 * which outputs a HTML labellable element but which is not a WInput. */
		WProgressBar pBar = new WProgressBar(100); //yes, WProgressBar outputs a labellable control but please don't do this!
		pBar.setValue(67);
		WLabel pBarLabel = new WLabel("WProgressBar", pBar);
		fieldsFlat.addField(pBarLabel, pBar);
		// You would also have to do that if you needed the WLabel elsewhere or has to override something. NOTE: This is a VERY BAD example.
		theLabel = new WLabel() {
			@Override
			protected void preparePaintComponent(final Request request) {
				super.preparePaintComponent(request);
				// you would normally do something sensible here
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
		/* Adding another WComponent to WLabel. WARNING: very few components are valid. If you ever do this you MUST check your UI in debug mode with
		 * client debugging turned on. */
		theLabel = new WLabel("WTextArea");
		WStyledText msg = new WStyledText(" these notes are important");
		msg.setType(WStyledText.Type.HIGH_PRIORITY);
		theLabel.add(msg);
		fieldsFlat.addField(theLabel, new WTextArea()).setInputWidth(100);
		/* The use of a WLabel "for" a WFieldSet is a special case. You probably shouldn't do this, but it does allow a WFieldSet with a complex
		 * inner set of fields to be included in a WFieldLayout and still meet rigid layout constraints. It would be better to not setFrameType NONE
		 * and use a null label. */
		// We re-use this string for both the WLabel and the WFieldSet's label
		String labelText = "Enter the dates of entry and exit";
		WFieldSet dateRangeFS = new WFieldSet(labelText);
		dateRangeFS.setFrameType(WFieldSet.FrameType.NONE);
		WFieldLayout innerLayout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
		innerLayout.setLabelWidth(17);
		innerLayout.addField("from", new WDateField());
		innerLayout.addField("to", new WDateField());
		//add the WFieldLayout to the WFieldSet
		dateRangeFS.add(innerLayout);
		//then just call addField using the WFieldSet as the input WComponent.
		fieldsFlat.addField(labelText, dateRangeFS);
		fieldsFlat.addField(new WButton("Save"));

		addNullLabelExample();
		addNestedFieldExamples();
		addAntiPatternExamples();
	}

	/**
	 * Example of when and how to use a null WLabel.
	 */
	private void addNullLabelExample() {
		add(new WHeading(HeadingLevel.H2, "How to use accessible null WLabels"));
		add(new ExplanatoryText("These examples shows how sometime a null WLabel is the right thing to do."
				+ "\n This example uses a WFieldSet as the labelled component and it has its own \"label\"."));

		// We want to add a WFieldSet to a WFieldLayout but without an extra label.
		WFieldLayout fieldsFlat = new WFieldLayout();
		fieldsFlat.setMargin(new Margin(null, null, Size.XL, null));
		add(fieldsFlat);
		WFieldSet fs = new WFieldSet("Do you like Bananas?");
		fieldsFlat.addField((WLabel) null, fs);
		// now add the WRadioButtons to the WFieldSet using an inner WFieldLayout
		WFieldLayout innerLayout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);

		// The content will be a group of WRadioButtons
		RadioButtonGroup group1 = new RadioButtonGroup();
		WRadioButton rb1 = group1.addRadioButton(1);
		WRadioButton rb2 = group1.addRadioButton(2);
		//make the labels for the radio buttons
		WLabel rb1Label = new WLabel("", rb1);
		WImage labelImage = new WImage("/image/success.png", "I still like bananas");
		labelImage.setHtmlClass("wc-valign-bottom");
		rb1Label.add(labelImage);
		WLabel rb2Label = new WLabel("", rb2);
		labelImage = new WImage("/image/error.png", "I still dislike bananas");
		labelImage.setHtmlClass("wc-valign-bottom");
		rb2Label.add(labelImage);
		innerLayout.addField(rb1Label, rb1);
		innerLayout.addField(rb2Label, rb2);
		// add the content to the WFieldLayout - the order really doesn't matter.
		fs.add(group1);
		fs.add(innerLayout);
	}

	/**
	 * Examples showing WLabel with a nested input control WComponent.
	 * This is VERY dangerous as only a very few WComponents are valid for this scenario. If you go down this route: stop!!
	 * These are really here for framework testing, not as examples as to  how to do things.
	 */
	private void addNestedFieldExamples() {
		add(new WHeading(HeadingLevel.H2, "Label nesting which is technically OK"));
		/* Just because it is OK to do this does not mean you should! So these "examples" have far fewer comments. */
		WPanel errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, Size.LARGE));
		errorLayoutPanel.setMargin(new Margin(null, null, Size.XL, null));
		add(errorLayoutPanel);
		errorLayoutPanel.add(new ExplanatoryText("This example shows WLabels with a single nested simple form control WTextField."
						+ " This is not a contravention of the HTML specification but you should not do it."));
		WLabel outerLabel = new WLabel("Label with nested WTextField and not 'for' anything");
		errorLayoutPanel.add(outerLabel);
		outerLabel.add(new WTextField());
		WTextField innerField = new WTextField();
		outerLabel = new WLabel("Label 'for' nested WTextField", innerField);
		errorLayoutPanel.add(outerLabel);
		outerLabel.add(innerField);
	}

	/**
	 * Add examples you should never follow. DO NOT use the following as examples of what to do: these are examples of what NOT to do.
	 */
	private void addAntiPatternExamples() {
		add(new WHeading(HeadingLevel.H2, "WLabel anti-patterns"));
		add(new ExplanatoryText("These are here for testing purposes and must not be used as examples to follow.\n"
						+ "Turn on debugging (bordertech.wcomponents.debug.enabled=true) to get much more information."));
		add(new WHeading(HeadingLevel.H3, "Poor but not erroneous uses of WLabel"));
		WPanel errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, Size.LARGE));
		add(errorLayoutPanel);
		//label not for anything should not be a WLabel
		errorLayoutPanel.add(new WLabel("I am not 'for' anything"));
		//WLabel for something which is not labellable
		errorLayoutPanel.add(new WLabel("I am for a component which should not be labelled", errorLayoutPanel));
		// If the WLabel is 'for' something that is not in the tree it becomes 'for' the WApplication. This is not necessarily a good thing!!!
		WCheckBox notHere = new WCheckBox();
		errorLayoutPanel.add(new WLabel("My component wasn't added", notHere));

		/*
		 * The examples which follow MUST NEVER BE USED! They cause ERRORS.
		 * They are here purely for framework testing.
		 */
		add(new WHeading(HeadingLevel.H3, "Very bad uses of WLabel"));
		errorLayoutPanel = new WPanel();
		errorLayoutPanel.setLayout(new FlowLayout(FlowLayout.VERTICAL, Size.LARGE));
		add(errorLayoutPanel);

		/*
		 * Nested WLabels: very bad
		 */
		errorLayoutPanel.add(new ExplanatoryText(
				"This example shows nested WLabels. This is a contravention of the HTML specification."));

		WPanel nestingErrorPanel = new WPanel();
		nestingErrorPanel.setLayout(new ColumnLayout(new int[]{50, 50}, Size.LARGE, Size.MEDIUM));
		errorLayoutPanel.add(nestingErrorPanel);
		WTextField outerField = new WTextField();
		WLabel outerLabel = new WLabel("I am an outer label", outerField);
		nestingErrorPanel.add(outerLabel);

		WTextField innerField = new WTextField();
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
		errorLayoutPanel.add(new ExplanatoryText("This example shows a WLabel with a nested simple form control WTextField but the WLabel is not "
				+ "'for' the WTextField. This is a contravention of the HTML specification."));

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

		add(new WHeading(HeadingLevel.H2, "Unlabelled controls"));
		add(new ExplanatoryText("These controls must be labelled but are not."));
		WFieldLayout fieldsFlat = new WFieldLayout();
		add(fieldsFlat);
		fieldsFlat.addField((WLabel) null, new WTextField());
		fieldsFlat.addField((WLabel) null, new WTextArea());
		fieldsFlat.addField((WLabel) null, new WDateField());
		fieldsFlat.addField((WLabel) null, new WCheckBox());
		fieldsFlat.addField((WLabel) null, new WCheckBoxSelect(
				new String[]{"Apple", "Cherry", "Orange", "Pineapple"}));
	}
}
