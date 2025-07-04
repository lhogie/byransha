import { useMutation, useQuery } from "@tanstack/react-query";
import axios from "axios";

// Custom hook to fetch API data with TanStack Query
export const useApiData = (
	endpoints: string,
	params: any = {},
	querySettings?: any,
) => {
	const queryParams = new URLSearchParams({ ...params }).toString();
	const queryString = queryParams ? `?${queryParams}` : "";
	const url = `${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}${queryString}`;

	return useQuery<{
		data: {
			"backend version": string;
			uptimeMs: string;
			username: string;
			user_id: number;
			node_id: number;
			durationNs: string;
			results: {
				endpoint: string;
				endpoint_class: string;
				response_type: string;
				pretty_name: string;
				what_is_this: string;
				durationNs: string;
				result: {
					contentType: string;
					dialect: string;
					data: any;
				};
				error: string;
			}[];
		};
	}>({
		initialData: undefined,
		queryKey: ["apiData", endpoints, params], // Unique key for caching
		queryFn: () =>
			axios.get(url, {
				withCredentials: true,
			}),
		retry: 2,
		staleTime: 60000,
		...querySettings,
	});
};

export const useApiMutation = (endpoints: string, options: any = {}) => {
	return useMutation<any, any, any, any>({
		mutationFn: (data: any) => {
			return axios.post(
				`${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}`,
				data,
				{
					withCredentials: true,
				},
			);
		},
		...options,
	});
};
