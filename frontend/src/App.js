import React from "react";
import './App.css'
import Home from "./pages/Home";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Demo from "./pages";

function App() {
    return (
        <Router>
            <Routes>
                <Route path='/' element={<Home/>} exact/>
                <Route path='/demo' element={<Demo/>} exact/>
            </Routes>
        </Router>
    );
}

export default App;
