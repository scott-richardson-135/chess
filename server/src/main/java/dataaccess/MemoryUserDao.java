package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao {
    private static final HashMap<String, UserData> USERS = new HashMap<>();
    @Override
    public void createUser(UserData u) {
        USERS.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) {
        return USERS.get(username);
    }

    public void clear() {
        USERS.clear();
    }

}
