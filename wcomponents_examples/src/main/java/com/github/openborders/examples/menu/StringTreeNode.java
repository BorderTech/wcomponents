package com.github.openborders.examples.menu;

import com.github.openborders.util.AbstractTreeNode;

/**
 * Trivial implementation of a tree node, in order to show how to 
 * transform a tree structure into a menu structure.
 * 
 * @author Yiannis Paschalidis 
 */
final class StringTreeNode extends AbstractTreeNode 
{
    /** The data for this node. */
    private final String data;
    
    /**
     * Creates a StringTreeNode.
     * @param data the node data.
     */
    public StringTreeNode(final String data)
    {
        this.data = data;
    }

    /** @return the node data. */
    public String getData()
    {
        return data;
    }
}
