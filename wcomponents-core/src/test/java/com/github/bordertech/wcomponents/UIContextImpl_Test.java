package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.SimpleBeanBoundTableModel.LevelDetails;
import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.WTable.ExpandMode;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * UIContextImpl_Test - unit tests for {@link UIContextImpl}.
 *
 * @author Anthony O'Connor, Jonathan Austin
 * @since 1.0.0
 */
public class UIContextImpl_Test extends AbstractWComponentTestCase {

	/**
	 * Test case 1 - numbers to count.
	 */
	private static final int COUNT1 = 10;

	/**
	 * Test case 1 - expected sum.
	 */
	private static final int SUM1 = 55;

	/**
	 * Test case 2 - numbers to count.
	 */
	private static final int COUNT2 = 5;

	/**
	 * Test case 2 - expected sum.
	 */
	private static final int SUM2 = 15;

	@Test
	public void testDoInvokeLaters() {
		TestSumRunner summer1 = new TestSumRunner(COUNT1); // sun 1 to 10 - expect 55
		TestSumRunner summer2 = new TestSumRunner(COUNT2); // sum 1 to 5 - expect 15

		UIContext uic = createUIContext();

		// doInvokeLaters on empty list - nothing happens
		uic.doInvokeLaters();

		// set some runnables and doInvokeLaters
		uic.invokeLater(summer1);
		uic.invokeLater(summer2);
		uic.doInvokeLaters();

		Assert.assertEquals("should return expected sum", SUM1, summer1.getSum());
		Assert.assertEquals("should return expected sum", SUM2, summer2.getSum());
	}

	/**
	 * TestSumRunner - sums the first 'count' positive integers. This extends Thread - but is run as a jUnit test case
	 * not in a webApp.
	 */
	private static final class TestSumRunner implements Runnable {

		/**
		 * Number of positive integers to add.
		 */
		private final int count;

		/**
		 * Sum of positive integers.
		 */
		private int sum = 0;

		/**
		 * @param count the count
		 */
		private TestSumRunner(final int count) {
			this.count = count;
		}

		@Override
		public void run() {
			for (int i = 1; i <= count; i++) {
				sum += i;
			}
		}

		/**
		 * @return the sum
		 */
		public int getSum() {
			return sum;
		}
	}

	@Test
	public void testUIAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();

