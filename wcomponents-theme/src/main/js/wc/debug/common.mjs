import hotReloadClient from "wc/debug/hotReloadClient.mjs";
import "wc/debug/consoleColor.mjs";
import "wc/debug/indicator.mjs";
import "wc/debug/i18n.mjs";
import "wc/debug/label.mjs";
import "wc/debug/heading.mjs";

console.log("Loaded debug modules");
hotReloadClient.getConnection();
