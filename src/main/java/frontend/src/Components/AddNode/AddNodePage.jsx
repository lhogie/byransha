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
         console.log("stringifyData");
         return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
    }, []);

    useEffect(() => {
        if (!rawApiData) return;
        try {
            const classList = rawApiData?.data?.results?.[0]?.result?.data || [];
            const shortNames = classList.map(item => {
                const fullName = Object.keys(item)[0];
                return fullName.split('.').pop();
            });
            setClassName(shortNames);
        } catch (err) {
            console.error("Failed to parse class names:", err);
        }
    }, [rawApiData]);

   return (
      <>
     <div className="add-node-page">
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
                            <span onClick={() => console.log(`Clicked on ${name}`)}>
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
                       <span onClick={() => console.log(`Clicked on ${name}`)}>
                         {name}
                       </span>
                     </div>
                   ))}
               </div>

       <IconButton className="close-button" onClick={() => { navigate("/home")}} aria-label="close">
         <CloseIcon />
       </IconButton>
     </div>
     </>
   );

};
export default AddNodePage;