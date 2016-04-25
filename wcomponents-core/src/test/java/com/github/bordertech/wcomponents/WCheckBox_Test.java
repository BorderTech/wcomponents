package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WCheckBox}.
 *
 * @author Ming Gao
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WCheckBox_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WCheckBox wcbTest = new WCheckBox();
		Assert.assertFalse("CheckBox should default to not selected", wcbTest.isSelected());
	}

	@Test
	public void testConstructorBoolean() {
		WCheckBox wcbTest = new WCheckBox(true);
		Assert.assertTrue("CheckBox should be selected", wcbTest.isSelected());
	}

	@Test
	public void testGetValueAsString() {
		WCheckBox wcbTest = new WCheckBox();

		wcbTest.setSelected(false);
		Assert.assertNull("CheckBox ValueAsString should be null for not selected", wcbTest.
				getValueAsString());

		wcbTest.setSelected(true);
		Assert.assertEquals("CheckBox ValueAsString should be 'true' for selected", "true", wcbTest.
				getValueAsString());
	}

	@Test
	public void testGroupAccessors() {
		assertAccessorsCorrect(new WCheckBox(), "group", null, new WComponentGroup<WCheckBox>(),
				new WComponentGroup<WCheckBox>());
	}

	@Test
	public void testDoHandleRequest() {
		WCheckBox wcbTest = new WCheckBox();
		wcbTest.setLocked(true);

		setActiveContext(createUIContext());

		// Request - Not Selected (no change)
		MockRequest request = setupRequest(wcbTest, false);
		boolean changed = wcbTest.doHandleRequest(request);
		Assert
				.assertFalse(
						"doHandleRequest should return false with not selected in request and checkbox not selected",
						changed);
		Assert.assertFalse("Should not be selected after not selected in request", wcbTest.
				isSelected());

		// Request - Selected (change)
		request = setupRequest(wcbTest, true);
		changed = wcbTest.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should return true with selected in request and checkbox not selected",
				changed);
		Assert.assertTrue("Should be selected after request with parameter set", wcbTest.
				isSelected());

		// Request - Selected (no change)
		request = setupRequest(wcbTest, true);
		changed = wcbTest.doHandleRequest(request);
		Assert.assertFalse(
				"doHandleRequest should return false with selected in request and checkbox selected",
				changed);
		Assert.assertTrue("Should be selected after request with parameter set", wcbTest.
				isSelected());

		// Request - Not Selected (change)
		request = setupRequest(wcbTest, false);
		changed = wcbTest.doHandleRequest(request);
		Assert.assertTrue(
				"doHandleRequest should return true with not selected in request and checkbox selected",
				changed);
		Assert.assertFalse("Should not be selected after not selected in request", wcbTest.
				isSelected());
	}

	@Test
	public void testGetRequestValue() {
		WCheckBox wcbTest = new WCheckBox();
		wcbTest.setLocked(true);

		setActiveContext(createUIContext());

		// Current value
		wcbTest.setSelected(true);

		// Empty Request should default to current value
		MockRequest request = new MockRequest();
		Assert.assertTrue("Request value should be current value that is true", wcbTest.
				getRequestValue(request));

		// Request with Check box selected
		request = setupRequest(wcbTest, true);
		Assert.assertTrue("Request value returned should be true", wcbTest.getRequestValue(request));

		// Request with Check box not selected
		request = setupRequest(wcbTest, false);
		Assert.assertFalse("Request value returned should be false", wcbTest.
				getRequestValue(request));
	}

	@Test
	public void testIsPresent() {
		WCheckBox wcbTest = new WCheckBox();
		setActiveContext(createUIContext());

		// Empty Request
		MockRequest request = new MockRequest();
		Assert.assertFalse("IsPresent should return false on empty request", wcbTest.isPresent(
				request));

		// Check box on request - selected
		request = setupRequest(wcbTest, true);
		Assert.assertTrue("IsPresent should return true for selected checkbox on request", wcbTest.
				isPresent(request));

		// Check box on request - not selected
		request = setupRequest(wcbTest, false);
		Assert.assertTrue("IsPresent should return true for not selected checkbox on request",
				wcbTest.isPresent(request));
	}

	@Test
	public void testGetValue() {
		WCheckBox wcbTest = new WCheckBox();
		setActiveContext(createUIContext());

		Assert.assertFalse("CheckBox getValue should default to false", wcbTest.getValue());

		wcbTest.setData(Boolean.FALSE);
		Assert.assertFalse("CheckBox getValue should be false", wcbTest.getValue());

		wcbTest.setData(Boolean.TRUE);
		Assert.assertTrue("CheckBox getValue should be true", wcbTest.getValue());

		wcbTest.setData(null);
		Assert.assertFalse("CheckBox getValue should be false for null data", wcbTest.getValue());

		wcbTest.setData("BAD DATA");
		Assert.assertFalse("CheckBox getValue should be false for invalid data", wcbTest.getValue());

		wcbTest.setData("true");
		Assert.assertFalse("CheckBox getValue should be false for invalid data", wcbTest.getValue());
	}

	@Test
	public void testSelectedAccessors() {
		assertAccessorsCorrect(new WCheckBox(), "selected", false, true, false);
	}

	@Test
	public void testSubmitOnChangeAccessors() {
		assertAccessorsCorrect(new WCheckBox(), "submitOnChange", false, true, false);
	}

	@Test
	public void testGetActionCommand() {
		WCheckBox wcbTest = new WCheckBox();
		setActiveContext(createUIContext());
		Assert.assertEquals("Action command should match the name", wcbTest.getId(), wcbTest.
				getActionCommand());
	}

	/**
	 * @param target the target component
	 * @param selected true if the component is selected
	 * @return the mock request
	 */
	private MockRequest setupRequest(final WCheckBox target, final boolean selected) {
		MockRequest request = new MockRequest();
		request.setParameter(target.getId(), selected ? "true" : "");

		return request;
	}

}
