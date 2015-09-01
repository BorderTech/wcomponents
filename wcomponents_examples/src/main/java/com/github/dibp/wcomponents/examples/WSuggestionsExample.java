package com.github.dibp.wcomponents.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WEmailField;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WPhoneNumberField;
import com.github.dibp.wcomponents.WSuggestions;
import com.github.dibp.wcomponents.WTextField;

/**
 * Demonstrate using {@link WSuggestions} with input text fields.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSuggestionsExample extends WContainer
{
    /**
     * Construct example.
     */
    public WSuggestionsExample()
    {
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);

        add(layout);

        // Cached suggestions
        WSuggestions suggestions = new WSuggestions("icao");
        add(suggestions);
        WTextField text = new WTextField();
        text.setSuggestions(suggestions);
        layout.addField("Cached list", text);

        // Static suggestions
        suggestions = new WSuggestions(Arrays.asList("foo1", "foo2", "foo3", "ofoo"));
        add(suggestions);
        text = new WTextField();
        text.setSuggestions(suggestions);
        layout.addField("Static", text);

        // Dynamic suggestions
        suggestions = new WSuggestions();
        add(suggestions);
        text = new WTextField();
        text.setSuggestions(suggestions);
        layout.addField("Dynamic", text);
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

    }

    /**
     * Ajax action to refresh suggestion list. Create dummy values.
     */
    private final static class AjaxAction implements Action
    {
        private final String prefix;

        /**
         * @param prefix prefix for the dummy suggestions
         */
        public AjaxAction(final String prefix)
        {
            this.prefix = prefix;
        }

        /** {@inheritDoc} */
        @Override
        public void execute(final ActionEvent event)
        {
            WSuggestions suggestions = (WSuggestions) event.getSource();
            String filter = suggestions.getAjaxFilter();

            // Create dummy suggestions
            List<String> sugg = new ArrayList<String>();
            for (int i = 0; i < 4; i++)
            {
                sugg.add(prefix + filter + i);
            }
            suggestions.setSuggestions(sugg);
        }
    }

}
