import {useMutation, useQuery} from '@tanstack/react-query';
import axios from 'axios';

// Custom hook to fetch API data with TanStack Query
export const useApiData = (endpoints, params = {}) => {
    const queryParams = new URLSearchParams({ ...params }).toString();
    const queryString = queryParams ? `?${queryParams}` : '';
    const url = `${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}${queryString}`;

    return useQuery({
        queryKey: ['apiData', endpoints, params], // Unique key for caching
        queryFn: () => axios.get(url, {
            withCredentials: true,
        }),
        retry: 2,
        staleTime: 60000,
        onError: (error) => {
            console.error(`API request failed for ${endpoints}:`, error);
        }
    });
};

export const useApiMutation = (endpoints, options = {}) => {
    return useMutation({
        mutationFn: (data) => {
            let params = data
            if (typeof data === 'object') {
                params = new URLSearchParams();
                for (const key in data) {
                    if (data.hasOwnProperty(key)) {
                        params.append(key, data[key]);
                    }
                }
            }

            return axios.get(`${import.meta.env.PUBLIC_API_BASE_URL}/${endpoints}?${params ? params : ''}`, {
                withCredentials: true,
            })
        },
        ...options
    });
}