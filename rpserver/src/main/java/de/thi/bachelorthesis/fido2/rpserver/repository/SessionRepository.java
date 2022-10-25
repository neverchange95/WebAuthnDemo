package de.thi.bachelorthesis.fido2.rpserver.repository;

import de.thi.bachelorthesis.fido2.rpserver.model.Session;

public interface SessionRepository {
    Session getSession(String id);
    void save(Session session);
    //void update(Session session);
}
