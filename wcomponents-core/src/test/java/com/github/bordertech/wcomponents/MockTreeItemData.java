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
public final class MockTreeItemData {

	/**
	 * Used for testing.
	 */
	public static final String TEST_MULTI_JSON = "{\"root\":[{\"id\":\"A\"},{\"id\":\"C\",\"items\":[{\"id\":\"C.1\",\"items\":[{\"id\":\"B\",\"expandable\":true}]}]},{\"id\":\"C.1.1\",\"expandable\":true}]}";

	/**
	 * Used for testing.
	 */
	public static final TreeItemIdNode TEST_MULTI_TREE = createTreeNodeMulti();

	/**
	 * Used for testing.
	 */
	public static final String TEST_BASIC_JSON = "{\"root\":[{\"id\":\"A\"}]}";
	/**
	 * Used for testing.
	 */
	public static final TreeItemIdNode TEST_BASIC_TREE = createTreeNodeBasic();
	/**
	 * Used for testing.
	 */
	public static final List<MyBean> DATA = new ArrayList<>();
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_A = new MyBean("A");

	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_B = new MyBean("B");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_B_1 = new MyBean("B.1");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_B_2 = new MyBean("B.2");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_C = new MyBean("C");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_C_1 = new MyBean("C.1");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_C_1_1 = new MyBean("C.1.1");
	/**
	 * Used for testing.
	 */
	public static final MyBean BEAN_C_1_1_1 = new MyBean("C.1.1.1");
	/**
	 * Used for testing.
	 */
	public static final Set<String> SELECTED_B_C_1;
	/**
	 * Used for testing.
	 */
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
	 * Prevent instantiation.
	 */
	private MockTreeItemData() {
	}

	/**
	 * @return a tree with multiple levels
	 */
	private static TreeItemIdNode createTreeNodeMulti() {

		TreeItemIdNode root = new TreeItemIdNode(null);

		// A
		// C
		// - C.1
		//   - B (hasChildren)
		// C.1.1 (hasChildren)
		TreeItemIdNode nodeB = new TreeItemIdNode("B");
		TreeItemIdNode nodeC = new TreeItemIdNode("C");
		TreeItemIdNode nodeC1 = new TreeItemIdNode("C.1");
		TreeItemIdNode nodeC11 = new TreeItemIdNode("C.1.1");

		nodeB.setHasChildren(true);
		nodeC11.setHasChildren(true);

		root.addChild(new TreeItemIdNode("A"));
		root.addChild(nodeC);
		nodeC.addChild(nodeC1);
		nodeC1.addChild(nodeB);
		root.addChild(nodeC11);

		return root;
	}

	/**
	 *
	 * @return a basic tree with one level
	 */
	private static TreeItemIdNode createTreeNodeBasic() {
		TreeItemIdNode root = new TreeItemIdNode(null);
		// A
		root.addChild(new TreeItemIdNode("A"));
		return root;
	}

	/**
	 *
	 * @return a WTree setup with the mock tree item model
	 */
	public static WTree setupWTree() {
		WTree tree = new WTree();
		tree.setTreeModel(new MockTreeItemData.MyTestModel(MockTreeItemData.DATA));
		return tree;
	}

	/**
	 *
	 * @return a WTree setup with the mock tree item model
	 */
	public static WTree setupWTreeWithCustom() {
		WTree tree = new WTree();
		tree.setTreeModel(new MockTreeItemData.MyTestModel(MockTreeItemData.DATA));
		tree.setCustomTree(createTreeNodeMulti());
		return tree;
	}

	/**
	 * Test tree item model.
	 */
	public static class MyTestModel extends AbstractTreeItemModel {

		private final List<MyBean> data;

		/**
		 * @param data the mock data for the model
		 */
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
