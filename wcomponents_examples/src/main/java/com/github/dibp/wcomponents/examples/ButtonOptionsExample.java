package com.github.dibp.wcomponents.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCheckBox;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WDropdown;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WFieldSet;
import com.github.dibp.wcomponents.WHorizontalRule;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTextField;
import com.github.dibp.wcomponents.WButton.ImagePosition;
import com.github.dibp.wcomponents.validation.ValidatingAction;
import com.github.dibp.wcomponents.validation.WValidationErrors;

/**
 * ButtonOptionsExample contains a series of controls for displaying and
 * manipulating an example button.
 * 
 * @author Steve Harney
 * @since 1.0.0
 */
public class ButtonOptionsExample extends WPanel
{
    /** button container for holding example button. */
    private final WContainer buttonContainer = new WContainer();

    /** text field for the button label. */
    private final WTextField tfButtonLabel = new WTextField();

    /** text field for the access key. */
    private final WTextField tfAccesskey = new WTextField();

    /** check box for the disable/enable of the example button. */
    private final WCheckBox cbDisabled = new WCheckBox();

    /** check box for render as link. */
    private final WCheckBox cbRenderAsLink = new WCheckBox();

    /** check box to set button as image. */
    private final WCheckBox cbSetImage = new WCheckBox();
    
    /** drop down for image position. */
    private final WDropdown ddImagePosition = new WDropdown();

    /** initial button label text. */
    private static final String INITIAL_BUTTON_TEXT = "Label Text";

    /**
     * Button example constructor, primarily used for setting up the button
     * config field set.
     * 
     */
    public ButtonOptionsExample()
    {
        tfButtonLabel.setText(INITIAL_BUTTON_TEXT);
        tfAccesskey.setMaxLength(1);
        
        List<ImagePosition> positions = new ArrayList<ImagePosition>(Arrays.asList(WButton.ImagePosition.values()));
        positions.add(0, null);
        ddImagePosition.setOptions(positions);

        WValidationErrors errors = new WValidationErrors();
        add(errors);
        WFieldSet fsButtonControls = getButtonControls(errors);
        add(fsButtonControls);
        add(new WHorizontalRule());
        // we use a container here so we can reset it for
        // the render as link option.
        add(buttonContainer);
        add(new WHorizontalRule());

    }

    /**
     * build the button controls field set.
     * 
     * @param errors
     *            the error pane from the page.
     * @return a field set for the controls.
     */
    private WFieldSet getButtonControls(final WValidationErrors errors)
    {
        // Options Layout
        WFieldSet fieldSet = new WFieldSet("Button configuration");
        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(30);

        layout.addField("Text", tfButtonLabel);
        layout.addField("AccessKey", tfAccesskey);
        layout.addField("Render as link", cbRenderAsLink);
        layout.addField("Disabled", cbDisabled);
        layout.addField("setImage ('/image/pencil.png')", cbSetImage);
        layout.addField("Image Position", ddImagePosition);

        // Apply Button
        WButton apply = new WButton("Apply");
        apply.setAction(new ValidatingAction(errors, fieldSet)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                applySettings();
            }
        });

        fieldSet.add(layout);
        fieldSet.add(apply);
        return fieldSet;
    }

    /**
     * this is were the majority of the work is done for building the button.
     * Note that it is in a container that is reset, effectively creating a new
     * button. this is only done to enable to dynamically change the button to a
     * link and back.
     */
    private void applySettings()
    {
        buttonContainer.reset();
        WButton exampleButton = new WButton(tfButtonLabel.getText());

        exampleButton.setRenderAsLink(cbRenderAsLink.isSelected());

        exampleButton.setText(tfButtonLabel.getText());

        if (cbSetImage.isSelected())
        {
            exampleButton.setImage("/image/pencil.png");
            exampleButton.setImagePosition((ImagePosition) ddImagePosition.getSelected());
        }

        exampleButton.setDisabled(cbDisabled.isSelected());
        
        if (tfAccesskey.getText() != null && tfAccesskey.getText().length() > 0)
        {
            exampleButton.setAccessKey(tfAccesskey.getText().toCharArray()[0]);
        }
        
        buttonContainer.add(exampleButton);
    }

    /**
     * preparePaintComponents is called to use the applySettings to configure
     * the buttons.
     * 
     * @param request
     *            the web request.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        
        if (!isInitialised())
        {
            applySettings();
            setInitialised(true);
        }
    }
}
