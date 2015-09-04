package com.github.bordertech.wcomponents.examples.theme;

import java.util.Arrays;

import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WField;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WMultiDropdown;

/**
 * Examples use of the {@link WMultiDropdown} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMultiDropdownExample extends WContainer 
{
    /** Simple data used by the example. */
    private static final String[] data = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    
    /** The field layout used to display the example dropdowns. */
    private final WFieldLayout layout = new WFieldLayout(); 
        
    /**
     * Creates a WMultiDropdownExample.
     */
	public WMultiDropdownExample()
    {
        add(new WHeading(WHeading.SECTION, "Dynamic Multi-dropdown examples"));
        add(layout);

        WMultiDropdown dropdown = new WMultiDropdown("icao");
        layout.addField("Dynamic multi-dropdown 1", dropdown);
        
        
        dropdown = new WMultiDropdown(data);
        dropdown.setMaxSelect(5);
        layout.addField("Dynamic multi-dropdown 2", dropdown);

        dropdown = new WMultiDropdown(data);
        dropdown.setDisabled(true);
        layout.addField("Dynamic multi-dropdown 3", dropdown);

        dropdown = new WMultiDropdown(data);
        dropdown.setSelected(Arrays.asList(new String[]{data[0]}));
        layout.addField("Dynamic multi-dropdown 4", dropdown);

        dropdown = new WMultiDropdown(data);
        dropdown.setSelected(Arrays.asList(new String[]{data[0], data[1], data[2]}));
        layout.addField("Dynamic multi-dropdown 5", dropdown);

        dropdown = new WMultiDropdown(data);
        dropdown.setSelected(Arrays.asList(new String[]{data[0], data[1], data[2], data[3], data[4]}));
        dropdown.setDisabled(true);
        dropdown.setMaxSelect(5);
        layout.addField("Dynamic multi-dropdown 6", dropdown);

        WButton refresh = new WButton("Refresh");
        add(refresh);
    }
	
}
