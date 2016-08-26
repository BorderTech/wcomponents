package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.2.0
 */
public class WTree_Test extends AbstractWComponentTestCase {

	private static final List<MyBean> DATA = new ArrayList<>();
	private static final MyBean BEAN_A = new MyBean("A");
	private static final MyBean BEAN_B = new MyBean("B");
	private static final MyBean BEAN_B_1 = new MyBean("B.1");
	private static final MyBean BEAN_B_2 = new MyBean("B.2");
	private static final MyBean BEAN_C = new MyBean("C");
	private static final MyBean BEAN_C_1 = new MyBean("C.1");
	private static final MyBean BEAN_C_1_1 = new MyBean("C.1.1");
	private static final MyBean BEAN_C_1_1_1 = new MyBean("C.1.1.1");

	private static final Set<String> SELECTED_B_C_1;
	private static final Set<String> SELECTED_B_2;

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
		sel.add(BEAN_B.getName());
		sel.add(BEAN_C_1.getName());
		SELECTED_B_C_1 = Collections.unmodifiableSet(sel);

		// Selected
		sel = new HashSet<>();
		sel.add(BEAN_B_2.getName());
		SELECTED_B_2 = Collections.unmodifiableSet(sel);

	}

	@Test
	public void testConstructorDefault() {
		WTree tree = new WTree();
		Assert.assertEquals("Default Const - type", WTree.Type.VERTICAL, tree.getType());
	}

	@Test
	public void testConstructor1() {
		WTree tree = new WTree(WTree.Type.HORIZONTAL);
		Assert.assertEquals("Constructor1 - type", WTree.Type.HORIZONTAL, tree.getType());
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WTree(), "type", WTree.Type.VERTICAL, WTree.Type.HORIZONTAL, WTree.Type.VERTICAL);
	}

	@Test
	public void testTypeNull() {
		WTree tree = new WTree();
		tree.setType(null);
		Assert.assertEquals("Setting a null type should default to Vertical", WTree.Type.VERTICAL, tree.getType());
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WTree(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testTreeModelAccessors() {
		assertAccessorsCorrect(new WTree(), "treeModel", EmptyTreeItemModel.INSTANCE, new MyTestModel(Collections.EMPTY_LIST), new MyTestModel(Collections.EMPTY_LIST));
	}

	@Test
	public void testOpenActionAccessors() {
		assertAccessorsCorrect(new WTree(), "openAction", null, new TestAction(), new TestAction());
	}

	@Test
	public void testShuffleAccessors() {
		assertAccessorsCorrect(new WTree(), "shuffle", false, true, false);
	}

	@Test
	public void testShuffleActionAccessors() {
		assertAccessorsCorrect(new WTree(), "shuffleAction", null, new TestAction(), new TestAction());
	}

	@Test
	public void testSelectModeAccessors() {
		assertAccessorsCorrect(new WTree(), "selectMode", WTree.SelectMode.SINGLE, WTree.SelectMode.MULTIPLE, WTree.SelectMode.SINGLE);
	}

	@Test
	public void testSelectModeNull() {
		WTree tree = new WTree();
		tree.setSelectMode(null);
		Assert.assertEquals("Setting a null select mode should default to Single", WTree.SelectMode.SINGLE, tree.getSelectMode());
	}

	@Test
	public void testExpandModeAccessors() {
		assertAccessorsCorrect(new WTree(), "expandMode", WTree.ExpandMode.CLIENT, WTree.ExpandMode.DYNAMIC, WTree.ExpandMode.LAZY);
	}

	@Test
	public void testExpandModeNull() {
		WTree tree = new WTree();
		tree.setExpandMode(null);
		Assert.assertEquals("Setting a null expand mode should default to Client", WTree.ExpandMode.CLIENT, tree.getExpandMode());
	}

	@Test
	public void testSelectedRowsAccessors() {
		assertAccessorsCorrect(new WTree(), "selectedRows", Collections.EMPTY_SET, new HashSet<>(Arrays.asList("A", "B")), new HashSet<>(Arrays.asList("X", "Y")));
	}

	@Test
	public void testExpandedRowsAccessors() {
		assertAccessorsCorrect(new WTree(), "expandedRows", Collections.EMPTY_SET, new HashSet<>(Arrays.asList("A", "B")), new HashSet<>(Arrays.asList("X", "Y")));
	}

	@Test
	public void testGetValueAsString() {

		WTree tree = new WTree();
		Assert.assertNull("Value as String should be null by default", tree.getValueAsString());

		// Null
		tree.setSelectedRows(null);
		Assert.assertNull("Value as String should be null for null selected", tree.getValueAsString());

		// Empty
		tree.setSelectedRows(Collections.EMPTY_SET);
		Assert.assertNull("Value as String should be null for empty", tree.getValueAsString());

		// Selected
		tree.setSelectedRows(SELECTED_B_2);
		Assert.assertEquals("Value as String should be BEAN_B_2", BEAN_B_2.getName(), tree.getValueAsString());
	}

	@Test
	public void testDoHandleRequestNothingSelected() {
		WTree tree = setupTree();
		MockRequest request = setupRequest(tree);
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", Collections.EMPTY_SET, tree.getSelectedRows());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testDoHandleRequestWithValidOption() {
		WTree tree = setupTree();
		MockRequest request = setupRequest(tree, BEAN_B_2.getName());
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have option BEAN_B_2 selected", SELECTED_B_2, tree.getSelectedRows());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithInvalidOption() {
		WTree tree = setupTree();
		tree.setSelectedRows(SELECTED_B_C_1);
		MockRequest request = setupRequest(tree, "XX");
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have no option selected", Collections.EMPTY_SET, tree.getSelectedRows());
		Assert.assertTrue("doHandleRequest should have returned true", changed);
	}

	@Test
	public void testDoHandleRequestWithSameOption() {
		WTree tree = setupTree();
		tree.setSelectedRows(SELECTED_B_2);
		MockRequest request = setupRequest(tree, BEAN_B_2.getName());
		boolean changed = tree.doHandleRequest(request);
		Assert.assertEquals("Should have option BEAN_B_2 selected", SELECTED_B_2, tree.getSelectedRows());
		Assert.assertFalse("doHandleRequest should have returned false", changed);
	}

	@Test
	public void testGetRequestValue() {

		WTree tree = setupTree();
		tree.setSelectedRows(SELECTED_B_C_1);

		// Empty Request, should return current selected
		MockRequest request = new MockRequest();
		Assert.assertEquals("Should return the current selected option for an empty request", SELECTED_B_C_1, tree.getRequestValue(request));

		// OptionB_2 on the Request
		request = setupRequest(tree, BEAN_B_2.getName());
		Assert.assertEquals("getRequestValue should return the option on the request", SELECTED_B_2,
				tree.getRequestValue(request));
	}

	private WTree setupTree() {
		WTree tree = new WTree();
		tree.setTreeModel(new MyTestModel(DATA));
		return tree;
	}

	private MockRequest setupRequest(final WTree tree, final String... options) {
		MockRequest request = new MockRequest();
		request.setParameter(tree.getId() + "-h", "x");
		if (options != null) {
			int i = 0;
			String prefix = tree.getItemIdPrefix();
			for (String opt : options) {
				options[i++] = prefix + opt;
			}
			request.setParameter(tree.getId(), options);
		}
		return request;
	}

	/**
	 * Test tree item model.
	 */
	private static class MyTestModel extends AbstractTreeItemModel {

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
			return getRowBean(row).getName();
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
			return DATA.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildCount(final List<Integer> row) {
			return getRowBean(row).getChildren().size();
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
				for (Integer rowIdx : row.subList(1, row.size() - 1)) {
					bean = bean.getChildren().get(rowIdx);
				}
			}
			return bean;
		}
	}

	/**
	 * Test bean.
	 */
	private static class MyBean implements Serializable {

		private final String name;
		private final String url;
		private final List<MyBean> children = new ArrayList<>();

		/**
		 * @param name the label name
		 */
		public MyBean(final String name) {
			this(name, null);
		}

		/**
		 *
		 * @param name the label name
		 * @param url the image URL
		 */
		public MyBean(final String name, final String url) {
			this.name = name;
			this.url = url;
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

	}

}
