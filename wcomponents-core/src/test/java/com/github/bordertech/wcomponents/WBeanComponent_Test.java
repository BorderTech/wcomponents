package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.Config;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Unit tests for {@link WBeanComponent}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WBeanComponent_Test extends AbstractWComponentTestCase {

	/**
	 * When these tests are done put things back as they were.
	 */
	@AfterClass
	public static void tearDownClass() {
		Config.reset();
	}

	@Test
	public void testIsChanged() {
		WBeanComponent component = new WBeanComponent();

		String text1 = "WBeanComponent_Test.testIsChanged.text1";
		String text2 = "WBeanComponent_Test.testIsChanged.text2";

		component.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertFalse("Should not be changed by default", component.isChanged());

		component.setData(text1);
		Assert.assertTrue("Should be changed if text differs from default", component.isChanged());

		component.setBean(text1);
		component.setBeanProperty(".");
		Assert.assertFalse("Should not be changed if text equals bean", component.isChanged());

		component.setData(text2);
		Assert.assertTrue("Should be changed if text differs from bean", component.isChanged());
	}

	@Test
	public void testBeanAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "bean", null, new MyTestBean("A"), new MyTestBean("B"));
	}

	@Test
	public void testBeanIdAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "beanId", null, "A", "B");
	}

	@Test
	public void testBeanPropertyAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "beanProperty", null, "A", "B");
	}

	@Test
	public void testSearchAncestorsAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "searchAncestors", true, false, true);
	}

	@Test
	public void testBeanProviderAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "beanProvider", null, new MyTestBeanProvider(), new MyTestBeanProvider());
	}

	@Test
	public void testDataAccessors() {
		assertAccessorsCorrect(new WBeanComponent(), "data", null, "A", "B");
	}

	@Test
	public void testBeanProvider() {

		WBeanComponent comp = new WBeanComponent();
		MyTestBeanProvider provider = new MyTestBeanProvider();
		comp.setBeanProvider(provider);
		comp.setLocked(true);

		// First user
		UIContext uic1 = createUIContext();
		setActiveContext(uic1);
		comp.setBeanId("A");

		// Check correct bean returned for first user
		MyTestBean bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id A should be returned for first user", "A", bean.getId());
		Assert.assertTrue("Bean provider should have been called", provider.isCalled());

		// Second user
		provider.resetCalled();
		UIContext uic2 = createUIContext();
		setActiveContext(uic2);
		comp.setBeanId("B");

		// Check correct bean returned for first user
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id B should be returned for second user", "B", bean.getId());
		Assert.assertTrue("Bean provider should have been called", provider.isCalled());

		// Go back to user 1
		setActiveContext(uic1);
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id A should be returned again for first user", "A", bean.getId());
	}

	@Test
	public void testSearchAncestor() {

		WContainer root = new WContainer();
		WBeanComponent comp = new WBeanComponent();
		comp.setBeanProperty(".");
		root.add(comp);

		root.setLocked(true);
		setActiveContext(createUIContext());

		// Set a bean on the root
		MyTestBean bean = new MyTestBean("X");
		root.setBean(bean);

		// Search Ancestor to true (default)
		comp.setSearchAncestors(true);
		MyTestBean bean2 = (MyTestBean) comp.getBean();
		Assert.assertNotNull("Should find bean from root component", bean2);
		Assert.assertEquals("Should return the bean set on the root component", bean, bean2);

		// Switch off search ancestor
		comp.setSearchAncestors(false);
		bean2 = (MyTestBean) comp.getBean();
		Assert.assertNull("Should not find bean and return null", bean2);
	}

	@Test
	public void testBeanValue() {
		WBeanComponent comp = new WBeanComponent();
		comp.setBeanProperty("value");

		comp.setLocked(true);
		setActiveContext(createUIContext());

		// Set a bean on the root
		MyTestBean bean = new MyTestBean("X");
		comp.setBean(bean);

		// Get the bean value
		String value = (String) comp.getBeanValue();
		Assert.assertEquals("Should return the bean value", bean.getValue(), value);
	}

	@Test
	public void testBeanValueParentLegacy() {

		WContainer root = new WContainer();
		WBeanComponent comp = new WBeanComponent();
		comp.setBeanProperty(".");
		root.add(comp);

		// Set a bean property on the root
		root.setBeanProperty("value");

		root.setLocked(true);
		setActiveContext(createUIContext());

		// Set a bean on the root
		MyTestBean bean = new MyTestBean("X");
		root.setBean(bean);

		// LEGACY logic PARAMETER
		Config.getInstance().setProperty("bordertech.wcomponents.bean.logic.correct", "false");

		// Legacy behaviour is the whole bean is returned form the parent (instead of the bean value)
		MyTestBean bean2 = (MyTestBean) comp.getBean();
		Assert.assertEquals("Legacy mode should return the whole bean instead of the bean value", bean, bean2);

		// Correct logic PARAMETER
		Config.getInstance().setProperty("bordertech.wcomponents.bean.logic.correct", "true");

		// Correct behaviour is the bean value is returned form the parent
		String value = (String) comp.getBean();
		Assert.assertEquals("Correct mode should return the bean value fromthe parent", bean.getValue(), value);
	}

	@Test
	public void testBeanProviderScratchMapPhaseScope() {
		WBeanComponent comp = new WBeanComponent();
		MyTestBeanProvider provider = new MyTestBeanProvider();
		comp.setBeanProvider(provider);
		comp.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);
		comp.setBeanId("A");
		MyTestBean bean = (MyTestBean) comp.getBean();

		// Check correct bean returned
		Assert.assertEquals("Bean with id A should be returned", "A", bean.getId());
		Assert.assertTrue("Bean provider should have been called", provider.isCalled());

		// Map contains bean
		Map map = uic.getScratchMap(comp);
		Assert.assertEquals("Key in the scratch map should be the bean id", "A", map.get("WBeanComponent.request.bean.id"));
		Assert.assertEquals("Object in the scratch map should be the bean id", bean, map.get("WBeanComponent.request.bean.obj"));

		// Reset provider flag
		provider.resetCalled();

		// Ask for bean again
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id A should be returned", "A", bean.getId());
		Assert.assertFalse("Bean provider should not have been called. Is in scratch map", provider.isCalled());

		// Change bean ID
		comp.setBeanId("B");

		// Reset provider flag
		provider.resetCalled();

		// Ask for bean again
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id B should be returned", "B", bean.getId());
		Assert.assertTrue("Bean provider should have been called for different bean id", provider.isCalled());

		map = uic.getScratchMap(comp);
		Assert.assertEquals("Key in the scratch map should be the new bean id", "B", map.get("WBeanComponent.request.bean.id"));
		Assert.assertEquals("Object in the scratch map should be the new bean id", bean, map.get("WBeanComponent.request.bean.obj"));
	}

	@Test
	public void testBeanProviderRequestScratchMapPhaseScope() {

		Config.getInstance().setProperty("bordertech.wcomponents.bean.provider.request.scope.enabled", "true");

		WBeanComponent comp = new WBeanComponent();
		MyTestBeanProvider provider = new MyTestBeanProvider();
		comp.setBeanProvider(provider);
		comp.setLocked(true);

		UIContext uic = createUIContext();
		setActiveContext(uic);
		comp.setBeanId("A");
		MyTestBean bean = (MyTestBean) comp.getBean();

		// Check correct bean returned
		Assert.assertEquals("Bean with id A should be returned", "A", bean.getId());
		Assert.assertTrue("Bean provider should have been called", provider.isCalled());

		// Map contains bean
		Map map = uic.getRequestScratchMap(comp);
		Assert.assertEquals("Key in the scratch map should be the bean id", "A", map.get("WBeanComponent.request.bean.id"));
		Assert.assertEquals("Object in the scratch map should be the bean id", bean, map.get("WBeanComponent.request.bean.obj"));

		// Reset provider flag
		provider.resetCalled();

		// Ask for bean again
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id A should be returned", "A", bean.getId());
		Assert.assertFalse("Bean provider should not have been called. Is in scratch map", provider.isCalled());

		// Change bean ID
		comp.setBeanId("B");

		// Reset provider flag
		provider.resetCalled();

		// Ask for bean again
		bean = (MyTestBean) comp.getBean();
		Assert.assertEquals("Bean with id B should be returned", "B", bean.getId());
		Assert.assertTrue("Bean provider should have been called for different bean id", provider.isCalled());

		map = uic.getRequestScratchMap(comp);
		Assert.assertEquals("Key in the scratch map should be the new bean id", "B", map.get("WBeanComponent.request.bean.id"));
		Assert.assertEquals("Object in the scratch map should be the new bean id", bean, map.get("WBeanComponent.request.bean.obj"));
	}

	/**
	 * Test bean provider.
	 */
	public static class MyTestBeanProvider implements BeanProvider, Serializable {

		// Basic cache for beans
		private final Map<String, MyTestBean> beans = new HashMap<>();

		private boolean called = false;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getBean(final BeanProviderBound beanProviderBound) {
			called = true;
			String beanId = (String) beanProviderBound.getBeanId();
			MyTestBean bean = beans.get(beanId);
			if (bean == null) {
				bean = new MyTestBean(beanId);
				bean.setValue(beanId + "-val");
				beans.put(beanId, bean);
			}
			return bean;
		}

		/**
		 * @return true if getBean was called.
		 */
		public boolean isCalled() {
			return called;
		}

		/**
		 * Reset the called flag.
		 */
		public void resetCalled() {
			called = false;
		}
	}

	/**
	 * Test POJO.
	 */
	public static class MyTestBean implements Serializable {

		private final String id;
		private String value;

		/**
		 * @param id the bean id
		 */
		public MyTestBean(final String id) {
			this.id = id;
		}

		/**
		 * @return the bean id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the bean value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the bean value
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {
			return obj instanceof MyTestBean && Objects.equals(((MyTestBean) obj).getId(), getId());
		}

	}

}
