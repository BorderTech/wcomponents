package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Container;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WNamingContext;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.AppPreferenceParameterExample;
import com.github.bordertech.wcomponents.examples.AutoReFocusExample;
import com.github.bordertech.wcomponents.examples.AutoReFocusRepeaterExample;
import com.github.bordertech.wcomponents.examples.ErrorGenerator;
import com.github.bordertech.wcomponents.examples.ForwardExample;
import com.github.bordertech.wcomponents.examples.HtmlInjector;
import com.github.bordertech.wcomponents.examples.InfoDump;
import com.github.bordertech.wcomponents.examples.KitchenSink;
import com.github.bordertech.wcomponents.examples.SimpleFileUpload;
import com.github.bordertech.wcomponents.examples.TextDuplicator;
import com.github.bordertech.wcomponents.examples.TextDuplicatorHandleRequestImpl;
import com.github.bordertech.wcomponents.examples.TextDuplicatorVelocity2;
import com.github.bordertech.wcomponents.examples.TextDuplicatorVelocityImpl;
import com.github.bordertech.wcomponents.examples.WAbbrTextExample;
import com.github.bordertech.wcomponents.examples.WApplicationExample;
import com.github.bordertech.wcomponents.examples.WButtonExample;
import com.github.bordertech.wcomponents.examples.WCheckBoxTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WContentExample;
import com.github.bordertech.wcomponents.examples.WDialogExample;
import com.github.bordertech.wcomponents.examples.WDropdownSpaceHandlingExample;
import com.github.bordertech.wcomponents.examples.WDropdownSpecialCharHandlingExample;
import com.github.bordertech.wcomponents.examples.WDropdownSubmitOnChangeExample;
import com.github.bordertech.wcomponents.examples.WDropdownTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WImageExample;
import com.github.bordertech.wcomponents.examples.WRadioButtonTriggerActionExample;
import com.github.bordertech.wcomponents.examples.WTextExample;
import com.github.bordertech.wcomponents.examples.WWindowExample;
import com.github.bordertech.wcomponents.examples.datatable.DataTableBeanExample;
import com.github.bordertech.wcomponents.examples.datatable.DataTableOptionsExample;
import com.github.bordertech.wcomponents.examples.datatable.SelectableDataTableExample;
import com.github.bordertech.wcomponents.examples.datatable.SimpleEditableDataTableExample;
import com.github.bordertech.wcomponents.examples.datatable.SimpleRowEditingTableExample;
import com.github.bordertech.wcomponents.examples.datatable.TableCellWithActionExample;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableExample;
import com.github.bordertech.wcomponents.examples.datatable.TreeTableHierarchyExample;
import com.github.bordertech.wcomponents.examples.datatable.WDataTableExample;
import com.github.bordertech.wcomponents.examples.repeater.RepeaterExample;
import com.github.bordertech.wcomponents.examples.repeater.RepeaterExampleWithEditableRows;
import com.github.bordertech.wcomponents.examples.repeater.link.RepeaterLinkExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlAllExamples;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlCrtWDropdownExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlGroupExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlMandatoryExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleCheckBoxSelectExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleDisableExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWDropdownExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWFieldExample;
import com.github.bordertech.wcomponents.examples.subordinate.SubordinateControlSimpleWMultiSelectExample;
import com.github.bordertech.wcomponents.examples.theme.AccessKeyExample;
import com.github.bordertech.wcomponents.examples.theme.NestedTabSetExample;
import com.github.bordertech.wcomponents.examples.theme.WButtonDefaultSubmitExample;
import com.github.bordertech.wcomponents.examples.theme.WCancelButtonExample;
import com.github.bordertech.wcomponents.examples.theme.WCheckBoxSelectExample;
import com.github.bordertech.wcomponents.examples.theme.WCollapsibleExample;
import com.github.bordertech.wcomponents.examples.theme.WCollapsibleGroupExample;
import com.github.bordertech.wcomponents.examples.theme.WConfirmationButtonExample;
import com.github.bordertech.wcomponents.examples.theme.WDateFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldLayoutExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldNestedExample;
import com.github.bordertech.wcomponents.examples.theme.WFieldSetExample;
import com.github.bordertech.wcomponents.examples.theme.WHeadingExample;
import com.github.bordertech.wcomponents.examples.theme.WHiddenCommentExample;
import com.github.bordertech.wcomponents.examples.theme.WListExample;
import com.github.bordertech.wcomponents.examples.theme.WMenuExample;
import com.github.bordertech.wcomponents.examples.theme.WMenuSelectModeExample;
import com.github.bordertech.wcomponents.examples.theme.WMenuWithAccessKeysExample;
import com.github.bordertech.wcomponents.examples.theme.WMessageBoxExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiDropdownExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiFileWidgetExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiSelectPairExample;
import com.github.bordertech.wcomponents.examples.theme.WMultiTextFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WPanelTypeExample;
import com.github.bordertech.wcomponents.examples.theme.WPartialDateFieldExample;
import com.github.bordertech.wcomponents.examples.theme.WProgressBarExample;
import com.github.bordertech.wcomponents.examples.theme.WRadioButtonSelectExample;
import com.github.bordertech.wcomponents.examples.theme.WRowExample;
import com.github.bordertech.wcomponents.examples.theme.WSelectToggleExample;
import com.github.bordertech.wcomponents.examples.theme.WTabAndCollapsibleExample;
import com.github.bordertech.wcomponents.examples.theme.WTabSetExample;
import com.github.bordertech.wcomponents.examples.theme.WTabSetTriggerActionExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxExamples;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxPollingWButtonExample;
import com.github.bordertech.wcomponents.examples.theme.ajax.AjaxWDropdownExample;
import com.github.bordertech.wcomponents.examples.validation.ValidationExamples;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicDiagnosticComponentExample;
import com.github.bordertech.wcomponents.examples.validation.basic.BasicFieldsValidationExample2;
import com.github.bordertech.wcomponents.examples.validation.fields.FieldValidation;
import com.github.bordertech.wcomponents.monitor.UicStats;
import com.github.bordertech.wcomponents.monitor.UicStatsAsHtml;
import com.github.bordertech.wcomponents.examples.othersys.LinkExamples;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.util.ObjectGraphDump;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import com.github.bordertech.wcomponents.validator.AbstractFieldValidator;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A component that enables you to pick an example to display.
 *
 * @author Martin Shevchenko
 */
