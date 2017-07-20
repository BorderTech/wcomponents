package com.github.bordertech.wcomponents.examples.layout;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.ColumnLayout.Alignment;
import com.github.bordertech.wcomponents.util.HtmlClassProperties;

/**
 * Example showing how to use the {@link ColumnLayout} component.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ColumnLayoutExample extends WContainer {

	/**
	 * Example column widths.
	 */
	private static final int[][] EXAMPLE_COLUMN_WIDTHS = {{10, 90}, {20, 80}, {30, 70}, {40, 60}, {50, 50},
	{60, 40}, {70, 30}, {80, 20}, {90, 10},
	{33, 33, 33}, {25, 25, 25, 25}, {20, 20, 20, 20, 20}};

	private static final Size[][] EXAMPLE_GAPS = {{Size.ZERO, Size.ZERO},
		{Size.LARGE, Size.ZERO}, {Size.ZERO, Size.LARGE},
		{Size.SMALL, Size.MEDIUM}};

	/**
	 * Creates a ColumnLayoutExample.
	 */
	public ColumnLayoutExample() {
		for (int[] colWidths : EXAMPLE_COLUMN_WIDTHS) {
			addExample(colWidths);
		}
		for (Size[] gaps : EXAMPLE_GAPS) {
			addHgapVGapExample(gaps[0], gaps[1]);
		}
		addAlignmentExample();
		addAutoWidthExample();
		addAppLevelCSSExample();
		addResponsiveExample();
	}

	/**
	 * Adds an example to the set of examples.
	 *
	 * @param colWidths the percentage widths for each column.
	 */
	private void addExample(final int[] colWidths) {

		add(new WHeading(HeadingLevel.H2, getTitle(colWidths)));

		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(colWidths));
		add(panel);

		for (int i = 0; i < colWidths.length; i++) {
			panel.add(new BoxComponent(colWidths[i] + "%"));
		}
	}

	/**
	 * Add a column layout which will change its rendering on small screens.
	 */
	private void addResponsiveExample() {
		add(new WHeading(HeadingLevel.H2, "Default responsive design"));
		add(new ExplanatoryText("This example applies the theme's default responsive design rules for ColumnLayout.\n "
				+ "The columns have width and alignment and there is also a hgap and a vgap."));
				WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(new int[]{33, 33, 33},
				new Alignment[]{Alignment.LEFT, Alignment.CENTER, Alignment.RIGHT}, 12, 18));
		panel.setHtmlClass(HtmlClassProperties.RESPOND);
		add(panel);
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
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
	private void addHgapVGapExample(final Size hgap, final Size vgap) {
		add(new WHeading(HeadingLevel.H2, "Column Layout: hgap=" + hgap.toString() + " vgap=" + vgap.toString()));
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
		add(new WHeading(HeadingLevel.H2, "Column Alignments: Left, Center, Right"));
		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(new int[]{33, 33, 33},
				new Alignment[]{Alignment.LEFT, Alignment.CENTER, Alignment.RIGHT}));
		add(panel);
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		add(new WHorizontalRule());
	}

	/**
	 * This example shows a column which does not have widths set in Java. This is a "good thing": widths should be set in CSS.
	 */
	private void addAutoWidthExample() {
		add(new WHeading(HeadingLevel.H2, "Automatic (app defined) widths"));
		add(new ExplanatoryText("This example shows what happens if you use undefined (0) column width and do not then define them in CSS."));
		WPanel panel = new WPanel();
		panel.setLayout(new ColumnLayout(new int[]{0, 0, 0},
				new Alignment[]{Alignment.LEFT, Alignment.CENTER, Alignment.RIGHT}));
		add(panel);
		panel.add(new BoxComponent("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."));
		panel.add(new BoxComponent("Praesent eu turpis convallis, fringilla elit nec, ullamcorper purus. Proin dictum ac nunc rhoncus fringilla. "
				+ "Pellentesque habitant morbi tristique senectus et netus et malesuada fames."));
		panel.add(new BoxComponent("Vestibulum vehicula a turpis et efficitur. Integer maximus enim a orci posuere, id fermentum magna dignissim. "
				+ "Sed condimentum, dui et condimentum faucibus, quam erat pharetra."));
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));
		add(new WHorizontalRule());
	}
	/**
	 * Build an example with undefined column widths then use application-level CSS and a htmlClass property to define the widths.
	 *
	 */
	private void addAppLevelCSSExample() {
		String htmlClass = "my_local_class";
		add(new WHeading(HeadingLevel.H2, "Automatic (app defined) widths"));
		add(new ExplanatoryText("This example shows the use of a htmlClass and app-specific CSS (in this case inline) to style the columns.\n"
				+ "In this case the columns are: 20% and left, 50% and center, 30% and right; and the columns break to full width and are forced to "
				+ "left aligned at 1000px."));
		WPanel panel = new WPanel();
		panel.setHtmlClass(htmlClass);
		panel.setLayout(new ColumnLayout(new int[]{0, 0, 0},
				new Alignment[]{Alignment.LEFT, Alignment.CENTER, Alignment.RIGHT}));
		add(panel);
		panel.add(new BoxComponent("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."));
		panel.add(new BoxComponent("Praesent eu turpis convallis, fringilla elit nec, ullamcorper purus. Proin dictum ac nunc rhoncus fringilla. "
				+ "Pellentesque habitant morbi tristique senectus et netus et malesuada fames."));
		panel.add(new BoxComponent("Vestibulum vehicula a turpis et efficitur. Integer maximus enim a orci posuere, id fermentum magna dignissim. "
				+ "Sed condimentum, dui et condimentum faucibus, quam erat pharetra."));
		panel.add(new BoxComponent("Left"));
		panel.add(new BoxComponent("Center"));
		panel.add(new BoxComponent("Right"));

		// .columnLayout is the local name of ColumnLayout and is guranteed, row is now part of the WComponents CSS API but _may_ change.
		String rowSelector = "." + htmlClass + " > .wc-columnlayout > .wc-row";
		String columnSelector =  rowSelector + " > .wc-column";
		String css = columnSelector
				+ " {width: 20%}"
				+ columnSelector
				+ ":first-child {width: 50%}" // the first column in the layout
				+ columnSelector
				+ ":last-child {width: 30%;}" // the last column in the layout
				+ rowSelector
				+ " + .wc-row {margin-top: 0.5em;}"  // sibling rows in the column layout
				+ "@media only screen and (max-width: 1000px) {"  //when the screen goes below 1000px wide
				+ rowSelector
				+ " {display: block;}"
				+ columnSelector + ", " + columnSelector + ":first-child, " + columnSelector + ":last-child "
				+ " {display: inline-block; box-sizing: border-box; width: 100%; text-align: left;} "
				+ columnSelector
				+ " + .wc-column {margin-top: 0.25em;}}";

		WText cssText = new WText("<style type='text/css'>" + css + "</style>");
		cssText.setEncodeText(false);
		add(cssText);
		add(new WHorizontalRule());
	}
}
