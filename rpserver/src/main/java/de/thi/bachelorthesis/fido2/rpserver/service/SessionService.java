package de.thi.bachelorthesis.fido2.rpserver.service;

import de.thi.bachelorthesis.fido2.rpserver.model.Session;

public interface SessionService {
    Session createSessionData();
    void createSession(Session session);
    Session getSession(String sessionId);
}
