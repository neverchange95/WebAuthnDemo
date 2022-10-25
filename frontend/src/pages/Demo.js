import React, {useState} from 'react';
import styled from "styled-components";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import DemoSection from "../components/DemoSection";

const Demo = () => {
    const [isOpen, setIsOpen] = useState(false)

    const toggle = () => {
        setIsOpen(!isOpen)
    }

    return (
        <DemoContainer>
            <Sidebar isOpen={isOpen} toggle={toggle}/>
            <Navbar toggle={toggle}/>
            <DemoSection />
        </DemoContainer>
    );
}

const DemoContainer = styled.div`
  background: black;
  margin-top: 80px;
  min-height: 100vh;
  height: 100%;
`

export default Demo;
