package com.github.bordertech.wcomponents.util;

import junit.framework.Assert;
import org.junit.Test;

/**
 * AbstractComparator_Test - JUnit tests for {@link AbstractComparator}.
 *
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class AbstractComparator_Test {

	@Test
	public void testCompare() {
		// A trivial AbstractComparator implementation
		AbstractComparator comparator = new AbstractComparator() {
			@Override
			protected Comparable getComparable(final Object obj) {
				return (Comparable) obj;
			}
		};

		Assert.assertEquals("Nulls should be equal", 0, comparator.compare(null, null));
		Assert.assertTrue("Null should be less than non-null", comparator.compare(null, "x") < 0);
		Assert.assertTrue("Null should be less than non-null", comparator.compare("x", null) > 0);
		Assert.assertTrue("x is less than y", comparator.compare("x", "y") < 0);
		Assert.assertTrue("x is less than y", comparator.compare("y", "x") > 0);
		Assert.assertEquals("Equal objects should be equal", 0, comparator.compare("x", "x"));
	}
}
