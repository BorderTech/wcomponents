package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;

/**
 * <p>
 * This component demonstrates the usage of the {@link WFieldLayout}, components. These are in the following
 * configurations. </p>
 *
 * <ul>
 * <li>Flat (Default)</li>
 * <li>Stacked</li>
 * </ul>
 *
 * <p>
 * This also demonstrates the behaviour of the {@link WCheckBox} within a {@link WCheckBox}.
 * </p>
 *
 * @author Adam Millard
 */
public class WFieldLayoutExample extends WPanel {

	private final WText cssText = new WText();
	private final WPanel legalNumberedPanel = new WPanel();

	/**
	 * Creates a WFieldLayoutExample.
	 */
	public WFieldLayoutExample() {
		WHeading heading = new WHeading(HeadingLevel.H2, "\'Flat\' WFieldLayout");
		add(heading);

		WFieldLayout layout = new WFieldLayout();
		layout.setTitle("\'Flat\' WFieldLayout");
		layout.addField("WTextField 1", new WTextField()).setInputWidth(100);
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		heading = new WHeading(HeadingLevel.H2, "\'Stacked\' WFieldLayout");
		add(heading);

		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setTitle("\'Stacked\' WFieldLayout");
		layout.addField("WTextField 1", new WTextField()).setInputWidth(100);
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		heading = new WHeading(HeadingLevel.H2, "\'Ordered\' WFieldLayout");
		add(heading);

		layout = new WFieldLayout();
		layout.setOrdered(true);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		heading = new WHeading(HeadingLevel.H2, "\'Ordered\' WFieldLayout with offset 6");
		add(heading);

		layout = new WFieldLayout();
		layout.setOrdered(true);
		layout.setOrderedOffset(6);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);
//
//		add(new WHorizontalRule());
//		add(new WHeading(HeadingLevel.H2, "Using labelWidth"));
//
//		add(new WHeading(HeadingLevel.H3, "Flat with labelWidth=33"));
//		layout = new WFieldLayout();
//		layout.setLabelWidth(33);
//		layout.addField("WTextField 1", new WTextField());
//		layout.addField("WTextField 2", new WTextField());
//		layout.addField("WCheckBox", new WCheckBox());
//		add(layout);

		add(new WHeading(HeadingLevel.H3, "Flat with labelWidth=50"));
		layout = new WFieldLayout();
		layout.setLabelWidth(50);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Flat with labelWidth=67"));
		layout = new WFieldLayout();
		layout.setLabelWidth(67);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Stacked with labelWidth=33"));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setLabelWidth(33);
		layout.addField(
				"WTextField 1 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField(
				"WTextField 2 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Stacked with labelWidth=50"));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setLabelWidth(50);
		layout.addField(
				"WTextField 1 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField(
				"WTextField 2 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Stacked with labelWidth=67"));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setLabelWidth(67);
		layout.addField(
				"WTextField 1 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField(
				"WTextField 2 which has an inordinately ong label which is quite unnecessary and is in itself most likely an a11y issue for some users but we shall persist in the aid of providing a testable piece of code.",
				new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Ordered and Flat with labelWidth=33"));
		layout = new WFieldLayout();
		layout.setOrdered(true);
		layout.setLabelWidth(33);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Ordered and Flat with labelWidth=67"));
		layout = new WFieldLayout();
		layout.setOrdered(true);
		layout.setLabelWidth(67);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Ordered and Stacked with labelWidth=33"));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setOrdered(true);
		layout.setLabelWidth(33);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H3, "Oredered and Stacked with labelWidth=67"));
		layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		layout.setOrdered(true);
		layout.setLabelWidth(67);
		layout.addField("WTextField 1", new WTextField());
		layout.addField("WTextField 2", new WTextField());
		layout.addField("WCheckBox", new WCheckBox());
		add(layout);

		add(new WHeading(HeadingLevel.H2, "Nested ordered layouts"));
		add(recursiveFieldLayout(0, 1));
		add(recursiveFieldLayout(0, 6));

		add(new WHeading(HeadingLevel.H2, "Nested mixed layouts"));
		add(recursiveFieldLayout(true, 0, 1));
		add(recursiveFieldLayout(true, 0, 6));
	}

	/**
	 * Create a recursive field layout.
	 *
	 * @param curr recursion index
	 * @param startAt the ordered offset
	 * @return the recursive field layout.
	 */
	private WFieldLayout recursiveFieldLayout(final int curr, final int startAt) {
		WFieldLayout innerLayout = new WFieldLayout();
		innerLayout.setLabelWidth(20);

		if (curr == 0 && startAt == 0) {
			innerLayout.setMargin(new Margin(12, 0, 0, 0));
		}
		innerLayout.setOrdered(true);

		if (startAt > 1) {
			innerLayout.setOrderedOffset(startAt);
		}
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt : 1), new WTextField());
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt + 1 : 2),
				new WTextField());
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt + 2 : 2),
				new WTextField());
		if (curr < 4) {
			int next = curr + 1;
			innerLayout.addField("indent level " + String.valueOf(next), recursiveFieldLayout(next, 0));
		}
		innerLayout.addField("Test after nest", new WTextField());
		return innerLayout;
	}

	/**
	 * Create a recursive field layout.
	 *
	 * @param ordered true if all nested layouts are ordered.
	 * @param curr recursion index
	 * @param startAt the ordered offset
	 * @return the recursive field layout.
	 */
	private WFieldLayout recursiveFieldLayout(final boolean ordered, final int curr, final int startAt) {
		WFieldLayout innerLayout = new WFieldLayout();
		innerLayout.setLabelWidth(20);

		if (curr == 0 && startAt == 0) {
			innerLayout.setMargin(new Margin(12, 0, 0, 0));
		}
		innerLayout.setOrdered(ordered);

		if (ordered && startAt > 1) {
			innerLayout.setOrderedOffset(startAt);
		}
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt : 1), new WTextField());
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt + 1 : 2),
				new WTextField());
		innerLayout.addField("Test " + String.valueOf(startAt > 1 ? startAt + 2 : 2),
				new WTextField());
		if (curr < 4) {
			int next = curr + 1;
			innerLayout.addField("indent level " + String.valueOf(next), recursiveFieldLayout(curr % 2 == 1, next,
					0));
		}
		innerLayout.addField("Test after nest", new WTextField());
		return innerLayout;
	}
}
