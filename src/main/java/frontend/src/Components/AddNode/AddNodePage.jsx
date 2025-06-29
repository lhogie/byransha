import React, {useEffect, useState, useCallback} from 'react';
import {useTitle} from "../../global/useTitle";
import {
    Box,
    Card,
    CardContent,
    Container,
    Fade,
    Grid,
    IconButton,
    InputAdornment,
    Paper,
    TextField,
    Typography
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import {useNavigate} from "react-router";
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import ReloadIcon from '@mui/icons-material/Refresh';
import SearchIcon from '@mui/icons-material/Search';
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import {useQueryClient} from "@tanstack/react-query";


const AddNodePage = () => {
    useTitle(`Add node`);

    const navigate = useNavigate();
    const {data: rawApiData, isLoading: loading, error, refetch} = useApiData('bnode_class_distribution');
    const queryClient = useQueryClient();


    const [className, setClassName] = useState([]);
    const [fullClassName, setFullClassName] = useState([]);
    const [persistingClasses, setPersistingClasses] = useState(new Set());
    const [searchTerm, setSearchTerm] = useState("");
    const [exitAnim, setExitAnim] = useState(false);
    const [favorites, setFavorites] = useState(() => {
        try {
            const stored = localStorage.getItem('favorites');
            const parsed = JSON.parse(stored);
            return Array.isArray(parsed) ? parsed : [];
        } catch {
            return [];
        }
    });

    const handleClose = () => {
        setExitAnim(true);
        setTimeout(() => navigate("/home"), 300);
    };

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries();
        },
    });


    const handleCreateAndJump = async (name) => {
        const fullName = fullClassName.find(item => item.endsWith(name));
        try {
            const response = await fetch(`https://localhost:8080/api/add_node?BNodeClass=${encodeURIComponent(fullName)}`, {
                credentials: 'include',
                headers: {
                    Accept: "application/json, text/plain, */*"
                }
            });
            const result = await response.json();
            const data = result.results?.[0]?.result?.data.id;

            await jumpMutation.mutateAsync({ node_id: data });

        } catch (err) {
            console.error(`Error during handleCreateAndJump for ${fullName}:`, err);
            throw err;
        }
    };


    const handleClickClass = async (name) => {
        try {
            await handleCreateAndJump(name);
            const fullName = fullClassName.find(item => item.endsWith(name));
            navigate(`/add-node/form/${fullName}`);
        } catch (err) {
            console.error("Navigation skipped due to error:", err);
        }
    };



    const toggleFavorite = (name) => {
        setFavorites((prev) =>
            prev.includes(name) ? prev.filter(n => n !== name) : [...prev, name]
        );
    };

    useEffect(() => {
        localStorage.setItem('favorites', JSON.stringify(favorites));
    }, [favorites]);

    const stringifyData = useCallback((data, indent = 2) => {
        if (!data) return "";
        return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
    }, []);

    useEffect(() => {
        if (!rawApiData) return;
        try {
            const classList = rawApiData?.data?.results?.[0]?.result?.data || [];

            const filteredList = classList.filter(item => {
                const fullName = Object.keys(item)[0];
                return fullName;
            });

            const shortName = filteredList.map(item => {
                const fullName = Object.keys(item)[0];
                return fullName.split('.').pop();
            });

            const fullName = filteredList.map(item => {
                return Object.keys(item)[0];
            });

            setClassName(shortName);
            setFullClassName(fullName);
        } catch (err) {
            console.error("Failed to parse class names:", err);
        }
    }, [rawApiData]);

    const fetchClassInfo = async (fullName) => {
        const cacheKey = `persisting:${fullName}`;
        const cached = localStorage.getItem(cacheKey);
        if (cached !== null) {
            return cached === 'true';
        }

        try {
            const response = await fetch(`https://localhost:8080/api/class_information?classForm=${encodeURIComponent(fullName)}`, {
                credentials: 'include',
                headers: {
                    Accept: "application/json, text/plain, */*"
                }
            }); // Adjust the URL to add the server url when deploy.
            const result = await response.json();
            const data = result?.results?.[0]?.result?.data;
            const isBusiness = data?.BusinessNode !== undefined ? data.BusinessNode : false;

            //localStorage.setItem(cacheKey, isPersisting ? 'true' : 'false');
            return isBusiness;
        } catch (err) {
            console.error(`Error fetching info for ${fullName}:`, err);
            return false;
        }
    };


    useEffect(() => {
        if (!fullClassName || fullClassName.length === 0) return;

        const checkPersistingNodes = async () => {
            const persistingSet = new Set();

            await Promise.all(
                fullClassName.map(async (name) => {
                    const hasPersisting = await fetchClassInfo(name);
                    if (hasPersisting) {
                        persistingSet.add(name);
                    }
                })
            );

            setPersistingClasses(persistingSet);
        };

        checkPersistingNodes();
    }, [fullClassName]);

    return (
        <>
            <Fade in={!exitAnim} timeout={300}>
                <Container component={Paper} elevation={3} sx={{
                    p: 4,
                    position: 'relative',
                    bgcolor: '#f8f9fa',
                    maxWidth: '100%',
                    width: '100%',
                    minHeight: '80vh',
                    overflow: 'hidden'
                }}>
                    <Typography
                        variant="h3"
                        component="h1"
                        gutterBottom
                        sx={{
                            color: '#2c3e50',
                            textAlign: 'center',
                            fontWeight: 600,
                            pb: 2,
                            borderBottom: '3px solid #3498db'
                        }}
                    >
                        Add a new node
                    </Typography>

                    <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3, width: '100%' }}>
                        <TextField
                            fullWidth
                            variant="outlined"
                            placeholder="Search class name..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            sx={{ maxWidth: 400 }}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                ),
                            }}
                        />
                    </Box>

                    {favorites.length > 0 && (
                        <>
                            <Typography
                                variant="h4"
                                component="h2"
                                sx={{
                                    color: '#34495e',
                                    my: 3,
                                    fontWeight: 500
                                }}
                            >
                                Favorites (Persistent Only)
                            </Typography>
                            <Grid container spacing={2} sx={{ mt: 2, justifyContent: 'center' }}>
                                {favorites
                                    .map(name => {
                                        const fullName = fullClassName.find(f => f.endsWith(name));
                                        return { short: name, full: fullName };
                                    })
                                    .filter(({ full }) => full && persistingClasses.has(full))
                                    .filter(({ short }) => short.toLowerCase().includes(searchTerm.toLowerCase()))
                                    .map(({ short }) => (
                                        <Grid item key={short}>
                                            <Card
                                                onClick={() => handleClickClass(short)}
                                                sx={{
                                                    minWidth: 120,
                                                    cursor: 'pointer',
                                                    transition: 'all 0.2s ease',
                                                    '&:hover': {
                                                        bgcolor: '#f0f8ff',
                                                        transform: 'translateY(-2px)',
                                                        boxShadow: 3
                                                    }
                                                }}
                                            >
                                                <CardContent sx={{
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'flex-start',
                                                    p: 2,
                                                    '&:last-child': { pb: 2 }
                                                }}>
                                                    <Box
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            toggleFavorite(short);
                                                        }}
                                                        sx={{ mr: 1, display: 'inline-flex', cursor: 'pointer' }}
                                                    >
                                                        <StarIcon sx={{ color: '#f1c40f' }} />
                                                    </Box>
                                                    <Box>
                                                        {short}
                                                    </Box>
                                                </CardContent>
                                            </Card>
                                        </Grid>
                                    ))}
                            </Grid>
                        </>
                    )}

                    <Typography
                        variant="h4"
                        component="h2"
                        sx={{
                            color: '#34495e',
                            my: 3,
                            fontWeight: 500
                        }}
                    >
                        All persisting classes
                    </Typography>
                    <Grid container spacing={2} sx={{ mt: 2, justifyContent: 'center' }}>
                        {className
                            .map((name, index) => ({
                                short: name,
                                full: fullClassName[index]
                            }))
                            .filter(({ full }) => persistingClasses.has(full))
                            .filter(({ short }) => short.toLowerCase().includes(searchTerm.toLowerCase()))
                            .map(({ short }) => (
                                <Grid item key={short}>
                                    <Card
                                        sx={{
                                            minWidth: 120,
                                            cursor: 'pointer',
                                            transition: 'all 0.2s ease',
                                            '&:hover': {
                                                bgcolor: '#f0f8ff',
                                                transform: 'translateY(-2px)',
                                                boxShadow: 3
                                            }
                                        }}
                                    >
                                        <CardContent
                                            onClick={() => handleClickClass(short)}
                                            sx={{
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'flex-start',
                                                p: 2,
                                                '&:last-child': { pb: 2 }
                                            }}
                                        >
                                            <Box
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    toggleFavorite(short);
                                                }}
                                                sx={{ mr: 1, display: 'inline-flex', cursor: 'pointer' }}
                                            >
                                                {favorites.includes(short) ? (
                                                    <StarIcon sx={{ color: '#f1c40f' }} />
                                                ) : (
                                                    <StarBorderIcon sx={{ color: '#ccc' }} />
                                                )}
                                            </Box>
                                            <Box>
                                                {short}
                                            </Box>
                                        </CardContent>
                                    </Card>
                                </Grid>
                            ))}
                    </Grid>

                    <Box sx={{
                        position: 'absolute',
                        top: 10,
                        right: 10,
                        display: 'flex',
                        gap: 1,
                        zIndex: 1000
                    }}>
                        <IconButton
                            onClick={() => {
                                Object.keys(localStorage)
                                    .filter(key => key.startsWith('persisting:'))
                                    .forEach(key => localStorage.removeItem(key));
                                setPersistingClasses(new Set());
                                refetch();
                            }}
                            aria-label="reload"
                            title="Reload all classes"
                            sx={{
                                '&:hover': {
                                    color: '#3498db'
                                }
                            }}
                        >
                            <ReloadIcon />
                        </IconButton>
                        <IconButton
                            onClick={handleClose}
                            aria-label="close"
                            title="Close"
                            sx={{
                                '&:hover': {
                                    color: '#e74c3c'
                                }
                            }}
                        >
                            <CloseIcon />
                        </IconButton>
                    </Box>
                </Container>
            </Fade>
        </>
    );
};

export default AddNodePage;
