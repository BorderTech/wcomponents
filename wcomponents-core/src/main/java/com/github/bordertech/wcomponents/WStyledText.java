package com.github.bordertech.wcomponents;

/**
 * <p>
 * This component is used to render the different types of text. If the text contains special characters, they will be
 * escaped automatically, as in {@link WText}.</p>
 *
 * <p>
 * Note that the visual representation of the different types of text will depend on the current Theme in use.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WStyledText extends WText {

	/**
	 * The available types of text.
	 *
	 * @author Yiannis Paschalidis
	 */
	public enum Type {
		/**
		 * Plain text is not styled differently to normal text.
		 */
		PLAIN,
		/**
		 * Indicates high priority.
		 */
		HIGH_PRIORITY,
		/**
		 * Indicates medium priority.
		 */
		MEDIUM_PRIORITY,
		/**
		 * Indicates low priority.
		 */
		LOW_PRIORITY,
		/**
		 * The text will be emphasised.
		 */
		EMPHASISED,
		/**
		 * The text will display as an active indicator.
		 */
		ACTIVE_INDICATOR,
		/**
		 * The text will display as a match indicator.
		 */
		MATCH_INDICATOR,
		/**
		 * The text is styled as insert.
		 */
		INSERT,
		/**
		 * The text is styled as delete.
		 */
		DELETE,
		/**
		 * The text is styled with mandatory indicator.
		 */
		MANDATORY_INDICATOR
	};

	/**
	 * Specifies how white-space should be handled.
	 */
	public enum WhitespaceMode {
		/**
		 * Default whitespace handling. Browser-dependent behaviour, but most likely whitespace will be ignored.
		 */
		DEFAULT,
		/**
		 * In paragraph mode, new lines are treated as paragraph markers.
		 */
		PARAGRAPHS,
		/**
		 * This mode attempts to preserve all white-space formatting.
		 */
		PRESERVE
	}

	/**
	 * Creates a plain WStyledText with no text.
	 */
	public WStyledText() {
		this(null);
	}

	/**
	 * Creates a plain WStyledText with the given text.
	 *
	 * @param text the text.
	 */
	public WStyledText(final String text) {
		this(text, Type.PLAIN);
	}

	/**
	 * Creates a plain WStyledText with the given text and type.
	 *
	 * @param text the text.
	 * @param type the type of styling to use.
	 */
	public WStyledText(final String text, final Type type) {
		getComponentModel().setData(text);
		getComponentModel().type = type;
	}

	/**
	 * @return Returns the type.
	 */
	public Type getType() {
		return getComponentModel().type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(final Type type) {
		getOrCreateComponentModel().type = type;
	}

	/**
	 * @return Returns the white-space mode.
	 */
	public WhitespaceMode getWhitespaceMode() {
		return getComponentModel().whitespaceMode;
	}

	/**
	 * @param mode The mode to set.
	 */
	public void setWhitespaceMode(final WhitespaceMode mode) {
		getOrCreateComponentModel().whitespaceMode = mode;
	}

	/**
	 * Holds the extrinsic state information of a WStyledText.
	 *
	 * @author Yiannis Paschalidis
	 */
	public static class StyledTextModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The type of styling to use.
		 */
		private Type type;

		/**
		 * Indicates how white-space should be handled.
		 */
		private WhitespaceMode whitespaceMode = WhitespaceMode.DEFAULT;
	}

	/**
	 * Creates a new Component model.
	 *
	 * @return a new StyledTextModel.
	 */
	@Override
	protected StyledTextModel newComponentModel() {
		return new StyledTextModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected StyledTextModel getComponentModel() {
		return (StyledTextModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected StyledTextModel getOrCreateComponentModel() {
		return (StyledTextModel) super.getOrCreateComponentModel();
	}
}
