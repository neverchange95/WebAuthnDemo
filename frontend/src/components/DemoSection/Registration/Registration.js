import React, {useState} from "react";
import styled from "styled-components";
import userSVG from '../../../images/user.svg';
import fingerprintSVG from '../../../images/fingerprint.svg';
import keySVG from '../../../images/key.svg';
import ReactJson from 'react-json-view';
import {performMakeCredReq,base64UrlEncode} from "../../../helper/CredHandling";
import {restPost} from "../../../helper/RequestHandling";

const Registration = () => {
    const [step, setStep] = useState(1);
    const [activeReg, setActiveReg] = useState(false);
    const [activeOpt, setActiveOpt] = useState(false);
    const [inputUsername, setInputUsername] = useState('')
    const [inputDisplayname, setInputDisplayname] = useState('')
    const [error, setError] = useState(false)
    const [attestation, setAttestation] = useState('none')
    const [authenticator, setAuthenticator] = useState('platform')
    const [verification, setVerification] = useState('preferred')
    const [residentKeyReq, setResidentKeyReq] = useState(false)
    const [errMessage, setErrMessage] = useState('')
    const [request, setRequest] = useState({})
    const [response, setResponse] = useState({})
    const [createCredentialOptions, setCreateCredentialOptions] = useState({})
    const [responseStatus, setResponseStatus] = useState(false)
    const [requestStatus, setRequestStatus] = useState(false)
    const [credentialCreated, setCredentialCreated] = useState(false)
    const [credentialResponse, setCredentialResponse] = useState({})

    const increaseStep = () => {
        if(responseStatus !== true) {
            if(requestStatus !== true) {
                setErrMessage('Registriere dich bevor du auf "Weiter" klickst!')
                setError(true)
            } else {
                setErrMessage('Es konnte keine Verbindung zum Server aufgebaut werden!')
                setError(true)
            }
        } else {
            setStep(step + 1)
        }
    }

    const decreaseStep = () => {
        setStep(step - 1)
    }

    const startRegistration = () => {
        setError(false)

        if(inputUsername === '') {
            setErrMessage('Bestimme zunächst einen Usernamen!')

            setError(true)
            return
        }

        if(inputDisplayname === '') {
            setErrMessage('Bestimme zunächst einen Anzeigename!')

            setError(true)
            return;
        }

        setActiveReg(true)

        // prepare credentials creation options request JSON object
        let severPublicKeyCredentialCreationOptionsRequest = {
            username: inputUsername,
            displayName: inputDisplayname,
        }

        severPublicKeyCredentialCreationOptionsRequest.authenticatorSelection = {
            requireResidentKey: residentKeyReq,
            userVerification: verification,
            authenticatorAttachment: authenticator
        }

        severPublicKeyCredentialCreationOptionsRequest.attestation = attestation

        setRequest(severPublicKeyCredentialCreationOptionsRequest)
        setRequestStatus(true)


        getRegistrationChallenge(severPublicKeyCredentialCreationOptionsRequest)
            .then(createCredentialOptions => {
                setCreateCredentialOptions(createCredentialOptions)
        })
    }

    async function startCreatingCredential() {
        if(!credentialCreated) {
            await sleep(1000)
            createCredential(createCredentialOptions)
            setCredentialCreated(true)
        }
    }

    function getRegistrationChallenge(serverPublicKeyCredentialCreationOptionsRequest) {
        return restPost('http://localhost:8080/attestation/options', serverPublicKeyCredentialCreationOptionsRequest)
            .then(res => {
                if(res.status !== 'ok') {
                    return Promise.reject(res.errorMessage)
                } else {
                    setResponse(JSON.parse(JSON.stringify(res)))
                    setResponseStatus(true)
                    let createCredentialOptions = performMakeCredReq(res)
                    return Promise.resolve(createCredentialOptions)
                }
            })
    }

    function createCredential(options) {
        return navigator.credentials.create({publicKey: options})
            .then(rawAttestation => {
                // Decode rawAttestation
                const utf8Decoder = new TextDecoder('utf-8')
                const decodedClientData = utf8Decoder.decode(rawAttestation.response.clientDataJSON)
                const CBOR = require('cbor-js');
                const decodedAttestationObj = CBOR.decode(rawAttestation.response.attestationObject);

                // Get RP-ID Hash
                const rpidHash = base64UrlEncode(decodedAttestationObj.authData.slice(0, 32))

                // Get Flags
                const flags = decodedAttestationObj.authData.slice(32, 33)
                let flagBits = byte2bits(flags[0])
                const flagObj = getFlagObject(flagBits)

                // Get Counter
                const counter = decodedAttestationObj.authData.slice(33, 37)
                const counterInteger = counter[0] << 24 | (counter[1] & 0xff) << 16 | (counter[2] & 0xff) << 8 | (counter[3] & 0xff)

                // Get AAGUID
                const aaguid = base64UrlEncode(decodedAttestationObj.authData.slice(37, 53))

                // Get the length of the credential ID
                const dataView = new DataView(new ArrayBuffer(2))
                const idLenBytes = decodedAttestationObj.authData.slice(53, 55)
                idLenBytes.forEach(
                    (value, index) => dataView.setUint8(index, value)
                )
                const credentialIdLength = dataView.getUint16()

                // Get credential ID
                const credentialId = base64UrlEncode(decodedAttestationObj.authData.slice(55, 55 + credentialIdLength))

                // Get public key object
                const publicKeyBytes = decodedAttestationObj.authData.slice(55 + credentialIdLength)
                const publicKeyObject = CBOR.decode(publicKeyBytes.buffer)

                const credentialData = {
                    aaguid: aaguid,
                    credentialId: credentialId,
                    publicKey: publicKeyObject,
                }

                const authData = {
                    rpIdHash: rpidHash,
                    flags: flagObj,
                    signatureCounter: counterInteger,
                    credentialData : credentialData,
                }

                const attestationObject = {
                    authData: authData,
                    fmt: decodedAttestationObj.fmt,
                    attStmt: decodedAttestationObj.attStmt
                }

                // Prepare JSON Viewer Object
                const credentialAuthenticatorResponse = {
                    rawId: base64UrlEncode(rawAttestation.rawId),
                    id: base64UrlEncode(rawAttestation.rawId),
                    response : {
                        clientDataJSON: JSON.parse(decodedClientData),
                        attestationObject: attestationObject,
                    },
                    type: rawAttestation.type,
                }

                // Prepare response for the relying party
                let attestation = {
                    rawId: base64UrlEncode(rawAttestation.rawId),
                    id: base64UrlEncode(rawAttestation.rawId),
                    response : {
                        clientDataJSON: base64UrlEncode(rawAttestation.response.clientDataJSON),
                        attestationObject: base64UrlEncode(rawAttestation.response.attestationObject)
                    },
                    type: rawAttestation.type,
                };

                // set extensions if it is available
                if (rawAttestation.getClientExtensionResults) {
                    attestation.extensions = rawAttestation.getClientExtensionResults();
                    credentialAuthenticatorResponse.extensions = rawAttestation.getClientExtensionResults();
                }

                // set transports if it is available
                if (typeof rawAttestation.response.getTransports === "function") {
                    attestation.response.transports = rawAttestation.response.getTransports();
                    credentialAuthenticatorResponse.response.transports = rawAttestation.response.getTransports();
                }

                setCredentialResponse(credentialAuthenticatorResponse)

                // post the attestation object to the relying party
                return restPost("http://localhost:8080/attestation/result", attestation)
            })
            .catch(function(error) {
                if (error === "AbortError") {
                    // User aborted the registration process
                    console.info("Registrierung wurde vom Benutzer abgebrochen");
                }
                return Promise.reject(error);
            })
            .then(response => {
                // get relying party response
                if(response.status !== 'ok') {
                    alert("Registrierung ist fehlgeschlagen!")
                    console.log("Serverstatus Registrierung: " + response.status)
                    return Promise.reject(response.errorMessage)
                } else {
                    alert("Registrierung war erfolgreich!")
                    console.log("Serverstatus Registrierung: " + response.status)
                    return Promise.resolve(response)
                }
            })
    }

    function sleep(millis) {
        return new Promise(resolve => setTimeout(resolve, millis))
    }

    function byte2bits(a) {
        let tmp = "";
        for(let i = 128; i >= 1; i /= 2)
            tmp += a&i?'1':'0';
        return tmp;
    }

    function getFlagObject(flagBits) {
        const flagObject = {}
        flagObject.ED = flagBits[0] === '1'
        flagObject.AT = flagBits[1] === '1'
        flagObject.UV = flagBits[5] === '1'
        flagObject.UP = flagBits[7] === '1'

        return flagObject
    }

    return(
        <>
            <RegistrationStepperContainer>
                <Heading className={step === 1 ? 'active-heading' : ''}>Token- und Benutzerregistrierung</Heading>
                <Heading className={step === 2 ? 'active-heading' : ''}>Erzeugen des Credentials</Heading>
                <Heading className={step === 3 ? 'active-heading' : ''}>Erzeugtes Credential</Heading>

                <ProgressBarContainer>
                    <ProgressLine className={(step === 2 ? 'fifty' : step === 3 ? 'hundred' : '')}/>
                    <ProgressCircle className={'active'}><UserImage src={userSVG} alt='user' /></ProgressCircle>
                    <ProgressCircle className={step === 2 ? 'active' : step === 3 ? 'active' : ''}><FingerprintImage src={fingerprintSVG} alt='user' /></ProgressCircle>
                    <ProgressCircle className={step === 3 ? 'active' : ''}><KeyImage src={keySVG} alt='user' /></ProgressCircle>
                </ProgressBarContainer>

                <StepContent className={step === 1 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>
                                Im ersten Schritt dieser Demo werden vom Anwender die Angaben über den anzulegenden Benutzer
                                in den beiden vorgesehenen Textfeldern getätigt.
                            </li>
                            <li style={{marginTop: '10px'}}>
                                Standardmäßig verwendet diese Demo einen im Endgerät verbauten Authentifikator, wie bspw.
                                Windows Hello oder TouchID. Zur Registrierung eines externen Tokens, wie bspw. einen YubiKey
                                oder ein über Bluetooth verbundenes Smartphone muss in den "Weiteren Optionen" unter dem
                                Punkt "Authentifikatorplattform" der Wert "Cross-Platform" gewählt werden.
                            </li>
                            <li style={{marginTop: '10px'}}>
                                In den "Weiteren Optionen" sind außerdem weitere Werte, wie die Art der Authentifikatorbeglaubigung,
                                die Durchführung einer Benutzerverifizierung oder dem Erstellen eines Discoverable
                                Credentials zur Authentifizierung ohne die Eingabe eines Benutzernamens konfigurierbar.
                            </li>
                            <li style={{marginTop: '10px'}}>
                                Um mit der Registrierung zu starten muss nur ein Benutzer- und Anzeigename in die vorgesehenen
                                Textfelder eingetragen und anschließend der Button "Registrierung" angeklickt werden.
                                Es wird nun eine Verbindung zur relying party hergestellt und die gewählten Informationen werden vom
                                Client übergeben. Die relying party antwortet mit notwendigen Informationen zur Erzeugung des
                                Schlüsselmaterials und wartet anschließend auf den generierten öffentlichen Schlüssel des Benutzers.
                                Um diesen durch den Authentifikator zu erzeugen, wird der Button "Weiter" betätigt.
                            </li>
                        </ul>
                    </Description>
                    <InputGroup>
                        <InputLabel>Benutzername:</InputLabel>
                        <CustomInput type='text' name='username' value={inputUsername} onInput={e => setInputUsername(e.target.value)}/>
                    </InputGroup>

                    <InputGroup>
                        <InputLabel>Anzeigename:</InputLabel>
                        <CustomInput type='text' name='displayname' value={inputDisplayname} onInput={e => setInputDisplayname(e.target.value)}/>
                    </InputGroup>

                    <CustomButton style={{marginBottom: "30px", width: "200px"}} onClick={() => { activeOpt !== true ? setActiveOpt(true) : setActiveOpt(false)}}>Weitere Optionen</CustomButton>

                    <RegistrationOptionsContainer className={activeOpt === true ? 'active' : ''}>
                        <h4>Authentifikatorbeglaubigung:</h4>
                            <p style={{fontSize: '14px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                Mit der Authentifikatorbeglaubigung wird der relying party ermöglicht, eine kryptografisch
                                verifizierte Vertrauenskette des Geräteherstellers zu verwenden, um basierend auf individuellen
                                Bedürfnissen auszuwählen, welchen Sicherheitsschlüsseln man vertrauen möchte. Die Beglaubigung
                                ist daher ein Mittel für die relying party, um die Herkunft des Authentifikators mit einer
                                Beglaubigungszertifizierungsstelle zu verifizieren. In der Bescheinigung können einige Informationen
                                über den Benutzer offengelegt werden (bspw. welchen Authentifikator er verwendet), daher besteht die Möglichkeit
                                für eine indirekte Beglaubigung.
                            </p>
                            <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                <li><b>NONE:</b> Die relying party ist an KEINER Beglaubigung vom Authentifikator interessiert.</li>
                                <li><b>INDIRECT:</b> Die relying party möchte eine Beglaubigung erhalten, überlässt den
                                    Client jedoch die Entscheidung, wie diese auszusehen hat. D.h. der Client KANN die
                                    vom Authentifikator generierte Beglaubigung verändern/anonymisieren, um bspw. die
                                    Privatsphäre des Benutzers zu schützen. Die relying party hat in diesem Fall jedoch
                                    keine Garantie dafür, dass diese Bescheinigung verifizierbar ist.
                                </li>
                                <li><b>DIRECT:</b> Die relying party benötigt eine verifizierbare unveränderte Bescheinigung direkt vom Authentifikator.</li>
                            </ul>
                        <RadioButton type='radio' value='none' name='attestation' checked={attestation === 'none'} onChange={e => setAttestation(e.target.value)}/>None
                        <RadioButton type='radio' value='indirect' name='attestation' checked={attestation === 'indirect'} onChange={e => setAttestation(e.target.value)} style={{marginLeft: '20px'}}/>Indirect
                        <RadioButton type='radio' value='direct' name='attestation' checked={attestation === 'direct'} onChange={e => setAttestation(e.target.value)} style={{marginLeft: '20px'}}/>Direct

                        <h4 style={{marginTop: '20px'}}>Authentifikatorplattform:</h4>
                            <p style={{fontSize: '14px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                Legt den Kommunikationsmechanismus zwischen Client und Authentifikator fest.
                            </p>
                            <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                <li><b>PLATFORM:</b> Der Authentifikator ist im Gerät integriert (TouchID, FaceID, Windows Hello, etc.).</li>
                                <li><b>CROSS-PLATFORM:</b> Der Authentifikator istx eine entfernbare Hardwarekomponente (USB, Bluetooth, NFC, etc.).</li>
                            </ul>
                        <RadioButton type='radio' value='platform' name='authenticator' checked={authenticator === 'platform'} onChange={e => setAuthenticator(e.target.value)}/>Platform
                        <RadioButton type='radio' value='cross-platform' name='authenticator' checked={authenticator === 'cross-platform'} onChange={e => setAuthenticator(e.target.value)} style={{marginLeft: '20px'}}/>Cross-Platform

                        <h4 style={{marginTop: '20px'}}>Discoverable Credential (Authentifizierung ohne Benutzernamen):</h4>
                            <p style={{fontSize: '14px', lineHeight: '20px', marginBottom: '10px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                Ermöglicht, die für die Anmeldung benötigten Schlüsselinformationen auf dem Authentifikator selbst zu speichern.
                                Als Ergebnis kann die Authentifizierung ohne Bereitstellung von CredentialIDs, und somit ohne die Eingabe eines Benutzernamens erfolgen.
                                Voraussetzung hierfür ist, dass der verwendete Authentifikator über einen internen Speicher verfügt.
                            </p>
                        <RadioButton type='checkbox' onChange={e => setResidentKeyReq(e.target.checked)} />Required

                        <h4 style={{marginTop: '20px'}}>Benutzerverifizierung:</h4>
                        <p style={{fontSize: '14px', lineHeight: '20px', marginBottom: '10px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            Hiermit weist die relying party den Authentifikator an eine Benutzerverifizierung durchzuführen
                            und der relying party im Anschluss das Ergebnis zukommen zu lassen. Die Verifizierung kann unter anderem durch einen PIN, ein Passwort
                            oder eines biometrischen Merkmals erfolgen.
                        </p>
                        <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                                <li><b>REQUIRED:</b> Die relying party benötigt zwingend das Ergebnis einer Benutzerverifizierung.</li>
                                <li><b>PREFERRED:</b> Die relying party bevorzugt das Ergebnis einer Benutzerverifizierung. Der Registrierungsprozess wird in diesem Fall jedoch auch erfolgreich durchgeführt, wenn die relying party kein Ergebnis über eine erfolgreich durchgeführte Verifizierung erhält.</li>
                                <li><b>DISCOURAGED:</b> Die relying party benötigt KEIN Ergebnis einer Benutzerverifizierung.</li>
                            </ul>
                        <RadioButton type='radio' value='required' name='verification' checked={verification === 'required'} onChange={e => setVerification(e.target.value)} />Required
                        <RadioButton type='radio' value='preferred' name='verification' checked={verification === 'preferred'} onChange={e => setVerification(e.target.value)} style={{marginLeft: '20px'}}/>Preferred
                        <RadioButton type='radio' value='discouraged' name='verification' checked={verification === 'discouraged'} onChange={e => setVerification(e.target.value)} style={{marginLeft: '20px'}}/>Discouraged
                    </RegistrationOptionsContainer>

                    <ButtonContainer>
                        <CustomButton type='submit' onClick={() => { startRegistration() }}>Registierung</CustomButton>
                        <ErrorMessage className={error === true ? 'active' : ''}>{errMessage}</ErrorMessage>
                        <CustomButton onClick={() => {increaseStep(); startCreatingCredential()}}>Weiter</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeReg === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 0: Client</h3>
                            <AnimatedArrow/>
                            <h3>Relying Party</h3>
                        </HeadingArrowDescription>

                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Der Client initiiert den folgenden Request, um einen Benutzer anhand des Benutzernamens zu registrieren.</li>
                            <li style={{marginTop: '10px'}}>Dazu werden mit dem dem Benutzer- und Displaynamen die gewählten <em>weiteren Optionen</em> an die Relying Party übergeben.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeReg === true ? 'active' : ''}>
                        <ReactJson src={request} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>

                    <ResponseDescription className={activeReg === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 1: Relying Party</h3>
                            <AnimatedArrow/>
                            <h3>Browser</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Die relying party nimmt den oben durchgeführten Request entgegen und antwortet mit einem PublicKeyCredentialCreationOptions Objekt. Der Authentifikator erzeugt auf dessen Basis in Schritt 2 das Schlüsselpaar für den jeweiligen Benutzer und der jeweiligen relying party.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>status:</b> Gibt den Response Status der relying party an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>errorMessage:</b> Beinhaltet eine Beschreibung des Fehlers, falls ein Fehler auftritt. Sonst null.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>sessionId:</b> Die ID der Session, um User-Anfragen einer Sitzung zuzuordnen. Dieser String wurde von der relying party erzeugt.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rp:</b> Enthält Informationen über die relying party.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>id:</b> Wert zur eindeutigen Identifikation der relying party. Dieser Wert muss ein Suffix des Origin sein.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>name:</b> Aussagekräftige Bezeichnung dieser relying party.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>icon:</b> Eine URL als USVString-Wert, welcher auf eine Bildressource verweist, die als Logo oder Symbol der relying party steht. (Optional).</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>user:</b> Enthält Informationen über das zu erstellende Benutzerkonto.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>id:</b> Wurde aus dem angegebenen Benutzernamen generiert und wird für die eindeutige Identifikation des Benutzerkontos benötigt.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>name:</b> Der vom Anwender in Schritt 1 angegebene Benutzername.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>displayName:</b> Benutzerfreundlicher Name für die Anzeige an der Weboberfläche (bspw. Maximilian Mustermann).</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>challenge:</b> Ist ein von der relying party zufällig generierter Wert und dient unter anderem der Vorbeugung von Wiederholungsangriffen. </li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>pubKeyCredParams:</b> Gibt die von der relying party unterstützten und erlaubten Algorithmen zur Erzeugung des Schlüsselpaares an. Enthält dieses Objekt mehrere Elemente, sind diese nach absteigender Präferenz sortiert.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>type:</b> Beschreibt den Typ des kryptographischen Algorithmus. Beinhaltet bei WebAuthn immer den Wert <em>public-key.</em></li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>alg:</b> Ein nummerischer Bezeichner für den Algorithmus, der zum generieren des Schlüsselpaares verwendet werden soll. Diese Algorithmen entsprechen den COSE-Algorithmen (CBOR Object Signing and Encryption).</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>timeout:</b> Zeit in Millisekunden, die von der relying party auf eine Antwort gewartet wird.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>excludeCredentials:</b> Eine Liste bereits registrierter Credentials für diesen Benutzer. Dient als Info für den Authentikator, um eine doppelte Registierung eines Credentials zu vermeiden.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>authenticatorSelection:</b> Spezifiziert Anforderungen, welche Operationen ein Authentifikator durchführen und unterstützen muss.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>authenticatorAttachment:</b> Beschreibt den Authentifikatortyp (platform = im Clientgerät integriert, cross-platform = externe Komponente).</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>requireResidentKey:</b> Beschreibt, ob ein <em>discoverable credential</em> erzeugt werden soll. Damit kann sich der Anwender ohne einer Angabe von Benutzerinformationen authentifizieren. Außerdem werden die Authentifizierungsdaten hierbei auf dem Authentifikator selbst gesichert, statt sie der relying party zur Speicherung zu übergeben.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>userVerification:</b> Beschreibt, ob der Authentifikator neben dem Test auf Präsenz, eine lokale Prüfung der Authentizität des Anwenders durchführen soll.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>attestation:</b> Gibt die gewünschte Beglaubigungsvariante an. Für weitere Informationen siehe <em>Weitere Optionen.</em></li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeReg === true ? 'active' : ''}>
                        <ReactJson src={response} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>

                <StepContent className={step === 2 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Der Browser sucht nun nach verfügbaren Authentifikatoren und verbindet sich entsprechend. Sind mehrere Authentifikatoren verfügbar, wird der Browser den Anwender zu einer Auswahl auffordern.</li>
                            <li style={{marginTop: '10px'}}> Ist eine Verifikation des Benutzers erforderlich, wird dieser außerdem zur Eingabe eines PINs, eines Passwortes, eines biometrischen Merkmals oder ähnlichem aufgefordert, bevor der Authentifikator die entsprechenden Operationen durchführt.</li>
                        </ul>
                    </Description>
                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zurück</CustomButton>
                        <CustomButton onClick={() => increaseStep()}>Weiter</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeReg === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 2: Browser</h3>
                            <AnimatedArrow/>
                            <h3>Authentifikator</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Damit der Authentifikator das Credential erfolgreich erzeugen kann, wird zunächst das in Schritt 1 erhaltene Objekt in das richtige Format gebracht:</li>
                                <li style={{marginLeft: '25px', marginTop: '10px'}}>Dazu werden die Attribute <em>status</em> und <em>errorMessage</em> entfernt, sowie die <em>userID</em> und die <em>challenge</em> mit Base64 entschlüsselt.</li>
                            <li style={{marginTop: '10px'}}>Der Browser (Client) ruft anschließend die Funktion <em>navigator.credentials.create</em> auf und übergibt dabei als Parameter das unten aufgeführte Objekt.</li>
                            <li style={{marginTop: '10px'}}>Der Client sucht nach verfügbaren Authentifikatoren und verbindet sich entsprechend. Sind mehrere Authentifikatoren verfügbar, wird der Anwender zur Auswahl aufgefordert.</li>
                            <li style={{marginTop: '10px'}}>Daraufhin erstellt der Authentifikator ein neues asymmetrisches Schlüsselpaar und gibt das im nächsten Schritt 3 zu sehende Objekt an den Browser zurück.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeReg === true ? 'active' : ''}>
                        <ReactJson src={createCredentialOptions} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>

                <StepContent className={step === 3 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Das unten aufgeführte Objekt enthält die in Schritt 2 vom Authentifikator erzeugten und an die relying party übergebenen Daten. Das Objekt enthält unter anderem den erzeugten öffentlichen Schlüssel und weitere Attribute, um den Registrierungsprozess zu validieren.</li>
                            <li style={{marginTop: '10px'}}>Der Serverstatus über den Erfolg des Registrierungsprozesses kann nachträglich nochmals in der Konsole des Browsers geprüft werden.</li>
                        </ul>
                    </Description>
                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zurück</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeReg === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Step 3: Authentifikator</h3>
                            <AnimatedArrow/>
                            <h3>Browser</h3>
                            <AnimatedArrow/>
                            <h3>Relying Party</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rawId:</b> Ist identisch mit dem darunter aufgeführten Attribut id, nur in binärer Form. Das Attribut wurde zur besseren Lesbarkeit Base64 verschlüsselt und ist ursprünglich ein Byte Array.</li>
                                <li style={{marginLeft: '25px', marginTop: '10px'}}><b>id:</b> Diese ID ist identisch mit der weiter unten aufgeführten CredentialID und ist hier nur zusätzlich aufgeführt. </li>
                                <li style={{marginLeft: '25px', marginTop: '10px'}}><b>response:</b> Beinhaltet alle vom Authentifikator erzeugten und an den Browser zurückgegebenen Daten.</li>
                                    <li style={{marginLeft: '45px', marginTop: '6px'}}><b>clientDataJSON:</b> Eine Sammlung von Daten, die vom Browser an den Authentifikator übergeben werden.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>type:</b> Gibt die durchgeführte WebAuthn-Operation an. Beinhaltet entweder den Wert "webauthn.get", wenn ein vorhandenes Credential abgerufen wird oder "webauthn.create", wenn ein neuer Berechtigungsnachweis erstellt wird. Das Attribut beinhaltet somit bei der Registrierung immer den Wert "webauthn.create"</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>challenge:</b> Die kryptografische Challenge, welche beim initialen Austausch von der relying party gesendet wurde.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>origin:</b> Origin, auf dem die webauthn.create-Operation durchgeführt wurde. Die rpID muss ein Suffix dieses Wertes sein.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>crossOrigin:</b> Gibt an, ob die Möglichkeit von cross-origin-requests bestehen soll. D.h. die relying party akzeptiert Anfragen auch von einer anderen Origin, die nicht ihrer eigenen Origin entspricht.</li>
                                    <li style={{marginLeft: '45px', marginTop: '6px'}}><b>attestationObject:</b> Beinhaltet die Rückgabewerte der durchgeführten Operationen zur Erzeugung des Credentials, sowie die Daten für die Authentifikatorbeglaubigung.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>authData:</b> Beinhaltet die Rückgabewerte der durchgeführten Operationen zur Erzeugung des Credentials.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>rpIdHash:</b> Ein SHA256-Hash der relying party ID. Die relying party stellt damit sicher, dass dieser Hash mit dem Hash der eigenen ID übereinstimmt, um Phishing oder Man-in-the-Middle-Angriffe zu verhindern.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>flags:</b> Ein Bitfeld, das verschiedene Attribute angibt, die vom Authentifikator bestätigt wurden.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>ED:</b> Zeigt an, ob die zurückgegebenen Daten Erweiterungen (extensions) enthalten.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>AT:</b> Zeigt an, ob der Authentifikator Daten über für eine Beglaubigung hinzugefügt hat.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>UV:</b> Zeigt an, ob der Benutzer vom Authentifikator verifiziert wurde. Die Verifizierung erfolgt mittels einer PIN, einem Passwort, eines biometrischen Merkmals oder ähnlichem.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>UP:</b> Zeigt durch eine Prüfung der Nutzerpräsenz an, ob der Benutzer anwesend ist. Diese Prüfung kann durch das Berühren einer auf dem Authentifikator befindlichen Schaltfläche geschehen oder durch eine erfolgreich durchgeführte lokale Verifizierung des Anwenders.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>signatureCounter:</b> Ein Zähler, der bei jeder erfolgreich durchgeführten Authentifizierung inkrementiert wird. Dieser dient der relying party dazu geklonte Authentifikatoren zu erkennen.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>credentialData:</b> Beinhaltet Daten über das erzeugte Credential.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>aaguid:</b> Ein vom Hersteller des Authentifikators gewählter Identifier. Dieser dient der relying party unter anderem dazu, den öffentlichen Beglaubigungsschlüssel eines Authentifikators für die Verifikation der Vertrauenssignatur im FIDO Metadaten Service zu finden.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>credentialId:</b> Der zum Schlüsselpaar generierte Identifier.</li>
                                                <li style={{marginLeft: '105px', marginTop: '4px'}}><b>publicKey:</b> Der erzeugte öffentliche Schlüssel, verschlüsselt im COSE-Key-Format. Dieser wird bei der relying party in einer Datenbank zum jeweiligen Benutzer gespeichert.</li>
                                                    <li style={{marginLeft: '125px', marginTop: '4px'}}><b>1:</b> Beschreibt den Schlüsseltyp.</li>
                                                    <li style={{marginLeft: '125px', marginTop: '4px'}}><b>3:</b> Gibt den Algorithmus, welcher zur Generierung der Signaturen verwendet wird an.</li>
                                                    <li style={{marginLeft: '125px', marginTop: '4px'}}><b>-1:</b> Beschreibt den Schlüsselkurven Typ.</li>
                                                    <li style={{marginLeft: '125px', marginTop: '4px'}}><b>-2:</b> Beschreibt die x-Koordinate des Public Keys.</li>
                                                    <li style={{marginLeft: '125px', marginTop: '4px'}}><b>-3:</b> Beschreibt die y-Koordinate des Public Keys.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>fmt:</b> Gibt das Format für das <em>attStmt</em> an, damit die relying party im klaren ist, wie sie das attestation statement zu parsen hat.</li>
                                        <li style={{marginLeft: '65px', marginTop: '4px'}}><b>attStmt:</b> Beinhaltet die Daten zur Authentifikatorbeglaubigung. Das Objekt kann sich je nach Beglaubigungsformat und Authentifikator unterscheiden.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>alg:</b> Gibt den COSE-Algorithmus an, mit dem die Signatur erzeugt wurde.</li>
                                            <li style={{marginLeft: '85px', marginTop: '4px'}}><b>sig:</b> Beinhaltet die erzeugte Beglaubigungssignatur.</li>
                                    <li style={{marginLeft: '45px', marginTop: '6px'}}><b>transports:</b> Gibt den Kommunikationsweg zwischen Browser und Authentifikator an.</li>
                                <li style={{marginLeft: '25px', marginTop: '10px'}}><b>type:</b> Enthält den Typ der Anmeldeinformationen. Gültige Werte sind: password, federated und public-key. In diesem Fall handelt es sich immer um ein PublicKeyCredential und somit ist der Wert immer "public-key".</li>
                                <li style={{marginLeft: '25px', marginTop: '10px'}}><b>extensions:</b> Gibt mögliche Client-WebAuthn-Erweiterungen an, um bspw. nicht nur festzustellen, ob der Benutzer verifiziert wurde, sondern auch wie die Verifikation erfolgt ist. Diese Demo bietet jedoch keine Möglichkeit Erweiterungen zu wählen. Das Objekt ist somit immer leer.</li>
                            <li style={{marginTop: '10px'}}>Das gesamte Objekt wird im Anschluss Base64 verschlüsselt und der relying party übergeben.</li>
                            <li style={{marginTop: '10px'}}>Die relying party führt nach Erhalt eine Reihe von Überprüfungen durch, um sicherzustellen, dass die Registrierungszeremonie nicht manipuliert wurde und alle Daten den Erwartungen entsprechen.</li>
                            <li style={{marginTop: '10px'}}>Abschließend erzeugt die relying party einen neuen Benutzer in der Datenbank und speichert unter anderem den erhaltenen Public Key.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeReg === true ? 'active' : ''}>
                        <ReactJson src={credentialResponse} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>
            </RegistrationStepperContainer>
        </>
    );
}

const JSONViewer = styled.div`
  margin-top: 30px;
  display: none;

  &.active {
    display: block;
  }
`

const ErrorMessage = styled.h4`
  color: red;
  text-align: center;
  margin-bottom: 20px;

  display: none;

  &.active {
    display: block;
  }
`

const AnimatedArrow = styled.div`
    width:100px;
    height:30px;
    margin-left: 10px;
    margin-right: 10px;
    margin-top: -2px;
    display: flex;

  &:before {
    content: "";
    background: #01bf71;
    width:15px;
    clip-path: polygon(0 10px,calc(100% - 15px) 10px,calc(100% - 15px) 0,100% 50%,calc(100% - 15px) 100%,calc(100% - 15px) calc(100% - 10px),0 calc(100% - 10px));
    animation: a1 3.0s infinite linear;
  }
  @keyframes a1 {
    90%,100%{flex-grow: 1}
  }

`

const RegistrationStepperContainer = styled.div`

`

const Heading = styled.h1`
  margin-top: 30px;
  margin-bottom: 40px;
  color: white;
  text-align: center;
  display: none;

  &.active-heading {
    display: block;
  }
`

const ProgressBarContainer = styled.div`
  display: flex;
  justify-content: space-between;
  position: relative;
  margin: 0 auto 50px;
  max-width: 90%;

  &:before {
    content: "";
    background-color: white;
    position: absolute;
    top: 50%;
    left: 0;
    transform: translateY(-50%);
    height: 4px;
    width: 100%;
    transition: 0.4s ease;
  }
`

const ProgressLine = styled.div`
  background-color: #01bf71;
  position: absolute;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  height: 4px;
  width: 0%;
  transition: 0.4s ease;

  &.fifty {
    width: 50%;
  }

  &.hundred {
    width: 100%;
  }
`

const ProgressCircle = styled.div`
  background-color: white;
  color: black;
  border-radius: 50%;
  height: 40px;
  width: 40px;
  border: 3px solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.4s ease;
  z-index: 1;

  &.active {
    border-color: #01bf71;
  }
`

const UserImage = styled.img`
  width: 23px;
  height: 23px;
`

const FingerprintImage = styled.img`
  width: 25px;
  height: 25px;
`

const KeyImage = styled.img`
  width: 23px;
  height: 23px;
`

const StepContent = styled.div`
  display: none;

  &.active-step-content {
    display: block;
  }
`

const Description = styled.div`
  margin-top: 50px;
  color: white;
`

const ResponseDescription = styled.div`
  margin-top: 50px;
  color: white;
  display: none;

  &.active {
    display: block;
  }
`

const HeadingArrowDescription = styled.div`
  display: flex;
`

const InputGroup = styled.div`
  margin: 2rem 0;
`

const InputLabel = styled.label`
  display: block;
  margin-bottom: 0.5rem;
`

const CustomInput = styled.input`
  display: block;
  width: 100%;
  padding: 0.75rem;
  border: 1px solid white;
  border-radius: 0.25rem;
  background-color: black;
  color: white;
`

const RadioButton = styled.input`
  margin-right: 5px;
`

const ButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
`

const CustomButton = styled.a`
  padding: 0.75rem;
  display: block;
  text-decoration: none;
  background-color: #01bf71;
  color: black;
  text-align: center;
  border-radius: 0.25rem;
  cursor: pointer;
  transition: 0.3s;
  width: 150px;

  &:hover {
    box-shadow: 0 0 0 2px black, 0 0 0 3px #01bf71;
  }
`

const RegistrationOptionsContainer = styled.div`
  display: none;
  margin-bottom: 40px;

  &.active {
    display: block;
  }

`
export default Registration
