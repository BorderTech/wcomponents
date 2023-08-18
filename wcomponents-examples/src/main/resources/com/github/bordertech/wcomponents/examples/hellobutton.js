require(["wc/dom/event", "wc/dom/initialise"], function(event, initialise){
	// This script exists _only_ as an example to show use of WButton setClientCommandOnly(boolean);

	/**
	 * If an element with class 'hellobutton' is clicked, pop up an alert dialog.
	 * @param {MouseEvent} $event the wrapped click event
	 */
	function clickEvent($event) {
		const target = $event.target;

		if (target && target.classList.contains("hellobutton")) {
			window.alert("hello");
		}
	}
	// add an event manager subscription for the click handler
	initialise.register({ postInit: () => event.add(document.body, "click", clickEvent) });
});
