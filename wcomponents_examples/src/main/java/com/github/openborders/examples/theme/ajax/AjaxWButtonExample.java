package com.github.openborders.examples.theme.ajax;

import com.github.openborders.WAjaxControl;
import com.github.openborders.WContainer;

import com.github.openborders.examples.WImageExample;

/**
 * In this example the button from {@link WImageExample} is made an AJAX trigger. The image is an AJAX target. So rather
 * than refreshing the whole page to change the image, only the image is replaced via the AJAX response.
 * 
 * @author Christina Harris
 * @since 1.0.0
 */
public class AjaxWButtonExample extends WContainer
{
    /** The image example which is modified to be AJAX capable. */
    private final WImageExample example = new WImageExample();
    
    /**
     * Creates an AjaxWButtonExample.
     */
    public AjaxWButtonExample()
    {
        add(example);

        // make the image change an ajax request
        add(new WAjaxControl(example.getChangeImageButton(), example.getWImage()));
    }
}
