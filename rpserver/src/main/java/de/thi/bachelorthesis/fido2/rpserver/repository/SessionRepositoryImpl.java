package de.thi.bachelorthesis.fido2.rpserver.repository;

import de.thi.bachelorthesis.fido2.rpserver.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SessionRepositoryImpl implements SessionRepository {
    private static final String KEY = "FIDO2::Session";
    private Map<String, Session> sessionStore;
    private final long sessionTtlMillis = 180000L;

    @Autowired SessionRepositoryImpl() {
        this.sessionStore = new HashMap<>();
    }

    @Override
    public Session getSession(String id) {
        return sessionStore.get(makeKey(id));
    }

    private static String makeKey(String id) {
        return KEY + ":" + id;
    }

    @Override
    public void save(Session session) {
        sessionStore.put(makeKey(session.getId()), session);
    }

    /*@Override
    public void update(Session session) {
        Long timeToLive = redisTemplate.getExpire(makeKey(session.getId()));
        if (timeToLive == null) {
            timeToLive = sessionTtlMillis;
        }
        valueOperations.set(makeKey(session.getId()), session, timeToLive, TimeUnit.MILLISECONDS);
    }*/
}
