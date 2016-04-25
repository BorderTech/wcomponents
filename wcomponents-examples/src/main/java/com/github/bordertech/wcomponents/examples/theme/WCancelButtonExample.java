package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Message;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.util.Arrays;
import java.util.List;

/**
 * An example showing use of a {@link WCancelButton} with unsaved changes being flagged both by the client and server.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WCancelButtonExample extends WPanel implements MessageContainer {

	/**
	 * Used to display messages to the user.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * The "pages" in the wizard-style interface.
	 */
	private final List<PanelWithTextField> pages = Arrays.asList(new PanelWithTextField[]{
		new PanelWithTextField("First item"),
		new PanelWithTextField("Second item"),
		new PanelWithTextField("Final item")
	});

	/**
	 * The card manager used to hold the pages.
	 */
	private final WCardManager cardManager = new WCardManager();

	/**
	 * The button used to navigate to the previous page.
	 */
	private final WButton prevButton = new WButton("Previous");

	/**
	 * The button used to navigate to the next page.
	 */
	private final WButton nextButton = new WButton("Next");

	/**
	 * The button used to navigate to "submit" the form.
	 */
	private final WButton finishButton = new WButton("Submit");

	/**
	 * The cancel button.
	 */
	private final WCancelButton cancelButton = new WCancelButton("Cancel");

	/**
	 * Creates a WCancelButtonExample.
	 */
	public WCancelButtonExample() {
		// Build UI
		setLayout(new FlowLayout(Alignment.VERTICAL));
		add(messages);
		add(cardManager);

		for (PanelWithTextField page : pages) {
			cardManager.add(page);
		}

		WPanel buttonPanel = new WPanel(Type.FEATURE);
		buttonPanel.setLayout(new FlowLayout(Alignment.LEFT, 3, 0));
		buttonPanel.setMargin(new com.github.bordertech.wcomponents.Margin(12, 0, 0, 0));
		buttonPanel.add(prevButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(finishButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel);

		add(new WHeading(WHeading.MAJOR, "Testing example for Internet Explorer"));
		add(new ExplanatoryText(
				"Internet Explorer (up to and including IE11) honours clicks on disabled buttons and causes the event handlers to be called."
				+ " This example is only used to check that we are correctly handling these click on disabled buttons. To undertake the test enter some text"
				+ " into any field on this screen then click the disabled 'IE Test Cancel' button below. You should not see the unsaved changes warning."));

		WFieldLayout ieCancelLayout = new WFieldLayout();
		add(ieCancelLayout);
		ieCancelLayout.setLabelWidth(25);
		WCancelButton ieCancelButton = new WCancelButton("IE Test Cancel");
		WTextField ieTestField = new WTextField();
		ieTestField.setDefaultSubmitButton(ieCancelButton);
		ieCancelLayout.addField("Enter any text", ieTestField);
		ieCancelButton.setDisabled(true);
		ieCancelLayout.addField((WLabel) null, ieCancelButton);

		// Set button actions
		prevButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				changePage(-1);
			}
		});

		nextButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				changePage(1);
			}
		});

		cancelButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				reset();
			}
		});

		finishButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				String text = "Finished with"
						+ ", First item = " + pages.get(0).getTextField().getValueAsString()
						+ ", Second item = " + pages.get(1).getTextField().getValueAsString()
						+ ", Final item = " + pages.get(2).getTextField().getValueAsString();

				reset();

				messages.addMessage(new Message(Message.SUCCESS_MESSAGE, text));
			}
		});

		// Set initial button states
		prevButton.setDisabled(true);
		finishButton.setDisabled(true);
	}

	/**
	 * Handles a pagination request.
	 *
	 * @param direction the direction and amount of pages to move through.
	 */
	private void changePage(final int direction) {
		int currentPage = pages.indexOf(cardManager.getVisible());
		currentPage = Math.min(2, Math.max(0, currentPage + direction));

		cardManager.makeVisible(pages.get(currentPage));

		prevButton.setDisabled(currentPage == 0);
		nextButton.setDisabled(currentPage == 2);
		finishButton.setDisabled(currentPage != 2);

		cancelButton.setUnsavedChanges(currentPage > 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}

	/**
	 * A simple panel which contains a text field for data entry.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class PanelWithTextField extends WPanel {

		/**
		 * The text field for this panel.
		 */
		private final WTextField textField = new WTextField();

		/**
		 * Creates a PanelWithTextField.
		 *
		 * @param fieldLabel the text for the textField's label.
		 */
		private PanelWithTextField(final String fieldLabel) {
			super();
			WFieldLayout fieldLayout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
			fieldLayout.setLabelWidth(25);
			add(fieldLayout);
			fieldLayout.addField(fieldLabel, textField);
		}

		/**
		 * @return the panel's text field.
		 */
		public WTextField getTextField() {
			return textField;
		}
	}

}
