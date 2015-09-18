package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.AbstractWComponentTestCase;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 * WFieldErrorIndicator_Test - unit tests for {@link WFieldErrorIndicator}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WFieldErrorIndicator_Test extends AbstractWComponentTestCase {

	@Test
	public void testConstructor() {
		WTextField component = new WTextField();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(component);

		Assert.assertEquals("Incorrect indicator type",
				AbstractWFieldIndicator.FieldIndicatorType.ERROR, indicator
				.getFieldIndicatorType());
		Assert.assertEquals("Incorrect releated field", component, indicator.getRelatedField());

		Assert.assertEquals("Incorrect releated field id", component.getId(), indicator.
				getRelatedFieldId());
	}

	@Test
	public void testIsDefaultState() {
		WPanel root = new WPanel();
		WTextField component = new WTextField();
		WFieldErrorIndicator indicator = new WFieldErrorIndicator(component);

		root.add(indicator);
		root.add(component);

		root.setLocked(true);
		setActiveContext(createUIContext());
		Assert.assertTrue("Should be in default state by default", indicator.isDefaultState());

		List<Diagnostic> diags = new ArrayList<>();
		root.validate(diags);
		root.showErrorIndicators(diags);

		Assert.assertTrue("Should be in default if there are no errors", indicator.isDefaultState());

		// Add an error by making the field mandatory
		root.reset();
		component.setMandatory(true);
		root.validate(diags);
		root.showErrorIndicators(diags);

		Assert.assertFalse("Should not be in default if there are errors", indicator.
				isDefaultState());

		root.reset();
		Assert.assertTrue("Should be in default after reset", indicator.isDefaultState());
	}
}
