import React, {useState} from "react";
import styled from "styled-components";
import Registration from "./Registration/Registration";
import Authentication from "./Authentication/Authentication";


const DemoSection = () => {
    const [toggleState, setToggleState] = useState(1);

    const toggleTab = (index) => {
        setToggleState(index);
    };

    return(
        <>
            <StyledTabContainer>
                <TabButtonContainer>
                    <TabButton
                        className={toggleState === 1 ? "tabs active-tabs" : "tabs"}
                        onClick={() => toggleTab(1)} >
                        Registrierung
                    </TabButton>
                    <TabButton
                        className={toggleState === 2 ? "tabs active-tabs" : "tabs"}
                        onClick={() => toggleTab(2)}>
                        Authentifizierung
                    </TabButton>
                </TabButtonContainer>

                <TabContentContainer>
                    <TabContent className={toggleState === 1 ? "content  active-content" : "content"}>
                        <Registration/>
                    </TabContent>

                    <TabContent className={toggleState === 2 ? "content  active-content" : "content"}>
                        <Authentication/>
                    </TabContent>
                </TabContentContainer>
            </StyledTabContainer>
            <Distance/>
        </>
    );
}

const Distance = styled.div`
  padding-bottom: 100px;
`

const StyledTabContainer = styled.div`
  color: white;
  display: flex;
  flex-direction: column;
  position: relative;
  max-width: 1100px;
  width: 100%;
  height: 100%;
  background: #1c1c1c;
  margin: 80px auto 0;
  word-break: break-all;
  border: 1px solid gray;
`

const TabButtonContainer = styled.div`
  display: flex;    
`

const TabButton = styled.button`
  border: none;
  color: white;
  
  &.tabs {
    padding: 15px;
    text-align: center;
    width: 50%;
    background: rgba(128, 128, 128, 0.075);
    cursor: pointer;
    border-bottom: 1px solid rgba(0, 0, 0, 0.274);
    box-sizing: content-box;
    position: relative;
    outline: none;
  }

  &.tabs:not(:last-child){
    border-right: 1px solid rgba(0, 0, 0, 0.274);
  }

  &.active-tabs  {
    background: black;
    border-bottom: 1px solid transparent;
  }

  &.active-tabs::before {
    content: "";
    display: block;
    position: absolute;
    top: -5px;
    left: 50%;
    transform: translateX(-50%);
    width: calc(100% + 2px);
    height: 5px;
    background: #01bf71;
  }
`

const TabContentContainer = styled.div`
  flex-grow: 1;
`

const TabContent = styled.div`
  &.content {
    background: black;
    padding: 20px;
    width: 100%;
    height: 100%;
    display: none;
  }
  &.content h2 {
    padding: 0px 0 5px 0px;
  }
  &.content hr {
    width: 100px;
    height: 2px;
    background: #222;
    margin-bottom: 5px;
  }
  &.content p {
    width: 100%;
    height: 100%;
  }
  &.active-content {
    display: block;
  }
`

export default DemoSection
