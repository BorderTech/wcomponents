package com.github.openborders.wcomponents.render.nil;

import com.github.openborders.wcomponents.RenderContext;
import com.github.openborders.wcomponents.Renderer;
import com.github.openborders.wcomponents.WComponent;

/** 
 * The layout factory for the nil renderer package.
 * This factory produces a renderer which doesn't do anything,
 * and is useful for testing.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class RendererFactory implements com.github.openborders.wcomponents.RendererFactory
{
    /** The singleton NullRenderer instance. */
    private static final Renderer NULL_RENDERER = new NullRenderer();

    /** {@inheritDoc} */
    public Renderer getRenderer(final Class<?> clazz)
    {
        return NULL_RENDERER;
    }
    
    /** {@inheritDoc} */
    public Renderer getTemplateRenderer()
    {
        return NULL_RENDERER;
    }

    /** A no-op renderer. */
    private static final class NullRenderer implements Renderer
    {
        /**
         * Doesn't do anything.
         * @param component ignored.
         * @param renderContext ignored.
         */
        public void render(final WComponent component, final RenderContext renderContext)
        {
            // NO-OP
        }
    };
}
