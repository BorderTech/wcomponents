package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WDialog;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPartialDateField;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.util.Util;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.Date;
import java.util.List;

/**
 * This example demonstrates the use of the {@link WDialog} component.
 *
 * @author Christina Harris
 * @since 1.0.0
 *
 *
 * @author Mark Reeves
 * @since 1.0.0 Added many examples and reordered the examples so that the preferred mechanism for opening a dialog is
 * the primary example.
 */
public class WDialogExample extends WPanel implements MessageContainer {

	/**
	 * The messages instance for this UI.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * This text is used for automated testing ONLY. It is used to test for a specific piece of content in a modal
	 * dialog to show that the dialog has opened. It is not necessary for the functioning of the example dialogs that
	 * this text be specified.
	 */
	private static final String MODAL_TEXT = "Modal dialog example";
	/**
	 * This text is used for automated testing ONLY. It is used to test for a specific piece of content in a modal
	 * dialog to show that the dialog has opened. It is not necessary for the functioning of the example dialogs that
	 * this text be specified.
	 */
	private static final String NON_MODAL_TEXT = "Non-modal dialog example";

	/**
	 * WButton used to launch an immediate modal dialog.
	 */
	private final WDialog modalDialog;
	/**
	 * WButton used to launch an immediate non-modal dialog.
	 */
	private final WDialog nonModalDialog;

