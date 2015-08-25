package com.github.openborders.examples.petstore; 

import java.text.DecimalFormat;

import com.github.openborders.WBeanContainer;
import com.github.openborders.WStyledText;
import com.github.openborders.WText;

import com.github.openborders.examples.petstore.model.InventoryBean;

/**
 * CostRenderer renders the cost of the inventory item, and 
 * indicates whether it is on sale.
 * 
 * Expects an InventoryBean as its bean value. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class InventoryCostRenderer extends WBeanContainer
{
    /**
     * Creates an InventoryCostRenderer.
     */
    public InventoryCostRenderer()
    {
        // Add a custom WText which renders a formatted value
        add(new WText()
        {
            /** {@inheritDoc} */
            @Override
            public String getText()
            {
                InventoryBean item = (InventoryBean) getBean();
                
                if (item != null)
                {
                    return new DecimalFormat("$0.00").format(item.getUnitCost() / 100.0);
                }
                
                return "$-.--"; // Error
            }
        });

        // Add a custom WStyledText which is only visible if the item is on special
        add(new WStyledText(" -- on special!", WStyledText.Type.EMPHASISED)
        {
            /** {@inheritDoc} */
            @Override
            public boolean isVisible() 
            {
                InventoryBean item = (InventoryBean) getBean();
                return item != null && item.getStatus() == InventoryBean.STATUS_SPECIAL;
            };
        });
    }
}
