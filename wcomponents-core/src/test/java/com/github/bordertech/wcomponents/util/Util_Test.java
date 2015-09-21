package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test the functionality of the Util class.
 *
 * @author Yiannis Paschalidis
 * @since 11/07/08
 */
public class Util_Test {

	@Test
	public void testEmpty() {
		Assert.assertTrue("A null String should be empty", Util.empty(null));
		Assert.assertTrue("\"\" should be empty", Util.empty(""));
		Assert.assertTrue("\" \" should be empty", Util.empty(" "));
		Assert.assertTrue("\"  \" should be empty", Util.empty("  "));
		Assert.assertTrue("\"\\n\" should be empty", Util.empty("\n"));
		Assert.assertTrue("\"\\t \\n \\r\" should be empty", Util.empty("\t \n \r"));

		Assert.assertFalse("\"a\" should not be empty", Util.empty("a"));
		Assert.assertFalse("\"a \" should not be empty", Util.empty("a "));
		Assert.assertFalse("\" a\" should not be empty", Util.empty(" a"));
		Assert.assertFalse("\" a \" should not be empty", Util.empty(" a "));
	}

	@Test
	public void testEquals() {
		Assert.assertTrue("Two nulls should be equal", Util.equals(null, null));
		Assert.assertTrue("A constant String should equal itself", Util.equals("a", "a"));
		Assert.assertTrue("Equal failed for equal objects", Util.equals(new Integer(1), new Integer(
				1)));

		Assert.assertFalse("Null should not equal a constant String", Util.equals(null, "a"));
		Assert.assertFalse("A constant String should not equal null", Util.equals("a", null));
		Assert.assertFalse("Unequal constant strings should not be equal", Util.equals("a", "b"));
		Assert.assertFalse("Two distinct Objects should not be equal", Util.equals(new Object(),
				new Object()));
	}

	@Test
	public void testCompareAllowNull() {
		Assert.assertEquals("Two nulls should be equal", 0, Util.compareAllowNull(null, null));
		Assert.assertTrue("Nulls should be less than anything else", Util.
				compareAllowNull(null, "a") < 0);
		Assert.assertTrue("Anything should be greater than null",
				Util.compareAllowNull("a", null) > 0);
		Assert.assertEquals("Equal values should be equal", 0, Util.compareAllowNull("a", "a"));
		Assert.assertTrue("'A' should be less than 'B'", Util.compareAllowNull("a", "b") < 0);
		Assert.assertTrue("'B' should be greater than 'A'", Util.compareAllowNull("b", "a") > 0);
	}

	@Test
	public void testRightTrim() {
		Assert.assertNull("Incorrect right trimmed value returned", Util.rightTrim(null));
		Assert.assertEquals("Incorrect right trimmed value returned", "", Util.rightTrim(""));
		Assert.assertEquals("Incorrect right trimmed value returned", "", Util.rightTrim(" "));
		Assert.assertEquals("Incorrect right trimmed value returned", "", Util.rightTrim("  "));
		Assert.assertEquals("Incorrect right trimmed value returned", "A", Util.rightTrim("A"));
		Assert.assertEquals("Incorrect right trimmed value returned", "A", Util.rightTrim("A "));
		Assert.assertEquals("Incorrect right trimmed value returned", " A", Util.rightTrim(" A "));
		Assert.assertEquals("Incorrect right trimmed value returned", " A", Util.rightTrim(" A  "));
		Assert.
				assertEquals("Incorrect right trimmed value returned", "  A", Util.
						rightTrim("  A  "));
		Assert.assertEquals("Incorrect right trimmed value returned", "A B C", Util.rightTrim(
				"A B C"));
		Assert.assertEquals("Incorrect right trimmed value returned", "  A B C", Util.rightTrim(
				"  A B C"));
		Assert.assertEquals("Incorrect right trimmed value returned", "  A B C", Util.rightTrim(
				"  A B C "));
		Assert.assertEquals("Incorrect right trimmed value returned", "  A B C", Util.rightTrim(
				"  A B C  "));
	}

	@Test
	public void testLeftTrim() {
		Assert.assertNull("Incorrect left trimmed value returned", Util.leftTrim(null));
		Assert.assertEquals("Incorrect left trimmed value returned", "", Util.leftTrim(""));
		Assert.assertEquals("Incorrect left trimmed value returned", "", Util.leftTrim(" "));
		Assert.assertEquals("Incorrect left trimmed value returned", "", Util.leftTrim("  "));
		Assert.assertEquals("Incorrect left trimmed value returned", "A", Util.leftTrim("A"));
		Assert.assertEquals("Incorrect left trimmed value returned", "A ", Util.leftTrim("A "));
		Assert.assertEquals("Incorrect left trimmed value returned", "A ", Util.leftTrim(" A "));
		Assert.assertEquals("Incorrect left trimmed value returned", "A  ", Util.leftTrim(" A  "));
		Assert.assertEquals("Incorrect left trimmed value returned", "A  ", Util.leftTrim("  A  "));
		Assert.
				assertEquals("Incorrect left trimmed value returned", "A B C", Util.
						leftTrim("A B C"));
		Assert.assertEquals("Incorrect left trimmed value returned", "A B C", Util.leftTrim(
				"  A B C"));
		Assert.assertEquals("Incorrect left trimmed value returned", "A B C ", Util.leftTrim(
				"  A B C "));
		Assert.assertEquals("Incorrect left trimmed value returned", "A B C  ", Util.leftTrim(
				"  A B C  "));
	}

}
