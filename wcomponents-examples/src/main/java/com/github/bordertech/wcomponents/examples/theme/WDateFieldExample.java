package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCancelButton;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
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
import java.util.Date;

/**
 * This example displays the features of {@link WDateField}.
 *
 * @author Ming Gao
 */
public class WDateFieldExample extends WContainer {

	/**
	 * The WDateField used for getting and setting in this example.
	 */
	private final WDateField dateField = new WDateField();
	/**
	 * a readOnly WDateField.
	 */
	private final WDateField dateFieldReadOnly = new WDateField();
	/**
	 * The main WFieldLayoutused in the example.
	 */
	private final WFieldLayout mainLayout = new WFieldLayout();

	/**
	 * Creates a WDateFieldExample.
	 */
	public WDateFieldExample() {
		/* The readOnly state of WDateField is used to output non-interactive
		 * content. It is still an input though and should usually have a label
		 * of some kind. In this example all of the fields are aded to a
		 * WFieldLayout.*/
		dateFieldReadOnly.setReadOnly(true);

		final WTextField textField = new WTextField();
		textField.setColumns(40);
		textField.setDisabled(true);

		WButton copyDateBtn = new WButton("Copy text from 'Date' to 'Text output'");
		copyDateBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				textField.setText(dateField.getText());
			}
		});
		copyDateBtn.setAjaxTarget(textField);

		WButton copyJavaDateBtn = new WButton("Copy date value from 'Date' to 'Text output'");
		copyJavaDateBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				Date date = dateField.getDate();

				if (date != null) {
					textField.setText(date.toString());
				} else {
					textField.setText(null);
				}
			}
		});
		copyJavaDateBtn.setAjaxTarget(textField);

		WButton setTodayBtn = new WButton("Set 'Date' to today");
		setTodayBtn.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				dateField.setDate(new Date());
			}
		});
		setTodayBtn.setAjaxTarget(dateField);

		mainLayout.addField("Date", dateField);
		mainLayout.addField("Text output", textField);

		mainLayout.addField("Read only date field", dateFieldReadOnly).getLabel().setHint(
				"populated from the editable date field above");

		/*
         * WDateFields with a date on load.
		 */
		WDateField todayDF = new WDateField();
		todayDF.setDate(new Date());
		mainLayout.addField("Today", todayDF);

		WDateField todayDFro = new WDateField();
		todayDFro.setDate(new Date());
		todayDFro.setReadOnly(true);
		mainLayout.addField("Today (read only)", todayDFro);

		/* disabled WDateField */
		WDateField disabledDateField = new WDateField();
		disabledDateField.setDisabled(true);
		mainLayout.addField("Disabled", disabledDateField);

		disabledDateField = new WDateField();
		disabledDateField.setDate(new Date());
		disabledDateField.setDisabled(true);
		mainLayout.addField("Disabled (today)", disabledDateField);

		//do the layout
		WPanel buttonPanel = new WPanel(WPanel.Type.FEATURE);
		buttonPanel.setMargin(new Margin(12, 0, 0, 0));
		buttonPanel.setLayout(new BorderLayout());

		WPanel innerButtonPanel = new WPanel();
		innerButtonPanel.setLayout(new FlowLayout(Alignment.LEFT, 6, 0));
		buttonPanel.add(innerButtonPanel, BorderLayout.CENTER);
		innerButtonPanel.add(setTodayBtn);
		innerButtonPanel.add(copyDateBtn);
		innerButtonPanel.add(copyJavaDateBtn);
		/*
         * NOTE: the cancel button is here to test a previous race condition in the theme.
         * If you do not change the WDateFields it should not cause an unsaved changes warning.
		 */
		WCancelButton cancelButton = new WCancelButton("Cancel");
		cancelButton.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				mainLayout.reset();
			}
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

		add(new WHeading(WHeading.MAJOR, "Example of a date range component"));
		WFieldSet dateRange = new WFieldSet("Enter the expected arrival and departure dates.");
		add(dateRange);

		WPanel dateRangePanel = new WPanel();
		dateRangePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
		dateRange.add(dateRangePanel);
		final WDateField arrivalDate = new WDateField();
		final WDateField departureDate = new WDateField();
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
		add(new WHeading(WHeading.MAJOR, "Date fields with input constraints"));
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(33);
		add(layout);

		/* mandatory */
		WDateField constrainedDateField = new WDateField();
		constrainedDateField.setMandatory(true);
		layout.addField("Mandatory date field", constrainedDateField);

		/* min date */
		constrainedDateField = new WDateField();
		constrainedDateField.setMinDate(new Date());
		layout.addField("Minimum date today", constrainedDateField);

		/* max date */
		constrainedDateField = new WDateField();
		constrainedDateField.setMaxDate(new Date());
		layout.addField("Maximum date today", constrainedDateField);
	}

	/**
	 * Reflect the value of dateField in the read only version.
	 *
	 * @param request the request being processed
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		dateFieldReadOnly.setDate(dateField.getDate());
	}
}
