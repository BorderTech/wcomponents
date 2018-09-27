package com.github.bordertech.wcomponents.test.components;


import com.github.bordertech.wcomponents.*;

/**
 * <p>
 * A simple UI to test, which contains a WTabSet with two tabs. The first
 * tab is client-side, so is always present. The second tab is lazy-loaded
 * via AJAX.</p>
 *
 * <p>
 * The first tab contains an example where the fields are not exposed via
 * getters. The second tab contains the same example, but with getters.</p>
 */
public class ExampleUI extends WContainer {

	/**
	 * The text duplicator used in the example.
	 */
	private final TextDuplicator textDuplicator = new TextDuplicator();

	/**
	 * The text duplicator (with getters) used in the example.
	 */
	private final TextDuplicator.TextDuplicatorWithGetters textDuplicatorWithGetters
		= new TextDuplicator.TextDuplicatorWithGetters();

	/**
	 * Creates an ExampleUI.
	 */
	public ExampleUI() {
		add(new TextDuplicator());
	}

	/**
	 * @return the text duplicator.
	 */
	public TextDuplicator getTextDuplicator() {
		return textDuplicator;
	}

	/**
	 * @return the text duplicator with getters.
	 */
	public TextDuplicator.TextDuplicatorWithGetters getTextDuplicatorWithGetters() {
		return textDuplicatorWithGetters;
	}


	/**
	 * A text duplicator example.
	 */
	private static class TextDuplicator extends WContainer {

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WTextField textFld = new WTextField();

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WButton duplicateButton = new WButton("Duplicate");

		/**
		 * The text field which the actions modify the state of.
		 */
		private final WButton clearButton = new WButton("Clear");

		/**
		 * Creates a TextDuplicator.
		 */
		TextDuplicator() {
			duplicateButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					String text = textFld.getText();
					textFld.setText(text + text);
				}
			});

			clearButton.setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					textFld.setText("");
				}
			});

			add(new WLabel("Text to duplicate", textFld));
			add(textFld);
			add(duplicateButton);
			add(clearButton);
		}

		/**
		 * @return the text field.
		 */
		WTextField getTextField() {
			return textFld;
		}

		/**
		 * @return the "duplicate" button.
		 */
		WButton getDuplicateButton() {
			return duplicateButton;
		}

		/**
		 * @return the "clear" button.
		 */
		WButton getClearButton() {
			return clearButton;
		}

		/**
		 * An extension of the text duplicator example which exposes the fields.
		 */
		private static class TextDuplicatorWithGetters extends TextDuplicator {

			/**
			 * @return the text field.
			 */
			@Override
			public WTextField getTextField() {
				return super.getTextField();
			}

			/**
			 * @return the "duplicate" button.
			 */
			@Override
			public WButton getDuplicateButton() {
				return super.getDuplicateButton();
			}

			/**
			 * @return the "clear" button.
			 */
			@Override
			public WButton getClearButton() {
				return super.getClearButton();
			}
		}
	}

}


