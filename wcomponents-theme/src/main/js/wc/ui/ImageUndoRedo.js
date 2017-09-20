/**
 * Adds undo/redo functinality to the image editor.
 * Being in a separate module is a lie, this is not a standalone reusable bit of functionality.
 * It is split up for the sake of maintenance sanity.
 */
define(function() {
	var SAVE_INTERVAL = 1000,
		MAX_HISTORY = 30;

	/**
	 *
	 * @param {ImageEdit} imageEdit The imageEdit this is really a part of.
	 * @constructor
	 */
	function FabricUndoRedo(imageEdit) {
		var ignoreChanges = false,
			timer,
			state = [],
			theVeryFirstState,
			modPointer = 0;

		/**
		 * Does the current state differ from the initial state?
		 * @returns {Boolean} true if the current state is different (i.e. the user has made some changes).
		 */
		this.hasChanges = function() {
			var currentState = state[modPointer];
			if (currentState && theVeryFirstState && (currentState !== theVeryFirstState)) {
				return true;
			}
			return false;
		};

		/**
		 * It is silly to save state when only small amounts of "work" have been undertaken,
		 * this function queues a save request and ensures that they don't happen too often.
		 */
		function debounceSave() {
			if (timer) {
				window.clearTimeout(timer);
			}
			timer = window.setTimeout(function() {
				save();
			}, SAVE_INTERVAL);
		}

		function save() {
			var serializedState, diff, oldState;
			if (!ignoreChanges) {
				serializedState = JSON.stringify(imageEdit.getCanvas());
				if (state.length > 0) {
					oldState = state[modPointer];
					if (serializedState === oldState) {
						return;
					}
				} else {
					theVeryFirstState = serializedState;
				}
				diff = state.length - modPointer;
				state.splice(modPointer + 1, diff, serializedState);
				if (state.length > MAX_HISTORY) {
					state.shift();
				}
				modPointer = state.length - 1;
			}
		}

		function renderCanvas() {
			ignoreChanges = true;
			imageEdit.renderCanvas(restoreDone);
		}

		function restoreDone() {
			ignoreChanges = false;
		}

		/**
		 * Restores state from the history at the given index in the stack.
		 * @param {number} idx The index of the state to restore.
		 */
		function restoreState(idx) {
			var newState = state[idx],
				canvas = imageEdit.getCanvas();
			if (newState) {
				modPointer = idx;
				canvas.clear().renderAll();
				canvas.loadFromJSON(newState, renderCanvas);
				renderCanvas();
			}
		}

		function objectAdded($event) {
			var object = $event.target;
			if (object && object.width && object.height) {  // e.g. when a redact rect is added it has zero dimensions
				debounceSave();
			}
		}

		imageEdit.getCanvas().on("object:modified", debounceSave);
		imageEdit.getCanvas().on("object:added", objectAdded);

		this.save = debounceSave;

		/**
		 * You are using a computer, you know what "undo" does.
		 */
		this.undo = function() {
			var idx = modPointer - 1;
			if (idx >= 0) {
				restoreState(idx);
			}
		};

		/**
		 * You are using a computer, you know what "redo" does.
		 */
		this.redo = function() {
			var idx = modPointer + 1;
			if (modPointer < state.length) {
				restoreState(idx);
			}
		};

		/**
		 * Resets the image to its initial state.
		 */
		this.reset = function() {
			// I wrote this and it worked first go, scary...
			if (this.hasChanges()) {
				state.length = modPointer = 0;
				state.push(theVeryFirstState);
				restoreState(0);
			}
		};
	}
	return FabricUndoRedo;
});