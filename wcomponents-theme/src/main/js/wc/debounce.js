define(["wc/timers"], function(timers) {
	/**
	 * Returns a wrapper function which will invoke the wrapped function only
	 * when it has not been called for `delay` milliseconds.
	 *
	 * @param func The function to debounce.
	 * @param delay The period between last call and invocation.
	 * @returns {Function} Essentially a debounced version of the function passed in as `func`.
	 */
	function debounce(func, delay) {
		var timer;
		return function debounceWrapper() {
			var $this = this, args = arguments;
			if (timer || timer === 0) {
				timers.clearTimeout(timer);
			}
			timer = timers.setTimeout(function() {
				try {
					func.apply($this, args);
				} catch (ex) {
					console.error("Error in debounced function", ex);
				}
			}, delay);
		};
	}

	return debounce;
});
