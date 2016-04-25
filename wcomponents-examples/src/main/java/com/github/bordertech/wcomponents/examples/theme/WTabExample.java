package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTab;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.util.Date;

/**
 * Examples of properties of {@link WTab}. For properties of {@link WTabSet} see {@link WTabSetExample}.
 *
 * @author exbtma
 */
public class WTabExample extends WPanel {

	/**
	 * Sample Long Text.
	 */
	private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent lectus."
			+ " Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Phasellus"
			+ " et turpis. Aenean convallis eleifend elit. Donec venenatis justo id nunc. Sed at purus vel quam mattis"
			+ " elementum. Sed ultrices lobortis orci. Pellentesque enim urna, volutpat at, sagittis id, faucibus sed,"
			+ " lectus. Integer dapibus nulla semper mi. Nunc posuere molestie augue. Aliquam varius libero in tortor."
			+ " Sed nibh. Nunc erat nunc, pellentesque at, sodales vel, dapibus sit amet, tortor.";

	/**
	 * a tab with dynamic content.
	 */
	private final WTab tabset1TabDynamic;
	/**
	 * a tab with lazy content.
	 */
	private final WTab tabset1TabLazy;
	/**
	 * a tab with eager content.
	 */
	private final WTab tabset1TabEager;
	/**
	 * a tab with server content.
	 */
	private final WTab tabset1TabServer;
	/**
	 * a tab with client content.
	 */
	private final WTab tabset1TabClient;

	/**
	 * some explanatory text.
	 */
	private final ExplanatoryText explanationWithTimeStamp;

