/**
 * Provides a calendar based date chooser control for use by WDateFields when a native date control is not available
 * and WPartialDateFields in all cases.
 *
 * @typedef {Object} module:wc/ui/calendar.config() Optional module configuration.
 * @property {?int} min The minimum year to allow in the date picker.
 * @default 1000
 * @property {?int} max The maximum year to allow in the date picker.
 * @default 9999.
 *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/date/addDays
 * @requires module:wc/date/copy
 * @requires module:wc/date/dayName
 * @requires module:wc/date/daysInMonth
 * @requires module:wc/date/getDifference
 * @requires module:wc/date/monthName
 * @requires module:wc/date/today
 * @requires module:wc/date/interchange
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/viewportCollision
 * @requires module:wc/dom/getBox
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/loader/resource
 * @requires external:lib/sprintf
 * @requires module:wc/isNumeric
 * @requires module:wc/ui/dateField
 * @requires module:wc/dom/initialise
 * @requires module:wc/timers
 *
 * @see {@link module:wc/ui/datefield}
 *
 * @todo needs a lot of documentation of private members.
 */
define(["wc/dom/attribute",
		"wc/date/addDays",
		"wc/date/copy",
		"wc/date/dayName",
		"wc/date/daysInMonth",
		"wc/date/getDifference",
		"wc/date/monthName",
		"wc/date/today",
		"wc/date/interchange",
		"wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/shed",
		"wc/dom/tag",
		"wc/dom/viewportCollision",
		"wc/dom/getBox",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/loader/resource",
		"lib/sprintf",
		"wc/isNumeric",
		"wc/ui/dateField",
		"wc/dom/initialise",
		"wc/timers",
		"module"],
/** @param attribute wc/dom/attribute @param addDays wc/date/addDays @param copy wc/date/copy @param dayName wc/date/dayName @param daysInMonth wc/date/daysInMonth @param getDifference wc/date/getDifference @param monthName wc/date/monthName @param today wc/date/today @param interchange wc/date/interchange @param classList wc/dom/classList @param event wc/dom/event @param focus wc/dom/focus @param shed wc/dom/shed @param tag wc/dom/tag @param viewportCollision wc/dom/viewportCollision @param getBox wc/dom/getBox @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param loader wc/loader/resource @param sprintf lib/sprintf @param isNumeric wc/isNumeric @param dateField wc/ui/dateField @param initialise wc/dom/initialise @param timers wc/timers @param module @ignore */
function(attribute, addDays, copy, dayName, daysInMonth, getDifference, monthName, today, interchange, classList, event,
		focus, shed, tag, viewportCollision, getBox, Widget, i18n, loader, sprintf, isNumeric, dateField, initialise,
		timers, module) {

	"use strict";

	/**
	 * @constructor
	 * @alias module:wc/ui/calendar~Calendar
	 * @private
	 */
	function Calendar() {
		var DATE_KEY = "date_key",
			CONTAINER_ID = "wc-calbox",
			DAY_CONTAINER_ID = "wc-caldaybox",
			MONTH_SELECT_ID = "wc-calmonth",
			YEAR_ELEMENT_ID = "wc-calyear",
			CONTROL_ATTRIBUTE = "aria-controls",
			AUTO_VALIDATE_WAIT = 250,  // delay after year is changed before the calendar is refreshed
			CLASS = {
				SOUTH: "wc_colsth",
				WEST: "wc_colwest",
				TODAY: "wc_wdf_today",
				DATE_BUTTON: "wc_wdf_pick",
				LAST: "last"
			},
			LAUNCHER = dateField.getLaunchWidget(),
			PICKABLE = new Widget("button", CLASS.DATE_BUTTON),
			ROW,
			CAL_BUTTON = new Widget("button", "wc_wdf_mv"),
			CLOSE_BUTTON = new Widget("button", "wc_wdf_cls"),
			isOpening = false,
			yearChangedTimeout,
			reposTimer,
			refocusId,
			MIN_ATTRIB = "min",
			MAX_ATTRIB = "max",
			conf = module.config(),
			MIN_YEAR = ((conf && conf.min) ? conf.min : 1000),
			MAX_YEAR = ((conf && conf.max) ? conf.max : 9999);


		function findMonthSelect() {
			return document.getElementById(MONTH_SELECT_ID);
		}

		function findYearField() {
			return document.getElementById(YEAR_ELEMENT_ID);
		}

		/*
		 * get the empty calendar sprintf base string
		 */
		function getEmptyCalendar() {
			return loader.load("wc.ui.dateField.calendar.xml", true);
		}

		function resetMonthPickerOptions(disable) {
			var monthSelect = findMonthSelect(),
				i;
			if (monthSelect && monthSelect.options && monthSelect.options.length) {
				for (i = 0; i < monthSelect.options.length; ++i) {
					if (disable) {
						shed.disable(monthSelect.options[i], true);
					}
					else {
						shed.enable(monthSelect.options[i], true);
					}
				}
			}
		}

		function getMinMaxMonthDay(input, isMax, isDay) {
			var constraint,
				xfrObj,
				defaultVal = -1,
				what = (isDay ? "day" : "month"),
				attrib = (isMax ? MAX_ATTRIB : MIN_ATTRIB), result;

			if (isMax) {
				defaultVal = isDay ? 32 : 12;
			}
			if (!(input && ((constraint = input.getAttribute(attrib))))) {
				// nothing to do
				return defaultVal;
			}
			xfrObj = interchange.toValues(constraint);
			result = xfrObj[what];

			return (isDay ? (result * 1) : (result - 1));
		}

		/**
		 * Shift the focus from the current day element to either the next day LEFT, RIGHT, UP or DOWN depending on the
		 * direction the user has indicated
		 * @function
		 * @private
		 * @param {Element} currentElement The element that is currently active (ie the one to which our navigation is relative)
		 * @param {number} direction Left, right, up or down (expressed in "arrow key" codes).
		 */
		function navigateDayLeftRightUpDown(currentElement, direction) {
			// UP    7 days before
			// DOWN  7 days after
			// LEFT  1 day before
			// RIGHT 1 day after
			var days = PICKABLE.findDescendants(document.getElementById(DAY_CONTAINER_ID)),
				i = days.length;
			while (i--) {
				if (days[i] === currentElement) {
					break;
				}
			}
			switch (direction) {
				case KeyEvent.DOM_VK_LEFT:
					i = Math.max(0, i - 1);
					break;
				case KeyEvent.DOM_VK_UP:
					i = Math.max(0, i - 7);
					break;
				case KeyEvent.DOM_VK_RIGHT:
					i = Math.min(days.length - 1, i + 1);
					break;
				case KeyEvent.DOM_VK_DOWN:
					i = Math.min(days.length - 1, i + 7);
					break;
			}
			if (currentElement !== days[i]) {  // eg we are already on the last day and the direction is ARROW_RIGHT
				focus.setFocusRequest(days[i]);
			}
		}

		/**
		 * Gets an integer value from an input element for those UAs which do not correctly implement input type="number".
		 * @function
		 * @private
		 * @param element the input holding the year value
		 * @returns number or NaN if the input is not numeric
		 */
		function getYearValueAsNumber(element) {
			var result = element.value.trim();
			if (result && isNumeric(result)) {
				result = parseInt(result, 10);
			}
			else {
				result = NaN;
			}
			return result;
		}

		/*
		 * check if the month or year was changed, then rebuild calendar
		 */
		function refresh() {
			var yearField = findYearField(),
				input = getInputForCalendar(),
				limit = getLimits(yearField, input),
				year = getYearValueAsNumber(yearField),
				month,
				current,
				newDate;

			// ignore invalid years
			if (!isNaN(year)) {
				current = retrieveDate();

				newDate = setYear(current, year);  // YEAR
				month = setMonth(newDate, year, limit);  // MONTH
				setDay(current, newDate, year, month, limit);  // DAY

				setDate(newDate, false);
			}
		}

		/*
		 * Helper for refresh.
		 * @private
		 * @function
		 */
		function getLimits(yearField, input) {
			var result = {
				yearMin: yearField.getAttribute(MIN_ATTRIB),
				yearMax: yearField.getAttribute(MAX_ATTRIB),
				monthMin: getMinMaxMonthDay(input),
				monthMax: getMinMaxMonthDay(input, true),
				dayMin: getMinMaxMonthDay(input, false, true),
				dayMax: getMinMaxMonthDay(input, true, true)
			};
			return result;
		}

		/*
		 * Helper for refresh.
		 * @private
		 * @function
		 */
		function setYear(date, year) {
			var newDate = copy(date);
			newDate.setDate(1);  // ALWAYS set date to something less than 29 !!BEFORE!! calling setMonth
			newDate.setFullYear(year);
			return newDate;
		}

		/*
		 * Helper for refresh.
		 * @private
		 * @function
		 */
		function setMonth(date, year, limit) {
			var monthSelect = findMonthSelect(),
				month = monthSelect.selectedIndex;
			if (limit.yearMin === year || limit.yearMax === year) {
				if (limit.yearMin === year) {
					if ((limit.monthMin || limit.monthMin === 0) && limit.monthMin > month) {
						date.setMonth(limit.monthMin);
						monthSelect.selectedIndex = limit.monthMin;
					}
					else {
						date.setMonth(month);
					}
				}
				if (limit.yearMax === year) {
					if (limit.monthMax && limit.monthMax < month) {
						date.setMonth(limit.monthMax);
						monthSelect.selectedIndex = limit.monthMax;
					}
					else {
						date.setMonth(month);
					}
				}
			}
			else {
				date.setMonth(month);
			}
			return monthSelect.selectedIndex;
		}

		/*
		 * Helper for refresh.
		 * @private
		 * @function
		 */
		function setDay(current, date, year, month, limit) {
			// check if the date was rolled forward
			// this can happen if we go from, say, 31 march back to feb
			var input, days = current.getDate(),
				daysMax = daysInMonth(date.getFullYear(), date.getMonth() + 1);

			if (limit.yearMin === year || limit.yearMax === year) {
				input = getInputForCalendar();
				if (limit.monthMin === month) {
					if (limit.dayMin > days) {
						days = limit.dayMin;
					}
				}
				else if (limit.monthMax === month) {
					if (limit.dayMax < days) {
						days = limit.dayMax;
					}
				}
			}
			if (days > daysMax) {
				date.setDate(daysMax);
			}
			else {
				date.setDate(days);
			}
		}

		/**
		 * Refresh the displayed month when the year field is changed.
		 * @function
		 * @private
		 * @param {Element} yearElement The input element holding the year.
		 */
		function yearChanged(yearElement) {
			var min = yearElement.getAttribute(MIN_ATTRIB) || MIN_YEAR,
				max = yearElement.getAttribute(MAX_ATTRIB) || MAX_YEAR;
			timers.clearTimeout(yearChangedTimeout);
			yearChangedTimeout = timers.setTimeout(function() {
				var value = getYearValueAsNumber(yearElement);
				if (!isNaN(value) && value >= min && value <= max) {
					yearElement.value = value;
					refresh();
				}
			}, AUTO_VALIDATE_WAIT);
		}

		/**
		 * Hide the calendar.
		 *
		 * @param {Boolean} ignoreFocusReset If true do not attempt to re-focus the calendar icon this is required by
		 * {@link module:wc/ui/calendar~selectDay} which needs to focus the dateField not the calendar icon
		 * in order to bootstrap the field.
		 */
		function hideCalendar(ignoreFocusReset) {
			var cal = getCal(true),
				input;

			// touching = null;
			if (cal && !shed.isHidden(cal)) {

				// focus the dateField if required
				if (!ignoreFocusReset && (input = getInputForCalendar(cal))) {
					refocusId = input.id;
				}
				else {
					refocusId = null;
				}
				cal.removeAttribute("style");  // remove any inline styles
				shed.hide(cal);
			}
		}


		/**
		 * Helper for keydown event listener which handles key presses on year input.
		 *
		 * @function
		 * @private
		 * @param {Element} element The calendar's year input.
		 * @param {int} keyCode The keydown event's keyCode.
		 * @returns {Boolean} true if the event's default action is to be prevented.
		 */
		function keydownHelperChangeYear(element, keyCode) {
			yearChanged(element);
			if (keyCode === KeyEvent.DOM_VK_RETURN) { // do not submit on enter/return in year field
				return true;
			}
			return false;
		}

		/**
		 * Helper for keydown event listener which handles key presses on date pick buttons.
		 *
		 * @function
		 * @private
		 * @param {Element} element the target of the keydown event previously determined as a picker button.
		 * @param {int} keyCode the keydown event's keyCode.
		 * @param {Boolean} shiftKey was the SHIFT key down?
		 * @returns {Boolean} true if the event is to have its default action prevented.
		 */
		function keydownHelperDateButton(element, keyCode, shiftKey) {
			switch (keyCode) {
				case KeyEvent.DOM_VK_LEFT:
				case KeyEvent.DOM_VK_RIGHT:
				case KeyEvent.DOM_VK_UP:
				case KeyEvent.DOM_VK_DOWN:
					navigateDayLeftRightUpDown(element, keyCode);
					return true;
				case KeyEvent.DOM_VK_T:
					setDate(new Date(), true);
					break;
				case KeyEvent.DOM_VK_TAB:
					if (!shiftKey && classList.contains(element, CLASS.LAST)) {  // tabbing fwd past last day
						focus.setFocusRequest(findMonthSelect());  // move focus to first element
						return true;
					}
					break;
			}
			return false;
		}

		/**
		 * Key listeners in the calendar: ESC to close the calendar, ARROW and TAB key walking, SPACE & ENTER key
		 * selection of pickable date, Year change handler is target is the year field.
		 * @function
		 * @private
		 * @param {Event} $event The keydown event.
		 */
		function _calendarKeydownEvent($event) {
			var buttons,
				element = $event.target,
				shiftKey = $event.shiftKey,
				keyCode = $event.keyCode,
				handled = false;

			if ($event.defaultPrevented) {
				return;
			}

			if (keyCode === KeyEvent.DOM_VK_ESCAPE) {
				hideCalendar();
				handled = true;  // if the date field is in a dialog, do not close dialog
			}
			else if (element.id === YEAR_ELEMENT_ID && keyCode !== KeyEvent.DOM_VK_TAB && keyCode !== KeyEvent.DOM_VK_SHIFT) {
				handled = keydownHelperChangeYear(element, keyCode);
			}
			else if (keyCode === KeyEvent.DOM_VK_TAB && shiftKey && element === findMonthSelect()) {  // tabbing back past month select
				buttons = PICKABLE.findDescendants(getCal());
				focus.setFocusRequest(buttons[buttons.length - 1]);  // move focus to last element
				handled = true;
			}
			else if ((element = PICKABLE.findAncestor(element))) {
				handled = keydownHelperDateButton(element, keyCode, shiftKey);
			}

			if (handled) {
				$event.preventDefault();
			}
		}

		/**
		 * changeEvent listener for month select. Updates the displayed month.
		 * @function
		 * @private
		 * @param {Event} $event A change event.
		 */
		function monthChangeEvent($event) {
			if (!$event.defaultPrevented) {
				refresh();
			}
		}

		/**
		 * changeEvent listener for year input. Updates the displayed month in the calendar.
		 * @function
		 * @private
		 * @param {Event} $event A change event.
		 */
		function yearChangeEvent($event) {
			yearChanged($event.target);
		}

		/*
		 * Builds the actual HTML calendar component
		 */
		function create() {
			var days = dayName.get(),
				_today = today.get(),
				dayCellTemplate = "<th><abbr title=\"%s\">%1$.1s</abbr></th>",
				daysOfWeek = [],
				container,
				calendar = getEmptyCalendar(),
				i = 1;

			// build a string containing the html for the days of week row
			do {
				daysOfWeek[daysOfWeek.length] = sprintf.sprintf(dayCellTemplate, days[i %= 7]);
			}
			while (i++);
			daysOfWeek = daysOfWeek.join("");
			// put it all together
			calendar = sprintf.sprintf(calendar,
			/* 1 */(monthName.get()).join("</option><option>"),
			/* 2 */_today.getFullYear(),
			/* 3 */daysOfWeek,
			/* 4 */i18n.get("${wc.ui.dateField.i18n.calendarMonthLabel}"),
			/* 5 */i18n.get("${wc.ui.dateField.i18n.calendarYearLabel}"),
			/* 6 */i18n.get("${wc.ui.dateField.i18n.lastMonth}"),
			/* 7 */i18n.get("${wc.ui.dateField.i18n.today}"),
			/* 8 */i18n.get("${wc.ui.dateField.i18n.nextMonth}"),
			/* 9 */i18n.get("${wc.ui.dateField.i18n.close}"));

			container = document.createElement("div");
			container.id = CONTAINER_ID;
			document.body.appendChild(container);
			container.innerHTML = calendar;
			document.getElementById(MONTH_SELECT_ID).selectedIndex = _today.getMonth();

			event.add(container, event.TYPE.keydown, _calendarKeydownEvent);
			event.add(findMonthSelect(), event.TYPE.change, monthChangeEvent);
			event.add(findYearField(), event.TYPE.change, yearChangeEvent);
			return container;
		}

		/**
		 * Get the calendar's containing element.
		 *
		 * @param {Boolean} doNotCreate If true do not create the calendar if it is not found.
		 * @returns {?Element} The calendar.
		 */
		function getCal(doNotCreate) {
			var rval = document.getElementById(CONTAINER_ID);
			if (!(rval || doNotCreate)) {
				rval = create();
			}
			return rval;
		}

		/*
		 * retrieve a stored date for a picker. If one has not been stored return the current date @returns a date object
		 */
		function retrieveDate() {
			var millis = attribute.get(getCal(), DATE_KEY),
				dateObj;
			if (millis || millis === 0) {
				dateObj = new Date(millis);
			}
			console.log("retrieving date", new Date(millis));
			return dateObj;
		}


		function getInputForCalendar($cal) {
			var cal = $cal || getCal(true),
				inputId,
				result;
			if (cal && (inputId = cal.getAttribute(CONTROL_ATTRIBUTE))) {
				result = document.getElementById(inputId);
			}
			return result;
		}

		/*
		 * when a date picker has been set we store the date so that we can reset it if it is reopened @param dateObj
		 * the date object to store
		 */
		function storeDate(dateObj) {
			var millis;
			if (!dateObj || dateObj.constructor !== Date) {
				throw new TypeError("storeDate expects a date object");
			}
			millis = dateObj.getTime();
			console.log("storing date", new Date(millis));
			attribute.set(getCal(), DATE_KEY, millis);
		}

		/*
		 * attribute.get(cal, DATE_KEY) is how the date is passed back and forth from the date input and the calendar
		 * control. when we set the date we rebuild the calendar to show this date as the default selected @param date
		 * the date object to set the calendar to @param [setFocus] true to focus after setting date @param
		 * [setSelected] true to set the date as the current selection
		 */
		function setDate(date, setFocus, setSelected) {
			var cal = getCal(),
				_date = copy(date),  // do not change date
				_today = new Date(),
				monthIndex = _date.getMonth(),
				year = _date.getFullYear(),
				monthElement = findMonthSelect(),
				yearElement = findYearField(),
				dayOfWeek, endDate, tbody, weeks, monthEnd, inMonth, lastDay, days, day, i, j, text, focusDay, button,
				input, inputDate, minDay, maxDay, minYear, maxYear, minMonth, maxMonth, disableEverything;

			storeDate(_date);
			monthElement.selectedIndex = monthIndex;
			yearElement.value = year;
			// getDay returns 0 = sun 6 = sat, rotate to 0 = mon, 6 = sun
			_date.setDate(1);
			dayOfWeek = _date.getDay();
			if (--dayOfWeek === -1) {
				dayOfWeek = 6;
			}
			// calculate the first and last days of the calendar
			addDays(-1 * dayOfWeek, _date);
			endDate = copy(_date);
			addDays(34, endDate);

			tbody = cal.getElementsByTagName(tag.TBODY)[0];
			weeks = tbody.getElementsByTagName(tag.TR);
			monthEnd = false;
			inMonth = false;

			maxYear = yearElement.getAttribute(MAX_ATTRIB);
			minYear = yearElement.getAttribute(MIN_ATTRIB);

			if (minYear || maxYear) {
				resetMonthPickerOptions();  // make sure all months are enabled
				minYear = minYear * 1;
				maxYear = maxYear * 1;

				input = getInputForCalendar(cal);

				if ((maxYear && year >= maxYear)) {
					if (year > maxYear) {
						disableEverything = true;
					}
					else {
						maxMonth = getMinMaxMonthDay(input, true);

						if (maxMonth || maxMonth === 0) {
							// disable after maxMonth
							for (i = maxMonth + 1; i < monthElement.options.length; ++i) {
								shed.disable(monthElement.options[i], true);
							}
							if (maxMonth === monthIndex) {
								maxDay = getMinMaxMonthDay(input, true, true);
							}
						}
						else {  // should never be here
							resetMonthPickerOptions();
						}
					}
				}

				if ((minYear && year <= minYear)) {
					if (year < minYear) {
						disableEverything = true;
					}
					else {
						minMonth = getMinMaxMonthDay(input);
						if (minMonth || minMonth === 0) {
							// disable before minMonth
							for (i = 0; i < minMonth; ++i) {
								if (!monthElement.options[i]) {
									break;
								}
								shed.disable(monthElement.options[i], true);
							}
							if (minMonth === monthIndex) {
								minDay = getMinMaxMonthDay(input, false, true);
							}
						}
						else {  // should never be here
							resetMonthPickerOptions();
						}
					}
				}

				if (disableEverything) {
					resetMonthPickerOptions(true);
				}
				// if we did not disableEverything because we were in the wrng year we may still have to disable all days if we are in the wrong month
				else if ((minMonth && minMonth > monthIndex) || ((maxMonth || maxMonth === 0) && maxMonth < monthIndex)) {
					disableEverything = true;
				}
			}
			else {
				resetMonthPickerOptions();  // make sure all months are enabled if the date field does not have min/max limit
			}

			// build each week
			for (i = 0; i < weeks.length; i++) {
				if (monthEnd) {
					shed.hide(weeks[i]);
					break;
				}
				else {
					shed.show(weeks[i]);
				}

				days = weeks[i].getElementsByTagName(tag.TD);

				// build each day
				for (j = 0; j < days.length; j++) {
					day = days[j];
					day.innerHTML = "";

					text = _date.getDate();

					// if in current month make the element pickable
					if (monthIndex === _date.getMonth()) {
						inMonth = true;
						button = '<button type="button" class="wc_btn_nada ' + CLASS.DATE_BUTTON + '" value="' + text + '">' + text + '</button>';
						day.innerHTML = button;
						button = day.firstChild;
						lastDay = button;

						if (disableEverything || (minDay && text < minDay) || (maxDay && text > maxDay)) {
							shed.disable(button, true);
						}
						else {
							shed.enable(button, true);
						}

						if (setSelected && (getDifference(_date, date) === 0)) {
							shed.select(button, true);
						}
						else if (!setSelected && (input = getInputForCalendar(cal)) && input.value && (inputDate = dateField.getValue(input)) && (inputDate = interchange.toDate(inputDate)) && getDifference(_date, inputDate) === 0) {
							shed.select(button, true);
						}

						if (getDifference(_date, _today) === 0) {
							classList.add(button, CLASS.TODAY);
						}

						if (setFocus && !focusDay && (getDifference(_date, date) === 0)) {
							focusDay = button;
						}
					}
					else {
						day.appendChild(document.createTextNode(text));
					}
					addDays(1, _date);
					if (inMonth && monthIndex !== _date.getMonth()) {
						monthEnd = true;
					}
				}  // end if days in a week
			}  // end of weeks

			if (lastDay) {
				classList.add(lastDay, CLASS.LAST);
			}
			if (focusDay) {
				focus.setFocusRequest(focusDay);
			}
		}


		function setMinMaxYear(input) {
			var year, max, min, xfrObj;

			if (!(input && ((max = input.getAttribute(MAX_ATTRIB)) || (min = input.getAttribute(MIN_ATTRIB))))) {
				// nothing to do
				return;
			}
			if ((year = document.getElementById(YEAR_ELEMENT_ID))) {
				if (min) {
					xfrObj = interchange.toValues(min);
					if (xfrObj.year) {
						year.setAttribute(MIN_ATTRIB, xfrObj.year);
					}
				}
				else {
					year.setAttribute(MIN_ATTRIB, MIN_YEAR);
				}

				if (max) {
					xfrObj = interchange.toValues(max);
					if (xfrObj.year) {
						year.setAttribute(MAX_ATTRIB, xfrObj.year);
					}
				}
				else {
					year.setAttribute(MAX_ATTRIB, MAX_YEAR);
				}
			}
		}

		/**
		 * Show the calendar.
		 * @function
		 * @private
		 * @param {Element} element The calendar launch button.
		 */
		function show(element) {
			var input = element.value,
				cal,
				date,
				selectDate = false,
				constrained;

			cal = getCal();
			cal.setAttribute(CONTROL_ATTRIBUTE, input);

			// get the date to use as the default. If there is a date in the input we use that,
			// otherwise we default to today.
			input = document.getElementById(input);
			setMinMaxYear(input);

			date = dateField.getValue(input);
			if (date) {
				date = interchange.toDate(date);
				selectDate = true;
			}
			if (!date) {
				date = new Date();
				if ((constrained = input.getAttribute(MIN_ATTRIB)) && (constrained = interchange.toDate(constrained))) {
					if (getDifference(constrained, date, false) > 0) {
						date = constrained;
					}
				}

				if ((constrained = input.getAttribute(MAX_ATTRIB)) && (constrained = interchange.toDate(constrained))) {
					if (getDifference(constrained, date, false) < 0) {
						date = constrained;
					}
				}
			}
			setDate(date, false, selectDate);

			/*
			 * NOTE: element is the calendar launch button. Do NOT insert the calendar into the DOM before this
			 * button as it has focus and a certain well known browser will lose focus from the element if the DOM
			 * is manipulated preceding that element.
			 */
			if (element.nextSibling) {
				element.parentNode.insertBefore(cal, element.nextSibling);
			}
			else {
				element.parentNode.appendChild(cal);
			}
			shed.show(cal);
		}

		/**
		 * Does rudimentary collision detection for the calendar so if the calendar is going to overflow the viewport
		 * adds a class to move it.
		 * @function
		 * @private
		 * @param {Element} cal the calendar.
		 */
		function detectCollision(cal) {
			var collision = viewportCollision(cal),
				initiallyCollideSouth = collision.s > 0,
				initiallyCollideWest = collision.w < 0,
				box, left, top;

			/*
			 * NOTE: default open is below input field and lined up at the right edge of the combo so we do not need to
			 * do north collision. If fixing a west collision causes an east collision we can move the calendar west until
			 * it is either fully in viewport OR touches the left edge of the viewport leaving a horizontal scroll.
			 */
			if (initiallyCollideSouth) {
				top = cal.offsetTop;
				if (!isNaN(top)) {
					cal.style.top = (top - collision.s) + "px";
				}
			}
			if (initiallyCollideWest) {
				classList.add(cal, CLASS.WEST);
				collision = viewportCollision(cal);
				if (collision.e > 0) {
					box = getBox(cal);
					left = Math.min(box.left, collision.e);  // we have to move this far left to move the entire calendar into view
					if (left > 0) {
						cal.style.left = (-1 * left) + "px";
					}
				}
			}
		}

		function changeMonth(element) {
			var monthList,
				yearBox,
				currentYear,
				_today = new Date(),
				numberOfMonths, // should be 12 but who knows when this may change!!
				minYear, maxYear;

			if (element.value === "t") {
				setDate(_today, true);
			}
			else {
				monthList = findMonthSelect();
				numberOfMonths = monthList.options.length;
				yearBox = document.getElementById(YEAR_ELEMENT_ID);
				// If we do not have a year set then default to this year before change
				currentYear = getYearValueAsNumber(yearBox);
				if (isNaN(currentYear)) {
					yearBox.value = currentYear = _today.getFullYear();
				}
				if (element.value === "-1") {
					// go to previous month
					if (monthList.selectedIndex === 0) {
						// change the year first. If we do not have a year set then default to this year then change
						if (!(minYear = yearBox.getAttribute(MIN_ATTRIB)) || minYear < yearBox.value) {
							yearBox.value = currentYear - 1;
							monthList.selectedIndex = numberOfMonths - 1;
						}
					}
					else if ((minYear = yearBox.getAttribute(MIN_ATTRIB)) && minYear === yearBox.value) {
						if (getMinMaxMonthDay(getInputForCalendar()) < monthList.selectedIndex) {
							monthList.selectedIndex = monthList.selectedIndex - 1;
						}
					}
					else {
						monthList.selectedIndex = monthList.selectedIndex - 1;
					}
				}
				else if (monthList.selectedIndex === numberOfMonths - 1) { // go to next month
					// change the year first. If we do not have a year set then default to this year then change
					if (!(maxYear = yearBox.getAttribute(MAX_ATTRIB)) || parseInt(maxYear, 10) > yearBox.value) {
						yearBox.value = currentYear + 1;
						monthList.selectedIndex = 0;
					}
				}
				else  if ((maxYear = yearBox.getAttribute(MAX_ATTRIB)) && maxYear === yearBox.value) { // if we have a max on the year input we have a max date, so we need to get the max month if the current year is equal to the max year
					if (getMinMaxMonthDay(getInputForCalendar(), true) > monthList.selectedIndex) {
						monthList.selectedIndex = monthList.selectedIndex + 1;
					}
				}
				else {
					monthList.selectedIndex = monthList.selectedIndex + 1;
				}
				refresh();
			}
		}

		/**
		 * Actually does the work of activating a calendar launch button. Shows the calendar if it is possible to do so.
		 *
		 * @function
		 * @private
		 * @param {Element} element The launch control button or date input.
		 */
		function doLaunch(element) {
			try {
				if (element && !isOpening && !shed.isDisabled(element)) {
					isOpening = true;
					show(element);
				}
			}
			finally {
				isOpening = false;
			}
		}

		/**
		 * The user has clicked a day in the calendar, set the selected date into the date input.
		 * @function
		 * @private
		 * @param {Element} dayElement The selected day.
		 */
		function selectDay(dayElement) {
			var calendar = getCal(), day, date, sb, newValue, input = getInputForCalendar(calendar);

			if (input && !shed.isDisabled(input)) {
				day = dayElement.value;
				date = retrieveDate(input);
				date.setDate(day);

				sb = [date.getDate(), (date.getMonth() + 1), date.getFullYear()];
				newValue = sb.join(" ");

				input.value = newValue;
				focus.setFocusRequest(input, function(_el) {
					dateField.acceptFirstMatch(_el);
				});
				hideCalendar(true);
			}
			else {
				hideCalendar();  // should never get here!
			}
		}

		function clearMinMaxYear() {
			var year = document.getElementById(YEAR_ELEMENT_ID);
			if (year) {
				year.setAttribute(MIN_ATTRIB, MIN_YEAR);
				year.setAttribute(MAX_ATTRIB, MAX_YEAR);
			}
		}

		/*
		 * Calendar icon click listener.
		 */
		function clickEvent($event) {
			var element;
			if (!$event.defaultPrevented) {
				if ((element = LAUNCHER.findAncestor($event.target))) {
					doLaunch(element);
				}
				else if (getCal(true)) {  // by using getCal(true) we can by-pass a widget descriptor lookup if the calendar has never been opened as document.getElementById is very fast.
					if ((element = PICKABLE.findAncestor($event.target))) {
						selectDay(element);
					}
					else if ((element = CAL_BUTTON.findAncestor($event.target))) {
						changeMonth(element);
					}
					else if ((element = CLOSE_BUTTON.findAncestor($event.target))) {
						hideCalendar();
					}
				}
			}
		}

		function focusEvent($event) {
			var element, calendar;
			if (!$event.defaultPrevented && (element = $event.target)) {
				calendar = getCal(true);
				if (calendar && ((element === window || element === document) || !(calendar.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINED_BY))) {
					hideCalendar(true);
				}
			}
		}

		function keydownEvent($event) {
			var target = $event.target,
				keyCode = $event.keyCode,
				launcher;
			if (keyCode === KeyEvent.DOM_VK_DOWN && ($event.altKey || $event.metaKey) && dateField.isOneOfMe(target, false) && (launcher = LAUNCHER.findDescendant(target.parentNode))) {
				doLaunch(launcher);
			}
		}

		/*
		 * strip aria- attributes on hide
		 */
		function shedSubscriber(element, action) {
			var cal, input;
			if (element.id === CONTAINER_ID) {
				if (action === shed.actions.HIDE) {
					element.removeAttribute(CONTROL_ATTRIBUTE);
					clearMinMaxYear();
					classList.remove(element, CLASS.SOUTH);
					classList.remove(element, CLASS.WEST);
					element.style.left = "";
					// touching = null;
					if (refocusId) {
						if ((input = document.getElementById(refocusId)) && focus.canFocus(input)) {
							focus.setFocusRequest(input);
						}
						refocusId = null;
					}
				}
				else if (action === shed.actions.SHOW) {
					position(element);
					focus.focusFirstTabstop(element);
				}
			}
			else if (action === shed.actions.HIDE && ((cal = getCal(true)) && !!(element.compareDocumentPosition(cal) & Node.DOCUMENT_POSITION_CONTAINS))) {  // if we are hiding something inside the calendar it is probably a row
				ROW = ROW || new Widget("tr");
				if (ROW.isOneOfMe(element)) {
					// we have to remove the pickable elements from any dates which are no longer in the visible calendar
					Array.prototype.forEach.call(PICKABLE.findDescendants(element), function(next) {
						next.parentNode.removeChild(next);
					});
				}
			}
		}

		/**
		 * If something causes (or may cause) the calendar to need repositioning the easiest thing to do is simple close it.
		 * The use can reopen it when they are done messing with the viewport.
		 */
		function reposEvent() {
			if (reposTimer) {
				timers.clearTimeout(reposTimer);
			}
			if (!isOpening) {
				reposTimer = timers.setTimeout(hideCalendar, 100);
			}
		}

		/**
		 * Positions the calendar relative to its input element.
		 * @param {Element} [element] The calendar element (if you already have it, otherwise we'll find it for you).
		 */
		function position(element) {
			var input, box, cal = element || getCal(true), fixed;
			if (cal && !shed.isHidden(cal)) {
				fixed = (window.getComputedStyle && window.getComputedStyle(cal).position === "fixed");
				if (fixed) {
					input = getInputForCalendar(cal);
					if (input) {
						box = getBox(input);
						cal.style.top = box.bottom + "px";
					}
				}
				detectCollision(cal);
			}
		}

		/**
		 * Event wire up on initialise.
		 *
		 * @function module:wc/ui/calendar.initialise
		 * @public
		 * @param {Element} element The element being initialised, usually document.body.
		 */
		this.initialise = function(element) {
			if (event.canCapture) {
				event.add(window, event.TYPE.focus, focusEvent, null, null, true);
			}
			else {
				event.add(element, event.TYPE.focusin, focusEvent);
			}
			event.add(element, event.TYPE.click, clickEvent);
			event.add(element, event.TYPE.keydown, keydownEvent);
		};

		/**
		 * Late initialisation to set up the {@link module:wc/dom/shed} subscribers.
		 * @function module:wc/ui/calendar.postInit
		 * @public
		 */
		this.postInit = function() {
			event.add(window, event.TYPE.resize, reposEvent);
			// event.add(window, event.TYPE.scroll, reposEvent);  // this is bad if opening the calendar causes the page to scroll
			shed.subscribe(shed.actions.SHOW, shedSubscriber);
			shed.subscribe(shed.actions.HIDE, shedSubscriber);
		};

		/**
		 * Public for testing.
		 *
		 * @function module:wc/ui/calendar._keydownEventHandler
		 * @public
		 * @ignore
		 */
		this._keydownEventHandler = _calendarKeydownEvent;
	}

	var /** @alias module:wc/ui/calendar */ instance = new Calendar();
	initialise.register(instance);
	return instance;
});
