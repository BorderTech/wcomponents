package com.github.bordertech.wcomponents.autocomplete;

import com.github.bordertech.wcomponents.autocomplete.segment.AddressPart;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.segment.PhoneFormat;
import com.github.bordertech.wcomponents.autocomplete.segment.PhonePart;
import com.github.bordertech.wcomponents.autocomplete.type.Telephone;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;

/**
 * Provides help to acquire and format values for the {@code autocomplete} attribute found on some HTML controls. See
 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofilling-form-controls:-the-autocomplete-attribute"
 * target="_blank"> the HTML spec for autocomplete</a>.
 *
 * <p>
 * Note that there are some circumstances where the {@code autocomplete} attribute may be ignored or inferred. see <a
 * href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#autofill-processing-model:attr-fe-autocomplete-off-6">the spec</a>.
 * </p>
 *
 * @author Mark Reeves
 * @since 1.5.3
 */
public final class AutocompleteUtil {

	/**
	 * The separator used when an {@code autocomplete} attribute has multiple values.
	 */
	private static final String SEPARATOR = " ";

	/**
	 * The prefix which must be attached to a name to create a valid auto-fill section.
	 */
	private static final String SECTION_PREFIX = "section-";

	/**
	 * The value used to turn auto-fill off: must not be used with any other value.
	 */
	private static final String OFF = "off";

	/**
	 * Prevent instantiation.
	 */
	private AutocompleteUtil() {
	}

	/**
	 * @return the value of the {@code autocomplete} attribute which turns auto-fill off.
	 */
	public static String getOff() {
		return OFF;
	}

	/**
	 * Combines (preferably at least two otherwise this is pointless) Strings into a valid attribute value with appropriate separators. Note: if any
	 * argument is a case-insensitive match for "off" then only "off" will be returned.
	 *
	 * @param valueOne a value to add to the autocomplete attribute
	 * @param args any other values to add to the autocomplete attribute
	 * @return a value suitable for the HTML autocomplete attribute.
	 */
	public static String getCombinedAutocomplete(final String valueOne, final String... args) {
		if (OFF.equalsIgnoreCase(valueOne)) {
			return OFF;
		}
		StringBuilder builder = new StringBuilder();
		if (valueOne != null) {
			builder.append(valueOne);
		}

		if (args != null) {
			for (String val : args) {
				if (!Util.empty(val)) {
					if (OFF.equalsIgnoreCase(val)) {
						return OFF;
					}
					builder.append(SEPARATOR);
					builder.append(val);
				}
			}
		}

		String built = builder.toString().trim();
		if (Util.empty(built)) {
			return null;
		}
		return built;
	}

	/**
	 * Get an {@code autocomplete} attribute value for a named auto-fill section.
	 * <p>
	 * For information on sections see
	 * <a href="https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-section" target="_blank">
	 * https://html.spec.whatwg.org/multipage/form-control-infrastructure.html#attr-fe-autocomplete-section</a>. Note that in this use a section is
	 * <strong>not</strong> a HTML section element as provided by {@link com.github.bordertech.wcomponents.WSection}.
	 * </p>
	 *
	 * @param sectionName the name of the autocomplete section required
	 * @return an autocomplete value for a named section
	 */
	public static String getNamedSection(final String sectionName) {
		if (Util.empty(sectionName)) {
			throw new IllegalArgumentException("argument must not be empty");
		}
		return SECTION_PREFIX.concat(sectionName);
	}

	/**
	 * Combine autocomplete values into a single String suitable to apply to a named auto-fill section.
	 *
	 * @param sectionName the name of the autocomplete section
	 * @param args any other valid autocomplete values
	 * @return a single attribute value useful to apply an autocomplete helper to a named section
	 */
	public static String getCombinedForSection(final String sectionName, final String... args) {
		return getCombinedAutocomplete(getNamedSection(sectionName), args);
	}

	/**
	 * @param phoneType the type of phone number
	 * @param phone the telephone auto-fill variant, being full (including international prefix) or local (without international prefix)
	 * @return a {@code autocomplete} attribute value relevant for a particular type of phone number, in either of a full or local format
	 */
	public static String getCombinedFullPhone(final PhoneFormat phoneType, final Telephone phone) {
		if (phoneType == null && phone == null) {
			return null;
		}
		final String innerType = phoneType == null ? null : phoneType.getValue();
		final Telephone innerPhone = phone == null ? Telephone.FULL : phone;

		return AutocompleteUtil.getCombinedAutocomplete(innerType, innerPhone.getValue());
	}

	/**
	 * @param phoneType the type of phone number
	 * @param phonePart the phone number segment
	 * @return a {@code autocomplete} attribute value relevant for a specified telephone number type and segment
	 */
	public static String getCombinedPhoneSegment(final PhoneFormat phoneType, final PhonePart phonePart) {
		if (phoneType == null && phonePart == null) {
				return null;
		}
		String innerPhoneType = phoneType == null ? null : phoneType.getValue();
		String innerPhonePart = phonePart == null ? null : phonePart.getValue();
		return getCombinedAutocomplete(innerPhoneType, innerPhonePart);
	}

	/**
	 * @param addressType the type of address being auto-filled
	 * @param addressPart the address segment for the field
	 * @return a {@code autocomplete} attribute value relevant for an address or part thereof.
	 */
	public static String getCombinedAddress(final AddressType addressType, final AddressPart addressPart) {

		if (addressType == null && addressPart == null) {
			return null;
		}
		String innerAddressType = addressType == null ? null : addressType.getValue();
		String innerAddressPart = addressPart == null ? null : addressPart.getValue();

		return getCombinedAutocomplete(innerAddressType, innerAddressPart);
	}

	/**
	 * Helper to reduce typing in implementations of {@link Autocompleteable}.
	 * @param sectionName the name of the auto-fill section to add to the component's {@code autocomplete} attribute
	 * @param component the component being modified
	 * @return a value for the {@code autocomplete} attribute which is pre-pended by the formatted auto-fill section name
	 */
	public static String getCombinedForAddSection(final String sectionName, final Autocompleteable component) {
		if (Util.empty(sectionName)) {
			throw new IllegalArgumentException("Auto-fill section names must not be empty.");
		}
		if (component == null) {
			return getNamedSection(sectionName);
		}
		if (component.isAutocompleteOff()) {
			throw new SystemException("Auto-fill sections cannot be applied to fields with autocomplete off.");
		}

		String currentValue = component.getAutocomplete();
		return getCombinedForSection(sectionName, currentValue);
	}
}
