package com.github.bordertech.wcomponents.validation;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WTextField;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ValidatingAction_Test - unit tests for {@link ValidatingAction}.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ValidatingAction_Test {

	/**
	 * A component to test against.
	 */
	private MyComponent componentToValidate;
	/**
	 * A concrete ValidatingAction implementation to test.
	 */
	private MyValidatingAction validatingAction;

	@Before
	public void setUp() {
		componentToValidate = new MyComponent();
		validatingAction = new MyValidatingAction(componentToValidate);
	}

	@Test
	public void testExecuteOnError() {
		componentToValidate.setErrorLevel(Diagnostic.ERROR);

		Assert.assertEquals("Incorrect validation component returned.", componentToValidate,
				validatingAction.getComponentToValidate());

		validatingAction.execute(new ActionEvent("source", "command"));
		Assert.assertTrue("Should have called executeOnError",
				validatingAction.executeOnErrorExecuted);
		Assert.assertFalse("Should not have called executeOnValid",
				validatingAction.executeOnValidExecuted);
	}

	@Test
	public void testExecuteOnValid() {
		// Test with no diagnostics
		Assert.assertEquals("Incorrect validation component returned.", componentToValidate,
				validatingAction.getComponentToValidate());

		validatingAction.execute(new ActionEvent("source", "command"));
		Assert.assertTrue("Should have called executeOnValid",
				validatingAction.executeOnValidExecuted);
		Assert.assertFalse("Should not have called executeOnError",
				validatingAction.executeOnErrorExecuted);

		// Test with a warning diagnostic - should still succeed
		componentToValidate.setErrorLevel(Diagnostic.WARNING);
		validatingAction.execute(new ActionEvent("source", "command"));
		Assert.assertTrue("Should have called executeOnValid",
				validatingAction.executeOnValidExecuted);
		Assert.assertFalse("Should not have called executeOnError",
				validatingAction.executeOnErrorExecuted);
	}

	/**
	 * This trivial implementation of ValidatingAction just records whether various methods have been called.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class MyValidatingAction extends ValidatingAction {

		private boolean executeOnValidExecuted = false;
		private boolean executeOnErrorExecuted = false;

		/**
		 * @param componentToValidate the component to validate
		 */
		private MyValidatingAction(final WComponent componentToValidate) {
			super(new WValidationErrors(), componentToValidate);
		}

		@Override
		public void executeOnError(final ActionEvent event, final List<Diagnostic> diags) {
			executeOnErrorExecuted = true;
			super.executeOnError(event, diags);
		}

		@Override
		public void executeOnValid(final ActionEvent event) {
			executeOnValidExecuted = true;
		}
	}

	/**
	 * A simple component for testing different levels of validation.
	 */
	private static final class MyComponent extends WTextField {

		/**
		 * The severity to create errors with.
		 */
		private int errorSeverity = -1;

		/**
		 * Sets the error level.
		 *
		 * @param errorLevel the error level to set.
		 */
		void setErrorLevel(final int errorLevel) {
			this.errorSeverity = errorLevel;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void validateComponent(final List<Diagnostic> diags) {
			super.validateComponent(diags);

			if (errorSeverity >= Diagnostic.INFO) {
				diags.add(new DiagnosticImpl(errorSeverity, this, "dummy"));
			}
		}
	}
}
