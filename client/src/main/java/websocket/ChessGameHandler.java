package websocket;

import model.GameData;
import ui.GameplayRepl;

public class ChessGameHandler implements GameHandler {
    GameplayRepl repl;

    public ChessGameHandler(GameplayRepl repl) {
        this.repl = repl;
    }

    @Override
    public void printMessage(String message) {
        repl.printMessage(message);
    }

    @Override
    public void updateGame(GameData game) {
        repl.updateGame(game);
    }
}
