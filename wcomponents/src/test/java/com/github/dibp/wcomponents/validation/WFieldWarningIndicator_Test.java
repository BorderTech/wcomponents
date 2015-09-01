package com.github.dibp.wcomponents.validation;

import junit.framework.Assert;

import org.junit.Test;

import com.github.dibp.wcomponents.AbstractWComponentTestCase;
import com.github.dibp.wcomponents.WTextField;

/**
 * WFieldWarningIndicator_Test - unit tests for {@link WFieldWarningIndicator}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WFieldWarningIndicator_Test extends AbstractWComponentTestCase
{
    @Test
    public void testConstructor()
    {
        WTextField component = new WTextField();
        WFieldWarningIndicator indicator = new WFieldWarningIndicator(component);

        Assert.assertEquals("Incorrect indicator type", AbstractWFieldIndicator.FieldIndicatorType.WARN, indicator
            .getFieldIndicatorType());
        Assert.assertEquals("Incorrect releated field", component, indicator.getRelatedField());
        
        Assert.assertEquals("Incorrect releated field id", component.getId(), indicator.getRelatedFieldId());
    }
}
