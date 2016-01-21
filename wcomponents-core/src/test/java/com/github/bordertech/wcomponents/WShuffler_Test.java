package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.mock.MockRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for WShuffler.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WShuffler_Test extends AbstractWComponentTestCase {

	/**
	 * Test option A.
	 */
	private static final String OPTION_A = "A";
	/**
	 * Test option B.
	 */
	private static final String OPTION_B = "B";
	/**
	 * Test option C.
	 */
	private static final String OPTION_C = "C";
	/**
	 * Test option D.
	 */
	private static final String OPTION_X = "X";
	/**
	 * List of test options.
	 */
	private static final List<String> OPTIONS = Arrays.asList(OPTION_A, OPTION_B, OPTION_C);
	/**
	 * List of test options.
	 */
	private static final List<String> OPTIONS2 = Arrays.asList(OPTION_X);
	/**
	 * Empty list.
	 */
	private static final List<?> EMPTY_LIST = Collections.EMPTY_LIST;

	@Test
	public void testConstructors() {
		// Default Constructor
		WShuffler shuffler = new WShuffler();
		Assert.assertNull("Default options should be null", shuffler.getOptions());

		// Constructor 2
		shuffler = new WShuffler(OPTIONS);
		Assert.assertEquals("Incorrect default options returned", OPTIONS, shuffler.getOptions());
	}

	@Test
	public void testOptionAccessors() {
		assertAccessorsCorrect(new WShuffler(), "options", null, OPTIONS, OPTIONS2);
	}

	@Test
	public void testGetValueConvertDataToList() {
		WShuffler shuffler = new WShuffler();

		shuffler.setLocked(true);
		setActiveContext(createUIContext());

		// Data is null
		shuffler.setData(null);
		Assert.assertNull("getValue for null data should be null", shuffler.getValue());

		// Data is empty list
		shuffler.setData(EMPTY_LIST);
		Assert.assertEquals("getValue for empty list data should be an empty list", EMPTY_LIST,
				shuffler.getValue());

		// Data is a list
		shuffler.setOptions(OPTIONS);
		Assert.assertEquals("getValue for list data should be the selected options in a list",
				OPTIONS,
				shuffler.getValue());

		// Data is an empty array
		shuffler.setData(new String[]{});
		Assert.assertEquals("getValue for empty array data should be an empty list", EMPTY_LIST,
				shuffler.getValue());

		// Data is an array
		shuffler.setData(OPTIONS.toArray());
		Assert.assertEquals("getValue for array data should be the selected options in a list",
				OPTIONS,
				shuffler.getValue());

		// Data is an Option
		shuffler.setData(OPTION_B);
		Assert.assertEquals("getValue for Object data should be the selected option in a list",
				Arrays.asList(OPTION_B), shuffler.getValue());
	}

	@Test
	public void testRowAccessors() {
		assertAccessorsCorrect(new WShuffler(), "rows", 0, 1, 2);
	}

	@Test
	public void testDoHandleRequest() {
		WShuffler shuffler = new WShuffler(OPTIONS);
		shuffler.setLocked(true);
		setActiveContext(createUIContext());

		// Empty Request (No Change)
		MockRequest request = new MockRequest();
		boolean changed = shuffler.doHandleRequest(request);

		Assert.assertFalse("Empty Request - Options should not have changed", changed);
		Assert.assertEquals("Empty Request - Incorrect default options returned", OPTIONS, shuffler.
				getOptions());

		// Request with Options in same order (No Change)
		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(shuffler.getId(), OPTIONS.toArray(new String[]{}));
		changed = shuffler.doHandleRequest(request);

		Assert.assertFalse("Same Request - Options should not have changed", changed);
		Assert.assertEquals("Same Request - Incorrect default options returned", OPTIONS, shuffler.
				getOptions());

		// Shuffle options via a request (change)
		setActiveContext(createUIContext());
		request = new MockRequest();
		request.setParameter(shuffler.getId(), new String[]{OPTION_C, OPTION_A, OPTION_B});
		changed = shuffler.doHandleRequest(request);

		Assert.assertTrue("Shuffled Request - Options should not have changed", changed);
		Assert.assertEquals("Shuffled Request - Invalid number of options", 3,
				shuffler.getOptions().size());
		Assert.assertEquals("Shuffled Request - Invalid first option", OPTION_C, shuffler.
				getOptions().get(0));
		Assert.assertEquals("Shuffled Request - Invalid second option", OPTION_A, shuffler.
				getOptions().get(1));
		Assert.assertEquals("Shuffled Request - Invalid third option", OPTION_B, shuffler.
				getOptions().get(2));

		// Default should not have changed
		resetContext();
		Assert.assertEquals("Incorrect default options returned", OPTIONS, shuffler.getOptions());
	}

	@Test
	public void testGetRequestValue() {
		WShuffler shuffler = new WShuffler(OPTIONS);
		shuffler.setLocked(true);

		// Empty Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();

		// Should return default options
		Assert.assertEquals("Incorrect default options returned", OPTIONS, shuffler.getRequestValue(
				request));

		// Shuffle options on the request
		request = new MockRequest();
		request.setParameter(shuffler.getId(), new String[]{OPTION_C, OPTION_A, OPTION_B});
		Assert.assertNotNull("Request Value should not be null", shuffler.getRequestValue(request));
		Assert.
				assertEquals("Invalid number of options", 3, shuffler.getRequestValue(request).
						size());
		Assert.assertEquals("Invalid first option", OPTION_C, shuffler.getRequestValue(request).get(
				0));
		Assert.assertEquals("Invalid second option", OPTION_A, shuffler.getRequestValue(request).
				get(1));
		Assert.assertEquals("Invalid third option", OPTION_B, shuffler.getRequestValue(request).get(
				2));
	}

	@Test
	public void testIsPresent() {
		WShuffler shuffler = new WShuffler(OPTIONS);
		shuffler.setLocked(true);

		// Empty Request
		setActiveContext(createUIContext());
		MockRequest request = new MockRequest();
		Assert.assertFalse("IsPresent should return false", shuffler.isPresent(request));

		// Shuffler on the request (but options are different)
		request = new MockRequest();
		request.setParameter(shuffler.getId(), new String[]{OPTION_A, OPTION_B});
		Assert.assertFalse("IsPresent should return false when options have changed", shuffler.
				isPresent(request));

		// Shuffler on the request (Same options)
		request = new MockRequest();
		request.setParameter(shuffler.getId(), new String[]{OPTION_C, OPTION_A, OPTION_B});
		Assert.assertTrue("IsPresent should return true", shuffler.isPresent(request));
	}

}
