package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };

        MoveUtils.addSliding(board, position, piece, directions, validMoves);

        return validMoves;

    }
}
