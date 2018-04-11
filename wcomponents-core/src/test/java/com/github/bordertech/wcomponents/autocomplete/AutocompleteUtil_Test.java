package com.github.bordertech.wcomponents.autocomplete;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests of {@link AutocompleteUtil}.
 * @author Mark Reeves
 */
public class AutocompleteUtil_Test {

	@Test
	public void testGetCombinedAutocomplete() {
		String inOne = "foo";
		String inTwo = "bar";
		String expected = "foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne, inTwo));
	}

	@Test
	public void testGetCombinedAutocompleteOneInput() {
		String inOne = "foo";
		String expected = "foo";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne));
	}

	@Test
	public void testGetCombinedAutocompleteTrims() {
		String inOne = " foo";
		String inTwo = "bar ";
		String expected = "foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedAutocomplete(inOne, inTwo));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmpty() {
		String inOne = "foo";
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(inOne));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyNullIn() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparators() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete("", ""));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparatorsAndNullIn() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null, ""));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsNullIfEmptyWithSeparatorsAndSpace() {
		Assert.assertNull(AutocompleteUtil.getCombinedAutocomplete(null, "", null, " "));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffIn() {
		Assert.assertEquals(AutocompleteUtil.OFF, AutocompleteUtil.getCombinedAutocomplete(AutocompleteUtil.OFF, "anything"));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInVarArgs() {
		Assert.assertEquals(AutocompleteUtil.OFF, AutocompleteUtil.getCombinedAutocomplete("anything", AutocompleteUtil.OFF));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInCaseInsensitive() {
		Assert.assertEquals(AutocompleteUtil.OFF, AutocompleteUtil.getCombinedAutocomplete("OFF", "anything"));
	}

	@Test
	public void testGetCombinedAutocompleteReturnsOffIfOffInVarArgsCaseInsensitive() {
		Assert.assertEquals(AutocompleteUtil.OFF, AutocompleteUtil.getCombinedAutocomplete("anything", "oFf"));
	}

	@Test
	public void testGetNamedSection() {
		String in = "foo";
		String expected = "section-foo";

		Assert.assertEquals("Unexpected output", expected, AutocompleteUtil.getNamedSection(in));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetNamedSectionNullName() {
		AutocompleteUtil.getNamedSection(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetNamedSectionEmptyName() {
		AutocompleteUtil.getNamedSection("");
	}

	@Test
	public void testGetCombinedForSection() {
		String sectionName = "foo";
		String otherName = "bar";
		String expected = "section-foo bar";
		Assert.assertEquals(expected, AutocompleteUtil.getCombinedForSection(sectionName, otherName));
	}
}
