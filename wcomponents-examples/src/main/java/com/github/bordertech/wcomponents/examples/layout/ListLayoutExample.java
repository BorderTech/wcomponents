package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.ListLayout;

/**
 * Example showing how to use the {@link ListLayout} component.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class ListLayoutExample extends WContainer {

	/**
	 * Example list items.
	 */
	private static final String[] EXAMPLE_ITEMS = {"Apple", "Orange", "Banana", "Grape"};

	/**
	 * Creates a ColumnLayoutExample.
	 */
	public ListLayoutExample() {
		addExample("Stacked, Left Align", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false));
		addExample("Stacked, Center Align", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.CENTER,
				ListLayout.Separator.NONE, false));
		addExample("Stacked, Right Align", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.RIGHT,
				ListLayout.Separator.NONE, false));
		addExample("Flat, Left Align", new ListLayout(ListLayout.Type.FLAT,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false));
		addExample("Flat, Center Align", new ListLayout(ListLayout.Type.FLAT,
				ListLayout.Alignment.CENTER,
				ListLayout.Separator.NONE, false));
		addExample("Flat, Right Align", new ListLayout(ListLayout.Type.FLAT,
				ListLayout.Alignment.RIGHT,
				ListLayout.Separator.NONE, false));
		addExample("Striped, Left Align", new ListLayout(ListLayout.Type.STRIPED,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false));
		addExample("Striped, Center Align", new ListLayout(ListLayout.Type.STRIPED,
				ListLayout.Alignment.CENTER,
				ListLayout.Separator.NONE, false));
		addExample("Striped, Right Align", new ListLayout(ListLayout.Type.STRIPED,
				ListLayout.Alignment.RIGHT,
				ListLayout.Separator.NONE, false));
		addExample("Stacked, Bar Separator", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.BAR, false));
		addExample("Stacked, Dot Separator", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.DOT, false));
		addExample("Flat, Bar Separator", new ListLayout(ListLayout.Type.FLAT,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.BAR, false));
		addExample("Flat, Dot Separator", new ListLayout(ListLayout.Type.FLAT,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.DOT, false));
		addExample("Stacked, vgap=12", new ListLayout(ListLayout.Type.STACKED,
				ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false, 0, 12));
		addExample("Flat, hgap=12", new ListLayout(ListLayout.Type.FLAT, ListLayout.Alignment.LEFT,
				ListLayout.Separator.NONE, false, 12, 0));
	}

	/**
	 * Adds an example to the set of examples.
	 *
	 * @param heading the heading for the example
	 * @param layout the layout for the panel
	 */
	private void addExample(final String heading, final ListLayout layout) {
		add(new WHeading(WHeading.SECTION, heading));
		WPanel panel = new WPanel();
		panel.setLayout(layout);
		add(panel);
		for (String item : EXAMPLE_ITEMS) {
			panel.add(new WText(item));
		}
		add(new WHorizontalRule());
	}

}
