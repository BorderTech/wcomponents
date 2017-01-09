package com.github.bordertech.wcomponents.layout;

import com.github.bordertech.wcomponents.util.GapSizeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * FlowLayout_Test - unit tests for {@link FlowLayout}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
public class ListLayout_Test {

	/**
	 * A reusable gap value.
	 */
	private static final GapSizeUtil.Size GAP = GapSizeUtil.Size.SMALL;

	/**
	 * A different reusable gap value. This is used to differentiate the (now deprecated) hgap and vgap properties.
	 */
	private static final GapSizeUtil.Size BIG_GAP = GapSizeUtil.Size.LARGE;


	@Test
	public void testDefaultConstructor() {
		ListLayout list = new ListLayout();
		Assert.assertEquals("Default type should be STACKED", ListLayout.Type.STACKED, list.getType());
		Assert.assertEquals("Default alignment should be LEFT", ListLayout.Alignment.LEFT, list.getAlignment());
		Assert.assertEquals("Default separator should be DOT", ListLayout.Separator.DOT, list.getSeparator());
		Assert.assertNull("Default gap should be null", list.getGap());
		Assert.assertFalse("Default ordered should be false", list.isOrdered());
	}

	@Test
	public void testTypeConstructor() {
		for (ListLayout.Type t : ListLayout.Type.values()) {
			ListLayout list = new ListLayout(t);
			Assert.assertEquals("Incorrect type", t, list.getType());
			Assert.assertEquals("Default alignment should be LEFT", ListLayout.Alignment.LEFT, list.getAlignment());
		Assert.assertEquals("Default separator should be DOT", ListLayout.Separator.DOT, list.getSeparator());
		Assert.assertNull("Default gap should be null", list.getGap());
			Assert.assertFalse("Default ordered should be false", list.isOrdered());
		}
	}

	@Test
	public void testOrderedConstructor() {
		ListLayout list = new ListLayout(true);
		Assert.assertTrue("ordered should be true", list.isOrdered());
		Assert.assertEquals("Default type should be STACKED", ListLayout.Type.STACKED, list.getType());
		Assert.assertEquals("Default alignment should be LEFT", ListLayout.Alignment.LEFT, list.getAlignment());
		Assert.assertEquals("Default separator should be DOT", ListLayout.Separator.DOT, list.getSeparator());
		Assert.assertNull("Default gap should be null", list.getGap());
	}

	@Test
	public void testTypeAlignmentConstructor() {
		ListLayout list;
		for (ListLayout.Type t : ListLayout.Type.values()) {
			for (ListLayout.Alignment a : ListLayout.Alignment.values()) {
				list = new ListLayout(t, a);
				Assert.assertEquals("Incorrect type", t, list.getType());
				Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
			}
		}
	}

	@Test
	public void testTypeAlignmentSeparatorOrderedConstructor() {
		ListLayout list;
		for (ListLayout.Type t : ListLayout.Type.values()) {
			for (ListLayout.Alignment a : ListLayout.Alignment.values()) {
				for (ListLayout.Separator s : ListLayout.Separator.values()) {
					list = new ListLayout(t, a, s, true);
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertTrue("ordered should be true", list.isOrdered());
					list = new ListLayout(t, a, s, false);
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertFalse("ordered should be false", list.isOrdered());
				}
			}
		}
	}

	@Test
	public void testTypeAlignmentSeparatorOrderedGapConstructor() {
		ListLayout list;
		for (ListLayout.Type t : ListLayout.Type.values()) {
			for (ListLayout.Alignment a : ListLayout.Alignment.values()) {
				for (ListLayout.Separator s : ListLayout.Separator.values()) {
					list = new ListLayout(t, a, s, true, GAP);
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertTrue("ordered should be true", list.isOrdered());
					Assert.assertEquals("Incorrect gap", GAP, list.getGap());
					list = new ListLayout(t, a, s, false, GAP);
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertFalse("ordered should be false", list.isOrdered());
					Assert.assertEquals("Incorrect gap", GAP, list.getGap());
				}
			}
		}
	}

	// Tests of deprecated constructor
	@Test
	public void testHgapVgapConstructor() {
		ListLayout list;
		boolean isFlat;
		for (ListLayout.Type t : ListLayout.Type.values()) {
			for (ListLayout.Alignment a : ListLayout.Alignment.values()) {
				for (ListLayout.Separator s : ListLayout.Separator.values()) {
					isFlat = t == ListLayout.Type.FLAT;
					list = new ListLayout(t, a, s, true, GapSizeUtil.sizeToInt(GAP), GapSizeUtil.sizeToInt(BIG_GAP));
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertTrue("ordered should be true", list.isOrdered());
					Assert.assertEquals("Incorrect gap", isFlat ? GAP : BIG_GAP, list.getGap());

					list = new ListLayout(t, a, s, false, GapSizeUtil.sizeToInt(GAP), GapSizeUtil.sizeToInt(BIG_GAP));
					Assert.assertEquals("Incorrect type", t, list.getType());
					Assert.assertEquals("Incorrect alignment", a, list.getAlignment());
					Assert.assertEquals("Incorrect separator", s, list.getSeparator());
					Assert.assertFalse("ordered should be false", list.isOrdered());
					Assert.assertEquals("Incorrect gap", isFlat ? GAP : BIG_GAP, list.getGap());
				}
			}
		}
	}

	// Test deprecated accessors.
	// Type.FLAT  should have hgap but 0 vgap.
	// other types should have vgap but 0 hgap.
	@Test
	public void testHGapVGapAccessors() {
		ListLayout list;
		boolean isFlat;

		for (ListLayout.Type t : ListLayout.Type.values()) {
			isFlat = t == ListLayout.Type.FLAT;
			list = new ListLayout(t, ListLayout.Alignment.LEFT, ListLayout.Separator.NONE, true, GapSizeUtil.sizeToInt(GAP), GapSizeUtil.sizeToInt(BIG_GAP));
			Assert.assertEquals("Incorrect horizontal gap", isFlat ? GapSizeUtil.sizeToInt(GAP) : 0, list.getHgap());
			Assert.assertEquals("Incorrect vertical gap", isFlat ? 0 : GapSizeUtil.sizeToInt(BIG_GAP), list.getVgap());
		}
	}
}
