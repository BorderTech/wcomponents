define(["fabric"], function(fabric) {
	var fbCanvas, imageEdit, redactMode = false, startX, startY,
		fabricRedact = {
			mousedown: function(event) {
				fabricRedact._rect = new fabric.Rect({
					top : startY,
					left : startX,
					width : 0,
					height : 0,
					fill : "black",
					stroke: "black",
					strokewidth: 4
				});
				fabricRedact.paintShape(fabricRedact._rect);
			},
			mousemove: function(event, width, height) {
				fabricRedact._rect.set("width", width);
				fabricRedact._rect.set("height", height);
			},
			paintShape: function(shape) {
				var fbImage, group = fbCanvas.getObjects("group");
				if (group && group.length) {
					group = group[0];
					group.add(shape);
				}
				else {
					fbImage = imageEdit.getFbImage();
					if (fbImage) {
						group = new fabric.Group([fbImage.cloneAsImage(), shape]);
						fbCanvas.clear().renderAll();
						fbCanvas.add(group);
					}
				}
			}
		};

	function wireEventListeners(handlers) {
		var isMouseDown = false;

		fbCanvas.on("mouse:down", function(option) {
			var event = option.e;
			if (redactMode && event.button === 0) {
				startX = event.offsetX || 0;  // offsetX
				startY = event.offsetY || 0;  // offsetY
				isMouseDown = true;
				if (handlers.mousedown) {
					handlers.mousedown(event);
				}
			}
		});

		fbCanvas.on("mouse:up", function(option) {
			isMouseDown = false;
			if (redactMode) {
				if (handlers.mouseup) {
					handlers.mouseup(option.e);
				}
			}
		});

		fbCanvas.on("mouse:move", function(option) {
			var width, height, event = option.e;

			if (redactMode && isMouseDown && handlers.mousemove) {
				width = event.offsetX - startX;
				height = event.offsetY - startY;
				handlers.mousemove(option.e, width, height);
			}
		});
	}

	return {
		controls: function(eventConfig) {
			var click = eventConfig.click;
			click.redact = {
				func: function() {
					var fbImage = imageEdit.getFbImage();
					if (fbImage) {
						redactMode = !redactMode;
						fbImage.selectable = !redactMode;
					}
					else {
						redactMode = false;
					}
				}
			};
		},
		activate: function(canvas, editor) {
			var handlers = fabricRedact;
			fbCanvas = canvas;
			imageEdit = editor;
			wireEventListeners(handlers);
		}
	};

});
