package com.github.bordertech.wcomponents.qa.findbugs;

import com.github.bordertech.wcomponents.AbstractWComponent;
import com.github.bordertech.wcomponents.ComponentModel;
import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;

/**
 * Test code for the {@link CheckGetComponentModel} detector.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class CheckGetComponentModel_Test {

	/**
	 * Test component to analyse.
	 */
	public static final class MyComponent extends AbstractWComponent {

		/**
		 * Call to getOrCreateComponentModel is suspicious.
		 */
		@ExpectWarning(value = "WCGETM")
		public void getOrCreateSuspicious() {
			getOrCreateComponentModel();
		}

		/**
		 * Call to getComponentModel from a setter is suspicious.
		 */
		@ExpectWarning(value = "WCGETM")
		public void setModelSuspicious() {
			getComponentModel();
		}

		/**
		 * Direct get from component model is ok.
		 *
		 * @return the data
		 */
		@NoWarning(value = "WCGETM")
		public long getDataDirect() {
			return ((MyModel) getComponentModel()).data;
		}

		/**
		 * Indirect get from component model is ok.
		 *
		 * @return the data
		 */
		@NoWarning(value = "WCGETM")
		public long getDataIndirect() {
			MyModel model = ((MyModel) getComponentModel());
			return model.data;
		}

		/**
		 * Get from component model with side-effect is bad.
		 *
		 * @return the data
		 */
		@ExpectWarning(value = "WCGETM")
		public long getDataWithSideEffect() {
			MyModel model = ((MyModel) getComponentModel());
			model.data++;
			return model.data;
		}

		/**
		 * Direct set on component model is bad.
		 */
		@ExpectWarning(value = "WCGETM")
		public void setDataDirectBad() {
			((MyModel) getComponentModel()).data = 1;
		}

		/**
		 * Indirect set on component model is bad.
		 */
		@ExpectWarning(value = "WCGETM")
		public void setDataIndirectBad() {
			MyModel model = ((MyModel) getComponentModel());
			model.data = 1;
		}

		/**
		 * Direct set on component model is ok for getOrCreateComponentModel.
		 */
		@NoWarning(value = "WCGETM")
		public void setDataDirectCorrect() {
			((MyModel) getOrCreateComponentModel()).data = 1;
		}

		/**
		 * Indirect set on component model is ok for getOrCreateComponentModel.
		 *
		 * @param data indirect data
		 */
		@NoWarning(value = "WCGETM")
		public void setDataIndirect(final String data) {
			MyModel model = ((MyModel) getOrCreateComponentModel());
			model.data = 1;
		}

		/**
		 * Call of setter on component model is bad.
		 */
		@ExpectWarning(value = "WCGETM")
		public void setDataSetterBad() {
			((MyModel) getComponentModel()).setData(1);
		}

		/**
		 * Call of setter on component model return from getOrCreateComponentModel is ok.
		 */
		@NoWarning(value = "WCGETM")
		public void setDataSetterCorrect() {
			((MyModel) getOrCreateComponentModel()).setData(1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ComponentModel newComponentModel() {
			return new MyModel();
		}

		/**
		 * A ComponentModel extension is used to check that the instanceOf tests work.
		 */
		public static final class MyModel extends ComponentModel {

			private long data = 0;

			/**
			 * @param data the data to save on the model
			 */
			public void setData(final long data) {
				this.data = data;
			}

			/**
			 * @return the data on the model
			 */
			public long getData() {
				return data;
			}
		}
	}
}
