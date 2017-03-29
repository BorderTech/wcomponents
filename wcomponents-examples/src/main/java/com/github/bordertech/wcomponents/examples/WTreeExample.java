package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.AbstractTreeItemModel;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.Size;
import com.github.bordertech.wcomponents.TreeItemIdNode;
import com.github.bordertech.wcomponents.TreeItemImage;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WColumn;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WRow;
import com.github.bordertech.wcomponents.WTextField;
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
	 * A check box to turn on AJAX trigger.
	 */
	private final WCheckBox cbAjaxTrigger = new WCheckBox();

	/**
	 * A WAjaxControl to show how to use WTree as a WAjaxControl trigger.
	 */
	private final WAjaxControl control = new WAjaxControl(tree);

	/**
	 * A check box to show documents instead of people.
	 */
	private final WCheckBox cbUseDocuments = new WCheckBox();

	/**
	 * A check box to use custom tree.
	 */
	private final WCheckBox cbCustomTree = new WCheckBox();

	/**
	 * A check box to use custom image.
	 */
	private final WCheckBox cbUseImage = new WCheckBox();

	/**
	 * Construct the example.
	 */
	public WTreeExample() {

		WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
		add(layout);
		layout.setMargin(new Margin(Size.ZERO, Size.ZERO, Size.LARGE, Size.ZERO));
		layout.addField("Use HTree", cbMakeHTree);
		layout.addField("Enable multiple selection", cbUseMultiSelect);
		layout.addField("Enable ajax control", cbAjaxTrigger);
		layout.addField("Use documents", cbUseDocuments);
		layout.addField("Use custom image", cbUseImage);
		layout.addField("Custom tree", cbCustomTree);

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

		WRow row = new WRow();
		add(row);
		WColumn treeColumn = new WColumn(50);
		WColumn targetColumn = new WColumn(50);
		row.add(treeColumn);
		row.add(targetColumn);

		tree.setIdName("tree");
		treeColumn.add(tree);

		final WTextField target = new WTextField();
		target.setReadOnly(true);
		targetColumn.add(new WHeading(HeadingLevel.H2, "Selected rows."));
		targetColumn.add(target);

		control.addTarget(target);
		control.setVisible(false); // Ajax control not enabled by default. See applyOptions.
		add(control);

		tree.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				target.setText(tree.getSelectedRows().toString());
			}
		});
	}

	/**
	 * Set options for the WTree based on user input in the options field(s).
	 */
	private void applyOptions() {
		tree.reset();
		tree.setType(cbMakeHTree.isSelected() ? WTree.Type.HORIZONTAL : WTree.Type.VERTICAL);
		tree.setSelectMode(cbUseMultiSelect.isSelected() ? WTree.SelectMode.MULTIPLE : WTree.SelectMode.SINGLE);
		tree.setExpandMode((WTree.ExpandMode) ddExpMode.getSelected());

		control.setVisible(cbAjaxTrigger.isSelected());

		// This model holds the data so would be included on the user session.
		ExampleTreeModel data = new ExampleTreeModel(ExampleDataUtil.
				createExampleData(), cbUseDocuments.isSelected(), cbUseImage.isSelected());
		tree.setTreeModel(data);
		if (cbCustomTree.isSelected()) {
			TreeItemIdNode custom = new TreeItemIdNode(null);
			if (cbUseDocuments.isSelected()) {
				// Put all documents under Tom Smith (ID16)
				TreeItemIdNode node = new TreeItemIdNode("ID16");
				node.addChild(new TreeItemIdNode("11122"));
				node.addChild(new TreeItemIdNode("23456"));
				node.addChild(new TreeItemIdNode("78901"));
				node.addChild(new TreeItemIdNode("23457"));

				// Put 3 people in the custom tree
				custom.addChild(new TreeItemIdNode("ID4"));
				custom.addChild(node);
				custom.addChild(new TreeItemIdNode("ID1"));
			} else {
				// Put people under Tom SMith

				TreeItemIdNode itemID2 = new TreeItemIdNode("ID2");
				itemID2.setHasChildren(true);

				TreeItemIdNode itemID1 = new TreeItemIdNode("ID1");
				itemID1.setHasChildren(true);

				TreeItemIdNode node = new TreeItemIdNode("ID16");
				node.addChild(itemID2);
				node.addChild(new TreeItemIdNode("ID3"));
				node.addChild(new TreeItemIdNode("ID6"));
				node.addChild(new TreeItemIdNode("ID5"));

				// Take some nodes from other nodes
				node.addChild(new TreeItemIdNode("2A"));
				node.addChild(new TreeItemIdNode("1B1"));

				// Put 3 people at top level
				custom.addChild(new TreeItemIdNode("ID4"));
				custom.addChild(node);
				custom.addChild(itemID1);
			}
			tree.setCustomTree(custom);
		}

	}

	/**
	 * Example tree model.
	 */
	public static class ExampleTreeModel extends AbstractTreeItemModel {

		private static final TreeItemImage PDF_IMAGE = new TreeItemImage(new ImageResource("/image/attachment.png"));

		private static final TreeItemImage PERSON_IMAGE = new TreeItemImage(new ImageResource("/image/user.png"));

		/**
		 * List that holds the sample data.
		 */
		private final List<PersonBean> data;

		/**
		 * Flag if expand docs.
		 */
		private final boolean useDocs;

		/**
		 * Flag if use custom image.
		 */
		private final boolean useImage;

		/**
		 * @param data the sample data
		 * @param useDocs use documents in the expand level
		 * @param useImage use image in each node
		 */
		public ExampleTreeModel(final List<PersonBean> data, final boolean useDocs, final boolean useImage) {
			this.data = data;
			this.useDocs = useDocs;
			this.useImage = useImage;
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

			if (useDocs) {
				if (row.size() == 1) {
					// Top Level (Name)
					PersonBean bean = getRootPerson(row);
					return bean.getFirstName() + " " + bean.getLastName();
				} else {
					// Expandable Level (Document)
					PersonBean.TravelDoc doc = getDocument(row);
					return doc.getDocumentNumber();
				}
			} else {
				// Person Name
				PersonBean bean = getPerson(row);
				return bean.getFirstName() + " " + bean.getLastName();
			}
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
			if (useDocs) {
				// Top Level - check if level has has documents
				if (row.size() == 1) {
					PersonBean bean = getPerson(row);
					return bean.getDocuments() == null ? 0 : bean.getDocuments().size();
				}
				return 0;
			} else {
				// Check has "more" persons
				PersonBean bean = getPerson(row);
				return bean.getMore() == null ? 0 : bean.getMore().size();
			}
		}

		@Override
		public String getItemId(final List<Integer> row) {
			if (useDocs) {
				if (row.size() == 1) {
					PersonBean bean = getRootPerson(row);
					return bean.getPersonId();
				} else {
					PersonBean.TravelDoc doc = getDocument(row);
					return doc.getDocumentNumber();
				}
			} else {
				return getPerson(row).getPersonId();
			}
		}

		@Override
		public TreeItemImage getItemImage(final List<Integer> row) {
			if (!this.useImage) {
				return null;
			}
			if (useDocs && row.size() == 2) {
				return PDF_IMAGE;
			}
			return PERSON_IMAGE;
		}

		/**
		 * @param row the row index for a person
		 * @return the person bean
		 */
		private PersonBean getPerson(final List<Integer> row) {
			// Get root person
			PersonBean person = getRootPerson(row);
			for (int i = 1; i < row.size(); i++) {
				int idx = row.get(i);
				person = person.getMore().get(idx);
			}
			return person;
		}

		/**
		 * @param row the row index
		 * @return the top level person bean
		 */
		private PersonBean getRootPerson(final List<Integer> row) {
			int idx = row.get(0);
			PersonBean person = data.get(idx);
			return person;
		}

		/**
		 * @param row the row index for a document
		 * @return the document bean
		 */
		private PersonBean.TravelDoc getDocument(final List<Integer> row) {
			PersonBean bean = getRootPerson(row);
			int docIdx = row.get(1);
			PersonBean.TravelDoc doc = bean.getDocuments().get(docIdx);
			return doc;
		}

	}

}
