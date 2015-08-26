package com.github.openborders.wcomponents.examples;

import com.github.openborders.wcomponents.ImageResource;
import com.github.openborders.wcomponents.Margin;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WFigure;
import com.github.openborders.wcomponents.WImage;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WFigure.FigureMode;
import com.github.openborders.wcomponents.subordinate.Equal;
import com.github.openborders.wcomponents.subordinate.Hide;
import com.github.openborders.wcomponents.subordinate.Rule;
import com.github.openborders.wcomponents.subordinate.Show;
import com.github.openborders.wcomponents.subordinate.WSubordinateControl;

/**
 * Demonstrate how {@link WFigure} can be used.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFigureExample extends WContainer
{
    /** Default box margin. */
    private static final Margin DEFAULT_BOX_MARGIN = new Margin(0, 0, 6, 0);

    /** Default example margin. */
    private static final Margin DEFAULT_MARGIN = new Margin(24);

    /** Image resource. */
    private static final ImageResource IMAGE_RESOURCE = new ImageResource("/com/github/openborders/wcomponents/examples/portlet-portrait.jpg", "Portrait");

    /**
     * Build example.
     */
    public WFigureExample()
    {
        WPanel box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);

        // Basic Figure
        WFigure figure = new WFigure(new WImage(IMAGE_RESOURCE), "Basic Figure");
        box.add(figure);
        figure.setMargin(DEFAULT_MARGIN);

        // Eager Figure
        box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);
        figure = new WFigure(new WImage(IMAGE_RESOURCE), "Eager Figure");
        figure.setMode(FigureMode.EAGER);
        figure.setMargin(DEFAULT_MARGIN);
        box.add(figure);

        // Lazy Figure
        box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);

        WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
        box.add(layout);

        WCheckBox chb = new WCheckBox();
        layout.addField("Show and hide lazy figure", chb);

        figure = new WFigure(new WImage(IMAGE_RESOURCE), "Lazy Figure");
        figure.setMode(FigureMode.LAZY);
        figure.setMargin(DEFAULT_MARGIN);
        box.add(figure);

        WSubordinateControl control = new WSubordinateControl();
        box.add(control);

        control.addRule(new Rule(new Equal(chb, "true"), new Show(figure), new Hide(figure)));

        add(new WButton("submit"));
    }

}
