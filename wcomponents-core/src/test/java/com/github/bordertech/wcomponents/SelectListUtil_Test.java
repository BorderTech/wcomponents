package com.github.bordertech.wcomponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * SelectListUtil_Test - unit tests for {@link SelectListUtil}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class SelectListUtil_Test {

	@Test
	public void testContainsOptionAndMatching() {
		String optionA = "A";
		String optionB = "B";
		String optionC = "C";
		List<String> options = Arrays.asList(new String[]{optionA, optionB, optionC});

		Assert.assertTrue("OptionA should be in options", SelectListUtil.containsOption(options,
				optionA));
		Assert.assertFalse("Null should not be an option", SelectListUtil.containsOption(options,
				null));
		Assert.assertFalse("Boolean TRUE is not an option", SelectListUtil.containsOption(options,
				Boolean.TRUE));

		// With Matching
		Assert.assertTrue("OptionA should be in options", SelectListUtil.containsOptionWithMatching(
				options, optionA));
		Assert.assertFalse("Null should not an option", SelectListUtil.containsOptionWithMatching(
				options, null));
		Assert.assertFalse("Boolean TRUE should not be an option",
				SelectListUtil.containsOptionWithMatching(options, Boolean.TRUE));

		// With Matching
		Assert.assertEquals("OptionA should be in options", optionA,
				SelectListUtil.getOptionWithMatching(options, optionA));
		Assert.assertNull("Invalid option should return null", SelectListUtil.getOptionWithMatching(
				options, "Z"));
	}

	@Test
	public void testContainsOptionWithGroupsAndMatching() {
		String optionA = "A";
		String optionB = "B";
		String optionC = "C";
		String optionD = null;

		// Setup Groups
		OptionGroup group1 = new OptionGroup("Group1", Arrays.
				asList(new String[]{optionA, "b", "c"}));
		OptionGroup group2 = new OptionGroup("Group2", Arrays.
				asList(new String[]{"x", optionB, "z"}));
		List<Object> options = Arrays.asList(
				new Object[]{optionC, group1, "Two", group2, optionD, "Three"});

		Assert.assertTrue("OptionA should be in options", SelectListUtil.containsOption(options,
				optionA));
		Assert.assertTrue("OptionB should be in options", SelectListUtil.containsOption(options,
				optionB));
		Assert.assertTrue("OptionC should be in options", SelectListUtil.containsOption(options,
				optionC));
		Assert.assertTrue("OptionD should be in options", SelectListUtil.containsOption(options,
				optionD));
		Assert.assertFalse("Boolean TRUE is not an option", SelectListUtil.containsOption(options,
				Boolean.TRUE));

		// With Matching
		Assert.assertTrue("OptionA should be in options", SelectListUtil.containsOptionWithMatching(
				options, optionA));
		Assert.assertTrue("OptionB should be in options", SelectListUtil.containsOptionWithMatching(
				options, optionB));
		Assert.assertTrue("OptionC should be in options", SelectListUtil.containsOptionWithMatching(
				options, optionC));
		Assert.assertTrue("OptionD should be in options", SelectListUtil.containsOptionWithMatching(
				options, optionD));
		Assert.assertFalse("Boolean TRUE is not an option",
				SelectListUtil.containsOptionWithMatching(options, Boolean.TRUE));

	}

	@Test
	public void testGetFirstOption() {
		String optionA = "A";
		String optionB = "B";
		List<String> options = Arrays.asList(new String[]{optionA, optionB});
		Assert.assertEquals("First option should be optionA", optionA, SelectListUtil.
				getFirstOption(options));

		Assert.assertNull("First option should be null for null options", SelectListUtil.
				getFirstOption(null));
	}

	@Test
	public void testGetFirstOptionFirstNull() {
		String optionA = null;
		String optionB = "B";
		List<String> options = Arrays.asList(new String[]{optionA, optionB});
		Assert.assertEquals("First option should be optionA", optionA, SelectListUtil.
				getFirstOption(options));
	}

	@Test
	public void testGetFirstOptionWithGroups() {
		String optionA = "A";
		// Setup Groups
		OptionGroup group1 = new OptionGroup("Group1", null);
		OptionGroup group2 = new OptionGroup("Group2", Arrays.
				asList(new String[]{optionA, "B", "C"}));
		List<Object> options = Arrays.asList(new Object[]{group1, group2, "D"});
		Assert.assertEquals("First option should be optionA", optionA, SelectListUtil.
				getFirstOption(options));
	}

	@Test
	public void testGetFirstOptionWithGroupsFirstNull() {
		// First option Null
		String optionA = null;
		// Setup Groups
		OptionGroup group1 = new OptionGroup("Group1", null);
		OptionGroup group2 = new OptionGroup("Group2", Arrays.
				asList(new String[]{optionA, "B", "C"}));
		List<Object> options = Arrays.asList(new Object[]{group1, group2, "D"});
		Assert.assertEquals("First option should be optionA", optionA, SelectListUtil.
				getFirstOption(options));
	}

	@Test
	public void testGetOptionWithMatching() {
		Object objectStringA = new Object() {
			@Override
			public String toString() {
				return "A";
			}
		};

		Object objectStringC = new Object() {
			@Override
			public String toString() {
				return "C";
			}
		};

		String optionA = "A";
		String optionB = "B";
		String optionC = "C";
		String optionD = null;

		// Setup Groups
		OptionGroup group1 = new OptionGroup("Group1", Arrays.
				asList(new String[]{optionA, "b", "c"}));
		OptionGroup group2 = new OptionGroup("Group2", Arrays.
				asList(new String[]{"x", optionB, "z"}));
		List<Object> options = Arrays.asList(
				new Object[]{optionC, group1, "Two", group2, optionD, "Three"});

		// Equal Options
		Assert.assertEquals("Match should be optionA", optionA, SelectListUtil.
				getOptionWithMatching(options, optionA));
		Assert.assertEquals("Match should be optionB", optionB, SelectListUtil.
				getOptionWithMatching(options, optionB));
		Assert.assertEquals("Match should be optionC", optionC, SelectListUtil.
				getOptionWithMatching(options, optionC));
		Assert.assertEquals("Match should be optionD", optionD, SelectListUtil.
				getOptionWithMatching(options, optionD));
		Assert.assertNull("Match should be null for invalid option", SelectListUtil.
				getOptionWithMatching(options, "INVALID OPTION"));

		// Legacy Matching
		Assert.assertEquals("Match should be optionA for legacy match", optionA, SelectListUtil.
				getOptionWithMatching(options, objectStringA));
		Assert.assertEquals("Match should be optionC for legacy match", optionC, SelectListUtil.
				getOptionWithMatching(options, objectStringC));
	}

	@Test
	public void testGetOptionWithMatchingNullCode() {
		List<MyOption> options = Arrays.asList(new MyOption(null, null));
		Assert.assertNull("Should be null for option with null code",
				SelectListUtil.getOptionWithMatching(options, "A"));
	}

	@Test
	public void testGetOptionWithMatchingLegacyNullValue() {
		List<MyOption> options = new ArrayList<>();
		options.add(null);
		Assert.assertNull("Should be null for option with null code",
				SelectListUtil.getOptionWithMatching(options, "A"));
	}

	/**
	 * Test class for Option.
	 */
	private static final class MyOption implements Option {

		/**
		 * Option code value.
		 */
		private final String code;
		/**
		 * Option desc value.
		 */
		private final String desc;

		/**
		 * Construct option.
		 *
		 * @param code the option code value.
		 * @param desc the option description value.
		 */
		private MyOption(final String code, final String desc) {
			this.code = code;
			this.desc = desc;
		}

		/**
		 * @return the option code
		 */
		@Override
		public String getCode() {
			return code;
		}

		/**
		 * @return the option description
		 */
		@Override
		public String getDesc() {
			return desc;
		}
	}

}
