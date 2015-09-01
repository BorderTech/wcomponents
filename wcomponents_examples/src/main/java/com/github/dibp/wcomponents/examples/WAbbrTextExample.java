package com.github.dibp.wcomponents.examples;

import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WAbbrText;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WHeading;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WStyledText;
import com.github.dibp.wcomponents.layout.ListLayout;
import com.github.dibp.wcomponents.util.Factory;
import com.github.dibp.wcomponents.util.LookupTable;

/**
 * <p>
 * This example shows usage of WAbbrText.
 * </p>
 * <p>
 * Hovering over each of the texts will display the tool tip
 * </p>
 * 
 * @author Kishan Bisht
 * @since 1.0.0
 */
public final class WAbbrTextExample extends WContainer
{
    /** Displays a set of WAbbrTexts containing Crt entry codes/descriptions. */
    private final WPanel crtSexPanel = new WPanel();

    /** Displays a set of WAbbrTexts containing Crt entry codes/descriptions. */
    private final WPanel crtIcaoPanel = new WPanel();

    /**
     * Creates a WAbbrTextExample.
     */
    public WAbbrTextExample()
    {
        add(new WHeading(WHeading.SECTION, "Abreviation created from strings"));
        WAbbrText at1 = new WAbbrText("App Id", "Identification number of the visa application");
        add(at1);

        add(new WHeading(WHeading.SECTION, "Abreviation created from lookup tables using the code as the text"));
        crtIcaoPanel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.LEFT, ListLayout.Separator.DOT, false));
        add(crtIcaoPanel);

        add(new WHeading(WHeading.SECTION, "Abreviation created from lookup tables using the description as the text"));
        final WStyledText info = new WStyledText("This example shows the dangers of doing code-set conversion and confusing the code and description. Obviously the abbreviation here is NOT the abbreviation we want. We would normally expect the reverse as in the example above.");
        add(info);
        info.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
        crtSexPanel.setLayout(new ListLayout(ListLayout.Type.STACKED, ListLayout.Alignment.LEFT, ListLayout.Separator.DOT, false));
        add(crtSexPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        
        if (!isInitialised())
        {
            LookupTable lookup = Factory.newInstance(LookupTable.class);

            for (Object entry : lookup.getTable("sex"))
            {
                WAbbrText abbr = new WAbbrText();
                abbr.setTextWithDesc(entry);
                crtSexPanel.add(abbr);
            }

            for (Object entry : lookup.getTable("icao"))
            {
                WAbbrText abbr = new WAbbrText();
                abbr.setTextWithCode(entry);
                crtIcaoPanel.add(abbr);
            }
            
            setInitialised(true);
        }
    }
}
