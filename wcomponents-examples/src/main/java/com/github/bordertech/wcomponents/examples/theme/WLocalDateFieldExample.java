package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WLocalDateField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.layout.BorderLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.Disable;
import com.github.bordertech.wcomponents.subordinate.Enable;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import java.time.LocalDate;

/**
 * This example displays the features of {@link WLocalDateField}.
 *
 * @author John McGuinness
 */
public class WLocalDateFieldExample extends WContainer {

	/**
	 * The WLocalDateField used for getting and setting in this example.
	 */
	private final WLocalDateField dateField = new WLocalDateField();
	/**
	 * a readOnly WLocalDateField.
	 */
	private final WLocalDateField dateFieldReadOnly = new WLocalDateField();
	/**
	 * The main WFieldLayoutused in the example.
	 */
	private final WFieldLayout mainLayout = new WFieldLayout();

	/**
	 * Creates a WLocalDateFieldExample.
	 */
	public WLocalDateFieldExample() {
		/* The readOnly state of WLocalDateField is used to output non-interactive content. It is still an input though and should usually have a label
		 * of some kind. In this example all of the fields are added to a WFieldLayout.*/
		dateFieldReadOnly.setReadOnly(true);

		dateField.setActionOnChange(evt -> {
			dateFieldReadOnly.setData(dateField.getData());
		});

		final WTextField textField = new WTextField();
		textField.setColumns(40);
		textField.setDisabled(true);

		WButton copyDateBtn = new WButton("Copy text");
		copyDateBtn.setAction(evt -> {
			textField.setText(dateField.getText());
		});
		copyDateBtn.setAjaxTarget(textField);

		WButton copyJavaDateBtn = new WButton("Copy date value");
		copyJavaDateBtn.setAction(evt -> {
			LocalDate date = dateField.getLocalDate();
			if (date != null) {
				textField.setText(date.toString());
			} else {
				textField.setText(null);
			}
		});
		copyJavaDateBtn.setAjaxTarget(textField);

		WButton setTodayBtn = new WButton("Set 'Date' to today");
		setTodayBtn.setAction(evt -> {
			dateField.setLocalDate(LocalDate.now());
		});
		setTodayBtn.setAjaxTarget(dateField);

		mainLayout.addField("Date", dateField);
		mainLayout.addField("Text output", textField);

		mainLayout.addField("Read only date field", dateFieldReadOnly).getLabel().setHint(
				"populated from the editable date field above");

		/*
		 * WLocalDateFields with a date on load.
		 */
		WLocalDateField todayDF = new WLocalDateField();
		todayDF.setLocalDate(LocalDate.now());
		mainLayout.addField("Today", todayDF);

		WLocalDateField todayDFro = new WLocalDateField();
		todayDFro.setLocalDate(LocalDate.now());
		todayDFro.setReadOnly(true);
		mainLayout.addField("Today (read only)", todayDFro);

		/* disabled WLocalDateField */
		WLocalDateField disabledDateField = new WLocalDateField();
		disabledDateField.setDisabled(true);
		mainLayout.addField("Disabled", disabledDateField);

		disabledDateField = new WLocalDateField();
		disabledDateField.setLocalDate(LocalDate.now());
		disabledDateField.setDisabled(true);
		mainLayout.addField("Disabled (today)", disabledDateField);

		//do the layout
		WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
		buttonPanel.setMargin(new Margin(Size.LARGE, null, null, null));
		buttonPanel.setLayout(new BorderLayout());

		WPanel innerButtonPanel = new WPanel();
		innerButtonPanel.setLayout(new FlowLayout(Alignment.LEFT, Size.MEDIUM));
		buttonPanel.add(innerButtonPanel, BorderLayout.CENTER);
		innerButtonPanel.add(setTodayBtn);
		innerButtonPanel.add(copyDateBtn);
		innerButtonPanel.add(copyJavaDateBtn);
		/*
	* NOTE: the cancel button is here to test a previous race condition in the theme.
	* If you do not change the WLocalDateFields it should not cause an unsaved changes warning.
		 */
		WCancelButton cancelButton = new WCancelButton("Cancel");
		cancelButton.setAction(evt -> {
			mainLayout.reset();
		});
		buttonPanel.add(cancelButton, BorderLayout.EAST);

		add(mainLayout);
		add(buttonPanel);
		add(new WAjaxControl(dateField, dateFieldReadOnly));

		addDateRangeExample();
		addContraintExamples();
	}

	/**
	 * Add date range example.
	 */
	private void addDateRangeExample() {

		add(new WHeading(HeadingLevel.H2, "Example of a date range component"));
		WFieldSet dateRange = new WFieldSet("Enter the expected arrival and departure dates.");
		add(dateRange);

		WPanel dateRangePanel = new WPanel();
		dateRangePanel.setLayout(new FlowLayout(FlowLayout.LEFT, Size.MEDIUM));
		dateRange.add(dateRangePanel);
		final WLocalDateField arrivalDate = new WLocalDateField();
		final WLocalDateField departureDate = new WLocalDateField();
		//One could add some validation rules around this so that "arrival" was always earlier than or equal to "departure"
		WLabel arrivalLabel = new WLabel("Arrival", arrivalDate);
		arrivalLabel.setHint("dd MMM yyyy");
		WLabel departureLabel = new WLabel("Departure", departureDate);
		departureLabel.setHint("dd MMM yyyy");
		dateRangePanel.add(arrivalLabel);
		dateRangePanel.add(arrivalDate);
		dateRangePanel.add(departureLabel);
		dateRangePanel.add(departureDate);

		//subordinate control to ensure that the departure date is only enabled if the arrival date is populated
		WSubordinateControl control = new WSubordinateControl();
		add(control);
		Rule rule = new Rule(new Equal(arrivalDate, null));
		control.addRule(rule);
		rule.addActionOnTrue(new Disable(departureDate));
		rule.addActionOnFalse(new Enable(departureDate));
		control.addRule(rule);
	}

	/**
	 * Add constraint example.
	 */
	private void addContraintExamples() {
		add(new WHeading(HeadingLevel.H2, "Date fields with input constraints"));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(33);
		add(layout);

		/* mandatory */
		WLocalDateField constrainedDateField = new WLocalDateField();
		constrainedDateField.setMandatory(true);
		layout.addField("Mandatory date field", constrainedDateField);

		/* min date */
		constrainedDateField = new WLocalDateField();
		constrainedDateField.setMinDate(LocalDate.now());
		layout.addField("Minimum date today", constrainedDateField);

		/* max date */
		constrainedDateField = new WLocalDateField();
		constrainedDateField.setMaxDate(LocalDate.now());
		layout.addField("Maximum date today", constrainedDateField);

		/* auto complete */
		constrainedDateField = new WLocalDateField();
		constrainedDateField.setBirthdayAutocomplete();
		layout.addField("With autocomplete hint", constrainedDateField);
		constrainedDateField = new WLocalDateField();
		constrainedDateField.setAutocompleteOff();
		layout.addField("With autocomplete off", constrainedDateField);
	}
}
