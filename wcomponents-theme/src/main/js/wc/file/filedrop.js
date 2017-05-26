define(["wc/has", "wc/dom/event", "wc/timers"], function(has, event, timers) {
	"use strict";
	var CLASSNAME = "wc_dragging",
		timer = {},
		dragging = false,
		handlers = {
			dragenter: function($event, callback) {
				if (!dragging) {
					draggingStarted($event.currentTarget, callback, "dragstart");
				}
				$event.stopPropagation();
			},
			dragover: function($event, callback) {
				if (!dragging) {
					draggingStarted($event.currentTarget, callback, "dragstart");
				}
				$event.stopPropagation();
			},
			dragleave: function($event, callback) {
				var element = $event.currentTarget;
				$event.stopPropagation();
				timer[element.id] = timers.setTimeout(draggingStopped, 500, element, callback, "dragstop", null);
			},
			drop: function ($event, callback) {
				var element = $event.currentTarget,
					files = $event.dataTransfer.files;
				draggingStopped(element);
				timer[element.id] = timers.setTimeout(draggingStopped, 100, element, callback, $event.type, files);
			}
		};

	function draggingStarted(element, callback, type) {
		dragging = true;
		element.classList.add(CLASSNAME);
		if (timer[element.id]) {
			timers.clearTimeout(timer[element.id]);
		}
		if (type && callback) {
			callback(type);
		}
	}

	function draggingStopped(element, callback, type, files) {
		dragging = false;
		element.classList.remove(CLASSNAME);
		if (timer[element.id]) {
			timers.clearTimeout(timer[element.id]);
		}
		if (type && callback) {
			callback(type, files);
		}
	}

	function callbackWrapper(callback) {
		return function ($event) {
			var handler = handlers[$event.type];
			if (handler) {
				$event.preventDefault();
				handler($event, callback);
			}
		};
	}

	function register(id, callback) {
		var element = document.getElementById(id),
			wrappedCallback = callbackWrapper(callback);
		if (element) {
			event.add(element, "dragenter", wrappedCallback);
			event.add(element, "dragleave", wrappedCallback);
			event.add(element, "dragover", wrappedCallback);
			event.add(element, "drop", wrappedCallback);
		} else {
			console.warn("Could not register dropzone", id);
		}
	}

	return {
		register: has("draganddrop") ? register : function() {}
	};
});
