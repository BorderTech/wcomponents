package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is a convenience WComponent and basically a WButton. When click on it, information about the WComponent tree and
 * UIContext will be displayed after the button.
 *
 * @author Ming Gao
 */
public class UicProfileButton extends WButton {

	/**
	 * Creates a UicProfileButton.
	 */
	public UicProfileButton() {
		super();
	}

	/**
	 * Creates a UicProfileButton with the given button caption.
	 *
	 * @param caption the button caption.
	 */
	public UicProfileButton(final String caption) {
		super(caption);
	}

	/**
	 * Override afterPaint to paint the profile information if the button has been pressed.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);

		if (this.isPressed() && renderContext instanceof WebXmlRenderContext) {
			// TODO: This must not emit mark-up
			PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();

			StringBuffer temp = dumpAll();
			writer.println("<br/><br/>");
			writer.println(temp);
		}
	}

	/**
	 * Dumps all the profiling information in textual format to a StringBuffer.
	 *
	 * @return the profiling information in textual format.
	 */
	public StringBuffer dumpAll() {
		WComponent root = WebUtilities.getTop(this);

		Map<String, GroupData> compTallyByClass = new TreeMap<>();

		GroupData compDataOverall = new GroupData();

		StringBuffer out = new StringBuffer();

		String className = root.getClass().getName();

		out.append("<b>The root of the WComponent tree is:</b>\n<br/>").append(className).append(
				"\n<br/>");

		tally(root, compTallyByClass, compDataOverall, out);

		out.append("<b>WComponent usage overall:</b>\n<br/> ");
		out.append(compDataOverall.total).append(" WComponent(s) in the WComponent tree.\n<br/>");

		if (compDataOverall.total > 0) {
			out.append("<b>WComponent usage by class:</b><br/>");

			for (Map.Entry<String, GroupData> entry : compTallyByClass.entrySet()) {
				className = entry.getKey();
				GroupData dataForClass = entry.getValue();
				out.append(' ').append(dataForClass.total).append("  ").append(className).append(
						"<br/>");
			}

			out.append("<br/><hr/>");
		}

		processUic(out);

		return out;
	}

	/**
	 * @param component the component
	 * @param map the group data
	 * @param compDataOverall the overall group data
	 * @param out the buffer
	 */
	private void tally(final WComponent component, final Map<String, GroupData> map,
			final GroupData compDataOverall, final StringBuffer out) {
		String classKey = component.getClass().getName();
		GroupData dataForClass = map.get(classKey);

		if (dataForClass == null) {
			dataForClass = new GroupData();
			map.put(classKey, dataForClass);
		}

		dataForClass.total++;
		compDataOverall.total++;

		if (component instanceof Container) {
			int count = ((Container) component).getChildCount();

			for (int i = 0; i < count; i++) {
				WComponent child = ((Container) component).getChildAt(i);
				tally(child, map, compDataOverall, out);
			}
		}
	}

	/**
	 * @param out the current buffer
	 */
	private void processUic(final StringBuffer out) {
		Set components = UIContextHolder.getCurrent().getComponents();

		// Variables that will contain the stats collected.
		GroupData dataOverall = new GroupData();
		Map<String, GroupData> tallyByClass = new TreeMap<>();
		Map<String, ProfileData> profileByClass = new TreeMap<>();
		Set<WComponent> topComponents = new HashSet<>();

		out.append("\n<b>UIContext details:\n</b>"
				+ "\n<br/>\n<table border=\"1\">\n<tr>\n<th>Class Name</th>\n"
				+ "\n<th>ID</th>\n"
				+ "\n<th>Is Serialisable</th>\n"
				+ "\n<th>Size</th></tr>\n");

		for (Iterator iter = components.iterator(); iter.hasNext();) {
			WComponent comp = (WComponent) iter.next();
			// processData(comp);

			String className = comp.getClass().getName();

			ProfileData profileForClass = profileByClass.get(className);

			if (!comp.isDefaultState()) {
				ProfileDetailsData profile = new ProfileDetailsData();
				profile.id = comp.getId();

				profile.isSerializable = true; // TODO: implement this properly
				profile.size = 0; // TODO: implement this properly

				out.append("\n<tr>\n<td>");
				out.append(className);
				out.append("</td>\n\n<td>");
				out.append(profile.id);
				out.append("</td>\n\n<td>");
				out.append(profile.isSerializable);
				out.append("</td>\n\n<td>");
				out.append(profile.size);
				out.append("</td></tr>\n");

				if (profileForClass == null) {
					profileForClass = new ProfileData();
					profileByClass.put(className, profileForClass);
				}

				profileForClass.profiles.add(profile);
			}

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

		out.append("\n</table>\n<br/><hr/>"
				+ "<b>WComponent session usage overall:</b>\n<br/> ");

		out.append(dataOverall.total).append(" WComponent(s) storing data in the session.\n<br/>");

		if (dataOverall.total > 0) {
			out.append('[')
					.append(dataOverall.unnecessaryCount)
					.append("] WComponent(s) in default state. WComponents in default state do not need to store data in the session.<br/>"
							+ " Therefore, only ").append(
							dataOverall.total - dataOverall.unnecessaryCount)
					.append(" WComponent(s) actually need(s) to store data in the session.<br/>");

			out.append("<b>WComponent session usage by class:</b><br/>");

			for (Map.Entry<String, GroupData> entry : tallyByClass.entrySet()) {
				String className = entry.getKey();
				GroupData dataForClass = entry.getValue();

				out.append(' ').append(dataForClass.total).append(" [").append(
						dataForClass.unnecessaryCount).append("] ")
						.append(className).append("<br/>");
			}

			out.append("Found ").append(topComponents.size()).append(" top component(s).<br/><hr/>");
		}
	}

	/**
	 * Holds statistics for a single WComponent class.
	 *
	 * @author Ming Gao
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

	/**
	 * @author Ming Gao
	 */
	private static final class ProfileDetailsData {

		private int size;

		private String id;

		private boolean isSerializable;
	}

	/**
	 * @author Ming Gao
	 */
	private static final class ProfileData {

		private final List<ProfileDetailsData> profiles = new ArrayList<>();
	}
}
