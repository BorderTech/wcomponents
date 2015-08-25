package com.github.openborders.examples.picker; 

import java.io.PrintWriter;

import com.github.openborders.RenderContext;
import com.github.openborders.WPanel;
import com.github.openborders.WText;
import com.github.openborders.WebUtilities;
import com.github.openborders.servlet.WebXmlRenderContext;

/** 
 * <p>This component displays the java source code for the WComponent examples.
 * 
 * @author Yiannis Paschalidis 
 */
public class SourcePanel extends WPanel
{
    /** The source code. */
    private final WText source = new WText();
    
    /**
     * Creates a SourcePanel.
     */
    public SourcePanel()
    {
        source.setEncodeText(false);
        add(source);        
    }
    
    /**
     * Sets the source code to be displayed in the panel.
     * 
     * @param sourceText the source code to display.
     */
    public void setSource(final String sourceText)
    {
        String formattedSource;
        
        if (sourceText == null)
        {
            formattedSource = "";
        }
        else
        {
            formattedSource = sourceText.replace(' ', '\u00a0'); // nbsp
            formattedSource = WebUtilities.encode(formattedSource); // escape content
            formattedSource = formattedSource.replaceAll("\\r?\\n", "<br/>");
        }

        source.setText(formattedSource);
    }
    
    /**
     * Override afterPaint in order to render the additional mark-up required
     * for client-side syntax highligthing.
     * 
     * @param renderContext the renderContext to send output to.
     */
    @Override
    protected void afterPaint(final RenderContext renderContext)
    {
        super.afterPaint(renderContext);
        
        if (renderContext instanceof WebXmlRenderContext)
        {
            PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
            
            // Kick of the syntax highlighting
            writer.write("<script type='text/javascript'>if (window.doHighlighting) doHighlighting('" + getId() + "');</script>");
        }
    }
}
