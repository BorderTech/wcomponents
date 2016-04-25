package com.github.bordertech.wcomponents.examples.petstore;

import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.SystemException;
import com.github.bordertech.wcomponents.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A slightly more realistic example LookupTable implementation. This implementation reads the table data from an
 * external file.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class PetStoreLookupTable implements LookupTable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(PetStoreLookupTable.class);

	/**
	 * A map to hold the relationship between the cache key and the source table.
	 */
	private static final Map<String, Object> CACHE_MAP = Collections.synchronizedMap(
			new HashMap<String, Object>());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getTable(final Object tableIdentifier) {
		String tableName = String.valueOf(tableIdentifier);
		List<TableEntry> tableData = CrtTableResource.getTable(tableName);

		List<Object> table = new ArrayList<>();

		// Only return entries which are currently effective
		for (TableEntry entry : tableData) {
			Date now = new Date();

			if ((entry.getDateFrom() == null || entry.getDateFrom().before(now))
					&& (entry.getDateTo() == null || entry.getDateTo().after(now))) {
				table.add(entry);
			}
		}

		return table;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCacheKeyForTable(final Object table) {
		String tableName = (String) table;

		// Our cache keys are just the names of the tables,
		// as the table data is immutable.
		// Only the country list is cached only the client
		if ("icao".equals(tableName)) {
			String key = tableName + "VERSION#";
			CACHE_MAP.put(key, tableName);
			return key;
		}

		return null;
	}

	@Override
	public Object getTableForCacheKey(final String key) {
		Object table = CACHE_MAP.get(key);
		return table;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode(final Object table, final Object entry) {
		if (entry instanceof TableEntry) {
			return ((TableEntry) entry).getCode();
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
		}

		return null;
	}

	/**
	 * Represents an entry in a table.
	 */
	public static final class TableEntry {

		/**
		 * The table entry code.
		 */
		private final String code;

		/**
		 * The table entry description.
		 */
		private final String desc;

		/**
		 * The table name.
		 */
		private final String tableName;

		/**
		 * The valid from date.
		 */
		private final Date dateFrom;

		/**
		 * The valid to date.
		 */
		private final Date dateTo;

		/**
		 * Creates a table entry.
		 *
		 * @param tableName the table name.
		 * @param code the entry code.
		 * @param desc the entry description.
		 * @param dateFrom the valid from date.
		 * @param dateTo the valid to date.
		 */
		public TableEntry(final String tableName, final String code, final String desc,
				final Date dateFrom, final Date dateTo) {
			this.tableName = tableName;
			this.code = code;
			this.desc = desc;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		/**
		 * @return the description.
		 */
		public String getDesc() {
			return desc;
		}

		/**
		 * @return the code.
		 */
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
		 * @return this entry's hash code.
		 */
		@Override
		public int hashCode() {
			return code == null ? 0 : code.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return desc;
		}

		/**
		 * @return the table name.
		 */
		public String getTableName() {
			return tableName;
		}

		/**
		 * @return the valid from date.
		 */
		public Date getDateFrom() {
			return dateFrom;
		}

		/**
		 * @return Returns valid to date.
		 */
		public Date getDateTo() {
			return dateTo;
		}
	}

	/**
	 * The CrtTableResource class handles reading table data from an external flat file.
	 */
	private static final class CrtTableResource {

		/**
		 * The location of the file resource.
		 */
		public static final String RESOURCE_NAME = "/com/github/bordertech/wcomponents/examples/petstore/resources/crt/crt.data";

		/**
		 * Comment character used in the data file.
		 */
		private static final String COMMENT_STR = "#";

		/**
		 * Attribute separator used in the data file.
		 */
		private static final String DELIMITER = "@";

		/**
		 * The date format for dates stored in the data file.
		 */
		private static final String DATE_FORMAT = "yyyyMMdd";

		/**
		 * Cached tables, to avoid having to read the data file from disk each time.
		 */
		private static final Map<String, List<TableEntry>> TABLES = Collections.synchronizedMap(
				new HashMap<String, List<TableEntry>>());

		/**
		 * Prevent instantation of this utility class.
		 */
		private CrtTableResource() {
		}

		/**
		 * Retrieves a table from the flat file. Tables are cached in memory to avoid excess IO.
		 *
		 * @param tableName the table name.
		 * @return the contents of the given table.
		 */
		public static List<TableEntry> getTable(final String tableName) {
			List<TableEntry> table = TABLES.get(tableName);

			if (table == null) {
				table = loadList(tableName);
				TABLES.put(tableName, table);
			}

			return table;
		}

		/**
		 * Loads a list from the CRT file.
		 *
		 * @param tableName the table name to read.
		 * @return the table.
		 */
		private static List<TableEntry> loadList(final String tableName) {
			InputStream in = null;

			try {
				in = CrtTableResource.class.getResourceAsStream(RESOURCE_NAME);

				if (in == null) {
					throw new SystemException("The resource '" + RESOURCE_NAME
							+ "' cannot be found in the file system or classpath.");
				}

				BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
				List<TableEntry> list = readTable(bufReader, tableName);

				return list;
			} catch (IOException ex) {
				throw new SystemException(
						"Unable to load codesets from resource '" + RESOURCE_NAME + "'.", ex);
			} finally {
				try {
					if (null != in) {
						in.close();
					}
				} catch (IOException ex) {
					LOG.error("Unable to close resource '" + RESOURCE_NAME + "'.", ex);
				}
			}
		}

		/**
		 * Loads a table from the flat file repository.
		 *
		 * @param bufReader the reader to read from
		 * @param tableName the table name
		 * @return the table.
		 * @throws IOException if there is an error reading from the reader.
		 */
		private static List<TableEntry> readTable(final BufferedReader bufReader,
				final String tableName) throws IOException {
			LOG.debug("Loading CRT '" + tableName + "'.");
			SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

			List<TableEntry> list = new ArrayList<>();

			for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
				// Don't continue processing this line if it is a comment or it is empty.
				if (line.startsWith(COMMENT_STR) || "".equals(line)) {
					continue;
				}

				// Don't continue processing this line if it's not for the crt we're interested in.
				if (!line.startsWith(tableName + DELIMITER)) {
					continue;
				}

				//
				// Split the line into attributes.
				//
				String[] args = line.split(DELIMITER);

				if (args.length < 4) {
					throw new IOException(
							"Format of the file is invalid!" + "\nThe format of each line should be "
							+ "[crtname]" + DELIMITER + "[code]" + DELIMITER + "[datefrom]" + DELIMITER
							+ "[dateto]" + DELIMITER + "[description]"
							+ "\nThe line that cause the error is: " + line);
				}

				String code = args[1];
				String dateFromStr = args[2];
				String dateToStr = args[3];

				String desc = null;
				if (args.length > 4) {
					desc = args[4].replaceAll("\\\\n", "\n");
					desc = desc.length() == 0 ? null : desc;
				}

				//
				// Convert the dates.
				//
				Date dateFrom = null;
				Date dateTo = null;
				try {
					if (!Util.empty(dateFromStr)) {
						dateFrom = dateFormatter.parse(dateFromStr);
					}
					if (!Util.empty(dateToStr)) {
						dateTo = dateFormatter.parse(dateToStr);
					}
				} catch (ParseException ex) {
					throw new SystemException("An invalid effective date range was encountered ("
							+ dateFromStr + " - " + dateToStr + ").", ex);
				}

				//
				// Make an entry.
				//
				TableEntry data = new TableEntry(tableName, code, desc, dateFrom, dateTo);

				// Add the entry to the list.
				list.add(data);
			}

			return list;
		}
	}
}
