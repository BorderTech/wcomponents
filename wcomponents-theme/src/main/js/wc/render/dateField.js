define(["wc/render/utils",
	"wc/has",
	"wc/i18n/i18n",
	"wc/dom/shed",
	"wc/dom/event",
	"wc/dom/dateFieldUtils",
	"wc/dom/fieldIndicatorUtils",
	"wc/date/Format",
	"wc/mixin",
	"wc/debounce"],
	function(renderUtils, has, i18n, shed, eventMgr, dfUtils, fieldIndicatorUtils, Format, mixin, debounce) {

		var checkEnableSwitcherEvent = debounce(function($event) {
				checkEnableSwitcher($event.target);
			}, 330),
			widgets = dfUtils.getWidgets();

		function renderAsync(element) {
			var messageKeys = ["datefield_title_default", "datefield_partial_switcher_label"],
				bundle = {};
			return i18n.translate(messageKeys).then(function(messages) {
				var i;
				for (i = 0; i < messageKeys.length; i++) {
					bundle[messageKeys[i]] = messages[i];
				}
				return renderDateField(element, bundle);
			});
		}

		function getId(element) {
			return element.id || element.getAttribute("data-wc-id");
		}

		/**
		 * Imports all field indicators into a target array.
		 * @param {Element} element  An element potentially containing field inidcators.
		 * @param {Element[]} target An array to which the field indicators will be imported.
		 * @return {Element[]} An array of field indicator elements.
		 */
		function gatherFieldIndicators(element, target) {
			// TODO how will this work with client side validation messages?
			var result= target || [],
				fiWidgets = fieldIndicatorUtils.getWidgets(),
				container = fiWidgets.FIELDINDICATOR.findDescendant(element);
			if (container) {
				renderUtils.importKids(container, result);
				container.parentNode.removeChild(container);
			}
			return result;
		}

		function renderDateField(element, i18nBundle) {
			var parent = element.parentNode,
				createContainerFunc = createContainer,
				allowPartial = element.getAttribute("data-wc-allowpartial"),
				elements, hasPartialDate = dfUtils.hasPartialDate(element);
			if (!has("native-dateinput") || allowPartial === "true" || (allowPartial !== null && hasPartialDate)) {
				elements = [createFakeDateInput(element, i18nBundle)];
				elements.push(renderDatePickerLauncher(element));
				elements.push(createListBox());
				createContainerFunc = createCustomContainer;
			} else {
				elements = [createDateInput(element)];
			}
			if (allowPartial !== null && has("native-dateinput")) {
				elements.push(createPartialSwitcher(element, i18nBundle, hasPartialDate));
			}
			gatherFieldIndicators(element, elements);
			elements = createContainerFunc(element, elements, allowPartial);
			if (parent) {
				element.parentNode.replaceChild(elements, element);
			}
			return elements;
		}

		function createPartialSwitcher(element, i18nBundle, disabled) {
			var switcher,
				dateFieldId = getId(element),
				switcherId = dateFieldId + "_partial",
				allowPartial = element.getAttribute("data-wc-allowpartial"),
				config = {
					attrs: {
						name: switcherId,
						type: "checkbox",
						value: "true",
						"aria-controls": dateFieldId
					},
					onChange: changeEvent
				};
			/*
			 * The mere existence of @allowPartial indicates that we are dealing with a partial date field.
			 * The value is irrelevant, it really has three meaningful states:
			 * "true" - allow partial dates and user requested partial
			 * "false" - allow partial dates but user has not requested partialx
			 * null - does not allow partial dates
			 */
			switcher = renderUtils.createElement("input", config);
			if (allowPartial === "true") {
				shed.select(switcher, true);
			}
			if (disabled || isDisabled(element)) {
				shed.disable(switcher, true);
			}
			switcher = renderUtils.createElement("label", {}, [switcher, i18nBundle["datefield_partial_switcher_label"]]);
			switcher = renderUtils.createElement("div", {}, [switcher]);
			return switcher;
		}

		function renderDatePickerLauncher(element) {
			var launcherWidget = widgets.LAUNCHER.extend("", {
					"aria-hidden": "true",
					tabindex: "-1",
					value: getId(element) + "_input"
				}),
				result = launcherWidget.render();
			renderUtils.appendKids(result, [widgets.LAUNCHER_ICON.render()]);

			if (isDisabled(element)) {
				shed.disable(result, true);
			}

			return result;
		}

		function createContainer(element, children, allowPartial) {
			var container,
				attrs = {
					id: getId(element)
				};

			if (allowPartial !== null) {
				attrs["data-wc-allowpartial"] = allowPartial;
			}
			renderUtils.extractAttributes(element, { "data-wc-class": "className" }, attrs);
			container = widgets.DATE_FIELD.render({ state: attrs });
			renderUtils.appendKids(container, children);
			return container;
		}

		function createCustomContainer(element, children, allowPartial) {
			var widget,
				container,
				attrs = {
					id: getId(element),
					"aria-expanded": "false"
				};
			if (allowPartial !== null) {
				widget = widgets.DATE_WRAPPER_PARTIAL;
				attrs["data-wc-allowpartial"] = allowPartial;
			} else {
				widget = widgets.DATE_WRAPPER_FAKE;
			}
			renderUtils.extractAttributes(element, { "data-wc-class": "className" }, attrs);
			container = widget.render({ state: attrs });
			renderUtils.appendKids(container, children);
			return container;
		}

		function createListBox() {
			return widgets.SUGGESTION_LIST.render();
		}

		function createDateInput(element) {
			var map = {
					"data-wc-tooltip": "title",
					"data-wc-required": "required",
					"data-wc-disabled": "disabled",
					"data-wc-accessibletext": "aria-label",
					"data-wc-buttonid": "data-wc-submit",
					"data-wc-placeholder": "placeholder",
					"data-wc-submitonchange": "data-wc-submitonchange",
					"data-wc-autocomplete": "autocomplete",
					"data-wc-min": "min",
					"data-wc-max": "max"
				},
				input, fieldId = getId(element),
				dateVal = dfUtils.getValue(element),
				attrs = {},
				config = { attrs: attrs };

			mapInputAttributes(element, attrs, map);
			if (attrs["data-wc-submitonchange"]) {
				attrs.className = "wc_soc";
				delete attrs["data-wc-submitonchange"];
			}

			mixin({
				value: dateVal.xfr || dateVal.raw,
				id: fieldId + "_input",
				name: fieldId,
				type: "date" },
			attrs);


			if (attrs.value && !dateVal.complete) {
				// This field only validly accepts ISO8601 dates
				attrs["aria-invalid"] = "true";
			}

			input = renderUtils.createElement("input", config);

			return input;
		}

		function createFakeDateInput(element, i18nBundle) {
			var map = {
					"data-wc-tooltip": "title",
					"data-wc-required": "required",
					"data-wc-disabled": "disabled",
					"data-wc-accessibletext": "aria-label",
					"data-wc-buttonid": "data-wc-submit",
					"data-wc-placeholder": "placeholder",
					"data-wc-submitonchange": "data-wc-submitonchange"
				},
				widget = widgets.DATE_FAKE,
				input,
				formatter = Format.getDefaultFormatter(),
				rawValue = dfUtils.getRawValue(element),
				formattedValue = formatter.format(rawValue),
				fieldId = getId(element),
				attrs = {
					title: i18nBundle["datefield_title_default"]
				};

			mapInputAttributes(element, attrs, map);
			if (attrs["data-wc-submitonchange"]) {
				attrs.className = "wc_soc";
				delete attrs["data-wc-submitonchange"];
			}

			mixin({
				value: formattedValue || rawValue,
				id: fieldId + "_input",
				name: fieldId,
				type: "text",
				autocomplete: "off" },
			attrs);

			input = widget.render({ state: attrs });
			eventMgr.add(input, "change", checkEnableSwitcherEvent);
			eventMgr.add(input, "keydown", checkEnableSwitcherEvent);


			return input;
		}

		function changeEvent($event) {
			var switcher = $event.target,
				containerId = switcher.getAttribute("aria-controls"),
				dateField = document.getElementById(containerId);
			if (dateField) {
				dateField.setAttribute("data-wc-allowpartial", shed.isSelected(switcher));
				renderAsync(dateField);
			}
		}

		function mapInputAttributes(source, target, map) {
			var attrs = Object.keys(map),
				asElement = target.nodeType === Node.ELEMENT_NODE;
			attrs.forEach(function(attrName) {
				var name = map[attrName];
				if (name && source.hasAttribute(attrName)) {
					if (asElement) {
						target.setAttribute(name, source.getAttribute(attrName));
					} else {
						target[name] = source.getAttribute(attrName);
					}
				}
			});
			return target;
		}

		/*
		 * don't call this directly, call checkEnableSwitcherEvent instead
		 */
		function checkEnableSwitcher(element) {
			var dateField = dfUtils.get(element),
				switcher = widgets.SWITCHER.findDescendant(dateField),
				hasPartial;
			if (switcher) {
				if (shed.isSelected(switcher)) {
					hasPartial = dfUtils.hasPartialDate(element);
					// Currently partial dates are accepted, disable switcher if date contains partial date
					if (hasPartial) {
						shed.disable(switcher, true);
					} else {
						shed.enable(switcher, true);
					}
				} else {
					shed.enable(switcher, true);
				}
			}
		}

		function isDisabled(element) {
			return element.hasAttribute("data-wc-disabled") || shed.isDisabled(element);
		}

		return {
			render: renderAsync
		};
	});
