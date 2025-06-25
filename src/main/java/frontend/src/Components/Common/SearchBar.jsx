import {Autocomplete, CircularProgress, TextField, Typography} from "@mui/material";
import {useApiData, useApiMutation} from "../../Hooks/useApiData";
import {useState} from "react";
import {useQueryClient} from "@tanstack/react-query";
import {useNavigate} from "react-router";
import {useDebounce} from "use-debounce";


export const SearchBar = () => {
    const [query, setQuery] = useState("")
    const [debounceQuery] = useDebounce(query, 250,
        { maxWait: 500 });
    const navigate = useNavigate()
    const queryClient = useQueryClient();

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries()
        },
    });
    const { isLoading, data } = useApiData('search_node', {
		query: debounceQuery
	}, {
		enabled: query.length > 0,
	})

    const results = data?.data?.results?.[0]?.result?.data || [];

	return  <Autocomplete
		sx={{ width: 300 }}
		isOptionEqualToValue={(option, value) => option.title === value.title}
		getOptionLabel={(option) => option.name}
		options={results}
		loading={isLoading}
        onChange={(event, value) => {
            if (value) {
                jumpMutation.mutate({
                    node_id: value.id,
                });
                navigate('/home')
            }
        }}
		renderInput={(params) => (
			<TextField
				{...params}
				label="Rechercher"
				variant="outlined"
				size="small"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
				slotProps={{
					input: {
						...params.InputProps,
						endAdornment: (
							<>
								{isLoading ? <CircularProgress color="inherit" size={20} /> : null}
								{params.InputProps.endAdornment}
							</>
						),
						sx: { pr: 0.5 },
					},
				}}
				sx={{ display: { xs: 'none', md: 'inline-block' }, mr: 1 }}
			/>
		)}
	/>
}