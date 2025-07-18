import React from "react";

export {
	useOptimizedState,
	useAsyncState,
	useOptimizedList,
	useOptimizedForm,
	useOptimizedDebounce,
} from "./useOptimizedState";

export {
	startTransition,
	useTransition,
	useDeferredValue,
	useId,
	useSyncExternalStore,
} from "react";

export type TransitionFunction = () => void;
export type DeferredValue<T> = T;

export const withTransition = (fn: () => void): void => {
	React.startTransition(fn);
};

export const createOptimizedCallback = <
	T extends (...args: unknown[]) => unknown,
>(
	callback: T,
	deps: React.DependencyList,
): T => {
	return React.useCallback(
		(...args: Parameters<T>) => {
			React.startTransition(() => {
				callback(...args);
			});
		},
		[...deps, callback],
	) as T;
};

export const createDeferredMemo = <T>(
	factory: () => T,
	deps: React.DependencyList,
): T => {
	const value = React.useMemo(factory, [...deps]);
	return React.useDeferredValue(value);
};

export const REACT_19_FEATURES = {
	CONCURRENT_FEATURES: true,
	START_TRANSITION: true,
	USE_DEFERRED_VALUE: true,
	USE_TRANSITION: true,
	AUTOMATIC_BATCHING: true,
	STRICT_MODE_IMPROVEMENTS: true,
} as const;

export const isReact19Available = (): boolean => {
	try {
		return (
			typeof React.startTransition === "function" &&
			typeof React.useTransition === "function" &&
			typeof React.useDeferredValue === "function"
		);
	} catch {
		return false;
	}
};

export const measurePerformance = (name: string, fn: () => void): void => {
	if (process.env.NODE_ENV === "development") {
		const start = performance.now();
		fn();
		const end = performance.now();
		console.log(`⚡ ${name} took ${end - start} milliseconds`);
	} else {
		fn();
	}
};

export const measureAsyncPerformance = async (
	name: string,
	fn: () => Promise<void>,
): Promise<void> => {
	if (process.env.NODE_ENV === "development") {
		const start = performance.now();
		await fn();
		const end = performance.now();
		console.log(`⚡ ${name} took ${end - start} milliseconds`);
	} else {
		await fn();
	}
};
