package com.github.openborders.examples;

import com.github.openborders.Action;
import com.github.openborders.ActionEvent;
import com.github.openborders.RadioButtonGroup;
import com.github.openborders.WContainer;
import com.github.openborders.WDropdown;
import com.github.openborders.WHeading;
import com.github.openborders.WLabel;
import com.github.openborders.WPanel;
import com.github.openborders.WRadioButton;
import com.github.openborders.WStyledText;
import com.github.openborders.WText;

/**
 * This example demonstrates the use of the submitOnChange flag available on
 * WDropdown. When you change the selected state in the state dropdown, the
 * options available in the region dropdown are changed to match. Also, if you
 * select "ACT", you get a special message displayed.
 *  
 * @author Ming Gao
 * @since 1.0.0
 */
public class WRadioButtonSubmitOnChangeExample extends WContainer
{
    private static final String ACT = "ACT";

    private static final String NSW = "NSW";

    private static final String VIC = "VIC";

    private final RadioButtonGroup rbgStateSelector = new RadioButtonGroup();
    
    private final WRadioButton rbtACT = rbgStateSelector.addRadioButton(ACT);

    private final WRadioButton rbtNSW = rbgStateSelector.addRadioButton(NSW);

    private final WRadioButton rbtVIC = rbgStateSelector.addRadioButton(VIC);

    private final WPanel regionPanel = new WPanel();

    private final WDropdown regionSelector = new WDropdown();

    private final WPanel actMessagePanel = new WPanel();
    
    /**
     * Creates a WRadioButtonSubmitOnChangeExample.
     */
    public WRadioButtonSubmitOnChangeExample()
    {
        rbgStateSelector.setSubmitOnChange(true);
        rbgStateSelector.setActionOnChange(new Action()
        {
            public void execute(final ActionEvent event)
            {
                updateRegion();
                regionSelector.resetData();
            }
        });
        
        add(new WHeading(WHeading.SECTION, "State"));
        
        add(new WLabel(ACT, rbtACT));
        add(rbtACT);
        add(new WLabel(NSW, rbtNSW));
        add(rbtNSW);
        add(new WLabel(VIC, rbtVIC));
        add(rbtVIC);
        
        add(rbgStateSelector);
        
        add(regionPanel);
        regionPanel.add(new WHeading(WHeading.SECTION, "Region"));
        regionPanel.add(regionSelector);

        regionSelector.setAccessibleText("Region");
        
        add(actMessagePanel);
        actMessagePanel.add(new WStyledText("Australian Capital Territory", WStyledText.Type.EMPHASISED));
        actMessagePanel.add(new WText(" - the heart of the nation!"));

        updateRegion();
        
    }

    /**
     * Updates the visibility and options present in the region selector,
     * depending on the state selector's value.
     */
    private void updateRegion()
    {
        actMessagePanel.setVisible(false);

        if (rbtACT.isSelected())
        {
            actMessagePanel.setVisible(true);
            regionPanel.setVisible(true);
            regionSelector.setOptions(new String[] { null, "Belconnen", "City", "Woden" });
            regionSelector.setVisible(true);
        }
        else if (rbtNSW.isSelected())
        {
            regionPanel.setVisible(true);
            regionSelector.setOptions(new String[] { null, "Hunter", "Riverina", "Southern Tablelands" });
            regionSelector.setVisible(true);
        }
        else if (rbtVIC.isSelected())
        {
            regionPanel.setVisible(true);
            regionSelector.setOptions(new String[] { null, "Gippsland", "Melbourne", "Mornington Peninsula" });
            regionSelector.setVisible(true);
        }
        else
        {
            regionSelector.setOptions(new Object[]{null});
            regionSelector.setVisible(false);
            regionPanel.setVisible(false);
        }
    }
}
