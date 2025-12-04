package websocket;

import model.GameData;

public interface GameHandler {
    public void updateGame(GameData game);
    public void printMessage(String message);
}
