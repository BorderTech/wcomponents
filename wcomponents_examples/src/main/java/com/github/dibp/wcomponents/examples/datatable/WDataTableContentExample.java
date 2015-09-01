package com.github.dibp.wcomponents.examples.datatable; 

import com.github.dibp.wcomponents.Image;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.SimpleTableDataModel;
import com.github.dibp.wcomponents.TableDataModel;
import com.github.dibp.wcomponents.WDataTable;
import com.github.dibp.wcomponents.WImage;
import com.github.dibp.wcomponents.WPanel;
import com.github.dibp.wcomponents.WTableColumn;
import com.github.dibp.wcomponents.WText;

import com.github.dibp.wcomponents.examples.DynamicImage;

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
