package com.github.dibp.wcomponents.examples;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WAjaxControl;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.WHorizontalRule;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WTextArea;
import com.github.dibp.wcomponents.layout.FlowLayout;
import com.github.dibp.wcomponents.layout.FlowLayout.Alignment;

/**
 * <p>
 * A {@link WTextArea} is a wcomponent used to display a html textarea. It is very
 * much like WTextField except that it has multiple lines of input.
 * </p>
 * <p>
 * WTextArea example demonstrates various states of the WTextArea
 * </p>
 * <p>
 * including
 * <ul>
 *  <li>Default</li>
 *  <li>Size limited</li>
 *  <li>Read Only</li>
 *  <li>Disabled</li>
 * </ul>
 * </p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TextAreaExample extends WPanel
{
    private final WTextArea ta1;
    private final WTextArea ta2;
    private final WTextArea ta3;
    private final WTextArea ta4;
    private final WTextArea ta5;

    /**
     * Creates a TextAreaExample.
     */
    public TextAreaExample()
    {
        setLayout(new FlowLayout(Alignment.VERTICAL));

        WFieldLayout layout = new WFieldLayout();
        WHeading heading = new WHeading(WHeading.MAJOR, "Default");
        add(heading);

        ta1 = new WTextArea();
        layout.addField("Default", ta1);
        add(layout);
        add(new WHorizontalRule());


        layout = new WFieldLayout();
        heading = new WHeading(WHeading.MAJOR, "Size 40x4, maxlength 200");
        add(heading);
        ta2 = new WTextArea();
        ta2.setColumns(40);
        ta2.setRows(4);
        ta2.setMaxLength(200);
        layout.addField("Size and Length Limited", ta2);

        add(layout);
        add(new WHorizontalRule());

        layout = new WFieldLayout();
        heading = new WHeading(WHeading.MAJOR, "Read only");
        add(heading);
        ta3 = new WTextArea();
        ta3.setReadOnly(true);
        layout.addField("Read only", ta3);

        add(layout);
        add(new WHorizontalRule());

        layout = new WFieldLayout();
        heading = new WHeading(WHeading.MAJOR, "Disabled");
        add(heading);

        ta4 = new WTextArea();
        ta4.setDisabled(true);
        layout.addField("Disabled", ta4);

        WButton toggleDisableButton = new WButton("Toggle disable");
        toggleDisableButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                ta4.setDisabled(!ta4.isDisabled());
            }
        });

        add(layout);
        add(toggleDisableButton);
        add(new WHorizontalRule());

        layout = new WFieldLayout();
        heading = new WHeading(WHeading.MAJOR, "Rich Text");
        add(heading);

        ta5 = new WTextArea();
        ta5.setRichTextArea(true);
        layout.addField("Rich Text", ta5);
        add(layout);
        // add(new WHorizontalRule());

        final WButton rtfButton = new WButton("Round Trip Show Rich Text");
        final WButton rtAjaxButton = new WButton("AJAX Show Rich Text");
        final WText rtfOutput = new WText();
        rtfOutput.setEncodeText(false);
        add(rtfButton);
        add(rtAjaxButton);
        WPanel rtfOutputPanel = new WPanel();
        add(rtfOutputPanel);
        rtfOutputPanel.add(rtfOutput);

        rtAjaxButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                rtfOutput.setText(ta5.getValue());
            }
        });
        WAjaxControl ajaxControl = new WAjaxControl(rtAjaxButton, rtfOutputPanel);
        add(ajaxControl);

        rtfButton.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                rtfOutput.setText(ta5.getValue());
            }
        });
      }

    /**
     * Override preparePaintComponent to test that dynamic attributes are handled correctly.
     *
     * @param request the request that triggered the paint.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);

        if (!isInitialised())
        {
            ta3.setText("This is read only.");
            ta4.setText("This is disabled.");

            setInitialised(true);
        }
    }
}
