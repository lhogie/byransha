import {useParams} from 'react-router';
import './AddNodePage.css';
import React, {useEffect, useState, useCallback} from 'react';
import {useTitle} from "../../global/useTitle";
import {View} from "../Common/View.jsx";
import {IconButton} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import CodeIcon from '@mui/icons-material/Code';
import {useNavigate} from "react-router";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import StarIcon from '@mui/icons-material/Star';
import StarBorderIcon from '@mui/icons-material/StarBorder';



const AddNodePage = () => {
    useTitle(`Add node`);

    const {viewId} = useParams();
    const navigate = useNavigate();
    const {data : rawApiData, isLoading: loading, error, refetch} = useApiData('bnode_class_distribution');
    const [className, setClassName] = useState([]);
    const [fullClassName, setFullClassName] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [favorites, setFavorites] = useState(() => {
      try {
        const stored = localStorage.getItem('favorites');
        const parsed = JSON.parse(stored);
        return Array.isArray(parsed) ? parsed : [];
      } catch {
        return [];
      }
    });

    const [exitAnim, setExitAnim] = useState(false);

    const handleClose = () => {
      setExitAnim(true);
      setTimeout(() => navigate("/home"), 300); // Match animation duration
    };

    const handleClickClass = (name) => {
        const fullName = fullClassName.find(item => item.endsWith(name));
        navigate(`/add-node/form/${fullName}`);
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

            // Filter out class names containing "endpoint"
            const filteredList = classList.filter(item => {
                const fullName = Object.keys(item)[0];
                return fullName && !fullName.toLowerCase().includes("endpoint") && !fullName.toLowerCase().includes("view")
                && !fullName.toLowerCase().includes("$");
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
                    <h2>Favorites</h2>
                    <div className="class-card-container">
                      {favorites
                        .filter(name => name.toLowerCase().includes(searchTerm.toLowerCase()))
                        .map((name) => (
                          <div key={name} className="class-card">
                            <span
                              className="star-icon"
                              onClick={(e) => {
                                e.stopPropagation();
                                toggleFavorite(name);
                              }}
                            >
                              <StarIcon style={{ color: '#f1c40f' }} />
                            </span>
                            <span onClick={() => handleClickClass(name)}>
                              {name}
                            </span>
                          </div>
                        ))}
                    </div>
                  </>
                )}

               <h2>All Classes</h2>
               <div className="class-card-container">
                 {className
                   .filter(name => !favorites.includes(name))
                   .filter(name => name.toLowerCase().includes(searchTerm.toLowerCase()))
                   .map((name) => (
                     <div key={name} className="class-card">
                       <span
                         className="star-icon"
                         onClick={(e) => {
                           e.stopPropagation();
                           toggleFavorite(name);
                         }}
                       >
                         <StarBorderIcon style={{ color: '#ccc' }} />
                       </span>
                       <span onClick={() => handleClickClass(name)}>
                         {name}
                       </span>
                     </div>
                   ))}
               </div>

       <IconButton className="close-button" onClick={handleClose} aria-label="close">
         <CloseIcon />
       </IconButton>
     </div>
     </>
   );

};
export default AddNodePage;