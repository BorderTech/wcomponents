package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WMultiDropdown_Test - Unit tests for {@link WMultiDropdown}.
 *
 * @author Yiannis Paschalidis
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiDropdown_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructorDefault() {
		WMultiDropdown multiDropdown = new WMultiDropdown();
		Assert.assertNull("Incorrect options returned", multiDropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", multiDropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorArray() {
		String[] options = new String[]{"A", "B"};
		WMultiDropdown multiDropdown = new WMultiDropdown(options);
		Assert.assertEquals("Incorrect options returned", Arrays.asList(options), multiDropdown.
				getOptions());
		Assert.assertFalse("allowNoSelection should be false", multiDropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorList() {
		List<String> options = Arrays.asList("A", "B");
		WMultiDropdown multiDropdown = new WMultiDropdown(options);
		Assert.assertEquals("Incorrect options returned", options, multiDropdown.getOptions());
		Assert.assertFalse("allowNoSelection should be false", multiDropdown.isAllowNoSelection());
	}

	@Test
	public void testConstructorTable() {
		WMultiDropdown multiDropdown = new WMultiDropdown(DayOfWeekTable.class);
		Assert.assertEquals("Incorrect table returned", DayOfWeekTable.class, multiDropdown.
				getLookupTable());
		Assert.assertFalse("allowNoSelection should be false", multiDropdown.isAllowNoSelection());
	}

	@Test
	public void testGetNewSelectionsDuplicates() {
		List<String> data = Arrays.asList("a", "b", "c");

		WMultiDropdown dropdown = new WMultiDropdown(data);

		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dropdown.getId(), new String[]{"1", "1", "2"});
		request.setParameter(dropdown.getId() + "-h", "x");

		dropdown.serviceRequest(request);

		Assert.assertEquals("Incorrect selection size", 2, dropdown.getSelected().size());
		Assert.assertEquals("Incorrect selection 1", "a", dropdown.getSelected().get(0));
		Assert.assertEquals("Incorrect selection 2", "b", dropdown.getSelected().get(1));
	}

	@Test
	public void testGetNewSelectionsNullInputs() {
		List<String> data = Arrays.asList(null, "b", "c");

		WMultiDropdown dropdown = new WMultiDropdown(data);
		dropdown.setMaxInputs(2);

		// Test duplicates
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dropdown.getId(), new String[]{"1", "2"});
		request.setParameter(dropdown.getId() + "-h", "x");

		dropdown.serviceRequest(request);

		Assert.assertEquals("Incorrect selection size", 1, dropdown.getSelected().size());
		Assert.assertEquals("Incorrect selection 1", "b", dropdown.getSelected().get(0));
	}

	@Test
	public void testGetNewSelectionsMultipleNullInputs() {
		List<String> data = Arrays.asList(null, "b", "c");

		WMultiDropdown dropdown = new WMultiDropdown(data);
		dropdown.setMaxInputs(2);

		// Test duplicates
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		request.setParameter(dropdown.getId(), new String[]{"1", "1", "1"});
		request.setParameter(dropdown.getId() + "-h", "x");

		dropdown.serviceRequest(request);

		Assert.assertEquals("Incorrect selection size", 1, dropdown.getSelected().size());
		Assert.assertNull("Incorrect selection 1", dropdown.getSelected().get(0));
	}

	@Test
	public void testMaxInputsAccessors() {
		assertAccessorsCorrect(new WMultiDropdown(), "maxInputs", 0, 2, 3);
	}

}
