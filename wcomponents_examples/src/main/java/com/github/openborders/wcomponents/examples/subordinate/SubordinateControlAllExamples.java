package com.github.openborders.wcomponents.examples.subordinate;

import com.github.openborders.wcomponents.WHorizontalRule;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;

/**
 * A collection of SubordinateControl examples. This portfolio example is used 
 * for unit testing. See individual examples for more specific information.
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class SubordinateControlAllExamples extends WPanel
{
    /**
     * Creates a SubordinateControlAllExamples.
     */
    public SubordinateControlAllExamples()
    {
        this.setLayout(new FlowLayout(Alignment.VERTICAL));

        add(new SubordinateControlMandatoryExample());
        add(new WHorizontalRule());
        add(new SubordinateControlSimpleExample());
        add(new WHorizontalRule());
        add(new SubordinateControlSimpleWDropdownExample());
        add(new WHorizontalRule());
        add(new SubordinateControlCrtWDropdownExample());
        add(new WHorizontalRule());
        add(new SubordinateControlSimpleWFieldExample());
        add(new WHorizontalRule());
        add(new SubordinateControlSimpleDisableExample());
        add(new WHorizontalRule());
        add(new SubordinateControlExample());
    }
}
