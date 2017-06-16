define(["fabric"], function(fabric) {
	var imageEdit, redactMode = false, startX, startY,
		fabricRedact = {
			drawStart: function() {
				var shape = fabricRedact._rect;
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
				var fbCanvas = imageEdit.getCanvas(),
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
				var shape = fabricRedact._rect;
				shape.set("width", width);
				shape.set("height", height);
				if (width < 0) {
					shape.setOriginX("right");
				} else {
					shape.setOriginX("left");
				}
				if (height < 0) {
					shape.setOriginY("bottom");
				} else {
					shape.setOriginY("top");
				}
				imageEdit.renderCanvas();
			},
			paintShape: function(shape) {
				var fbCanvas = imageEdit.getCanvas();
				fbCanvas.add(shape);
//				var lft, top, group;
//				group = getGroup();
//				group.add(shape);
//				lft = shape.getLeft() - group.getBoundingRectWidth() / 2;
//				top = shape.getTop() - group.getBoundingRectHeight() / 2;
// //				lft = lft / group.scaleX;
// //				top = top / group.scaleY;
//				shape.setLeft(lft);
//				shape.setTop(top);
			}
		},
		redactor = {
			controls: function(eventConfig) {
				var click = eventConfig.click;
				click.redact = {
					func: function(config, $event) {
						var redactions, i, next,
							element = $event.target,
							fbImage = imageEdit.getFbImage();
						if (fbImage) {
							redactMode = element.checked;
							fbImage.selectable = !redactMode;
							redactions = redactor.getRedactions();
							if (redactions) {
								for (i = 0; i < redactions.length; i++) {
									next = redactions[i];
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
				var handlers = fabricRedact;
				imageEdit = editor;
				imageEdit.getCanvas().preserveObjectStacking = true;
				wireEventListeners(handlers);
				redactMode = false;
			},
			getRedactions: function() {
				var fbCanvas = imageEdit.getCanvas(),
					rects = fbCanvas.getObjects("rect");
				return rects;
			}
		};

//	function getGroup() {
//		var fbImage, group = fbCanvas.getObjects("group");
//		if (group && group.length) {
//			group = group[0];
//		}
//		else {
//			fbImage = imageEdit.getFbImage();
//			if (fbImage) {
//				group = new fabric.Group([fbImage.cloneAsImage()], {
//					top: fbImage.getTop(),
//					left: fbImage.getLeft(),
//					originX: "left",
//					originY: "top",
//					selectable: fbImage.selectable
//				});
//				fbCanvas.clear().renderAll();
//				fbCanvas.add(group);
//			}
//		}
//		return group;
//	}

	function wireEventListeners(handlers) {
		var initedKey = "wc_redact_inited",
			isMouseDown = false,
			fbCanvas = imageEdit.getCanvas();

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

		function mousedownEvent(option) {
			var event = option.e;
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

		function mousemoveEvent(option) {
			var width, height, event = option.e;

			if (redactMode && isMouseDown && handlers.drawing) {
				width = event.offsetX - startX;
				height = event.offsetY - startY;
				handlers.drawing(width, height);
				// console.log("width", width, "height", height, "event.offsetX", event.offsetX, "event.offsetY", event.offsetY, "startX", startX, "startY", startY);
			}
		}
	}

	return redactor;
});
