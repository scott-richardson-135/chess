package service;

import dataaccess.*;
public class ClearService {
    public void clear() {
        new MemoryUserDao().clear();
        new MemoryAuthDao().clear();
        new MemoryGameDao().clear();
    }
}
