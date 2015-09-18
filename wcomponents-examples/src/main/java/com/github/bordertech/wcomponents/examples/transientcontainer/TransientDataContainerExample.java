package com.github.bordertech.wcomponents.examples.transientcontainer;

import com.github.bordertech.wcomponents.AbstractTransientDataContainer;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.layout.FlowLayout;
import com.github.bordertech.wcomponents.layout.FlowLayout.Alignment;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * An example showing best practice using the AbstractTransientDataContainer. While this example just wraps one table
 * with the TransientDataContainer, a "real" application is likely to wrap a larger section of their UI. For example, a
 * table containing data may be wrapped to force it to be re-read from an application cache each time.</p>
 *
 * <p>
 * This example uses a dummy cache, where data is never expired. A more realistic implementation of
 * AbstractTransientDataContainer's setupData method would invoke the DMS, which will call the underlying service if the
 * result is not found in the cache.</p>
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class TransientDataContainerExample extends WPanel {

	/**
	 * The transient data container.
	 */
	private final ExampleTransientDataContainer transientDataContainer;

	/**
	 * The table panel.
	 */
	private final ExampleTablePanel tablePanel = new ExampleTablePanel();

	/**
	 * Example data for the table. In reality, the data would come from a database or service call.
	 */
	private static final List<ExampleDataBean> EXAMPLE_DATA = Arrays.asList(new ExampleDataBean[]{
		new ExampleDataBean("Aqua", "Triangle", "Alligator"),
		new ExampleDataBean("Brown", "Square", "Ant"),
		new ExampleDataBean("Cyan", "Pentagon", "Antelope"),
		new ExampleDataBean("Fuchsia", "Hexagon", "Ape"),
		new ExampleDataBean("Green", "Heptagon", "Baboon"),
		new ExampleDataBean("Indigo", "Octagon", "Badger"),
		new ExampleDataBean("Khaki", "Nonagon", "Bat"),
		new ExampleDataBean("Lime", "Decagon", "Bear"),
		new ExampleDataBean("Magenta", "Hendecagon", "Beaver"),
		new ExampleDataBean("Navy", "Dodecagon", "Bee"),
		new ExampleDataBean("Orange", "Tridecagon", "Beetle"),
		new ExampleDataBean("Purple", "Tetradecadon", "Bird"),
		new ExampleDataBean("Red", "Pentadecagon", "Bison"),
		new ExampleDataBean("Tan", "Hexadecagon", "Buffalo")});

	/**
	 * Creates a TransientDataContainerExample.
	 */
	public TransientDataContainerExample() {
		setLayout(new FlowLayout(Alignment.VERTICAL));

		add(new WText(
				"This example shows best practice using the AbstractTransientDataContainer. "
				+ " The WTable is wrapped in a TransientDataContainer, and as such will not "
				+ " store any data in the UIContext after the rendering is completed."));

		add(new WHorizontalRule());

		transientDataContainer = new ExampleTransientDataContainer(tablePanel);
		add(transientDataContainer);

		WButton button = new WButton("Load data");
		add(button);

		button.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				// Emulate storing a service call result in the application cache.
				String cacheKey = "dummy cache key";
				DummyApplicationCache.put(cacheKey, EXAMPLE_DATA);

				// Pass the cache key to the ExampleTransientDataContainer.
				// The container will use the cache key to retrieve the data before each render.
				transientDataContainer.setCacheKey(cacheKey);
			}
		});

		WButton refreshButton = new WButton("Refresh page");
		add(refreshButton);
	}

	/**
	 * An example implementation of the TransientDataContainer. In this example, the text to be displayed is stored as
	 * an attribute rather than a more sensible value, e.g. a cache key.
	 *
	 * @author Yiannis Paschalidis
	 */
	private static final class ExampleTransientDataContainer extends AbstractTransientDataContainer {

		/**
		 * The attribute key used to store the cache key.
		 */
		private static final String CACHE_KEY = "cacheKey";

		// Children that we are responsible for:
		/**
		 * The tablePanel that we need to supply with data.
		 */
		private final ExampleTablePanel tablePanel;

		/**
		 * Creates a ExampleTransientDataContainer.
		 *
		 * @param tablePanel the ExampleTablePanel to control.
		 */
		private ExampleTransientDataContainer(final ExampleTablePanel tablePanel) {
			this.tablePanel = tablePanel;
			add(tablePanel);
		}

		/**
		 * Ensures that all child components are set up correctly before they are painted.
		 */
		@Override
		public void setupData() {
			// 1) Retrieve the cache key attribute
			String cacheKey = (String) getAttribute(CACHE_KEY);

			// 2) Retrieve the data from the cache using the key
			List data = (List) DummyApplicationCache.get(cacheKey);

			// 3) Set the data as necessary for all children (just the table in this example)
			tablePanel.setData(data);
		}

		/**
		 * Sets the cache key as an attribute which is persisted across requests. This will be the only data that ends
		 * up being stored in the session. The cache key is used to retrieve the data from the application cache before
		 * each paint.
		 *
		 * @param cacheKey the cache key to set.
		 */
		public void setCacheKey(final String cacheKey) {
			setAttribute(CACHE_KEY, cacheKey);
		}
	}
}
