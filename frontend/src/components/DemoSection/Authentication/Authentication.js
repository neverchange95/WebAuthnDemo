import React, {useState} from "react";
import styled from "styled-components";
import {restPost} from "../../../helper/RequestHandling";
import userSVG from "../../../images/user.svg";
import fingerprintSVG from "../../../images/fingerprint.svg";
import signatureSVG from "../../../images/signature.svg";
import ReactJson from "react-json-view";
import {base64UrlEncode, performGetCredReq, performMakeCredReq} from "../../../helper/CredHandling";

const Authentication = () => {
    const [step, setStep] = useState(1)
    const [activeAuth, setActiveAuth] = useState(false)
    const [activeOpt, setActiveOpt] = useState(false);
    const [inputUsername, setInputUsername] = useState('')
    const [verification, setVerification] = useState('preferred')
    const [request, setRequest] = useState({})
    const [response, setResponse] = useState({})
    const [getCredentialOptions, setGetCredentialOptions] = useState({})
    const [error, setError] = useState(false)
    const [errMessage, setErrMessage] = useState('')
    const [responseStatus, setResponseStatus] = useState(false)
    const [requestStatus, setRequestStatus] = useState(false)
    const [authenticatorResponse, setAuthenticatorResponse] = useState({})
    const [activeAuthDemo, setActiveAuthDemo] = useState(true)
    const [loggedInUsername, setLoggedInUsername] = useState('no user logged in')
    const [loggedInDisplayname, setLoggedInDisplayname] = useState('no user logged in')
    const [registeredCredentials, setRegisteredCredentials] = useState({})

    const [activeRegOpt, setActiveRegOpt] = useState(false)
    const [attestationRegNewCred, setAttestationRegNewCred] = useState('none')
    const [authenticatorRegNewCred, setAuthenticatorRegNewCred] = useState('platform')
    const [verificationRegNewCred, setVerificationRegNewCred] = useState('preferred')
    const [residentKeyReqRegNewCred, setResidentKeyReqRegNewCred] = useState(false)


    const increaseStep = () => {
        if(responseStatus !== true) {
            if(requestStatus !== true) {
                setErrMessage('Melde dich an, bevor du auf "Weiter" klickst!')
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

    function startAuthentication() {
        setError(false)

        let serverPublicKeyCredentialGetOptionsRequest = {
            username: inputUsername,
            userVerification: verification
        }

        setRequest(serverPublicKeyCredentialGetOptionsRequest)
        setActiveAuth(true)
        setRequestStatus(true)

        getAuthCallenge(serverPublicKeyCredentialGetOptionsRequest)
            .then(getCredentialOptions => {
                setGetCredentialOptions(getCredentialOptions)
            })
    }

    async function startGettingCredential() {
        await sleep(1000)
        getAssertion(getCredentialOptions)
    }

    function getAuthCallenge(serverPublicKeyCredentialGetOptionsRequest) {
        return restPost('http://localhost:8080/assertion/options', serverPublicKeyCredentialGetOptionsRequest)
            .then(res => {
                if(res.status !== 'ok') {
                    return Promise.reject(res.errorMessage)
                } else {
                    setResponse(JSON.parse(JSON.stringify(res)))
                    setResponseStatus(true)
                    let getCredentialOptions = performGetCredReq(res)
                    return Promise.resolve(getCredentialOptions)
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

    /**
     * Calls the .get() API and sends result to server to verify
     * @return {any} server response object
     */
    function getAssertion(options) {
        return navigator.credentials.get({publicKey: options})
            .then(rawAssertion => {
                const utf8Decoder = new TextDecoder('utf-8')
                const decodedClientData = utf8Decoder.decode(rawAssertion.response.clientDataJSON)

                let authData = rawAssertion.response.authenticatorData

                // Get RP-ID Hash
                let rpIdHash = authData.slice(0, 32)
                const rpidHash = base64UrlEncode(rpIdHash)

                // Get flags
                let flagsBuffer = authData.slice(32, 33)
                let int8ViewFlags = new Uint8Array(flagsBuffer)
                let flagBits = byte2bits(int8ViewFlags[0])
                const flagObj = getFlagObject(flagBits)

                // Get counter
                let counter = authData.slice(33, 37)
                let int8ViewCounter = new Uint8Array(counter)
                const counterInteger = int8ViewCounter[0] << 24 | (int8ViewCounter[1] & 0xff) << 16 | (int8ViewCounter[2] & 0xff) << 8 | (int8ViewCounter[3] & 0xff)

                let authenticatorData = {
                    flags: flagObj,
                    rpIdHash: rpidHash,
                    signatureCounter: counterInteger,
                }

                let response = {
                    authenticatorData: authenticatorData,
                    clientDataJSON: JSON.parse(decodedClientData),
                    signature: base64UrlEncode(rawAssertion.response.signature),
                    userHandle: base64UrlEncode(rawAssertion.response.userHandle),
                }

                let authenticatorAssertionResponse = {
                    authenticatorAttachment: rawAssertion.authenticatorAttachment,
                    id: rawAssertion.id,
                    rawId: base64UrlEncode(rawAssertion.rawId),
                    response: response,
                    type: rawAssertion.type
                }

                setAuthenticatorResponse(authenticatorAssertionResponse)

                let assertion = {
                    rawId: base64UrlEncode(rawAssertion.rawId),
                    id: base64UrlEncode(rawAssertion.rawId),
                    response: {
                        clientDataJSON: base64UrlEncode(rawAssertion.response.clientDataJSON),
                        userHandle: base64UrlEncode(rawAssertion.response.userHandle),
                        signature: base64UrlEncode(rawAssertion.response.signature),
                        authenticatorData: base64UrlEncode(rawAssertion.response.authenticatorData)
                    },
                    type: rawAssertion.type,
                };

                if (rawAssertion.getClientExtensionResults) {
                    assertion.extensions = rawAssertion.getClientExtensionResults();
                    authenticatorAssertionResponse.extensions = rawAssertion.getClientExtensionResults()
                }

                return restPost("http://localhost:8080/assertion/result", assertion);
            })
            .catch(function(error) {
                if (error === "AbortError") {
                    // User aborted the registration process
                    console.info("Authentifizierung wurde vom Benutzer abgebrochen");
                }
                return Promise.reject(error);
            })
            .then(response => {
                if (response.status !== 'ok') {
                    alert("Authentifizierung ist fehlgeschlagen!")
                    console.log("Serverstatus Authentifizierung: " + response.status)
                    return Promise.reject(response.errorMessage);
                } else {
                    alert("Authentifizierung war erfolgreich!")
                    console.log("Serverstatus Authentifizierung: " + response.status)
                    setLoggedInUsername(response.username)
                    setLoggedInDisplayname(response.displayName)
                    setRegisteredCredentials(response.allowCredentials)
                    return Promise.resolve(response);
                }
            });
    }

    const openAccountInfo = () => {
        setActiveAuthDemo(false)
    }

    const backToAuthDemo = () => {
        setActiveAuthDemo(true)
    }

    const openRegisterNewCredentialOptions = () => {
        setActiveRegOpt(true)
    }

    const startRegistrationNewCredential = () => {
        let serverPublicKeyCredentialCreationOptionsRequest = {
            username: loggedInUsername,
            displayName: loggedInDisplayname,
        }

        serverPublicKeyCredentialCreationOptionsRequest.authenticatorSelection = {
            requireResidentKey: residentKeyReqRegNewCred,
            userVerification: verificationRegNewCred,
            authenticatorAttachment: authenticatorRegNewCred
        }

        serverPublicKeyCredentialCreationOptionsRequest.attestation = attestationRegNewCred

        return restPost('http://localhost:8080/attestation/options', serverPublicKeyCredentialCreationOptionsRequest)
            .then(res => {
                if(res.status !== 'ok') {
                    // set error message
                    alert("REST POST Endpoint /attestation/options failed")
                } else {
                    let createCredentialOptions = performMakeCredReq(res)
                    navigator.credentials.create({publicKey: createCredentialOptions})
                        .then(rawAttestation => {
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
                            }

                            // set transports if it is available
                            if (typeof rawAttestation.response.getTransports === "function") {
                                attestation.response.transports = rawAttestation.response.getTransports();
                            }

                            // post the attestation object to the relying party
                            return restPost("http://localhost:8080/attestation/result", attestation)
                        })
                        .catch(function(error) {
                            if (error === "AbortError") {
                                // User aborted the registration process
                                console.info("Aborted by user");
                            }
                            return Promise.reject(error);
                        })
                        .then(response => {
                            setActiveRegOpt(false)
                            // get relying party response
                            if(response.status !== 'ok') {
                                return Promise.reject(response.errorMessage)
                            } else {
                                setRegisteredCredentials(response.allowCredentials)
                                return Promise.resolve(response)
                            }
                        })
                }
            })
    }

    return(
        <>
            <AccountContainer className={activeAuthDemo === false ? 'active' : ''}>
                <Heading className={'active-heading'}>Verwalten der Credentials</Heading>
                <CustomButton onClick={() => backToAuthDemo()}>Zurück</CustomButton>
                <h3 style={{textAlign: 'center', marginTop: '10px'}}>Username: <em>{loggedInUsername}</em></h3>
                <h3 style={{textAlign: 'center', marginTop: '10px'}}>Anzeigename: <em>{loggedInDisplayname}</em></h3>
                <h3 style={{marginTop: '20px'}}>Registrierte Credentials:</h3>

                <JSONViewer className={'active'}>
                    <ReactJson src={registeredCredentials} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                </JSONViewer>

                <CustomButton className={activeRegOpt === true ? 'notActive' : ''} onClick={() => openRegisterNewCredentialOptions()} style={{marginTop: '30px' ,width: '300px'}}>Neues Credential registieren</CustomButton>

                <RegistrationOptionsContainer className={activeRegOpt === true ? 'active' : ''}>
                    <h4>Attestation Conveyance Preference (Beglaubigung des Authentikators):</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>NONE:</b> Standardwert. Die relying party ist an KEINER Beglaubigung durch den Authentikator interessiert.</li>
                        <li><b>INDIRECT:</b> Die relying party möchte eine Beglaubigung erhalten, überlässt den Client jedoch die Entscheidung, wie diese auszusehen hat. D.h. der Client KANN die vom Authenticator generierte Beglaubigung verändern/anonymisieren, um bspw. die Privatsphäre des Benutzers zu schützen. Die relying party hat in diesem Fall jedoch eine Garantie dafür, dass diese Bescheinigung verifizierbar ist.</li>
                        <li><b>DIRECT:</b> Die relying party benötigt eine verifizierbare Bescheinigung direkt vom Authentikator.</li>
                    </ul>
                    <RadioButton type='radio' value='none' name='attestationNewCred' checked={attestationRegNewCred === 'none'} onChange={e => setAttestationRegNewCred(e.target.value)}/>None
                    <RadioButton type='radio' value='indirect' name='attestationNewCred' checked={attestationRegNewCred === 'indirect'} onChange={e => setAttestationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Indirect
                    <RadioButton type='radio' value='direct' name='attestationNewCred' checked={attestationRegNewCred === 'direct'} onChange={e => setAttestationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Direct

                    <h4 style={{marginTop: '20px'}}>Authentikator Attachment (Authentikatorplattform):</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>PLATFORM:</b> Der Authentikator ist fest im Gerät verbaut (TouchID, FaceID, ...).</li>
                        <li><b>CROSS-PLATFORM:</b> Der Authentikator ist entfernbare Hardware (YubiKey, Bluetooth, ...).</li>
                    </ul>
                    <RadioButton type='radio' value='platform' name='authenticatorNewCred' checked={authenticatorRegNewCred === 'platform'} onChange={e => setAuthenticatorRegNewCred(e.target.value)}/>Platform
                    <RadioButton type='radio' value='cross-platform' name='authenticatorNewCred' checked={authenticatorRegNewCred === 'cross-platform'} onChange={e => setAuthenticatorRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Cross-Platform

                    <h4 style={{marginTop: '20px'}}>Resident key (Authentifizierung ohne Benutzernamen):</h4>
                    <RadioButton type='checkbox' onChange={e => setResidentKeyReqRegNewCred(e.target.checked)} />Required

                    <h4 style={{marginTop: '20px'}}>User verification (Benutzerverifizierung):</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>REQUIRED:</b> Die relying party benötigt zwingend eine Benutzerüberprüfung.</li>
                        <li><b>PREFERRED:</b> Die relying party bevorzugt eine Benutzerüberprüfung. D.h. Authentifizierung schlägt nicht fehl, wenn eine Verifizierung nicht möglich ist.</li>
                        <li><b>DISCOURAGED:</b> Die relying party benötigt KEINE Benutzerüberprüfung.</li>
                    </ul>
                    <RadioButton type='radio' value='required' name='verificationNewCred' checked={verificationRegNewCred === 'required'} onChange={e => setVerificationRegNewCred(e.target.value)} />Required
                    <RadioButton type='radio' value='preferred' name='verificationNewCred' checked={verificationRegNewCred === 'preferred'} onChange={e => setVerificationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Preferred
                    <RadioButton type='radio' value='discouraged' name='verificationNewCred' checked={verificationRegNewCred === 'discouraged'} onChange={e => setVerificationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Discouraged

                    <CustomButton onClick={() => startRegistrationNewCredential()} style={{marginTop: '30px' ,width: '300px'}}>Registieren</CustomButton>
                </RegistrationOptionsContainer>
            </AccountContainer>

            <AuthenticationStepperContainer className={activeAuthDemo === true ? 'active' : ''}>
                <Heading className={step === 1 ? 'active-heading' : ''}>Benutzerauswahl</Heading>
                <Heading className={step === 2 ? 'active-heading' : ''}>Lokale Authentifizierung</Heading>
                <Heading className={step === 3 ? 'active-heading' : ''}>Authentifikator Response</Heading>

                <ProgressBarContainer>
                    <ProgressLine className={(step === 2 ? 'fifty' : step === 3 ? 'hundred' : '')}/>
                    <ProgressCircle className={'active'}><UserImage src={userSVG} alt='user' /></ProgressCircle>
                    <ProgressCircle className={step === 2 ? 'active' : step === 3 ? 'active' : ''}><FingerprintImage src={fingerprintSVG} alt='user' /></ProgressCircle>
                    <ProgressCircle className={step === 3 ? 'active' : ''}><KeyImage src={signatureSVG} alt='user' /></ProgressCircle>
                </ProgressBarContainer>
                <StepContent className={step === 1 ? 'active-step-content' :  ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>In diesem Teil der Demo wird der bei der Registrierung erstellte Berechtigungsnachweis (Credential) dazu genutzt den jeweiligen Benutzer zu authentifizieren.</li>
                            <li style={{marginTop: '10px'}}>Benötigt wird hierzu lediglich der bei der Registierung angegebene Benutzername. Wurde bei der Registierung in den <em>Weiteren Optionen</em> der Haken bei dem Punkt <em>Discoverable Credential</em> gesetzt kann eine Authentifizierung auch ohne Eingabe eines Benutzernamens erfolgen.</li>
                            <li style={{marginTop: '10px'}}>Der Benutzer hat zudem die Möglichkeit unter den <em>Weiteren Optionen</em> auszuwählen, ob eine Benutzerverifizierung vom Authentifikator durchgeführt werden soll.</li>
                        </ul>
                    </Description>

                    <InputGroup>
                        <InputLabel>Benutzername</InputLabel>
                        <CustomInput type='text' name='username' value={inputUsername} onInput={e => setInputUsername(e.target.value)}/>
                    </InputGroup>

                    <CustomButton style={{marginBottom: "30px", width: "200px"}} onClick={() => { activeOpt !== true ? setActiveOpt(true) : setActiveOpt(false)}}>Weitere Optionen</CustomButton>


                    <AuthenticationOptionsContainer className={activeOpt === true ? 'active' : ''}>
                        <h4 style={{marginTop: '20px'}}>User verification:</h4>
                        <p style={{fontSize: '14px', lineHeight: '20px', marginBottom: '10px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            Dadruch weist die relying-party den Authentikator an eine Benutzerverifizierung durchzuführen
                            und der relying-party im Anschluss das Ergebnis zukommen zu lassen. Die Verifizierung kann unter anderem durch einen PIN, ein Passwort
                            oder eines biometrischen Merkmals geschehen.

                            <p style={{marginTop: '3px'}}><em>Diese Option wird normalerweise von der Anwendung selbst festgelegt. D.h. der Benutzer hat i.d.R. keinen Einfluss darüber.</em></p>
                        </p>
                        <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li><b>REQUIRED:</b> Benutzerüberprüfung zwingend gefordert.</li>
                            <li><b>PREFERRED:</b> Benutzerüberprüfung wird bevorzugt. D.h. Authentifizierung schlägt nicht fehl, wenn nicht vorhanden.</li>
                            <li><b>DISCOURAGED:</b> Keine Benutzerüberprüfung.</li>
                        </ul>
                        <RadioButton type='radio' value='required' name='verification' checked={verification === 'required'} onChange={e => setVerification(e.target.value)} />Required
                        <RadioButton type='radio' value='preferred' name='verification' checked={verification === 'preferred'} onChange={e => setVerification(e.target.value)} style={{marginLeft: '20px'}}/>Preferred
                        <RadioButton type='radio' value='discouraged' name='verification' checked={verification === 'discouraged'} onChange={e => setVerification(e.target.value)} style={{marginLeft: '20px'}}/>Discouraged
                    </AuthenticationOptionsContainer>

                    <ButtonContainer>
                        <CustomButton type='submit' onClick={() => { startAuthentication() }}>Anmeldung</CustomButton>
                        <ErrorMessage className={error === true ? 'active' : ''}>{errMessage}</ErrorMessage>
                        <CustomButton onClick={() => {increaseStep(); startGettingCredential()}}>Weiter</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 0: Browser</h3>
                            <AnimatedArrow/>
                            <h3>Relying Party</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Der Client initiiert den folgenden Request, um eine Authentifizierung zu starten.</li>
                            <li style={{marginTop: '10px'}}>Die Authentifizierung kann entweder mit einem Usernamen oder ohne erfolgen.</li>
                            <li style={{marginTop: '10px'}}>Eine Anmeldung <b>ohne</b> Usernamen setzt jedoch vorraus, dass die Registrierung eines Discoverable Credentials erfolgt ist. D.h. die Anmeldeinformationen können auf dem Authentifikator gespeichert werden.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={request} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 1: Relying Party</h3>
                            <AnimatedArrow/>
                            <h3>Browser</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Die relying party nimmt den oben durchgeführten request entgegen und antwortet mit einem PublicKeyCredentialRequestOptions Objekt, welches in Schritt 2 an den Authentikator weitergegeben wird und dieser ein passendes Credential dazu sucht.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>status:</b> Gibt den Response Status der relying party an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>errorMessage:</b> Beinhaltet eine Beschreibung des Fehlers, falls ein Fehler auftritt. Sonst null.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>sessionId:</b> Die ID der Session, um User-Anfragen einer Sitzung zuzuordnen. Wurde von der relying-party erzeugt.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>challenge:</b> Ist ein von der relying-party zufällig generierter Wert und wird benötigt, um Wiederholungsangriffe vorzubeugen. Dieser Wert fließt unter anderem in die Authentifizierungssignatur mit ein.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>timeout:</b> Zeit in Millisekunden, die von der relying party auf eine Antwort gewartet wird bis der Aufruf geschlossen wird.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rpId:</b> Die ID der relying party. Sie muss dem vom Client gesehenen und bei der Registierung genutzten Origin oder einen Suffix diesen Origins entsprechen.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>allowCredentials:</b> Eine Liste der registierten Credentials des Benutzers. Wird eine Authentifizierung ohne Benutzernamen gewählt, ist diese Liste leer und der Authentifikator sucht sich das jeweilige passende Credential selbst anhand der vorliegenden rpID.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>type:</b> Enthält den Typ der Anmeldeinformationen. Gültige Werte sind: password, federated und public-key. In diesem Fall handelt es sich immer um ein PublicKeyCredential und somit ist der Wert immer "public-key".</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>id:</b> Eindeutiger Identifier dieses Credentials. Wurde bei der Registrierung vom Authentifikator erzeugt.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>transports:</b> Gibt den Kommunikationsweg zwischen Browser und Authentifikator an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>userVerification:</b> Gibt an, ob eine Verifikation des Benutzers durchgeführt werden soll.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={response} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>

                <StepContent className={step === 2 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Der Browser sucht nun nach verfügbaren Authentifikatoren und verbindet sich entsprechend. Sind mehrere Authentikatoren verfügbar, wird der Browser den Anwender zu einer Auswahl auffordern.</li>
                            <li style={{marginTop: '10px'}}>Anschließend sucht der Authentifikator nach einem passenden Credential. Die Suche erfolgt entwender anhand einer in der Liste <em>allowCredentials</em> enthaltenen credentialID oder anhand der rpID, sofern die Liste leer ist.</li>
                            <li style={{marginTop: '10px'}}>Ist eine Verfifikation des Benutzers erforderlich, wird dieser außerdem zur Eingabe eines PINs, eines Passwortes oder eines biometrischen Merkmales aufgefordert, bevor der Authentifikator die entsprechenden Operationen durchführt.</li>
                        </ul>
                    </Description>

                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zurück</CustomButton>
                        <CustomButton onClick={() => increaseStep()}>Weiter</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 2: Browser</h3>
                            <AnimatedArrow/>
                            <h3>Authentikator</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Damit der Authentikator das Credential erfolgreich erzeugen kann, wird zunächst das in Schritt 1 erhaltene Objekt in das richtige Format gebracht:</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}>Dazu werden die Attribute status und errorMessage entfernt, sowie die userID und die challenge mit Base64 entschlüsselt.</li>
                            <li style={{marginTop: '10px'}}>Der Browser (Client) ruft anschließend die Funktion navigator.credentials.create auf und übergibt dabei als Parameter das unten aufgeführte Objekt.</li>
                            <li style={{marginTop: '10px'}}>Der Client sucht nach verfügbaren Authentikatoren und verbindet sich ensprechend. Sind mehrere Authentifikatoren verfügbar, wird der Anwender zur Auswahl aufgefordert.</li>
                            <li style={{marginTop: '10px'}}>Daraufhin erstellt der Authentikator die Authentifizierungssignatur mit dem passenden privaten Schlüssel und gibt das im nächsten Schritt 3 zu sehende Objekt an den Browser zurück.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={getCredentialOptions} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>

                </StepContent>

                <StepContent className={step === 3 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Das unten aufgeführte Objekt enthält die in Schritt 2 vom Authentifikator erzeugten und an die relying-party übergebenen Daten. Das Objekt enthält unter anderem die erzeugte Authentifizierungssignatur und weitere Attribute, um den Authentifizierungsprozess zu validieren.</li>
                            <li style={{marginTop: '10px'}}>Der Serverstatus über den Erfolg des Authentifizierungsprozesses kann nachträglich nochmals in der Konsole des Browsers geprüft werden.</li>
                            <li style={{marginTop: '10px'}}>Ein weiterer Authentifikator kann über den Button <em>Zum Account</em> registriert werden. Außerdem besteht unter diesem Punkt die Möglichkeit alle bereits registrierten Credentials einzusehen.</li>
                        </ul>
                    </Description>

                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zurück</CustomButton>
                        <CustomButton onClick={() => openAccountInfo()}>Zum Account</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 3: Authentikator</h3>
                            <AnimatedArrow/>
                            <h3>Browser</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>authenticatorAttachment:</b> Beschreibt den Authentikatortyp (platform = im Clientgerät integriert, cross-platform = externe Komponente). </li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>id:</b> Gibt die ID des zur Erzeugung der Authentifizierungssignatur verwendeten Credentials an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rawId:</b> Ist identisch zur "id", nur in binärer Form. Das Attribut wurde zur besseren Lesbarkeit ebenfalls Base64 verschlüsselt.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>response:</b> Beinhaltet alle vom Authentifikator erzeugten und an den Browser zurückgegebenen Daten.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>authenticatorData:</b> Beinhaltet die Rückgabewerte der durchgeführten Operationen zur Erzeugung der Authentifizierungssignatur.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>flags:</b> Ein Bitfeld, das verschiedene Attribute angibt, die vom Authentikator bestätigt wurden..</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>ED:</b> Zeigt an, ob die zurück gegebenen Daten Erweiterungen (extensions) enthalten.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>AT:</b> Zeigt an, ob der Authentifikator Daten über eine Beglaubigung hinzugefügt hat.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>UV:</b> Zeigt an, ob der Benutzer vom Authentikator verifiziert wurde. Die Verifizierung erfolgt mittels einer PIN, einem Passwort, eines biometrischen Merkmals oder ähnlichem.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>UP:</b> Zeigt durch eine Prüfung der Nutzerpräsenz an, ob der Benutzer anwesend ist. Diese Prüfung kann durch das Berühren einer auf dem Authentifikator befindlichen Schaltfläche geschehen oder durch eine erfolgreich durchgeführte lokale Verifizierung des Anwenders.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>rpIdHash:</b> Ein SHA256-Hash der relying party ID. Die relying party stellt damit sicher, dass dieser Hash mit dem Hash der eigenen ID übereinstimmt, um Phishing oder Man-in-the-Middle-Angriffe zu verhindern.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>signatureCounter:</b> Ein Zähler, der bei jeder erfolgreich durchgeführten Authentifizierung inkrementiert wird, damit die relying party geklonte Authentikatoren erkennen kann.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>clientDataJSON:</b> Eine Sammlung von Daten, die vom Browser an den Authentikator übergeben werden.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>type:</b> Gibt die durchgeführte webauthn-Operation an. Beinhaltet entweder den Wert "webauthn.get", wenn ein vorhandenes Credential abgerufen wird oder "webauthn.create", wenn ein neuer Berechtigungsnachweis erstellt wird. Das Attribut beinhaltet somit bei der Authentifizierung immer den Wert "webauthn.get".</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>challenge:</b> Die kryptografische Challenge, welche beim initialen Austausch von der relying party gesendet wurde.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>origin:</b> Gibt an, ob die Möglichkeit von cross-origin-requests bestehen soll. D.h. die relying party akzeptiert Anfragen auch von einer anderen Origin, die nicht ihres eigenen Origins entspricht.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>crossOrigin:</b> Gibt an, ob die Möglichkeit von cross-origin-requests bestehen soll. D.h. die relying party akzeptiert Anfragen auch von einer anderen Domain.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>signature:</b> Die erzeugte Authentifizierungssignatur, welche mit dem passenden privaten Schlüssel vom Authentifikator über die Bytes des Objektes <em>authenticatorData</em> und einem SHA-256-Hash des <em>clientDataJSON</em> Objektes generiert wurde. Die relying party prüft die Gültigkeit dieser Signatur im Gegenzug mit dem passenden öffentlichen Schlüssel.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>userHandle:</b> Entspricht der bei der Registrierung von der relying party erzeugten UserID aus dem angegebenen Benutzernamen.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>type:</b> Enthält den Typ der Anmeldeinformationen. Gültige Werte sind: password, federated und public-key. In diesem Fall handelt es sich immer um ein PublicKeyCredential und somit ist der Wert immer "public-key".</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>extensions:</b> Gibt mögliche Client-Weauthn-Erweiterungen an, um bspw. nicht nur festzustellen, ob der Benutzer verifiziert wurde, sondern auch wie. Diese Demo bietet jedoch keine Möglichkeit Erweiterungen zu wählen. Das Objekt ist somit immer leer.</li>
                            <li style={{marginTop: '10px'}}>Das gesamte Objekt wird im Anschluss Base64 verschlüsselt und zur relying party gesendet.</li>
                            <li style={{marginTop: '10px'}}>Die relying party sucht nach dem Erhalt dieses Objektes den passenden öffentlichen Schlüssel, um damit die Signatur zu verifizieren.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={authenticatorResponse} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>

            </AuthenticationStepperContainer>
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

const AuthenticationStepperContainer = styled.div`
  display: none;

  &.active {
    display: block;
  }
`

const AccountContainer = styled.div`
  display: none;

  &.active {
    display: block;
  }
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
  
  &.notActive {
    display: none;
  }
`

const AuthenticationOptionsContainer = styled.div`
  display: none;
  margin-bottom: 40px;

  &.active {
    display: block;
  };
`

const RegistrationOptionsContainer = styled.div`
  display: none;
  margin-bottom: 40px;

  &.active {
    display: block;
  };
`

export default Authentication
