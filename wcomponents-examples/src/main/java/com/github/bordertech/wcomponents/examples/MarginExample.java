package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Marginable;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.WMenu;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPanel.Type;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WTabSet.TabMode;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import java.util.Arrays;

/**
 * Demonstrate setting a margin on {@link Marginable} components.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class MarginExample extends WContainer {

	/**
	 * Default box margin.
	 */
	private static final Margin DEFAULT_BOX_MARGIN = new Margin(0, 0, 6, 0);

	/**
	 * Default example margin.
	 */
	private static final Margin DEFAULT_MARGIN = new Margin(24);

	/**
	 * Build example.
	 */
	public MarginExample() {
		WPanel root = new WPanel();
		add(root);
		root.setMargin(new Margin(12));

		// WSection
		WPanel box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);

		WSection section = new WSection("Section with margin of all 24");
		section.setMargin(DEFAULT_MARGIN);
		section.getContent().add(new WText("Section content"));
		box.add(section);

		// WCollapsible
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WCollapsible collapsible = new WCollapsible(new WText("Collapsible content"),
				"Collapsible with margin of all 24");
		collapsible.setMargin(DEFAULT_MARGIN);
		box.add(collapsible);

		// WTable
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WTable table = new WTable();
		table.setCaption("WTable with margin of all 24");
		table.setMargin(DEFAULT_MARGIN);
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));
		box.add(table);

		// WFieldLayout
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WFieldLayout fieldLayout = new WFieldLayout();
		fieldLayout.setMargin(DEFAULT_MARGIN);
		fieldLayout.addField("Field layout with margin of all 24", new WTextField());
		box.add(fieldLayout);

		// WRow
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WRow row = new WRow(6);
		row.setMargin(DEFAULT_MARGIN);
		box.add(row);
		WColumn col = new WColumn(100);
		col.add(new WText("WRow with margin of all 24"));
		row.add(col);

		// WFieldset
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WFieldSet fieldSet = new WFieldSet("FieldSet with margin of all 24");
		fieldSet.setMargin(DEFAULT_MARGIN);
		fieldSet.add(new WText("content"));
		box.add(fieldSet);

		// WDefintionList
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WDefinitionList defList = new WDefinitionList();
		defList.setMargin(DEFAULT_MARGIN);
		defList.addTerm("Term", new WText("Definition list with a margin of all 24"));
		box.add(defList);

		// WList
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WList list = new WList(WList.Type.FLAT);
		list.setMargin(DEFAULT_MARGIN);
		list.setRepeatedComponent(new WText());
		list.setBeanList(Arrays.asList(new String[]{"A", "B", "C"}));
		box.add(list);

		// WTabSet
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WTabSet tabSet = new WTabSet();
		tabSet.setMargin(DEFAULT_MARGIN);
		tabSet.addTab(new WText("Content1"), "Tab1", TabMode.CLIENT);
		tabSet.addTab(new WText("Content2"), "Tab2", TabMode.CLIENT);
		box.add(tabSet);

		// WMenu
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WMenu menu = new WMenu();
		menu.setMargin(DEFAULT_MARGIN);
		menu.add(new WMenuItem("Item1"));
		box.add(menu);

		// WHeading
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WHeading heading = new WHeading(WHeading.MAJOR, "Heading with margin of all 24");
		heading.setMargin(DEFAULT_MARGIN);
		box.add(heading);

		// WPanel
		box = new WPanel(Type.BOX);
		box.setMargin(DEFAULT_BOX_MARGIN);
		root.add(box);
		WPanel panel = new WPanel(Type.BOX);
		panel.setMargin(DEFAULT_MARGIN);
		panel.add(new WText("Panel with margin of all 24"));
		box.add(panel);
	}

}
