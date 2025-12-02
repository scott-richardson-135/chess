package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameId, Session session) {

        Set<Session> sessionSet = connections.computeIfAbsent(
                gameId,
                id -> ConcurrentHashMap.newKeySet()
        );

        sessionSet.add(session);
    }

    public void removeSessionFromGame(int gameId, Session session) {
        Set<Session> sessionSet = connections.get(gameId);

        if (sessionSet != null) {
            sessionSet.remove(session);
        }

    }

    public Set<Session> getSessionsForGame(int gameId) {
        return connections.get(gameId);
    }

    public void broadcast(int gameId, Object message, Session except) throws IOException {
        Set<Session> sessions = getSessionsForGame(gameId);
        if (sessions == null) {
            return;
        }

        String json = new Gson().toJson(message);

        for (Session session : sessions) {
            if (session.isOpen()) {
                if (!session.equals(except)) {
                    session.getRemote().sendString(json);
                }
            }
        }
    }
}
