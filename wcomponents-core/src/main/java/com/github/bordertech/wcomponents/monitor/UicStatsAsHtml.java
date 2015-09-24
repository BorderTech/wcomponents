package com.github.bordertech.wcomponents.monitor;

import com.github.bordertech.wcomponents.WComponent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A utility class which writes out UicStats statistics in HTML format.
 *
 * @author Martin Shevchenko
 * @since 1.0.0
 */
public final class UicStatsAsHtml {

	/**
	 * No instance methods here.
	 */
	private UicStatsAsHtml() {
	}

	/**
	 * Writes out the given statistics in HTML format.
	 *
	 * @param writer the writer to write to.
	 * @param stats the stats to write.
	 */
	public static void write(final PrintWriter writer, final UicStats stats) {
		writer.println("<dl class=\"column\">");

		writer.print("<dt>Total root wcomponents found in UIC</dt>");
		writer.println("<dd>" + stats.getRootWCs().size() + "</dd>");

		writer.print("<dt>Size of UIC (by serialization)</dt>");
		writer.println("<dd>" + stats.getOverallSerializedSize() + "</dd>");

		writer.print("<dt>UI</dt>");
		writer.println("<dd>" + stats.getUI().getClass().getName() + "</dd>");

		writer.println("</dl>");

		for (Iterator<WComponent> it = stats.getWCsAnalysed(); it.hasNext();) {
			WComponent comp = it.next();
			Map<WComponent, UicStats.Stat> treeStats = stats.getWCTreeStats(comp);

			writer.println("<br /><b>Analysed component:</b> " + comp);
			writer.println("<br /><b>Number of components in tree:</b> " + treeStats.size());

			writeHeader(writer);
			writeProfileForTree(writer, treeStats);
			writeFooter(writer);
		}
	}

	/**
	 * Writes the stats for a single component.
	 *
	 * @param writer the writer to write the stats to.
	 * @param treeStats the stats for the component.
	 */
	private static void writeProfileForTree(final PrintWriter writer,
			final Map<WComponent, UicStats.Stat> treeStats) {
		// Copy all the stats into a list so we can sort and cull.
		List<UicStats.Stat> statList = new ArrayList<>(treeStats.values());

		Comparator<UicStats.Stat> comparator = new Comparator<UicStats.Stat>() {
			@Override
			public int compare(final UicStats.Stat stat1, final UicStats.Stat stat2) {
				if (stat1.getModelState() > stat2.getModelState()) {
					return -1;
				} else if (stat1.getModelState() < stat2.getModelState()) {
					return 1;
				} else {
					int diff = stat1.getClassName().compareTo(stat2.getClassName());

					if (diff == 0) {
						diff = stat1.getName().compareTo(stat2.getName());
					}

					return diff;
				}
			}
		};

		Collections.sort(statList, comparator);

		for (int i = 0; i < statList.size(); i++) {
			UicStats.Stat stat = statList.get(i);
			writeRow(writer, stat);
		}
	}

	/**
	 * Writes the stats header HTML.
	 *
	 * @param writer the writer to write the header to
	 */
	private static void writeHeader(final PrintWriter writer) {
		writer.println("<table border=\"1\">");
		writer.println("<thead>");
		writer.print("<tr>");
		writer.print("<th>Class</th>");
		writer.print("<th>Model</th>");
		writer.print("<th>Size</th>");
		writer.print("<th>Ref.</th>");
		writer.print("<th>Name</th>");
		writer.print("<th>Comment</th>");
		writer.println("</tr>");
		writer.println("</thead>");
	}

	/**
	 * Writes the stats footer HTML.
	 *
	 * @param writer the writer to write the footer to
	 */
	private static void writeFooter(final PrintWriter writer) {
		writer.println("</table>");
	}

	/**
	 * Writes a row containing a single stat.
	 *
	 * @param writer the writer to write the row to.
	 * @param stat the stat to write.
	 */
	private static void writeRow(final PrintWriter writer, final UicStats.Stat stat) {
		writer.print("<tr>");
		writer.print("<td>" + stat.getClassName() + "</td>");
		writer.print("<td>" + stat.getModelStateAsString() + "</td>");

		if (stat.getSerializedSize() > 0) {
			writer.print("<td>" + stat.getSerializedSize() + "</td>");
		} else {
			writer.print("<td>&#160;</td>");
		}

		writer.print("<td>" + stat.getRef() + "</td>");
		writer.print("<td>" + stat.getName() + "</td>");

		if (stat.getComment() == null) {
			writer.print("<td>&#160;</td>");
		} else {
			writer.print("<td>" + stat.getComment() + "</td>");
		}

		writer.println("</tr>");
	}
}
