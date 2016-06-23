package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * <p>
 * WText is used to render some basic/raw text.
 * </p>
 *
 * @author Adam Millard
 * @since 1.0.0
 *
 * @author Yiannis Paschalidis - rewritten as a bean aware component in Sfp_Common10
 */
public class WText extends WBeanComponent {

	/**
	 * Creates an empty WText.
	 */
	public WText() {
	}

	/**
	 * Creates a WText with the given initial text.
	 *
	 * @param text the text to display, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 *
	 * <pre>
	 * // Will display the text "Hello world"
	 * new WText("Hello world");
	 * </pre>
	 *
	 * <pre>
	 * // Will display "Secret agent James Bond, 007"
	 * new WText("Secret agent {0}, {1,number,000}", "James Bond", 7);
	 * </pre>
	 */
	public WText(final String text, final Serializable... args) {
		getComponentModel().setData(I18nUtilities.asMessage(text, args));
	}

	//================================
	// Attributes
	/**
	 * @return the current text.
	 */
	public String getText() {
		return I18nUtilities.format(null, getData());
	}

	/**
	 * <p>
	 * Sets the text.</p>
	 *
	 * <p>
	 * NOTE: If the text is dynamically generated, it may be preferable to override {@link #getText()} instead. This
	 * will reduce the amount of data which is stored in the user session.
	 * </p>
	 *
	 * @param text the text to set, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the message format string.
	 *
	 * <pre>
	 * // Changes the text to "Hello world"
	 * myText.setText("Hello world");
	 * </pre>
	 *
	 * <pre>
	 * // Changes the text to "Secret agent James Bond, 007"
	 * new WText("Secret agent {0}, {1,number,000}", "James Bond", 7);
	 * </pre>
	 */
	public void setText(final String text, final Serializable... args) {
		setData(I18nUtilities.asMessage(text, args));
	}

	/**
	 * Indicates whether the heading text needs to be encoded.
	 *
	 * @return true if the text needs to be encoded, false if not.
	 */
	public boolean isEncodeText() {
		return isFlagSet(ComponentModel.ENCODE_TEXT_FLAG);
	}

	/**
	 * <p>
	 * Sets whether the text needs to be encoded.</p>
	 *
	 * <p>
	 * When setting <code>encodeText</code> to <code>false</code>, it then becomes the responsibility of the application
	 * to ensure that the text does not contain any characters which need to be escaped.</p>
	 *
	 * <p>
	 * <b>WARNING:</b> If you are using WText to display "user entered" or untrusted data, use of this method with
	 * <code>encodeText</code> set to <code>false</code> may result in security issues.</p>
	 *
	 * <p>
	 * <b>WARNING:</b> Avoid using WText to add excessive amounts of HTML mark-up to the UI. Embedded mark-up is more
	 * likely to break in future WComponent releases. If you are attempting to use WText for layout purposes, consider
	 * using a {@link com.github.bordertech.wcomponents.layout.LayoutManager LayoutManager} instead. For example,
	 * <code>new WText("&lt;br&gt;")</code> could be rewritten to use the
	 * {@link com.github.bordertech.wcomponents.layout.FlowLayout FlowLayout} layout with a vertical layout
	 * direction.</p>
	 *
	 * @param encodeText true if the text needs to be encoded, false if not.
	 */
	public void setEncodeText(final boolean encodeText) {
		setFlag(ComponentModel.ENCODE_TEXT_FLAG, encodeText);
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : ('"' + text + '"');
		return toString(text);
	}
}
