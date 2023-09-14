import fabric from "fabric/dist/fabric.js";

let redactMode = false, startX, startY, imageEdit;
const fabricRedact = {
		drawStart: function() {
			let shape = fabricRedact._rect;
			if (shape) {
				console.warn("drawEnd not called");
				imageEdit.getCanvas().remove(shape);
			}
			shape = fabricRedact._rect = new fabric.Rect({
				top : startY,
				left : startX,
				width : 0,
				height : 0,
				fill : "black",
				stroke: "black",
				selectable: true,
				strokewidth: 4
			});
			fabricRedact.paintShape(shape);
		},
		drawEnd: function() {
			const fbCanvas = imageEdit.getCanvas(),
				shape = fabricRedact._rect;
			if (shape) {
				try {
					if (shape.width !== 0 && shape.height !== 0) {
						fbCanvas.trigger("object:added", { target: shape });
					} else {
						fbCanvas.remove(shape);
					}
				} finally {
					delete fabricRedact._rect;
				}
			}
		},
		drawing: function(width, height) {
			const shape = fabricRedact._rect;
			shape.set("width", width);
			shape.set("height", height);
			if (width < 0) {
				shape.originX = "right";
			} else {
				shape.originX = "left";
			}
			if (height < 0) {
				shape.originY = "bottom";
			} else {
				shape.originY = "top";
			}
			imageEdit.renderCanvas();
		},
		paintShape: function(shape) {
			const fbCanvas = imageEdit.getCanvas();
			fbCanvas.add(shape);
			//	var lft, top, group;
			//	group = getGroup();
			//	group.add(shape);
			//	lft = shape.getLeft() - group.getBoundingRectWidth() / 2;
			//	top = shape.getTop() - group.getBoundingRectHeight() / 2;
			// //	lft = lft / group.scaleX;
			// //	top = top / group.scaleY;
			//	shape.setLeft(lft);
			//	shape.setTop(top);
		}
	},
	redactor = {
		controls: function(eventConfig) {
			var click = eventConfig.click;
			click.redact = {
				func: function(config, $event) {
					const element = $event.target,
						fbImage = imageEdit.getFbImage();
					if (fbImage) {
						redactMode = element.checked;
						fbImage.selectable = !redactMode;
						const redactions = redactor.getRedactions();
						if (redactions) {
							for (let i = 0; i < redactions.length; i++) {
								let next = redactions[i];
								next.selectable = redactMode;
							}
						}
					} else {
						redactMode = element.checked = false;
					}
				}
			};
		},
		activate: function(editor) {
			const handlers = fabricRedact;
			imageEdit = editor;
			imageEdit.getCanvas().preserveObjectStacking = true;
			wireEventListeners(handlers);
			redactMode = false;
		},
		getRedactions: function() {
			const fbCanvas = imageEdit.getCanvas();
			return fbCanvas.getObjects("rect");
		}
	};

// function getGroup() {
//	var fbImage, group = fbCanvas.getObjects("group");
//	if (group && group.length) {
//		group = group[0];
//	}
//	else {
//		fbImage = imageEdit.getFbImage();
//		if (fbImage) {
//			group = new fabric.Group([fbImage.cloneAsImage()], {
//				top: fbImage.getTop(),
//				left: fbImage.getLeft(),
//				originX: "left",
//				originY: "top",
//				selectable: fbImage.selectable
//			});
//			fbCanvas.clear().renderAll();
//			fbCanvas.add(group);
//		}
//	}
//	return group;
// }

function wireEventListeners(handlers) {
	const initedKey = "wc_redact_inited";
	let isMouseDown = false;
	let fbCanvas = imageEdit.getCanvas();

	try {
		if (!fbCanvas[initedKey]) {
			fbCanvas[initedKey] = true;
			fbCanvas.on("mouse:down", mousedownEvent);
			fbCanvas.on("mouse:up", mouseupEvent);
			fbCanvas.on("mouse:move", mousemoveEvent);
		} else {
			console.warn("redact shouldn't double init");
		}
	} finally {
		fbCanvas =  null;
	}

	/**
	 *
	 * @param {{ e: MouseEvent }} option
	 */
	function mousedownEvent({e: event}) {
		if (redactMode && event.button === 0) {
			startX = event.offsetX || 0;  // offsetX
			startY = event.offsetY || 0;  // offsetY
			isMouseDown = true;
			if (handlers.drawStart) {
				handlers.drawStart();
			}
		}
	}

	function mouseupEvent() {
		isMouseDown = false;
		if (redactMode) {
			if (handlers.drawEnd) {
				handlers.drawEnd();
			}
		}
		startX = startY = 0;
	}

	/**
	 * @param {{e: MouseEvent}} option
	 */
	function mousemoveEvent({ e: event }) {
		if (redactMode && isMouseDown && handlers.drawing) {
			const width = event.offsetX - startX;
			const height = event.offsetY - startY;
			handlers.drawing(width, height);
			// console.log("width", width, "height", height, "event.offsetX", event.offsetX, "event.offsetY", event.offsetY, "startX", startX, "startY", startY);
		}
	}
}

export default redactor;
