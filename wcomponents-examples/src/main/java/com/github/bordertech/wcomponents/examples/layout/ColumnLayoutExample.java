package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.CellAlignment;

/**
 * Example showing how to use the {@link ColumnLayout} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ColumnLayoutExample extends WContainer {

	/**
	 * Example column widths.
	 */
	private static final int[][] EXAMPLE_COLUMN_WIDTHS = {{10, 90}, {20, 80}, {30, 70}, {40, 60}, {50, 50},
	{60, 40}, {70, 30}, {80, 20}, {90, 10},
	{33, 33, 33}, {25, 25, 25, 25}, {20, 20, 20, 20, 20}};

	/**
	 * Example hgap, vgap widths.
	 */
	private static final int[][] EXAMPLE_HGAP_VGAP_WIDTHS = {{0, 0}, {25, 0}, {0, 25}, {6, 12}};

	/**
	 * Creates a ColumnLayoutExample.
	 */
	public ColumnLayoutExample() {

		for (int[] colWidths : EXAMPLE_COLUMN_WIDTHS) {
			addExample(colWidths);
		}
		for (int[] gaps : EXAMPLE_HGAP_VGAP_WIDTHS) {
			addHgapVGapExample(gaps[0], gaps[1]);
		}
		addAlignmentExample();
	}

	/**
	 * Adds an example to the set of examples.
	 *
	 * @param colWidths the percentage widths for each column.
	 */
	private void addExample(final int[] colWidths) {

		add(new WHeading(WHeading.SECTION, getTitle(colWidths)));

		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(colWidths));
		add(panel);

		for (int i = 0; i < colWidths.length; i++) {
			panel.add(new BoxComponent(colWidths[i] + "%"));
		}
		add(new WHorizontalRule());
	}

	/**
	 * Concatenates column widths to form the heading text for the example.
	 *
	 * @param widths the widths to concatenate.
	 * @return the title text.
	 */
	private static String getTitle(final int[] widths) {
		StringBuffer buf = new StringBuffer("Column widths: ");

		for (int i = 0; i < widths.length; i++) {
			if (i > 0) {
				buf.append(", ");
			}

			buf.append(widths[i]);
		}

		return buf.toString();
	}

	/**
	 * Build an example using hgap and vgap.
	 *
	 * @param hgap the hgap width
	 * @param vgap the vgap width
	 */
	private void addHgapVGapExample(final int hgap, final int vgap) {
		add(new WHeading(WHeading.SECTION, "Column Layout: hgap=" + hgap + " vgap=" + vgap));
		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(new int[]{25, 25, 25, 25}, hgap, vgap));
		add(panel);

		for (int i = 0; i < 8; i++) {
			panel.add(new BoxComponent("25%"));
		}

		add(new WHorizontalRule());
	}

	/**
	 * Build an example using column alignments.
	 */
	private void addAlignmentExample() {
		add(new WHeading(WHeading.SECTION, "Column Alignments: Left, Center, Right"));
		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(new int[]{33, 33, 33},
				new CellAlignment[]{CellAlignment.LEFT, CellAlignment.CENTER, CellAlignment.RIGHT}));
		add(panel);
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		add(new WHorizontalRule());
	}

}
