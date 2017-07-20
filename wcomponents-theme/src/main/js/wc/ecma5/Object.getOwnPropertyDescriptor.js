define(["wc/has"], function(has) {
	"use strict";
	if (!has("object-getownpropertydescriptor") && has("object-definesetter")) {
		Object.getOwnPropertyDescriptor = getOwnPropertyDescriptor;
	}

	/*
	 * getOwnPropertyDescriptor for when there is no native function
	 * Adapted from: http://www.refactory.org/s/object_getownpropertydescriptor/view/latest
	 */
	function getOwnPropertyDescriptor(obj, prop) {
		var descriptor = {configurable: true, enumerable: true, writable: true, value: undefined},
			getter = obj.__lookupGetter__(prop),
			setter = obj.__lookupSetter__(prop);

		if (!Object.prototype.hasOwnProperty.call(obj, prop)) {
			// property doesn't exist or is inherited
			descriptor.data = undefined;
			return descriptor;
		}

		if (!getter && !setter) {
			// not an accessor so return prop
			descriptor.value = obj[prop];
			return descriptor;
		}

		// there is an accessor, remove descriptor.writable; populate descriptor.get and descriptor.set
		delete descriptor.writable;
		delete descriptor.value;
		descriptor.get = descriptor.set = undefined;

		if (getter) {
			descriptor.get = getter;
		}

		if (setter) {
			descriptor.set = setter;
		}

		return descriptor;
	}
	return getOwnPropertyDescriptor;
});
