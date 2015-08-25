package com.github.openborders.examples.datatable; 

import com.github.openborders.Image;
import com.github.openborders.Request;
import com.github.openborders.SimpleTableDataModel;
import com.github.openborders.TableDataModel;
import com.github.openborders.WDataTable;
import com.github.openborders.WImage;
import com.github.openborders.WPanel;
import com.github.openborders.WTableColumn;
import com.github.openborders.WText;

import com.github.openborders.examples.DynamicImage;

/** 
 * This example shows use of a {@link WDataTable}, with a two-dimensional array of data
 * and rendering of dynamic content using the WContentHelperServlet. The data will be 
 * held in the user's session. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public class WDataTableContentExample extends WPanel
{
    /** The table used in the example. */
    private final WDataTable table;
    
    /**
     * Creates a DataTableExample.
     */
    public WDataTableContentExample()
    {
        table = new WDataTable();
        table.addColumn(new WTableColumn("Name (text)", new WText()));
        table.addColumn(new WTableColumn("Name (image)", new DynamicWImage()));        
        add(table);
    }
    
    /**
     * Override preparePaintComponent in order to set up the example data 
     * the first time that the example is accessed by each user.
     * 
     * @param request the request being responded to.
     */
    @Override
    protected void preparePaintComponent(final Request request)
    {
        if (!isInitialised())
        {
            table.setDataModel(createTableModel());
            setInitialised(true);
        }
    }
    
    /**
     * Creates a simple table data model containing some dummy data.
     * @return a new data model.
     */
    private TableDataModel createTableModel()
    {
        String[][] data = new String[][]
        {
             new String[]{"Row 1", "Row 1"},
             new String[]{"Row 2", "Row 2"},
             new String[]{"Row 3", "Row 3"}
        };
        
        return new SimpleTableDataModel(data);
    }
    
    /**
     * A WImage implementation which displays a dynamic image
     * depending on its bean value. 
     */
    private static final class DynamicWImage extends WImage
    {
        /**
         * @return the image to be displayed.
         */
        @Override
        public Image getImage()
        {
            return new DynamicImage(((String) getData()));
        }
    }
    
}
