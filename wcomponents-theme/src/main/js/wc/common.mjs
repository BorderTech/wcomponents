/*
 * This is the one true meta module. It only makes sense to have one of these.
 *
 * This module exists solely for the purposes of optimization. Optimization here means reducing the
 * number of HTTP requests at runtime.
 *
 * You can override this module in your implementation and set whatever modules you wish to be included in the resulting
 * wc/common module. You would choose the modules most commonly loaded in your web application. Generally we would only
 * ever expect to see ui widgets listed here, but you can list whatever floats your boat, EXCEPT anything loaded in
 * compat or fixes.js - they're polyfills for ES5, DOM methods etc.
 *
 * The layer **must** include "wc/i18n/i18n" and "wc/a8n", otherwise things will go terribly wrong (well, things will go wrong
 * in testing if you don't include "wc/a8n").
 *
 * During the build the dependencies and sub-dependencies of this module will be calculated and packaged into
 * this file meaning you get a lot of modules for one single HTTP request.
 *
 * @ignore
 */

import fixes from "wc/fixes.mjs"; // you REALLY need this ...
import "wc/i18n/i18n.mjs"; // ALWAYS REQUIRED IN THIS LAYER
import "wc/a8n.mjs"; // ALWAYS REQUIRED IN THIS LAYER
import "wc/ui/backToTop.mjs";
import "wc/ui/field.mjs";
import "wc/ui/label.mjs";
import "wc/ui/tabset.mjs";
import "wc/ui/menu.mjs";
import "wc/dom/messageBox.mjs";
import "wc/ui/validation/all.mjs";
import "wc/ui/subordinate.mjs";

Promise.all(fixes.map(fix => import(fix)));
