package com.github.bordertech.wcomponents.util;

import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WRepeater.SubUIContext;
import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.WTable.RowIdWrapper;
import com.github.bordertech.wcomponents.WebUtilities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Static utility methods related to working with {@link WTable}.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class TableUtil {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(TableUtil.class);

	/**
	 * Row index delimiter.
	 */
	public static final String INDEX_DELIMITER = "-";

	/**
	 * Prevent instantiation of utility class.
	 */
	private TableUtil() {
	}

	/**
	 * Convert the row index to its string representation.
	 *
	 * @param row the row index
	 * @return the string representation of the row index
	 */
	public static String rowIndexListToString(final List<Integer> row) {
		if (row == null || row.isEmpty()) {
			return null;
		}

		StringBuffer index = new StringBuffer();
		boolean addDelimiter = false;

		for (Integer lvl : row) {
			if (addDelimiter) {
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
	public static List<Integer> rowIndexStringToList(final String row) {
		if (row == null) {
			return null;
		}

		List<Integer> rowIndex = new ArrayList<>();

		try {

			// Convert StringId to array
			String[] rowIdString = row.split(INDEX_DELIMITER);
			for (int i = 0; i < rowIdString.length; i++) {
				rowIndex.add(Integer.parseInt(rowIdString[i]));
			}
		} catch (NumberFormatException e) {
			LOG.warn("Invalid row id: " + row);
		}

		return rowIndex;
	}

	/**
	 * This can be used by column components on a {@link WTable} to determine the current row key.
	 *
	 * @return the row key for the current row, or null if no row details
	 */
	public static Object getCurrentRowKey() {
		UIContext uic = UIContextHolder.getCurrent();
		if (uic instanceof SubUIContext) {
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
	public static List<Integer> getCurrentRowIndex(final WComponent component) {
		UIContext uic = UIContextHolder.getCurrent();
		// Check have correct context
		if (!(uic instanceof SubUIContext)) {
			return null;
		}

		// Find the table
		WTable table = WebUtilities.getAncestorOfClass(WTable.class, component);
		if (table == null) {
			return null;
		}

		int repeaterIdx = ((SubUIContext) uic).getRowIndex();
		RowIdWrapper wrapper = table.getRepeater().getBeanList().get(repeaterIdx);
		return wrapper.getRowIndex();
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
	public static void sortData(final Object[] data, final Comparator<Object> comparator,
			final boolean ascending,
			final int lowIndex, final int highIndex, final int[] sortIndices) {
		if (lowIndex >= highIndex) {
			return; // 1 element, so sorted already!
		}

		Object midValue = data[sortIndices[(lowIndex + highIndex) >>> 1]];

		int i = lowIndex - 1;
		int j = highIndex + 1;
		int sign = ascending ? 1 : -1;

		for (;;) {
			do {
				i++;
			} while (comparator.compare(data[sortIndices[i]], midValue) * sign < 0);

			do {
				j--;
			} while (comparator.compare(data[sortIndices[j]], midValue) * sign > 0);

			if (i >= j) {
				break; // crossover, good!
			}

			// Out of order - swap!
			int temp = sortIndices[i];
			sortIndices[i] = sortIndices[j];
			sortIndices[j] = temp;
		}

		// now determine the split point...
		if (i > j) {
			i = j;
		}

		sortData(data, comparator, ascending, lowIndex, i, sortIndices);
		sortData(data, comparator, ascending, i + 1, highIndex, sortIndices);
	}

}
