package com.github.bordertech.wcomponents.examples.subordinate;

import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WRadioButtonSelect;
import com.github.bordertech.wcomponents.WStyledText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.TextImage;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.subordinate.builder.SubordinateBuilder;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * A more complex example of {@link SubordinateBuilder} usage.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class SubordinateBuilderComplexExample extends WContainer {

	/**
	 * The credit card payment option.
	 */
	private static final String CREDIT_CARD = "Credit Card";

	/**
	 * The electronic transfer payment option.
	 */
	private static final String TRANSFER = "Electronic transfer";

	/**
	 * Creates a SubordinateBuilderComplexExample.
	 */
	public SubordinateBuilderComplexExample() {
		add(new WHeading(WHeading.SECTION, "Payment options"));

		WRadioButtonSelect paymentType = new WRadioButtonSelect(new String[]{CREDIT_CARD, TRANSFER});
		paymentType.setFrameless(true);

		WFieldSet paymentTypeFieldSet = new WFieldSet("Select payment option");
		add(paymentTypeFieldSet);
		paymentTypeFieldSet.add(paymentType);

		CreditCardPanel creditCardPanel = new CreditCardPanel();
		TransferPanel transferPanel = new TransferPanel();

		add(creditCardPanel);
		add(transferPanel);

		// Create the subordinates for the top-level panel
		SubordinateBuilder builder = new SubordinateBuilder();
		builder.condition().equals(paymentType, CREDIT_CARD);
		builder.whenTrue().show(creditCardPanel).hide(transferPanel); // multiple actions can be used on true/false.
		add(builder.build());

		builder = new SubordinateBuilder();
		builder.condition().equals(paymentType, TRANSFER);
		builder.whenTrue().show(transferPanel).hide(creditCardPanel);
		add(builder.build());
	}

	/**
	 * An example entry panel for the user to enter in credit card information.
	 */
	private static final class CreditCardPanel extends WPanel {

		/**
		 * Month names for the expiry date.
		 */
		private static final String[] MONTH_NAMES = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

		/**
		 * Creates a CreditCardPanel.
		 */
		private CreditCardPanel() {
			super(WPanel.Type.BLOCK);
			RadioButtonGroup creditCardType = new RadioButtonGroup();
			WRadioButton cardAButton = creditCardType.addRadioButton("cardA");
			WRadioButton cardBButton = creditCardType.addRadioButton("cardB");
			WRadioButton cardCButton = creditCardType.addRadioButton("cardC");

			WPanel cardTypePanel = new WPanel();
			cardTypePanel.setLayout(new FlowLayout(Alignment.LEFT, 10, 0));
			cardTypePanel.add(cardAButton);
			cardTypePanel.add(createCardImage("A"));
			cardTypePanel.add(cardBButton);
			cardTypePanel.add(createCardImage("B"));
			cardTypePanel.add(cardCButton);
			cardTypePanel.add(createCardImage("C"));
			cardTypePanel.add(creditCardType);

			WFieldLayout fieldLayout = new WFieldLayout();
			fieldLayout.addField("Type of credit card", cardTypePanel);
			WField numberField = fieldLayout.addField("Credit card number", new WTextField());
			WField expiryField = fieldLayout.addField("Expiry date", createDatePicker());
			WField nameField = fieldLayout.addField("Name on credit card", new WTextField());
			WField cvcField = fieldLayout.addField("Card security code", new WTextField());
			fieldLayout.addField("Payment amount", new WStyledText("$nnn",
					WStyledText.Type.EMPHASISED));

			add(fieldLayout);

			// Create the subordinates for the credit card payment panel
			// When any card is selected, show the name and number fields
			SubordinateBuilder builder = new SubordinateBuilder();
			builder.condition().equals(creditCardType, cardAButton).or().equals(creditCardType,
					cardBButton).or().equals(creditCardType, cardCButton);
			builder.whenTrue().show(numberField, expiryField, nameField); // multiple fields can be acted on at once
			builder.whenFalse().hide(numberField, expiryField, nameField);
			add(builder.build());

			// When card B or card C is selected, show the CVC field
			builder = new SubordinateBuilder();
			builder.condition().equals(creditCardType, cardBButton).or().equals(creditCardType,
					cardCButton);
			builder.whenTrue().show(cvcField);
			builder.whenFalse().hide(cvcField);
			add(builder.build());
		}

		/**
		 * Creates an example image to display as the "logo" for a card.
		 *
		 * @param cardType the type of card to generate the logo for.
		 * @return a new image for the card.
		 */
		private WImage createCardImage(final String cardType) {
			WImage image = new WImage();
			image.setAccessibleText(cardType);
			image.setImage(new TextImage(cardType, new Dimension(50, 30)));
			return image;
		}

		/**
		 * Creates a data entry component for a month + year.
		 *
		 * @return a new date picker component.
		 */
		private WContainer createDatePicker() {
			WContainer datePicker = new WContainer();

			List<String> months = new ArrayList<>(Arrays.asList(MONTH_NAMES));
			months.add(0, null);

			List<Integer> years = new ArrayList<>(10);
			years.add(null);

			int start = Calendar.getInstance().get(Calendar.YEAR) - 1;

			for (int i = start; i < start + 10; i++) {
				years.add(i);
			}

			datePicker.add(new WDropdown(months));
			datePicker.add(new WDropdown(years));

			return datePicker;
		}
	}

	/**
	 * An example panel which provides instructions on how to complete the transaction. This panel does not contain any
	 * subordinate logic.
	 */
	private static final class TransferPanel extends WPanel {

		/**
		 * Creates a transfer panel.
		 */
		private TransferPanel() {
			super(WPanel.Type.BLOCK);
			setLayout(new FlowLayout(Alignment.VERTICAL, 0, 10));

			add(new WHeading(WHeading.SECTION, "Transfer details"));

			WStyledText text = new WStyledText(
					"If paying by electronic transfer:"
					+ "\n \u00b7 Payments must be received within seven days of submitting the order."
					+ "\n \u00b7 If payment is not received within seven days, the order will be discarded."
					+ "\n \u00b7 Use only your invoice number as the lodgement reference."
					+ "\n\nAccount details\nBank: example bank\nAcct No.: 0123456789\nAcct Name: example name"
			);

			text.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);

			add(text);
		}
	}
}
