package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.util.I18nUtilities;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jonathan Austin
 * @since 1.0.3
 */
public class WTreeItem extends WBeanComponent implements Container, Disableable {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(WTreeItem.class);

	/**
	 * A holder for a tree item image, if set.
	 */
	private final TreeItemImage itemImage = new TreeItemImage(this);

	/**
	 * Creates a WTreeItem with no text or image. The item text or image must be set after construction.
	 */
	public WTreeItem() {
		add(itemImage);
	}

	/**
	 * Creates a WTreeItem with the specified text.
	 *
	 * @param text the tree item text, using {@link MessageFormat} syntax.
	 *
	 * <pre>
	 * // Will create a tree item with the text &quot;Hello world&quot;
	 * new WTreeItem(&quot;Hello world&quot;);
	 * </pre>
	 */
	public WTreeItem(final String text) {
		this();
		setText(text);
	}

	/**
	 * Indicates whether this button is disabled in the given context.
	 *
	 * @return true if this button is disabled, otherwise false.
	 */
	@Override
	public boolean isDisabled() {
		return isFlagSet(ComponentModel.DISABLED_FLAG);
	}

	/**
	 * Sets whether this button is disabled.
	 *
	 * @param disabled if true, this button is disabled. If false, it is enabled.
	 */
	@Override
	public void setDisabled(final boolean disabled) {
		setFlag(ComponentModel.DISABLED_FLAG, disabled);
	}

	/**
	 * Return the button text. Returns:
	 * <ul>
	 * <li>else user text if set</li>
	 * <li>else shared text if set</li>
	 * <li>user value if set</li>
	 * <li>bean value if present</li>
	 * <li>else shared value</li>
	 * </ul>
	 *
	 * @return the button text.
	 */
	public String getText() {
		Object text = getComponentModel().text;

		if (text == null) {
			Object value = getData();
			if (value != null) {
				text = value.toString();
			}
		}

		return I18nUtilities.format(null, text);
	}

	/**
	 * Sets the button text.
	 *
	 * @param text the button text, using {@link MessageFormat} syntax.
	 * @param args optional arguments for the button text format string.
	 *
	 * <pre>
	 * // Sets the button text to &quot;Hello world&quot;
	 * myButton.setText(&quot;Hello world&quot;);
	 * </pre>
	 *
	 * <pre>
	 * // Sets the button text to &quot;Secret agent James Bond, 007&quot;
	 * myButton.setText(&quot;Secret agent {0}, {1,number,000}&quot;, &quot;James Bond&quot;, 7);
	 * </pre>
	 */
	public void setText(final String text, final Serializable... args) {
		TreeItemModel model = getOrCreateComponentModel();
		model.text = I18nUtilities.asMessage(text, args);
	}

	/**
	 * Return the image to display on the button.
	 *
	 * @return the image.
	 */
	public Image getImage() {
		return getComponentModel().image;
	}

	/**
	 * Sets the image to display on the button.
	 *
	 * @param image the image, or null for no image.
	 */
	public void setImage(final Image image) {
		TreeItemModel model = getOrCreateComponentModel();
		model.image = image;
		model.imageUrl = null;
	}

	/**
	 * Sets the image to display on the button. The image will be read from the application's class path rather than
	 * from its web docs.
	 *
	 * @param image the relative path to the image resource, or null for no image.
	 */
	public void setImage(final String image) {
		setImage(new ImageResource(image));
	}

	/**
	 * Return the {@link WImage} used by this button to hold the {@link Image} resource.
	 * <p>
	 * If the button is not using an Image resource, it will return null.
	 * </p>
	 *
	 * @return the WImage holding the Image resource, or null if the button is not using an Image resource.
	 */
	public WImage getImageHolder() {
		return getImage() == null ? null : itemImage;
	}

	/**
	 * Return the URL of the image to display on the button.
	 *
	 * @return the image url.
	 */
	public String getImageUrl() {
		return getImage() == null ? getComponentModel().imageUrl : getImageHolder().getTargetUrl();
	}

	/**
	 * Sets the URL of the image to display on the button.
	 *
	 * @param imageUrl the image url, or null for no image.
	 */
	public void setImageUrl(final String imageUrl) {
		TreeItemModel model = getOrCreateComponentModel();
		model.imageUrl = imageUrl;
		model.image = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getChildCount() {
		return super.getChildCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public WComponent getChildAt(final int index) {
		return super.getChildAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// to make public
	public int getIndexOfChild(final WComponent childComponent) {
		return super.getIndexOfChild(childComponent);
	}

	@Override
	public List<WComponent> getChildren() {
		return super.getChildren();
	}

	/**
	 * @return a String representation of this component, for debugging purposes.
	 */
	@Override
	public String toString() {
		String text = getText();
		text = text == null ? "null" : '"' + text + '"';
		return toString(text, -1, -1);
	}

	// --------------------------------
	// Extrinsic state management
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeItemModel newComponentModel() {
		return new TreeItemModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeItemModel getComponentModel() {
		return (TreeItemModel) super.getComponentModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeItemModel getOrCreateComponentModel() {
		return (TreeItemModel) super.getOrCreateComponentModel();
	}

	/**
	 * Holds the extrinsic state information of a WTreeItem.
	 */
	public static class TreeItemModel extends BeanAndProviderBoundComponentModel {

		/**
		 * The text to display on the tree item.
		 */
		private Serializable text;

		/**
		 * If not null, it will be taken as a URL to use as image.
		 */
		private String imageUrl;

		/**
		 * The image to display on the tree item.
		 */
		private Image image;
	}

	/**
	 * This WImage implementation delegates to the button's image and is only used to serve up the image for the button.
	 */
	private static final class TreeItemImage extends WImage {

		/**
		 * The button containing the button image.
		 */
		private final WTreeItem item;

		/**
		 * Creates a tree item image.
		 *
		 * @param item the owning tree item.
		 */
		private TreeItemImage(final WTreeItem item) {
			this.item = item;
		}

		/**
		 * Override isVisible to only return true if the tree item has an image.
		 *
		 * @return true if this component is visible, false if invisible.
		 */
		@Override
		public boolean isVisible() {
			return item.getImage() != null;
		}

		/**
		 * Override getImage to return the tree item's image.
		 *
		 * @return the button image.
		 */
		@Override
		public Image getImage() {
			return item.getImage();
		}
	}
}
