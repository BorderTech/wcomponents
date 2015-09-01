package com.github.dibp.wcomponents.examples;

import java.util.ArrayList;
import java.util.List;

import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WDataRenderer;
import com.github.dibp.wcomponents.WHorizontalRule;
import com.github.dibp.wcomponents.WRepeater;

/**
 * <p>This example demonstrates that focus will be returned to the control 
 * which triggered the form submit after a round trip to the server completes.</p>
 * 
 * <p>This example is similar to the {@link AutoReFocusExample}, except
 * that it demonstrates that focus is correctly set even when the components
 * are repeated.</p>
 * 
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public class AutoReFocusRepeaterExample extends WContainer
{
    /** The repeater used in this example. */
    private final WRepeater repeater = new WRepeater(new FocusRepeatRenderer());
    
    /** 
     * Creates an AutoReFocusRepeaterExample. 
     */
    public AutoReFocusRepeaterExample()
    {
        add(repeater);
    }
    
    /**
     * Override preparePaintComponent in order to set up the example data the first time through.
     * 
     * @param request the request being responded to.
     */
    @Override
    public void preparePaintComponent(final Request request)
    {
        if (!this.isInitialised())
        {
            List<String> dummyList = new ArrayList<String>(2);
            dummyList.add("a");
            dummyList.add("b");
            
            repeater.setData(dummyList);
            
            this.setInitialised(true);
        }
    }

    /**
     * The render to use with the repeater in this example. 
     * @author Martin Shevchenko
     */
    public static class FocusRepeatRenderer extends WDataRenderer
    {
        /**
         * Creates a FocusRepeatRenderer.
         */
        public FocusRepeatRenderer()
        {
            add(new WDropdownTriggerActionExample());
            add(new WRadioButtonTriggerActionExample());
            add(new WHorizontalRule());
        }
    }
}
