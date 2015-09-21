package com.github.bordertech.wcomponents.examples.validation;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WDataRenderer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WTextField;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class demonstrates a problem with binding a WDataRenderer to a data bean that is unable to store incorrectly
 * typed values. In this case, the problem attribute is java.util.Date. If you enter a value such as "44 Aug 2008" into
 * the WDateField, then there is a problem!
 *
 * @author Martin Shevchenko
 */
public class ValidationAndDataRendererIssue extends ValidationContainer {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ValidationAndDataRendererIssue.class);

	/**
	 * Creates a ValidationAndDataRendererIssue.
	 */
	public ValidationAndDataRendererIssue() {
		super(new MyFields());
	}

	/**
	 * The component that will be validated by this example.
	 *
	 * @author Martin Shevchenko
	 */
	public static class MyFields extends WDataRenderer {

		/**
		 * Displays/edits the bean's name attribute.
		 */
		private final WTextField nameTF;

		/**
		 * Displays/edits the bean's dob attribute.
		 */
		private final WDateField dobDF;

		/**
		 * Displays/edits the bean's disappearingDate attribute.
		 */
		private final WDateField disappearingDF;

		/**
		 * Creates a MyFields component.
		 */
		public MyFields() {
			nameTF = new WTextField();

			dobDF = new WDateField();
			dobDF.setMandatory(true);

			disappearingDF = new WDateField();
			disappearingDF.setMandatory(true);

			WFieldLayout layout = new WFieldLayout();
			layout.addField("Name", nameTF);
			layout.addField("DOB", dobDF);
			layout.addField("Disappearing Date", disappearingDF);

			add(layout);
		}

		/**
		 * Updates the component from the model.
		 *
		 * @param data the data to set on the component.
		 */
		@Override
		public void updateComponent(final Object data) {
			MyFieldsDataBean dataBean = (MyFieldsDataBean) data;
			nameTF.setText(dataBean.getName());

			// The following ugly if statement is to stop invalid dates from
			// disappearing from the UI. This whole problem comes about because
			// the data bean to which this data renderer is bound is not capable
			// of holding invalid date entries.
			// In the last call to updateData, if the date entered into the UI
			// was not parseable then the data bean's date attribute will have
			// been set to null.
			// Note that code like this may result in a problem when a new set
			// of data needs to be loaded from the data bean into the UI and the
			// UI field has not been cleared of previous invalid data. It would
			// be best to reset the UI before calling setData.
			if (dataBean.getDob() != null || dobDF.isParseable()) {
				dobDF.setDate(dataBean.getDob());
			}

			disappearingDF.setDate(dataBean.getDisappearingDate());
		}

		/**
		 * Updates the model from the component.
		 *
		 * @param data the data to update.
		 */
		@Override
		public void updateData(final Object data) {
			MyFieldsDataBean dataBean = (MyFieldsDataBean) data;
			dataBean.setName(nameTF.getText());
			dataBean.setDob(dobDF.getDate());
			dataBean.setDisappearingDate(disappearingDF.getDate());
		}

		/**
		 * Overridden to initialise the data on the user's first access of this component.
		 *
		 * @param request the request being responded to.
		 */
		@Override
		protected void preparePaintComponent(final Request request) {
			if (!this.isInitialised()) {
				MyFieldsDataBean dataBean = new MyFieldsDataBean();

				dataBean.setName("Alice");

				try {
					dataBean.setDob((new SimpleDateFormat("dd/MM/yyyy")).parse("20/02/1984"));
					dataBean.setDisappearingDate((new SimpleDateFormat("dd/MM/yyyy")).parse(
							"30/03/1993"));
				} catch (ParseException e) {
					LOG.error("Error parsing dates", e);
				}

				this.setData(dataBean);

				this.setInitialised(true);
			}

			super.preparePaintComponent(request);
		}
	}

	/**
	 * Example data bean.
	 *
	 * @author Martin Shevchenko
	 */
	public static class MyFieldsDataBean {

		/**
		 * The bean name attribute.
		 */
		private String name;

		/**
		 * The bean date of birth attribute.
		 */
		private Date dob;

		/**
		 * The bean date of birth attribute, which "disappears" when invalid.
		 */
		private Date disappearingDate;

		/**
		 * @return the dob.
		 */
		public Date getDob() {
			return dob;
		}

		/**
		 * Sets the dob.
		 *
		 * @param dob the dob to set.
		 */
		public void setDob(final Date dob) {
			this.dob = dob;
		}

		/**
		 * @return the disappearingDate.
		 */
		public Date getDisappearingDate() {
			return disappearingDate;
		}

		/**
		 * Sets the disappearingDate.
		 *
		 * @param disappearingDate the disappearingDate to set.
		 */
		public void setDisappearingDate(final Date disappearingDate) {
			this.disappearingDate = disappearingDate;
		}

		/**
		 * @return the name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 *
		 * @param name the name to set.
		 */
		public void setName(final String name) {
			this.name = name;
		}
	}
}
