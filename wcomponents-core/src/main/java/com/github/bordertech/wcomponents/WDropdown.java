package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteableText;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The WDropdown component is used to let the user select a single option from a drop-down list. The list of options
 * that can be selected are supplied at construction time as a parameter in the constructor or via the
 * {@link #setOptions(List)} method. The list of options are java objects that are rendered using their toString() by
 * default.</p>
 *
 * <p>
 * Use the {@link #getSelected() getSelected} method to determine which of the list of options was chosen by the user.
 * Note that getSelected returns one of the object instances supplied in the original list of options.</p>
 *
 * <p>
 * The WDropdown component supports extensions using the {@link #setType(DropdownType)} method. The
 * {@link DropdownType#COMBO "combo"} drop-down extension allows the user to enter in a value which was not present in
 * the original list.</p>
 *
 * @author James Gifford
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WDropdown extends AbstractWSingleSelectList implements AjaxTrigger, AjaxTarget,
		SubordinateTrigger, SubordinateTarget, AutocompleteableText {

	@Override
	public String getAutocomplete() {
		return getComponentModel().autocomplete;
	}

	@Override
	public void setAutocomplete(final String autocompleteValue) {
		final String newValue = Util.empty(autocompleteValue) ? null : autocompleteValue;
		if (!Util.equals(newValue, getAutocomplete())) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public void setAutocompleteOff() {
		if (!AutocompleteUtil.OFF.equalsIgnoreCase(getAutocomplete())) {
			getOrCreateComponentModel().autocomplete = AutocompleteUtil.OFF;
		}
	}

	@Override
	public void addAutocompleteSection(final String sectionName) {
		if (Util.empty(sectionName)) {
			throw new IllegalArgumentException("Auto-fill section names must not be empty.");
		}
		String currentValue = getAutocomplete();
		if (AutocompleteUtil.OFF.equalsIgnoreCase(currentValue)) {
			throw new SystemException("Auto-fill sections cannot be applied to fields with autocomplete off.");
		}
		String newValue = AutocompleteUtil.getCombinedForSection(sectionName, currentValue);

		if (!Util.equals(currentValue, newValue)) {
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
	 * @param sectionName a name of an auto-fill section being the string represented by the asterisk in {@code section-*}
	 */
	private void setAutocompleteHelper(final String value, final String sectionName) {
		if (value == null && Util.empty(sectionName)) {
			clearAutocomplete();
			return;
		}

		final String current = getAutocomplete();
		String newValue = Util.empty(sectionName)
				? value
				: AutocompleteUtil.getCombinedForSection(sectionName, value);

		if (!Util.equals(current, newValue)) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.DateAutocomplete dateType, final String sectionName) {
		final String strType = dateType == null ? null : dateType.getValue();
		setAutocompleteHelper(strType, sectionName);
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.EmailAutocomplete value, final String sectionName) {
		final String strType = value == null ? null : value.getValue();
		setAutocompleteHelper(strType, sectionName);
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.NumericAutocomplete value, final String sectionName) {
		final String strType = value == null ? null : value.getValue();
		setAutocompleteHelper(strType, sectionName);
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.PasswordAutocomplete passwordType, final String sectionName) {
		final String strType = passwordType == null ? null : passwordType.getValue();
		setAutocompleteHelper(strType, sectionName);
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.TelephoneAutocompleteType phoneType, final AutocompleteUtil.TelephoneAutocomplete phone,
			final String sectionName) {
		if (phoneType == null && phone == null && Util.empty(sectionName)) {
			clearAutocomplete();
			return;
		}

		String newValue;
		final String innerType = phoneType == null ? null : phoneType.getValue();
		final AutocompleteUtil.TelephoneAutocomplete innerPhone = phone == null ? AutocompleteUtil.TelephoneAutocomplete.FULL : phone;

		if (Util.empty(sectionName)) {
			newValue = AutocompleteUtil.getCombinedAutocomplete(innerType, innerPhone.getValue());
		} else {
			newValue = AutocompleteUtil.getCombinedForSection(sectionName, innerType, innerPhone.getValue());
		}

		if (!Util.equals(getAutocomplete(), newValue)) {
			getOrCreateComponentModel().autocomplete = newValue;
		}
	}

	@Override
	public void setAutocomplete(final AutocompleteUtil.UrlAutocomplete value, final String sectionName) {
		final String strType = value == null ? null : value.getValue();
		setAutocompleteHelper(strType, sectionName);
	}

	/**
	 * The type of drop down.
	 *
	 * @deprecated COMBO no longer required. WSuggestions and a WTextfield should be used instead
	 */
	public enum DropdownType implements Serializable {
		/**
		 * A plain drop down.
		 */
		NATIVE,
		/**
		 * The drop down is a combo box.
		 *
		 * @deprecated WSuggestions and a WTextfield should be used instead
		 */
		COMBO
	};

	/**
	 * Creates an empty WDropdown.
	 */
	public WDropdown() {
		this((List) null);
	}

	/**
	 * Creates a WDropdown with the specified options.
	 *
	 * @param options the drop down options.
	 */
	public WDropdown(final Object[] options) {
		this(Arrays.asList(options));
	}

	/**
	 * Creates a WDropdown with the specified options.
	 *
	 * @param options the drop down options.
	 */
	public WDropdown(final List options) {
		super(options, false);
	}

	/**
	 * Creates a WDropdown with the options provided by the given table.
	 *
	 * @param table the table to obtain the dropdown's options from.
	 */
	public WDropdown(final Object table) {
		super(table, false);
	}

	/**
	 * Sets whether the users are able to enter in an arbitrary value, rather than having to pick one from the drop-down
	 * list.
	 *
	 * @param editable true for editable, false for fixed.
	 * @deprecated editable no longer required. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	@Override
	public void setEditable(final boolean editable) {
		setType(editable ? DropdownType.COMBO : DropdownType.NATIVE);
	}

	/**
	 * Indicates whether users are able to enter in an arbitrary value, rather than having to pick one from the
	 * drop-down list.
	 *
	 * @return true if the user can enter arbitrary values, false if not.
	 * @deprecated editable no longer required. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	@Override
	public boolean isEditable() {
		return DropdownType.COMBO.equals(getType());
	}

	/**
	 * Sets the type of this drop down. If un-set, reverts to the un-editable/native type.
	 *
	 * @param type one of native or combo.
	 * @deprecated No longer required as COMBO will be dropped. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	public void setType(final DropdownType type) {
		super.setEditable(DropdownType.COMBO.equals(type));
		getOrCreateComponentModel().type = type;
	}

	/**
	 * Indicates the type of this drop down.
	 *
	 * @return the drop down type, one of native or combo.
	 * @deprecated No longer required as COMBO will be dropped. WSuggestions and a WTextfield should be used instead
	 */
	@Deprecated
	public DropdownType getType() {
		return getComponentModel().type;
	}

	/**
	 * Set the width of the selectable options in a COMBO when rendered to screen. Has no effect with a native drop down.
	 *
	 * @param optionWidth the option width.
	 * @deprecated as DropdownType.COMBO is deprecated.
	 */
	@Deprecated
	public void setOptionWidth(final int optionWidth) {
		getOrCreateComponentModel().optionWidth = optionWidth;
	}

	/**
	 * Get the width of the selectable options when rendered to screen. Has no effect with a native drop down.
	 *
	 * @return the option width.
	 */
	public int getOptionWidth() {
		return getComponentModel().optionWidth;
	}

	/**
	 * Creates a new DropdownModel which holds Extrinsic state management of the drop down.
	 *
	 * @return a new DropdownModel.
	 */
	@Override
	protected DropdownModel newComponentModel() {
		return new DropdownModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected DropdownModel getComponentModel() {
		return (DropdownModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected DropdownModel getOrCreateComponentModel() {
		return (DropdownModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of the drop down.
	 */
	public static class DropdownModel extends SelectionModel {

		/**
		 * The drop down type.
		 */
		private DropdownType type;

		/**
		 * The option width.
		 */
		private int optionWidth;

		/**
		 * The auto-fill hint for the field.
		 */
		private String autocomplete;

		/**
		 * @param type the drop down type.
		 */
		public void setType(final DropdownType type) {
			this.type = type;
		}

		/**
		 * @return the drop down type.
		 */
		public DropdownType getType() {
			return type;
		}

		/**
		 * @param optionWidth the option width.
		 */
		public void setOptionWidth(final int optionWidth) {
			this.optionWidth = optionWidth;
		}

		/**
		 * @return the option width.
		 */
		public int getOptionWidth() {
			return optionWidth;
		}
	}
}
