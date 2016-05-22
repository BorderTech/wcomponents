package com.github.bordertech.wcomponents.examples.table;

import com.github.bordertech.wcomponents.WTable;
import com.github.bordertech.wcomponents.examples.table.PersonBean.TravelDoc;
import com.github.bordertech.wcomponents.util.DateUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Utility class to create data for the {@link WTable} examples.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class ExampleDataUtil {

	/**
	 * Private constructor.
	 */
	private ExampleDataUtil() {
		// Do nothing
	}

	/**
	 * Creates the example data.
	 *
	 * @return the example data.
	 */
	public static List<PersonBean> createExampleData() {
		List<PersonBean> data = new ArrayList<>(5);

		data.add(new PersonBean("ID1", "Joe", "Bloggs", DateUtilities.createDate(1, 2, 1973)));
		data.add(new PersonBean("ID2", "Richard", "Starkey", DateUtilities.createDate(4, 8, 1976)));
		data.add(new PersonBean("ID3", "Peter", "Sellers", DateUtilities.createDate(21, 12, 1999)));
		data.add(new PersonBean("ID4", "Tom", "Smith", DateUtilities.createDate(16, 9, 1963)));
		data.add(new PersonBean("ID5", "Mary", "Jane", DateUtilities.createDate(2, 4, 1972)));
		data.add(new PersonBean("ID6", "John", "Bonham", DateUtilities.createDate(5, 3, 1952)));
		data.add(new PersonBean("ID7", "Nick", "Mason", DateUtilities.createDate(3, 5, 1946)));
		data.add(new PersonBean("ID8", "James", "Osterberg", DateUtilities.createDate(4, 6, 1974)));
		data.add(new PersonBean("ID9", "Kate", "Pierson", DateUtilities.createDate(7, 11, 1965)));
		data.add(new PersonBean("ID10", "Saul", "Hudson", DateUtilities.createDate(5, 3, 1978)));
		data.add(new PersonBean("ID11", "Kim", "Sung", DateUtilities.createDate(1, 10, 1945)));
		data.add(new PersonBean("ID12", "Ahmed", "McCarthur", DateUtilities.createDate(15, 7, 1985)));
		data.add(new PersonBean("ID13", "Nicholai", "Smith", DateUtilities.createDate(29, 4, 1996)));
		data.add(new PersonBean("ID14", "Polly", "Vinyl", DateUtilities.createDate(15, 8, 1978)));
		data.add(new PersonBean("ID15", "Ron", "Donald", DateUtilities.createDate(1, 1, 1923)));
		data.add(new PersonBean("ID16", "Tom", "Smith", DateUtilities.createDate(5, 8, 1932)));

		// Add documents
		data.get(0).setDocuments(Arrays.asList(new TravelDoc("11122", "Canada", "Ottawa",
				DateUtilities
				.createDate(5, 3, 1990), DateUtilities.createDate(2, 4, 1983))));
		data.get(2).setDocuments(Arrays.asList(new TravelDoc("23456", "New Zealand", "Wellington",
				DateUtilities
				.createDate(6, 3, 1999), DateUtilities.createDate(4, 7, 2009)),
				new TravelDoc("23457", "Australia", "Perth", DateUtilities
						.createDate(8, 5, 2005), DateUtilities.createDate(6, 9, 2015))));
		data.get(7).setDocuments(Arrays.asList(new TravelDoc("78901", "New Zealand", "Wellington",
				DateUtilities
				.createDate(10, 7, 2005), DateUtilities.createDate(8, 11, 2015))));

		// Add related persons - Level1
		data.get(0).setMore(Arrays.asList(new PersonBean("1A", "Jane", "Paice", DateUtilities.
				createDate(3, 4, 1980)),
				new PersonBean("1B", "Richie", "Benaud", DateUtilities
						.createDate(5, 11, 1989))));
		data.get(1)
				.setMore(Arrays.asList(new PersonBean("2A", "Steven", "Anderson", DateUtilities.
						createDate(1, 7, 1969)),
						new PersonBean("2B", "Terry", "Bollea", DateUtilities.
								createDate(8, 12, 1975))));
		data.get(3)
				.setMore(Arrays.asList(new PersonBean("4A", "John", "Stanley", DateUtilities.
						createDate(2, 7, 2001)),
						new PersonBean("4B", "Mary", "Smith", DateUtilities.createDate(8, 1, 2003))));
		data.get(4).setMore(Arrays.asList(new PersonBean("5A", "David", "Jones", DateUtilities.
				createDate(1, 4, 2002)),
				new PersonBean("5B", "Dwayne", "Johnson", DateUtilities
						.createDate(5, 2, 2014))));
		data.get(7).setMore(Arrays.asList(new PersonBean("8A", "Charles", "Spencer", DateUtilities.
				createDate(2, 6,
						1981))));

		// Add related persons - Level2
		data.get(0)
				.getMore()
				.get(1)
				.setMore(Arrays.asList(new PersonBean("1B1", "Chaim", "Witz", DateUtilities.
						createDate(1, 2, 2010)),
						new PersonBean("1B2", "John", "Bloggs", DateUtilities.createDate(3, 4, 2005))));

		data.get(3).getMore().get(0)
				.setMore(Arrays.asList(new PersonBean("4A1", "Vincent", "Anderson", DateUtilities.
						createDate(1, 8, 2010))));
		data.get(7).getMore().get(0)
				.setMore(Arrays.asList(new PersonBean("8A1", "Jnr", "Kulick", DateUtilities.
						createDate(4, 7, 2013))));

		return data;
	}

	/**
	 * @return basic example data
	 */
	public static String[][] createBasicData() {
		String[][] data = new String[][]{new String[]{"Joe", "Bloggs", "01/02/1973"},
		new String[]{"Jane", "Bloggs", "04/05/1976"},
		new String[]{"Kid", "Bloggs", "31/12/1999"}};
		return data;
	}

	/**
	 * Creates the example data.
	 *
	 * @param rows the number of rows to create
	 * @param documents the number of documents to add to each person
	 * @return the example data.
	 */
	public static List<PersonBean> createExampleData(final int rows, final int documents) {
		List<PersonBean> data = new ArrayList<>(rows);

		Date date1 = DateUtilities.createDate(1, 2, 1973);
		Date date2 = DateUtilities.createDate(2, 3, 1985);
		Date date3 = DateUtilities.createDate(3, 4, 2004);

		for (int i = 1; i <= rows; i++) {
			PersonBean bean = new PersonBean("P" + i, "Joe" + i, "Bloggs" + i, date1);

			List<TravelDoc> docs = new ArrayList<>(documents);
			for (int j = 1; j <= documents; j++) {
				String prefix = i + "-" + j;
				TravelDoc doc = new TravelDoc("DOC" + prefix, "Canada" + prefix, "Ottawa" + prefix,
						date2, date3);
				docs.add(doc);
			}

			bean.setDocuments(docs);

			data.add(bean);
		}

		return data;
	}

}