	/**
	 * Display various tabs to show each property and option.
	 */
	public WTabExample() {
		super();
		add(new WHeading(HeadingLevel.H2, "Examples of WTab properties"));
		add(new WHeading(HeadingLevel.H3, "Tab Modes."));
		explanationWithTimeStamp = new ExplanatoryText(
				"The tabs in the following example each display the time at which the tab content was fetched. The time"
					+ " the page was rendered was " + (new Date()).toString());
		add(explanationWithTimeStamp);

		WTabSet tabset1 = new WTabSet();
		tabset1.addTab(new ExplanatoryText("This is the content of tab one."), "Client",
				TabMode.CLIENT);
		tabset1TabServer = tabset1.addTab(new WText(""), "Server", TabMode.SERVER);
		tabset1TabLazy = tabset1.addTab(new WText(""), "Lazy", TabMode.LAZY);
		tabset1TabDynamic = tabset1.addTab(new WText(""), "Dynamic", TabMode.DYNAMIC);
		tabset1TabEager = tabset1.addTab(new WText(""), "Eager", TabMode.EAGER);
		tabset1TabClient = tabset1.addTab(new ExplanatoryText(
				"This content was present when the page first rendered."), "Another Client",
				TabMode.CLIENT);
		tabset1.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));
		add(tabset1);

		add(new WHeading(HeadingLevel.H3, "One tab disabled."));
		WTabSet tabset2 = new SampleTabSet();
		/* you should do a null check on the tab but we are taking a shortcut knowing the structure of the tabset */
		tabset2.getTab(1).setDisabled(true);
		tabset2.getTab(1).setText("Disabled tab");
		add(tabset2);
		add(new WHorizontalRule());

		add(new WHeading(HeadingLevel.H3, "Active tab."));
		add(new ExplanatoryText(
				"If a tab is not set active explicitly then the first visible tab is open by default unless the user "
					+ "has previously set a different tab open in this session."));

		WTabSet tabset3 = new SampleTabSet(1);
		add(tabset3);
		//tabset3.setActiveIndex(1);
		/* Instead of using setActiveIndex it is possible to set the active tab using the tab content.
		 * TODO: this is stupid! setActiveTab should use the WTab not its content! */
		/*tabset3.setActiveTab(tabset3.getTab(1).getContent());*/

		add(new WHeading(HeadingLevel.H3, "Active tab disabled."));
		WTabSet tabset4 = new SampleTabSet(1);
		add(tabset4);
		tabset4.getTab(tabset4.getActiveIndex()).setDisabled(true);
		tabset4.getTab(tabset4.getActiveIndex()).setText("Active and disabled tab");

		add(new WHorizontalRule());

		/* NOTE: if the WTabSet is of TYPE_ACCORDION then several tabs may be open at the same time. */
		add(new WHeading(HeadingLevel.H3, "Tabs with access keys"));
		WTabSet tabset5 = new WTabSet();
		add(tabset5);
		tabset5.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));

		tabset5.addTab(new ExplanatoryText("Some content should go into here."), "First tab",
				WTabSet.TAB_MODE_CLIENT, 'T');
		tabset5.addTab(new ExplanatoryText(LONG_TEXT), "Second tab", WTabSet.TAB_MODE_CLIENT, 'S');

		//access key does not have to be in the tab's text label
		tabset5.addTab(new ExplanatoryText("Some other content should go into here."), "Third tab",
				WTabSet.TAB_MODE_CLIENT, 'X');

		add(new WHeading(HeadingLevel.H3, "Creating a Tab with non-text content."));
		WTabSet tabset7 = new WTabSet();
		add(tabset7);

		WImage imageTab1 = new WImage("/image/attachment.png", "Attachments");
		imageTab1.setCacheKey("eg-tab-image-1");
		WImage imageTab2 = new WImage("/image/error.png", "Errors");
		imageTab2.setCacheKey("eg-tab-image-2");
		WImage imageTab3 = new WImage("/image/flag.png", "Flags");
		imageTab3.setCacheKey("eg-tab-image-3");
		WImage imageTab4 = new WImage("/image/help.png", "Help");
		imageTab4.setCacheKey("eg-tab-image-4");

		tabset7.addTab(new WText("Some content for the attachments tab"), new WDecoratedLabel(
				imageTab1, new WText("Attachments"), null),
				WTabSet.TAB_MODE_CLIENT, '1');

		tabset7.addTab(new WText("Some content for the errors tab"), new WDecoratedLabel(imageTab2,
				new WText("Error List"), null),
				WTabSet.TAB_MODE_CLIENT, '2');

		tabset7.addTab(new WText("Some content for the flagged tab"), new WDecoratedLabel(imageTab3,
				new WText("Flagged Items"), null),
				WTabSet.TAB_MODE_CLIENT, '3');

		tabset7.addTab(new WText("Some content for the help tab"), new WDecoratedLabel(imageTab4),
				WTabSet.TAB_MODE_CLIENT, '4');



		add(new WHorizontalRule());

		add(new WHeading(HeadingLevel.H2, "Using WSubordinateControl"));
		WTabSet targetTabset = new SampleTabSet();
		add(targetTabset);

		WTab targetTab = targetTabset.getTab(1);

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		final WCheckBox disabledControllerCb = new WCheckBox();
		final WCheckBox hiddenControllerCb = new WCheckBox();
		layout.addField("disable second tab", disabledControllerCb);
		layout.addField("hide  second tab", hiddenControllerCb);


		// Build & add the subordinate
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(hiddenControllerCb, String.valueOf(true));
		builder.whenTrue().hide(targetTab);
		builder.whenFalse().show(targetTab);
		add(builder.build());

		builder = new SubordinateBuilder();
		builder.condition().equals(disabledControllerCb, String.valueOf(true));
		builder.whenTrue().disable(targetTab);
		builder.whenFalse().enable(targetTab);
		add(builder.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		explanationWithTimeStamp.setText("The time the page was rendered was " + (new Date()).toString());
		tabset1TabClient.setContent(new ExplanatoryText(
				"This content was present when the page first rendered at " + (new Date()).toString()));
		tabset1TabServer.setContent(new ExplanatoryText(
				"This is the content of tab two. It should be noted that mode SERVER is deprecated.\nThis mode poses a"
					+ " number of usability problems and should not be used.\n This content was created at "
					+ (new Date()).toString()));
		tabset1TabLazy.setContent(new ExplanatoryText(
				"This tab content is rendered when the tab opens then remains static. Check the date stamp: "
					+ (new Date()).toString()));
		tabset1TabDynamic.setContent(new ExplanatoryText(
				"This tab content refreshes each time it is opened. Check the date stamp: "
					+ (new Date()).toString()));
		tabset1TabEager.setContent(new ExplanatoryText(
				"This tab content is fetched once asynchronously then remains static. Check the date stamp: "
					+ (new Date()).toString()));
	}

	/**
	 * Sample tab set.
	 */
	private static final class SampleTabSet extends WTabSet {

		/**
		 * Construct tab set.
		 */
		private SampleTabSet() {
			super();
			init();
		}

		/**
		 * rubbish shortcut to create a tabset with a particular tab open. This is just to show how to default to a tab
		 * other than the first.
		 *
		 * @param activeIdx the tab index to set.
		 */
		private SampleTabSet(final int activeIdx) {
			this();
			WTab tab = getTab(activeIdx);
			if (tab != null) {
				setActiveIndex(activeIdx);
				getTab(activeIdx).setText("Active tab");
			}
		}

		/**
		 * Setup tabs et.
		 */
		private void init() {
			setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 24, 0));
			addTab(new ExplanatoryText("Some content should go into here."), "First tab",
					WTabSet.TAB_MODE_CLIENT);
			addTab(new ExplanatoryText(LONG_TEXT), "Second tab", WTabSet.TAB_MODE_CLIENT);
			addTab(new ExplanatoryText("Some other content should go into here."), "Third tab",
					WTabSet.TAB_MODE_CLIENT);
		}
	}
}
