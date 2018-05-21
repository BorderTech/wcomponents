package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WEmailField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPhoneNumberField;
import com.github.bordertech.wcomponents.WSuggestions;
import com.github.bordertech.wcomponents.WTextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrate using {@link WSuggestions} with input text fields.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSuggestionsExample extends WContainer {

	/**
	 * Ajax target.
	 */
	private final WTextField resultField = new WTextField();

	private final WTextField textRO;

	/**
	 * Construct example.
	 */
	public WSuggestionsExample() {
		WFieldLayout layout = new WFieldLayout();
		layout.setLabelWidth(25);

		resultField.setReadOnly(true);

		add(layout);

		WSuggestions suggestions = new WSuggestions("icao");
		add(suggestions);
		final WTextField text1 = new WTextField();
		text1.setSuggestions(suggestions);
		text1.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				resultField.setText(text1.getValueAsString());
			}
		});
		add(new WAjaxControl(text1, resultField));
		layout.addField("Cached list", text1);

		// Static suggestions
		suggestions = new WSuggestions(Arrays.asList("foo1", "foo2", "foo3", "ofoo"));
		add(suggestions);
		final WTextField text2 = new WTextField();
		text2.setSuggestions(suggestions);
		text2.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				resultField.setText(text2.getValueAsString());
			}
		});
		add(new WAjaxControl(text2, resultField));
		layout.addField("Static", text2);

		// Dynamic suggestions
		suggestions = new WSuggestions();
		add(suggestions);
		final WTextField text3 = new WTextField();
		text3.setSuggestions(suggestions);
		text3.setActionOnChange(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				resultField.setText(text3.getValueAsString());
			}
		});
		add(new WAjaxControl(text3, resultField));
		layout.addField("Dynamic as ajax trigger", text3);
		suggestions.setRefreshAction(new AjaxAction(""));

		// Dynamic phone number suggestions
		suggestions = new WSuggestions();
		add(suggestions);
		WPhoneNumberField phone = new WPhoneNumberField();
		phone.setSuggestions(suggestions);
		layout.addField("Dynamic phone number", phone);
		suggestions.setRefreshAction(new AjaxAction("Phone - "));

		// Dynamic email suggestions
		suggestions = new WSuggestions();
		add(suggestions);
		WEmailField email = new WEmailField();
		email.setSuggestions(suggestions);
		layout.addField("Dynamic email", email);
		suggestions.setRefreshAction(new AjaxAction("Email - "));

		// Dynamic suggestions with force selection
		suggestions = new WSuggestions();
		suggestions.setAutocomplete(WSuggestions.Autocomplete.LIST);
		add(suggestions);
		final WTextField text5 = new WTextField();
		text5.setSuggestions(suggestions);
		layout.addField("Force selection from list", text5);
		suggestions.setRefreshAction(new AjaxAction(""));
//		text5.setActionOnChange(new Action() {
//			@Override
//			public void execute(final ActionEvent event) {
//				resultField.setText(text5.getValueAsString());
//			}
//		});
//		add(new WAjaxControl(text5, resultField));
		layout.addField("Output", resultField);

		suggestions = new WSuggestions("icao");
		add(suggestions);
		textRO = new WTextField();
		textRO.setSuggestions(suggestions);
		textRO.setReadOnly(true);
		layout.addField("Read only", textRO);

		suggestions = new WSuggestions(Arrays.asList("foo1", "foo2", "foo3", "ofoo"));
		add(suggestions);
		final WTextField textRO2 = new WTextField();
		textRO2.setSuggestions(suggestions);
		textRO2.setReadOnly(true);
		layout.addField("Static list read-only", textRO2);
	}

	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request); //To change body of generated methods, choose Tools | Templates.
		if (!isInitialised()) {
			setInitialised(true);
			WSuggestions suggestions = textRO.getSuggestions();
			if (suggestions != null) {
				List<String> suggestionList = suggestions.getSuggestions();
				if (suggestionList != null && suggestionList.size() > 0) {
					textRO.setText(suggestionList.get((int) Math.floor(suggestionList.size() / 2)));
				}
			}
		}
	}



	/**
	 * Ajax action to refresh suggestion list. Create dummy values.
	 */
	private static final class AjaxAction implements Action {

		private final String prefix;

		/**
		 * @param prefix prefix for the dummy suggestions
		 */
		private AjaxAction(final String prefix) {
			this.prefix = prefix;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void execute(final ActionEvent event) {
			WSuggestions suggestions = (WSuggestions) event.getSource();
			String filter = suggestions.getAjaxFilter();

			// Create dummy suggestions
			List<String> sugg = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				sugg.add(prefix + filter + i);
			}
			suggestions.setSuggestions(sugg);
		}
	}

}
