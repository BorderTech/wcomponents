package com.github.bordertech.wcomponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WTree}.
 *
 * @author Jonathan Austin
 * @since 1.2.0
 */
public class WTree_Test extends AbstractWComponentTestCase {

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
		assertAccessorsCorrect(new WTree(), "treeModel", EmptyTreeItemModel.INSTANCE, new MyTestModel(), new MyTestModel());
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

	/**
	 * Test tree item model.
	 */
	private static class MyTestModel extends AbstractTreeItemModel {

		private static final List<MyBean> DATA = new ArrayList<>();

		static {
			// A - No Children
			MyBean bean = new MyBean("A");
			DATA.add(bean);

			// B - Has One Level of children
			bean = new MyBean("B");
			DATA.add(bean);
			bean.getChildren().add(new MyBean("B.1"));
			bean.getChildren().add(new MyBean("B.2"));

			// C - Has Three levels of children
			bean = new MyBean("C");
			DATA.add(bean);
			bean = new MyBean("C.1");
			bean.getChildren().add(bean);
			bean = new MyBean("C.1.1");
			bean.getChildren().add(bean);
			bean = new MyBean("C.1.1.1");
			bean.getChildren().add(bean);
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
			MyBean bean = DATA.get(row.get(0));
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
