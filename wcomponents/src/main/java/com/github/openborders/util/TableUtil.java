package com.github.openborders.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.UIContext;
import com.github.openborders.UIContextHolder;
import com.github.openborders.WComponent;
import com.github.openborders.WTable;
import com.github.openborders.WebUtilities;
import com.github.openborders.WRepeater.SubUIContext;
import com.github.openborders.WTable.RowIdWrapper;

/**
 * Static utility methods related to working with {@link WTable}.
 * 
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class TableUtil
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(TableUtil.class);

    /** Row index delimiter. */
    public static final String INDEX_DELIMITER = "-";

    /** Prevent instantiation of utility class. */
    private TableUtil()
    {
    }

    /**
     * Convert the row index to its string representation.
     * 
     * @param row the row index
     * @return the string representation of the row index
     */
    public static String rowIndexListToString(final List<Integer> row)
    {
        if (row == null || row.isEmpty())
        {
            return null;
        }

        StringBuffer index = new StringBuffer();
        boolean addDelimiter = false;

        for (Integer lvl : row)
        {
            if (addDelimiter)
            {
                index.append(INDEX_DELIMITER);
            }
            index.append(lvl);
            addDelimiter = true;
        }

        return index.toString();
    }

    /**
     * Convert the string representation of a row index to a list.
     * 
     * @param row the string representation of the row index
     * @return the row index
     */
    public static List<Integer> rowIndexStringToList(final String row)
    {
        if (row == null)
        {
            return null;
        }

        List<Integer> rowIndex = new ArrayList<Integer>();

        try
        {

            // Convert StringId to array
            String[] rowIdString = row.split(INDEX_DELIMITER);
            for (int i = 0; i < rowIdString.length; i++)
            {
                rowIndex.add(Integer.parseInt(rowIdString[i]));
            }
        }
        catch (NumberFormatException e)
        {
            log.warn("Invalid row id: " + row);
        }

        return rowIndex;
    }

    /**
     * This can be used by column components on a {@link WTable} to determine the current row key.
     * 
     * @return the row key for the current row, or null if no row details
     */
    public static Object getCurrentRowKey()
    {
        UIContext uic = UIContextHolder.getCurrent();
        if (uic instanceof SubUIContext)
        {
            return ((SubUIContext) uic).getRowId();
        }
        return null;
    }

    /**
     * This can be used by column components on a {@link WTable} to determine the current row index.
     * 
     * @param component the column component
     * @return the row index for the current row, or null if no row details
     */
    public static List<Integer> getCurrentRowIndex(final WComponent component)
    {
        UIContext uic = UIContextHolder.getCurrent();
        // Check have correct context
        if (!(uic instanceof SubUIContext))
        {
            return null;
        }

        // Find the table
        WTable table = WebUtilities.getAncestorOfClass(WTable.class, component);
        if (table == null)
        {
            return null;
        }

        int repeaterIdx = ((SubUIContext) uic).getRowIndex();
        RowIdWrapper wrapper = table.getRepeater().getBeanList().get(repeaterIdx);
        return wrapper.getRowIndex();
    }

}
