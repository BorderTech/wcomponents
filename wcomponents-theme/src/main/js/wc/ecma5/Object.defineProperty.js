define(["wc/has"], function(has) {
	"use strict";
	/*
	 * This class is an implementation of the adapter pattern that wraps the old, non-standard
	 * accessor property API in the newer, standard API.
	 *
	 * This is necessary at time of writing as browsers are transitioning from non-standard to
	 * standard.
	 *
	 * Note that Safari 5 is a strange case where it supports both APIs but the standard API
	 * is crippled in that you can not set accessor properties on DOM elements using the
	 * standard API. We therefore blow away Safari's native implementation with our wrapper.
	 *
	 * The other issue with older versions of Safari is that you can't set an accessor property that
	 * collides with a native DOM element property.
	 *
	 * Strange that they didn't just write an adapter themselves under the hood.
	 *
	 * Beware, Chrome's user agent string contains Safari version numbers too, for example "Safari/537.15",
	 * however no version of Chrome should have this fix applied.
	 */

	if (!has("object-defineproperty-dom") && has("object-definegetter")) {
		Object.defineProperty = defineProperty;
	}

	/*
	 * Wraps non-standard accessor property API in standard one.
	 */
	function defineProperty(obj, prop, descriptor) {
		var result = obj,
			getter = descriptor.get,
			setter = descriptor.set;
		if (getter) {
			obj.__defineGetter__(prop, getter);
		}
		if (setter) {
			obj.__defineSetter__(prop, setter);
		}
		return result;
	}
	return defineProperty;
});
