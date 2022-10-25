import React, {useState} from 'react';
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import LandingSection from "../components/LandingSection";

const Home = () => {
    const [isOpen, setIsOpen] = useState(false)

    const toggle = () => {
        setIsOpen(!isOpen)
    }

    return (
        <>
            <Sidebar isOpen={isOpen} toggle={toggle}/>
            <Navbar toggle={toggle}/>
            <LandingSection/>
        </>
    );
}

export default Home
