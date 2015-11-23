package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Duplet_Test - JUnit tests for {@link Duplet}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class Duplet_Test {

	private static final int TESTVAL = 12345;

	@Test
	public void testNoArgsConstructor() {
		Duplet<String, Integer> duplet = new Duplet<>();

		Assert.assertNull("First should be null", duplet.getFirst());
		Assert.assertNull("Second should be null", duplet.getSecond());
	}

	@Test
	public void testConstructor() {
		Duplet<String, Integer> duplet = new Duplet<>("testConstructor.value1", 1);
		String value1 = duplet.getFirst();
		Integer value2 = duplet.getSecond();

		Assert.assertEquals("Incorrect value1", "testConstructor.value1", value1);
		Assert.assertEquals("Incorrect value2", Integer.valueOf(1), value2);
	}

	@Test
	public void testSetFirst() {
		Duplet<String, Integer> duplet = new Duplet<>();

		duplet.setFirst("testSetFirst.first");
		Assert.assertEquals("Incorrect first", "testSetFirst.first", duplet.getFirst());
		Assert.assertNull("Second should be null", duplet.getSecond());
	}

	@Test
	public void testSetSecond() {
		Duplet<String, Integer> duplet = new Duplet<>();

		duplet.setSecond(1);
		Assert.assertNull("First should be null", duplet.getFirst());
		Assert.assertEquals("Incorrect second", new Integer(1), duplet.getSecond());
	}

	@Test
	public void testEquals() {
		Duplet<Object, Object> dup1 = new Duplet<Object, Object>(new Long(TESTVAL), null);
		Duplet<Object, Object> dup2 = new Duplet<Object, Object>(new Long(TESTVAL), null);
		Duplet<Object, Object> dup3 = new Duplet<Object, Object>(null, new Long(TESTVAL));

		Assert.assertTrue("Duplet should equal itself", dup1.equals(dup1));
		Assert.assertTrue("Duplet should equal an equivalent duplet", dup1.equals(dup2));

		Assert.assertFalse("Duplet should not equal null", dup1.equals(null));
		Assert.assertFalse("Duplet should not equal another class", "".equals(dup1));
		Assert.assertFalse("Duplet should equal a different duplet", dup1.equals(dup3));
	}

	@Test
	public void testHashCode() {
		Duplet<Object, Object> dup1 = new Duplet<Object, Object>(new Long(TESTVAL), null);
		Duplet<Object, Object> dup2 = new Duplet<Object, Object>(new Long(TESTVAL), null);

		Assert.assertEquals("Duplet should have same hash as an equivalent duplet", dup1.hashCode(),
				dup2.hashCode());
	}
}
