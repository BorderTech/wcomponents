package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.TestLookupTable.DayOfWeekTable;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WAbbrText}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAbbrText_Test extends AbstractWComponentTestCase {

	@Test
	public void testDefaultConstructor() {
		WAbbrText abbr = new WAbbrText();
		Assert.assertNull("Default text should be null", abbr.getText());
		Assert.assertNull("Default abbrtext should be null", abbr.getToolTip());
	}

	@Test
	public void testTextConstructor() {
		String myText = "WAbbrText_Test.MyText";

		WAbbrText abbr = new WAbbrText(myText);
		Assert.assertEquals("Incorrect default text", myText, abbr.getText());
		Assert.assertNull("Default abbrtext should be null", abbr.getToolTip());
	}

	@Test
	public void testTextDescriptionConstructor() {
		String myText = "WAbbrText_Test.MyText";
		String description = "WAbbrText_Test.MyAbbr";

		WAbbrText abbr = new WAbbrText(myText, description);
		Assert.assertEquals("Incorrect default text", myText, abbr.getText());
		Assert.assertEquals("Incorrect default abbr text", description, abbr.getToolTip());
	}

	/** @deprecated */
	@Test
	public void testGetSetAbbrText() {
		assertAccessorsCorrect(new WAbbrText(), "abbrText", null, "toolTip 1", "toolTip 2");
	}

	@Test
	public void testSetTextWithDesc() {
		WAbbrText abbr = new WAbbrText();
		List<Object> data = new TestLookupTable().getTable(DayOfWeekTable.class);
		TestLookupTable.TableEntry entry = (TestLookupTable.TableEntry) data.get(0);

		abbr.setTextWithDesc(entry);
		Assert.assertEquals("Incorrect text", entry.getDesc(), abbr.getText());
		Assert.assertEquals("Incorrect abbr text", entry.getCode(), abbr.getToolTip());
	}

	@Test
	public void testSetTextWithCode() {
		WAbbrText abbr = new WAbbrText();
		List<Object> data = new TestLookupTable().getTable(DayOfWeekTable.class);
		TestLookupTable.TableEntry entry = (TestLookupTable.TableEntry) data.get(0);

		abbr.setTextWithCode(entry);
		Assert.assertEquals("Incorrect text", entry.getCode(), abbr.getText());
		Assert.assertEquals("Incorrect abbr text", entry.getDesc(), abbr.getToolTip());
	}
}
