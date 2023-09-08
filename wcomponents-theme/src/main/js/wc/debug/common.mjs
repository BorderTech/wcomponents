import hotReloadClient from "wc/debug/hotReloadClient";
import "wc/debug/consoleColor";
import "wc/debug/indicator";
import "wc/debug/i18n";
import "wc/debug/label";
import "wc/debug/heading";

console.log("Loaded debug modules");
hotReloadClient.getConnection();
