package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel;
import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel.LevelDetails;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.examples.table.PersonBean.TravelDoc;
import com.github.bordertech.wcomponents.util.TableUtil;
import java.util.List;

/**
 * This example demonstrates a simple {@link WTable} that is bean bound and has a custom renderer for expandable rows.
 * <p>
 * Uses {@link SimpleBeanBoundTableModel} to handle the bean binding and define the expandable levels.
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SimpleExpandableContentTableExample extends WPanel {

	/**
	 * The table used in the example.
	 */
	private final WTable table = new WTable();

	/**
	 * Create example.
	 */
	public SimpleExpandableContentTableExample() {
		add(table);

		// Columns
		table.addColumn(new WTableColumn("First name", new WText()));
		table.addColumn(new WTableColumn("Last name", new WText()));
		table.addColumn(new WTableColumn("DOB", new WDateField()));

		// Expand mode
		table.setExpandMode(ExpandMode.DYNAMIC);

		// Use expand all controls
		table.setExpandAll(true);

		// Set expandable level and the custom renderer.
		LevelDetails level = new LevelDetails("documents", TravelDocPanel.class);

		// Setup model - Define bean properties for the columns and the expandable level
		SimpleBeanBoundTableModel model = new SimpleBeanBoundTableModel(
				new String[]{"firstName", "lastName",
					"dateOfBirth"}, level);

		table.setTableModel(model);
	}

	/**
	 * Override preparePaintComponent in order to set up the example data the first time that the example is accessed by
	 * each user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			// Set the data as the bean on the table
			table.setBean(ExampleDataUtil.createExampleData());
			setInitialised(true);
		}
	}

	/**
	 * An example component to display travel document details. Expects that the supplied bean is a {@link TravelDoc}.
	 */
	public static final class TravelDocPanel extends WBeanContainer {

		/**
		 * Creates a TravelDocPanel.
		 */
		public TravelDocPanel() {
			WHorizontalRule rule = new WHorizontalRule() {
				@Override
				public boolean isVisible() {
					List<Integer> index = TableUtil.getCurrentRowIndex(TravelDocPanel.this);
					// On expanded row, so check the index of the expanded level
					return index.get(1) > 0;
				}

			};
			add(rule);

			WText documentNumber = new WText();
			WText countryOfIssue = new WText();
			WText placeOfIssue = new WText();

			documentNumber.setBeanProperty("documentNumber");
			countryOfIssue.setBeanProperty("countryOfIssue");
			placeOfIssue.setBeanProperty("placeOfIssue");

			WDefinitionList list = new WDefinitionList(WDefinitionList.Type.COLUMN);
			add(list);

			list.addTerm("Document number", documentNumber);
			list.addTerm("Country of issue", countryOfIssue);
			list.addTerm("Place Of issue", placeOfIssue);
		}
	}

}
