package com.github.openborders.examples;

import com.github.openborders.Margin;
import com.github.openborders.WButton;
import com.github.openborders.WCheckBox;
import com.github.openborders.WContainer;
import com.github.openborders.WFieldLayout;
import com.github.openborders.WPanel;
import com.github.openborders.WSection;
import com.github.openborders.WText;
import com.github.openborders.WSection.SectionMode;
import com.github.openborders.subordinate.Equal;
import com.github.openborders.subordinate.Hide;
import com.github.openborders.subordinate.Rule;
import com.github.openborders.subordinate.Show;
import com.github.openborders.subordinate.WSubordinateControl;

/**
 * Demonstrate how {@link WSection} can be used.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WSectionExample extends WContainer
{
    /** Default box margin. */
    private static final Margin DEFAULT_BOX_MARGIN = new Margin(0, 0, 6, 0);

    /** Default example margin. */
    private static final Margin DEFAULT_MARGIN = new Margin(24);

    /**
     * Build example.
     */
    public WSectionExample()
    {
        // Basic Section

        WPanel box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);

        WSection section = new WSection("Basic Section");
        box.add(section);
        section.getContent().add(new WText("Hello World"));
        section.setMargin(DEFAULT_MARGIN);

        WButton help = new WButton("");
        help.setImage("/image/help.png");
        //help.setRenderAsLink(true);
        help.setToolTip("Help");
        section.getDecoratedLabel().setTail(help);

        // Eager Section
        box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);
        section = new WSection("Eager Section");
        section.setMode(SectionMode.EAGER);
        section.setMargin(DEFAULT_MARGIN);
        box.add(section);
        section.getContent().add(new WText("Hello World"));

        // Lazy Section
        box = new WPanel(WPanel.Type.BOX);
        box.setMargin(DEFAULT_BOX_MARGIN);
        add(box);

        WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
        box.add(layout);

        WCheckBox chb = new WCheckBox();
        layout.addField("Show and hide lazy section", chb);

        section = new WSection("Lazy Section");
        section.setMode(SectionMode.LAZY);
        section.setMargin(DEFAULT_MARGIN);
        section.getContent().add(new WText("Hello World"));
        box.add(section);

        WSubordinateControl control = new WSubordinateControl();
        box.add(control);

        control.addRule(new Rule(new Equal(chb, "true"), new Show(section), new Hide(section)));

        add(new WButton("submit"));
    }

}
