import { createContext } from "react";

export const RequestContext = createContext<(triggerId: string, triggerValue: string, ajax?: boolean) => void>(
	() => {},
);

export const TabSetContext = createContext<(tabSetId: string, tabId: string, tabIndex: number) => void>(() => {});