		uic.setUI(component);
		Assert.assertEquals("Incorrect component returned", component, uic.getUI());
	}

	@Test
	public void testModelAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();
		WebModel model = new ComponentModel();

		uic.setModel(component, model);
		Assert.assertEquals("Incorrect model returned", model, uic.getModel(component));

		uic.removeModel(component);
		Assert.assertNull("Model should be null after being removed", uic.getModel(component));

		WComponent component2 = new DefaultWComponent();
		WebModel model2 = new ComponentModel();
		uic.setModel(component, model);
		uic.setModel(component2, model2);

		Assert.
				assertEquals("Incorrect number of components returned", 2, uic.getComponents().
						size());
		Assert.assertTrue("Component is not in the user context", uic.getComponents().contains(
				component));
		Assert.assertTrue("Component2 is not in the user context", uic.getComponents().contains(
				component2));
	}

	@Test
	public void testEnvironmentAccessors() {
		UIContext uic = createUIContext();
		Assert.assertTrue("User Context should have a dummy environment by default", uic.
				isDummyEnvironment());
		Assert.assertNotNull("User context should return an environment by default", uic.
				getEnvironment());

		Environment environment = new AbstractEnvironment() {
		};

		uic.setEnvironment(environment);
		Assert.assertEquals("Incorrect environment returned", environment, uic.getEnvironment());
	}

	@Test
	public void testFocussedAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();

		uic.setFocussed(component);
		uic.setUI(component);

		Assert.assertEquals("Incorrect focussed component returned", component, uic.getFocussed());

		UIContext uic2 = createUIContext();
		uic2.setUI(component);
		uic.setFocussed(component, uic2);
		Assert.assertEquals("Incorrect focussed component returned with a different context",
				component, uic.getFocussed());
	}

	@Test
	public void testFocussedIDAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();

		uic.setFocussed(component);
		uic.setUI(component);

		setActiveContext(uic);

		Assert.assertEquals("Incorrect focussed component id returned", component.getId(), uic.
				getFocussedId());

		UIContext uic2 = createUIContext();
		uic2.setUI(component);
		uic.setFocussed(component, uic2);
		Assert.assertEquals("Incorrect focussed component id returned with a different context",
				component.getId(), uic.getFocussedId());
	}

	@Test
	public void testFocusRequiredAccessors() {
		UIContext uic = createUIContext();
		Assert.assertFalse("Focus required should be false by default", uic.isFocusRequired());

		uic.setFocusRequired(true);
		Assert.assertTrue("Focus required should be true", uic.isFocusRequired());
	}

	@Test
	public void testFocusable() {
		UIContext uic = createUIContext();

		setActiveContext(uic);

		// Not visible
		WComponent component = new DefaultWComponent();
		uic.setUI(component);
		uic.setFocussed(component);
		Assert.
				assertEquals("Focussed id not valid before setting to not visible", component.
						getId(), uic.getFocussedId());
		component.setVisible(false);
		Assert.assertNull("Focussed id should be null when component not visible", uic.
				getFocussedId());

		// Hidden
		component = new DefaultWComponent();
		uic.setUI(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting to hidden", component.getId(),
				uic.getFocussedId());
		setFlag((DefaultWComponent) component, ComponentModel.HIDE_FLAG, true);
		Assert.assertNull("Focussed id should be null when component hidden", uic.getFocussedId());

		// Disabled
		component = new DisableableComponent();
		uic.setUI(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting to disabled", component.getId(),
				uic
				.getFocussedId());
		((Disableable) component).setDisabled(true);
		Assert.assertNull("Focussed id should be null when component disabled", uic.getFocussedId());

		// ReadOnly
		component = new AbstractInput() {
			@Override
			protected boolean doHandleRequest(final Request request) {
				return false;
			}

			@Override
			public Object getRequestValue(final Request request) {
				return null;
			}
		};

		uic.setUI(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting to readonly", component.getId(),
				uic.getFocussedId());
		((AbstractInput) component).setReadOnly(true);
		Assert.assertNull("Focussed id should be null when component is readonly", uic.
				getFocussedId());
	}

	@Test
	public void testFocusableInTree() {
		UIContext uic = createUIContext();

		setActiveContext(uic);

		// Parent Not visible
		WContainer parent = new WContainer();
		uic.setUI(parent);
		WComponent component = new DefaultWComponent();
		parent.add(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting parent to not visible", component.
				getId(), uic.getFocussedId());
		parent.setVisible(false);
		Assert.assertNull("Focussed id should be null when parent not visible", uic.getFocussedId());

		// Parent Hidden
		parent = new WContainer();
		uic.setUI(parent);
		component = new DefaultWComponent();
		parent.add(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting parent to hidden", component.
				getId(), uic.getFocussedId());
		parent.setHidden(true);
		Assert.assertNull("Focussed id should be null when parent hidden", uic.getFocussedId());

		// Invisible Container and Parent Hidden
		parent = new WContainer();
		uic.setUI(parent);
		WInvisibleContainer container = new WInvisibleContainer();
		component = new DefaultWComponent();
		container.add(component);
		parent.add(container);
		uic.setFocussed(component);
		Assert.assertEquals(
				"Focussed id not valid before setting parent with invisible container to hidden",
				component.getId(), uic.getFocussedId());
		parent.setHidden(true);
		Assert.assertNull("Focussed id should be null when parent with invisible container hidden",
				uic.getFocussedId());

		// Card Manager not visible
		parent = new WContainer();
		uic.setUI(parent);
		WCardManager manager = new WCardManager();
		component = new DefaultWComponent();
		WComponent component2 = new DefaultWComponent();
		manager.add(component);
		manager.add(component2);
		parent.add(manager);
		manager.makeVisible(component);
		uic.setFocussed(component);
		Assert.assertEquals("Focussed id not valid before setting not visible with Card Manager",
				component.getId(),
				uic.getFocussedId());
		manager.makeVisible(component2);
		Assert.assertNull("Focussed id should be null when not visible in Card Manager", uic.
				getFocussedId());
	}

	@Test
	public void testFocusableInWRepeater() {
		WContainer root = new WContainer();

		// Setup the repeater
		WRepeater repeater = new WRepeater();
		WComponent content = new WBeanComponent() {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);
				UIContext uic = UIContextHolder.getCurrent();

				if (uic instanceof SubUIContext) {
					SubUIContext suic = (SubUIContext) uic;

					// Set the component on the second row to have focus
					if (suic.getRowIndex() == 1) {
						uic.setFocussed(this, uic);
					}
				}
			}
		};
		repeater.setRepeatedComponent(content);

		root.add(repeater);

		// Set the repeater as the "UI" for context
		UIContext uic = new UIContextImpl();
		uic.setUI(root);

		setActiveContext(uic);

		// Setup the bean list on the repeater
		final String optionA = "A";
		final String optionB = "B";
		final String optionC = "C";
		List<String> beans = Arrays.asList(new String[]{optionA, optionB, optionC});
		repeater.setBeanList(beans);

		// ServiceRequest on the repeater (to create the SubUiContexts)
		MockRequest request = new MockRequest();
		repeater.serviceRequest(request);

		// Simulate clearing of scratch map
		uic.clearScratchMap();

		// Get the focused component
		String focusId = uic.getFocussedId();

		// Get the ID of the component on the second row
		UIContext suic = repeater.getRowContext(optionB);

		UIContextHolder.pushContext(suic);
		String rowComponentId = null;

		try {
			rowComponentId = content.getId();
		} finally {
			UIContextHolder.popContext();
		}

		Assert.assertNotNull("Focus ID should not be null", focusId);
		Assert.assertEquals("Focus ID should be the ID for the component on the 2nd row",
				rowComponentId, focusId);

		// Make root hidden
		root.setHidden(true);
		Assert.assertNull("Focus ID should be null with hidden parent", uic.getFocussedId());

	}

	@Test
	public void testFocusableInWRepeaterNested() {
		// Beans list
		final String optionX = "X";
		final String optionY = "Y";
		final List<String> beans = Arrays.asList(new String[]{optionX, optionY});

		// Another beans list for nested repeater
		final String optionA = "A";
		final String optionB = "B";
		final String optionC = "C";
		final List<String> beans2 = Arrays.asList(new String[]{optionA, optionB, optionC});

		// Setup the nested repeater
		WRepeater repeater2 = new WRepeater();
		WComponent content2 = new WBeanComponent() {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);
				UIContext uic = UIContextHolder.getCurrent();

				if (uic instanceof SubUIContext) {
					SubUIContext suic = (SubUIContext) uic;

					// Set the component on the third row to have focus
					if (suic.getRowIndex() == 2) {
						uic.setFocussed(this, uic);
					}
				}
			}
		};
		repeater2.setRepeatedComponent(content2);

		// Setup the top level repeater
		WRepeater repeater = new WRepeater();
		WBeanContainer content = new WBeanContainer() {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);
				UIContext uic = UIContextHolder.getCurrent();

				if (uic instanceof SubUIContext) {
					SubUIContext suic = (SubUIContext) uic;

					if (suic.getRowIndex() == 0) {
						// Set the bean list on the nested repeater, only for the first row
						WRepeater nested = (WRepeater) getChildAt(0);
						nested.setBeanList(beans2);
					}
				}
			}
		};
		content.add(repeater2);
		repeater.setRepeatedComponent(content);

		// Set the repeater as the "UI" for context
		UIContext uic = new UIContextImpl();
		uic.setUI(repeater);

		setActiveContext(uic);

		// Setup the bean list on the top level repeater
		repeater.setBeanList(beans);

		// ServiceRequest on the repeater (to create the SubUiContexts)
		MockRequest request = new MockRequest();
		repeater.serviceRequest(request);

		// Simulate clearing of scratch map
		uic.clearScratchMap();

		// Get the focused component
		String focusId = uic.getFocussedId();

		// Get the ID of the component on the second row
		String rowComponentId = null;

		try {
			UIContext suic = repeater.getRowContext(optionX);
			UIContextHolder.pushContext(suic);
			UIContext suic2 = repeater2.getRowContext(optionC);
			UIContextHolder.pushContext(suic2);
			rowComponentId = content2.getId();
		} finally {
			UIContextHolder.reset();
		}

		Assert.assertNotNull("Focus ID should not be null", focusId);
		Assert.assertEquals("Focus ID should be the ID for the component on the 2nd row",
				rowComponentId, focusId);
	}

	@Test
	public void testFocusableInWRepeaterNestedBeanBound() {
		// Beans list
		final MyBean option1 = new MyBean();
		final MyBean option2 = new MyBean();
		final MyBean option3 = new MyBean();
		final List<MyBean> beans = Arrays.asList(new MyBean[]{option1, option2, option3});

		// Another beans list for nested repeater
		final String optionA = "A";
		final String optionB = "B";
		final String optionC = "C";
		final List<String> beans2 = Arrays.asList(new String[]{optionA, optionB, optionC});

		// Add beans to Option1 for nested repeater
		option1.setRows(beans2);

		// Setup the nested repeater
		WRepeater repeater2 = new WRepeater();
		repeater2.setBeanProperty("rows");
		WComponent content2 = new WBeanComponent() {
			@Override
			public void handleRequest(final Request request) {
				super.handleRequest(request);
				UIContext uic = UIContextHolder.getCurrent();

				if (uic instanceof SubUIContext) {
					SubUIContext suic = (SubUIContext) uic;

					// Set the component on the second row to have focus
					if (suic.getRowIndex() == 1) {
						uic.setFocussed(this, uic);
					}
				}
			}
		};
		repeater2.setRepeatedComponent(content2);

		// Setup the top level repeater
		WRepeater repeater = new WRepeater();
		WBeanContainer content = new WBeanContainer();
		content.add(repeater2);
		repeater.setRepeatedComponent(content);

		// Lock component
		repeater.setLocked(true);

		// Set the repeater as the "UI" for context
		UIContext uic = new UIContextImpl();
		uic.setUI(repeater);

		setActiveContext(uic);

		// Setup the bean list on the top level repeater
		repeater.setBeanList(beans);

		// ServiceRequest on the repeater (to create the SubUiContexts)
		MockRequest request = new MockRequest();
		repeater.serviceRequest(request);

		// Simulate clearing of scratch map
		uic.clearScratchMap();

		// Get the focused component
		String focusId = uic.getFocussedId();

		// Get the ID of the component on the second row
		String rowComponentId = null;
		try {
			UIContext suic = repeater.getRowContext(option1);
			UIContextHolder.pushContext(suic);
			UIContext suic2 = repeater2.getRowContext(optionB);
			UIContextHolder.pushContext(suic2);
			rowComponentId = content2.getId();
		} finally {
			UIContextHolder.reset();
		}

		Assert.assertNotNull("Focus ID should not be null", focusId);
		Assert.assertEquals("Focus ID should be the ID for the component on the 2nd row",
				rowComponentId, focusId);
	}

	@Test
	public void testFocusableInWTableNestedBeanBound() {
		// Beans list
		final MyBean option1 = new MyBean();
		final MyBean option2 = new MyBean();
		final List<MyBean> beans = Arrays.asList(new MyBean[]{option1, option2});

		// Another beans list for nested repeater
		final String optionA = "A";
		final String optionB = "B";
		final String optionC = "C";
		final String optionD = "D";
		final List<String> beans2 = Arrays.asList(new String[]{optionA, optionB, optionC, optionD});

		// Add beans to Option1 for nested repeater
		option1.setRows(beans2);

		// Setup the table with an expandable level
		WTable table = new WTable();
		LevelDetails lvl = new LevelDetails("rows", MyTable.class, false);
		table.setTableModel(new SimpleBeanBoundTableModel(new String[]{"."}, lvl));
		table.addColumn(new WTableColumn("test", new WText()));
		table.setExpandMode(ExpandMode.CLIENT);
		table.setBeanProperty(".");

		// Lock component
		table.setLocked(true);

		// Set the table as the "UI" for context
		UIContext uic = new UIContextImpl();
		uic.setUI(table);

		setActiveContext(uic);

		// Setup the bean list on the table
		table.setBean(beans);

		// ServiceRequest on the repeater (to create the SubUiContexts)
		// The prepare paint will set the row with the option "D" as focused
		MockRequest request = new MockRequest();
		table.preparePaint(request);

		// Simulate clearing of scratch map
		uic.clearScratchMap();

		// Get the focused component
		String focusId = uic.getFocussedId();

		Assert.assertNotNull("Focus ID should not be null", focusId);
	}

	@Test
	public void testFwkAttributeAccessors() {
		UIContext uic = createUIContext();
		String key = "key";
		String value = "value";

		uic.setFwkAttribute(key, value);
		Assert.assertEquals("Incorrect fwk attribute value returned", value, uic.
				getFwkAttribute(key));

		uic.removeFwkAttribute(key);
		Assert.assertNull("Fwk attribute should be null after being removed", uic.getFwkAttribute(
				key));

		String key2 = "key2";
		String value2 = "value2";

		uic.setFwkAttribute(key, value);
		uic.setFwkAttribute(key2, value2);

		Assert.assertEquals("Incorrect number of fwk attributes returned", 2, uic.
				getFwkAttributeNames().size());
		Assert.assertTrue("The test key is not in the user context", uic.getFwkAttributeNames().
				contains(key));
		Assert.assertTrue("The second test key is not in the user context", uic.
				getFwkAttributeNames().contains(key2));
	}

	@Test
	public void testScratchMapAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();

		Assert.assertNotNull("Scratch Map should not be null", uic.getScratchMap(component));
		Assert.assertTrue("Scratch Map should be empty", uic.getScratchMap(component).isEmpty());

		// Add item to scratch map
		String key = "key";
		String value = "value";
		uic.getScratchMap(component).put(key, value);

		Assert.assertEquals("Incorrect number of scratch map entries", 1, uic.getScratchMap(
				component).size());
		Assert.assertTrue("The test key is not in the scratch map", uic.getScratchMap(component).
				containsKey(key));

		// Clear Scratch Map
		uic.clearScratchMap(component);
		Assert.assertTrue("Scratch map should be empty after being cleared", uic.getScratchMap(
				component).isEmpty());

		// Add item to scratch map
		uic.getScratchMap(component).put(key, value);

		// Clear ALL Scratch Maps
		uic.clearScratchMap();
		Assert.assertTrue("Scratch map should be empty after all scratch maps cleared", uic.
				getScratchMap(component).isEmpty());
	}

	@Test
	public void testRequestScratchMapAccessors() {
		UIContext uic = createUIContext();
		WComponent component = new DefaultWComponent();

		Assert.assertNotNull("Scratch Map should not be null", uic.getRequestScratchMap(component));
		Assert.assertTrue("Scratch Map should be empty", uic.getRequestScratchMap(component).isEmpty());

		// Add item to scratch map
		String key = "key";
		String value = "value";
		uic.getRequestScratchMap(component).put(key, value);

		Assert.assertEquals("Incorrect number of scratch map entries", 1, uic.getRequestScratchMap(
				component).size());
		Assert.assertTrue("The test key is not in the scratch map", uic.getRequestScratchMap(component).
				containsKey(key));

		// Clear Scratch Map
		uic.clearRequestScratchMap(component);
		Assert.assertTrue("Scratch map should be empty after being cleared", uic.getRequestScratchMap(
				component).isEmpty());

		// Add item to scratch map
		uic.getRequestScratchMap(component).put(key, value);

		// Clear ALL Scratch Maps
		uic.clearRequestScratchMap();
		Assert.assertTrue("Scratch map should be empty after all scratch maps cleared", uic.
				getRequestScratchMap(component).isEmpty());
	}

	@Test
	public void testCreationTime() {
		UIContext uic = createUIContext();
		Assert.assertTrue("Creation time not set correctly", uic.getCreationTime() > 0);
	}

	@Test
	public void testHeaders() {
		UIContext uic = createUIContext();
		Assert.assertNotNull("Headers should not be null", uic.getHeaders());
	}

	/**
	 * Test component that implements the Disable interface.
	 */
	private static class DisableableComponent extends AbstractWComponent implements Disableable {

		@Override
		public boolean isDisabled() {
			return isFlagSet(ComponentModel.DISABLED_FLAG);
		}

		@Override
		public void setDisabled(final boolean disabled) {
			setFlag(ComponentModel.DISABLED_FLAG, disabled);
		}
	}

	/**
	 * Test bean.
	 */
	public static class MyBean {

		/**
		 * The rows for the nested repeater.
		 */
		private List<String> rows;

		/**
		 * @return Returns the rows.
		 */
		public List<String> getRows() {
			return rows;
		}

		/**
		 * @param rows The rows to set.
		 */
		public void setRows(final List<String> rows) {
			this.rows = rows;
		}
	}

	/**
	 * Table used in expandable level.
	 */
	private static final class MyTable extends WTable {

		/**
		 * Construct. Package protected constructor so can instanciated by WTable as the expandable content.
		 */
		MyTable() {
			setTableModel(new SimpleBeanBoundTableModel(new String[]{"."}));
			WBeanComponent col = new WBeanComponent() {

				@Override
				protected void preparePaintComponent(final Request request) {
					String bean = (String) getData();
					if ("D".equals(bean)) {
						setFocussed();
					}
				}
			};
			addColumn(new WTableColumn("col1", col));
		}
	}
}
