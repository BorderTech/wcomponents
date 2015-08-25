package com.github.openborders.examples.picker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.InternalResource;
import com.github.openborders.Message;
import com.github.openborders.Request;
import com.github.openborders.UIContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.WApplication;
import com.github.openborders.WComponent;
import com.github.openborders.WContent;
import com.github.openborders.WMessages;
import com.github.openborders.WSkipLinks;
import com.github.openborders.WebUtilities;
import com.github.openborders.util.Config;
import com.github.openborders.util.Util;

/**
 * <p>A component which enables users to pick an example to display. The UI is provided by either delegate
 * {@link SimplePicker} or {@link TreePicker}, depending on the configuration parameters. By default, the TreePicker is
 * used. To use the (old) simple example picker, set the following parameter in e.g. your local_app.properties.</p>
 * 
 * <p>It also demonstrates how to add additional functionality on the client, by
 * performing syntax highlighting of the java source code using javascript/css.</p>
 * 
 * <pre>
 * com.github.openborders.examples.picker.ExamplePicker.ui = com.github.openborders.examples.picker.SimplePicker
 * </pre>
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExamplePicker extends WApplication
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(ExamplePicker.class);

    /** The parameter key controlling which UI is displayed. */
    private static final String PARAM_KEY = "com.github.openborders.examples.picker.ExamplePicker.ui";

    /** Used to display step error messages. */
    private final WMessages messages = new WMessages();

    /** Additional Javascript used to provide syntax-highlighting client-side. */
    private final WContent javascript = new WContent();
    
    /** Additional CSS used to provide syntax-highlighting client-side. */
    private final WContent css = new WContent();
    
    /**
     * Construct the example picker.
     */
    public ExamplePicker()
    {
        add(messages);

        String className = Config.getInstance().getString(PARAM_KEY, TreePicker.class.getName());

        try
        {
            WComponent ui = (WComponent) Class.forName(className).newInstance();
            add(ui);
            
        }
        catch (Exception e)
        {
            add(new ErrorComponent("Unable to load picker ui " + className, e));
        }

        String version = Config.getInstance().getString("wcomponents_examples.version");
        javascript.setCacheKey("wc.examplepicker.js." + version);
        css.setCacheKey("wc.examplepicker.css." + version);

        add(javascript);
        add(css);
    }

    /**
     * If a step error has occurred, then display an error message to the user.
     */
    @Override
    public void handleStepError()
    {
        messages.addMessage(new Message(Message.WARNING_MESSAGE,
                            "A request was made that is not in the expected sequence and the application has been refreshed to its current state."));
    }
    
    /**
     * Override preparePaint in order to set up the resources on first access by a user.
     * 
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        
        if (!isInitialised())
        {
            // Check project versions for Wcomponents_examples and WComponents match
            String egVersion = Config.getInstance().getString("wcomponents_examples.version");
            String wcVersion = WebUtilities.getProjectVersion();
            
            if (egVersion != null && !egVersion.equals(wcVersion))
            {
                String msg = "WComponents_Examples version (" + egVersion + ") does not match WComponents version ("
                             + wcVersion + ").";
                
                log.error(msg);
                
                messages.addMessage(new Message(Message.ERROR_MESSAGE, msg));
            }

            javascript.setContentAccess(new InternalResource("/com/github/openborders/examples/syntaxHighlight.js", "syntaxHighlight.js"));
            css.setContentAccess(new InternalResource("/com/github/openborders/examples/syntaxHighlight.css", "syntaxHighlight.css"));
            setInitialised(true);
        }
        
        UIContext uic = UIContextHolder.getCurrent();
        uic.getHeaders().addUniqueHeadLine("<script type='text/javascript' src='" + WebUtilities.encode(javascript.getUrl()) + "'></script>");
        uic.getHeaders().addUniqueHeadLine("<link type='text/css' rel='stylesheet' href='" + WebUtilities.encode(css.getUrl()) + "'></link>");
        
    }
}
