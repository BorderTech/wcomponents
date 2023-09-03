/**
 * Adds undo/redo functionality to the image editor.
 * Being in a separate module is a lie, this is not a standalone reusable bit of functionality.
 * It is split up for the sake of maintenance sanity.
 */

const SAVE_INTERVAL = 1000,
	MAX_HISTORY = 30;

class FabricUndoRedo {
	/**
	 * @param {module:wc/ui/ImageEdit} imageEdit The imageEdit this is really a part of.
	 */
	constructor(imageEdit) {
		let timer;
		let state = [];
		let ignoreChanges = false;
		let theVeryFirstState;
		let modPointer = 0;

		/**
		 * Does the current state differ from the initial state?
		 * @returns {Boolean} true if the current state is different (i.e. the user has made some changes).
		 */
		this.hasChanges = function () {
			const currentState = state[modPointer];
			return !!(currentState && theVeryFirstState && (currentState !== theVeryFirstState));

		};

		/**
		 * It is silly to save state when only small amounts of "work" have been undertaken,
		 * this function queues a save request and ensures that they don't happen too often.
		 */
		function debounceSave() {
			if (timer) {
				window.clearTimeout(timer);
			}
			timer = window.setTimeout(function () {
				save();
			}, SAVE_INTERVAL);
		}

		function save() {
			if (!ignoreChanges) {
				const serializedState = JSON.stringify(imageEdit.getCanvas());
				if (state.length > 0) {
					const oldState = state[modPointer];
					if (serializedState === oldState) {
						return;
					}
				} else {
					theVeryFirstState = serializedState;
				}
				const diff = state.length - modPointer;
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
			const newState = state[idx];
			if (newState) {
				modPointer = idx;
				const canvas = imageEdit.getCanvas();
				canvas.clear().renderAll();
				canvas.loadFromJSON(newState, renderCanvas);
				renderCanvas();
			}
		}
		function objectAdded($event) {
			const object = $event.target;
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
		this.undo = function () {
			const idx = modPointer - 1;
			if (idx >= 0) {
				restoreState(idx);
			}
		};

		/**
		 * You are using a computer, you know what "redo" does.
		 */
		this.redo = function () {
			const idx = modPointer + 1;
			if (modPointer < state.length) {
				restoreState(idx);
			}
		};

		/**
		 * Resets the image to its initial state.
		 */
		this.reset = function () {
			// I wrote this, and it worked first go, scary...
			if (this.hasChanges()) {
				state.length = modPointer = 0;
				state.push(theVeryFirstState);
				restoreState(0);
			}
		};
	}
}
export default FabricUndoRedo;
