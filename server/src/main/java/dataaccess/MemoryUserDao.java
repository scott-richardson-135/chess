package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao {
    HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData u) {
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

}
