package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Stack;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Test for the ComponentModel class.
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public class ComponentModel_Test extends AbstractWComponentTestCase {

	/**
	 * Test the copy data method.
	 */
	@Test
	public void testCopyData() {

		ComponentModel model = new ComponentModel();

		ArrayList<String> list = new ArrayList<>();
		list.add("1");
		ArrayList<String> copiedList = (ArrayList<String>) model.copyData(list);
		copiedList.add("2");
		Assert.assertEquals("Original list was updated incorrectly.", 1, list.size());
		Assert.assertEquals("Copied list was not updated.", 2, copiedList.size());

		String[] array = new String[1];
		array[0] = "0";
		String[] copiedArray = (String[]) model.copyData(array);
		copiedArray[0] = "1";
		Assert.assertEquals("Original array was updated incorrectly.", "0", array[0]);
		Assert.assertEquals("Copied list was not updated.", "1", copiedArray[0]);

		Stack<String> stack = new Stack<>();
		stack.add("1");
		Stack<String> copiedStack = (Stack<String>) model.copyData(stack);
		copiedStack.add("2");
		Assert.assertEquals("Original stack was updated incorrectly.", 1, stack.size());
		Assert.assertEquals("Copied stack was not updated.", 2, copiedStack.size());
	}
}
