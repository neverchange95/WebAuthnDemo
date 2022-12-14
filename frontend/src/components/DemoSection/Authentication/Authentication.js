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
                <CustomButton onClick={() => backToAuthDemo()}>Zur??ck</CustomButton>
                <h3 style={{textAlign: 'center', marginTop: '10px'}}>Benutzername: <em>{loggedInUsername}</em></h3>
                <h3 style={{textAlign: 'center', marginTop: '10px'}}>Anzeigename: <em>{loggedInDisplayname}</em></h3>
                <h3 style={{marginTop: '20px'}}>Registrierte Credentials:</h3>

                <JSONViewer className={'active'}>
                    <ReactJson src={registeredCredentials} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                </JSONViewer>

                <CustomButton className={activeRegOpt === true ? 'notActive' : ''} onClick={() => openRegisterNewCredentialOptions()} style={{marginTop: '30px' ,width: '300px'}}>Neues Credential registieren</CustomButton>

                <RegistrationOptionsContainer className={activeRegOpt === true ? 'active' : ''}>
                    <h4>Authentifikatorbeglaubigung:</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>NONE:</b> Standardwert. Die relying party ist an KEINER Beglaubigung vom Authentifikator interessiert.</li>
                        <li><b>INDIRECT:</b> Die relying party m??chte eine Beglaubigung erhalten, ??berl??sst den
                            Client jedoch die Entscheidung, wie diese auszusehen hat. D.h. der Client KANN die
                            vom Authentifikator generierte Beglaubigung ver??ndern/anonymisieren, um bspw. die
                            Privatsph??re des Benutzers zu sch??tzen. Die relying party hat in diesem Fall jedoch
                            keine Garantie daf??r, dass diese Bescheinigung verifizierbar ist.
                        </li>
                        <li><b>DIRECT:</b> Die relying party ben??tigt eine verifizierbare unver??nderte Bescheinigung direkt vom Authentifikator.</li>
                    </ul>
                    <RadioButton type='radio' value='none' name='attestationNewCred' checked={attestationRegNewCred === 'none'} onChange={e => setAttestationRegNewCred(e.target.value)}/>None
                    <RadioButton type='radio' value='indirect' name='attestationNewCred' checked={attestationRegNewCred === 'indirect'} onChange={e => setAttestationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Indirect
                    <RadioButton type='radio' value='direct' name='attestationNewCred' checked={attestationRegNewCred === 'direct'} onChange={e => setAttestationRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Direct

                    <h4 style={{marginTop: '20px'}}>Authentifikatorplattform:</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>PLATFORM:</b> Der Authentifikator ist im Ger??t integriert (TouchID, FaceID, Windows Hello, etc.).</li>
                        <li><b>CROSS-PLATFORM:</b> Der Authentifikator ist eine entfernbare Hardwarekomponente (USB, Bluetooth, NFC, etc.).</li>
                    </ul>
                    <RadioButton type='radio' value='platform' name='authenticatorNewCred' checked={authenticatorRegNewCred === 'platform'} onChange={e => setAuthenticatorRegNewCred(e.target.value)}/>Platform
                    <RadioButton type='radio' value='cross-platform' name='authenticatorNewCred' checked={authenticatorRegNewCred === 'cross-platform'} onChange={e => setAuthenticatorRegNewCred(e.target.value)} style={{marginLeft: '20px'}}/>Cross-Platform

                    <h4 style={{marginTop: '20px'}}>Discoverable Credential (Authentifizierung ohne Benutzernamen):</h4>
                    <RadioButton type='checkbox' onChange={e => setResidentKeyReqRegNewCred(e.target.checked)} />Required

                    <h4 style={{marginTop: '20px'}}>Benutzerverifizierung:</h4>
                    <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                        <li><b>REQUIRED:</b> Die relying party ben??tigt zwingend das Ergebnis einer Benutzerverifizierung.</li>
                        <li><b>PREFERRED:</b> Die relying party bevorzugt das Ergebnis einer Benutzerverifizierung. Der Registrierungsprozess wird in diesem Fall jedoch auch erfolgreich durchgef??hrt, wenn die relying party kein Ergebnis ??ber eine erfolgreich durchgef??hrte Verifizierung erh??lt.</li>
                        <li><b>DISCOURAGED:</b> Die relying party ben??tigt KEIN Ergebnis einer Benutzerverifizierung.</li>
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
                            <li>In diesem Teil der Demo wird der erzeugte Berechtigungsnachweis (Credential) dazu verwendet den jeweiligen Benutzer zu authentifizieren.</li>
                            <li style={{marginTop: '10px'}}>Ben??tigt wird hierzu lediglich der bei der Registrierung angegebene Benutzername. Wurde bei der Registrierung in den <em>Weiteren Optionen </em>
                                der Haken bei dem Punkt <em>Discoverable Credential</em> gesetzt und die Authentifizierungsdaten auf dem Authentifikator gespeichert, kann eine Authentifizierung auch ohne die Eingabe eines Benutzernamens erfolgen.</li>
                            <li style={{marginTop: '10px'}}>Der Benutzer hat zudem die M??glichkeit unter den <em>Weiteren Optionen</em> auszuw??hlen, ob eine Benutzerverifizierung vom Authentifikator durchgef??hrt werden soll.</li>
                        </ul>
                    </Description>

                    <InputGroup>
                        <InputLabel>Benutzername</InputLabel>
                        <CustomInput type='text' name='username' value={inputUsername} onInput={e => setInputUsername(e.target.value)}/>
                    </InputGroup>

                    <CustomButton style={{marginBottom: "30px", width: "200px"}} onClick={() => { activeOpt !== true ? setActiveOpt(true) : setActiveOpt(false)}}>Weitere Optionen</CustomButton>


                    <AuthenticationOptionsContainer className={activeOpt === true ? 'active' : ''}>
                        <h4 style={{marginTop: '20px'}}>Benutzerverifizierung:</h4>
                        <p style={{fontSize: '14px', lineHeight: '20px', marginBottom: '10px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            Hiermit weist die relying party den Authentifikator an eine Benutzerverifizierung durchzuf??hren
                            und der relying party im Anschluss das Ergebnis zukommen zu lassen. Die Verifizierung kann unter anderem durch einen PIN, ein Passwort
                            oder eines biometrischen Merkmals erfolgen.
                        </p>
                        <ul style={{fontSize: '13px', marginLeft: '12px', marginBottom: '10px', marginTop: '10px', lineHeight: '20px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li><b>REQUIRED:</b> Die relying party ben??tigt zwingend das Ergebnis einer Benutzerverifizierung.</li>
                            <li><b>PREFERRED:</b> Die relying party bevorzugt das Ergebnis einer Benutzerverifizierung. Der Registrierungsprozess wird in diesem Fall jedoch auch erfolgreich durchgef??hrt, wenn die relying party kein Ergebnis ??ber eine erfolgreich durchgef??hrte Verifizierung erh??lt.</li>
                            <li><b>DISCOURAGED:</b> Die relying party ben??tigt KEIN Ergebnis einer Benutzerverifizierung.</li>
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
                            <li style={{marginTop: '10px'}}>Eine Authentifizierung <b>ohne</b> Usernamen setzt jedoch voraus, dass die Registrierung eines Discoverable Credentials erfolgt ist und somit die Anmeldeinformationen auf dem Authentifikator gespeichert wurden.</li>
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
                            <li>Die relying party nimmt den oben durchgef??hrten Request entgegen und antwortet mit einem PublicKeyCredentialRequestOptions Objekt. Dieses wird in Schritt 2 an den Authentifikator weitergegeben.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>status:</b> Gibt den Response Status der relying party an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>errorMessage:</b> Beinhaltet eine Beschreibung des Fehlers, falls ein Fehler auftritt. Sonst null.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>sessionId:</b> Die ID der Session, um User-Anfragen einer Sitzung zuzuordnen. Dieser String wurde von der relying party erzeugt.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>challenge:</b> Ist ein von der relying party zuf??llig generierter Wert und dient unter anderem der Vorbeugung von Wiederholungsangriffen. Dieser Wert flie??t au??erdem in die Authentifizierungssignatur mit ein.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>timeout:</b> Zeit in Millisekunden, die von der relying party auf eine Antwort gewartet wird.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rpId:</b> Wert zur eindeutigen Identifikation der relying party. Dieser Wert muss ein Suffix des Origin sein.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>allowCredentials:</b> Eine Liste der registierten Credentials des Benutzers. Wird eine Authentifizierung ohne Benutzernamen gew??hlt, ist diese Liste leer und der Authentifikator sucht das passende Credential in seinem interen Speicher anhand der vorliegenden rpID.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>type:</b> Enth??lt den Typ der Anmeldeinformationen. G??ltige Werte sind: password, federated und public-key. In diesem Fall handelt es sich immer um ein PublicKeyCredential und somit ist der Wert immer "public-key".</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>id:</b> Der zum Schl??sselpaar generierte Identifier.</li>
                                <li style={{marginLeft: '45px', marginTop: '4px'}}><b>transports:</b> Gibt den Kommunikationsweg zwischen Browser und Authentifikator an.</li>
                            <li style={{marginLeft: '45px', marginTop: '4px'}}><b>userVerification:</b> Beschreibt, ob der Authentifikator neben dem Test auf Pr??senz, eine lokale Pr??fung der Authentizit??t des Anwenders durchf??hren soll.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={response} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>
                </StepContent>

                <StepContent className={step === 2 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Der Browser sucht nun nach verf??gbaren Authentifikatoren und verbindet sich entsprechend. Sind mehrere Authentifikatoren verf??gbar, wird der Browser den Anwender zu einer Auswahl auffordern.</li>
                            <li style={{marginTop: '10px'}}>Anschlie??end sucht der Authentifikator entwender nach einem passenden Credential innerhalb seines internen Speichers anhand der rpID oder entschl??sselt eine passende credentialID zu einem privaten Schl??ssel, sofern der Authentifikator ??ber keinen internen Speicher verf??gt.</li>
                            <li style={{marginTop: '10px'}}>Ist eine Verifikation des Benutzers erforderlich, wird dieser au??erdem zur Eingabe eines PINs, eines Passwortes, eines biometrischen Merkmals oder ??hnlichem aufgefordert, bevor der Authentifikator die entsprechenden Operationen durchf??hrt.</li>
                        </ul>
                    </Description>

                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zur??ck</CustomButton>
                        <CustomButton onClick={() => increaseStep()}>Weiter</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 2: Browser</h3>
                            <AnimatedArrow/>
                            <h3>Authentifikator</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Zun??chst wird das in Schritt 1 erhaltene Objekt in das richtige Format gebracht:</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}>Dazu werden die Attribute status und errorMessage entfernt, sowie die userID und die challenge mit Base64 entschl??sselt.</li>
                            <li style={{marginTop: '10px'}}>Der Browser (Client) ruft anschlie??end die Methode navigator.credentials.get() auf und ??bergibt dabei als Parameter das unten aufgef??hrte Objekt.</li>
                            <li style={{marginTop: '10px'}}>Der Client sucht nach verf??gbaren Authentifikatoren und verbindet sich ensprechend. Sind mehrere Authentifikatoren verf??gbar, wird der Anwender zur Auswahl aufgefordert.</li>
                            <li style={{marginTop: '10px'}}>Daraufhin erstellt der Authentifikator die Authentifizierungssignatur mit dem passenden privaten Schl??ssel und gibt das im n??chsten Schritt 3 zu sehende Objekt an den Browser zur??ck.</li>
                        </ul>
                    </ResponseDescription>

                    <JSONViewer className={activeAuth === true ? 'active' : ''}>
                        <ReactJson src={getCredentialOptions} theme={"shapeshifter"} onEdit={false} enableClipboard={false}/>
                    </JSONViewer>

                </StepContent>

                <StepContent className={step === 3 ? 'active-step-content' : ''}>
                    <Description>
                        <ul style={{marginLeft: '30px', marginBottom: '30px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li>Das unten aufgef??hrte Objekt enth??lt die in Schritt 2 vom Authentifikator erzeugten und an die relying party ??bergebenen Daten. Das Objekt enth??lt unter anderem die erzeugte Authentifizierungssignatur und weitere Attribute, um den Authentifizierungsprozess zu validieren.</li>
                            <li style={{marginTop: '10px'}}>Der Serverstatus ??ber den Erfolg des Authentifizierungsprozesses kann nachtr??glich nochmals in der Konsole des Browsers gepr??ft werden.</li>
                            <li style={{marginTop: '10px'}}>Ein weiterer Authentifikator kann ??ber den Button <em>Zum Account </em> registriert werden. Au??erdem besteht unter diesem Punkt die M??glichkeit alle bereits registrierten Credentials einzusehen.</li>
                        </ul>
                    </Description>

                    <ButtonContainer>
                        <CustomButton onClick={() => decreaseStep()}>Zur??ck</CustomButton>
                        <CustomButton onClick={() => openAccountInfo()}>Zum Account</CustomButton>
                    </ButtonContainer>

                    <ResponseDescription className={activeAuth === true ? 'active' : ''}>
                        <HeadingArrowDescription>
                            <h3>Schritt 3: Authentifikator</h3>
                            <AnimatedArrow/>
                            <h3>Browser</h3>
                        </HeadingArrowDescription>
                        <ul style={{fontSize: '15px', marginTop: '8px', marginLeft:'15px', wordBreak: 'normal', whiteSpace: 'normal'}}>
                            <li style={{marginLeft: '25px', marginTop: '4px'}}><b>authenticatorAttachment:</b> Beschreibt den Authentifikatortyp (platform = im Clientger??t integriert, cross-platform = externe Komponente).</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>id:</b> Gibt die ID des zur Erzeugung der Authentifizierungssignatur verwendeten Credentials an.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>rawId:</b> Dieser Wert ist identisch zu "id", nur in bin??rer Form. Das Attribut wurde zur besseren Lesbarkeit ebenfalls Base64 verschl??sselt.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>response:</b> Beinhaltet alle vom Authentifikator erzeugten und an den Browser zur??ckgegebenen Daten.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>authenticatorData:</b> Beinhaltet die R??ckgabewerte der durchgef??hrten Operationen zur Erzeugung der Authentifizierungssignatur.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>flags:</b> Ein Bitfeld, das verschiedene Attribute angibt, die vom Authentifikator best??tigt wurden.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>ED:</b> Zeigt an, ob die zur??ck gegebenen Daten Erweiterungen (extensions) enthalten.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>AT:</b> Zeigt an, ob der Authentifikator Daten einer Beglaubigung beigef??gt hat.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>UV:</b> Zeigt an, ob der Benutzer vom Authentifikator verifiziert wurde. Die Verifizierung erfolgt mittels einer PIN, einem Passwort, eines biometrischen Merkmals oder ??hnlichem.</li>
                                        <li style={{marginLeft: '85px', marginTop: '4px'}}><b>UP:</b> Zeigt durch eine Pr??fung der Nutzerpr??senz an, ob der Benutzer anwesend ist. Diese Pr??fung kann entweder durch das Ber??hren einer auf dem Authentifikator befindlichen Schaltfl??che erfolgen oder durch eine erfolgreich durchgef??hrte lokale Verifizierung des Anwenders.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>rpIdHash:</b> Ein SHA256-Hash der relying party ID. Die relying party stellt damit sicher, dass dieser Hash mit dem Hash der eigenen ID ??bereinstimmt, um Phishing oder Man-in-the-Middle-Angriffe zu verhindern.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>signatureCounter:</b> Ein Z??hler, der bei jeder erfolgreich durchgef??hrten Authentifizierung inkrementiert wird und zur Erkennung geklonter Authentifikatoren dient.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>clientDataJSON:</b> Eine Sammlung der Daten, die vom Browser an den Authentifikator ??bergeben werden.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>type:</b> Gibt die durchgef??hrte WebAuthn-Operation an. Beinhaltet entweder den Wert "webauthn.get", wenn ein vorhandenes Credential abgerufen wird oder "webauthn.create", wenn ein neuer Berechtigungsnachweis erstellt wird. Das Attribut beinhaltet somit bei der Authentifizierung immer den Wert "webauthn.get".</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>challenge:</b> Die kryptographische Challenge, welche beim initialen Austausch von der relying party gesendet wurde.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>origin:</b> Origin, auf dem die webauthn.get-Operation durchgef??hrt wurde. Die rpID muss ein Suffix dieses Wertes sein.</li>
                                    <li style={{marginLeft: '65px', marginTop: '4px'}}><b>crossOrigin:</b> Gibt an, ob die M??glichkeit von cross-origin-requests bestehen soll. D.h. die relying party akzeptiert Anfragen auch von einer anderen Origin, die nicht ihres eigenen Origins entspricht.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>signature:</b> Die erzeugte Authentifizierungssignatur, welche mit dem passenden privaten Schl??ssel vom Authentifikator ??ber die Bytes des Objektes <em>authenticatorData</em> und einem SHA-256-Hash des <em>clientDataJSON</em> Objektes generiert wurde. Die relying party pr??ft die G??ltigkeit dieser Signatur im Gegenzug mit dem passenden ??ffentlichen Schl??ssel.</li>
                                <li style={{marginLeft: '45px', marginTop: '6px'}}><b>userHandle:</b> Entspricht der bei der Registrierung von der relying party erzeugten UserID aus dem angegebenen Benutzernamen.</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>type:</b> Enth??lt den Typ der Anmeldeinformationen. G??ltige Werte sind: password, federated und public-key. In diesem Fall handelt es sich immer um ein PublicKeyCredential und somit ist der Wert immer "public-key".</li>
                            <li style={{marginLeft: '25px', marginTop: '10px'}}><b>extensions:</b> Gibt m??gliche Client-WebAuthn-Erweiterungen an, um bspw. nicht nur festzustellen, ob der Benutzer verifiziert wurde, sondern auch wie die Verifikation erfolgt ist. Diese Demo bietet jedoch keine M??glichkeit Erweiterungen zu w??hlen. Das Objekt ist somit immer leer.</li>
                            <li style={{marginTop: '10px'}}>Das gesamte Objekt wird im Anschluss Base64 verschl??sselt und zur relying party gesendet.</li>
                            <li style={{marginTop: '10px'}}>Die relying party sucht nach dem Erhalt dieses Objektes nach dem passenden ??ffentlichen Schl??ssel, um damit die Signatur zu verifizieren.</li>
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
