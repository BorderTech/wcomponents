package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class is used to create a group of radio buttons.
 * </p>
 * <p>
 * A {@link WRadioButton} can only be created by using the {@link #addRadioButton(Object)} method or
 * {@link #addRadioButton()} method. Call one of these methods to get an instance of a radio button and then add the
 * radio button to the required location in the UI component tree.
 * </p>
 * <p>
 * If the value of the radio button is known when creating its instance, then use the {@link #addRadioButton(Object)}.
 * </p>
 * <p>
 * If a radio button needs to be used in a repeating component and will have different values depending on its context,
 * then use the {@link #addRadioButton()}. Radio buttons used with a {@link WRepeater} will get their value from the
 * "bean" associated to the row in the repeater. By default, the bean property for the radio button will be set to ".",
 * but this can be overridden by calling {@link #setBeanProperty(String)} on the radio button instance.
 * </p>
 * <p>
 * For the radio button group to work correctly, it is important that each radio button in the group has a unique value.
 * The radio button group uses the {@link String} representation of the radio button's value to identify which button
 * has been selected. As the string representation of the radio button's value is sent to the client, be mindful that it
 * should not be too large.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class RadioButtonGroup extends AbstractInput implements AjaxTrigger, SubordinateTrigger {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(RadioButtonGroup.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		Object data = getData();
		if (data == null) {
			return null;
		}
		// An empty string is treated as null
		return Util.empty(data.toString()) ? null : data.toString();
	}

	/**
	 * This method will only processes a request where the group is on the request and has no value. If the group has no
	 * value, then none of the group's radio buttons will be triggered to process the request.
	 *
	 * @param request the request being responded to.
	 * @return true if the group has changed, otherwise false
	 */
	@Override
	protected boolean doHandleRequest(final Request request) {
		// Check if the group has a value on the Request
		if (request.getParameter(getId()) != null) {
			// Allow the handle request to be processed by the radio buttons
			return false;
		}

		// If no value, then clear the current value (if required)
		if (getValue() != null) {
			setData(null);
			return true;
		}

		return false;
	}

	/**
	 * This method is to be only called by the {@link WRadioButton} that has its value on the request.
	 *
	 * @param request the request being responded to.
	 * @return true if the group has changed, otherwise false
	 */
	boolean handleButtonOnRequest(final Request request) {
		// Only process on a POST
		if (!"POST".equals(request.getMethod())) {
			LOG.warn("RadioButton on a request that is not a POST. Will be ignored.");
			return false;
		}

		String value = getRequestValue(request);
		String current = getValue();

		boolean changed = !Util.equals(value, current);

		if (changed) {
			setChangedInLastRequest(true);
			setData(value);
			doHandleChanged();
		}

		return changed;
	}

	/**
	 * The radio button group does not set focus. It allows the radio buttons to set their own focus if they are
	 * selected.
	 */
	@Override
	public void setFocussed() {
		// Do Nothing - Allow radio buttons to set the focus if they are selected
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestValue(final Request request) {
		if (isPresent(request)) {
			String value = request.getParameter(getId());
			// Treat empty as null
			return Util.empty(value) ? null : value;
		} else {
			return getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isPresent(final Request request) {
		return request.getParameter(getId() + "-h") != null;
	}

	/**
	 * Retrieves the selected value for this group.
	 *
	 * @return the value of the selected radio button, or null if there is no selection.
	 */
	public String getSelectedValue() {
		return getValue();
	}

	/**
	 * Sets the selected value for this group.
	 *
	 * @param value the selected value, or null to clear the selection.
	 */
	public void setSelectedValue(final String value) {
		setData(value);
	}

	/**
	 * Sets whether the form should be submitted when the selection changes.
	 *
	 * @param submitOnChange true to submit the form on change.
	 */
	@Override
	public void setSubmitOnChange(final boolean submitOnChange) {
		super.setSubmitOnChange(submitOnChange);
	}

	/**
	 * Indicates whether the form should be submitted when the selection changes.
	 *
	 * @return true if the form should be submitted on change.
	 */
	@Override
	public boolean isSubmitOnChange() {
		return super.isSubmitOnChange();
	}

	/**
	 * This method will add a radio button to the group with the given value. The value must be unique for the group.
	 * <p>
	 * The radio button returned by this method must be added to the required location in the UI Component tree.
	 * </p>
	 * <p>
	 * The radio button group uses the {@link String} representation of the radio button's value to identify which
	 * button has been selected. As the string representation of the radio button's value is sent to the client, be
	 * mindful that it should not be too large.
	 * </p>
	 *
	 * @param value a unique value for the radio button.
	 * @return the radio button that was added to the group
	 */
	public WRadioButton addRadioButton(final Object value) {
		WRadioButton radioButton = new WRadioButton(this);
		radioButton.setData(value);
		return radioButton;
	}

	/**
	 * This method will add a radio button to the group.
	 * <p>
	 * Unlike {{@link #addRadioButton(Object)}, which requires a value to be passed in, this method has no value passed
	 * in for the radio button, as it is expected the radio button will be used with a {@link WRepeater} and will get
	 * its value from a bean. The bean property will default to ".", but can be set to the appropriate bean property
	 * after being returned.
	 * </p>
	 * <p>
	 * The radio button returned by this method must be added to the required location in the UI Component tree.
	 * </p>
	 * <p>
	 * The radio button group uses the {@link String} representation of the radio button's value to identify which
	 * button has been selected. As the string representation of the radio button's value is sent to the client, be
	 * mindful that it should not be too large.
	 * </p>
	 *
	 * @return the radio button that was added to the group
	 */
	public WRadioButton addRadioButton() {
		WRadioButton radioButton = new WRadioButton(this);
		radioButton.setBeanProperty(".");
		return radioButton;
	}
}
