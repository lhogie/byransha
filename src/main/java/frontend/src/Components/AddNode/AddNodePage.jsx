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
    const allClass = useApiMutation('all_class_view');
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData('all_class_view');
    const [classNames, setClassNames] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');

    const [favorites, setFavorites] = useState(() => {
      const stored = localStorage.getItem('favorites');
      return stored ? JSON.parse(stored) : [];
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
        return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
    }, []);

    useEffect(() => {
         if (!rawApiData) return;

         try {
             const classList = rawApiData?.data?.results?.[0]?.result?.data || [];
             const classNames = classList.map(item => item.name);
             setClassNames(classNames);

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
         {classNames
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