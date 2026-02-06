import React from "react";

export const useLoadingState = (initialState = false) => {
	const [isLoading, setIsLoading] = React.useState(initialState);

	const startLoading = React.useCallback(() => {
		React.startTransition(() => {
			setIsLoading(true);
		});
	}, []);

	const stopLoading = React.useCallback(() => {
		React.startTransition(() => {
			setIsLoading(false);
		});
	}, []);

	const withLoading = React.useCallback(
		async <T,>(asyncFn: () => Promise<T>): Promise<T> => {
			startLoading();
			try {
				const result = await asyncFn();
				return result;
			} finally {
				stopLoading();
			}
		},
		[startLoading, stopLoading],
	);

	return {
		isLoading,
		startLoading,
		stopLoading,
		withLoading,
	};
};
