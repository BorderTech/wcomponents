package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WContainer;
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
	 * Construct the example.
	 */
	public WTreeExample() {
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
	 * Example tree model.
	 */
	public static class ExampleTreeModel extends WTree.AbstractTreeModel {

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
