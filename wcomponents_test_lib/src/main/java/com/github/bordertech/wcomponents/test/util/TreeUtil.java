package com.github.bordertech.wcomponents.test.util; 

import java.util.List;

import com.github.bordertech.wcomponents.ComponentWithContext;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.WComponent;

/** 
 * Utility methods for navigating WComponent trees. 
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 * @deprecated use com.github.bordertech.wcomponents.util.TreeUtil
 */
@Deprecated
public final class TreeUtil
{
    /** Prevent instantiation of utility class. */
    private TreeUtil()
    {
    }
    
    /**
     * Obtains a list of components which are visible in the given tree.
     * Repeated components will be returned multiple times, one for each row which they are visible in.
     * 
     * @param comp the root component to search from.
     * @return a list of components which are visible in the given context.
     */
    public static List<ComponentWithContext> collateVisibles(final WComponent comp)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.collateVisibles(comp);
    }
    
    /**
     * Retrieves the root component of a WComponent hierarchy.
     * 
     * @param uic the context to retrieve the root component for.
     * @param comp a component in the tree.
     * @return the root of the tree.
     */
    public static WComponent getRoot(final UIContext uic, final WComponent comp)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.getRoot(uic, comp);
    }
    
    /**
     * Retrieves the component with the given Id.
     * 
     * @param root the root component to search from.
     * @param id the id to search for.
     * @return the component with the given id, or null if not found.
     */
    public static WComponent getComponentWithId(final WComponent root, final String id)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.getComponentWithId(root, id);
    }
    
    /**
     * Retrieves the context for the component with the given Id.
     * 
     * @param root the root component to search from.
     * @param id the id to search for.
     * @return the context for the component with the given id, or null if not found.
     */
    public static UIContext getContextForId(final WComponent root, final String id)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.getContextForId(root, id);
    }

    /**
     * General utility method to visit every WComponent in the tree, taking repeaters etc. into account.
     * 
     * @param node the node to traverse.
     * @param visitor the visitor to notify as the tree is traversed.
     */
    public static void traverse(final WComponent node, final WComponentTreeVisitor visitor)
    {
        com.github.bordertech.wcomponents.util.TreeUtil.traverse(node, visitor);
    }

    /**
     * General utility method to visit every visible WComponent in the tree, taking repeaters etc. into account.
     * 
     * @param node the node to traverse.
     * @param visitor the visitor to notify as the tree is traversed.
     */
    public static void traverseVisible(final WComponent node, final WComponentTreeVisitor visitor)
    {        
        com.github.bordertech.wcomponents.util.TreeUtil.traverseVisible(node, visitor);
    }

    /**
     * Retrieves WComponents by their path in the WComponent tree.
     * 
     * <p>Paths are specified using class names, starting from the furthest ancestor.
     * To reduce the path lengths, class names do not need to be fully-qualified. The
     * path does not need to explicitly state intermediate components between components,
     * and may include an index suffix to select a particular instance of a component
     * in e.g. a repeater or a set of fields. Some example paths are shown below.</p>
     *
     * Example paths.
     * <dl>
     *    <dt><code>{ "MyComponent" }</code></dt>
     *    <dd>Matches the first instance of MyComponent.</dd>
     *    <dt><code>{ "MyComponent[0]" }</code></dt>
     *    <dd>Also matches the first instance of MyComponent.</dd>
     *    <dt><code>{ "MyComponent[1]" }</code></dt>
     *    <dd>Matches the second instance of MyComponent.</dd>
     *    <dt><code>{ "MyPanel", "MyComponent" }</code></dt>
     *    <dd>Matches the first instance of MyComponent which is nested anywhere under a MyPanel.</dd>
     *    <dt><code>{ "MyApp", "MyPanel", "MyComponent" }</code></dt>
     *    <dd>Matches the first instance of MyComponent, nested within a MyPanel, which is in turn nested somewhere within a MyApp.</dd>
     * </dl>
     * 
     * @param component the component to search from.
     * @param path the path to the WComponent.
     * @return the component matching the given path, or null if not found.
     */
    public static ComponentWithContext[] findWComponents(final WComponent component, final String[] path)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.findWComponents(component, path);
    }
    
    /**
     * Retrieves the first WComponent by its path in the WComponent tree.
     * 
     * See {@link #findWComponents(WComponent, UIContext, String[])} for a description of paths.
     * 
     * @param component the component to search from.
     * @param path the path to the WComponent.
     * @return the first component matching the given path, or null if not found.
     */
    public static ComponentWithContext findWComponent(final WComponent component, final String[] path)
    {
        return com.github.bordertech.wcomponents.util.TreeUtil.findWComponent(component, path);
    }
    
    /**
     * An implementation of WComponentTreeVisitor which can return a result.
     */
    public abstract static class AbstractTreeVisitorWithResult<T> extends com.github.bordertech.wcomponents.util.TreeUtil.AbstractTreeVisitorWithResult
    {
    }
}
