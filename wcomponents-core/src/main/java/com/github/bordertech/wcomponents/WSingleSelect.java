package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteableText;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.AutocompleteSegment;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.DateType;
import com.github.bordertech.wcomponents.autocomplete.type.Email;
import com.github.bordertech.wcomponents.autocomplete.type.Numeric;
import com.github.bordertech.wcomponents.autocomplete.type.Password;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.autocomplete.type.Url;
import com.github.bordertech.wcomponents.util.Util;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The WSingleSelect input component allows the user to select one option from a list. The list of options that can be
 * selected are supplied at construction time as a parameter in the constructor or via the {@link #setOptions(List)}
 * method. The list of options are java objects that are rendered using their toString() by default.
 * </p>
 * <p>
 * Use the {@link #getSelected() getSelected} method to determine which of the list of options was chosen by the user.
 * Note that getSelected returns one of the object instances supplied in the original list of options.
 * </p>
 *
 * @author Jonathan Austin
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WSingleSelect extends AbstractWSingleSelectList implements AjaxTrigger, AjaxTarget, SubordinateTrigger, SubordinateTarget,
		AutocompleteableText {

	/**
	 * Creates an empty WSingleSelect.
	 */
	public WSingleSelect() {
		this((List) null);
	}

	/**
	 * Creates a WSingleSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WSingleSelect(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WSingleSelect containing the specified options.
	 *
	 * @param options the options to display.
	 */
	public WSingleSelect(final List options) {
		super(options, true);
	}

	/**
	 * Creates a WSingleSelect with the options provided by the given table.
	 *
	 * @param table the table to obtain the list's options from.
	 */
	public WSingleSelect(final Object table) {
		super(table, true);
	}

	/**
	 * Sets the maximum number of rows that are visible in the list at any one time. If the number of rows is less than
	 * two, then the default number of rows will be displayed.
	 *
	 * @param rows the number of rows to display.
	 */
	public void setRows(final int rows) {
		getOrCreateComponentModel().rows = rows;
	}

	/**
	 * @return the number of rows to display in the list.
	 */
	public int getRows() {
		return getComponentModel().rows;
	}

	@Override
	public String getAutocomplete() {
		return getComponentModel().autocomplete;
	}

	@Override
	public void setAutocompleteOff() {
		if (!isAutocompleteOff()) {
			getOrCreateComponentModel().autocomplete = AutocompleteUtil.getOff();
		}
	}

	@Override
	public void addAutocompleteSection(final String sectionName) {
		String newValue = AutocompleteUtil.getCombinedForAddSection(sectionName, this);
		if (!Util.equals(getAutocomplete(), newValue)) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public void clearAutocomplete() {
		if (getAutocomplete() != null) {
			getOrCreateComponentModel().autocomplete = null;
		}
	}

	/**
	 * does the work of converting the various types of autocomplete helper to the {@code autocomplete} attribute values.
	 * @param value the value for the {@code autocomplete} attribute
	 */
	private void setAutocomplete(final String value) {
		if (!Util.equals(getAutocomplete(), value)) {
			getOrCreateComponentModel().autocomplete = value;
		}
	}

	@Override
	public void setAutocomplete(final DateType value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Email value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Numeric value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Password value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Url value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final AutocompleteSegment value) {
		final String strType = value == null ? null : value.getValue();
		setAutocomplete(strType);
	}

	@Override
	public void setAutocomplete(final Telephone phone, final PhoneFormat phoneType) {
		setAutocomplete(AutocompleteUtil.getCombinedFullPhone(phoneType, phone));
	}

	@Override
	public void setPhoneSegmentAutocomplete(final PhoneFormat phoneType, final PhonePart phoneSegment) {
		setAutocomplete(AutocompleteUtil.getCombinedPhoneSegment(phoneType, phoneSegment));
	}

	@Override
	public void setAddressAutocomplete(final AddressType addressType, final AddressPart addressPart) {
		setAutocomplete(AutocompleteUtil.getCombinedAddress(addressType, addressPart));
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new SingleSelectModel.
	 */
	@Override // For type safety only
	protected SingleSelectModel newComponentModel() {
		return new SingleSelectModel();
	}

	@Override // For type safety only
	protected SingleSelectModel getComponentModel() {
		return (SingleSelectModel) super.getComponentModel();
	}

	@Override // For type safety only
	protected SingleSelectModel getOrCreateComponentModel() {
		return (SingleSelectModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the component.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class SingleSelectModel extends SelectionModel {

		/**
		 * The auto-fill hint for the field.
		 */
		private String autocomplete;

		/**
		 * The number of visible rows to display.
		 */
		private int rows;
	}
}
