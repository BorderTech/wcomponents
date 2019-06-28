define(["wc/date/Parser", "wc/i18n/i18n"], function(Parser, i18n) {

	/**
	 * Provides the set of configured date Parser instances.
	 * @constructor
	 */
	function Parsers() {
		var parsers, typeEnum = {
				STANDARD: "standard",
				PAST: "past",
				PARTIAL: "partial",
				PARTIAL_PAST: "partialPast"
			};

		this.type = typeEnum;

		this.get = function(type) {
			var result;
			if (!parsers) {
				initParsers();
			}
			result = parsers[typeEnum.STANDARD];
			if (type) {
				if (parsers.hasOwnProperty(type)) {
					result = parsers[type];
				} else {
					console.warn("Invalid parser type", type);
				}
			}
			return result;
		};

		/**
		 * Initialises the "parsers" instance variable.
		 * @function
		 * @private
		 */
		function initParsers() {
			var shortcuts = ["ytm", "+-"],
				standardMasks = shortcuts.concat(i18n.get("datefield_masks_full").split(",")),
				partialMasks = standardMasks.concat(i18n.get("datefield_masks_partial").split(","));

			/*
			 * Creates a new instance of a Parser
			 */
			function createParser(masks, expandYearIntoPast, rolling) {
				var result = new Parser();
				result.setRolling(!!rolling);
				result.setMasks(masks || standardMasks);
				result.setExpandYearIntoPast(!!expandYearIntoPast);
				return result;
			}

			parsers = {};
			parsers[typeEnum.STANDARD] = createParser();
			parsers[typeEnum.PAST] = createParser(null, true);
			parsers[typeEnum.PARTIAL] = createParser(partialMasks);
			parsers[typeEnum.PARTIAL_PAST] = createParser(partialMasks, true);
		}
	}

	return new Parsers();
});
