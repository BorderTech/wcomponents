package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabSetType;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.util.Date;

/**
 * This component shows the different usages of the {@link WTabSet} component.
 *
 * @author Adam Millard
 * @author Mark Reeves
 * @since 1.0.0
 */
public class WTabSetExample extends WContainer {

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
	public WTabSetExample() {
		constructExample();
	}

	/**
	 * Helper to do the work of the constructor since we do not really want to call over-rideable methods in a
	 * constructor.
	 */
	private void constructExample() {
		add(new WHeading(HeadingLevel.H2, "Examples showing TabSet Type property."));
		add(new WHeading(HeadingLevel.H3, "Tabs at the top"));
		WTabSet tabset0 = new SampleWTabset();
		add(tabset0);

		add(new WHeading(HeadingLevel.H3, "Tabs at the top with explicit TYPE"));
		/* Explicitly setting TYPE_TOP is superfluous */
		WTabSet tabset1 = new SampleWTabset(WTabSet.TYPE_TOP);
		add(tabset1);

		add(new WHeading(HeadingLevel.H3, "Tabs on the LEFT"));
		WTabSet tabset1a = new SampleWTabset(WTabSet.TYPE_LEFT);
		add(tabset1a);

		add(new WHeading(HeadingLevel.H3, "Tabs on the RIGHT"));
		WTabSet tabset1b = new SampleWTabset(WTabSet.TYPE_RIGHT);
		add(tabset1b);

		add(new WHeading(HeadingLevel.H3, "ACCORDION tabs"));
		WTabSet tabset1c = new SampleWTabset(TabSetType.ACCORDION);
		add(tabset1c);

		add(new WHorizontalRule());

		/* Content height */
		add(new WHeading(HeadingLevel.H2, "Examples showing content height property."));
		add(new WHeading(HeadingLevel.H3, "Tall content."));
		WTabSet htabset1 = new SampleWTabset();
		htabset1.setContentHeight(TALL_CONTENT);
		add(htabset1);

		WTabSet htabset1a = new SampleWTabset(WTabSet.TYPE_LEFT);
		htabset1a.setContentHeight(TALL_CONTENT);
		add(htabset1a);

		WTabSet htabset1b = new SampleWTabset(WTabSet.TYPE_RIGHT);
		htabset1b.setContentHeight(TALL_CONTENT);
		add(htabset1b);

		WTabSet htabset1c = new SampleWTabset(TabSetType.ACCORDION);
		htabset1c.setContentHeight(TALL_CONTENT);
		add(htabset1c);

		add(new WHeading(HeadingLevel.H3, "Short content."));
		WTabSet htabset1d = new SampleWTabset();
		htabset1d.setContentHeight(SHORT_CONTENT);
		add(htabset1d);

		WTabSet htabset1e = new SampleWTabset(WTabSet.TYPE_LEFT);
		htabset1e.setContentHeight(SHORT_CONTENT);
		add(htabset1e);

		WTabSet htabset1f = new SampleWTabset(WTabSet.TYPE_RIGHT);
		htabset1f.setContentHeight(SHORT_CONTENT);
		add(htabset1f);

		WTabSet htabset1g = new SampleWTabset(TabSetType.ACCORDION);
		htabset1g.setContentHeight(SHORT_CONTENT);
		add(htabset1g);

		add(new WHorizontalRule());
		add(new WHeading(HeadingLevel.H2, "Examples showing disabled property."));

		/* NOTE: WTabSet does not currently implement SubordinateTarget therefore one cannot create a subordinate
		 * control to disable/enable a WTabSet.
		 */
		WTabSet disabledTabset = new SampleWTabset();
		disabledTabset.setDisabled(true);
		add(disabledTabset);


		add(new WHeading(HeadingLevel.H2, "Examples showing accordion's single property."));
		WTabSet singleOpenAccordionabset = new SampleWTabset(WTabSet.TYPE_ACCORDION);
		singleOpenAccordionabset.setSingle(true);
		add(singleOpenAccordionabset);

		add(new WHorizontalRule());

		/* NOTE: no example of the hidden property because WTabSet is not a subordinate target. This is inconsistent
		 * with the schema. */
		add(new WHeading(HeadingLevel.H2, "Setting the initially active tab"));
		add(new WHeading(HeadingLevel.H3,
				"Server side with tabs on the left, with second tab initially active."));
		WTabSet tabset3 = new WTabSet(WTabSet.TYPE_LEFT);
		tabset3.setContentHeight("10em");
		tabset3.addTab(new WText("Some content should go in here."), "First tab",
				WTabSet.TAB_MODE_SERVER, 'i');

		WPanel largeContent2 = new WPanel();
		largeContent2.setLayout(new FlowLayout(Alignment.VERTICAL));
		largeContent2.add(new WText(LONG_TEXT));
		largeContent2.add(new WText(LONG_TEXT));
		largeContent2.add(new WText(LONG_TEXT));
		tabset3.addTab(largeContent2, "Second tab which is much longer", WTabSet.TAB_MODE_SERVER,
				'e');

		WImage image = new WImage("/image/success.png", "success");
		image.setCacheKey("eg-image-tab");
		WDecoratedLabel tabLabel = new WDecoratedLabel(image, new WText("Third tab"), null);

		tabset3.addTab(new WText("The tab button for this tab has an image!"), tabLabel,
				WTabSet.TAB_MODE_SERVER, 'h');

		// Set 2nd tab active
		tabset3.setActiveIndex(1);
		add(tabset3);

		add(new WHeading(HeadingLevel.H3,
				"Client side with tabs on the right and the third tab initially active."));
		WText thirdContent = new WText("Some more content should go into here.");
		WTabSet tabset4 = new WTabSet(WTabSet.TYPE_RIGHT);
		tabset4.setContentHeight("10em");
		tabset4.addTab(new WText("Some content should go into here."), "First tab",
				WTabSet.TAB_MODE_CLIENT);
		tabset4.addTab(new WText(LONG_TEXT), "Second tab which is longer", WTabSet.TAB_MODE_CLIENT);
		tabset4.addTab(thirdContent, "Third tab", WTabSet.TAB_MODE_CLIENT);

		// Set 3rd tab active
		tabset4.setActiveTab(thirdContent);
		add(tabset4);

		add(new WHeading(HeadingLevel.H3, "Client side with showing lots of tabs."));
		add(new ExplanatoryText(
				"This will effectively show what happens when tabs need to wrap. You should do everything possible to avoid this situation."));
		WTabSet tabset5 = new WTabSet();
		tabset5.addTab(new WText("Tab 1."), "First tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 2."), "Second tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 3."), "Third tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 4."), "Fourth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 5."), "Fifth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 6."), "Sixth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 7."), "Seventh tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 8."), "Eighth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 9."), "Nineth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 10."), "Tenth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 11."), "Eleventh tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 12."), "Twelfth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 13."), "Thirteenth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 14."), "Fourteenth tab", WTabSet.TAB_MODE_CLIENT);
		tabset5.addTab(new WText("Tab 15."), "Fifteenth tab", WTabSet.TAB_MODE_CLIENT);
		add(tabset5);

		add(new WHorizontalRule());

		add(new WHeading(HeadingLevel.H2, "Using WSubordinateControl"));
		WTabSet targetTabset = new SampleWTabset();
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
			this(WTabSet.TYPE_TOP);
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
			addTab(new DateText("Content 1"), "Tab 1 (client)", WTabSet.TAB_MODE_CLIENT);
			addTab(new DateText("Content 2"), "Tab 2 (client)", WTabSet.TAB_MODE_CLIENT);
			addTab(new DateText("Content 3"), "Tab 3 (server)", WTabSet.TAB_MODE_SERVER);
			addTab(new DateText("Content 4"), "Tab 4 (lazy)", WTabSet.TAB_MODE_LAZY);
			addTab(new DateText("Content 5"), "Tab 5 (dynamic)", WTabSet.TAB_MODE_DYNAMIC);
			addTab(new DateText("Content 6"), "Tab 6 (eager)", WTabSet.TAB_MODE_EAGER);
			setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));
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
			return super.getText() + "<br/> Text generated at: " + new Date();
		}
	}
}
