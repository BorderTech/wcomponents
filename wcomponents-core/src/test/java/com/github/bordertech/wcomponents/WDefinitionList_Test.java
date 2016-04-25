package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WDefinitionList.Type;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WDefinitionList_Test - unit tests for {@link WDefinitionList}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDefinitionList_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructors() {
		WDefinitionList list = new WDefinitionList();
		Assert.assertEquals("Incorrect default type", WDefinitionList.Type.NORMAL, list.getType());

		list = new WDefinitionList(WDefinitionList.Type.FLAT);
		Assert.assertEquals("Incorrect type after construction", WDefinitionList.Type.FLAT, list.
				getType());
	}

	@Test
	public void testTypeAccessors() {
		assertAccessorsCorrect(new WDefinitionList(), "type", Type.NORMAL, Type.FLAT, Type.STACKED);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WDefinitionList(), "margin", null, new Margin(1), new Margin(2));
	}

	@Test
	public void testAddTerm() {
		WDefinitionList list = new WDefinitionList();

		String term1 = "WDefinitionList_Test.testAddTerm.term1";
		String term2 = "WDefinitionList_Test.testAddTerm.term2";

		WComponent term2data1 = new DefaultWComponent();
		WComponent term2data2 = new DefaultWComponent();
		WComponent term2data3 = new DefaultWComponent();

		// Test addition of term with no component
		list.addTerm(term1);
		Container termContainer = (Container) list.getChildAt(0);
		Assert.assertEquals("Term1 should have been added", 1, termContainer.getChildCount());
		Assert.assertEquals("Incorrect value for Term1", term1, termContainer.getChildAt(0).getTag());

		// Test addition of term with multiple components
		list.addTerm(term2, term2data1, term2data2);
		list.addTerm(term2, term2data3);

		Assert.assertEquals("Incorrect term for component 1", term2, term2data1.getTag());
		Assert.assertEquals("Incorrect term for component 2", term2, term2data2.getTag());
		Assert.assertEquals("Incorrect term for component 3", term2, term2data3.getTag());
	}

	@Test
	public void testRemoveTerm() {
		WDefinitionList list = new WDefinitionList();

		String term1 = "WDefinitionList_Test.testRemoveTerm.term1";
		String term2 = "WDefinitionList_Test.testRemoveTerm.term2";

		WComponent term2data1 = new DefaultWComponent();
		WComponent term2data2 = new DefaultWComponent();

		list.addTerm(term1);
		list.addTerm(term2, term2data1, term2data2);
		Container termContainer = (Container) list.getChildAt(0);

		Assert.assertEquals("Incorrect term data", 3, termContainer.getChildren().size());

		list.removeTerm(term2);
		Assert.assertEquals("Incorrect term data", 1, termContainer.getChildCount());
		Assert.assertEquals("Incorrect value for Term1", term1, termContainer.getChildAt(0).getTag());
	}
}
