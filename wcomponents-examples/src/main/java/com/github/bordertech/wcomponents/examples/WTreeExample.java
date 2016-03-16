package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.AbstractTreeItemModel;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WTree;
import com.github.bordertech.wcomponents.examples.table.ExampleDataUtil;
import com.github.bordertech.wcomponents.examples.table.PersonBean;
import java.util.List;

/**
 * An example of how to use {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.1.0
 */
public class WTreeExample extends WContainer {

	/**
	 * The tree component.
	 */
	private final WTree tree = new WTree();

	/**
	 * A check box to set the tree to HORIZONTAL.
	 */
	private final WCheckBox cbMakeHTree = new WCheckBox();

	/**
	 * A check box to set multiple selection mode on the tree.
	 */
	private final WCheckBox cbUseMultiSelect = new WCheckBox();

	/**
	 * A selector for expand mode.
	 */
	private final WDropdown ddExpMode = new WDropdown();

	/**
	 * Construct the example.
	 */
	public WTreeExample() {
		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.setMargin(new Margin(0, 0, 12, 0));
		layout.addField("Use HTree", cbMakeHTree);
		layout.addField("Enable multiple selection", cbUseMultiSelect);

		ddExpMode.setOptions(WTree.ExpandMode.values());
		ddExpMode.setSelected(WTree.ExpandMode.CLIENT);
		layout.addField("Expand mode", ddExpMode);

		WButton btnOptions = new WButton("Apply");
		btnOptions.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				applyOptions();
			}
		});
		layout.addField((WLabel) null, btnOptions);

		tree.setIdName("tree");
		add(tree);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);
		if (!isInitialised()) {
			// This model holds the data so would be included on the user session.
			ExampleTreeModel data = new ExampleTreeModel(ExampleDataUtil.
					createExampleData());
			tree.setTreeModel(data);
			setInitialised(true);
		}
	}

	/**
	 * Set options for the WTree based on user input in the options field(s).
	 */
	private void applyOptions() {
		tree.setType(cbMakeHTree.isSelected() ? WTree.Type.HORIZONTAL : WTree.Type.VERTICAL);
		tree.setSelectMode(cbUseMultiSelect.isSelected() ? WTree.SelectMode.MULTIPLE : WTree.SelectMode.SINGLE);
		tree.setExpandMode((WTree.ExpandMode) ddExpMode.getSelected());
	}

	/**
	 * Example tree model.
	 */
	public static class ExampleTreeModel extends AbstractTreeItemModel {

		/**
		 * List that holds the sample data.
		 */
		private final List<PersonBean> data;

		/**
		 * @param data the sample data
		 */
		public ExampleTreeModel(final List<PersonBean> data) {
			this.data = data;
		}

		/**
		 * @return the sample data.
		 */
		public List<PersonBean> getData() {
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemLabel(final List<Integer> row) {
			int rootIdx = row.get(0);
			// Top Level
			PersonBean bean = getData().get(rootIdx);
			if (row.size() == 1) {
				return bean.getFirstName();
			} else if (row.size() == 2) {  // Expandable Level
				int docIdx = row.get(1);
				PersonBean.TravelDoc doc = bean.getDocuments().get(docIdx);
				return doc.getDocumentNumber();
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
			// Top Level - check if level has children (ie has documents)
			if (row.size() == 1) {
				PersonBean bean = data.get(row.get(0));
				return bean.getDocuments() == null ? 0 : bean.getDocuments().size();
			}
			return 0;
		}

	}

}
