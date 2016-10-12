package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.AbstractTreeItemModel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WTree;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.TreeItemUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * WTree based example selection tool. For internal use only.
 *
 * @author Mark Reeves
 * @since 1.2.3
 */
public class ExamplePickerTree extends WTree {

	/**
	 * The data held in this WTree.
	 */
	private final List<ExampleMenuList> data = new ArrayList<>();

	/**
	 * Create the example selection tree.
	 */
	public ExamplePickerTree() {
		super();
		setUp();
	}

	/**
	 * Add the examples as data in the tree.
	 */
	private void setUp() {
		addExamples("AJAX", ExampleData.AJAX_EXAMPLES);
		addExamples("Form controls", ExampleData.FORM_CONTROLS);
		addExamples("Feedback and indicators", ExampleData.FEEDBACK_AND_INDICATORS);
		addExamples("Layout", ExampleData.LAYOUT_EXAMPLES);
		addExamples("Menus", ExampleData.MENU_EXAMPLES);
		addExamples("Links", ExampleData.LINK_EXAMPLES);
		addExamples("Popups / dialogs", ExampleData.POPUP_EXAMPLES);
		addExamples("Subordinate", ExampleData.SUBORDINATE_EXAMPLES);
		addExamples("Tabs", ExampleData.TABSET_EXAMPLES);
		addExamples("Tables", ExampleData.WTABLE_EXAMPLES);
		addExamples("Validation", ExampleData.VALIDATION_EXAMPLES);
		addExamples("Other examples (uncategorised)", ExampleData.MISC_EXAMPLES);
		addExamples("DataTable (deprecated)", ExampleData.WDATATABLE_EXAMPLES);
	}

	/**
	 * @return The tree model for type safety.
	 */
	@Override
	public MenuTreeModel getTreeModel() {
		return (MenuTreeModel) super.getTreeModel();
	}

	/**
	 * Add a set of examples to the WTree.
	 *
	 * @param groupName The name of the example group.
	 * @param entries An array of examples in this group.
	 */
	public void addExamples(final String groupName, final ExampleData[] entries) {
		data.add(new ExampleMenuList(groupName, entries));
	}

	/**
	 * Get the example which is selected in the tree.
	 *
	 * @return an example data object.
	 */
	public final ExampleData getSelectedExampleData() {
		Set<String> allSelectedItems = getSelectedRows();
		if (allSelectedItems == null || allSelectedItems.isEmpty()) {
			return null;
		}
		for (String selectedItem : allSelectedItems) {
			// Only interested in the first selected item as it is a single select list.
			List<Integer> rowIndex = TreeItemUtil.rowIndexStringToList(selectedItem);
			return getTreeModel().getExampleData(rowIndex);
		}
		return null;
	}

	/**
	 * Set the tree model on first use.
	 *
	 * @param request The request.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			setInitialised(true);
			setTreeModel(new MenuTreeModel(data));
		}
	}

	/**
	 * The Model for this WTree.
	 */
	public static final class MenuTreeModel extends AbstractTreeItemModel {

		/**
		 * The model data.
		 */
		private final List<ExampleMenuList> data;

		/**
		 * Create a tree model.
		 *
		 * @param data the data to be held on this model.
		 */
		public MenuTreeModel(final List<ExampleMenuList> data) {
			this.data = data;
		}

		/**
		 * @return the model's data.
		 */
		public List<ExampleMenuList> getData() {
			return data;
		}

		/**
		 * Get the data for a given row.
		 *
		 * @param row The row in the data list we are interested in.
		 * @return the data entry for row.
		 */
		public ExampleData getExampleData(final List<Integer> row) {
			int rootIdx = row.get(0);
			ExampleMenuList listItem = getData().get(rootIdx);
			if (row.size() == 1) {
				return listItem.getDefaultExample();
			}
			if (row.size() == 2) {
				if (listItem != null) {
					return listItem.getExampleData(row.get(1));
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemLabel(final List<Integer> row) {
			int rootIdx = row.get(0);

			ExampleMenuList listItem = getData().get(rootIdx);
			if (row.size() == 1) {
				return listItem.getListName();
			}
			if (row.size() == 2) {
				int exampleIdx = row.get(1);
				ExampleData ex = listItem.getExampleData(exampleIdx);
				return ex.getExampleName();
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return getData().size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildCount(final List<Integer> row) {
			if (row.size() == 1) {
				ExampleMenuList list = data.get(row.get(0));
				return list == null ? 0 : list.getData().size();
			}
			return 0;
		}
	}

	/**
	 * The data object used by the WTree.
	 */
	public class ExampleMenuList {

		/**
		 * The name of the list of examples (the 'group').
		 */
		private final String listName;

		/**
		 * The list of examples for this item.
		 */
		private final List<ExampleData> data;

		/**
		 * Create an example menu list.
		 *
		 * @param listName the readable(group) name for the list
		 * @param entries the examples in this group
		 */
		public ExampleMenuList(final String listName, final ExampleData[] entries) {
			this.listName = listName;
			this.data = Arrays.asList(entries);
		}

		/**
		 * @return the readable name for the list.
		 */
		public String getListName() {
			return this.listName;
		}

		/**
		 * @return the data in this list.
		 */
		public List<ExampleData> getData() {
			return data;
		}

		/**
		 * Get the data for a particular row.
		 *
		 * @param row the row index
		 * @return the data for the row
		 */
		public ExampleData getExampleData(final int row) {
			if (data == null) {
				return null;
			}
			if (row < 0 || row >= data.size()) {
				throw new SystemException("Item out of bounds");
			}
			return data.get(row);
		}

		/**
		 * Get the default example for a group when a group node is selected. Could return null and do nothing.
		 *
		 * @return the first example in a list
		 */
		public ExampleData getDefaultExample() {
			// return getExampleData(0);
			return null;
		}
	}
}
