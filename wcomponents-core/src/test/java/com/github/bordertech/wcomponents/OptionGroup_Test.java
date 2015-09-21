package com.github.bordertech.wcomponents;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * OptionGroup_Test - Unit tests for {@link OptionGroup}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class OptionGroup_Test extends AbstractWComponentTestCase {

	@Test
	public void testOptionsAccessors() {
		OptionGroup group = new OptionGroup();
		Assert.assertNull("Default options should be null", group.getOptions());

		List<String> options = Arrays.asList(new String[]{"1", "2", "3"});
		group.setOptions(options);
		Assert.assertEquals("Incorrect options", options, group.getOptions());

		group = new OptionGroup(null, options);
		Assert.assertEquals("Incorrect options", options, group.getOptions());
	}

	@Test
	public void testDescriptionAccessors() {
		OptionGroup group = new OptionGroup();
		Assert.assertNull("Default description should be null", group.getDesc());

		String description = "OptionGroup_Test.testDescriptionAccessors.description";
		group.setDescription(description);
		Assert.assertEquals("Incorrect description", description, group.getDesc());

		group = new OptionGroup(description);
		Assert.assertEquals("Incorrect description", description, group.getDesc());
	}
}
