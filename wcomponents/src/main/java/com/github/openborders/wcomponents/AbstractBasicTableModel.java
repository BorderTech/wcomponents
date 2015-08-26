package com.github.openborders.wcomponents;

import java.io.Serializable;
import java.util.Comparator;

import com.github.openborders.wcomponents.AdapterBasicTableModel.BasicTableModel;

/**
 * A skeleton implementation of a basic data model that does not support filtering, sorting, or editability.
 * <p>
 * Used for data that is not in a tree like structure (ie not expandable).
 * </p>
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public abstract class AbstractBasicTableModel implements BasicTableModel, Serializable
{
    /**
     * @param row the row index
     * @return the row index as the unique key for the row
     */
    public Object getRowKey(final int row)
    {
        return row;
    }

    /**
     * This model does not support the concept of sorting by default. Subclasses will need to override this method and
     * {@link #isSortable(int)} to support sorting.
     *
     * @param col ignored.
     * @param ascending ignored.
     * @return null
     */
    public int[] sort(final int col, final boolean ascending)
    {
        return null;
    }

    /**
     * This model does not support the concept of editable cells by default. Subclasses will need to override this
     * method and {@link #isCellEditable(int, int)} to support cell editing.
     *
     * @param value ignored.
     * @param row ignored.
     * @param col ignored.
     */
    public void setValueAt(final Object value, final int row, final int col)
    {
        // NOP
    }

    /**
     * This model does not support the concept of sorting by default. Subclasses will need to override this method and
     * {@link #sort(int, boolean)} to support sorting.
     *
     * @param col ignored.
     * @return false.
     */
    public boolean isSortable(final int col)
    {
        return false;
    }

    /**
     * This model does not support the concept of row disabling by default. Subclasses will need to override this method
     * to support row disabling.
     *
     * @param row ignored.
     * @return false.
     */
    public boolean isDisabled(final int row)
    {
        return false;
    }

    /**
     * This model does not support the concept of row selectability by default. Subclasses will need to override this
     * method disable selection of specific rows.
     *
     * @param row ignored.
     * @return true.
     */
    public boolean isSelectable(final int row)
    {
        return true;
    }

    /**
     * This model does not support the concept of editable cells by default. Subclasses will need to override this
     * method and {@link #setValueAt(Object, int, int)} to support cell editing.
     *
     * @param row ignored.
     * @param col ignored.
     * @return false.
     */
    public boolean isCellEditable(final int row, final int col)
    {
        return false;
    }

    /**
     * Calculates the sort order for the data, using the given column and comparator.
     *
     * @param comparator the comparator to use for sorting
     * @param col the column to sort on
     * @param ascending true for an ascending sort, false for descending.
     * @return the row indices in sort order.
     */
    protected int[] sort(final Comparator<Object> comparator, final int col, final boolean ascending)
    {
        // We cache the column data to avoid repeated and potentially expensive lookups
        Object[] columnData = new Object[getRowCount()];

        for (int i = getRowCount() - 1; i >= 0; i--)
        {
            columnData[i] = getValueAt(i, col);
        }

        int[] sortIndices = new int[getRowCount()];

        for (int i = 0; i < sortIndices.length; i++)
        {
            sortIndices[i] = i;
        }

        sort(columnData, comparator, ascending, 0, columnData.length - 1, sortIndices);

        return sortIndices;
    }

    /**
     * Sorts the data using the given comparator, using a quick-sort.
     *
     * @param data the data for the column.
     * @param comparator the comparator to use for sorting.
     * @param ascending true for an ascending sort, false for descending.
     * @param lowIndex the start index for sub-sorting
     * @param highIndex the end index for sub-sorting
     * @param sortIndices the row indices, which will be updated as a result of the sort
     */
    private void sort(final Object[] data, final Comparator<Object> comparator, final boolean ascending,
                      final int lowIndex, final int highIndex, final int[] sortIndices)
    {
        if (lowIndex >= highIndex)
        {
            return; // 1 element, so sorted already!
        }

        Object midValue = data[sortIndices[(lowIndex + highIndex) / 2]];

        int i = lowIndex - 1;
        int j = highIndex + 1;
        int sign = ascending ? 1 : -1;

        for (;;)
        {
            do
            {
                i++;
            }
            while (comparator.compare(data[sortIndices[i]], midValue) * sign < 0);

            do
            {
                j--;
            }
            while (comparator.compare(data[sortIndices[j]], midValue) * sign > 0);

            if (i >= j)
            {
                break; // crossover, good!
            }

            // Out of order - swap!
            int temp = sortIndices[i];
            sortIndices[i] = sortIndices[j];
            sortIndices[j] = temp;
        }

        // now determine the split point...
        if (i > j)
        {
            i = j;
        }

        sort(data, comparator, ascending, lowIndex, i, sortIndices);
        sort(data, comparator, ascending, i + 1, highIndex, sortIndices);
    }
}
