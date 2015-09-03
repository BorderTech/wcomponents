package com.github.bordertech.wcomponents.examples.datatable; 

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.LogFactory;

import com.github.bordertech.wcomponents.AbstractTreeTableDataModel;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.TableTreeNode;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WDataTable;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTableColumn;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WDataTable.ExpandMode;

/** 
 * This example shows the use of a {@link WDataTable} with hierarchical data. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class TreeTableHierarchyExample extends WPanel
{
    /** Example table. */
    private final WDataTable table = createTable();    
    
    /**
     * Creates a TreeTableHierarchyExample.
     */
    public TreeTableHierarchyExample()
    {
        WDataTable table = createTable();
        
        add(table);
        add(new WButton("Submit"));
    }
    
    /**
     * Creates and configures the table to be used by the example.
     * The table is configured with global rather than user data. 
     * Although this is not a realistic scenario, it will suffice 
     * for this example.
     * 
     * @return a new configured table.
     */
    private WDataTable createTable()
    {
        WDataTable table = new WDataTable();
        table.setType(WDataTable.Type.HIERARCHIC);
        table.addColumn(new WTableColumn("First name", new WText()));
        table.addColumn(new WTableColumn("Last name", new WText()));
        table.addColumn(new WTableColumn("DOB", new WDateField()));
        table.setExpandMode(ExpandMode.CLIENT);
        table.setStripingType(WDataTable.StripingType.ROWS);
        
        TableTreeNode root = createTree();
        table.setDataModel(new ExampleTreeTableModel(root));
        
        return table;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        super.preparePaintComponent(request);
        if (!isInitialised())
        {
            TableTreeNode root = createTree();
            table.setDataModel(new ExampleTreeTableModel(root));
            setInitialised(true);
        }
    }
    
    /**
     * @return a tree containing the data for this example.
     */
    private TableTreeNode createTree()
    {
        TableTreeNode root = new TableTreeNode(null);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        
        try
        {
            TableTreeNode joe = new TableTreeNode(new PersonBean("Joe", "Bloggs", sdf.parse("01/02/1934")));
            TableTreeNode jane = new TableTreeNode(new PersonBean("Jane", "Bloggs", sdf.parse("02/03/1935"))); 
            TableTreeNode child = new TableTreeNode(new PersonBean("Child", "Bloggs", sdf.parse("03/04/1976")));
            TableTreeNode grandChild1 = new TableTreeNode(new PersonBean("Grandchild-One", "Bloggs", sdf.parse("04/05/2006")));
            TableTreeNode grandChild2 = new TableTreeNode(new PersonBean("Grandchild-Two", "Bloggs", sdf.parse("05/06/2007")));
            
            root.add(joe);
            root.add(jane);
            joe.add(child);
            child.add(grandChild1);
            child.add(grandChild2);
        }
        catch (ParseException e)
        {
            LogFactory.getLog(getClass()).error("Failed to create test data", e);
        }
        
        return root;
    }
    
    /**
     * An example tree table data model implementation. 
     * 
     * @author Yiannis Paschalidis
     * @since 1.0.0
     */
    private static final class ExampleTreeTableModel extends AbstractTreeTableDataModel
    {
        /** The first name column id. */
        private static final int FIRST_NAME = 0;
        
        /** The last name column id. */
        private static final int LAST_NAME = 1;
        
        /** The date of birth column id. */
        private static final int DOB = 2;

        /**
         * Creates an ExampleTreeTableModel.
         * @param root the root of the tree for this model.
         */
        public ExampleTreeTableModel(final TableTreeNode root)
        {
            super(root);
        }
        
        /** {@inheritDoc} */
        public Object getValueAt(final TableTreeNode node, final int col)
        {
            PersonBean personBean = (PersonBean) node.getData();

            switch (col)
            {
                case FIRST_NAME:
                    return personBean.getFirstName();
                    
                case LAST_NAME:
                    return personBean.getLastName();
                    
                case DOB:
                {
                    if (personBean.getDateOfBirth() == null)
                    {
                        return null;
                    }
                    
                    return personBean.getDateOfBirth();
                }
                
                default:
                    return null;
            }
        }
    }
}
