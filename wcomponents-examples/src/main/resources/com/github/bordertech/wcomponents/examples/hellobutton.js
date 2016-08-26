require(["wc/compat/compat!"], function() {
	require(["wc/dom/event", "wc/dom/classList", "wc/dom/initialise"], function(event, classList, initialise){
		// This script exists _only_ as an example to show use of WButton setClientCommandOnly(boolean);

		/**
		 * If an element with class 'hellobutton' is clicked, pop up an alert dialog.
		 * @param {module:wc/dom/event} $event the wrapped click event
		 */
		function clickEvent($event) {
			var target = $event.target;

			if (target && classList.contains(target, "hellobutton")) {
				window.alert("hello");
			}
		}
		// add an event manager subscription for the click handler
		initialise.register({postInit: function(){
			event.add(document.body, event.TYPE.click, clickEvent);
		}});
	});
});
