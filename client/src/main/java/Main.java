import chess.ChessGame;
import chess.ChessPiece;
import ui.ResponseException;
import ui.SignedOutRepl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        SignedOutRepl sorepl = new SignedOutRepl("http://localhost:8080");
        try {
            sorepl.run();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
}