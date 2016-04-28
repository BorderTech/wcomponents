package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WNumberField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.layout.GridLayout;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;
import java.util.List;

/**
 * <p>
 * This example demonstrates the {@link GridLayout} layout.
 * </p>
 *
 * @author Steve Harney
 * @since 1.0.0
 */
public class GridLayoutOptionsExample extends WContainer {

	/**
	 * the default column count in the layout.
	 */
	private static final int DEFAULT_COLUMN_COUNT = 12;

	/**
	 * the default number of boxes created in the layout.
	 */
	private static final int DEFAULT_BOX_COUNT = 36;

	/**
	 * the default number of rows in the layout.
	 */
	private static final int DEFAULT_ROW_COUNT = 0;

	/**
	 * the container for holding the grid layout instances.
	 * This has been converted to a WPanel to allow it to be an ajax target
	 */
	private final WPanel container = new WPanel();

	/**
	 * the number field for retrieving the number of columns required.
	 */
	private final WNumberField columnCount = new WNumberField();

	/**
	 * the number field for retrieving the number of rows required.
	 */
	private final WNumberField rowCount = new WNumberField();

	/**
	 * the number field for retrieving the horizontal gap.
	 */
	private final WNumberField hGap = new WNumberField();

	/**
	 * the number field for retrieving the vertical gap.
	 */
	private final WNumberField vGap = new WNumberField();

	/**
	 * the number field for retrieving the number of boxes to add to the layout.
	 */
	private final WNumberField boxCount = new WNumberField();

	/**
	 * the check box for setting the visibility on the {@link GridLayout}.
	 */
	private final WCheckBox cbVisible = new WCheckBox(true);

	/**
	 * Creates a GridLayoutExample.
	 */
	public GridLayoutOptionsExample() {

		WValidationErrors errors = new WValidationErrors();
		add(errors);
		add(getLayoutControls(errors));
		this.add(container);
	}

	/**
	 * build the list controls field set.
	 *
	 * @param errors the error box to be linked to the apply button.
	 * @return a field set for the controls.
	 */
	private WFieldSet getLayoutControls(final WValidationErrors errors) {
		// Options Layout
		WFieldSet fieldSet = new WFieldSet("List configuration");
		WFieldLayout layout = new ControlFieldLayout();
		fieldSet.add(layout);

		// options.
		columnCount.setDecimalPlaces(0);
		columnCount.setMinValue(0);
		columnCount.setNumber(DEFAULT_COLUMN_COUNT);
		columnCount.setMandatory(true);
		layout.addField("Number of Columns", columnCount);
		rowCount.setDecimalPlaces(0);
		rowCount.setMinValue(0);
		rowCount.setNumber(DEFAULT_ROW_COUNT);
		rowCount.setMandatory(true);
		layout.addField("Number of Rows", rowCount);
		hGap.setDecimalPlaces(0);
		hGap.setMinValue(0);
		hGap.setNumber(0);
		hGap.setMandatory(true);
		layout.addField("Horizontal Gap", hGap);
		vGap.setDecimalPlaces(0);
		vGap.setMinValue(0);
		vGap.setNumber(0);
		vGap.setMandatory(true);
		layout.addField("Vertical Gap", vGap);
		boxCount.setDecimalPlaces(0);
		boxCount.setMinValue(0);
		boxCount.setNumber(DEFAULT_BOX_COUNT);
		boxCount.setMandatory(true);
		layout.addField("Number of Boxes", boxCount);

		layout.addField("Visible", cbVisible);

		// Apply Button
		WButton apply = new WButton("Apply");
		apply.setAction(new ValidatingAction(errors, this) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				applySettings();
			}
		});

		fieldSet.add(apply);

		fieldSet.add(new WAjaxControl(apply, container));
		fieldSet.setMargin(new Margin(0, 0, 12, 0));
		return fieldSet;
	}

	/**
	 * reset the container that holds the grid layout and create a new grid layout with the appropriate properties.
	 */
	private void applySettings() {
		container.reset();
		// Now show an example of the number of different columns

		WPanel gridLayoutPanel = new WPanel();
		GridLayout layout = new GridLayout(rowCount.getValue().intValue(), columnCount.getValue()
				.intValue(),
				hGap.getValue().intValue(),
				vGap.getValue().intValue()
		);

		gridLayoutPanel.setLayout(layout);

		addBoxes(gridLayoutPanel, boxCount.getValue().intValue()); // give approx 3 rows, with a different number
		// of boxes on the final row
		container.add(gridLayoutPanel);

		gridLayoutPanel.setVisible(cbVisible.isSelected());
	}

	/**
	 * Adds a set of boxes to the given panel.
	 *
	 * @param panel the panel to add the boxes to.
	 * @param amount the number of boxes to add.
	 */
	private static void addBoxes(final WPanel panel, final int amount) {
		for (int i = 1; i <= amount; i++) {
			panel.add(new BoxComponent(String.valueOf(i)));
		}
	}

	/**
	 * Override preparePaintComponent to perform initialisation the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	public void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			applySettings();

			setInitialised(true);
		}
	}

	/**
	 * the ControlFieldLayout extends the WField layout to be able to link the row and column controls together.
	 *
	 * @author Steve Harney
	 */
	private class ControlFieldLayout extends WFieldLayout {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void validateComponent(final List<Diagnostic> diags) {
			super.validateComponent(diags);
			if (columnCount.getValue().intValue() == 0 && rowCount.getValue().intValue() == 0) {
				// Note that this error will hyperlink to row count field.
				diags.add(createErrorDiagnostic(rowCount, "Both rows and columns cannot be zero."));
			}
		}

	}

}
