import React, {useState} from "react";
import styled from "styled-components";
import {MdArrowForward, MdKeyboardArrowRight} from "react-icons/md";
import {Link} from 'react-router-dom';

const LandingSection = () => {
    const [hover, setHover] = useState(false)
    const onHover = () => {
        setHover(!hover)
    }

    return(
        <LandingContainer>
            <LandingContent>
                <LandingHeading>FIDO2/WebAuthn Demo Anwendung</LandingHeading>
                <LandingDescription>
                    WebAuthn ist eine Kernkomponente des FIDO2-Projektes und beschreibt einen neuen
                    Standard, der es Webanwendungen mithilfe eines asymmetrischen kryptographischen
                    Verfahrens ermöglicht Benutzer stark und ohne Passwort zu authentifizieren. Diese
                    Anwendung veranschaulicht dabei die ausgetauschten Daten zwischen Relying Party, Client und
                    Authentifikator und ermöglicht es einen vollständigen Registrierungs- und Authentifizierungsprozess
                    mit einem Authentifikator durchzuführen.
                </LandingDescription>
                <LandingDescription>Hier gehts zur Demo:</LandingDescription>
                <LandingButtonWrapper>
                    <Button to='demo' onMouseEnter={onHover} onMouseLeave={onHover} primary='true' dark='true'>
                        Los gehts! {hover ? <ArrowForward /> : <ArrowRight />}
                    </Button>
                </LandingButtonWrapper>
            </LandingContent>
        </LandingContainer>
    );
}

const LandingContainer = styled.div`
    background: black;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 30px;
    height: 100vh;
    position: relative;
    z-index: 1;
`

const LandingContent = styled.div`
    z-index: 3;
    max-width: 1200px;
    position: absolute;
    padding: 8px 24px;
    display: flex;
    flex-direction: column;
    align-items: center;
`

const LandingHeading = styled.h1`
    color: #fff;
    font-size: 48px;
    text-align: center;
  
    @media screen and (max-width: 768px) {
        font-size: 40px;
    }

    @media screen and (max-width: 768px) {
        font-size: 32px;
    }
`

const LandingDescription = styled.p`
    margin-top: 24px;
    color: #fff;
    font-size: 24px;
    text-align: center;
    max-width: 1200px;

    @media screen and (max-width: 768px) {
        font-size: 24px;
    }
    
    @media screen and (max-width: 768px) {
        font-size: 18px;
    }
`

const LandingButtonWrapper = styled.div`
    margin-top: 32px;
    display: flex;
    flex-direction: column;
    align-items: center;
`

const ArrowForward = styled(MdArrowForward)`
    margin-left: 8px;
    font-size: 20px;
`

const ArrowRight = styled(MdKeyboardArrowRight)`
    margin-left: 8px;
    font-size: 20px;
`

const Button = styled(Link)`
    border-radius: 50px;
    background: ${({primary}) => (primary ? '#01BF71' : '#010606')};
    white-space: nowrap;
    padding: ${({big}) => (big ? '14px 48px' : '12px 30px')};
    color: ${({dark}) => (dark ? '#010606' : '#fff')};
    font-size: ${({fontBig}) => (fontBig ? '20px' : '16px')};
    outline: none;
    border: none;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.2s ease-in-out;
    text-decoration: none;
    
    &:hover {
        transition: all 0.2s ease-in-out;
        background: ${({primary}) => (primary ? '#fff' : '#01BF71')};
    }
`

export default LandingSection
