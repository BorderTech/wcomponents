package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.BeanProvider;
import com.github.bordertech.wcomponents.BeanProviderBound;
import com.github.bordertech.wcomponents.DefaultWComponent;
import com.github.bordertech.wcomponents.WBeanContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WList;
import com.github.bordertech.wcomponents.WMessagesValidatingAction;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.examples.petstore.beanprovider.CrtBeanProvider;
import com.github.bordertech.wcomponents.examples.petstore.model.CartBean;
import com.github.bordertech.wcomponents.examples.petstore.model.InventoryBean;
import com.github.bordertech.wcomponents.examples.petstore.model.PetStoreDao;
import com.github.bordertech.wcomponents.layout.ColumnLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.validation.WFieldErrorIndicator;
import com.github.bordertech.wcomponents.validator.AbstractFieldValidator;
import java.text.DecimalFormat;

/**
 * ConfirmOrderPanel allows a user to confirm their order and enter shipping and payment details.
 *
 * Expects a ConfirmOrderBean as its bean value.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ConfirmOrderPanel extends WBeanContainer {

	/**
	 * Creates a ConfirmOrderPanel.
	 */
	public ConfirmOrderPanel() {
		setBeanProperty(".");

		WPanel mainPanel = new WPanel();
		mainPanel.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 10));
		add(mainPanel);

		mainPanel.add(new WHeading(WHeading.SUB_SUB_HEADING, "Order details"));
		mainPanel.add(new OrderSummaryPanel());

		mainPanel.add(new WHeading(WHeading.SUB_SUB_HEADING, "Address details"));

		// Name
		FieldPanel fieldPanel = new FieldPanel();
		WTextField firstName = new WTextField();
		firstName.setMandatory(true);
		firstName.setMaxLength(60);
		firstName.setBeanProperty("firstName");
		fieldPanel.add("First name", firstName);

		WTextField lastName = new WTextField();
		lastName.setMandatory(true);
		lastName.setMaxLength(60);
		lastName.setBeanProperty("lastName");
		fieldPanel.add("Last name", lastName);
		mainPanel.add(fieldPanel);

		// Address
		fieldPanel = new FieldPanel();
		WTextField addressLine1 = new WTextField();
		addressLine1.setAccessibleText("Street address line 1");
		addressLine1.setMandatory(true);
		addressLine1.setMaxLength(60);
		addressLine1.setBeanProperty("address.line1");
		fieldPanel.add("Street address", addressLine1);

		WTextField addressLine2 = new WTextField();
		addressLine2.setAccessibleText("Street address line 2");
		addressLine2.setMaxLength(60);
		addressLine2.setBeanProperty("address.line2");
		fieldPanel.add((String) null, addressLine2);

		WTextField suburb = new WTextField();
		suburb.setMandatory(true);
		suburb.setMaxLength(60);
		suburb.setBeanProperty("address.suburb");
		fieldPanel.add("Suburb", suburb);

		WTextField state = new WTextField();
		state.setMandatory(true);
		state.setMaxLength(60);
		state.setBeanProperty("address.state");
		fieldPanel.add("State", state);

		WTextField postCode = new WTextField();
		postCode.setMandatory(true);
		postCode.setMaxLength(60);
		postCode.setBeanProperty("address.postcode");
		fieldPanel.add("Post code", postCode);

		WDropdown country = new WDropdown(new String[]{"Australia"});
		country.setBeanProperty("address.country");
		fieldPanel.add("Country", country);

		mainPanel.add(fieldPanel);

		// Phone numbers/email address
		fieldPanel = new FieldPanel();

		WTextField homePhone = new WTextField();
		homePhone.setMandatory(true);
		homePhone.setMaxLength(20);
		homePhone.setBeanProperty("homePhone");
		fieldPanel.add("Home phone", homePhone);

		WTextField workPhone = new WTextField();
		workPhone.setMaxLength(20);
		workPhone.setBeanProperty("workPhone");
		fieldPanel.add("Work phone", workPhone);

		WTextField emailAddress = new WTextField();
		emailAddress.setMandatory(true);
		emailAddress.setMaxLength(60);
		emailAddress.setBeanProperty("emailAddress");
		fieldPanel.add("Email address", emailAddress);

		mainPanel.add(fieldPanel);

		// Payment details
		add(new WHeading(WHeading.SUB_SUB_HEADING, "Payment"));
		fieldPanel = new FieldPanel();
		fieldPanel.add("Payment type", new WDropdown(new String[]{"Pick-up at store"}));
		mainPanel.add(fieldPanel);

		// Terms and conditions
		WStyledText termsAndConditions = new WStyledText();
		termsAndConditions.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
		termsAndConditions.setBeanProvider(new CrtBeanProvider("terms_conditions", "DEFAULT"));

		mainPanel.add(new WHeading(WHeading.SUB_SUB_HEADING, "Terms & conditions"));
		mainPanel.add(termsAndConditions);
		fieldPanel = new FieldPanel();
		fieldPanel.setLayout(new FlowLayout(Alignment.LEFT));
		WCheckBox agreeFlag = new WCheckBox();
		agreeFlag.addValidator(new AbstractFieldValidator(
				"You must agree to the terms and conditions to proceed.") {
			@Override
			protected boolean isValid() {
				return ((WCheckBox) getInputField()).isSelected();
			}
		});

		fieldPanel.add("I agree to the terms & conditions", agreeFlag);
		mainPanel.add(fieldPanel);

		// Order button
		WButton placeOrderButton = new WButton("Confirm order");
		placeOrderButton.setAction(new WMessagesValidatingAction(this) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				ConfirmOrderPanel panel = ConfirmOrderPanel.this;
				WebUtilities.updateBeanValue(panel);

				PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, panel);

				if (app != null) {
					app.placeOrder();
				}
			}
		});

		mainPanel.add(placeOrderButton);
	}

	/**
	 * A convenience class to reduce the amount of code necessary to add a field, label and error indicator. The labels
	 * and fields are displayed with a 20%/80% column split.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class FieldPanel extends WPanel {

		/**
		 * Creates a FieldPanel.
		 */
		private FieldPanel() {
			setLayout(new ColumnLayout(new int[]{20, 80}));
		}

		/**
		 * Adds a field to the panel.
		 *
		 * @param fieldLabel the field's label.
		 * @param field the field to add.
		 */
		public void add(final String fieldLabel, final WComponent field) {
			if (fieldLabel == null) {
				add(new DefaultWComponent());
			} else {
				add(new WLabel(fieldLabel, field));
			}

			WContainer fieldAndIndicator = new WContainer();
			fieldAndIndicator.add(new WFieldErrorIndicator(field));
			fieldAndIndicator.add(field);
			add(fieldAndIndicator);
		}
	}

	/**
	 * A read-only summary display the of the products in the order.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class OrderSummaryPanel extends WPanel {

		/**
		 * The cart summary list.
		 */
		private final WList cartSummary = new WList(WList.Type.STACKED);

		/**
		 * Creates an OrderSummaryPanel.
		 */
		private OrderSummaryPanel() {
			setLayout(new FlowLayout(Alignment.VERTICAL));

			// The summary list needs to be given the contents of the cart.
			cartSummary.setBeanProvider(new BeanProvider() {
				@Override
				public Object getBean(final BeanProviderBound beanProviderBound) {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class,
							OrderSummaryPanel.this);
					return app.getCart();
				}
			});

			// Create a renderer for each item in the basket.
			// The bean for this class will be an item in the cart.
			// We override getText to return the appropriate text.
			WText summaryLine = new WText() {
				@Override
				public String getText() {
					CartBean cartBean = (CartBean) getBean();
					InventoryBean item = PetStoreDao.readInventory(cartBean.getProductId());
					DecimalFormat formatter = new DecimalFormat("$0.00");

					return cartBean.getCount() + "x "
							+ item.getProduct().getShortTitle()
							+ " @ " + formatter.format(item.getUnitCost() / 100.0);
				}
			};

			cartSummary.setRepeatedComponent(summaryLine);

			// Create a renderer for the totals row
			// We override getText to return the appropriate text
			WStyledText total = new WStyledText("", WStyledText.Type.EMPHASISED) {
				@Override
				public String getText() {
					PetStoreApp app = WebUtilities.getClosestOfClass(PetStoreApp.class, this);
					DecimalFormat formatter = new DecimalFormat("$0.00");

					if (app != null) {
						int total = 0;

						for (CartBean cartBean : app.getCart()) {
							InventoryBean item = PetStoreDao.readInventory(cartBean.getProductId());
							total += item.getUnitCost() * cartBean.getCount();
						}

						return "Total: " + formatter.format(total / 100.0);
					}

					return null;
				}
			};

			add(cartSummary);
			add(total);
		}
	}
}
