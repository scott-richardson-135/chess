package chess.moves;

import chess.*;

import java.util.Collection;

public interface PieceMoveCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    default boolean isInBounds(int row, int col) {
        return (row < 1 || row > 8 || col < 1 || col > 8);
    }
}
