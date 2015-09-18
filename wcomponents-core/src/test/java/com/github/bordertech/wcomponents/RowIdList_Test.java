package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.RowIdList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Junit test case for {@link RowIdList}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class RowIdList_Test {

	@Test
	public void testConstructorIllegalArgs() {
		try {
			new RowIdList(-1, 1);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}

		try {
			new RowIdList(3, 2);
			Assert.fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}
	}

	@Test
	public void testSize() {
		Assert.assertEquals("Incorrect size for [0,0]", 1, new RowIdList(0, 0).size());
		Assert.assertEquals("Incorrect size for [0,1]", 2, new RowIdList(0, 1).size());
		Assert.assertEquals("Incorrect size for [1,3]", 3, new RowIdList(1, 3).size());
	}

	@Test
	public void testIsEmpty() {
		List<Integer> list = new RowIdList(0, 0);
		Assert.assertFalse("RowIdList should never be empty", list.isEmpty());
	}

	@Test
	public void testSubList() {
		List<Integer> list = new RowIdList(20, 30);
		List<Integer> subList = list.subList(3, 6); // should contain 23-25 inclusive

		Assert.assertEquals("Incorrect sublist size", 3, subList.size());
		Assert.assertEquals("Incorrect sublist element 0", Integer.valueOf(23), subList.get(0));
		Assert.assertEquals("Incorrect sublist element 1", Integer.valueOf(24), subList.get(1));
		Assert.assertEquals("Incorrect sublist element 2", Integer.valueOf(25), subList.get(2));
	}

	@Test
	public void testImmutability() {
		List<Integer> list = new RowIdList(123, 123);

		try {
			list.add(345);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.add(0, 345);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.addAll(Arrays.asList(new Integer[]{345}));
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.addAll(0, Arrays.asList(new Integer[]{345}));
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.clear();
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.retainAll(list);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.removeAll(Collections.emptyList());
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.remove(0);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.remove(list.get(0));
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}

		try {
			list.set(0, 456);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("Size should not have changed", 1, list.size());
			Assert.assertEquals("Value should not have changed", 123, list.get(0).intValue());
		}
	}

	@Test
	public void testContains() {
		final int start = 123;
		final int end = 456;

		List<Integer> list = new RowIdList(start, end);

		Assert.assertFalse("Contains(String) should return false", list.contains("123"));
		Assert.assertFalse("Contains(start-1) should return false", list.contains(start - 1));
		Assert.assertTrue("Contains(start) should return true", list.contains(start));
		Assert.assertTrue("Contains(start + 1) should return true", list.contains(start + 1));
		Assert.assertTrue("Contains((end-start)/2) should return true", list.contains(
				(end - start / 2)));
		Assert.assertTrue("Contains(end - 1) should return true", list.contains(end - 1));
		Assert.assertTrue("Contains(end) should return true", list.contains(end));
		Assert.assertFalse("Contains(end+1) should return false", list.contains(end + 1));
	}

	@Test
	public void testContainsAll() {
		final int start = 123;
		final int end = 456;

		List<Integer> contained = Arrays.asList(new Integer[]{start, start + 1, end - 1, end});
		List<Integer> notContained = Arrays.asList(
				new Integer[]{start, start + 1, end - 1, end, end + 1});

		List<Integer> list = new RowIdList(start, end);
		Assert.assertTrue("ContainsAll should return true when all items are contained", list.
				containsAll(contained));
		Assert.assertFalse("ContainsAll should return false when not all items are contained", list.
				containsAll(notContained));
	}

	@Test
	public void testGet() {
		final int start = 123;
		final int end = 456;

		List<Integer> list = new RowIdList(start, end);

		for (int i = 0; i <= end - start; i++) {
			Assert.assertEquals("Incorrect value for get(" + i + ")", Integer.valueOf(start + i),
					list.get(i));
		}

		// Test OOB errors
		try {
			list.get(-1);
			Assert.fail("Should have thrown an ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}

		try {
			list.get(list.size());
			Assert.fail("Should have thrown an ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}
	}

	@Test
	public void testIndexOf() {
		final int start = 123;
		final int end = 456;

		List<Integer> list = new RowIdList(start, end);

		for (int i = 0; i <= end - start; i++) {
			Object item = Integer.valueOf(start + i);
			Assert.assertEquals("Incorrect value for indexOf(" + item + ")", i, list.indexOf(item));
			Assert.assertEquals("Incorrect value for lastIndexOf(" + item + ")", i, list.
					lastIndexOf(item));
		}

		// Test for item not in list
		Assert.assertEquals("Index of items not in list should be -1", -1, list.indexOf("123"));
		Assert.assertEquals("Index of items not in list should be -1", -1, list.indexOf(start - 1));
		Assert.assertEquals("Index of items not in list should be -1", -1, list.lastIndexOf(
				start - 1));
		Assert.assertEquals("Index of items not in list should be -1", -1, list.indexOf(end + 1));
		Assert.
				assertEquals("Index of items not in list should be -1", -1, list.
						lastIndexOf(end + 1));
	}

	@Test
	public void testIterator() {
		List<Integer> list = new RowIdList(7, 8);

		Iterator<Integer> iterator = list.iterator();

		Assert.assertEquals("Incorrect iterator value", Integer.valueOf(7), iterator.next());

		// Ensure list is immutable
		try {
			iterator.remove();
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("List size should not have changed", 2, list.size());
		}

		Assert.assertEquals("Incorrect iterator value", Integer.valueOf(8), iterator.next());
		Assert.assertFalse("Iterator should not have next", iterator.hasNext());

		// Ensure bounds checking works
		try {
			iterator.next();
			Assert.fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}
	}

	@Test
	public void testListIterator() {
		List<Integer> list = new RowIdList(7, 8);

		ListIterator<Integer> iterator = list.listIterator();

		// Ensure list is immutable
		try {
			iterator.remove();
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("List size should not have changed", 2, list.size());
		}

		try {
			iterator.set(123);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("List size should not have changed", 2, list.size());
		}

		try {
			iterator.add(123);
			Assert.fail("Should have thrown an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
			Assert.assertEquals("List size should not have changed", 2, list.size());
		}

		// Go forwards through the list
		Assert.assertEquals("Incorrect iterator value", Integer.valueOf(7), iterator.next());
		Assert.assertEquals("Incorrect iterator value", Integer.valueOf(8), iterator.next());

		// Ensure bounds checking works -- upper bound
		Assert.assertFalse("Iterator should not have next", iterator.hasNext());

		try {
			iterator.next();
			Assert.fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}

		// Go backwards through the list
		Assert.assertEquals("Incorrect iterator previous value", Integer.valueOf(8), iterator.
				previous());
		Assert.assertEquals("Incorrect iterator previous value", Integer.valueOf(7), iterator.
				previous());

		// Ensure bounds checking works -- lower bound
		Assert.assertFalse("Iterator should not have previous", iterator.hasPrevious());

		try {
			iterator.previous();
			Assert.fail("Should have thrown a NoSuchElementException");
		} catch (NoSuchElementException e) {
			Assert.assertNotNull("Thrown exception should contain a message", e.getMessage());
		}
	}

	@Test
	public void testToArray() {
		final int start = 123;
		final int end = 456;

		RowIdList list = new RowIdList(start, end);
		Integer[] array1 = list.toArray();
		Integer[] array2 = list.toArray(new Integer[list.size()]);

		Assert.assertEquals("Incorrect array1 length", list.size(), array1.length);
		Assert.assertEquals("Incorrect array2 length", list.size(), array2.length);

		for (int i = 0; i < array1.length; i++) {
			Assert.assertEquals("Incorrect array1 value", list.get(i), array1[i]);
			Assert.assertEquals("Incorrect array2 value", list.get(i), array2[i]);
		}
	}
}