	/**
	 * Creates a WDialogExample.
	 */
	@SuppressWarnings("serial")
	public WDialogExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));
		add(messages);

		final WPanel outputPanel = new WPanel(WPanel.Type.BOX);
		final WText txtNow = new WText("Now : " + (new Date()).toString());
		outputPanel.add(txtNow);

		/* Immediate opening: This is the preferred way to open a WDialog as it
		 * is the most efficient. To make a WDialog which opens without making
		 * a round trip to the server the WButton which opens the dialog is
		 * included in the constructor. The button action will be fired when the
		 * content of the dialog is fetched (ie: when the dialog is opened on
		 * the client).
		 *
		 * Immediate 1: Show that the opener button action fires.
		 *
		 * This example serves only to test that we pass the opener button as
		 * part of the call for content in an immediately opening dialog as
		 * this was not done in early versions of WComponents.
		 */
		final WButton dialogOpeningButton = new WButton(
				"Immediate (non-modal) dialog with button action");
		final ViewPersonList defList = new ViewPersonList();
		defList.addTerm("Dialog opened by", new WText(dialogOpeningButton.getText()));
		nonModalDialog = new WDialog(defList, dialogOpeningButton);
		nonModalDialog.setTitle("View list with time");
		nonModalDialog.setWidth(600);

		dialogOpeningButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				if (defList != null) {
					//remove the old time
					defList.removeTerm("List generated at");
					//and add the current time
					defList.addTerm("List generated at", new WText((new Date()).toString()));
				}
			}
		});

		/*
		 * Immediate 2: Immediate dialog: modal with width and height
		 *
		 * Again this dialog opens without a round trip. The content then contains
		 * content which can trigger an ajax action within the dialog content so
		 * we can update the dialog content without reloading any of th eunderlying form.
		 */
		// Content for the modal dialog
		final SelectPersonPanel selectPanel2 = new SelectPersonPanel();
		// Modal Dialog
		WButton modalSearchImmediate = new WButton("Show modal search Dialog (immediate)");

		modalDialog = new WDialog(selectPanel2, modalSearchImmediate);
		modalSearchImmediate.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				selectPanel2.reset();
				// modalDialog.display();
			}
		});
		modalDialog.setTitle("Search");
		modalDialog.setMode(WDialog.MODAL);

		selectPanel2.setSelectAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				WMessages.getInstance(WDialogExample.this).info("Selected: " + selectPanel2.
						getSelected());
				//reset the dialog after use.
				modalDialog.reset();
			}
		});

		/*
		 * Opening a dialog on page load.
		 *
		 * A dialog can be opened on page load. You should avoid causing a full
		 * page submit just to open a dialog as it is a very inefficient mechanism
		 * to merely open a dialog.
		 *
		 * Open after round trip 1: modal dialog with width and height
		 */
		// Content for the modal dialog
		final SelectPersonPanel selectPanel = new SelectPersonPanel();
		//the select person action
		selectPanel.setSelectAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				WMessages.getInstance(WDialogExample.this).info("Selected: " + selectPanel.
						getSelected());
			}
		});
		// Modal Dialog which opens after round trip to server
		final WDialog modalDialogRT = new WDialog(selectPanel);
		modalDialogRT.setMode(WDialog.MODAL);
		modalDialogRT.setResizable(true);
		WButton dialogButton1 = new WButton("Show modal search Dialog (round trip)");
		//When a WButton will cause a dialog to open when the page is reloaded it should be
		//marked as a popup trigger. This is an accessibility enhancement.
		dialogButton1.setPopupTrigger(true);
		dialogButton1.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				modalDialogRT.reset(); // clear out the last search
				modalDialogRT.display();
			}
		});

		/*
		 * Open in page load 2: non-modal dialog
		 */
		// Non-modal Dialog open after a round trip to the server
		final WDialog nonModalDialogRT = new WDialog(new ViewPersonList());
		//non-modal is the default but can be set explicitly
		nonModalDialogRT.setMode(WDialog.MODELESS);

		WButton dialogButton2 = new WButton("Show non-modal view dialog");
		dialogButton2.setPopupTrigger(true);
		dialogButton2.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				nonModalDialogRT.display();
			}
		});

		/*
		 * Simple properties of WDialog
		 * These examples each show a single property of WDialog using a setter.
		 * Each of these opens immediately (that is, without having to reload
		 * the underlying page).
		 */
 /*
		 * DIALOG with a TITLE
		 * If not set explicitly, the title of a dialog is determined by the UI theme.
		 * ALL dialogs must have a title, you probably DO NOT WANT the theme default!
		 */
		final WDialog dialogWithTitle = new WDialog(new ViewPersonList(), new WButton("Show dialog with specified title"));
		dialogWithTitle.setTitle("List of people");
		/*
		 * NOT RESIZEABLE
		 * A WDialog is resizeable by the user unless resizing is explicitly
		 * disabled: you usually don't want to do this as it may cause usability
		 * problems.
		 */
		final WDialog fixedSizeDialog = new WDialog(new ViewPersonList(), new WButton("Show dialog which is not resizeable"));
		fixedSizeDialog.setResizable(false);
		/*
		 * NOT RESIZEABLE with fixed dimensions
		 * A WDialog is resizeable by the user unless resizing is explicitly
		 * disabled: you usually don't want to do this as it may cause usability
		 * problems.
		 */
		final WDialog fixedSizeDialog2 = new WDialog(new ViewPersonList(), new WButton("Show dialog with size but not resizeable"));
		fixedSizeDialog2.setResizable(false);
		fixedSizeDialog2.setWidth(300);
		fixedSizeDialog2.setHeight(150);

		/*
		 * SET THE WIDTH of a dialog
		 * If not set explicitly, the initial width of a dialog is determined by the UI theme.
		 */
		final WDialog dialogWithWidth = new WDialog(new ViewPersonList(), new WButton("Show dialog with specified width (300px)"));
		dialogWithWidth.setWidth(300);
		/*
		 * SET THE HEIGHT of a dialog
		 * If not set explicitly, the initial width of a dialog is determined by the UI theme.
		 */
		final WDialog dialogWithHeight = new WDialog(new ViewPersonList(), new WButton("Show dialog with specified height (150px)"));
		dialogWithHeight.setHeight(150);
		/*
		 * SET THE HEIGHT of a dialog
		 * If not set explicitly, the initial width of a dialog is determined by the UI theme.
		 */
		final WDialog dialogWithHeight2 = new WDialog(new WText("1500px x 1000px"), new WButton("Show enormous dialog"));
		dialogWithHeight2.setHeight(1000);
		dialogWithHeight2.setWidth(1500);
		/*
		 * Make Modal
		 * If not set explicitly, the initial width of a dialog is determined by the UI theme.
		 */
		final WDialog dialogWithMode = new WDialog(new ViewPersonList(), new WButton("Show modal dialog"));
		dialogWithMode.setMode(WDialog.MODAL);

		// Layout
		add(new WHeading(HeadingLevel.H2,
				"Dialogs which display use of various properties one at a time"));
		add(dialogWithTitle);
		add(fixedSizeDialog);
		add(dialogWithWidth);
		add(dialogWithHeight);
		add(dialogWithHeight2);
		add(dialogWithMode);
		add(fixedSizeDialog2);

		add(new WHeading(WHeading.MAJOR, "Dialogs which open without page reload"));
		//remember the button of an immediate is part of the dialog: it will be place into the UI wherever the dialog is placed
		add(nonModalDialog);
		add(modalDialog);
		// for buttons which will result in a dialog on reload the button is added to the UI explicitly

		add(new WHeading(WHeading.MAJOR, "Dialogs which open after page reload"));
		add(dialogButton1);
		add(dialogButton2);

		//dialogs which do not have a WButton immediate opener control can be placed anywhere in the UI.
		//NOTE, however, that they will result in a layout cell if added to a WPanel with a layout.
		//which may have flow-on effects for the appearance of the controls in that panel.
		//if a WDialog does not have an opener button it is in the XML stream only if it is open.
		add(modalDialogRT);
		add(nonModalDialogRT);

		WDialog fileUploadDialog = new WDialog(new WMultiFileWidgetAjaxExample(), new WButton("Upload"));
		fileUploadDialog.setMode(WDialog.MODAL);
		fileUploadDialog.setWidth(600);
		add(fileUploadDialog);

		final WPartialDateField pdfDate = new WPartialDateField();
		WButton dateButton = new WButton("Set Date");
		dateButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				txtNow.setText("Date Selected : " + pdfDate.getValueAsString());
			}
		});

		WFieldLayout dateDlgFldLayout = new WFieldLayout();
		dateDlgFldLayout.addField("Set a date", pdfDate);
		WContainer dateBtnContainer = new WContainer();
		dateBtnContainer.add(dateButton);
		dateBtnContainer.add(new WAjaxControl(dateButton, outputPanel));
		dateDlgFldLayout.addField((WLabel) null, dateBtnContainer);
		WDialog dateDlg = new WDialog(dateDlgFldLayout, new WButton("Select a date in a dialog"));

		dateDlg.setWidth(450);
		dateDlg.setMode(WDialog.MODAL);

		add(dateDlg);
		add(outputPanel);


		add(new WHeading(HeadingLevel.H3, "Multi polling ajax inside a dialog"));
		add(new ExplanatoryText("You really don't want to do this."));
		WDialog pollingDialog = new WDialog(new MultiPollingExample());
		pollingDialog.setMode(WDialog.MODAL);
		WButton openPollingButton = new WButton("Open dialog with multi polling");
		pollingDialog.setTrigger(openPollingButton);
		add(openPollingButton);
		add(pollingDialog);


		add(new WHeading(HeadingLevel.H3, "WDialog with disabled launch button"));
		add(new ExplanatoryText("This is here to test a workaround for an IE 'feature'."));
		// WDialog with disabled activation button
		WButton disabledButton = new WButton("Open dialog (disabled)");
		disabledButton.setDisabled(true);
		WDialog dialogWithDisabledButton = new WDialog(new ViewPersonList(), disabledButton);
		add(dialogWithDisabledButton);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}

	/**
	 * An example display-only definition list which can be displayed in a modal dialog.
	 */
	private static final class ViewPersonList extends WDefinitionList {

		/**
		 * used to change the modal text which is part of the dialog content for automated testing.
		 */
		private static final String MODE_FLAG_TERM_TEXT = "Automated test text";

		/**
		 * Creates a ViewPersonList.
		 */
		private ViewPersonList() {
			super(WDefinitionList.Type.COLUMN);
			addTerm("First name", new WText("John"));
			addTerm("Last name", new WText("Bonham"));
		}

		/**
		 * Override preparePaintComponent in order to set some content which varies according to the modality of the
		 * containing dialog.
		 *
		 * @param request the request being responded to.
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			removeTerm(MODE_FLAG_TERM_TEXT);
			WDialog dialog = (WDialog) WebUtilities.getAncestorOfClass(WDialog.class, this);
			addTerm(MODE_FLAG_TERM_TEXT, new WText(
					((dialog == null || dialog.getMode() == WDialog.MODELESS) ? NON_MODAL_TEXT : MODAL_TEXT)));
		}
	}

	/**
	 * <p>
	 * An example panel which is used to search/select a person.</p>
	 *
	 * <p>
	 * This demonstrates "conversational" dialog content, where AJAX is used to update components within the dialog, and
	 * the dialog has validation to ensure the entered data is valid.</p>
	 *
	 * <p>
	 * This also demonstrates the use of a "chained" action, where another action is invoked from the executeOnValid
	 * part of a validating action.</p>
	 */
	private static final class SelectPersonPanel extends WPanel implements MessageContainer {

		private final SearchFieldSet searchFS = new SearchFieldSet();
		private final WMessages messages = new WMessages();
		private final WRadioButtonSelect rbSelect = new WRadioButtonSelect();
		private final WButton selectButton = new WButton("Select");
		private final WButton searchButton = new WButton("Search");
		private final WCancelButton cancelButton = new WCancelButton("Cancel");

		private final WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);

		/**
		 * The backing action to call when a person has been selected.
		 */
		private Action selectAction;

		/**
		 * Creates a SelectPersonPanel.
		 */
		@SuppressWarnings("serial")
		private SelectPersonPanel() {
			rbSelect.setMandatory(true, "Please select a name from the list");
			rbSelect.setFrameless(true);
			layout.addField("Make a selection", rbSelect);

			//we do not want the select button or radio buttons until a search is made
			layout.setVisible(false);
			selectButton.setVisible(false);

			add(messages);
			add(searchFS);
			add(layout);

			final WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
			buttonPanel.setLayout(new BorderLayout());
			buttonPanel.setMargin(new com.github.bordertech.wcomponents.Margin(12, 0, 0, 0));
			add(buttonPanel);
			buttonPanel.add(cancelButton, BorderLayout.WEST);
			buttonPanel.add(selectButton, BorderLayout.EAST);
			buttonPanel.add(searchButton, BorderLayout.EAST);
			add(new WText(MODAL_TEXT + ": this text is for automated testing only"));

			searchButton.setAjaxTarget(this);

			searchButton.setAction(new ValidatingAction(WMessages.getInstance(searchFS).
					getValidationErrors(), searchFS) {
				@Override
				public void executeOnValid(final ActionEvent event) {
					String[] data = new String[]{
						formatName(searchFS.getFirstName(), searchFS.getLastName()),
						formatName(searchFS.getFirstName() + "-Two", searchFS.getLastName()),
						formatName(searchFS.getFirstName() + "-Three", searchFS.getLastName())
					};

					layout.setVisible(true);
					selectButton.setVisible(true);
					rbSelect.setOptions(data);
					rbSelect.resetData();
					searchButton.setText("New Search");
				}
			});

			cancelButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					WDialog dialog = (WDialog) WebUtilities.getAncestorOfClass(WDialog.class,
							cancelButton);
					dialog.reset();
				}
			});

			selectButton.setAction(new ValidatingAction(WMessages.getInstance(selectButton).
					getValidationErrors(), rbSelect) {
				/**
				 * If validation passes, invoke the backing action.
				 *
				 * @param event the event which triggered validation.
				 */
				@Override
				public void executeOnValid(final ActionEvent event) {
					selectAction.execute(event);
				}

				/**
				 * Since the dialog is closed on a round-trip, we need to override executeOnError to re-show the dialog.
				 *
				 * @param event the event which triggered validation.
				 * @param diags the list of validation errors.
				 */
				@Override
				public void executeOnError(final ActionEvent event, final List<Diagnostic> diags) {
					super.executeOnError(event, diags);

					WDialog dialog = (WDialog) WebUtilities.getAncestorOfClass(WDialog.class,
							selectButton);
					dialog.display();
				}
			});
		}

		/**
		 * Sets the action to execute when a person is selected.
		 *
		 * @param selectAction the select action.
		 */
		public void setSelectAction(final Action selectAction) {
			this.selectAction = selectAction;
		}

		/**
		 * Retrieves the selected person.
		 *
		 * @return the selected person, or null if no person is selected.
		 */
		public String getSelected() {
			return (String) rbSelect.getSelected();
		}

		/**
		 * Formats a person's name for display to the user.
		 *
		 * @param firstName the first name
		 * @param lastName the last name
		 * @return the formatted name
		 */
		private String formatName(final String firstName, final String lastName) {
			if (!Util.empty(firstName)) {
				if (!Util.empty(lastName)) {
					return lastName.trim() + ", " + firstName.trim();
				}

				return firstName.trim();
			} else {
				return lastName.trim();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public WMessages getMessages() {
			return messages;
		}

		/**
		 * A simple panel where the user can enter a name to search on.
		 */
		private static final class SearchFieldSet extends WFieldSet {

			/**
			 * The text field used to enter the first name.
			 */
			private final WTextField firstName = new WTextField();

			/**
			 * The text field used to enter the last name.
			 */
			private final WTextField lastName = new WTextField();

			/**
			 * Creates a SearchFieldSet.
			 */
			private SearchFieldSet() {
				super("Search for person");
				setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
				firstName.setMandatory(true);
				WFieldLayout fieldLayout = new WFieldLayout();
				add(fieldLayout);
				fieldLayout.addField("First name", firstName);
				fieldLayout.addField("Last name", lastName);
			}

			/**
			 * Retrieves the first name entered by the user.
			 *
			 * @return the first name.
			 */
			public String getFirstName() {
				return firstName.getText();
			}

			/**
			 * Retrieves the last name entered by the user.
			 *
			 * @return the last name.
			 */
			public String getLastName() {
				return lastName.getText();
			}

		}
	}

	/**
	 * Get the modal dialog marker text. This is used for automated testing only and is not part of the example.
	 *
	 * @return the modal marker text
	 */
	public final String getModalText() {
		return MODAL_TEXT;
	}

	/**
	 * Get the non-modal dialog marker text. This is used for automated testing only and is not part of the example.
	 *
	 * @return the non-modal marker text
	 */
	public final String getNonModalText() {
		return NON_MODAL_TEXT;
	}

	/**
	 * Gets a button which opens a modal dialog. Used for automated testing only: not part of the example.
	 *
	 * @return a modal dialog launch button;
	 */
	public final WButton getModalButton() {
		return (WButton) modalDialog.getTrigger();
	}

	/**
	 * @return a non modal button
	 */
	public final WButton getNonModalButton() {
		return (WButton) nonModalDialog.getTrigger();
	}
}
