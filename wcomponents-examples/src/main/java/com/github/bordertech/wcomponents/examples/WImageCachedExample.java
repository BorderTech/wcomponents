package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.WImage;
import com.github.bordertech.wcomponents.WPanel;

/**
 * Demonstrate a cached and not cached dynamic image.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WImageCachedExample extends WPanel {

	/**
	 * Construct example.
	 */
	public WImageCachedExample() {
		// Cached dynamic image (ie has cache key)
		WImage image = new WImage();
		image.setCacheKey("CacheKeyDemo");
		image.setImage(new DynamicImage("Cached"));
		add(image);

		// Dynamic image that is not cached
		image = new WImage();
		image.setImage(new DynamicImage("No cache"));
		add(image);
	}

}
