package com.github.bordertech.wcomponents.examples.picker;

import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.ImageResource;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.WApplication;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WDefinitionList;
import com.github.bordertech.wcomponents.WHorizontalRule;
import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WTabSet;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.util.StreamUtil;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This panel displays the currently selected example.
 *
 * It provides some protection against bad example code, and will display an error message rather than failing.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 *
 * Uses WSection as the example container so that the refresh and reset buttons can be put in context.
 * @author Mark Reeves.
 */
final class ExampleSection extends WSection implements MessageContainer {

	/**
	 * Report example messages here.
	 */
	private final WMessages messages = new WMessages();

	/**
	 * Logger for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ExampleSection.class);

	/**
	 * The container to add the example to.
	 */
	private final SafetyContainer container = new SafetyContainer();

	/**
	 * The container to display the source code.
	 */
	private final SourcePanel source = new SourcePanel();

	/**
	 * The tab set containing the example and source.
	 */
	private final WTabSet tabset = new WTabSet();

	/**
	 * Creates an ExamplePanel.
	 */
	ExampleSection() {
		super(new WPanel(), new WDecoratedLabel(null, new WText("No Selection"), new WContainer()));
		buildUI();
	}

	/**
	 * Add the controls to the section in the right order.
	 */
	private void buildUI() {
		add(messages);
		add(tabset);
		// add(new AccessibilityWarningContainer());

		container.add(new WText("Select an example from the menu"));
		// Set a static ID on container and it becomes a de-facto naming context.
		container.setIdName("eg");
		tabset.addTab(container, "(no selection)", WTabSet.TAB_MODE_CLIENT);

		WImage srcImage = new WImage(new ImageResource("/image/text-x-source.png", "View Source"));
		srcImage.setCacheKey("srcTabImage");
		tabset.addTab(source, new WDecoratedLabel(srcImage), WTabSet.TAB_MODE_LAZY).setToolTip("View Source");

		// The refresh current view button.
		WButton refreshButton = new WButton("Refresh");
		refreshButton.setImage("/image/refresh-w.png");
		refreshButton.setRenderAsLink(true);
		refreshButton.setAction(new ValidatingAction(messages.getValidationErrors(), refreshButton) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				// Do Nothing
			}
		});

		// The reset example button.
		final WButton resetButton = new WButton("Reset");
		resetButton.setImage("/image/cancel-w.png");
		resetButton.setRenderAsLink(true);
		resetButton.setAction(new ValidatingAction(messages.getValidationErrors(), resetButton) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				resetExample();
			}
		});

		addToTail(refreshButton);
		addToTail(new WText("\u2002"));
		addToTail(resetButton);
	}

	/**
	 * Add an item to the WSection's content.
	 *
	 * @param component The component to add.
	 */
	public void add(final WComponent component) {
		this.getContent().add(component);
	}

	/**
	 * Add a component to the WDecoratedLabel's tail container.
	 *
	 * @param component The component to add.
	 */
	private void addToTail(final WComponent component) {
		WContainer tail = (WContainer) getDecoratedLabel().getTail();
		if (null != tail) { // bloody well better not be...
			tail.add(component);
		}
	}

	/**
	 * Selects an example.
	 *
	 * @param example the example to select.
	 * @param exampleName the name of the example being selected.
	 */
	public void selectExample(final WComponent example, final String exampleName) {
		WComponent currentExample = container.getChildAt(0).getParent();

		if (currentExample != null && currentExample.getClass().equals(example.getClass())) {
			// Same example selected, do nothing
			return;
		}

		resetExample();
		container.removeAll();

		this.getDecoratedLabel().setBody(new WText(exampleName));

		WApplication app = WebUtilities.getAncestorOfClass(WApplication.class, this);
		if (app != null) {
			app.setTitle(exampleName);
		}

		if (example instanceof ErrorComponent) {
			tabset.getTab(0).setText("Error");
			source.setSource(null);
		} else {
			String className = example.getClass().getName();
			WDefinitionList list = new WDefinitionList(WDefinitionList.Type.COLUMN);
			container.add(list);
			list.addTerm("Example path", new WText(className.replaceAll("\\.", " / ")));
			list.addTerm("Example JavaDoc", new JavaDocText(getSource(className)));
			container.add(new WHorizontalRule());
			tabset.getTab(0).setText(example.getClass().getSimpleName());
			source.setSource(getSource(className));
		}

		container.add(example);
		example.setLocked(true);
	}

	/**
	 * Selects an example. If there is an error instantiating the component, an error message will be displayed.
	 *
	 * @param example the ExampleData of the example to select.
	 */
	public void selectExample(final ExampleData example) {
		try {
			StringBuilder exampleName = new StringBuilder();
			if (example.getExampleGroupName() != null && !example.getExampleGroupName().equals("")) {
				exampleName.append(example.getExampleGroupName()).append(" - ");
			}
			exampleName.append(example.getExampleName());
			selectExample(example.getExampleClass().newInstance(), exampleName.toString());
		} catch (Exception e) {
			WMessages.getInstance(this).error("Error selecting example \"" + example.
					getExampleName() + '"');
			selectExample(new ErrorComponent(e.getMessage(), e), "Error");
		}
	}

	/**
	 * Resets the currently selected example.
	 */
	public void resetExample() {
		container.resetContent();
	}

	/**
	 * Tries to obtain the source file for the given class.
	 *
	 * @param className the name of the class to find the source for.
	 * @return the source file for the given class, or null on error.
	 */
	private static String getSource(final String className) {
		String sourceName = '/' + className.replace('.', '/') + ".java";

		InputStream stream = null;

		try {
			stream = ExampleSection.class.getResourceAsStream(sourceName);

			if (stream != null) {
				byte[] sourceBytes = StreamUtil.getBytes(stream);

				// we need to do some basic formatting of the source now.
				return new String(sourceBytes, "UTF-8");
			}
		} catch (IOException e) {
			LOG.warn("Unable to read source code for class " + className, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					LOG.error("Error closing stream", e);
				}
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WMessages getMessages() {
		return messages;
	}
}
