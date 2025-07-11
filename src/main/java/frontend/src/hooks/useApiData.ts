import { useInfiniteQuery, useMutation, useQuery } from "@tanstack/react-query";
import { Decoder } from "cbor-x/decode";
import axios from "axios";

const decoder = new Decoder({
	mapsAsObjects: true,
});

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
			axios
				.get(url, {
					withCredentials: true,
					headers: {
						Accept: "application/cbor",
					},
					responseType: "arraybuffer",
				})
				.then((res) => ({
					...res,
					data: decoder.decode(new Uint8Array(res.data)),
				})),
		retry: 2,
		staleTime: 60000,
		...querySettings,
	});
};

export const useInfiniteApiData = (
	endpoints: string,
	params: any = {},
	querySettings?: any,
) => {
	const queryParams = new URLSearchParams({ ...params }).toString();
	const queryString = queryParams ? `?${queryParams}` : "";
	const getUrl = (page: number) =>
		`${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}${queryString}&page=${page}&pageSize=10`;

	return useInfiniteQuery<{
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
					data: {
						data: any;
						page: number;
						pageSize: number;
						total: number;
						hasNext: boolean;
					};
				};
				error: string;
			}[];
		};
	}>({
		initialData: undefined,
		queryKey: ["apiData", endpoints, params], // Unique key for caching
		queryFn: ({ pageParam }: { pageParam: number }) =>
			axios
				.get(getUrl(pageParam), {
					withCredentials: true,
					headers: {
						Accept: "application/cbor",
					},
					responseType: "arraybuffer",
				})
				.then((res) => ({
					...res,
					data: decoder.decode(new Uint8Array(res.data)),
				})),
		getNextPageParam: (lastPage, _allPages, _lastPageParam, _allPageParams) =>
			lastPage.data.results[0].result.data.hasNext
				? lastPage.data.results[0].result.data.page + 1
				: undefined,
		initialPageParam: 1,
		retry: 2,
		staleTime: 60000,
		...querySettings,
	});
};

export const useApiMutation = (endpoints: string, options: any = {}) => {
	return useMutation<any, any, any, any>({
		mutationFn: (data: any) => {
			return axios
				.post(`${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}`, data, {
					withCredentials: true,
					headers: {
						Accept: "application/cbor",
					},
					responseType: "arraybuffer",
				})
				.then((res) => {
					return {
						...res,
						data: decoder.decode(new Uint8Array(res.data)),
					};
				});
		},
		...options,
	});
};
