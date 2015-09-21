package com.github.bordertech.wcomponents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class can be used to dump the contents of a UIContext object for debugging purposes.
 *
 * @author Martin_Schevchenko
 * @since 1.0.0
 */
public class UIContextDebugWrapper {

	/**
	 * The UIContext instance being wrapped.
	 */
	private final UIContext uic;

	/**
	 * Creates a UIContextDebugWrapper.
	 *
	 * @param uic the object to log.
	 */
	public UIContextDebugWrapper(final UIContext uic) {
		this.uic = uic;
	}

	/**
	 * @return the wrapped UIContext.
	 */
	protected UIContext getUIContext() {
		return uic;
	}

	/**
	 * Tally up the number of WComponents storing data in the session grouped by object type. Also tally up how many are
	 * storing data unnecessarily. Also tally how many different top components are referenced by the components stored
	 * in the UIContext.
	 *
	 * @return the debugging information.
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		UIContext cuic = getUIContext();
		UIContextHolder.pushContext(cuic);

		try {
			Set components = cuic.getComponents();

			// Variables that will contain the stats collected.
			GroupData dataOverall = new GroupData();
			Map<String, GroupData> tallyByClass = new TreeMap<>();
			Set<WComponent> topComponents = new HashSet<>();

			for (Iterator iter = components.iterator(); iter.hasNext();) {
				WComponent comp = (WComponent) iter.next();
				String className = comp.getClass().getName();

				GroupData dataForClass = tallyByClass.get(className);

				if (dataForClass == null) {
					dataForClass = new GroupData();
					tallyByClass.put(className, dataForClass);
				}

				dataForClass.total++;
				dataOverall.total++;

				if (comp.isDefaultState()) {
					dataForClass.unnecessaryCount++;
					dataOverall.unnecessaryCount++;
				}

				WComponent top = WebUtilities.getTop(comp);
				topComponents.add(top);
			}

			buf.append("WComponent session usage overall:\n ");
			buf.append(dataOverall.total).append(" WComponent(s) storing data in the session.\n");

			if (dataOverall.total > 0) {
				buf.append("[").append(dataOverall.unnecessaryCount).append(
						"] WComponent(s) in default state. WComponents in default state do not need to store data in the session.\n");
				buf.append(" Therefore, only ").append(
						dataOverall.total - dataOverall.unnecessaryCount).append(
								" WComponent(s) actually need(s) to store data in the session.\n");

				buf.append("WComponent session usage by class:\n");

				for (Map.Entry<String, GroupData> entry : tallyByClass.entrySet()) {
					String className = entry.getKey();
					GroupData dataForClass = entry.getValue();
					buf.append(' ').append(dataForClass.total).append(" [").append(
							dataForClass.unnecessaryCount).append("] ").append(className).append(
							'\n');
				}

				buf.append("Found ").append(topComponents.size()).append(" top component(s).\n");
			}
		} finally {
			UIContextHolder.popContext();
		}

		return buf.toString();
	}

	/**
	 * Holds statistics for a single WComponent class.
	 */
	private static final class GroupData {

		/**
		 * The count of instances of the particular WComponent class with a component model in the context/session.
		 */
		private int total = 0;

		/**
		 * The count of instances of the particular WComponent class with a component model in the context/session which
		 * doesn't need to be there.
		 */
		private int unnecessaryCount = 0;
	}
}
