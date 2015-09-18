package com.github.bordertech.wcomponents;

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
	 * @param richTextArea the number of rows.
	 */
	public void setRichTextArea(final boolean richTextArea) {
		getOrCreateComponentModel().richTextArea = richTextArea;
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
	}
}
