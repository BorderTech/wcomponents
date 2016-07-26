package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.SystemException;
import java.text.MessageFormat;

/**
 * A selectable component with a button-like appearance.
 * @author Mark Reeves
 */
public class WToggleButton extends WCheckBox {
	/**
	 * Create a default WToggleButton.
	 */
	public WToggleButton() {
		super(false);
	}

	/**
	 * Create a WToggleButton in a given selected state.
	 * @param selected the selected state
	 */
	public WToggleButton(final boolean selected) {
		super(selected);
	}

	/**
	 * Create a WToggleButton with specific text.
	 * @param text the text to show on the toggle button
	 */
	public WToggleButton(final String text) {
		this(text, false);
	}

	/**
	 * Create a WToggleButton with specified text and selected state.
	 * @param selected thee selected state of the toggle button where {@code true} is selected
	 * @param text the text to show on the toggle button
	 */
	public WToggleButton(final String text, final boolean selected) {
		super(selected);
		setText(text);
	}

	/**
	 * Sets the button text.
	 *
	 * @param text the button text, using {@link MessageFormat} syntax
	 */
	public final void setText(final String text) {
		getOrCreateComponentModel().text = text;
	}

	/**
	 * @return the text to show on the toggle button
	 */
	public String getText() {
		return getComponentModel().text;
	}

	/**
	 * Do not allow defaultSubmitButton on a WToggleButton.
	 * @param defaultSubmitButton not used
	 */
	@Override
	public void setDefaultSubmitButton(final WButton defaultSubmitButton) {
		throw new SystemException("Cannot set default submit button on a toggle button.");
	}

	/**
	 *
	 * @return null as there cannot be a defaultSubmitButton on a WToggleButton.
	 */
	@Override
	public WButton getDefaultSubmitButton() {
		return null;
	}

	/**
	 * A WToggleButton cannot be mandatory as a mandatory toggle button is one which must be set into a selected state and if a control
	 * <strong>must</strong> be set into a selected state it is not a selection control.
	 * @return false
	 */
	@Override
	public boolean isMandatory() {
		return false;
	}

	/**
	 * Must not set submitOnChange on a WToggleButton. If you need a button which submits the form use
	 * {@link com.github.bordertech.wcomponents.WButton}.
	 * @param submitOnChange not used
	 */
	@Override
	public void setSubmitOnChange(final boolean submitOnChange) {
		throw new SystemException("Cannot set submitOnChange on a toggle button.");
	}

	/**
	 * @return false as WToggleButton cannot have submitOnChange: that would be a WButton
	 */
	@Override
	public boolean isSubmitOnChange() {
		return false;
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new ToggleButtonModel.
	 */
	@Override
	// For type safety only
	protected ToggleButtonModel newComponentModel() {
		return new ToggleButtonModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ToggleButtonModel getComponentModel() {
		return (ToggleButtonModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// For type safety only
	protected ToggleButtonModel getOrCreateComponentModel() {
		return (ToggleButtonModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds state information for the toggle button.
	 */
	public static class ToggleButtonModel extends CheckBoxModel {
		/**
		 * Text to show on the button.
		 */
		private String text;
	}
}
