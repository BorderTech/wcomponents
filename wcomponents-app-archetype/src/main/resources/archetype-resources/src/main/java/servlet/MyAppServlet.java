package ${package}.servlet;

import ${package}.ui.MyApp;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.registry.UIRegistry;
import com.github.bordertech.wcomponents.servlet.WServlet;

/**
 * This class enables hosting of wcomponents in a servlet container. To get a basic
 * wcomponent application running in a servlet container:
 *
 * <ul>
 *   <li>Build your own WComponent application from building block WComponents.</li>
 *   <li>Extend WServlet, overriding the getUI(Object) method to return your wcomponent application.</li>
 *   <li>Add your WServlet to your web.xml file.</li>
 * </ul>
 */
public class MyAppServlet extends WServlet
{
    /**
     * This method returns the top-level component for the application.
     * The component should be an instance of WApplication and be obtained
     * using the UIRegistry, e.g.
     *
     * <pre>
     *    return UIRegistry.getInstance().getUI(MyApp.class.getName());
     * </pre>
     *
     * @param httpServletRequest the servlet request being handled.
     * @return the top-level WComponent for this application.
     */
    @Override
    public WComponent getUI(final Object httpServletRequest)
    {
        return UIRegistry.getInstance().getUI(MyApp.class.getName());
    }
}