public class SimplePicker extends WContainer implements MessageContainer {

	private static final String RECENT_FILE_NAME = "recent.dat";

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(SimplePicker.class);

	private final SafetyContainer container;

	private final PickerDialog pickerDialog;

	private final WButton profileBtn;

	private final WMessages messages = new WMessages();

	private final WCardManager cardManager = new WCardManager();

	private final WContainer mainDisplay = new WContainer();

	/**
	 * Creates an SimplePicker.
	 */
	public SimplePicker() {
		add(messages);

		add(cardManager);

		cardManager.add(mainDisplay);

		WButton chooseBtn = new WButton("Choose");
		WButton resetContextBtn = new WButton("Reset Context");
		profileBtn = new WButton("Profile");

		// TODO: This is bad - use a layout instead
		WText lineBreak = new WText("<br />");
		lineBreak.setEncodeText(false);

		// A place holder for the selected example.
		container = new SafetyContainer();

		mainDisplay.add(chooseBtn);
		mainDisplay.add(resetContextBtn);
		mainDisplay.add(profileBtn);
		mainDisplay.add(lineBreak);

		WNamingContext context = new WNamingContext("eg");
		context.add(container);
		mainDisplay.add(context);

		chooseBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				List recent = loadRecentList();
				pickerDialog.setRecentList(recent);
				cardManager.makeVisible(pickerDialog);
			}
		});

		resetContextBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				container.resetContent();
			}
		});

		pickerDialog = new PickerDialog();

		// Register the dialog so that if we are serializing the UIContext, then
		// the picker dialog doesn't need to be serialized into each uicontext.
		// UIRegistry.getInstance().register(PickerDialog.class.getName(),
		// pickerDialog);
		UIRegistry.getInstance().register(pickerDialog.getClass().getName(),
				pickerDialog);
	}

	/**
	 * When the example picker starts up, we want to display the last selection the user made in their previous session.
	 * We can do this because the list of recent selections is stored in a file on the file system.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!this.isInitialised()) {
			List recent = loadRecentList();

			if (recent != null && !recent.isEmpty()) {
				String selection = (String) recent.get(0);
				displaySelection(selection);
			}

			this.setInitialised(true);
		}

		updateTitle();
	}

	/**
	 * Updates the title, based on the selected example.
	 */
	private void updateTitle() {
		String title;

		if (this.getCurrentComponent() == null) {
			title = "Example Picker";
		} else {
			title = this.getCurrentComponent().getClass().getName();
		}

		WApplication app = WebUtilities.getAncestorOfClass(WApplication.class, this);

		if (app != null) {
			app.setTitle(title);
		}
	}

	/**
	 * Override afterPaint in order to paint the UIContext serialization statistics if the profile button has been
	 * pressed.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);

		if (profileBtn.isPressed()) {
			// UIC serialization stats
			UicStats stats = new UicStats(UIContextHolder.getCurrent());

			WComponent currentComp = this.getCurrentComponent();

			if (currentComp != null) {
				stats.analyseWC(currentComp);
			}

			if (renderContext instanceof WebXmlRenderContext) {
				PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();

				writer.println("<hr />");
				writer.println("<h2>Serialization Profile of UIC</h2>");
				UicStatsAsHtml.write(writer, stats);

				if (currentComp != null) {
					writer.println("<hr />");
					writer.println("<h2>ObjectProfiler - " + currentComp + "</h2>");
					writer.println("<pre>");

					try {
						writer.println(ObjectGraphDump.dump(currentComp).toFlatSummary());
					} catch (Exception e) {
						LOG.error("Failed to dump component", e);
					}

					writer.println("</pre>");
				}
			}
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
	 * Retrieves the list of recently selected examples from a file on the file system.
	 *
	 * @return the list of recently used examples.
	 */
	private List loadRecentList() {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					RECENT_FILE_NAME));

			XMLDecoder d = new XMLDecoder(in);
			Object result = d.readObject();
			d.close();

			return (List) result;
		} catch (FileNotFoundException ex) {
			// This is ok, it's probably the first time the picker has been used.
			return new ArrayList();
		}
	}

	/**
	 * Retrieves the component that is currently being displayed by the example picker.
	 *
	 * @return the current example.
	 */
	public Container getCurrentComponent() {
		Container currentComponent = null;

		if (((Container) (container.getChildAt(0))).getChildCount() > 0) {
			currentComponent = (Container) container.getChildAt(0);
		}

		return currentComponent;
	}

	/**
	 * Get the example picker to display the given selection.
	 *
	 * @param selection the class name of the wcomponent to be loaded and displayed.
	 */
	public void displaySelection(final String selection) {
		WComponent selectedComponent = UIRegistry.getInstance().getUI(selection);

		if (selectedComponent == null) {
			// Can't load selected component.
			WMessages.getInstance(this).error(
					"Unable to load example: " + selection + ", see log for details.");
			return;
		}

		displaySelection(selectedComponent);
	}

	/**
	 * Get the example picker to display the given selected component.
	 *
	 * @param selectedComponent the wcomponent to be displayed.
	 */
	public void displaySelection(final WComponent selectedComponent) {
		WComponent currentComponent = getCurrentComponent();

		if (selectedComponent == currentComponent) {
			// We are already displaying this component, so nothing to do.
			return;
		}

		// We have a new selection so display it.
		container.removeAll();
		container.add(selectedComponent);
	}

	/**
	 * Store the list of recent selections to a file on the file system.
	 *
	 * @param recent the recent selections
	 */
	private void storeRecentList(final List recent) {
		try {
			if (recent == null) {
				return;
			}

			// Only keep the last 8 entries.
			while (recent.size() > 8) {
				recent.remove(recent.size() - 1);
			}

			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					RECENT_FILE_NAME));
			XMLEncoder e = new XMLEncoder(out);
			e.writeObject(recent);
			e.close();
		} catch (IOException ex) {
			LOG.error("Unable to save recent list", ex);
		}
	}

	/**
	 * This wcomponent is a dialog that enables the user to select a new wcomponent to display.
	 */
	private final class PickerDialog extends WPanel {

		private final WValidationErrors errorBox = new WValidationErrors();

		private final WDropdown recentDropdown;

		private final WDropdown examplesDropdown;

		private final WTextField selectionTextField;

		private final WButton selectBtn;

		private final WButton cancelBtn;

		private final String[] examples = new String[]{
			// examples
			// examples
			// examples
			// examples
			// examples
			// examples
			// examples
			// examples
			null,
			AppPreferenceParameterExample.class.getName(),
			AutoReFocusExample.class.getName(),
			AutoReFocusRepeaterExample.class.getName(),
			ErrorGenerator.class.getName(),
			ForwardExample.class.getName(),
			HtmlInjector.class.getName(),
			InfoDump.class.getName(),
			KitchenSink.class.getName(),
			com.github.bordertech.wcomponents.examples.layout.LayoutExample.class.getName(),
			SimpleFileUpload.class.getName(),
			TextDuplicator.class.getName(),
			TextDuplicatorHandleRequestImpl.class.getName(),
			TextDuplicatorVelocityImpl.class.getName(),
			TextDuplicatorVelocity2.class.getName(),
			WAbbrTextExample.class.getName(),
			WApplicationExample.class.getName(),
			WButtonExample.class.getName(),
			WCheckBoxTriggerActionExample.class.getName(),
			WContentExample.class.getName(),
			WDialogExample.class.getName(),
			WDropdownSpaceHandlingExample.class.getName(),
			WDropdownSpecialCharHandlingExample.class.getName(),
			WDropdownSubmitOnChangeExample.class.getName(),
			WDropdownTriggerActionExample.class.getName(),
			WImageExample.class.getName(),
			WWindowExample.class.getName(),
			WTextExample.class.getName(),
			WRadioButtonTriggerActionExample.class.getName(),
			// repeater examples
			RepeaterExample.class.getName(),
			RepeaterExampleWithEditableRows.class.getName(),
			RepeaterLinkExample.class.getName(),
			// Data table examples
			WDataTableExample.class.getName(),
			SelectableDataTableExample.class.getName(),
			SimpleEditableDataTableExample.class.getName(),
			SimpleRowEditingTableExample.class.getName(),
			TableCellWithActionExample.class.getName(),
			DataTableBeanExample.class.getName(),
			TreeTableExample.class.getName(),
			TreeTableHierarchyExample.class.getName(),
			DataTableOptionsExample.class.getName(),
			// theme examples
			AccessKeyExample.class.getName(),
			AjaxWDropdownExample.class.getName(),
			AjaxExamples.class.getName(),
			AjaxPollingWButtonExample.class.getName(),
			NestedTabSetExample.class.getName(),
			SubordinateControlAllExamples.class.getName(),
			SubordinateControlCrtWDropdownExample.class.getName(),
			SubordinateControlExample.class.getName(),
			SubordinateControlGroupExample.class.getName(),
			SubordinateControlMandatoryExample.class.getName(),
			SubordinateControlSimpleExample.class.getName(),
			SubordinateControlSimpleDisableExample.class.getName(),
			SubordinateControlSimpleWDropdownExample.class.getName(),
			SubordinateControlSimpleWFieldExample.class.getName(),
			SubordinateControlSimpleWMultiSelectExample.class.getName(),
			SubordinateControlSimpleCheckBoxSelectExample.class.getName(),
			WButtonDefaultSubmitExample.class.getName(),
			WCancelButtonExample.class.getName(),
			WCheckBoxSelectExample.class.getName(),
			WCollapsibleExample.class.getName(),
			WCollapsibleGroupExample.class.getName(),
			WConfirmationButtonExample.class.getName(),
			WDateFieldExample.class.getName(),
			WFieldLayoutExample.class.getName(),
			WFieldSetExample.class.getName(),
			WFieldNestedExample.class.getName(),
			WHeadingExample.class.getName(),
			WHiddenCommentExample.class.getName(),
			WListExample.class.getName(),
			WMenuExample.class.getName(),
			WMenuWithAccessKeysExample.class.getName(),
			WMenuSelectModeExample.class.getName(),
			WMessageBoxExample.class.getName(),
			WMultiDropdownExample.class.getName(),
			WMultiFileWidgetExample.class.getName(),
			WMultiSelectPairExample.class.getName(),
			WMultiTextFieldExample.class.getName(),
			WPanelTypeExample.class.getName(),
			WPartialDateFieldExample.class.getName(),
			WProgressBarExample.class.getName(),
			WRadioButtonSelectExample.class.getName(),
			WRowExample.class.getName(),
			WSelectToggleExample.class.getName(),
			WTabAndCollapsibleExample.class.getName(),
			WTabSetExample.class.getName(),
			WTabSetTriggerActionExample.class.getName(),
			// validation examples
			ValidationExamples.class.getName(),
			BasicDiagnosticComponentExample.class.getName(),
			BasicFieldsValidationExample2.class.getName(),
			FieldValidation.class.getName(),
			// othersys
			LinkExamples.class.getName()};

		/**
		 * Creates a PickerDialog.
		 */
		private PickerDialog() {
			recentDropdown = new WDropdown();

			examplesDropdown = new WDropdown(examples);

			selectionTextField = new WTextField();
			selectionTextField.setColumns(60);
			selectionTextField.addValidator(new AbstractFieldValidator("Bad class.") {
				@Override
				protected boolean isValid() {
					WTextField textField = (WTextField) getInputField();
					String selection = textField.getText();

					if (selection != null && selection.length() > 0) {
						WComponent selectedComponent = UIRegistry.getInstance()
								.getUI(selection);
						return selectedComponent != null;
					}

					return true;
				}
			});

			selectBtn = new WButton("Select");
			selectBtn.setAction(new ValidatingAction(errorBox, PickerDialog.this) {
				@Override
				public void executeOnValid(final ActionEvent event) {
					doSelect();
				}
			});

			cancelBtn = new WButton("Cancel");
			cancelBtn.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					cardManager.makeVisible(mainDisplay);
				}
			});

			// Put it all together.
			WFieldLayout layout = new WFieldLayout();

			layout.addField("Recent", recentDropdown);
			layout.addField("Example", examplesDropdown);
			layout.addField("Other", selectionTextField);

			add(errorBox);
			add(layout);

			add(selectBtn);
			add(cancelBtn);

			setDefaultSubmitButton(selectBtn);
		}

		/**
		 * Sets the list of recently selected examples.
		 *
		 * @param recent the recently selected examples.
		 */
		public void setRecentList(final List recent) {
			recentDropdown.setOptions(recent);
		}

		/**
		 * Retrieves the example which has been selected by the user.
		 *
		 * @return the selected example.
		 */
		public String getSelected() {
			return (String) recentDropdown.getSelected();
		}

		/**
		 * Retrieves the list of examples which the user has recently selected.
		 *
		 * @return the user's recent selections.
		 */
		public List getRecentSelections() {
			return recentDropdown.getOptions();
		}

		/**
		 * Based on selection it is responsible for displaying a WComponent and making sure the recent list is updated.
		 * If selectionText is supplied then it is selected and if not then example list is checked. If these both are
		 * not supplied then it looks for recent list selection.
		 */
		private void doSelect() {
			String selection = "";

			if (selectionTextField.getText() == null
					|| selectionTextField.getText().equals("")) {
				selection = (String) examplesDropdown.getSelected();

				if (selection != null) {
					examplesDropdown.setSelected(null);
				} else {
					selection = (String) recentDropdown.getSelected();

					if (selection == null) {
						// No selection so don't change anything.
						return;
					}
				}
			} else {
				selection = selectionTextField.getText();
			}

			if (selection != null) {
				updateRecent(selection);
			}

			executeOnSelect();

			cardManager.makeVisible(mainDisplay);
		}

		/**
		 * Updates the recent list with the new selection.
		 *
		 * @param newSelection the new selection.
		 */
		private void updateRecent(final String newSelection) {
			List recentList = recentDropdown.getOptions();

			if (recentList.contains(newSelection)) {
				recentList.remove(newSelection);
			}
			recentList.add(0, newSelection);

			recentDropdown.setOptions(recentList);
			recentDropdown.setSelected(newSelection);
		}

		/**
		 * Handle select example.
		 */
		public void executeOnSelect() {
			List recent = pickerDialog.getRecentSelections();
			storeRecentList(recent);
			String selection = pickerDialog.getSelected();
			displaySelection(selection);
		}
	}
}
