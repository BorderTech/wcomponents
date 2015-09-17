package com.github.bordertech.wcomponents.examples.repeater.link;

import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WRepeater;
import com.github.bordertech.wcomponents.WTabSet;
import java.util.ArrayList;
import java.util.List;

/**
 * These examples show different usages of buttons/links when used within a {@link WRepeater} in different combinations.
 *
 * @author Adam Millard
 */
public class RepeaterLinkExample extends WPanel {

	/**
	 * The "Basic" tab, which shows a single instance of the render component (not repeated).
	 */
	private final BasicComponent basic = new BasicComponent();

	/**
	 * A repeated version of the "Basic" tab.
	 */
	private final RepeaterComponent repeated = new RepeaterComponent();

	/**
	 * A repeated version of the "Basic" tab, where each row contains a link.
	 */
	private final RepeaterLinkTab repeatedLink = new RepeaterLinkTab();

	/**
	 * Demonstrates the use of nested WRepeaters.
	 */
	private final NestedRepeaterTabComponent nestedRepeaterTab = new NestedRepeaterTabComponent();

	/**
	 * Creates a RepeaterLinkExample.
	 */
	public RepeaterLinkExample() {
		super(Type.BLOCK);

		WTabSet tabs = new WTabSet();
		tabs.addTab(basic, "Basic", WTabSet.TAB_MODE_SERVER);
		tabs.addTab(repeated, "Repeated", WTabSet.TAB_MODE_SERVER);
		tabs.addTab(repeatedLink, "Repeated Link", WTabSet.TAB_MODE_SERVER);
		tabs.addTab(nestedRepeaterTab, "Repeated Nested Tab", WTabSet.TAB_MODE_SERVER);
		add(tabs);
	}

	/**
	 * Override preparePaint to initialise the data the first time through.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		if (!isInitialised()) {
			MyData data = new MyData("Homer");
			basic.setData(data);

			List<MyData> dataList = new ArrayList<>();
			dataList.add(new MyData("Homer"));
			dataList.add(new MyData("Marge"));
			dataList.add(new MyData("Bart"));
			repeated.setBeanList(dataList);

			dataList = new ArrayList<>();
			dataList.add(new MyData("Greg"));
			dataList.add(new MyData("Jeff"));
			dataList.add(new MyData("Anthony"));
			dataList.add(new MyData("Murray"));
			repeatedLink.setData(dataList);

			List<List<MyData>> rootList = new ArrayList<>();

			List<MyData> subList = new ArrayList<>();
			subList.add(new MyData("Ernie"));
			subList.add(new MyData("Bert"));
			rootList.add(subList);

			subList = new ArrayList<>();
			subList.add(new MyData("Starsky"));
			subList.add(new MyData("Hutch"));
			rootList.add(subList);

			nestedRepeaterTab.setData(rootList);

			setInitialised(true);
		}
	}
}
