package com.github.bordertech.wcomponents;

/**
 * This component is useful to put hidden text on a page. Normal XML comments may be stripped by filters or XSLT.
 *
 * @author Darian Bridge
 * @since 1.0.0
 */
public class WHiddenComment extends WBeanComponent {

	/**
	 * Creates an empty WHiddenComment.
	 */
	public WHiddenComment() {
	}

	/**
	 * Creates a WHiddenComment with the specified text.
	 *
	 * @param text the hidden text.
	 */
	public WHiddenComment(final String text) {
		getOrCreateComponentModel().setData(text);
	}

	//================================
	// Attributes
	/**
	 * @return the hidden text.
	 */
	public String getText() {
		Object data = getData();
		return data == null ? null : data.toString();
	}

	/**
	 * Sets the hidden text.
	 *
	 * @param text the hidden text to set.
	 */
	public void setText(final String text) {
		setData(text);
	}
}
