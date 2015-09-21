package com.github.bordertech.wcomponents.examples.common;

import com.github.bordertech.wcomponents.Option;
import com.github.bordertech.wcomponents.util.Base64Util;
import com.github.bordertech.wcomponents.util.LookupTable;
import com.github.bordertech.wcomponents.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example lookup table implementation for the WComponent examples.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ExampleLookupTable implements LookupTable {

	/**
	 * A map to hold the relationship between the cache key and the source table.
	 */
	private static final Map<String, Object> CACHE_MAP = Collections.synchronizedMap(
			new HashMap<String, Object>());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getTable(final Object table) {
		List<Object> data = new ArrayList<>();

		String tableName;
		if (table instanceof TableWithNullOption) {
			// Get source table name
			tableName = ((TableWithNullOption) table).getTableName();
			// Insert null option
			data.add(null);
		} else {
			tableName = (String) table;
		}

		for (String[] row : TABLE_DATA) {
			if (row[0].equals(tableName)) {
				data.add(new TableEntry(row[1], row[2]));
			}
		}

		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCacheKeyForTable(final Object table) {
		String tableName;
		TableWithNullOption nullOptionTable = null;
		if (table instanceof TableWithNullOption) {
			nullOptionTable = (TableWithNullOption) table;
			tableName = nullOptionTable.getTableName();
		} else {
			tableName = (String) table;
		}

		// For the examples, we only cache the "icao" and "australian_state" sets.
		if ("icao".equals(tableName) || "australian_state".equals(tableName) || "nodata".equals(
				tableName)) {
			StringBuffer key = new StringBuffer(tableName);
			if (nullOptionTable != null) {
				key.append("C");
				key.append(Base64Util.encodeString(nullOptionTable.getNullCode()));
				key.append("D");
				key.append(Base64Util.encodeString(nullOptionTable.getNullDescription()));
			}
			key.append("##VERSION##");
			CACHE_MAP.put(key.toString(), table);
			return key.toString();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
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
		if (table instanceof TableWithNullOption && entry == null) {
			return ((TableWithNullOption) table).getNullCode();
		} else if (entry instanceof TableEntry) {
			return ((TableEntry) entry).getCode();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(final Object table, final Object entry) {
		if (table instanceof TableWithNullOption && entry == null) {
			return ((TableWithNullOption) table).getNullDescription();
		} else if (entry instanceof TableEntry) {
			return ((TableEntry) entry).getDesc();
		}

		return null;
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
		 * Creates a TableEntry.
		 *
		 * @param code the entry code.
		 * @param desc the entry description.
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
	}

	/**
	 * Table with a null option inserted. Allows the null code and description to be overridden.
	 */
	public static class TableWithNullOption implements Serializable {

		/**
		 * Default null code.
		 */
		private static final String DEFAULT_NULL_CODE = "";
		/**
		 * Default null description.
		 */
		private static final String DEFAULT_NULL_DESCRIPTION = "";

		/**
		 * Source table.
		 */
		private final String tableName;
		/**
		 * Null code used for the null description.
		 */
		private final String nullDescription;
		/**
		 * Null code used for the null option.
		 */
		private final String nullCode;

		/**
		 * @param tableName the source table.
		 */
		public TableWithNullOption(final String tableName) {
			this.tableName = tableName;
			this.nullDescription = DEFAULT_NULL_DESCRIPTION;
			this.nullCode = DEFAULT_NULL_CODE;
		}

		/**
		 * @param tableName the source table.
		 * @param nullDescription the description for the null option
		 */
		public TableWithNullOption(final String tableName, final String nullDescription) {
			this.tableName = tableName;
			this.nullDescription = nullDescription;
			this.nullCode = DEFAULT_NULL_CODE;
		}

		/**
		 * @param tableName the source table.
		 * @param nullDescription the description for the null option
		 * @param nullCode the code for the null option
		 */
		public TableWithNullOption(final String tableName, final String nullDescription,
				final String nullCode) {
			this.tableName = tableName;
			this.nullDescription = nullDescription;
			this.nullCode = nullCode;
		}

		/**
		 * @return the source table.
		 */
		public String getTableName() {
			return tableName;
		}

		/**
		 * @return the description for the null option
		 */
		public String getNullDescription() {
			return nullDescription;
		}

		/**
		 * @return the code for the null option
		 */
		public String getNullCode() {
			return nullCode;
		}
	}

	/**
	 * Example table data.
	 */
	private static final String[][] TABLE_DATA = new String[][]{
		{"error_message", "DEFAULT",
			"The system is currently unavailable."},
		{"australian_state", "OTHER", "Outside Australia"},
		{"australian_state", "ACT",
			"Australian Capital Territory"},
		{"australian_state", "NSW", "New South Wales"},
		{"australian_state", "NT", "Northern Territory"},
		{"australian_state", "QLD", "Queensland"},
		{"australian_state", "SA", "South Australia"},
		{"australian_state", "TAS", "Tasmania"},
		{"australian_state", "VIC", "Victoria"},
		{"australian_state", "WA", "Western Australia"},
		{"icao", "AFG", "AFGHANISTAN"},
		{"icao", "ALB", "ALBANIA"},
		{"icao", "DZA", "ALGERIA"},
		{"icao", "ASM", "AMERICAN SAMOA"},
		{"icao", "AND", "ANDORRA"},
		{"icao", "AGO", "ANGOLA"},
		{"icao", "AIA", "ANGUILLA"},
		{"icao", "ATA", "ANTARCTICA"},
		{"icao", "ATG", "ANTIGUA AND BARBUDA"},
		{"icao", "ARG", "ARGENTINA"},
		{"icao", "ARM", "ARMENIA"},
		{"icao", "ABW", "ARUBA"},
		{"icao", "AUS", "AUSTRALIA"},
		{"icao", "AUT", "AUSTRIA"},
		{"icao", "AZE", "AZERBAIJAN"},
		{"icao", "BHS", "BAHAMAS"},
		{"icao", "BHR", "BAHRAIN"},
		{"icao", "BGD", "BANGLADESH"},
		{"icao", "BRB", "BARBADOS"},
		{"icao", "BLR", "BELARUS"},
		{"icao", "BEL", "BELGIUM"},
		{"icao", "BLZ", "BELIZE"},
		{"icao", "BEN", "BENIN"},
		{"icao", "BMU", "BERMUDA"},
		{"icao", "BTN", "BHUTAN"},
		{"icao", "BOL", "BOLIVIA"},
		{"icao", "BIH", "BOSNIA & HERZEGOVINA"},
		{"icao", "BWA", "BOTSWANA"},
		{"icao", "BVT", "BOUVET ISLAND"},
		{"icao", "BRA", "BRAZIL"},
		{"icao", "GBP", "BRIT -PROTECTED PERS"},
		{"icao", "GBD", "BRIT DEPEND TERRCIT"},
		{"icao", "IOT", "BRIT INDIAN OCN TERR"},
		{"icao", "GBR", "BRITISH -CITIZEN"},
		{"icao", "GBO", "BRITISH -O/SEAS CITZ"},
		{"icao", "GBS", "BRITISH -SUBJECT"},
		{"icao", "GBN", "BRITISH NATIONAL OVERSEAS"},
		{"icao", "BRN", "BRUNEI DARUSSALAM"},
		{"icao", "BGR", "BULGARIA"},
		{"icao", "BFA", "BURKINA FASO"},
		{"icao", "HVO", "BURKINA FASO"},
		{"icao", "BUR", "BURMA"},
		{"icao", "BDI", "BURUNDI"},
		{"icao", "BYS", "BYELORUSSIA"},
		{"icao", "KHM", "CAMBODIA"},
		{"icao", "CMR", "CAMEROON"},
		{"icao", "CAN", "CANADA"},
		{"icao", "CPV", "CAPE VERDE"},
		{"icao", "CYM", "CAYMAN ISLANDS"},
		{"icao", "CAF", "CENTRAL AFRICAN REP"},
		{"icao", "TCD", "CHAD"},
		{"icao", "CHL", "CHILE"},
		{"icao", "CHN", "CHINA"},
		{"icao", "CCK", "COCOS (KEELING) ISL."},
		{"icao", "COL", "COLOMBIA"},
		{"icao", "COM", "COMOROS"},
		{"icao", "COG", "CONGO"},
		{"icao", "COK", "COOK ISLANDS"},
		{"icao", "CRI", "COSTA RICA"},
		{"icao", "CIV", "COTE D'IVOIRE"},
		{"icao", "HRV", "CROATIA"},
		{"icao", "CUB", "CUBA"},
		{"icao", "CYP", "CYPRUS"},
		{"icao", "CZE", "CZECH REPUBLIC"},
		{"icao", "CSK", "CZECHOSLOVAKIA"},
		{"icao", "COD", "DEM REP OF THE CONGO"},
		{"icao", "DNK", "DENMARK"},
		{"icao", "DJI", "DJIBOUTI"},
		{"icao", "DMA", "DOMINICA"},
		{"icao", "DOM", "DOMINICAN REPUBLIC"},
		{"icao", "DDR", "EAST GERMANY"},
		{"icao", "TMP", "EAST TIMOR"},
		{"icao", "ECU", "ECUADOR"},
		{"icao", "EGY", "EGYPT"},
		{"icao", "SLV", "EL SALVADOR"},
		{"icao", "GNQ", "EQUATORIAL GUINEA"},
		{"icao", "ERI", "ERITREA"},
		{"icao", "EST", "ESTONIA"},
		{"icao", "TST", "19970318", "ETA TEST"},
		{"icao", "ETH", "ETHIOPIA"},
		{"icao", "FLK", "FALKLAND ISLANDS"},
		{"icao", "FRO", "FAROE ISLANDS"},
		{"icao", "FJI", "FIJI"},
		{"icao", "FIN", "FINLAND"},
		{"icao", "FRA", "FRANCE"},
		{"icao", "FXX", "FRANCE, METROPOLITAN"},
		{"icao", "GUF", "FRENCH GUIANA"},
		{"icao", "PYF", "FRENCH POLYNESIA"},
		{"icao", "ATF", "FRENCH SOUTHERN TERR"},
		{"icao", "MKD", "FYROM"},
		{"icao", "GAB", "GABON"},
		{"icao", "GMB", "GAMBIA"},
		{"icao", "GEO", "GEORGIA"},
		{"icao", "SGS", "GEORGIA/SANDWICH ISL"},
		{"icao", "D", "GERMANY"},
		{"icao", "GHA", "GHANA"},
		{"icao", "GIB", "GIBRALTAR"},
		{"icao", "GRC", "GREECE"},
		{"icao", "GRL", "GREENLAND"},
		{"icao", "GRD", "GRENADA"},
		{"icao", "GLP", "GUADELOUPE"},
		{"icao", "GUM", "GUAM"},
		{"icao", "GTM", "GUATEMALA"},
		{"icao", "GIN", "GUINEA"},
		{"icao", "GNB", "GUINEA-BISSAU"},
		{"icao", "GUY", "GUYANA"},
		{"icao", "HTI", "HAITI"},
		{"icao", "HMD", "HEARD & MCDONALD ISL"},
		{"icao", "HND", "HONDURAS"},
		{"icao", "HKG", "HONG KONG SAR"},
		{"icao", "HUN", "HUNGARY"},
		{"icao", "ISL", "ICELAND"},
		{"icao", "IND", "INDIA"},
		{"icao", "IDN", "INDONESIA"},
		{"icao", "IRN", "IRAN"},
		{"icao", "IRQ", "IRAQ"},
		{"icao", "IRL", "IRELAND"},
		{"icao", "ISR", "ISRAEL"},
		{"icao", "ITA", "ITALY"},
		{"icao", "JAM", "JAMAICA"},
		{"icao", "JPN", "JAPAN"},
		{"icao", "JOR", "JORDAN"},
		{"icao", "KAZ", "KAZAKHSTAN"},
		{"icao", "KEN", "KENYA"},
		{"icao", "KIR", "KIRIBATI"},
		{"icao", "PRK", "KOREA, NORTH"},
		{"icao", "KOR", "KOREA, SOUTH"},
		{"icao", "KWT", "KUWAIT"},
		{"icao", "KGZ", "KYRGYZSTAN"},
		{"icao", "LAO", "LAO PEOPLES DEM REP"},
		{"icao", "LVA", "LATVIA"},
		{"icao", "LBN", "LEBANON"},
		{"icao", "LSO", "LESOTHO"},
		{"icao", "LBR", "LIBERIA"},
		{"icao", "LBY", "LIBYA"},
		{"icao", "LIE", "LIECHTENSTEIN"},
		{"icao", "LTU", "LITHUANIA"},
		{"icao", "LUX", "LUXEMBOURG"},
		{"icao", "MAC", "MACAU SAR"},
		{"icao", "MDG", "MADAGASCAR"},
		{"icao", "MWI", "MALAWI"},
		{"icao", "MYS", "MALAYSIA"},
		{"icao", "MDV", "MALDIVES"},
		{"icao", "MLI", "MALI"},
		{"icao", "MLT", "MALTA"},
		{"icao", "MHL", "MARSHALL ISLANDS"},
		{"icao", "MTQ", "MARTINIQUE"},
		{"icao", "MRT", "MAURITANIA"},
		{"icao", "MUS", "MAURITIUS"},
		{"icao", "MYT", "MAYOTTE"},
		{"icao", "MEX", "MEXICO"},
		{"icao", "FSM", "MICRONESIA"},
		{"icao", "MDA", "MOLDOVA, REPUBLIC OF"},
		{"icao", "MCO", "MONACO"},
		{"icao", "MNG", "MONGOLIA"},
		{"icao", "MSR", "MONTSERRAT"},
		{"icao", "MAR", "MOROCCO"},
		{"icao", "MOZ", "MOZAMBIQUE"},
		{"icao", "MMR", "MYANMAR"},
		{"icao", "NAM", "NAMIBIA"},
		{"icao", "NRU", "NAURU"},
		{"icao", "NPL", "NEPAL"},
		{"icao", "NLD", "NETHERLANDS"},
		{"icao", "ANT", "NETHERLANDS ANTILLES"},
		{"icao", "NTZ", "NEUTRAL ZONE"},
		{"icao", "NCL", "NEW CALEDONIA"},
		{"icao", "NZL", "NEW ZEALAND"},
		{"icao", "NIC", "NICARAGUA"},
		{"icao", "NER", "NIGER"},
		{"icao", "NGA", "NIGERIA"},
		{"icao", "NIU", "NIUE"},
		{"icao", "MNP", "NORTHERN MARIANA ISL"},
		{"icao", "NOR", "NORWAY"},
		{"icao", "OMN", "OMAN"},
		{"icao", "PAK", "PAKISTAN"},
		{"icao", "PLW", "PALAU"},
		{"icao", "PSE", "19960222", "PALESTINIAN AUTHRTY"},
		{"icao", "PAN", "PANAMA"},
		{"icao", "PNG", "PAPUA NEW GUINEA"},
		{"icao", "PRY", "PARAGUAY"},
		{"icao", "PER", "PERU"},
		{"icao", "PHL", "PHILIPPINES"},
		{"icao", "PCN", "PITCAIRN"},
		{"icao", "POL", "POLAND"},
		{"icao", "PRT", "PORTUGAL"},
		{"icao", "PRI", "PUERTO RICO"},
		{"icao", "QAT", "QATAR"},
		{"icao", "XXB", "REFUGEE AS PER ART 1"},
		{"icao", "XXC", "REFUGEE OTHER"},
		{"icao", "REU", "REUNION"},
		{"icao", "ROM", "ROMANIA"},
		{"icao", "RUS", "RUSSIAN FEDERATION"},
		{"icao", "RWA", "RWANDA"},
		{"icao", "LCA", "SAINT LUCIA"},
		{"icao", "WSM", "SAMOA"},
		{"icao", "SMR", "SAN MARINO"},
		{"icao", "STP", "SAO TOME & PRINCIPE"},
		{"icao", "SAU", "SAUDI ARABIA"},
		{"icao", "SEN", "SENEGAL"},
		{"icao", "SCG", "20030723", "SERBIA AND MONTENEGRO"},
		{"icao", "SYC", "SEYCHELLES"},
		{"icao", "SLE", "SIERRA LEONE"},
		{"icao", "SGP", "SINGAPORE"},
		{"icao", "SVK", "SLOVAK REPUBLIC"},
		{"icao", "SVN", "SLOVENIA"},
		{"icao", "SLB", "SOLOMON ISLANDS"},
		{"icao", "SOM", "SOMALIA"},
		{"icao", "ZAF", "SOUTH AFRICA"},
		{"icao", "SUN", "SOVIET UNION"},
		{"icao", "ESP", "SPAIN"},
		{"icao", "LKA", "SRI LANKA"},
		{"icao", "SHN", "ST HELENA"},
		{"icao", "KNA", "ST KITTS & NEVIS"},
		{"icao", "SPM", "ST PIERRE & MIQUELON"},
		{"icao", "VCT", "ST VINCENT/GRENADINE"},
		{"icao", "XXA", "STATELESS PERSON"},
		{"icao", "SDN", "SUDAN"},
		{"icao", "SUR", "SURINAME"},
		{"icao", "SJM", "SVALBARD & JAN MAYEN"},
		{"icao", "SWZ", "SWAZILAND"},
		{"icao", "SWE", "SWEDEN"},
		{"icao", "CHE", "SWITZERLAND"},
		{"icao", "SYR", "SYRIA"},
		{"icao", "TWN", "TAIWAN"},
		{"icao", "TJK", "TAJIKISTAN"},
		{"icao", "TZA", "TANZANIA"},
		{"icao", "THA", "THAILAND"},
		{"icao", "TLS", "20021115", "TIMOR-LESTE"},
		{"icao", "TGO", "TOGO"},
		{"icao", "TKL", "TOKELAU"},
		{"icao", "TON", "TONGA"},
		{"icao", "TTO", "TRINIDAD AND TOBAGO"},
		{"icao", "TUN", "TUNISIA"},
		{"icao", "TUR", "TURKEY"},
		{"icao", "TKM", "TURKMENISTAN"},
		{"icao", "TCA", "TURKS AND CAICOS ISL"},
		{"icao", "TUV", "TUVALU"},
		{"icao", "UMI", "U.S MINOR ISLANDS"},
		{"icao", "UGA", "UGANDA"},
		{"icao", "UKR", "UKRAINE"},
		{"icao", "ARE", "UNITED ARAB EMIRATES"},
		{"icao", "UNA", "UNITED NATIONS AGNCY"},
		{"icao", "UNO", "UNITED NATIONS ORG"},
		{"icao", "USA", "UNITED STATES"},
		{"icao", "XXX", "UNSPCFED NATIONALITY"},
		{"icao", "URY", "URUGUAY"},
		{"icao", "UZB", "UZBEKISTAN"},
		{"icao", "VUT", "VANUATU"},
		{"icao", "VAT", "VATICAN CITY STATE"},
		{"icao", "VEN", "VENEZUELA"},
		{"icao", "VNM", "VIETNAM"},
		{"icao", "VGB", "VIRGIN ISLANDS (BRIT)"},
		{"icao", "VIR", "VIRGIN ISLANDS (U.S)"},
		{"icao", "WLF", "WALLIS & FUTUNA ISL"},
		{"icao", "ESH", "WESTERN SAHARA"},
		{"icao", "YEM", "YEMEN"},
		{"icao", "YMD", "YEMEN"},
		{"icao", "YUG", "YUGOSLAVIA"},
		{"icao", "ZAR", "ZAIRE"},
		{"icao", "ZMB", "ZAMBIA"},
		{"icao", "ZWE", "ZIMBABWE"},
		{"icao", "UNK", "UNMIK TRAVEL DOC"},
		{"marital_status", "U", "Not Stated"},
		{"marital_status", "D", "Divorced"},
		{"marital_status", "E", "Engaged"},
		{"marital_status", "F", "De Facto"},
		{"marital_status", "M", "Married"},
		{"marital_status", "N", "Never Married"},
		{"marital_status", "S", "Separated"},
		{"marital_status", "W", "Widowed"},
		{"sex", "M", "Male"}, {"sex", "F", "Female"},
		{"title", "77", "Mr"}, {"title", "78", "Mrs"},
		{"title", "74", "Miss"}, {"title", "79", "Ms"},
		{"title", "99", "Other"}, {"yes_no", "Y", "Yes"},
		{"yes_no", "N", "No"}, {"yes_no", "U", "Unknown"}};

}
