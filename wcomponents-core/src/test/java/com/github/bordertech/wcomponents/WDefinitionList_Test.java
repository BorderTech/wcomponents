package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.WDefinitionList.Type;
import org.junit.Assert;
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
		assertAccessorsCorrect(new WDefinitionList(), WDefinitionList::getType, WDefinitionList::setType,
			Type.NORMAL, Type.FLAT, Type.STACKED);
	}

	@Test
	public void testMarginAccessors() {
		assertAccessorsCorrect(new WDefinitionList(), WDefinitionList::getMargin, WDefinitionList::setMargin,
			null, new Margin(Size.SMALL), new Margin(Size.MEDIUM));
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
		Assert.assertTrue("There should not be any terms", list.getTerms().isEmpty());

		list.addTerm(term1);
		Assert.assertEquals("There should have been 1 term added", 1, list.getTerms().size());
		Assert.assertEquals("The first term's name is incorrect", term1, list.getTerms().get(0).getFirst());
		Assert.assertTrue("The first term should not have any components", list.getTerms().get(0).getSecond().isEmpty());

		// Test addition of term with multiple components
		list.addTerm(term2, term2data1, term2data2);
		list.addTerm(term2, term2data3);

		Assert.assertEquals("There should have been 2 terms added", 2, list.getTerms().size());
		Assert.assertEquals("The second term's name is incorrect", term2, list.getTerms().get(1).getFirst());
		Assert.assertEquals("The second term should have 3 components", 3, list.getTerms().get(1).getSecond().size());

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
		Assert.assertEquals("There should have been 2 terms added", 2, list.getTerms().size());

		list.removeTerm(term2);
		Assert.assertEquals("There should be only 1 term remaining", 1, list.getTerms().size());
		
		Assert.assertEquals("Incorrect tag for component 1", term2, term2data1.getTag());
		Assert.assertEquals("Incorrect tag for component 2", term2, term2data2.getTag());
	}

	@Test
	public void testRemoveComponent() {
		WDefinitionList list = new WDefinitionList();

		String term1 = "WDefinitionList_Test.testRemoveComponent.term1";

		WComponent data1 = new DefaultWComponent();
		WComponent data2 = new DefaultWComponent();

		list.addTerm(term1, data1, data2);
		Assert.assertEquals("There should have been 1 terms added", 1, list.getTerms().size());
		Assert.assertEquals("There should have been 2 components added", 2, list.getTerms().get(0).getSecond().size());

		list.remove(data2);
		
		Assert.assertEquals("There should be only 1 component remaining", 1, list.getTerms().get(0).getSecond().size());
	}
}
