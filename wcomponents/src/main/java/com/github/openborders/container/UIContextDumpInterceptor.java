package com.github.openborders.container; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.RenderContext;
import com.github.openborders.UIContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.util.Config;
import com.github.openborders.util.ObjectGraphDump;
import com.github.openborders.util.ObjectGraphNode;

/**
 * SessionDumpInterceptor is an interceptor that dumps the 
 * contents of the session to the log file.
 * 
 * For local testing/troubleshooting, the filter can be enabled by setting the following property:
 *      com.github.openborders.container.UIContextDumpInterceptor.enabled=true
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class UIContextDumpInterceptor extends InterceptorComponent
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(UIContextDumpInterceptor.class);
    
    /** The key used to look up the "enabled" flag in the {@link Config WComponent Configuration}. */
    private static final String PARAMETERS_ENABLED_KEY = 
        "wcomponent.developer.UIContextDump.enabled";

    /** The key used to look up the single flag in the {@link Config WComponent Configuration}. */
    private static final String UICONTEXT_TREE_DUMPED_KEY = 
        "com.github.openborders.container.UIContextDumpInterceptor.treeDumped";
    
    /**
     * Paints the component.
     * 
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        boolean enabled = Config.getInstance().getBoolean(PARAMETERS_ENABLED_KEY, false);

        super.paint(renderContext);
        
        if (enabled)
        {
            UIContext uic = UIContextHolder.getCurrent();
            
            // We want to dump the shared WComponent tree (UIC) first, so that all the 
            // references from the UI context are shown as such
            Object[] treeAndSession = 
                new Object[]
                {
                     uic.getUI(),
                     uic
                };
            
            try
            {
                ObjectGraphNode root = ObjectGraphDump.dump(treeAndSession);

                // We only want to dump the tree once
                boolean treeDumped = "true".equalsIgnoreCase((String) uic.getFwkAttribute(UICONTEXT_TREE_DUMPED_KEY));

                if (!treeDumped)
                {
                    uic.setFwkAttribute(UICONTEXT_TREE_DUMPED_KEY, "true");

                    // The UI will be the 1st child
                    log.info("UI dump follows:");
                    log.info(((ObjectGraphNode) root.getChildAt(0)).toXml());
                }

                // The UI context will be the 2nd child
                log.info("UI Context dump follows:");
                log.info(((ObjectGraphNode) root.getChildAt(1)).toXml());
            }
            catch (Exception e)
            {
                log.error("Failed to dump context", e);
            }
        }
    }
}
