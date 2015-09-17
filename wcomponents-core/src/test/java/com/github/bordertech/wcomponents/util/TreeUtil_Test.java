package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.UIContextImpl;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link TreeUtil} component.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TreeUtil_Test {

	private WApplication root;
	private WContainer containerChild;
	private WComponent simpleChild;
	private WText repeatedComponent;
	private WRepeater repeaterChild;
	private WComponent grandChild;
	private WCardManager cardManager;
	private WText card1;
	private WText card2;

	@Before
	public void initTree() {
		root = new WApplication();
		containerChild = new WContainer();
		simpleChild = new WTextField();
		repeatedComponent = new WText();
		repeaterChild = new WRepeater(repeatedComponent);
		grandChild = new WTextArea();
		cardManager = new WCardManager();
		card1 = new WText();
		card2 = new WText();

		root.add(containerChild);
		root.add(simpleChild);
		root.add(repeaterChild);
		root.add(cardManager);
		containerChild.add(grandChild);
		cardManager.add(card1);
		cardManager.add(card2);
		root.setLocked(true);

		setActiveContext(new UIContextImpl());
		repeaterChild.setData(Arrays.asList(new String[]{"1", "2", "3"}));
	}

	@Test
	public void testCollateVisibles() {
		// Simple case - everything visible.
		List<ComponentWithContext> visibles = TreeUtil.collateVisibles(root);
		UIContext uic = UIContextHolder.getCurrent();

		Assert.assertEquals("Incorrect number of visible components", 10, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain containerChild", isInList(visibles, containerChild,
				uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain repeaterChild",
				isInList(visibles, repeaterChild, uic));
		Assert.assertTrue("List should contain grandChild", isInList(visibles, grandChild, uic));
		Assert.assertTrue("List should contain repeatedComponent row 1", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("1")));
		Assert.assertTrue("List should contain repeatedComponent row 2", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("2")));
		Assert.assertTrue("List should contain repeatedComponent row 3", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("3")));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card1, uic));

		// Make grandChild invisible - should only remove it
		grandChild.setVisible(false);
		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 9, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain containerChild", isInList(visibles, containerChild,
				uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain repeaterChild",
				isInList(visibles, repeaterChild, uic));
		Assert.assertTrue("List should contain repeatedComponent row 1", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("1")));
		Assert.assertTrue("List should contain repeatedComponent row 2", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("2")));
		Assert.assertTrue("List should contain repeatedComponent row 3", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("3")));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card1, uic));
		grandChild.setVisible(true);

		// Make containerChild invisible - should remove it and grandChild
		containerChild.setVisible(false);
		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 8, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain repeaterChild",
				isInList(visibles, repeaterChild, uic));
		Assert.assertTrue("List should contain repeatedComponent row 1", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("1")));
		Assert.assertTrue("List should contain repeatedComponent row 2", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("2")));
		Assert.assertTrue("List should contain repeatedComponent row 3", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("3")));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card1, uic));
		containerChild.setVisible(true);

		// Make repeaterChild invisible - should remove it and its 3 rows
		repeaterChild.setVisible(false);
		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 6, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain containerChild", isInList(visibles, containerChild,
				uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain grandChild", isInList(visibles, grandChild, uic));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card1, uic));
		repeaterChild.setVisible(true);

		// Make 2nd row of repeater invisible
		setActiveContext(repeaterChild.getRowContext("2"));
		repeatedComponent.setVisible(false);
		setActiveContext(uic);

		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 9, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain containerChild", isInList(visibles, containerChild,
				uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain repeaterChild",
				isInList(visibles, repeaterChild, uic));
		Assert.assertTrue("List should contain grandChild", isInList(visibles, grandChild, uic));
		Assert.assertTrue("List should contain repeatedComponent row 1", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("1")));
		Assert.assertTrue("List should contain repeatedComponent row 3", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("3")));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card1, uic));

		// Switch active cards
		cardManager.makeVisible(card2);
		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 9, visibles.size());
		Assert.assertTrue("List should contain root", isInList(visibles, root, uic));
		Assert.assertTrue("List should contain containerChild", isInList(visibles, containerChild,
				uic));
		Assert.assertTrue("List should contain simpleChild", isInList(visibles, simpleChild, uic));
		Assert.assertTrue("List should contain repeaterChild",
				isInList(visibles, repeaterChild, uic));
		Assert.assertTrue("List should contain grandChild", isInList(visibles, grandChild, uic));
		Assert.assertTrue("List should contain repeatedComponent row 1", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("1")));
		Assert.assertTrue("List should contain repeatedComponent row 3", isInList(visibles,
				repeatedComponent, repeaterChild.getRowContext("3")));
		Assert.assertTrue("List should contain card manager", isInList(visibles, cardManager, uic));
		Assert.assertTrue("List should contain visible card", isInList(visibles, card2, uic));

		// Make root invisible - should be an empty list
		root.setVisible(false);
		visibles = TreeUtil.collateVisibles(root);
		Assert.assertEquals("Incorrect number of visible components", 0, visibles.size());
	}

	/**
	 * Indicates whether the list contains the given component and context.
	 *
	 * @param components the list of components to check.
	 * @param component the component to search for.
	 * @param uic the component context to search for.
	 * @return true if the component is in the list with the given context, false otherwise.
	 */
	private boolean isInList(final List<ComponentWithContext> components, final WComponent component,
			final UIContext uic) {
		for (ComponentWithContext comp : components) {
			if (comp.getComponent() == component && comp.getContext() == uic) {
				return true;
			}
		}

		return false;
	}

	@Test
	public void testGetRoot() {
		UIContext uic = UIContextHolder.getCurrent();
		Assert.
				assertSame("Incorrect root node returned for root", root, TreeUtil.
						getRoot(uic, root));
		Assert.assertSame("Incorrect root node returned for containerChild", root, TreeUtil.getRoot(
				uic, containerChild));
		Assert.assertSame("Incorrect root node returned for simpleChild", root, TreeUtil.
				getRoot(uic, simpleChild));
		Assert.assertSame("Incorrect root node returned for repeaterChild", root, TreeUtil.getRoot(
				uic, repeaterChild));
		Assert.assertSame("Incorrect root node returned for grandChild", root, TreeUtil.getRoot(uic,
				grandChild));

		UIContext row2Context = repeaterChild.getRowContext("2");
		Assert.assertSame("Incorrect root node returned for repeatedComponent row 2", root,
				TreeUtil.getRoot(row2Context, repeatedComponent));
	}

	@Test
	public void testGetComponentWithId() {
		Assert.assertSame("Incorrect component returned for root", root, TreeUtil.
				getComponentWithId(root, root.getId()));
		Assert.assertSame("Incorrect component returned for containerChild", containerChild,
				TreeUtil.getComponentWithId(root, containerChild.getId()));
		Assert.assertSame("Incorrect component returned for simpleChild", simpleChild, TreeUtil.
				getComponentWithId(root, simpleChild.getId()));
		Assert.assertSame("Incorrect component returned for repeaterChild", repeaterChild, TreeUtil.
				getComponentWithId(root, repeaterChild.getId()));
		Assert.assertSame("Incorrect component returned for grandChild", grandChild, TreeUtil.
				getComponentWithId(root, grandChild.getId()));

		UIContext row2Context = repeaterChild.getRowContext("2");
		setActiveContext(row2Context);
		Assert.assertSame("Incorrect component returned for repeatedComponent row 2",
				repeatedComponent, TreeUtil.getComponentWithId(root, repeatedComponent.getId()));
	}

	@Test
	public void testGetContextForId() {
		UIContext uic = UIContextHolder.getCurrent();
		Assert.assertSame("Incorrect context returned for root", uic, TreeUtil.getContextForId(root,
				root.getId()));
		Assert.assertSame("Incorrect context returned for containerChild", uic, TreeUtil.
				getContextForId(root, containerChild.getId()));
		Assert.assertSame("Incorrect context returned for simpleChild", uic, TreeUtil.
				getContextForId(root, simpleChild.getId()));
		Assert.assertSame("Incorrect context returned for repeaterChild", uic, TreeUtil.
				getContextForId(root, repeaterChild.getId()));
		Assert.assertSame("Incorrect context returned for grandChild", uic, TreeUtil.
				getContextForId(root, grandChild.getId()));

		UIContext row2Context = repeaterChild.getRowContext("2");
		setActiveContext(row2Context);

		Assert.assertSame("Incorrect context returned for repeatedComponent row 2", row2Context,
				TreeUtil.getContextForId(root, repeatedComponent.getId()));
	}

	@Test
	public void testFindWComponent() {
		UIContext uic = UIContextHolder.getCurrent();
		ComponentWithContext result = TreeUtil.findWComponent(root, new String[]{"WApplication"});
		Assert.assertSame("Incorrect component returned for find WApplication", root, result.
				getComponent());
		Assert.assertSame("Incorrect context returned for find WApplication", uic, result.
				getContext());

		result = TreeUtil.findWComponent(root, new String[]{"WTextArea"});
		Assert.assertSame("Incorrect component returned for find WTextArea", grandChild, result.
				getComponent());
		Assert.assertSame("Incorrect context returned for find WTextArea", uic, result.getContext());

		result = TreeUtil.findWComponent(root, new String[]{"WContainer", "WTextArea"});
		Assert.assertSame("Incorrect component returned for find WContainer/WTextArea", grandChild,
				result.getComponent());
		Assert.assertSame("Incorrect context returned for find WContainer/WTextArea", uic, result.
				getContext());

		result = TreeUtil.findWComponent(root, new String[]{"WText[1]"});
		Assert.assertSame("Incorrect component returned for find WText[1]", repeatedComponent,
				result.getComponent());
		UIContext row2Context = repeaterChild.getRowContext("2");
		Assert.assertSame("Incorrect context returned for find WText[1]", row2Context, result.
				getContext());

		result = TreeUtil.findWComponent(root, new String[]{"WApplication", "WText[1]"});
		Assert.assertSame("Incorrect component returned for find WApplication/WText[1]",
				repeatedComponent, result.getComponent());
		row2Context = repeaterChild.getRowContext("2");
		Assert.assertSame("Incorrect context returned for find WApplication/WText[1]", row2Context,
				result.getContext());

		result = TreeUtil.findWComponent(root, new String[]{"WContainer", "WRepeater"});
		Assert.assertNull("Should not have a result for an invalid path", result);
	}

	/**
	 * Sets the given context to be the active one.
	 *
	 * @param uic the context to set as active.
	 */
	private void setActiveContext(final UIContext uic) {
		resetContext();
		UIContextHolder.pushContext(uic);
	}

	/**
	 * Resets the UIContext stack after each test method.
	 */
	@After
	public void resetContext() {
		UIContextHolder.reset();
	}
}
