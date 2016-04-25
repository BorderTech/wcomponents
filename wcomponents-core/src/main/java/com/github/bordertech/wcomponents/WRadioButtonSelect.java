package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WSingleSelect.SingleSelectModel;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * WRadioButtonSelect is a convenience class which presents a group of radio buttons for the user to select one option
 * from. Unlike {@link WRadioButton}, an explicit {@link RadioButtonGroup} and individual labels for the radio buttons
 * do not need to be provided. The visual arrangement of the radio buttons can be configured using the
 * {@link #setButtonLayout(Layout)} method.</p>
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WRadioButtonSelect extends AbstractWSingleSelectList implements AjaxTrigger, AjaxTarget,
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
	 * A layout where buttons are placed horizontally. Convenience constant for {@link WRadioButtonSelect.Layout#FLAT}.
	 */
	public static final Layout LAYOUT_FLAT = Layout.FLAT;
	/**
	 * A layout where buttons are placed vertically. Convenience constant for {@link WRadioButtonSelect.Layout#STACKED}.
	 */
	public static final Layout LAYOUT_STACKED = Layout.STACKED;
	/**
	 * A layout where buttons are placed in columns. Convenience constant for {@link WRadioButtonSelect.Layout#COLUMNS}.
	 */
	public static final Layout LAYOUT_COLUMNS = Layout.COLUMNS;

	/**
	 * Creates an empty WRadioButtonSelect.
	 */
	public WRadioButtonSelect() {
		this((List) null);
	}

	/**
	 * Creates a WRadioButtonSelect with the specified options.
	 *
	 * @param options the radio button options.
	 */
	public WRadioButtonSelect(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WRadioButtonSelect with the specified options.
	 *
	 * @param options the radio button options.
	 */
	public WRadioButtonSelect(final List options) {
		super(options, true);
	}

	/**
	 * Creates a WRadioButtonSelect with the options provided by the given crt.
	 *
	 * @param table the table to obtain the group's options from.
	 */
	public WRadioButtonSelect(final Object table) {
		super(table, true);
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout the layout.
	 */
	public void setButtonLayout(final Layout layout) {
		RadioButtonSelectModel model = getOrCreateComponentModel();
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

		RadioButtonSelectModel model = getOrCreateComponentModel();
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
	 * @return true if the frame should not be rendered.
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
	 * Indicates whether this component is AJAX enabled. A list is an AJAX list if it has a
	 * {@link #setAjaxTarget(AjaxTarget) target set}.
	 *
	 * @return true if this list is AJAX enabled, false otherwise.
	 */
	@Override
	public boolean isAjax() {
		return super.isAjax();
	}

	/**
	 * Sets the default AJAX target for this list. If a target is supplied, an AJAX request is made rather than a
	 * round-trip to the server. The AJAX response will only contain the (possibly updated) target element rather than
	 * the entire UI.
	 *
	 * @param ajaxTarget the AJAX target.
	 */
	@Override
	public void setAjaxTarget(final AjaxTarget ajaxTarget) {
		super.setAjaxTarget(ajaxTarget);
	}

	/**
	 * Retrieves the default AJAX target.
	 *
	 * @return the default AJAX target for this list.
	 */
	@Override
	public AjaxTarget getAjaxTarget() {
		return super.getAjaxTarget();
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new RadioButtonSelectModel.
	 */
	@Override // For type safety only
	protected RadioButtonSelectModel newComponentModel() {
		return new RadioButtonSelectModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected RadioButtonSelectModel getComponentModel() {
		return (RadioButtonSelectModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected RadioButtonSelectModel getOrCreateComponentModel() {
		return (RadioButtonSelectModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class RadioButtonSelectModel extends SingleSelectModel {

		/**
		 * The button group layout.
		 */
		private Layout layout = LAYOUT_STACKED;

		/**
		 * The number of columns for layout.
		 */
		private int numColumns = 0;

		/**
		 * Indicates whether the radio button select frame should be visible when rendering.
		 */
		private boolean frameless = false;
	}
}
