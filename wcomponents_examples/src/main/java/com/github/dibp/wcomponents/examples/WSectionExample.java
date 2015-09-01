package com.github.dibp.wcomponents.examples;

import com.github.dibp.wcomponents.Margin;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WCheckBox;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WSection;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WSection.SectionMode;
import com.github.dibp.wcomponents.subordinate.Equal;
import com.github.dibp.wcomponents.subordinate.Hide;
import com.github.dibp.wcomponents.subordinate.Rule;
import com.github.dibp.wcomponents.subordinate.Show;
import com.github.dibp.wcomponents.subordinate.WSubordinateControl;

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
