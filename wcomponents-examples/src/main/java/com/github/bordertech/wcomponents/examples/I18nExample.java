package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.HeadingLevel;
import com.github.bordertech.wcomponents.Margin;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.Date;
import java.util.Locale;

/**
 * This example demonstrates use of internationalisation.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class I18nExample extends WPanel implements MessageContainer {

	/**
	 * Used to display info/warning/error messages to the user.
	 */
	private final WMessages messages = new WMessages();

	/** Use client validation? */
	private final WCheckBox useClientValidation = new WCheckBox();
	private final WText csvJs = new WText("<script type=\"text/javascript\" defer=\"defer\">\n"
		+ "require([\"wc/compat/compat!\"], function(){\n\trequire([\"wc/ui/validation/all\"]);});\n"
		+ "</script>\n");

	/**
	 * Creates an I18nExample.
	 */
	public I18nExample() {
		csvJs.setEncodeText(false);
		csvJs.setVisible(false);
		useClientValidation.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				csvJs.setVisible(useClientValidation.isSelected());
			}
		});


		setLayout(new FlowLayout(Alignment.VERTICAL));
		add(messages);

		WPanel buttons = new WPanel(WPanel.Type.FEATURE);
		buttons.setLayout(new FlowLayout(Alignment.LEFT, 10, 0));
		add(buttons);

		buttons.add(new ChangeLocaleButton(null));
		buttons.add(new ChangeLocaleButton(new Locale("en")));
		buttons.add(new ChangeLocaleButton(new Locale("fr", "CA")));

		add(new WHeading(HeadingLevel.H2, "Internationalisation example"));

		final WTextField nameField = new WTextField();
		final ExplanatoryText helloWorldText = new ExplanatoryText();
		final WDateField dateField = new WDateField();

		nameField.setMandatory(true);
		dateField.setMandatory(true);
		dateField.setMaxDate(new Date());

		WButton actionButton = new WButton("SUBMIT_FORM");
		actionButton.setAction(
				new ValidatingAction(messages.getValidationErrors(), I18nExample.this) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				helloWorldText.setText("HELLO_NAME", nameField.getText());
			}
		});



		WFieldLayout layout = new WFieldLayout();
		add(layout);
		layout.setMargin(new Margin(0, 0, 12, 0));
		layout.addField("CLIENT_SIDE_PROMPT", useClientValidation);
		layout.addField(new WLabel("ENTER_NAME_PROMPT"), nameField);
		layout.addField("DATE_PROMPT", dateField);
		layout.addField((WLabel) null, actionButton);
		WHeading outputHeading = new WHeading(HeadingLevel.H3, "Output")
		{
			@Override
			protected void preparePaintComponent(Request request) {
				super.preparePaintComponent(request); //To change body of generated methods, choose Tools | Templates.
				String name = nameField.getText();
				setVisible(!(null == name || "".equals(name)));
			}

		};
		add(outputHeading);
		add(helloWorldText);
		add(csvJs);
		add(new WAjaxControl(useClientValidation, this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}

	/**
	 * This button is used to change the user's active locale to the given Locale.
	 */
	private static final class ChangeLocaleButton extends WButton {

		/**
		 * Creates a ChangeLocaleButton.
		 *
		 * @param locale the locale the button will switch to.
		 */
		private ChangeLocaleButton(final Locale locale) {
			setImage(getButtonImage(locale));
			setRenderAsLink(true);
			setToolTip("Change locale to " + (locale == null ? "default" : locale));

			setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					UIContextHolder.getCurrent().setLocale(locale);
				}
			});
		}

		/**
		 * Return a button image for this button. The button images are present in the classpath, in the same location
		 * as the resource bundles.
		 *
		 * @param locale the locale to retrieve the image for.
		 * @return the image for the given locale.
		 */
		private String getButtonImage(final Locale locale) {
			return "/i18n/" + (locale == null ? "default" : locale) + ".png";
		}
	}
}
