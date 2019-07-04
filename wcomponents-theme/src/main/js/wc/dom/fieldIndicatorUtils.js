define(["wc/dom/Widget"], function(Widget) {
	var widgets,
		utils = {
			getWidgets: function() {
				return widgets || (widgets = createWidgets());
			}
		};

	function createWidgets() {
		var widgetMap = {
			FIELDINDICATOR: new Widget("wc-fieldindicator")
		};
		return widgetMap;
	}

	return utils;
});
