import { createContext } from "react";

export const AjaxContext = createContext<(triggerId: string, targetId: string) => void>(() => {});
