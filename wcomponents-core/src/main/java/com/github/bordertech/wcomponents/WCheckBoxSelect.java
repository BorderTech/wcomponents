package com.github.bordertech.wcomponents;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * WCheckBoxSelect is a convenience class which presents a group of check boxes for the user to select one or more
 * options from. Unlike {@link WCheckBox}, the check boxes inside a WCheckBoxSelect will automatically have text labels
 * associated with them. The visual arrangement of the check boxes can be configured using the
 * {@link #setButtonLayout(Layout)} method.</p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WCheckBoxSelect extends AbstractWMultiSelectList implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger, SubordinateTarget {

	/**
	 * An enumeration of button layouts.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum Layout {
		/**
		 * A layout where buttons are placed horizontally.
		 */
		FLAT,
		/**
		 * A layout where buttons are placed vertically.
		 */
		STACKED,
		/**
		 * A layout where buttons are placed in columns.
		 */
		COLUMNS
	};

	/**
	 * A layout where buttons are placed horizontally. Convenience constant for {@link WCheckBoxSelect.Layout#FLAT}.
	 */
	public static final Layout LAYOUT_FLAT = Layout.FLAT;
	/**
	 * A layout where buttons are placed vertically. Convenience constant for {@link WCheckBoxSelect.Layout#STACKED}.
	 */
	public static final Layout LAYOUT_STACKED = Layout.STACKED;
	/**
	 * A layout where buttons are placed in columns. Convenience constant for {@link WCheckBoxSelect.Layout#COLUMNS}.
	 */
	public static final Layout LAYOUT_COLUMNS = Layout.COLUMNS;

	/**
	 * Creates an empty WCheckBoxSelect.
	 */
	public WCheckBoxSelect() {
		this((List) null);
	}

	/**
	 * Creates a WCheckBoxSelect with the specified options.
	 *
	 * @param options the check box options.
	 */
	public WCheckBoxSelect(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WCheckBoxSelect with the specified options.
	 *
	 * @param options the check box options.
	 */
	public WCheckBoxSelect(final List options) {
		super(options, true);
	}

	/**
	 * Creates a WCheckBoxSelect with the options provided by the given table.
	 *
	 * @param table the table to obtain the options from.
	 */
	public WCheckBoxSelect(final Object table) {
		super(table, true);
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout the layout.
	 */
	public void setButtonLayout(final Layout layout) {
		CheckBoxSelectModel model = getOrCreateComponentModel();
		model.layout = layout;
		model.numColumns = 0;
	}

	/**
	 * Sets the layout to be a certain number of columns.
	 *
	 * @param numColumns the number of columns.
	 */
	public void setButtonColumns(final int numColumns) {
		if (numColumns < 1) {
			throw new IllegalArgumentException("Must have one or more columns");
		}

		CheckBoxSelectModel model = getOrCreateComponentModel();
		model.numColumns = numColumns;
		model.layout = numColumns == 1 ? LAYOUT_STACKED : LAYOUT_COLUMNS;
	}

	/**
	 * @return the number of columns for layout. Only applies to LAYOUT_COLUMNS layout.
	 */
	public int getButtonColumns() {
		return getComponentModel().numColumns;
	}

	/**
	 * @return the layout;
	 */
	public Layout getButtonLayout() {
		return getComponentModel().layout;
	}

	/**
	 * @return true if the frame should not be rendererd.
	 */
	public boolean isFrameless() {
		return getComponentModel().frameless;
	}

	/**
	 * @param frameless if true, the frame will not be rendered.
	 */
	public void setFrameless(final boolean frameless) {
		getOrCreateComponentModel().frameless = frameless;
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new CheckBoxSelectModel.
	 */
	@Override // For type safety only
	protected CheckBoxSelectModel newComponentModel() {
		return new CheckBoxSelectModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CheckBoxSelectModel getComponentModel() {
		return (CheckBoxSelectModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected CheckBoxSelectModel getOrCreateComponentModel() {
		return (CheckBoxSelectModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class CheckBoxSelectModel extends MultiSelectionModel {

		/**
		 * The check box button layout.
		 */
		private Layout layout = LAYOUT_STACKED;

		/**
		 * The number of columns for layout.
		 */
		private int numColumns = 0;

		/**
		 * Indicates whether the check box frame should be visible when rendering.
		 */
		private boolean frameless = false;
	}
}
