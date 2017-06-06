package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabSetType;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.util.Date;

/**
 * This component shows the different usages of the {@link WTabSet} component in the ACCORDION state.
 *
 * @author Mark Reeves
 * @since 1.2.7
 */
public class AccordionExample extends WContainer {

	/**
	 * Sample long text for tab content.
	 */
	private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent lectus."
			+ " Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Phasellus "
			+ "et turpis. Aenean convallis eleifend elit. Donec venenatis justo id nunc. Sed at purus vel quam mattis "
			+ "elementum. Sed ultrices lobortis orci. Pellentesque enim urna, volutpat at, sagittis id, faucibus sed, "
			+ "lectus. Integer dapibus nulla semper mi. Nunc posuere molestie augue. Aliquam varius libero in tortor. "
			+ "Sed nibh. Nunc erat nunc, pellentesque at, sodales vel, dapibus sit amet, tortor.";
	/**
	 * Example content height for tall tabsets.
	 */
	private static final String TALL_CONTENT = "20em";
	/**
	 * Example content height for short tabsets.
	 */
	private static final String SHORT_CONTENT = "3em";

	/**
	 * Construct WTabSet examples.
	 */
	public AccordionExample() {
		constructExample();
	}

	/**
	 * Helper to do the work of the constructor since we do not really want to call over-rideable methods in a
	 * constructor.
	 */
	private void constructExample() {
		add(new WHeading(HeadingLevel.H3, "ACCORDION tabs"));
		WTabSet tabset1c = new SampleWTabset(TabSetType.ACCORDION);
		add(tabset1c);


		/* Content height */
		add(new WHeading(HeadingLevel.H2, "Examples showing content height property."));
		add(new WHeading(HeadingLevel.H3, "Tall content."));

		WTabSet htabset1c = new SampleWTabset(TabSetType.ACCORDION);
		htabset1c.setContentHeight(TALL_CONTENT);
		add(htabset1c);

		add(new WHeading(HeadingLevel.H3, "Short content."));

		WTabSet htabset1g = new SampleWTabset(TabSetType.ACCORDION);
		htabset1g.setContentHeight(SHORT_CONTENT);
		add(htabset1g);


		add(new WHeading(HeadingLevel.H2, "Examples showing accordion's single property."));
		WTabSet singleOpenAccordionabset = new SampleWTabset(WTabSet.TYPE_ACCORDION);
		singleOpenAccordionabset.setSingle(true);
		add(singleOpenAccordionabset);


		add(new WHeading(HeadingLevel.H2, "Using WSubordinateControl"));
		WTabSet targetTabset = new SampleWTabset(TabSetType.ACCORDION);
		add(targetTabset);

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		final WCheckBox disabledControllerCb = new WCheckBox();
		final WCheckBox hiddenControllerCb = new WCheckBox();
		layout.addField("disable tabset", disabledControllerCb);
		layout.addField("hide tabset", hiddenControllerCb);


		// Build & add the subordinate
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(hiddenControllerCb, String.valueOf(true));
		builder.whenTrue().hide(targetTabset);
		builder.whenFalse().show(targetTabset);
		add(builder.build());

		builder = new SubordinateBuilder();
		builder.condition().equals(disabledControllerCb, String.valueOf(true));
		builder.whenTrue().disable(targetTabset);
		builder.whenFalse().enable(targetTabset);
		add(builder.build());

	}

	/**
	 * Sample tab set.
	 */
	private static final class SampleWTabset extends WTabSet {

		/**
		 * Construct sample tab set.
		 */
		private SampleWTabset() {
			this(WTabSet.TYPE_ACCORDION);
		}

		/**
		 * @param type the tab set type
		 */
		private SampleWTabset(final WTabSet.TabSetType type) {
			super(type);
			init();
		}

		/**
		 * Setup the tab set.
		 */
		private void init() {


			addTab(sampleTabContent("Content 1"), "Tab 1 (client)", WTabSet.TAB_MODE_CLIENT);
			addTab(sampleTabContent("Content 2"), "Tab 2 (client)", WTabSet.TAB_MODE_CLIENT);
			addTab(sampleTabContent("Content 4"), "Tab 4 (lazy)", WTabSet.TAB_MODE_LAZY);
			addTab(sampleTabContent("Content 5"), "Tab 5 (dynamic)", WTabSet.TAB_MODE_DYNAMIC);
			addTab(sampleTabContent("Content 6"), "Tab 6 (eager)", WTabSet.TAB_MODE_EAGER);
			setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
		}


		/**
		 * Generate some content for the tabs.
		 * @param dateText some text to include in the content
		 * @return a WContainer with content.
		 */
		private WContainer sampleTabContent(final String dateText) {
			WContainer container = new WContainer();

			WStyledText txt = new WStyledText((new DateText(dateText)).getText());
			txt.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
			container.add(txt);

			WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_FLAT);
			layout.setLabelWidth(25);
			container.add(layout);

			layout.addField("Who are you?", new WTextField());
			layout.addField("I like bananas", new WCheckBox());
			layout.addField(new WButton("Save"));

			return container;
		}
	}

	/**
	 * A text component with some dynamic data, so we can see that content changes.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class DateText extends WText {

		/**
		 * Creates a DateText.
		 *
		 * @param text the text to prefix before the date.
		 */
		private DateText(final String text) {
			super(text);
			setEncodeText(false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText() {
			return super.getText() + " generated at: " + new Date();
		}
	}
}
