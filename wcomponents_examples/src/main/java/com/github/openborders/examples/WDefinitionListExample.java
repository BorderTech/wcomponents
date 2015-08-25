package com.github.openborders.examples; 

import com.github.openborders.WContainer;
import com.github.openborders.WDefinitionList;
import com.github.openborders.WHeading;
import com.github.openborders.WText;

/** 
 * Demonstrate use of {@link WDefinitionList}.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WDefinitionListExample extends WContainer
{
    /**
     * Creates a WDefinitionListExample.
     */
    public WDefinitionListExample()
    {
        add(new WHeading(WHeading.SECTION, "Normal layout"));        
        WDefinitionList list = new WDefinitionList();
        addListItems(list);
        add(list);
        
        add(new WHeading(WHeading.SECTION, "Flat layout"));        
        list = new WDefinitionList(WDefinitionList.Type.FLAT);
        addListItems(list);
        add(list);        
        
        add(new WHeading(WHeading.SECTION, "Stacked layout"));        
        list = new WDefinitionList(WDefinitionList.Type.STACKED);
        addListItems(list);
        add(list);        
        
        add(new WHeading(WHeading.SECTION, "Column layout"));        
        list = new WDefinitionList(WDefinitionList.Type.COLUMN);
        addListItems(list);
        add(list);        
    }
    
    /**
     * Adds some items to a definition list.
     * @param list the list to add the items to.
     */
    private void addListItems(final WDefinitionList list)
    {
        // Example of adding multiple data items at once.
        list.addTerm("Colours", new WText("Red"), new WText("Green"), new WText("Blue"));
        
        // Example of adding multiple data items using multiple calls.
        list.addTerm("Shapes", new WText("Circle"));
        list.addTerm("Shapes", new WText("Square"), new WText("Triangle"));
    }
}
