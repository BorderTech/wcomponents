package com.github.dibp.wcomponents.subordinate;

import com.github.dibp.wcomponents.SubordinateTarget;
import com.github.dibp.wcomponents.WLabel;

/**
 * An action that enables a given target component.
 * 
 * @author Martin Shevchenko
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class Enable extends AbstractSetEnable
{
    /**
     * Creates an enable action with the given target.
     * 
     * @param target the component to enable.
     */
    public Enable(final SubordinateTarget target)
    {
        super(target, Boolean.TRUE);
    }

    /**
     * @return an action type of enable.
     */
    public ActionType getActionType()
    {
        return ActionType.ENABLE;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        String targetName = getTarget().getClass().getSimpleName();

        WLabel label = getTarget().getLabel();
        if (label != null)
        {
            targetName = label.getText();
        }

        return "enable " + targetName;
    }
}
