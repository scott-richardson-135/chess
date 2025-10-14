package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao {
    private static final HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData u) {
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }

}
