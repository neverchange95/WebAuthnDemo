package de.thi.bachelorthesis.fido2.rpserver.controller;

import de.thi.bachelorthesis.fido2.rpserver.entity.RpEntity;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerRegPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.model.AdapterAuthServerPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.model.AdapterRegServerPublicKeyCredential;
import de.thi.bachelorthesis.fido2.rpserver.model.Session;
import de.thi.bachelorthesis.fido2.rpserver.model.Status;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.*;
import de.thi.bachelorthesis.fido2.rpserver.repository.RpRepository;
import de.thi.bachelorthesis.fido2.rpserver.service.ChallengeService;
import de.thi.bachelorthesis.fido2.rpserver.service.ResponseService;
import de.thi.bachelorthesis.fido2.rpserver.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AdapterController {
    private final ChallengeService challengeService;
    private final ResponseService responseService;
    private final SessionService sessionService;
    private final RpRepository rpRepository;
    private final String COOKIE_NAME = "fido2-session-id";

    @Autowired
    public AdapterController(ChallengeService challengeService, SessionService sessionService, ResponseService responseService, RpRepository rpRepository) {
        this.challengeService = challengeService;
        this.sessionService = sessionService;
        this.responseService = responseService;
        this.rpRepository = rpRepository;

        RpEntity rp = new RpEntity();
        rp.setId("localhost");
        rp.setDescription("example");
        rp.setName("Test RP");
        rpRepository.save(rp);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/attestation/options")
    public ServerPublicKeyCredentialCreationOptionsResponse getRegistrationChallenge(@RequestHeader String host, @RequestBody ServerPublicKeyCredentialCreationOptionsRequest optionsRequest, HttpServletResponse httpServletResponse) {
        ServerPublicKeyCredentialCreationOptionsResponse response = challengeService.getRegChallenge(optionsRequest);
        response.setStatus(Status.OK);

        Session session = sessionService.createSessionData();
        session.setRegOptionResponse(response);

        response.setSessionId(session.getId());
        sessionService.createSession(session);

        httpServletResponse.addCookie(new Cookie(COOKIE_NAME, session.getId()));
        httpServletResponse.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        return response;
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/attestation/result")
    public AdapterServerResponse sendRegistrationResponse(@RequestHeader String host, @RequestBody AdapterRegServerPublicKeyCredential clientResponse, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AdapterServerResponse serverResponse;

        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies == null || cookies.length == 0) {
            serverResponse = new AdapterServerResponse();
            serverResponse.setStatus(Status.FAILED);
            serverResponse.setErrorMessage("Cookie not found");
            return serverResponse;
        }

        String sessionId = null;
        for(Cookie cookie : cookies) {
            if(COOKIE_NAME.equals(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }

        String scheme = httpServletRequest.getScheme();

        StringBuilder builder = new StringBuilder()
                // http://localhost:8080
                .append(scheme)
                .append("://")
                .append("localhost")
                .append(":")
                .append("8080");

        ServerRegPublicKeyCredential serverRegPublicKeyCredential = new ServerRegPublicKeyCredential();
        serverRegPublicKeyCredential.setId(clientResponse.getId());
        serverRegPublicKeyCredential.setType(clientResponse.getType());
        serverRegPublicKeyCredential.setResponse(clientResponse.getResponse());

        RegistrationResponse response = responseService.handleAttestation(serverRegPublicKeyCredential, sessionId, builder.toString(), "localhost", null);

        httpServletResponse.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        response.setSessionId(sessionId);

        return response;
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/assertion/options")
    public ServerPublicKeyCredentialGetOptionsResponse getAuthenticationChallenge(@RequestHeader String host, @RequestBody ServerPublicKeyCredentialGetOptionsRequest optionRequest, HttpServletResponse httpServletResponse) {
        ServerPublicKeyCredentialGetOptionsResponse serverResponse = challengeService.getAuthChallenge(optionRequest);

        // create session
        Session session = sessionService.createSessionData();
        session.setAuthOptionResponse(serverResponse);
        sessionService.createSession(session);

        serverResponse.setStatus(Status.OK);
        serverResponse.setSessionId(session.getId());

        httpServletResponse.addCookie(new Cookie(COOKIE_NAME, serverResponse.getSessionId()));
        httpServletResponse.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        return serverResponse;
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/assertion/result")
    public AuthenticationResponse sendAuthenticationResponse(@RequestHeader String host, @RequestBody AdapterAuthServerPublicKeyCredential clientResponse, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        AuthenticationResponse serverResponse;

        // get session id
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            //error
            serverResponse = new AuthenticationResponse();
            serverResponse.setStatus(Status.FAILED);
            serverResponse.setErrorMessage("Cookie not found");
            return serverResponse;
        }

        String sessionId = null;
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }

        String scheme = httpServletRequest.getScheme();

        StringBuilder builder = new StringBuilder()
                // http://localhost:8080
                .append(scheme)
                .append("://")
                .append("localhost")
                .append(":")
                .append("8080");

        ServerAuthPublicKeyCredential serverAuthPublicKeyCredential = new ServerAuthPublicKeyCredential();
        serverAuthPublicKeyCredential.setResponse(clientResponse.getResponse());
        serverAuthPublicKeyCredential.setId(clientResponse.getId());
        serverAuthPublicKeyCredential.setType(clientResponse.getType());
        serverAuthPublicKeyCredential.setExtensions(clientResponse.getExtensions());


        AuthenticationResponse response = responseService.handleAssertion(serverAuthPublicKeyCredential, sessionId, builder.toString(), "localhost", null);

        httpServletResponse.setHeader("Access-Control-Allow-Origin","http://localhost:3000");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        response.setSessionId(sessionId);

        return response;
    }
}
