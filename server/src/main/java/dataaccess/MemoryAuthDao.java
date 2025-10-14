package dataaccess;

import model.AuthData;
import service.exceptions.AlreadyTakenException;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDao {
    private final HashMap<String, AuthData> auths = new HashMap<>();
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (auths.containsKey(auth.authToken())) {
            throw new DataAccessException("Auth token already exists");
        }

        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
