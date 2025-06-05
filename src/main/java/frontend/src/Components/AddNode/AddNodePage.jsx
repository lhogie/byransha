import {useParams} from 'react-router';
import './AddNodePage.css';
import React, {useEffect, useState, useCallback} from 'react';
import {useTitle} from "../../global/useTitle";
import {View} from "../Common/View.jsx";
import {IconButton} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import CodeIcon from '@mui/icons-material/Code';
import {useNavigate} from "react-router";
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import ReloadIcon from '@mui/icons-material/Refresh';
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import {useQueryClient} from "@tanstack/react-query";



const AddNodePage = () => {
  useTitle(`Add node`);

  const {viewId} = useParams();
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

      await jumpMutation.mutateAsync(`node_id=${encodeURIComponent(data)}`);

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
      <div className={`add-node-page ${exitAnim ? 'add-node-exit' : ''}`}>
        <h1>Add a new node</h1>

        <input
          type="text"
          placeholder="Search class name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-bar"
        />

        {favorites.length > 0 && (
          <>
            <h2>Favorites (Persistent Only)</h2>
            <div className="class-card-container">
              {favorites
                .map(name => {
                  const fullName = fullClassName.find(f => f.endsWith(name));
                  return { short: name, full: fullName };
                })
                .filter(({ full }) => full && persistingClasses.has(full))
                .filter(({ short }) => short.toLowerCase().includes(searchTerm.toLowerCase()))
                .map(({ short }) => (
                  <div key={short} className="class-card">
                    <span
                      className="star-icon"
                      onClick={(e) => {
                        e.stopPropagation();
                        toggleFavorite(short);
                      }}
                    >
                      <StarIcon style={{ color: '#f1c40f' }} />
                    </span>
                    <span onClick={() => handleClickClass(short)}>{short}</span>
                  </div>
                ))}
            </div>
          </>
        )}

        <h2>All persisting classes</h2>
        <div className="class-card-container">
          {className
            .map((name, index) => ({
              short: name,
              full: fullClassName[index]
            }))
            .filter(({ full }) => persistingClasses.has(full))
            .filter(({ short }) => short.toLowerCase().includes(searchTerm.toLowerCase()))
            .map(({ short }) => (
              <div key={short} className="class-card">
                <span
                  className="star-icon"
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleFavorite(short);
                  }}
                >
                  {favorites.includes(short) ? (
                    <StarIcon style={{ color: '#f1c40f' }} />
                  ) : (
                    <StarBorderIcon style={{ color: '#ccc' }} />
                  )}
                </span>
                <span onClick={() => handleClickClass(short)}>{short}</span>
              </div>
            ))}
        </div>

        <div className = "button-group">
            <IconButton className="header-button reload-button"  onClick={() => {Object.keys(localStorage).filter(key => key
                            .startsWith('persisting:'))
                            .forEach(key => localStorage.removeItem(key));
                            setPersistingClasses(new Set());
                            refetch();
                        }} aria-label="reload" title="Reload all classes">
                    <ReloadIcon />
            </IconButton>
            <IconButton className="header-button close-button" onClick={handleClose} aria-label="close" title="Close">
                <CloseIcon />
            </IconButton>
        </div>


      </div>
    </>
  );
};

export default AddNodePage;
