package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.HtmlSanitizerUtil;
import com.github.bordertech.wcomponents.util.Util;
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

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
public class WTextArea extends WTextField {

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
	 * The the data for this WTextArea. If the text area is not rich text its output is XML escaped so we can ignore
	 * sanitization. If the text area is a rich text area then we check the sanitizeOnOutput flag as sanitization is
	 * rather resource intensive.
	 * @return The data for this WTextArea.
	 */
	@Override
	public Object getData() {
		Object data = super.getData();
		if (!(data instanceof String && this.isRichTextArea() && this.isSanitizeOnOutput())) {
			return data;
		}

		String dataString = (String) data;
		if (Util.empty(dataString)) {
			// no need to sanitize an empty string.
			return data;
		}

		try {
			// Sanitize using the output (lax) policy.
			return HtmlSanitizerUtil.sanitize(dataString, true);
		} catch (ScanException | PolicyException e) {
			// If the Sanitizer throws an error we are not able to sanitize so we will encode everything just in case.
			return StringEscapeUtils.escapeXml10(dataString);
		}
	}

	/**
	 * Set data in this component. If the WTextArea is a rich text input we need to sanitize the input.
	 * @param data The input data
	 */
	@Override
	public void setData(final Object data) {
		if (!(data instanceof String && this.isRichTextArea())) {
			super.setData(data);
		} else {
			String dataString = (String) data;

			if (Util.empty(dataString)) {
				// no need to sanitize an empty string.
				super.setData(data);
			} else {
				try {
					// first sanitize input to get rid of potentially harmful HTML.
					super.setData(HtmlSanitizerUtil.sanitize(dataString));
				} catch (ScanException | PolicyException e) {
					// If the Sanitizer throws an error we are not able to sanitize so we will encode everything
					super.setData(StringEscapeUtils.escapeXml10(dataString));
				}
			}
		}
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
		private boolean sanitizeOnOutput;
	}
}
