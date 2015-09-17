package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lookup table implementation for the WComponent tests.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TestLookupTable implements LookupTable {

	/**
	 * Table name constant for the yes/no table.
	 */
	public static final String YES_NO_TABLE = "yes_no";

	/**
	 * Table name constant for the day of the week table.
	 */
	public static final String DAY_OF_WEEK_TABLE = "day_of_week";

	/**
	 * Table name constant for the cacheable day of the week table.
	 */
	public static final String CACHEABLE_DAY_OF_WEEK_TABLE = "day_of_week_cacheable";

	/**
	 * Table name constant for the day of the week table with a NULL option.
	 */
	public static final String DAY_OF_WEEK_TABLE_WITH_NULL_OPTION = "day_of_week_with_null_option";

	/**
	 * Table name constant for the cacheable day of the week table with a NULL option.
	 */
	public static final String CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION = "cacheable_day_of_week_with_null_option";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getTable(final Object table) {
		if (table instanceof String) {
			return getTable((String) table);
		} else if (table instanceof Class) {
			return getTable((Class<?>) table);
		}

		return Collections.emptyList();
	}

	/**
	 * Retrieves the cache id for the given table.
	 *
	 * @param table the table to look up.
	 * @return null - these tables are not cacheable.
	 */
	@Override
	public String getCacheKeyForTable(final Object table) {
		if (CACHEABLE_DAY_OF_WEEK_TABLE.equals(table)) {
			return CACHEABLE_DAY_OF_WEEK_TABLE;
		} else if (CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION.equals(table)) {
			return CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION;
		}

		return null;
	}

	@Override
	public Object getTableForCacheKey(final String key) {
		if (CACHEABLE_DAY_OF_WEEK_TABLE.equals(key)) {
			return DAY_OF_WEEK_TABLE;
		} else if (CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION.equals(key)) {
			return DAY_OF_WEEK_TABLE_WITH_NULL_OPTION;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode(final Object table, final Object entry) {
		if (entry instanceof TableEntry) {
			return ((TableEntry) entry).getCode();
		} else if (table != null && entry == null) {
			return "";
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(final Object table, final Object entry) {
		if (entry instanceof TableEntry) {
			return ((TableEntry) entry).getDesc();
		} else if (table != null && entry == null) {
			return "";
		}

		return null;
	}

	/**
	 * Retrieves the contents of a given table.
	 *
	 * @param tableName the name of the table to look up.
	 * @return a list of table objects.
	 */
	private List<Object> getTable(final String tableName) {
		if (CACHEABLE_DAY_OF_WEEK_TABLE.equals(tableName)) {
			return getTable(DAY_OF_WEEK_TABLE);
		} else if (CACHEABLE_DAY_OF_WEEK_TABLE_WITH_NULL_OPTION.equals(tableName) || DAY_OF_WEEK_TABLE_WITH_NULL_OPTION.
				equals(tableName)) {
			List<Object> list = getTable(DAY_OF_WEEK_TABLE);
			list.add(0, null);
			return list;
		}

		// For testing, a 01-01-1970 creation time means no options
		UIContext uic = UIContextHolder.getCurrent();
		if (uic != null && uic.getCreationTime() == 0) {
			List<Object> data = Collections.emptyList();
			return data;
		}

		List<Object> data = new ArrayList<>();

		for (String[] row : TABLE_DATA) {
			if (row[0].equals(tableName)) {
				data.add(new TableEntry(row[1], row[2]));
			}
		}

		return data;
	}

	/**
	 * Retrieves the contents of a given table by a table class.
	 *
	 * @param tableClass the class of the table to look up.
	 * @return a list of table objects.
	 */
	private List<Object> getTable(final Class<?> tableClass) {
		// For testing, a 01-01-1970 creation time means no options
		UIContext uic = UIContextHolder.getCurrent();
		if (uic != null && uic.getCreationTime() == 0) {
			List<Object> data = Collections.emptyList();
			return data;
		}

		if (YesNoTable.class.equals(tableClass) || DayOfWeekTable.class.equals(tableClass)) {
			try {
				return getTable(tableClass.newInstance().toString());
			} catch (Exception e) {
				throw new SystemException("Failed to read table for " + tableClass, e);
			}
		}

		return null;
	}

	/**
	 * Test marker for the yes/no table.
	 */
	public static final class YesNoTable {

		/**
		 * @return {@link #YES_NO_TABLE}
		 */
		@Override
		public String toString() {
			return YES_NO_TABLE;
		}
	}

	/**
	 * Test marker for the day of week table.
	 */
	public static final class DayOfWeekTable {

		/**
		 * @return {@link #DAY_OF_WEEK_TABLE}
		 */
		@Override
		public String toString() {
			return DAY_OF_WEEK_TABLE;
		}
	}

	/**
	 * Test marker for the cacheable day of week table.
	 */
	public static final class CacheableDayOfWeekTable {

		/**
		 * @return {@link #CACHEABLE_DAY_OF_WEEK_TABLE}
		 */
		@Override
		public String toString() {
			return CACHEABLE_DAY_OF_WEEK_TABLE;
		}
	}

	/**
	 * Represents an entry in a table.
	 */
	public static final class TableEntry implements Serializable, Option {

		/**
		 * The table entry code.
		 */
		private final String code;

		/**
		 * The table entry description.
		 */
		private final String desc;

		/**
		 * Creates a KeyValuePair.
		 *
		 * @param code the table entry code.
		 * @param desc the table entry description.
		 */
		public TableEntry(final String code, final String desc) {
			this.code = code;
			this.desc = desc;
		}

		/**
		 * @return the description.
		 */
		@Override
		public String getDesc() {
			return desc;
		}

		/**
		 * @return the code.
		 */
		@Override
		public String getCode() {
			return code;
		}

		/**
		 * Indicates whether the given object is equal to this one.
		 *
		 * @param obj the object to test for equality.
		 * @return true if the object is a TableEntry and has the same code as this one.
		 */
		@Override
		public boolean equals(final Object obj) {
			return (obj instanceof TableEntry) && Util.equals(code, ((TableEntry) obj).code);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return code == null ? 0 : code.hashCode();
		}
	}

	/**
	 * Example table data.
	 */
	private static final String[][] TABLE_DATA = new String[][]{
		{DAY_OF_WEEK_TABLE, "MON", "Monday"},
		{DAY_OF_WEEK_TABLE, "TUE", "Tuesday"},
		{DAY_OF_WEEK_TABLE, "WED", "Wednesday"},
		{DAY_OF_WEEK_TABLE, "THU", "Thursday"},
		{DAY_OF_WEEK_TABLE, "FRI", "Friday"},
		{DAY_OF_WEEK_TABLE, "SAT", "Saturday"},
		{DAY_OF_WEEK_TABLE, "SUN", "Sunday"},
		{YES_NO_TABLE, "Y", "Yes"},
		{YES_NO_TABLE, "N", "No"}
	};
}
