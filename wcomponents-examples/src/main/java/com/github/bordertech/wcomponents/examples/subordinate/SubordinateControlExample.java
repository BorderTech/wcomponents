package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.Input;
import com.github.bordertech.wcomponents.SubordinateTarget;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WComponentGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.validation.ValidationContainer;
import com.github.bordertech.wcomponents.subordinate.And;
import com.github.bordertech.wcomponents.subordinate.Condition;
import com.github.bordertech.wcomponents.subordinate.Equal;
import com.github.bordertech.wcomponents.subordinate.Hide;
import com.github.bordertech.wcomponents.subordinate.Rule;
import com.github.bordertech.wcomponents.subordinate.Show;
import com.github.bordertech.wcomponents.subordinate.ShowInGroup;
import com.github.bordertech.wcomponents.subordinate.WSubordinateControl;
import com.github.bordertech.wcomponents.validator.RegExFieldValidator;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This example demonstrates use of the SubordinateControl.</p>
 *
 * <p>
 * This example scenario involves a component (CardSelector) that enables a user to enter a card type, where there are
 * many different types of card types with different validations. When the user switches between types, the input field
 * should update to match.</p>
 *
 * <p>
 * There are 2 instances of this CardSelector, the first one is configured to use server side features to switch between
 * card types, the second one is configured to use client side switching.</p>
 *
 * @author Martin Shevchenko
 * @since 15/12/2008
 */
public class SubordinateControlExample extends ValidationContainer {

	/**
	 * The drop-down option for entering an Visa Card.
	 */
	private static final String VISA_CARD = "Visa";
	/**
	 * The drop-down option for entering an MasterCard.
	 */
	private static final String MASTER_CARD = "MasterCard";
	/**
	 * The drop-down option for entering another type of Card.
	 */
	private static final String OTHER_CARD = "Other Card";

	/**
	 * Creates a SubordinateControlExample.
	 */
	public SubordinateControlExample() {
		super(build());
	}

	/**
	 * @return a new instance of the example UI.
	 */
	private static WComponent build() {
		WContainer panel = new WContainer();
		panel.add(new CardSelector());
		return panel;
	}

	//=========================================================================
	/**
	 * Example of a wcomponent that uses SubordinateControl to handle its visibility logic.
	 */
	public static class CardSelector extends WPanel {

		private final WDropdown cardTypeSelection;
		private final WCheckBox cvcBox;

		/**
		 * The input field for entering an VISA card.
		 */
		private final WTextField visaCardNumberInputField;

		/**
		 * The input field for entering a MasterCard number input field.
		 */
		private final WTextField masterCardNumberInputField;

		/**
		 * The input field for entering an arbitrary card.
		 */
		private final WTextField otherCardInputField;

		/**
		 * An extra details field for the mastercard.
		 */
		private final WTextField cvcInputField;

		private final Map<String, Input> mapOptionToField = new HashMap<>();

