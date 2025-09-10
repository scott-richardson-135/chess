package chess.moves;

import chess.*;

import java.util.Collection;

public interface PieceMoveCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);
}
