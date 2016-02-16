package com.github.bordertech.wcomponents.examples.theme;

import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;

/**
 * Example showing how to use the {@link WRow} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WRowExample extends WPanel {

	/**
	 * Creates a WRowExample.
	 */
	public WRowExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));

		add(new WHeading(HeadingLevel.H2, "WRow / WCol"));

		add(createRow(0, new int[]{10, 90}));
		add(createRow(0, new int[]{20, 80}));
		add(createRow(0, new int[]{30, 70}));
		add(createRow(0, new int[]{40, 60}));
		add(createRow(0, new int[]{50, 50}));
		add(createRow(0, new int[]{60, 40}));
		add(createRow(0, new int[]{70, 30}));
		add(createRow(0, new int[]{80, 20}));
		add(createRow(0, new int[]{90, 10}));
		add(createRow(0, new int[]{33, 33, 33}));
		add(createRow(0, new int[]{25, 25, 25, 25}));
		add(createRow(0, new int[]{20, 20, 20, 20, 20}));

		add(new WHeading(HeadingLevel.H2, "WRow / WCol with hgap=5"));
		add(createRow(5, new int[]{33, 33, 33}));
		add(createRow(5, new int[]{25, 25, 25, 25}));
		add(createRow(5, new int[]{20, 20, 20, 20, 20}));
		add(new WHeading(HeadingLevel.H2, "WRow / WCol undefined width"));
		add(createRow(0, new int[]{0, 0, 0}));
		addAppLevelCSSExample();
	}

	/**
	 * Creates a row containing columns with the given widths.
	 *
	 * @param hgap the horizontal gap between columns, in pixels.
	 * @param colWidths the percentage widths for each column.
	 * @return a WRow containing columns with the given widths.
	 */
	private WRow createRow(final int hgap, final int[] colWidths) {
		WRow row = new WRow(hgap);

		for (int i = 0; i < colWidths.length; i++) {
			WColumn col = new WColumn(colWidths[i]);
			WPanel box = new WPanel(WPanel.Type.BOX);
			box.add(new WText(colWidths[i] + "%"));
			col.add(box);
			row.add(col);
		}

		return row;
	}

	/**
	 * Build an example with undefined column widths then use application-level CSS and a htmlClass property to define the widths.
	 *
	 */
	private void addAppLevelCSSExample() {
		String htmlClass = "my_local_class";
		add(new WHeading(HeadingLevel.H2, "App defined widths"));
		add(new ExplanatoryText("This example shows the use of a htmlClass and app-specific CSS (in this case inline)"
				+ " to style the columns including responsive widths"
				+ " which kick in at 1000px and 900px"));

		WRow row = new WRow();
		row.setHtmlClass(htmlClass);
		add(row);

		WColumn col1 = new WColumn();
		String col1HtmlClass = "my_col1";
		col1.setHtmlClass(col1HtmlClass);
		col1.add(new ExplanatoryText("This is some text content in the first column."));
		row.add(col1);

		WColumn col2 = new WColumn();
		String col2HtmlClass = "my_col2";
		col2.setHtmlClass(col2HtmlClass);
		col2.add(new ExplanatoryText("Some content in column 2."));
		row.add(col2);

		WColumn col3 = new WColumn();
		col3.add(new ExplanatoryText("Some content in column 3."));
		row.add(col3);

		String columnClass = ".wc-column";
		String rowSelector = "." + htmlClass;
		String columnSelector =  rowSelector + " > " + columnClass; // .column is the local name of WColumn's XML element and is part of the client side API.
		String css = columnSelector + " {width: 20%; background-color: #f0f0f0; padding: 0.5em;}"
				+ columnSelector + " + " + columnClass + " {margin-left: 0.5em}"
 				+ columnSelector + "." + col2.getHtmlClass() + " {width: 60%;}"
				+ "@media only screen and (max-width: 1000px) {"  //when the screen goes below 1000px wide
				+ rowSelector + " {display: block;}"
				+ columnSelector + " {display: inline-block; box-sizing: border-box;}"
				+ columnSelector + " + " + columnClass + " {margin-left: 0}"
				+ columnSelector + "." + col1.getHtmlClass() + " {display: block; width: 100%; margin-bottom: 0.5em;} "
				+ columnSelector + " ~ " + columnClass + " {width: calc(50% - 0.25em); background-color: #f0f000}"
				+ "." + col2.getHtmlClass() + " {margin-right: 0.25em}"
				+ "." + col2.getHtmlClass() + " + " + columnClass + " {margin-left: 0.25em;}"
				+ "}\n@media only screen and (max-width: 900px) {"  //when the screen goes below 900px wide;
				+ columnSelector + " {width: 100% !important; margin-left: 0 !important; margin-right: 0 !important; background-color: #ff0 !important;}" //the importants are becauseI am lazy
				+ "." + col2.getHtmlClass() + " {margin-bottom: 0.5em;}\n}";

		WText cssText = new WText("<style type='text/css'>" + css + "</style>");
		cssText.setEncodeText(false);
		add(cssText);
	}
}
