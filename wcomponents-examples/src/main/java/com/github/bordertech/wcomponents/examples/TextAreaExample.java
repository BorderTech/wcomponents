package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * WTextArea example demonstrates various states of the {@link WTextArea}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class TextAreaExample extends WPanel {
	/**
	 * Creates a TextAreaExample.
	 */
	public TextAreaExample() {
		final WMessages messages = new WMessages();
		add(messages);
		/*
		 * Use WFieldLayout to add input/label pairs to a UI.
		 */
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		/*
		 * A simple WTextArea.
		 */
		final WTextArea defaultTextArea = new WTextArea();
		/*
		 * A WTextArea used to show how to set dimensions and input limits.
		 */
		WTextArea constrainedTextArea = new WTextArea();
		constrainedTextArea.setColumns(40);
		constrainedTextArea.setRows(4);
		constrainedTextArea.setMaxLength(200);
		constrainedTextArea.setPlaceholder("type here");
		/*
		 * A WTextArea used to show how to create a static read-only multi-line text field.
		 */
		final WTextArea readOnlyTextArea = new WTextArea();
		readOnlyTextArea.setReadOnly(true);
		readOnlyTextArea.setText("This is read only.");
		/*
		 * A WTextArea used to show how to a disabled multi-line text field.
		 */
		final WTextArea disabledTextArea = new WTextArea();
		disabledTextArea.setDisabled(true);
		disabledTextArea.setText("This is disabled.");
		/*
		 * A WtextArea used to show how to set read-only content based on user input into an editable WTextArea.
		 */
		final WTextArea readOnlyReflector = new WTextArea();
		readOnlyReflector.setReadOnly(true);
		/*
		 * A WTextArea used as a theme test showing that it is possible to have a WTextArea which has more content than its maxlength setting.
		 * The line breaks in the text will push the chars over the maxlength allowed in HTML. You should NEVER do this on purpose!
		 */
		WTextArea initiallyInvalid = new WTextArea();
		initiallyInvalid.setMaxLength(10);
		initiallyInvalid.setText("abc\ndef\ngh");
		/*
		 * A rich text field.
		 */
		final WTextArea rtf = new WTextArea();
		rtf.setRichTextArea(true);
		/*
		 * A read-only rich text field.
		 */
		final WTextArea rtfReadOnly = new WTextArea();
		rtfReadOnly.setReadOnly(true);
		rtfReadOnly.setRichTextArea(true);
		/*
		 * A button used to apply user input to the read-only WTextArea.
		 */
		WButton showReadOnlyContentButton = new WButton("Copy as read only");
		showReadOnlyContentButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				readOnlyReflector.setData(defaultTextArea.getData());
			}
		});
		/*
		 * A button used to toggle the disabled state of the disabled WTextArea.
		 */
		WButton toggleDisableButton = new WButton("Toggle disable");
		toggleDisableButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				disabledTextArea.setDisabled(!disabledTextArea.isDisabled());
			}
		});

		/**
		 * Action used to copy user-entered rich text to the read-only rich text field.
		 */
		Action setRTFContent = new Action() {
				@Override
				public void execute(final ActionEvent event) {
					rtfReadOnly.setData(rtf.getValue());
				}
			};
		/**
		 * A button used to copy the user-entered rich text to the read-only rich text field via a full round trip.
		 */
		WButton rtfButton = new WButton("Round-trip copy rich text (bad)");
		rtfButton.setAction(setRTFContent);
		/**
		 * A button used to copy the user-entered rich text to the read-only rich text field via AJAX.
		 */
		WButton rtAjaxButton = new WButton("AJAX copy rich text (good)");
		rtAjaxButton.setAction(setRTFContent);

		/*
		 * A container for the buttons used to copy the user-entered rich text to the read-only rich text field.
		 */
		WContainer rtfButtonContainer = new WContainer();
		rtfButtonContainer.add(rtfButton);
		rtfButtonContainer.add(rtAjaxButton);

		// Layout of this example.
		// NOTE: The order of the first four WTextAreas is important as it forms part of the Selenium test of this example.
		add(layout);
		layout.addField("Default", defaultTextArea);
		layout.addField(showReadOnlyContentButton);
		layout.addField("Read-only reflection of default WTextArea", readOnlyReflector);
		layout.addField("Size and Length Limited", constrainedTextArea).getLabel().setHint("Max 200 characters");
		layout.addField("Read-only", readOnlyTextArea);
		layout.addField("Disabled", disabledTextArea);
		layout.addField(toggleDisableButton);
		layout.addField("Test: more content than maxlength", initiallyInvalid);
		layout.addField("Rich Text", rtf);
		layout.addField((String) null, rtfButtonContainer);
		layout.addField("Read-only reflection of the rich text area.", rtfReadOnly);
		WTextArea mandatoryTA = new WTextArea();
		mandatoryTA.setMandatory(true);
		layout.addField("Mandatory", mandatoryTA);mandatoryTA = new WTextArea();
		mandatoryTA.setMandatory(true);
		mandatoryTA.setText("Remove me");
		layout.addField("Mandatory with default content", mandatoryTA);

		WButton validatingButton = new WButton("Submit");
		validatingButton.setAction(new ValidatingAction(messages.getValidationErrors(), layout) {
			@Override
			public void executeOnValid(ActionEvent event) {
				messages.reset();
				messages.addMessage(new Message(Message.SUCCESS_MESSAGE, "Hurray!"));
			}
		});
		layout.addField(new WButton("Submit"));


		add(new WAjaxControl(showReadOnlyContentButton, readOnlyReflector));
		add(new WAjaxControl(rtAjaxButton, rtfReadOnly));
	}
}