		/**
		 * Creates a CardSelector.
		 */
		public CardSelector() {
			WFieldLayout layout = new WFieldLayout();
			add(layout);
			layout.setLabelWidth(25);
			layout.setMargin(new com.github.bordertech.wcomponents.Margin(0, 0, 12, 0));

			//
			//
			cardTypeSelection = new WDropdown();
			cardTypeSelection.setOptions(new String[]{VISA_CARD, MASTER_CARD, OTHER_CARD});
			layout.addField("Card Type", cardTypeSelection);

			// The rules below define that this checkbox appears if the user
			// selects a MasterCard type.
			cvcBox = new WCheckBox();
			WField cvcField = layout.addField("CVC", cvcBox);

			//
			// Subordinates
			//
			visaCardNumberInputField = new VisaCardField();
			WField visaField = layout.addField(VISA_CARD, visaCardNumberInputField);
			mapOptionToField.put(VISA_CARD, visaCardNumberInputField);

			masterCardNumberInputField = new MasterCardField();
			WField masterCardField = layout.addField(MASTER_CARD, masterCardNumberInputField);
			mapOptionToField.put(MASTER_CARD, masterCardNumberInputField);

			otherCardInputField = new OtherCardField();
			WField otherCardField = layout.addField(OTHER_CARD, otherCardInputField);
			mapOptionToField.put(OTHER_CARD, otherCardInputField);

			// The rules below define that this entry field appear if MasterCard
			// type is selected AND the cvc checkbox is selected.
			cvcInputField = new WTextField();
			cvcInputField.setColumns(3);
			cvcInputField.setMaxLength(3);
			cvcInputField.setMinLength(3);
			cvcInputField.setMandatory(true);
			cvcInputField.addValidator(new RegExFieldValidator("^[0-9]*$",
					"{0} must only contain numeric characters."));

			WField extraField = layout.addField("CVC", cvcInputField);

			// Configure a SubordinateControl to handle visibility of the
			// subordinate card entry fields.
			WSubordinateControl control = new WSubordinateControl();
			add(control);

			WComponentGroup<SubordinateTarget> group = new WComponentGroup<>();
			add(group);
			group.addToGroup(visaField);
			group.addToGroup(masterCardField);
			group.addToGroup(otherCardField);

			Rule rule;

			rule = new Rule();
			rule.setCondition(new Equal(cardTypeSelection, VISA_CARD));
			rule.addActionOnTrue(new ShowInGroup(visaField, group));
			control.addRule(rule);

			rule = new Rule();
			rule.setCondition(new Equal(cardTypeSelection, MASTER_CARD));
			rule.addActionOnTrue(new ShowInGroup(masterCardField, group));
			rule.addActionOnTrue(new Show(cvcField));
			rule.addActionOnFalse(new Hide(cvcField));
			control.addRule(rule);

			rule = new Rule();
			rule.setCondition(new Equal(cardTypeSelection, OTHER_CARD));
			rule.addActionOnTrue(new ShowInGroup(otherCardField, group));
			control.addRule(rule);

			rule = new Rule();
			Condition condition1 = new Equal(cardTypeSelection, MASTER_CARD);
			Condition condition2 = new Equal(cvcBox, "true");
			rule.setCondition(new And(condition1, condition2));
			rule.addActionOnTrue(new Show(extraField));
			rule.addActionOnFalse(new Hide(extraField));
			control.addRule(rule);
		}

		/**
		 * @return the Card Type as a string
		 */
		public String getCardTypeAsString() {
			String cardType = null;
			String selection = (String) cardTypeSelection.getSelected();

			if (selection != null) {
				Input field = mapOptionToField.get(selection);
				cardType = field.getValueAsString();
			}

			return cardType;
		}

		/**
		 * Indicates whether the "Visa" option is selected.
		 *
		 * @return true if the "Visa" option is selected, false otherwise.
		 */
		public boolean isVisaCard() {
			return VISA_CARD.equals(cardTypeSelection.getSelected());
		}

		/**
		 * Indicates whether the "MasterCard" option is selected.
		 *
		 * @return true if the "MasterCard" option is selected, false otherwise.
		 */
		public boolean isMasterCard() {
			return MASTER_CARD.equals(cardTypeSelection.getSelected());
		}

		/**
		 * Indicates whether the "Other Card" option is selected.
		 *
		 * @return true if the "Other Card" option is selected, false otherwise.
		 */
		public boolean isOtherCard() {
			return OTHER_CARD.equals(cardTypeSelection.getSelected());
		}
	}

	//=========================================================================
	/**
	 * A Text field extension for entering an Visa Card number, which ensures that the Card Number is in the correct
	 * format.
	 */
	public static class VisaCardField extends WTextField {

		/**
		 * Creates an VisaCardField.
		 */
		public VisaCardField() {
			this.setMandatory(true);
			this.setColumns(10);
			this.setMaxLength(10);
			this.setMinLength(10);
			this.addValidator(new RegExFieldValidator("^[0-9]*$",
					"{0} must only contain numeric characters."));
		}
	}

	/**
	 * A Number field extension for entering a MasterCard, which ensures that the CardType is in the correct format.
	 */
	public static class MasterCardField extends WTextField {

		/**
		 * Creates a MasterCardField.
		 */
		public MasterCardField() {
			this.setMandatory(true);
			this.setColumns(8);
			this.setMaxLength(8);
			this.setMinLength(8);
			this.addValidator(new RegExFieldValidator("^[0-9]*$",
					"{0} must only contain numeric characters."));

		}
	}

	/**
	 * A Number field extension for entering an arbitrary Card Number.
	 */
	public static class OtherCardField extends WTextField {

		/**
		 * Creates an OtherCardField.
		 */
		public OtherCardField() {
			this.setMandatory(true);
			this.setColumns(12);
			this.setMaxLength(12);
			this.addValidator(new RegExFieldValidator("^[0-9]*$",
					"{0} must only contain numeric characters."));
		}
	}
}
