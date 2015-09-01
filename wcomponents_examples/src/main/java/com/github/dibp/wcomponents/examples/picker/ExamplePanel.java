package com.github.dibp.wcomponents.examples.picker; 

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.dibp.wcomponents.WApplication;
import com.github.dibp.wcomponents.WComponent;
import com.github.dibp.wcomponents.WDefinitionList;
import com.github.dibp.wcomponents.WHorizontalRule;
import com.github.dibp.wcomponents.WMessages;
import com.github.dibp.wcomponents.WNamingContext;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTabSet;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.util.StreamUtil;

import com.github.dibp.wcomponents.examples.common.AccessibilityWarningPanel;

/**
 * This panel displays the currently selected example.
 * 
 * It provides some protection against bad example code, and
 * will display an error message rather than failing. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
final class ExamplePanel extends WPanel
{
    /**
     * Logger for this class.
     */
    private static final Log log = LogFactory.getLog(ExamplePanel.class);
    
    /** The container to add the example to. */
    private final SafetyContainer container = new SafetyContainer();
    
    /** The container to display the source code. */
    private final SourcePanel source = new SourcePanel();
    
    /** The tab set containing the example and source. */
    private final WTabSet tabset = new WTabSet();
    
    /**
     * Creates an ExamplePanel.
     */
    public ExamplePanel()
    {
        setType(Type.CHROME);
        add(tabset);
        
        WNamingContext context = new WNamingContext("eg");
        context.add(container);
        
        tabset.addTab(context, "(no selection)", WTabSet.TAB_MODE_CLIENT);
        tabset.addTab(source, "Source", WTabSet.TAB_MODE_LAZY);
        
        container.add(new WText("Select an example from the menu"));
        add(new AccessibilityWarningPanel());
    }

    /**
     * Selects an example.
     * 
     * @param example the example to select.
     * @param exampleName the name of the example being selected. 
     */
    public void selectExample(final WComponent example, final String exampleName)
    {
        WComponent currentExample = container.getChildAt(0).getParent();
        
        if (currentExample != null && currentExample.getClass().equals(example.getClass()))
        {
            // Same example selected, do nothing
            return;
        }
        
        resetExample();
        container.removeAll();

        setTitleText(exampleName);
        
        WApplication app = WebUtilities.getAncestorOfClass(WApplication.class, this);
        if(app != null)
        {
            app.setTitle(exampleName);
        }

        
        if (example instanceof ErrorComponent)
        {
            tabset.getTab(0).setText("Error");
            source.setSource(null);
        }
        else
        {
            String className = example.getClass().getName();
            WDefinitionList list = new WDefinitionList(WDefinitionList.Type.COLUMN);
            container.add(list);
            list.addTerm("Example path",new WText(className.replaceAll("\\.", " / ")));
            list.addTerm("Example JavaDoc", new JavaDocText(getSource(className)));
            container.add(new WHorizontalRule());
            tabset.getTab(0).setText(example.getClass().getSimpleName());
            source.setSource(getSource(className));
        }
        
        container.add(example);
        example.setLocked(true);
    }
    
    /**
     * Selects an example. If there is an error instantiating the component,
     * an error message will be displayed. 
     * 
     * @param example the ExampleData of the example to select.
     */
    public void selectExample(final ExampleData example)
    {
        try
        {
            StringBuilder exampleName = new StringBuilder();
            if (example.getExampleGroupName() != null && !example.getExampleGroupName().equals(""))
            {
                exampleName.append(example.getExampleGroupName()).append(" - ");
            }
            exampleName.append(example.getExampleName());
            selectExample(example.getExampleClass().newInstance(), exampleName.toString());
        }
        catch (Exception e)
        {
            WMessages.getInstance(this).error("Error selecting example \"" + example.getExampleName() + '"');
            selectExample(new ErrorComponent(e.getMessage(), e), "Error");
        }
    }

    /**
     * Resets the currently selected example.
     */
    public void resetExample()
    {
        container.resetContent();
    }
    
    /**
     * Tries to obtain the source file for the given class.
     * 
     * @param className the name of the class to find the source for.
     * @return the source file for the given class, or null on error.
     */
    private static String getSource(final String className)
    {
        String sourceName = '/' + className.replace('.', '/') + ".java";
 
        InputStream stream = null;
        
        try
        {
            stream = ExamplePanel.class.getResourceAsStream(sourceName);
            
            if (stream != null)
            {
                byte[] sourceBytes = StreamUtil.getBytes(stream);
                
                // we need to do some basic formatting of the source now.                
                return new String(sourceBytes, "UTF-8");
            }
        }
        catch (IOException e)
        {
            log.warn("Unable to read source code for class " + className, e);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {
                    log.error("Error closing stream", e);
                }
            }
        }
        
        return null;
    }
}
