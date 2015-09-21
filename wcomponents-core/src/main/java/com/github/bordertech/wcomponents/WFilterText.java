package com.github.bordertech.wcomponents;

/**
 * WFilterText is an extension of {@link WText} that can be used to replace text. Both the search and replace regular
 * expressions must be set before text is filtered.
 *
 * Example usage for a quick way to replace new lines with paragraphs (Note: Use
 * {@link com.github.bordertech.wcomponents.WStyledText} for this):
 *
 * <pre>
 *    WFilterText text = new WFilterText("([^\\n\\r]+)", "&lt;p&gt;$1&lt;/p&gt;");
 *    text.setText("This text is in a paragraph.\nThis text is in another paragraph.");
 * </pre>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFilterText extends WText {

	/**
	 * Creates an empty WFilterText.
	 */
	public WFilterText() {
	}

	/**
	 * Creates a WFilterText with the given static text.
	 *
	 * @param text the static text.
	 */
	public WFilterText(final String text) {
		super(text);
	}

	/**
	 * Creates a WFilterText with the given search/replace strings.
	 *
	 * @param search the search string.
	 * @param replace the replacement string.
	 */
	public WFilterText(final String search, final String replace) {
		FilterTextModel model = getComponentModel();
		model.search = search;
		model.replace = replace;
	}

	/**
	 * Override in order to filter the encoded text.
	 *
	 * @return the filtered encoded text
	 */
	@Override
	public String getText() {
		String text = super.getText();
		FilterTextModel model = getComponentModel();

		if (text != null && model.search != null && model.replace != null) {
			text = text.replaceAll(model.search, model.replace);
		}

		return text;
	}

	/**
	 * @return Returns the replace.
	 */
	public String getReplace() {
		return getComponentModel().replace;
	}

	/**
	 * @param replace The replace to set.
	 */
	public void setReplace(final String replace) {
		getOrCreateComponentModel().replace = replace;
	}

	/**
	 * @return Returns the search.
	 */
	public String getSearch() {
		return getComponentModel().search;
	}

	/**
	 * @param search The search to set.
	 */
	public void setSearch(final String search) {
		getOrCreateComponentModel().search = search;
	}

	/**
	 * Creates a new FilterTextModel.
	 *
	 * @return a new FilterTextModel
	 */
	@Override
	protected FilterTextModel newComponentModel() {
		return new FilterTextModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FilterTextModel getComponentModel() {
		return (FilterTextModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override // For type safety only
	protected FilterTextModel getOrCreateComponentModel() {
		return (FilterTextModel) super.getOrCreateComponentModel();
	}

	/**
	 * FilterTextModel holds Extrinsic state management of the component.
	 */
	public static class FilterTextModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The search regexp.
		 */
		private String search;

		/**
		 * The replacement regexp.
		 */
		private String replace;
	}
}
