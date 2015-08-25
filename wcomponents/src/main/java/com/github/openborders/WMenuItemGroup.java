package com.github.openborders;


/**
 * This component provides a logical grouping of related menu items.
 * The rendered version in the UI usually provides a visual grouping
 * as well, including the menu item group's title.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WMenuItemGroup extends AbstractContainer implements Disableable
{
    private final WDecoratedLabel label;

    /**
     * Creates a new WMenuItem containing the specified button.
     * @param headingText the heading text for the group.
     */
    public WMenuItemGroup(final String headingText)
    {
        this(new WDecoratedLabel(headingText));
    }

    /**
     * Creates a new WMenuItem containing the specified button.
     * @param label the label for the group.
     */
    public WMenuItemGroup(final WDecoratedLabel label)
    {
        this.label = label;
        add(label);
    }
    
    /**
     * Adds a menu item to this group.
     * @param menuItem the menu item to add.
     */
    public void addMenuItem(final WMenuItem menuItem)
    {
        add(menuItem);
    }

    /**
     * Adds a menu item to this group.
     */
    public void addSeparator()
    {
        add(new WSeparator());
    }

    /**
     * @return returns the group heading text.
     */
    public String getHeadingText()
    {
        return label.getText();
    }

    /**
     * @return the decorated label for this menu item group
     */
    public WDecoratedLabel getDecoratedLabel()
    {
        return label;
    }

    /**
     * Sets the group heading text.
     * @param headingText the heading to set.
     */
    public void setHeadingText(final String headingText)
    {
        label.setText(headingText);
    }
    
    /**
     * Indicates whether this group is disabled.
     * @return true if this group is disabled.
     */
    public boolean isDisabled()
    {
        return isFlagSet(ComponentModel.DISABLED_FLAG);
    }

    /**
     * Sets whether this group is disabled.
     * @param disabled true to disable the group, false to enable it.
     */
    public void setDisabled(final boolean disabled)
    {
        setFlag(ComponentModel.DISABLED_FLAG, disabled);
    }
    
    /**
     * Add the given menu item as a child of this component.
     * @param item the item to add.
     */
    public void add(final WMenuItem item)
    {
        super.add(item);
    }

    /**
     * Add the given sub-menu as a child of this component.
     * 
     * @param item the sub-menu to add.
     */
    public void add(final WSubMenu item)
    {
        super.add(item);
    }
    
    /**
     * Add the given separator as a child of this component.
     * 
     * @param separator the separator to add.
     */
    public void add(final WSeparator separator)
    {
        super.add(separator);
    }

    /** {@inheritDoc} */
    @Override // to make public
    void remove(final WComponent child)
    {
        super.remove(child);
    }
    
    /**
     * @return a String representation of this component, for debugging purposes.
     */
    @Override
    public String toString()
    {
        String text = label.getText();
        text = text == null ? "null" : ('"' + text + '"');
        return toString(text, 1, getChildCount() - 1);
    }
}
