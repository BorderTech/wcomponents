package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WText;

/**
 * <p>
 * This is component looks a bit like a set of tabs. It has a row of buttons at the top that control which tab is
 * displayed in the space below the buttons. Use the addTab method to add a tab to this component.</p>
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SimpleTabs extends WContainer {

	/**
	 * Holds the TabButtons for each tab.
	 */
	private final WContainer btnPanel = new WContainer();
	/**
	 * Holds the content for each tab.
	 */
	private final WCardManager deck = new WCardManager();

	/**
	 * Creates a SimpleTabs.
	 */
	public SimpleTabs() {
		add(btnPanel, "buttons");
		add(new WHorizontalRule());
		add(deck, "deck");
	}

	/**
	 * Adds a tab.
	 *
	 * @param card the tab content.
	 * @param name the tab name.
	 */
	public void addTab(final WComponent card, final String name) {
		WContainer titledCard = new WContainer();
		WText title = new WText("<b>[" + name + "]:</b><br/>");
		title.setEncodeText(false);
		titledCard.add(title);
		titledCard.add(card);

		deck.add(titledCard);

		final TabButton button = new TabButton(name, titledCard);

		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				deck.makeVisible(button.getAssociatedCard());
			}
		});

		btnPanel.add(button);
	}

	/**
	 * An extension of WButton that holds the associated tab content.
	 *
	 * @author Martin Shevchenko
	 */
	static class TabButton extends WButton {

		/**
		 * The associated tab content.
		 */
		private final WComponent associatedCard;

		/**
		 * Creates a TabButton.
		 *
		 * @param name the tab name.
		 * @param associatedCard the tab content.
		 */
		TabButton(final String name, final WComponent associatedCard) {
			super(name);
			this.associatedCard = associatedCard;
		}

		/**
		 * @return the tab content.
		 */
		public WComponent getAssociatedCard() {
			return associatedCard;
		}
	}
}
