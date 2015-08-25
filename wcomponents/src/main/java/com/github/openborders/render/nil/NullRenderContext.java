package com.github.openborders.render.nil; 

import com.github.openborders.RenderContext;

/** 
 * The NullRenderContext produces no output and is useful for testing.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class NullRenderContext implements RenderContext
{
    /** {@inheritDoc} */
    public String getRenderPackage()
    {
        return "com.github.openborders.render.nil";
    }
}
