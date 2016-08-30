package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Mock data for testing {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.2.5
 */
public class MockTreeItemData {

	public static final String TEST_MULTI_JSON = "{\"root\":[{\"id\":\"A\"},{\"id\":\"B\",\"items\":[{\"id\":\"B.1\",\"items\":[{\"id\":\"B.1.1\"}]},{\"id\":\"B.2\"}]},{\"id\":\"C\"}]}";

	public static final TreeItemIdNode TEST_MULTI_TREE = createTreeMulti();

	public static final String TEST_BASIC_JSON = "{\"root\":[{\"id\":\"A\"}]}";

	public static final TreeItemIdNode TEST_BASIC_TREE = createTreeBasic();

	public static final List<MyBean> DATA = new ArrayList<>();
	public static final MyBean BEAN_A = new MyBean("A");
	public static final MyBean BEAN_B = new MyBean("B");
	public static final MyBean BEAN_B_1 = new MyBean("B_1");
	public static final MyBean BEAN_B_2 = new MyBean("B_2");
	public static final MyBean BEAN_C = new MyBean("C");
	public static final MyBean BEAN_C_1 = new MyBean("C_1");
	public static final MyBean BEAN_C_1_1 = new MyBean("C_1_1");
	public static final MyBean BEAN_C_1_1_1 = new MyBean("C_1_1_1");

	public static final Set<String> SELECTED_B_C_1;
	public static final Set<String> SELECTED_B_2;

	static {
		// A - No Children
		DATA.add(BEAN_A);

		// B - Has One Level of children
		DATA.add(BEAN_B);
		BEAN_B.getChildren().add(BEAN_B_1);
		BEAN_B.getChildren().add(BEAN_B_2);

		// C - Has Three levels of children
		DATA.add(BEAN_C);
		BEAN_C.getChildren().add(BEAN_C_1);
		BEAN_C_1.getChildren().add(BEAN_C_1_1);
		BEAN_C_1_1.getChildren().add(BEAN_C_1_1_1);

		// Selected
		Set<String> sel = new HashSet<>();
		sel.add(BEAN_B.getId());
		sel.add(BEAN_C_1.getId());
		SELECTED_B_C_1 = Collections.unmodifiableSet(sel);

		// Selected
		sel = new HashSet<>();
		sel.add(BEAN_B_2.getId());
		SELECTED_B_2 = Collections.unmodifiableSet(sel);

	}

	/**
	 * @return a tree with multiple levels
	 */
	public static TreeItemIdNode createTreeMulti() {

		TreeItemIdNode root = new TreeItemIdNode(null);

		// A
		root.addChild(new TreeItemIdNode("A"));

		// B
		TreeItemIdNode nodeB = new TreeItemIdNode("B");
		root.addChild(nodeB);
		// B.1
		TreeItemIdNode nodeB1 = new TreeItemIdNode("B_1");
		nodeB.addChild(nodeB1);
		// B.1.1
		nodeB1.addChild(new TreeItemIdNode("B_1_1"));
		// B.2
		nodeB.addChild(new TreeItemIdNode("B_2"));

		// C
		root.addChild(new TreeItemIdNode("C"));

		return root;
	}

	/**
	 *
	 * @return a basic tree with one level
	 */
	public static TreeItemIdNode createTreeBasic() {
		TreeItemIdNode root = new TreeItemIdNode(null);
		// A
		root.addChild(new TreeItemIdNode("A"));
		return root;
	}

	public static WTree setupTree() {
		WTree tree = new WTree();
		tree.setTreeModel(new MockTreeItemData.MyTestModel(MockTreeItemData.DATA));
		return tree;
	}

	/**
	 * Test tree item model.
	 */
	public static class MyTestModel extends AbstractTreeItemModel {

		private final List<MyBean> data;

		public MyTestModel(final List<MyBean> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemId(final List<Integer> row) {
			// Use the label as the key
			return getRowBean(row).getId();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getItemLabel(final List<Integer> row) {
			return getRowBean(row).getName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getRowCount() {
			return data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildCount(final List<Integer> row) {
			return getRowBean(row).getChildren().size();
		}

		@Override
		public TreeItemImage getItemImage(final List<Integer> row) {
			MyBean bean = getRowBean(row);
			if (bean.getId().equals("A")) {
				return new TreeItemImage("URL-A");
			}
			return null;
		}

		/**
		 *
		 * @param row the row index
		 * @return the bean for the row
		 */
		private MyBean getRowBean(final List<Integer> row) {
			// Top level bean
			MyBean bean = data.get(row.get(0));
			if (row.size() > 1) {
				for (Integer rowIdx : row.subList(1, row.size())) {
					bean = bean.getChildren().get(rowIdx);
				}
			}
			return bean;
		}
	}

	/**
	 * Test bean.
	 */
	public static class MyBean implements Serializable {

		private final String id;
		private final String name;
		private final String url;
		private final List<MyBean> children = new ArrayList<>();

		/**
		 * @param name the label name
		 */
		public MyBean(final String name) {
			this(name, name);
		}

		/**
		 * @param name the label name
		 * @param id the item id
		 */
		public MyBean(final String name, final String id) {
			this(name, id, null);
		}

		/**
		 *
		 * @param name the label name
		 * @param id the item id
		 * @param url the image URL
		 */
		public MyBean(final String name, final String id, final String url) {
			this.name = name;
			this.id = id;
			this.url = url;
		}

		/**
		 * @return the item id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the label name
		 */
		public String getName() {
			return name;
		}

		/**
		 *
		 * @return the image URL or null
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @return the list of child beans
		 */
		public List<MyBean> getChildren() {
			return children;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 97 * hash + Objects.hashCode(this.id);
			return hash;
		}

		@Override
		public boolean equals(final Object obj) {
			return (obj instanceof MyBean && Util.equals(this.id, ((MyBean) obj).getId()));
		}

	}

}
