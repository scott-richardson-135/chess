package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDao {
    private static final HashMap<String, AuthData> AUTHS = new HashMap<>();
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (AUTHS.containsKey(auth.authToken())) {
            throw new DataAccessException("Auth token already exists");
        }

        AUTHS.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return AUTHS.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        AUTHS.remove(authToken);
    }

    public void clear() {
        AUTHS.clear();
    }
}
