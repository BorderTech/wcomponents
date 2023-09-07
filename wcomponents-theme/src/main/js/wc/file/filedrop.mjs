import event from "wc/dom/event";
import timers from "wc/timers";

let dragging = false;
const CLASSNAME = "wc_dragging",
	timer = {},
	handlers = {
		/**
		 * @param {DragEvent & { currentTarget: HTMLElement}} $event
		 * @param {(function(DragEvent): void)} callback
		 */
		dragenter: function($event, callback) {
			if (!dragging) {
				draggingStarted($event.currentTarget, callback, "dragstart");
			}
			$event.stopPropagation();
		},
		/**
		 * @param {DragEvent & { currentTarget: HTMLElement}} $event
		 * @param {(function(DragEvent): void)} callback
		 */
		dragover: function($event, callback) {
			if (!dragging) {
				draggingStarted($event.currentTarget, callback, "dragstart");
			}
			$event.stopPropagation();
		},
		/**
		 * @param {DragEvent & { currentTarget: HTMLElement}} $event
		 * @param {(function(DragEvent): void)} callback
		 */
		dragleave: function($event, callback) {
			const element = $event.currentTarget;
			$event.stopPropagation();
			timer[element.id] = timers.setTimeout(draggingStopped, 500, element, callback, "dragstop", null);
		},
		/**
		 * @param {DragEvent & { currentTarget: HTMLElement}} $event
		 * @param {(function(DragEvent): void)} callback
		 */
		drop: function ($event, callback) {
			const element = $event.currentTarget,
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

/**
 *
 * @param {Element} element
 * @param {(function(string, ?FileList): void)} [callback]
 * @param {string} [type]
 * @param {FileList} [files]
 */
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

/**
 * An event handler that will call the callback on various drag events.
 * @param {(function(string, ?FileList): void)} callback
 * @return {(function(DragEvent): void)}
 */
function callbackWrapper(callback) {
	return function ($event) {
		const handler = handlers[$event.type];
		if (handler) {
			$event.preventDefault();
			handler($event, callback);
		}
	};
}

/**
 * Registers a drop-zone.
 * @param {string} id
 * @param {(function(string, ?FileList): void)} callback
 */
function register(id, callback) {
	const element = document.getElementById(id);
	if (element) {
		const wrappedCallback = callbackWrapper(callback);
		event.add(element, "dragenter", wrappedCallback);
		event.add(element, "dragleave", wrappedCallback);
		event.add(element, "dragover", wrappedCallback);
		event.add(element, "drop", wrappedCallback);
	} else {
		console.warn("Could not register dropzone", id);
	}
}

export default {
	register
};
