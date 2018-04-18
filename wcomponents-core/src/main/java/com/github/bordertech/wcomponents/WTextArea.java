package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.autocomplete.AutocompleteUtil;
import com.github.bordertech.wcomponents.autocomplete.AutocompleteableMultiline;
import com.github.bordertech.wcomponents.autocomplete.segment.AddressType;
import com.github.bordertech.wcomponents.autocomplete.type.Multiline;
import com.github.bordertech.wcomponents.util.HtmlSanitizerUtil;

/**
 * <p>
 * A WTextArea is a wcomponent used to display a html textarea. It is very much like WTextField except that it has
 * multiple lines of input. Use the "setRows" method to define the number of lines displayed. Since 2015 this component
 * may also be put into Rich Text Editor mode.
 * </p>
 *
 * @author James Gifford
 * @author Jonathan Austin
 * @author Rick Brown
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTextArea extends WTextField implements AutocompleteableMultiline {

	/**
	 * The data for this WTextArea. If the text area is not rich text its output is XML escaped so we can ignore
	 * sanitization. If the text area is a rich text area then we check the sanitizeOnOutput flag as sanitization is
	 * rather resource intensive.
	 *
	 * @return The data for this WTextArea.
	 */
	@Override
	public Object getData() {
		Object data = super.getData();
		if (isRichTextArea() && isSanitizeOnOutput() && data != null) {
			return sanitizeOutputText(data.toString());
		}
		return data;
	}

	/**
	 * Set data in this component. If the WTextArea is a rich text input we need to sanitize the input.
	 *
	 * @param data The input data
	 */
	@Override
	public void setData(final Object data) {
		if (isRichTextArea() && data instanceof String) {
			super.setData(sanitizeInputText((String) data));
		} else {
			super.setData(data);
		}
	}

	/**
	 * @return the number of rows of text that are visible without scrolling.
	 */
	public int getRows() {
		return getComponentModel().rows;
	}

	/**
	 * Sets the number of rows of text that are visible without scrolling.
	 *
	 * @param rows the number of rows.
	 */
	public void setRows(final int rows) {
		getOrCreateComponentModel().rows = rows;
	}

	/**
	 * @return true if the field is in rich text mode.
	 */
	public boolean isRichTextArea() {
		return getComponentModel().richTextArea;
	}

	/**
	 * Pass true to put the field into rich text mode.
	 *
	 * @param richTextArea the number of rows
	 */
	public void setRichTextArea(final boolean richTextArea) {
		getOrCreateComponentModel().richTextArea = richTextArea;
	}

	/**
	 * Pass true if you need to run the HTML sanitizer on <em>any</em> output. This is only needed if the textarea is
	 * rich text as in other cases the output will be XML encoded.
	 *
	 * @param sanitize true if output sanitization is required.
	 */
	public void setSanitizeOnOutput(final boolean sanitize) {
		getOrCreateComponentModel().sanitizeOnOutput = sanitize;
	}

	/**
	 * @return true if this text area is to be sanitized on output.
	 */
	public boolean isSanitizeOnOutput() {
		return getComponentModel().sanitizeOnOutput;
	}

	/**
	 * @param text the output text to sanitize
	 * @return the sanitized text
	 */
	protected String sanitizeOutputText(final String text) {
		return HtmlSanitizerUtil.sanitizeOutputText(text);
	}

	/**
	 * @param text the input text to sanitize
	 * @return the sanitized text
	 */
	protected String sanitizeInputText(final String text) {
		return HtmlSanitizerUtil.sanitizeInputText(text);
	}

	@Override
	public void setAutocomplete(final Multiline value) {
		String strValue = value == null ? null : value.getValue();
		setAutocomplete(strValue);
	}

	@Override
	public void setFullStreetAddressAutocomplete(final AddressType value) {
		String combinedAddress = value == null
				? Multiline.STREET_ADDRESS.getValue()
				: AutocompleteUtil.getCombinedAutocomplete(value.getValue(), Multiline.STREET_ADDRESS.getValue());

		setAutocomplete(combinedAddress);
	}

	/**
	 * Creates a new TextAreaModel.
	 *
	 * @return a new TextAreaModel
	 */
	@Override
	protected TextAreaModel newComponentModel() {
		return new TextAreaModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TextAreaModel getComponentModel() {
		return (TextAreaModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected TextAreaModel getOrCreateComponentModel() {
		return (TextAreaModel) super.getOrCreateComponentModel();
	}

	/**
	 * TextAreaModel holds Extrinsic state management of the field.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class TextAreaModel extends TextFieldModel {

		/**
		 * The number of rows of text that are visible without scrolling.
		 */
		private int rows;

		/**
		 * If true this TextArea will be a Rich Text Field.
		 */
		private boolean richTextArea;

		/**
		 * If true this TextArea will sanitize HTML content when outputting data. This is opt-in because most textareas
		 * will not require output sanitization. It should be turned on if the text area is rich text and the upstream
		 * content (not user-input content) is unverified.
		 */
		private boolean sanitizeOnOutput = true;
	}
}
