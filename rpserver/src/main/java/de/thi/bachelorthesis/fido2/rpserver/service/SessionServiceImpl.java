package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.model.Session;
import de.thi.bachelorthesis.fido2.rpserver.repository.SessionRepository;
import de.thi.bachelorthesis.fido2.rpserver.util.HmacUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Session createSessionData() {
        Session session = new Session();
        String sessionId = UUID.randomUUID().toString();
        SecretKey hmacKey;
        try {
            hmacKey = HmacUtil.generateHmacKey();
        } catch (NoSuchAlgorithmException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.CRYPTO_OPERATION_EXCEPTION,
                    "Exception during generating hmac key", e);
        }
        String hmacKeyString = Base64.getUrlEncoder().withoutPadding().encodeToString(hmacKey.getEncoded());
        session.setId(sessionId);
        session.setHmacKey(hmacKeyString);
        return session;
    }

    @Override
    public void createSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public Session getSession(String sessionId) {
        return sessionRepository.getSession(sessionId);
    }
}
